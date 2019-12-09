package net.twasiplugin.songrequests;

import net.twasi.core.database.models.User;
import net.twasi.core.services.providers.DataService;
import net.twasiplugin.songrequests.database.requests.SongRequestDTO;
import net.twasiplugin.songrequests.database.requests.SongRequestRepo;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class SongRequestController {

    private static SongRequestRepo repo = DataService.get().get(SongRequestRepo.class);

    private User user;

    private SongRequestDTO currentSong;
    private List<SongRequestDTO> queue;

    public SongRequestController(User user) {
        this.user = user;
        sync();
    }

    /**
     * Method to synchronize current song and queue from database
     */
    private void sync() {
        this.queue = new ArrayList<>(repo.getRequestsByUser(user, false));
        if (this.queue.size() == 0) currentSong = null;
        else currentSong = this.queue.get(0);
        this.queue.remove(0);
    }

    /**
     * Method to go one song back
     * No matter whether it was played or skipped
     */
    public void back() {
        throw new NotImplementedException(); // TODO implement
    }

    /**
     * Method to skip one song
     *
     * @throws NullPointerException if there is no current song
     */
    public void skip() throws NullPointerException {
        currentSong.setSkipped();
        repo.commit(currentSong);
        sync();
    }

    /**
     * Method to go to the next song.
     *
     * @throws NullPointerException if there is no current song
     */
    public void next() throws NullPointerException {
        currentSong.setPlayed();
        repo.commit(currentSong);
        sync();
    }

    /**
     * @return The controller's owning user
     */
    public User getUser() {
        return user;
    }

    /**
     * @return The current song or null if there is no current song
     */
    public SongRequestDTO getCurrentSong() {
        return currentSong;
    }

    /**
     * @return A list of the next songs
     */
    public List<SongRequestDTO> getQueue() {
        return queue;
    }
}
