package net.twasiplugin.songrequests.database.cache.searchcache;

import net.twasi.core.database.lib.Repository;
import net.twasiplugin.songrequests.SongRequestProvider;

public class SearchDetailsRepo extends Repository<SearchDetailsDTO> {

    public void cache(SearchDetailsDTO dto) {
        store.delete(query()
                .field("query").equal(dto.query)
                .field("provider").equal(dto.provider)
        );
        add(dto);
    }

    public SearchDetailsDTO get(String query, SongRequestProvider provider) {
        return query()
                .field("query").equal(query)
                .field("provider").equal(provider)
                .get();
    }

}
