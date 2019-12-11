package net.twasiplugin.songrequests.api.ws.songrequest.models;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Track;
import net.twasi.core.database.models.TwitchAccount;
import net.twasiplugin.songrequests.SongRequestProvider;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class SongDTO {

    public ObjectId userId;
    public String id = null;
    public RequesterDTO requester;
    public String name;
    public List<String> artists;
    public List<String> covers;
    public int provider;
    public String uri;
    public String url;
    public int duration;
    public long timeStamp;
    public PlayInformation playInformation = new PlayInformation();

    public SongDTO(ObjectId userId, RequesterDTO requester, String songName, List<String> artists, List<String> covers, SongRequestProvider provider, String uri, String url, int duration, long timeStamp) {
        this.userId = userId;
        this.requester = requester;
        this.name = songName;
        this.artists = artists;
        this.covers = covers;
        this.provider = provider.getFrontendId();
        this.uri = uri;
        this.url = url;
        this.duration = duration;
        this.timeStamp = timeStamp;
    }

    public SongDTO(ObjectId userId, RequesterDTO requester, String songName, List<String> artists, List<String> covers, SongRequestProvider provider, String uri, String url, int duration) {
        this(userId, requester, songName, artists, covers, provider, uri, url, duration, Calendar.getInstance().getTime().getTime());
    }

    // Default constructor for Morphia
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
                "http://open.spotify.com/track/" + spotifyTrack.getUri().replace("spotify:track:", ""),
                spotifyTrack.getDurationMs()
        );
    }

    public static SongDTO from(SearchResult youTubeTrack, int duration, TwitchAccount requester, ObjectId user) {
        return from(youTubeTrack, duration, RequesterDTO.from(requester), user);
    }

    public static SongDTO from(SearchResult youTubeTrack, int duration, RequesterDTO requester, ObjectId user) {
        return new SongDTO(
                user,
                requester,
                youTubeTrack.getSnippet().getTitle(),
                Collections.singletonList(youTubeTrack.getSnippet().getChannelTitle()),
                Collections.singletonList(youTubeTrack.getSnippet().getThumbnails().getDefault().getUrl()),
                SongRequestProvider.YOUTUBE,
                youTubeTrack.getId().getVideoId(),
                "https://youtube.com/watch?v=" + youTubeTrack.getId().getVideoId(),
                duration
        );
    }

    public static SongDTO from(SearchResult item, List<Video> durations, RequesterDTO requesterDTO, ObjectId id) {
        Video video = durations.stream().filter(item2 -> item.getId().getVideoId().equals(item2.getId())).findAny().get();
        long l = Duration.parse(video.getContentDetails().getDuration()).toMillis();
        return from(item, (int) l, requesterDTO, id);
    }

    public static class PlayInformation {

        public long played = -1L;
        public long skipped = -1L;

    }
}
