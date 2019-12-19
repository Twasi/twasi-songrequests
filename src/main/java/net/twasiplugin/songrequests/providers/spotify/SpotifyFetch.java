package net.twasiplugin.songrequests.providers.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.tracks.GetSeveralTracksRequest;
import net.twasi.core.database.models.User;
import net.twasi.core.services.providers.DataService;
import net.twasiplugin.songrequests.api.ws.songrequest.models.RequesterDTO;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;
import net.twasiplugin.songrequests.database.cache.songcache.SongCacheRepo;
import net.twasiplugin.songrequests.database.spotifycredentials.SpotifyCredentialsDTO;
import net.twasiplugin.songrequests.database.spotifycredentials.SpotifyCredentialsRepo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static net.twasiplugin.songrequests.api.ws.spotify.SpotifyCredentials.getValidUntil;

public class SpotifyFetch extends ArrayList<SongDTO> {

    private static SongCacheRepo repo = DataService.get().get(SongCacheRepo.class);

    public SpotifyFetch(List<String> uris, User user) throws IOException, SpotifyWebApiException {
        List<SongDTO> cachedSongs = repo.getByUris(uris);
        List<String> fetchUris = uris.stream()
                .filter(uri -> cachedSongs.stream().noneMatch(dto -> dto.uri.equals(uri)))
                .collect(Collectors.toList());

        if (fetchUris.size() == 0) {
            this.addAll(cachedSongs);
            return;
        }

        SpotifyApi sApi = SpotifyApiBuilder.build();

        if (user != null) {
            SpotifyCredentialsRepo repo = DataService.get().get(SpotifyCredentialsRepo.class);

            SpotifyCredentialsDTO dto = repo.getByUser(user);
            if (dto.getValidUntil().getTime() < Calendar.getInstance().getTimeInMillis() - 3000) {
                AuthorizationCodeCredentials result = sApi.authorizationCodeRefresh().refresh_token(dto.getRefreshToken()).build().execute();
                dto.setAccessToken(result.getAccessToken());
                dto.setValidUntil(getValidUntil(result.getExpiresIn()));
                repo.commit(dto);
            }

            sApi.setAccessToken(dto.getAccessToken());
        }

        GetSeveralTracksRequest.Builder severalTracks = sApi.getSeveralTracks(
                fetchUris.stream()
                        .map(uri -> uri.replace("spotify:track:", ""))
                        .collect(Collectors.toList())
                        .toArray(new String[]{})
        );

        Track[] execute = severalTracks.build().execute();

        List<SongDTO> songs;
        this.addAll(
                songs = Arrays.stream(execute)
                        .map(t -> SongDTO.from(t, (RequesterDTO) null, user.getId()))
                        .collect(Collectors.toList())
        );

        songs.forEach(repo::cache);
    }

}
