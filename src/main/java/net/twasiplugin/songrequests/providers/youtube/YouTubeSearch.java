package net.twasiplugin.songrequests.providers.youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import net.twasi.core.database.models.User;
import net.twasiplugin.songrequests.SongRequestProvider;
import net.twasiplugin.songrequests.api.ws.songrequest.models.RequesterDTO;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;
import net.twasiplugin.songrequests.providers.ProviderSearch;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class YouTubeSearch extends ProviderSearch {

    public YouTubeSearch(String query, RequesterDTO requester, User user, int max) throws IOException {
        ProviderSearch cachedResult = getCachedResult(query);
        if (cachedResult != null) {
            addAll(cachedResult);
            return;
        }

        YouTube.Search.List ySearchApi = YouTubeApiBuilder.buildSearch();
        ySearchApi.setQ(query);
        List<SearchResult> searchResponse = ySearchApi.execute().getItems();

        YouTube.Videos.List yDetailsApi = YouTubeApiBuilder.buildContentDetails();
        yDetailsApi.setId(searchResponse.stream().map(item -> item.getId().getVideoId()).collect(Collectors.joining(",")));
        List<Video> durations = yDetailsApi.execute().getItems();
        List<SongDTO> collect = searchResponse.stream().map(item -> SongDTO.from(
                item,
                durations,
                requester,
                user.getId()
        )).collect(Collectors.toList());
        addAll(collect);

        cache(query, this);
    }

    public YouTubeSearch(String query, RequesterDTO requester, User user) throws IOException {
        this(query, requester, user, 5);
    }

    public YouTubeSearch(String query, User user, int max) throws IOException {
        this(query, null, user, max);
    }

    public YouTubeSearch(String query, User user) throws IOException {
        this(query, null, user, 5);
    }

    @Override
    protected SongRequestProvider getProvider() {
        return SongRequestProvider.YOUTUBE;
    }
}
