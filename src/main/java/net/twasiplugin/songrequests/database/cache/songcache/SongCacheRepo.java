package net.twasiplugin.songrequests.database.cache.songcache;

import net.twasi.core.database.lib.Repository;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;

public class SongCacheRepo extends Repository<SongCacheDTO> {

    public SongDTO getByUri(String uri) {
        SongCacheDTO songCacheDTO = query().field("song.uri").equal(uri).get();
        if (songCacheDTO != null) return songCacheDTO.getSong();
        return null;
    }

    public void cache(SongDTO song) {
        song.requester = null;
        song.playInformation = null;
        song.userId = null;
        SongCacheDTO dto = query().field("song.uri").equal(song.uri).get();
        if (dto != null) {
            dto.setSong(song); // Update details
            commit(dto);
        } else add(new SongCacheDTO(song));
    }

}
