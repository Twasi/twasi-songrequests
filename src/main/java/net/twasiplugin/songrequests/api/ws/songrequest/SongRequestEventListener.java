package net.twasiplugin.songrequests.api.ws.songrequest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.twasi.core.api.ws.TwasiWebsocketEvent;
import net.twasi.core.api.ws.TwasiWebsocketListenerEndpoint;
import net.twasi.core.api.ws.models.TwasiWebsocketAnswer;
import net.twasi.core.api.ws.models.TwasiWebsocketMessage;
import net.twasi.core.database.models.TwitchAccount;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;

public class SongRequestEventListener extends TwasiWebsocketListenerEndpoint<SongRequestEventListenerConfig> {

    public String getTopic() {
        return "events";
    }

    @Override
    public JsonElement handle(TwasiWebsocketMessage msg) throws Exception {
        TwitchAccount channel = msg.getClient().getAuthentication().getUser().getTwitchAccount();
        JsonObject action = msg.getAction().getAsJsonObject();
        switch (action.get("type").getAsString().toLowerCase()) {
            case "newSong":
                newSong(SongDTO.from(action.get("song").getAsJsonObject()), channel);
        }
        return TwasiWebsocketAnswer.warn("No valid request type delivered.");
    }

    public void newSong(SongDTO song, TwitchAccount channel) {
        publish(channel, new TwasiWebsocketEvent<>(song).toSendable());
    }

    private void publish(TwitchAccount channel, JsonElement element) {
        publishFilteredByConfig(config -> {
            String check = config.channel;
            return check.equalsIgnoreCase(channel.getTwitchId()) || check.equalsIgnoreCase(channel.getUserName());
        }, element);
    }
}
