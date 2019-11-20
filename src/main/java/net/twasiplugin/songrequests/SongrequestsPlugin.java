package net.twasiplugin.songrequests;

import net.twasi.core.api.ws.TwasiWebsocketEndpoint;
import net.twasi.core.plugin.TwasiPlugin;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasiplugin.songrequests.api.ws.songrequest.SongRequestListener;
import net.twasiplugin.songrequests.api.ws.spotify.SpotifyCredentials;

import java.util.Arrays;
import java.util.List;

public class SongrequestsPlugin extends TwasiPlugin<SongrequestsConfig> {

    public static SongrequestsConfig CONFIG;

    @Override
    public void onActivate() {
        CONFIG = getConfiguration();
    }

    public Class<? extends TwasiUserPlugin> getUserPluginClass() {
        return null;
    }

    @Override
    public List<TwasiWebsocketEndpoint<?>> getWebsocketEndpoints() {
        return Arrays.asList(
                new SpotifyCredentials(),
                new SongRequestListener()
        );
    }
}
