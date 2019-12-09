package net.twasiplugin.songrequests.database.spotifycredentials;

import net.twasi.core.database.lib.Repository;
import net.twasi.core.database.models.User;

public class SpotifyCredentialsRepo extends Repository<SpotifyCredentialsDTO> {

    public SpotifyCredentialsDTO getByUser(User user) {
        return query().field("user").equal(user.getId()).get();
    }

    public void removeByUser(User user) {
        SpotifyCredentialsDTO dto = getByUser(user);
        if (dto != null) remove(dto);
    }

    public boolean hasCredentials(User user) {
        return query().field("user").equal(user.getId()).count() > 0;
    }
}
