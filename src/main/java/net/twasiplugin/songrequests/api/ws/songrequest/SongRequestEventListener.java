package net.twasiplugin.songrequests.api.ws.songrequest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
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
import net.twasiplugin.songrequests.database.models.SongrequestDTO;
import net.twasiplugin.songrequests.database.repos.SongrequestRepo;
import net.twasiplugin.songrequests.spotify.SpotifyApiBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SongRequestEventListener extends TwasiWebsocketListenerEndpoint<SongRequestEventListenerConfig> {

    private static SongrequestRepo repo = DataService.get().get(SongrequestRepo.class);

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
                return TwasiWebsocketAnswer.success(new Gson().toJsonTree(getResolvedQueue(getQueue(user))));
            case "add":
                return this.add(msg, user);
            case "search":
                return this.search(msg, user);
        }
        return TwasiWebsocketAnswer.warn("No valid request type delivered.");
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
                return null;
            case SPOTIFY:
                // Check token
                if (!request.has("token"))
                    throw new WebsocketHandledException("No token provided.");
                String token = request.get("token").getAsString();

                // Build api and set token
                SpotifyApi api = SpotifyApiBuilder.build();
                api.setAccessToken(token);
                int page = 1;

                // Get requested page or set 1 if none provided
                if (request.has("page")) page = request.get("page").getAsInt();
                if (page < 1) page = 1;

                // Build query
                SearchTracksRequest.Builder songs = api.searchTracks(query).limit(5).offset((page - 1) * 5);

                // Execute query
                Paging<Track> execute = songs.build().execute();

                // Map result
                List<Object> list = Arrays.stream(execute.getItems()).map(track -> SongDTO.from(track, (RequesterDTO) null, user.getId())).collect(Collectors.toList());
                return TwasiWebsocketAnswer.success(new Gson().toJsonTree(list));
        }
    }

    private JsonElement back(User user) {
        return null;
    }

    private JsonElement next(User user, boolean skip) {
        List<SongrequestDTO> queue = getQueue(user);
        if (queue.size() > 0) {
            SongrequestDTO update = queue.get(0);
            if (skip) update.setSkipped();
            else update.setPlayed();
            repo.commit(update);
            queue.remove(0);
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
            SongrequestDTO songrequestDTO = new SongrequestDTO(user.getId(), dto);
            this.add(songrequestDTO, user);
            return TwasiWebsocketAnswer.success();
        } catch (JsonParseException e) {
            return TwasiWebsocketAnswer.warn("Invalid song provided.");
        }
    }

    private void add(SongrequestDTO song, User user) {
        repo.add(song);
        updateQueue(user);
    }

    public void updateQueue(User user) {
        publish(user.getTwitchAccount(), new TwasiWebsocketEvent<>(getResolvedQueue(getQueue(user)), "queue").toSendable());
    }

    private List<SongrequestDTO> getQueue(User user) {
        return repo.getRequestsByUser(user, false);
    }

    private List<SongDTO> getResolvedQueue(List<SongrequestDTO> queue) {
        return queue.stream().map(SongrequestDTO::getSong).collect(Collectors.toList());
    }

    private void publish(TwitchAccount channel, JsonElement element) {
        publishFilteredByConfig(config -> config.channel.equalsIgnoreCase(channel.getTwitchId()) || config.channel.equalsIgnoreCase(channel.getUserName()), element);
    }
}
