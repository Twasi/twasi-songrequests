package net.twasiplugin.songrequests.database.repos;

import net.twasi.core.database.lib.Repository;
import net.twasi.core.database.models.User;
import net.twasiplugin.songrequests.database.models.SongrequestDTO;

import java.util.List;

public class SongrequestRepo extends Repository<SongrequestDTO> {

    public List<SongrequestDTO> getRequestsByUser(User user, int page, boolean played) {
        return query()
                .field("user").equal(user.getId())
                .field("played").equal(played)
                .order("-requested")
                .asList(paginated(page));
    }

    public long countByUser(User user, boolean played) {
        return query()
                .field("user").equal(user.getId())
                .field("played").equal(played)
                .count();
    }

    public long countByUser(User user) {
        return query()
                .field("user").equal(user.getId())
                .count();
    }

}
