package net.twasiplugin.songrequests.youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Search.List;
import net.twasiplugin.songrequests.SongrequestsPlugin;

import java.io.IOException;

public class YouTubeApiBuilder {

    public static List buildSearch() throws IOException {
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
        }).setApplicationName("youtube-search").build();
        List list = youtube.search().list("id,snippet");
        list.setKey(SongrequestsPlugin.CONFIG.youTubeApiKey);
        list.setType("video");
        list.setMaxResults(5L);
        return list;
    }

    public static YouTube.Videos.List buildContentDetails() throws IOException {
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
        }).setApplicationName("youtube-search-content-details").build();
        YouTube.Videos.List list = youtube.videos().list("contentDetails");
        list.setPart("contentDetails");
        list.setKey(SongrequestsPlugin.CONFIG.youTubeApiKey);
        return list;
    }

}
