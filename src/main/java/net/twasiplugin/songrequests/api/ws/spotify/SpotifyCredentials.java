package net.twasiplugin.songrequests.api.ws.spotify;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import net.twasi.core.api.ws.api.TwasiWebsocketEndpoint;
import net.twasi.core.api.ws.api.WebsocketClientConfig;
import net.twasi.core.api.ws.models.TwasiWebsocketAnswer;
import net.twasi.core.api.ws.models.TwasiWebsocketMessage;
import net.twasi.core.api.ws.models.WebsocketHandledException;
import net.twasi.core.database.models.User;
import net.twasi.core.services.providers.DataService;
import net.twasiplugin.songrequests.database.spotifycredentials.SpotifyCredentialsDTO;
import net.twasiplugin.songrequests.database.spotifycredentials.SpotifyCredentialsRepo;
import net.twasiplugin.songrequests.providers.spotify.SpotifyApiBuilder;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static net.twasiplugin.songrequests.SongrequestPlugin.CONFIG;

public class SpotifyCredentials extends TwasiWebsocketEndpoint<WebsocketClientConfig> {

    private SpotifyCredentialsRepo repo = DataService.get().get(SpotifyCredentialsRepo.class);

    public String getTopic() {
        return "spotify-credentials";
    }

    @Override
    public JsonElement handle(TwasiWebsocketMessage msg) throws IOException {
        JsonObject action = msg.getAction().getAsJsonObject();
        switch (action.get("type").getAsString().toLowerCase()) {
            case "init":
                return init();
            case "get":
            case "refresh": // Don't let the client decide when to refresh. If The token is nearly dead it refreshes anyway
                return get(msg);
            case "set":
                return set(msg);
            case "remove":
                return remove(msg);
        }
        return TwasiWebsocketAnswer.warn("No valid request type delivered.");
    }

    private JsonElement remove(TwasiWebsocketMessage msg) {
        repo.removeByUser(msg.getClient().getAuthentication().getUser());
        return TwasiWebsocketAnswer.success();
    }

    private JsonElement refresh(TwasiWebsocketMessage msg) throws IOException {
        try {
            SpotifyCredentialsDTO dto = repo.getByUser(msg.getClient().getAuthentication().getUser());
            AuthorizationCodeRefreshRequest request = SpotifyApiBuilder.build().authorizationCodeRefresh().refresh_token(dto.getRefreshToken()).build();
            AuthorizationCodeCredentials credentials = request.execute();
            dto.setAccessToken(credentials.getAccessToken());
            dto.setValidUntil(getValidUntil(credentials.getExpiresIn()));
            repo.commit(dto);
            return getAnswerFromDTO(dto);
        } catch (NullPointerException e) {
            return TwasiWebsocketAnswer.warn("NO_CREDENTIALS_FOUND");
        } catch (SpotifyWebApiException e) {
            throw new WebsocketHandledException("Error with Spotify API. Please try to reauthenticate.");
        }
    }

    private JsonElement init() {
        AuthorizationCodeUriRequest request = SpotifyApiBuilder.build().authorizationCodeUri().scope(CONFIG.spotify.scope).build();
        String uri = request.execute().toString();
        JsonObject res = new JsonObject();
        res.add("uri", new JsonPrimitive(uri));
        return TwasiWebsocketAnswer.success(res);
    }

    private JsonElement get(TwasiWebsocketMessage msg) throws IOException {
        long now = Calendar.getInstance().getTime().getTime();
        try {
            SpotifyCredentialsDTO dto = DataService.get().get(SpotifyCredentialsRepo.class).getByUser(msg.getClient().getAuthentication().getUser());
            if (now > (dto.getValidUntil().getTime() - 10 * 60 * 1000)) return refresh(msg);
            return getAnswerFromDTO(dto);
        } catch (NullPointerException e) {
            return TwasiWebsocketAnswer.warn("NO_CREDENTIALS_FOUND");
        }
    }

    private JsonElement set(TwasiWebsocketMessage msg) throws IOException {
        try {
            User user = msg.getClient().getAuthentication().getUser();
            repo.removeByUser(user);
            AuthorizationCodeRequest request = SpotifyApiBuilder.build().authorizationCode(msg.getAction().getAsJsonObject().get("code").getAsString()).build();
            AuthorizationCodeCredentials credentials = request.execute();
            SpotifyCredentialsDTO dto = new SpotifyCredentialsDTO(user.getId(), credentials.getAccessToken(), credentials.getRefreshToken(), getValidUntil(credentials.getExpiresIn()));
            repo.add(dto);
            return getAnswerFromDTO(dto);
        } catch (JsonParseException e) {
            throw new WebsocketHandledException("No authorization code provided.");
        } catch (SpotifyWebApiException e) {
            throw new WebsocketHandledException("Error with Spotify API. Please try to reauthenticate.");
        }
    }

    private JsonElement getAnswerFromDTO(SpotifyCredentialsDTO dto) {
        JsonObject ob = new JsonObject();
        ob.add("token", new JsonPrimitive(dto.getAccessToken()));
        ob.add("expires", new JsonPrimitive(dto.getValidUntil().getTime()));
        return ob;
    }

    public static Date getValidUntil(int expiresIn) {
        return new Date(Calendar.getInstance().getTime().getTime() + (expiresIn - 60) * 1000);
    }
}
