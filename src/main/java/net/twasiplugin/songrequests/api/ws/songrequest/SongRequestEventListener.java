package net.twasiplugin.songrequests.api.ws.songrequest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import net.twasi.core.api.ws.api.TwasiWebsocketListenerEndpoint;
import net.twasi.core.api.ws.models.TwasiWebsocketAnswer;
import net.twasi.core.api.ws.models.TwasiWebsocketEvent;
import net.twasi.core.api.ws.models.TwasiWebsocketMessage;
import net.twasi.core.api.ws.models.WebsocketHandledException;
import net.twasi.core.database.models.TwitchAccount;
import net.twasi.core.database.models.User;
import net.twasi.core.services.providers.DataService;
import net.twasiplugin.songrequests.SongRequestProvider;
import net.twasiplugin.songrequests.api.ws.songrequest.models.RequesterDTO;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;
import net.twasiplugin.songrequests.database.reports.ReportDTO;
import net.twasiplugin.songrequests.database.reports.ReportRepo;
import net.twasiplugin.songrequests.database.requests.SongRequestDTO;
import net.twasiplugin.songrequests.database.requests.SongRequestRepo;
import net.twasiplugin.songrequests.database.usersettings.SongRequestSettingsDTO;
import net.twasiplugin.songrequests.database.usersettings.SongRequestSettingsRepo;
import net.twasiplugin.songrequests.spotify.SpotifySearch;
import net.twasiplugin.songrequests.youtube.YouTubeSearch;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SongRequestEventListener extends TwasiWebsocketListenerEndpoint<SongRequestEventListenerConfig> {

    private static SongRequestRepo repo = DataService.get().get(SongRequestRepo.class);

    public String getTopic() {
        return "events";
    }

    @Override
    public JsonElement handle(TwasiWebsocketMessage msg) throws Exception {
        User user = msg.getClient().getAuthentication().getUser();
        JsonObject action = msg.getAction().getAsJsonObject();
        boolean skip = false;
        switch (action.get("type").getAsString().toLowerCase()) {
            case "skip":
                skip = true;
            case "next":
                return this.next(user, skip);
            case "back":
                return this.back(user);
            case "queue":
                return TwasiWebsocketAnswer.success(new Gson().toJsonTree(
                        new QueueUpdate(
                                getResolvedQueue(getQueue(user)),
                                getResolvedHistory(getHistory(user))
                        )
                ));
            case "add":
                return this.add(msg, user);
            case "search":
                return this.search(msg, user);
            case "report":
                return this.report(msg, user);
            case "settings":
                return this.settings(msg, user);
            case "remove":
                return this.remove(msg, user);
        }
        return TwasiWebsocketAnswer.warn("No valid request type delivered.");
    }

    private JsonElement remove(TwasiWebsocketMessage msg, User user) {
        repo.removeById(msg.getAction().getAsJsonObject().get("songId").getAsString(), user);
        this.updateQueue(user);
        return TwasiWebsocketAnswer.success();
    }

    private JsonElement settings(TwasiWebsocketMessage msg, User user) {
        SongRequestSettingsRepo repo = DataService.get().get(SongRequestSettingsRepo.class);
        SongRequestSettingsDTO dto = repo.getByUser(user);
        JsonObject action = msg.getAction().getAsJsonObject();
        if (action.has("settings")) {
            dto.apply(action.get("settings").getAsJsonObject());
            repo.commit(dto);
        }
        return TwasiWebsocketAnswer.success(new Gson().toJsonTree(dto));
    }

    private JsonElement report(TwasiWebsocketMessage msg, User user) {
        ReportDTO dto = new Gson().fromJson(msg.getAction().getAsJsonObject().get("report").getAsJsonObject(), ReportDTO.class);
        dto.user = user.getId();
        DataService.get().get(ReportRepo.class).add(dto);
        return TwasiWebsocketAnswer.success();
    }

    private JsonElement search(TwasiWebsocketMessage msg, User user) throws IOException, SpotifyWebApiException {
        JsonObject request = msg.getAction().getAsJsonObject();

        if (!request.has("provider"))
            throw new WebsocketHandledException("No provider provided (Omegalul).");

        if (!request.has("query"))
            throw new WebsocketHandledException("No query provided.");

        SongRequestProvider provider = SongRequestProvider.getByFrontendId(request.get("provider").getAsInt());
        if (provider == null)
            throw new WebsocketHandledException("Unknown provider.");

        String query = request.get("query").getAsString();
        if (query == null)
            throw new WebsocketHandledException("Invalid query.");

        switch (provider) {
            case YOUTUBE:
            default:
                return TwasiWebsocketAnswer.success(new Gson().toJsonTree(
                        new YouTubeSearch(query, user)
                ));

            case SPOTIFY:
                return TwasiWebsocketAnswer.success(new Gson().toJsonTree(
                        new SpotifySearch(query, user)
                ));
        }
    }

    private JsonElement back(User user) {
        List<SongRequestDTO> playedSongs = repo.getRequestsByUser(user, true);
        if (playedSongs.size() == 0) return TwasiWebsocketAnswer.warn("There are no songs you could go back to.");
        SongRequestDTO last = playedSongs.get(0);
        last.resetPlayed();
        last.resetSkipped();
        repo.commit(last);
        updateQueue(user);
        return TwasiWebsocketAnswer.success();
    }

    private JsonElement next(User user, boolean skip) {
        List<SongRequestDTO> queue = getQueue(user);
        if (queue.size() > 0) {
            SongRequestDTO update = queue.get(0);
            if (skip) update.setSkipped();
            else update.setPlayed();
            repo.commit(update);
        }
        updateQueue(user);
        return TwasiWebsocketAnswer.success();
    }

    private JsonElement add(TwasiWebsocketMessage msg, User user) {
        try {
            JsonObject song = msg.getAction().getAsJsonObject().get("song").getAsJsonObject();
            try {
                if (!song.has("requester")) {
                    song.remove("requester");
                    song.add("requester", new Gson().toJsonTree(RequesterDTO.from(user.getTwitchAccount())));
                }
            } catch (Exception ignored) {
            }
            SongDTO dto = new Gson().fromJson(song, SongDTO.class);
            SongRequestDTO songrequestDTO = new SongRequestDTO(user.getId(), dto);
            this.add(songrequestDTO, user);
            return TwasiWebsocketAnswer.success();
        } catch (JsonParseException e) {
            return TwasiWebsocketAnswer.warn("Invalid song provided.");
        }
    }

    private void add(SongRequestDTO song, User user) {
        repo.add(song);
        updateQueue(user);
    }

    public void updateQueue(User user) {
        publish(user.getTwitchAccount(), new TwasiWebsocketEvent<>(
                new QueueUpdate(getResolvedQueue(getQueue(user)), getResolvedHistory(getHistory(user))),
                "queue"
        ).toSendable());
    }

    public List<SongRequestDTO> getQueue(User user) {
        return repo.getRequestsByUser(user, false);
    }

    private List<SongRequestDTO> getHistory(User user) {
        return repo.getRequestsByUser(user, true, 10);
    }

    private List<SongDTO> getResolvedQueue(List<SongRequestDTO> queue) {
        return queue.stream().map(SongRequestDTO::getSong).collect(Collectors.toList());
    }

    private List<SongDTO> getResolvedHistory(List<SongRequestDTO> history) {
        return history.stream().map(dto -> {
            SongDTO song = dto.getSong();
            Date played = dto.getPlayed();
            Date skipped = dto.getSkipped();
            song.playInformation.played = played != null ? played.getTime() : -1L;
            song.playInformation.skipped = skipped != null ? skipped.getTime() : -1L;
            return song;
        }).collect(Collectors.toList());
    }

    private void publish(TwitchAccount channel, JsonElement element) {
        publishFilteredByConfig(config -> config.channel.equalsIgnoreCase(channel.getTwitchId()) || config.channel.equalsIgnoreCase(channel.getUserName()), element);
    }

    private static class QueueUpdate {
        public List<SongDTO> queue;
        public List<SongDTO> history;

        public QueueUpdate(List<SongDTO> queue, List<SongDTO> history) {
            this.queue = queue;
            this.history = history;
        }
    }
}
