package net.twasiplugin.songrequests;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import net.twasi.core.api.ws.api.TwasiWebsocketEndpoint;
import net.twasi.core.database.models.User;
import net.twasi.core.plugin.TwasiPlugin;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.ServletService;
import net.twasiplugin.songrequests.api.ws.songrequest.SongRequestEventListener;
import net.twasiplugin.songrequests.api.ws.songrequest.models.PreviewSongDTO;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;
import net.twasiplugin.songrequests.api.ws.spotify.SpotifyCredentials;
import net.twasiplugin.songrequests.providers.spotify.SpotifyFetch;
import net.twasiplugin.songrequests.servlets.SpotifyAuthServlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SongrequestPlugin extends TwasiPlugin<SongrequestsConfig> {

    public static SongrequestsConfig CONFIG;
    public static SongRequestEventListener EVENTS;
    public static List<PreviewSongDTO> previewSongDTOs = null;

    @Override
    public void onActivate() {
        CONFIG = getConfiguration();
        ServiceRegistry.get(ServletService.class).addServlet(SpotifyAuthServlet.class, "spotify-callback");
    }

    public Class<? extends TwasiUserPlugin> getUserPluginClass() {
        return SongrequestUserPlugin.class;
    }

    @Override
    public List<TwasiWebsocketEndpoint<?>> getWebsocketEndpoints() {
        return Arrays.asList(
                new SpotifyCredentials(),
                EVENTS = new SongRequestEventListener()
        );
    }

    public static List<PreviewSongDTO> getPreviewSongDTOs(User user) {
        if (previewSongDTOs != null) return previewSongDTOs;
        try {
            List<SongDTO> dtos = new SpotifyFetch(CONFIG.previewSongs.stream().map(dto -> dto.spotify.uri).collect(Collectors.toList()), user);
            previewSongDTOs = dtos.stream()
                    .map(dto -> PreviewSongDTO.from(
                            dto, CONFIG.previewSongs.stream()
                                    .filter(s -> s.spotify.uri.equals(dto.uri))
                                    .findFirst()
                                    .get())
                    ).collect(Collectors.toList());
        } catch (IOException | SpotifyWebApiException e) {
            e.printStackTrace();
            return null;
        }
        return previewSongDTOs;
    }
}
