package net.twasiplugin.songrequests.database.usersettings;

import com.google.gson.JsonObject;
import net.twasi.core.database.models.BaseEntity;
import net.twasi.core.database.models.permissions.PermissionGroups;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import java.util.HashMap;
import java.util.Map;

@Entity(noClassnameStored = true, value = "songrequests.settings")
public class SongRequestSettingsDTO extends BaseEntity {

    private static final Map<PermissionGroups, Integer> DEFAULT_REQUEST_AMOUNTS = new HashMap<>();

    static {
        DEFAULT_REQUEST_AMOUNTS.put(PermissionGroups.VIEWER, 3);
        DEFAULT_REQUEST_AMOUNTS.put(PermissionGroups.BROADCASTER, -1);
        DEFAULT_REQUEST_AMOUNTS.put(PermissionGroups.MODERATOR, 10);
        DEFAULT_REQUEST_AMOUNTS.put(PermissionGroups.SUBSCRIBERS, 5);
    }

    private ObjectId user;
    private Map<PermissionGroups, Integer> maxRequests = DEFAULT_REQUEST_AMOUNTS;
    private double volumeBalance = 0.68;
    private double volume = 0.6;
    private int maxDuration = 600000;

    public SongRequestSettingsDTO(ObjectId user, Map<PermissionGroups, Integer> maxRequests, double volumeBalance, double volume, int maxDuration) {
        this.user = user;
        this.maxRequests = maxRequests;
        this.volumeBalance = volumeBalance;
        this.volume = volume;
        this.maxDuration = maxDuration;
    }

    public SongRequestSettingsDTO() {
    }

    public static Map<PermissionGroups, Integer> getDefaultRequestAmounts() {
        return DEFAULT_REQUEST_AMOUNTS;
    }

    public ObjectId getUser() {
        return user;
    }

    public void setUser(ObjectId user) {
        this.user = user;
    }

    public Map<PermissionGroups, Integer> getMaxRequests() {
        return maxRequests;
    }

    public void setMaxRequests(Map<PermissionGroups, Integer> maxRequests) {
        this.maxRequests = maxRequests;
    }

    public double getVolumeBalance() {
        return volumeBalance;
    }

    public void setVolumeBalance(double volumeBalance) {
        this.volumeBalance = volumeBalance;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public void apply(JsonObject settings) {
        if (settings.has("maxRequests")) {
            settings.get("maxRequests").getAsJsonObject().entrySet().forEach((set) -> {
                String group = set.getKey();
                int amount = set.getValue().getAsInt();
                try {
                    PermissionGroups g = PermissionGroups.valueOf(group.toUpperCase());
                    this.maxRequests.replace(g, amount);
                } catch (Exception ignored) {
                }
            });
        }
        if (settings.has("volume")) {
            double volume = settings.get("volume").getAsDouble();
            if (volume > 1) volume = 1;
            if (volume < 0) volume = 0;
            this.volume = volume;
        }
        if (settings.has("volumeBalance")) {
            double volumeBalance = settings.get("volumeBalance").getAsDouble();
            if (volumeBalance > 1) volumeBalance = 1;
            if (volumeBalance < 0) volumeBalance = 0;
            this.volumeBalance = volumeBalance;
        }
        if (settings.has("maxDuration")) {
            int maxDuration = settings.get("maxDuration").getAsInt();
            if (maxDuration < 0) maxDuration = 0;
            this.maxDuration = maxDuration;
        }
    }
}
