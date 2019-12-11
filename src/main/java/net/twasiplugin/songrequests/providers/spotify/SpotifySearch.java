package net.twasiplugin.songrequests.providers.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import net.twasi.core.database.models.User;
import net.twasi.core.services.providers.DataService;
import net.twasiplugin.songrequests.SongRequestProvider;
import net.twasiplugin.songrequests.api.ws.songrequest.models.RequesterDTO;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;
import net.twasiplugin.songrequests.database.spotifycredentials.SpotifyCredentialsDTO;
import net.twasiplugin.songrequests.database.spotifycredentials.SpotifyCredentialsRepo;
import net.twasiplugin.songrequests.providers.ProviderSearch;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static net.twasiplugin.songrequests.api.ws.spotify.SpotifyCredentials.getValidUntil;

public class SpotifySearch extends ProviderSearch {

    public SpotifySearch(String query, RequesterDTO requester, User user, int max) throws IOException, SpotifyWebApiException {
        super(requester);
        ProviderSearch cachedResult = getCachedResult(query);
        if (cachedResult != null) {
            addAll(cachedResult);
            return;
        }

        // Build api and set token
        SpotifyApi sApi = SpotifyApiBuilder.build();
        SpotifyCredentialsRepo repo = DataService.get().get(SpotifyCredentialsRepo.class);

        SpotifyCredentialsDTO dto = repo.getByUser(user);
        if (dto.getValidUntil().getTime() < Calendar.getInstance().getTimeInMillis() - 3000) {
            AuthorizationCodeCredentials result = sApi.authorizationCodeRefresh().refresh_token(dto.getRefreshToken()).build().execute();
            dto.setAccessToken(result.getAccessToken());
            dto.setValidUntil(getValidUntil(result.getExpiresIn()));
            repo.commit(dto);
        }

        sApi.setAccessToken(dto.getAccessToken());

        // Build query
        int page = 1;
        SearchTracksRequest.Builder songs = sApi.searchTracks(query).limit(max).offset((page - 1) * 5);

        // Execute query
        Paging<Track> execute = songs.build().execute();

        // Map result
        List<SongDTO> list = Arrays.stream(execute.getItems()).map(track -> SongDTO.from(track, requester, user.getId())).collect(Collectors.toList());
        addAll(list);

        cache(query, this);
    }

    public SpotifySearch(String query, User user, int max) throws IOException, SpotifyWebApiException {
        this(query, null, user, max);
    }

    public SpotifySearch(String query, RequesterDTO requester, User user) throws IOException, SpotifyWebApiException {
        this(query, requester, user, 5);
    }

    public SpotifySearch(String query, User user) throws IOException, SpotifyWebApiException {
        this(query, null, user, 5);
    }

    @Override
    protected SongRequestProvider getProvider() {
        return SongRequestProvider.SPOTIFY;
    }
}
