package net.twasiplugin.songrequests.api.ws.songrequest.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import net.twasi.core.database.models.TwitchAccount;
import net.twasiplugin.songrequests.SongRequestProvider;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class SongDTO {

    public ObjectId userId;
    public RequesterDTO requester;
    public String songName;
    public List<String> artists;
    public SongRequestProvider provider;
    public String uri;
    public long timeStamp;

    public SongDTO(ObjectId userId, RequesterDTO requester, String songName, List<String> artists, SongRequestProvider provider, String uri, long timeStamp) {
        this.userId = userId;
        this.requester = requester;
        this.songName = songName;
        this.artists = artists;
        this.provider = provider;
        this.uri = uri;
        this.timeStamp = timeStamp;
    }

    public SongDTO(ObjectId userId, RequesterDTO requester, String songName, List<String> artists, SongRequestProvider provider, String uri) {
        this(userId, requester, songName, artists, provider, uri, Calendar.getInstance().getTime().getTime());
    }

    public SongDTO() {
    }

    public static SongDTO from(JsonObject song) {
        return new Gson().fromJson(song, SongDTO.class);
    }

    public static SongDTO from(Track spotifyTrack, TwitchAccount requester, ObjectId user) {
        return from(spotifyTrack, RequesterDTO.from(requester), user);
    }

    public static SongDTO from(Track spotifyTrack, RequesterDTO requester, ObjectId user) {
        return new SongDTO(
                user,
                requester,
                spotifyTrack.getName(),
                Arrays.stream(spotifyTrack.getArtists()).map(ArtistSimplified::getName).collect(Collectors.toList()),
                SongRequestProvider.SPOTIFY,
                spotifyTrack.getUri()
        );
    }
}
