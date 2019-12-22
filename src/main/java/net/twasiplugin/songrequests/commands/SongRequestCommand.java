package net.twasiplugin.songrequests.commands;

import com.wrapper.spotify.exceptions.detailed.UnauthorizedException;
import net.twasi.core.database.models.User;
import net.twasi.core.database.models.permissions.PermissionGroups;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.customcommands.TwasiCustomCommandEvent;
import net.twasi.core.plugin.api.customcommands.TwasiPluginCommand;
import net.twasi.core.services.providers.DataService;
import net.twasi.core.translations.renderer.TranslationRenderer;
import net.twasiplugin.songrequests.SongRequestProvider;
import net.twasiplugin.songrequests.SongrequestPlugin;
import net.twasiplugin.songrequests.api.ws.songrequest.models.RequesterDTO;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;
import net.twasiplugin.songrequests.database.requests.SongRequestRepo;
import net.twasiplugin.songrequests.database.requests.exceptions.DuplicateSongException;
import net.twasiplugin.songrequests.database.requests.exceptions.TooManyRequestsException;
import net.twasiplugin.songrequests.database.spotifycredentials.SpotifyCredentialsRepo;
import net.twasiplugin.songrequests.database.usersettings.SongRequestSettingsRepo;
import net.twasiplugin.songrequests.providers.ProviderSearch;
import net.twasiplugin.songrequests.providers.spotify.SpotifySearch;
import net.twasiplugin.songrequests.providers.youtube.YouTubeSearch;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static net.twasiplugin.songrequests.SongRequestProvider.SPOTIFY;
import static net.twasiplugin.songrequests.SongRequestProvider.YOUTUBE;

public class SongRequestCommand extends TwasiPluginCommand {

    private static SpotifyCredentialsRepo credentialsRepo = DataService.get().get(SpotifyCredentialsRepo.class);
    private static SongRequestRepo songrequestRepo = DataService.get().get(SongRequestRepo.class);
    private static SongRequestSettingsRepo settingsRepo = DataService.get().get(SongRequestSettingsRepo.class);

    public SongRequestCommand(TwasiUserPlugin twasiUserPlugin) {
        super(twasiUserPlugin);
    }

    @Override
    public String getCommandName() {
        return "songrequest";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("sr");
    }

    @Override
    protected boolean execute(TwasiCustomCommandEvent event) {
        User user = event.getStreamer().getUser();

        String name = event.getArgsAsOne();
        TranslationRenderer renderer = event.getRenderer("songrequests");

        if (name.length() == 0) {
            event.reply(renderer.render("help"));
            return false;
        }

        SongRequestProvider provider = detectProvider(name);
        if (provider != null) {
            try {
                name = name.split(" ", 2)[1];
            } catch (Exception e) {
                event.reply(renderer.render("help"));
                return false;
            }
        } else if (credentialsRepo.hasCredentials(user)) provider = SPOTIFY;
        else provider = YOUTUBE;

        renderer.bind("query", name);
        renderer.bind("provider", provider.toPrettyString());
        renderer.bind("providerId", String.valueOf(provider.getFrontendId()));

        ProviderSearch songs;
        try {
            songs = provider == SPOTIFY ? new SpotifySearch(name, user, 1) : new YouTubeSearch(name, user, 1);
        } catch (UnauthorizedException e) {
            // TODO find correct Exception (UnauthorizedException isn't it)
            event.reply(renderer.render("spotify-reauth"));
            credentialsRepo.removeByUser(user);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            event.reply(renderer.render("error"));
            return false;
        }

        if (songs.size() == 0) {
            event.reply(renderer.render("nothing-found"));
            return false;
        }
        SongDTO song = songs.get(0);
        renderer.bindObject("song", song);

        song.requester = RequesterDTO.from(event.getSender());

        long maxRequests = getMaxRequests(event.getSender().getGroups(), user);
        renderer.bind("maxRequests", String.valueOf(maxRequests));
        try {
            songrequestRepo.checkAndAdd(song, user, maxRequests);
        } catch (TooManyRequestsException e) {
            event.reply(renderer.render("too-many-requests"));
            return false;
        } catch (DuplicateSongException e) {
            event.reply(renderer.render("duplicate-song"));
            return false;
        }

        SongrequestPlugin.EVENTS.updateQueue(user);
        event.reply(renderer.render("success"));
        return true;
    }

    private SongRequestProvider detectProvider(String name) {
        String first = name.split(" ")[0].toLowerCase();
        if (first.equals("spotify") || first.equals("s")) return SPOTIFY;
        if (first.equals("youtube") || first.equals("yt")) return YOUTUBE;
        return null;
    }

    private long getMaxRequests(List<PermissionGroups> groups, User user) {
        Map<PermissionGroups, Integer> settings = settingsRepo.getByUser(user).getMaxRequests();
        if (groups.contains(PermissionGroups.BROADCASTER) && settings.containsKey(PermissionGroups.BROADCASTER))
            return settings.get(PermissionGroups.BROADCASTER);
        if (groups.contains(PermissionGroups.MODERATOR) && settings.containsKey(PermissionGroups.MODERATOR))
            return settings.get(PermissionGroups.MODERATOR);
        if (groups.contains(PermissionGroups.SUBSCRIBERS) && settings.containsKey(PermissionGroups.SUBSCRIBERS))
            return settings.get(PermissionGroups.SUBSCRIBERS);
        if (groups.contains(PermissionGroups.VIEWER) && settings.containsKey(PermissionGroups.VIEWER))
            return settings.get(PermissionGroups.VIEWER);
        return 3;
    }


}
