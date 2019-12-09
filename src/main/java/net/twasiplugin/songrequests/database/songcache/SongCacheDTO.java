package net.twasiplugin.songrequests.database.songcache;

import net.twasi.core.database.models.BaseEntity;
import org.mongodb.morphia.annotations.Entity;

@Entity(noClassnameStored = true, value = "songrequests.song-cache")
public class SongCacheDTO extends BaseEntity {


}
