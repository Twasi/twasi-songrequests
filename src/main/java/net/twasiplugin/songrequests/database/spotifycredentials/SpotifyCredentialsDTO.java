package net.twasiplugin.songrequests.database.spotifycredentials;

import net.twasi.core.database.models.BaseEntity;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import java.util.Date;

@Entity(noClassnameStored = true, value = "songrequests.spotify-credentials")
public class SpotifyCredentialsDTO extends BaseEntity {

    private ObjectId user;
    private String accessToken;
    private String refreshToken;
    private Date validUntil;

    public SpotifyCredentialsDTO(ObjectId user, String accessToken, String refreshToken, Date validUntil) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.validUntil = validUntil;
    }

    public SpotifyCredentialsDTO() {
    }

    public ObjectId getUser() {
        return user;
    }

    public void setUser(ObjectId user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }
}
