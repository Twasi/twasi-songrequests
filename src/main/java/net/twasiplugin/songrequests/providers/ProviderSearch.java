package net.twasiplugin.songrequests.providers;

import net.twasi.core.services.providers.DataService;
import net.twasiplugin.songrequests.SongRequestProvider;
import net.twasiplugin.songrequests.api.ws.songrequest.models.RequesterDTO;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;
import net.twasiplugin.songrequests.database.songcache.SongCacheRepo;

import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class ProviderSearch extends ArrayList<SongDTO> {

    protected final static SongCacheRepo cache = DataService.get().get(SongCacheRepo.class);
    private static ArrayList<SearchDetails> searchCache = new ArrayList<>();
    private RequesterDTO requester;

    protected abstract SongRequestProvider getProvider();

    protected ProviderSearch(RequesterDTO requester) {
        this.requester = requester;
    }

    protected final ProviderSearch getCachedResult(String query) {
        return searchCache.stream()
                .filter(details -> details.query.equalsIgnoreCase(query))
                .map(details -> details.results)
                .peek(results -> results.forEach(song -> song.requester = requester))
                .findFirst()
                .orElse(null);
    }

    protected final void cache(String query, ProviderSearch results) {
        searchCache = searchCache.stream()
                .filter(details -> details.provider != getProvider() || !details.query.equalsIgnoreCase(query))
                .collect(Collectors.toCollection(ArrayList::new));
        searchCache.add(new SearchDetails(getProvider(), query, results));
        if (results != null) for (SongDTO result : results)
            cache.cache(result);
    }

    protected static class SearchDetails {
        public final SongRequestProvider provider;
        public final String query;
        public final ProviderSearch results;

        protected SearchDetails(SongRequestProvider provider, String query, ProviderSearch results) {
            this.provider = provider;
            this.query = query;
            this.results = results;
        }
    }
}
