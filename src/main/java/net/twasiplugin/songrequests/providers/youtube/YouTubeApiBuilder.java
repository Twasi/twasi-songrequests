package net.twasiplugin.songrequests.providers.youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Search.List;
import net.twasiplugin.songrequests.SongrequestPlugin;

import java.io.IOException;

public class YouTubeApiBuilder {

    public static List buildSearch(int max) throws IOException {
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
        }).setApplicationName("youtube-search").build();
        List list = youtube.search().list("id,snippet");
        list.setKey(SongrequestPlugin.CONFIG.youTubeApiKey);
        list.setType("video");
        list.setMaxResults((long) max);
        return list;
    }

    public static List buildSearch() throws IOException {
        return buildSearch(5);
    }

    public static YouTube.Videos.List buildContentDetails() throws IOException {
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
        }).setApplicationName("youtube-search-content-details").build();
        YouTube.Videos.List list = youtube.videos().list("contentDetails");
        list.setPart("contentDetails");
        list.setKey(SongrequestPlugin.CONFIG.youTubeApiKey);
        return list;
    }

}
