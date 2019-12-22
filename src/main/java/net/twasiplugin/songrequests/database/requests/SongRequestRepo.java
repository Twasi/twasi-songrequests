package net.twasiplugin.songrequests.database.requests;

import net.twasi.core.database.lib.Repository;
import net.twasi.core.database.models.User;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;
import net.twasiplugin.songrequests.database.requests.exceptions.DuplicateSongException;
import net.twasiplugin.songrequests.database.requests.exceptions.TooManyRequestsException;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;

import java.util.Calendar;
import java.util.List;

public class SongRequestRepo extends Repository<SongRequestDTO> {

    /**
     * Function to get songrequest entities from database
     *
     * @param user   The user to get songrequest information for
     * @param played Whether to get already played or queued songs
     * @return A list of songrequests
     */
    public List<SongRequestDTO> getRequestsByUser(User user, boolean played) {
        return buildQuery(user, played).asList();
    }

    /**
     * Function to get songrequest entities from database
     *
     * @param user   The user to get songrequest information for
     * @param played Whether to get already played or queued songs
     * @param amount The amount of songs to query
     * @return A list of songrequests
     */
    public List<SongRequestDTO> getRequestsByUser(User user, boolean played, int amount) {
        return buildQuery(user, played).asList(new FindOptions().limit(amount));
    }

    /**
     * Function to get songrequest entities from database
     *
     * @param user   The user to get songrequest information for
     * @param played Whether to get already played or queued songs
     * @param amount The amount of songs to query
     * @param offset A query offset
     * @return A list of songrequests
     */
    public List<SongRequestDTO> getRequestsByUser(User user, boolean played, int amount, int offset) {
        return buildQuery(user, played).asList(new FindOptions().limit(amount).skip(offset));
    }

    private Query<SongRequestDTO> buildQuery(User user, boolean played) {
        Query<SongRequestDTO> q = query()
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
                .order(played ? "-requested" : "requested");
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

    /**
     * @param songId The id of the entity that should be deleted
     */
    public void removeById(String songId, User user) {
        this.store.findAndDelete(query().field("id").equal(songId).field("user").equal(user.getId()));
    }

    /**
     * @param song The song to add
     * @param user The user to add the song for
     * @param max  How many songs the user can add at maximum. -1 = infinite
     * @throws TooManyRequestsException if the user cannot add any more requests
     * @throws DuplicateSongException   if the song already was requested
     */
    public void checkAndAdd(SongDTO song, User user, long max) throws TooManyRequestsException, DuplicateSongException {
        if (max > 0)
            if (buildQuery(user, false).field("song.requester.twitchId").equal(song.requester.twitchId).count() > max)
                throw new TooManyRequestsException();
        if (buildQuery(user, false).field("song.uri").equal(song.uri).count() > 0)
            throw new DuplicateSongException();
        song.userId = user.getId();
        song.playInformation = new SongDTO.PlayInformation();
        song.timeStamp = Calendar.getInstance().getTimeInMillis();
        add(new SongRequestDTO(user.getId(), song));
    }
}
