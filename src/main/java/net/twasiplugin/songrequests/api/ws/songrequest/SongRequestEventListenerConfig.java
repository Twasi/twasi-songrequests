package net.twasiplugin.songrequests.api.ws.songrequest;

import net.twasi.core.api.ws.WebsocketClientConfig;

public class SongRequestEventListenerConfig extends WebsocketClientConfig {

    public String channel;

    public boolean newSongs = true;
    public boolean SongChanged = true;

}