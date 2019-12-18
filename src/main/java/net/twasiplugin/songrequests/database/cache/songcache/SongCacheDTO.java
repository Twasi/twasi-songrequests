package net.twasiplugin.songrequests.database.cache.songcache;

import net.twasi.core.database.models.BaseEntity;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;
import org.mongodb.morphia.annotations.Entity;

import java.util.Calendar;
import java.util.Date;

@Entity(noClassnameStored = true, value = "songrequests.cache.song")
public class SongCacheDTO extends BaseEntity {

    private SongDTO song;
    private Date added = Calendar.getInstance().getTime();

    public SongCacheDTO(SongDTO song) {
        this.song = song;
    }

    public SongCacheDTO() {
    }

    public SongDTO getSong() {
        return song;
    }

    public void setSong(SongDTO song) {
        this.song = song;
    }

    public Date getAdded() {
        return added;
    }
}
