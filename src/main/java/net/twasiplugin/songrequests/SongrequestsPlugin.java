package net.twasiplugin.songrequests;

import net.twasi.core.api.ws.api.TwasiWebsocketEndpoint;
import net.twasi.core.plugin.TwasiPlugin;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.ServletService;
import net.twasiplugin.songrequests.api.ws.songrequest.SongRequestEventListener;
import net.twasiplugin.songrequests.api.ws.spotify.SpotifyCredentials;
import net.twasiplugin.songrequests.servlets.SpotifyAuthServlet;

import java.util.Arrays;
import java.util.List;

public class SongrequestsPlugin extends TwasiPlugin<SongrequestsConfig> {

    public static SongrequestsConfig CONFIG;
    public static SongRequestEventListener EVENTS;

    @Override
    public void onActivate() {
        CONFIG = getConfiguration();
        ServiceRegistry.get(ServletService.class).addServlet(SpotifyAuthServlet.class, "spotify-callback");
    }

    public Class<? extends TwasiUserPlugin> getUserPluginClass() {
        return SongrequestUserPlugin.class;
    }

    @Override
    public List<TwasiWebsocketEndpoint<?>> getWebsocketEndpoints() {
        return Arrays.asList(
                new SpotifyCredentials(),
                EVENTS = new SongRequestEventListener()
        );
    }
}
