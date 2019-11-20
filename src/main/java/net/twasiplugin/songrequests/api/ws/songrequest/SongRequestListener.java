package net.twasiplugin.songrequests.api.ws.songrequest;

import com.google.gson.JsonElement;
import net.twasi.core.api.ws.TwasiWebsocketListenerEndpoint;
import net.twasi.core.api.ws.models.TwasiWebsocketMessage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SongRequestListener extends TwasiWebsocketListenerEndpoint<SongRequestListenerConfig> {

    public String getTopic() {
        return "songrequests";
    }

    @Override
    public JsonElement handle(TwasiWebsocketMessage msg) throws Exception {
        throw new NotImplementedException();
    }
}
