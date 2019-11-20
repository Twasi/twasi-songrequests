package net.twasiplugin.songrequests.database.repos;

import net.twasi.core.database.lib.Repository;
import net.twasi.core.database.models.User;
import net.twasiplugin.songrequests.database.models.SpotifyCredentialsDTO;

public class SpotifyCredentialsRepo extends Repository<SpotifyCredentialsDTO> {

    public SpotifyCredentialsDTO getByUser(User user) {
        return query().field("user").equal(user.getId()).get();
    }

    public void removeByUser(User user) {
        SpotifyCredentialsDTO dto = getByUser(user);
        if (dto != null) remove(dto);
    }
}
