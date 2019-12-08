package net.twasiplugin.songrequests.database.models;

import net.twasi.core.database.models.BaseEntity;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import java.util.Calendar;
import java.util.Date;

@Entity("twasi.songrequests.requests")
public class SongrequestDTO extends BaseEntity {

    private ObjectId user;
    private SongDTO song;
    private Date skipped = null;
    private Date played = null;
    private Date requested = Calendar.getInstance().getTime();

    public SongrequestDTO(ObjectId user, SongDTO song) {
        this.user = user;
        this.song = song;
    }

    public SongrequestDTO() {
    }

    public ObjectId getUser() {
        return user;
    }

    public void setUser(ObjectId user) {
        this.user = user;
    }

    public SongDTO getSong() {
        return song;
    }

    public void setSong(SongDTO song) {
        this.song = song;
    }

    public boolean isSkipped() {
        return skipped != null;
    }

    public Date getSkipped() {
        return skipped;
    }

    public void setSkipped() {
        this.skipped = Calendar.getInstance().getTime();
    }

    public void resetSkipped() {
        this.skipped = null;
    }

    public boolean isPlayed() {
        return played != null;
    }

    public Date getPlayed() {
        return played;
    }

    public void setPlayed() {
        this.played = Calendar.getInstance().getTime();
    }

    public void resetPlayed() {
        this.played = null;
    }

    public Date getRequested() {
        return requested;
    }

    public void setRequested(Date requested) {
        this.requested = requested;
    }
}
