package net.twasiplugin.songrequests.youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Search.List;

import java.io.IOException;

public class YouTubeApiBuilder {

    public static List build() throws IOException {
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
        }).setApplicationName("youtube-search").build();
        List list = youtube.search().list("id,snippet");
        /*list.setKey();*/
        return list;
    }

}
