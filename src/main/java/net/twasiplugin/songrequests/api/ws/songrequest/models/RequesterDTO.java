package net.twasiplugin.songrequests.api.ws.songrequest.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.twasi.core.database.models.TwitchAccount;

public class RequesterDTO {

    public String displayName;
    public String userName;
    public String twitchId;
    public String avatar;

    public RequesterDTO(String displayName, String userName, String twitchId, String avatar) {
        this.displayName = displayName;
        this.userName = userName;
        this.twitchId = twitchId;
        this.avatar = avatar;
    }

    public RequesterDTO() {
    }

    public static RequesterDTO from(TwitchAccount acc) {
        return new RequesterDTO(
                acc.getDisplayName(),
                acc.getUserName(),
                acc.getTwitchId(),
                acc.getAvatar()
        );
    }

    public static RequesterDTO from(JsonElement element) {
        return new Gson().fromJson(element, RequesterDTO.class);
    }

}
