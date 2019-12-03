package net.twasiplugin.songrequests.database.repos;

import net.twasi.core.database.lib.Repository;
import net.twasi.core.database.models.User;
import net.twasiplugin.songrequests.database.models.SongrequestDTO;
import org.mongodb.morphia.query.Query;

import java.util.List;

public class SongrequestRepo extends Repository<SongrequestDTO> {

    /**
     * Function to get songrequest entities from database
     *
     * @param user   The user to get songrequest information for
     * @param played Whether to get already played or queued songs
     * @return A list of songrequests
     */
    public List<SongrequestDTO> getRequestsByUser(User user, boolean played) {
        Query<SongrequestDTO> q = query()
                .field("user").equal(user.getId());

        if (played) q
                .or(
                        q.criteria("played").notEqual(null),
                        q.criteria("skipped").notEqual(null)
                );
        else q = q
                .field("played").equal(null)
                .field("skipped").equal(null);

        return q
                .order("-requested")
                .asList();
    }

    /**
     * Method to query amount of open or closed songrequest entities by user
     *
     * @param user   The user to query for
     * @param played Whether to count already played or queued songs
     * @return The amount of entities
     */
    public long countByUser(User user, boolean played) {
        return query()
                .field("user").equal(user.getId())
                .field("played").equal(played)
                .count();
    }

    /**
     * Method to query total amount of songrequest entities by user
     *
     * @param user The user to query for
     * @return The amount of entities
     */
    public long countByUser(User user) {
        return query()
                .field("user").equal(user.getId())
                .count();
    }

}
