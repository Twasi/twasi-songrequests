package net.twasiplugin.songrequests.api.ws.songrequest.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Track;
import net.twasi.core.database.models.TwitchAccount;
import net.twasiplugin.songrequests.SongRequestProvider;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class SongDTO {

    public ObjectId userId;
    public RequesterDTO requester;
    public String name;
    public List<String> artists;
    public List<String> covers;
    public int provider;
    public String uri;
    public int duration;
    public long timeStamp;

    public SongDTO(ObjectId userId, RequesterDTO requester, String songName, List<String> artists, List<String> covers, SongRequestProvider provider, String uri, int duration, long timeStamp) {
        this.userId = userId;
        this.requester = requester;
        this.name = songName;
        this.artists = artists;
        this.covers = covers;
        this.provider = provider.getFrontendId();
        this.uri = uri;
        this.duration = duration;
        this.timeStamp = timeStamp;
    }

    public SongDTO(ObjectId userId, RequesterDTO requester, String songName, List<String> artists, List<String> covers, SongRequestProvider provider, String uri, int duration) {
        this(userId, requester, songName, artists, covers, provider, uri, duration, Calendar.getInstance().getTime().getTime());
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
                Arrays.stream(spotifyTrack.getAlbum().getImages()).map((Image::getUrl)).collect(Collectors.toList()),
                SongRequestProvider.SPOTIFY,
                spotifyTrack.getUri(),
                spotifyTrack.getDurationMs()
        );
    }
}
