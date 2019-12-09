package net.twasiplugin.songrequests.database.models;

import net.twasi.core.database.models.BaseEntity;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity("songrequest-reports")
public class ReportDTO extends BaseEntity {
    public ObjectId user;
    public String message;
    public List<SpotifyReport> spotifyReports;
    public Date timestamp = Calendar.getInstance().getTime();

    public static class SpotifyReport {
        public String type;
        public String message;
    }

}
