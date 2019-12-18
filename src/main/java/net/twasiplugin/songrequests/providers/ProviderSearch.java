package net.twasiplugin.songrequests.providers;

import net.twasi.core.services.providers.DataService;
import net.twasiplugin.songrequests.SongRequestProvider;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;
import net.twasiplugin.songrequests.database.cache.searchcache.SearchDetailsDTO;
import net.twasiplugin.songrequests.database.cache.searchcache.SearchDetailsRepo;
import net.twasiplugin.songrequests.database.cache.songcache.SongCacheRepo;

import java.util.ArrayList;

public abstract class ProviderSearch extends ArrayList<SongDTO> {

    private final static SongCacheRepo cache = DataService.get().get(SongCacheRepo.class);
    private final static SearchDetailsRepo repo = DataService.get().get(SearchDetailsRepo.class);

    protected abstract SongRequestProvider getProvider();

    protected final ProviderSearch getCachedResult(String query) {
        SearchDetailsDTO dto = repo.get(getFQuery(query), getProvider());
        if (dto == null) return null;
        return dto.results;
    }

    protected final void cache(String query, ProviderSearch results) {
        repo.cache(new SearchDetailsDTO(getProvider(), getFQuery(query), results));
        if (results != null) results.forEach(cache::cache);
    }

    protected String getFQuery(String query) {
        return query.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}
