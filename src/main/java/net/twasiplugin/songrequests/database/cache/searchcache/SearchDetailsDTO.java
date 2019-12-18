package net.twasiplugin.songrequests.database.cache.searchcache;

import net.twasi.core.database.models.BaseEntity;
import net.twasiplugin.songrequests.SongRequestProvider;
import net.twasiplugin.songrequests.providers.ProviderSearch;
import org.mongodb.morphia.annotations.Entity;

import java.util.Calendar;
import java.util.Date;

@Entity(noClassnameStored = true, value = "songrequests.cache.search")
public class SearchDetailsDTO extends BaseEntity {
    public SongRequestProvider provider;
    public String query;
    public ProviderSearch results;
    public Date lastUpdate = Calendar.getInstance().getTime();

    public SearchDetailsDTO(SongRequestProvider provider, String query, ProviderSearch results) {
        this.provider = provider;
        this.query = query;
        this.results = results;
    }

    public SearchDetailsDTO() {
    }
}