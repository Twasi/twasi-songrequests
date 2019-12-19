package net.twasiplugin.songrequests.providers.spotify;

import com.wrapper.spotify.SpotifyApi;

import java.net.URI;

import static net.twasiplugin.songrequests.SongrequestPlugin.CONFIG;

public class SpotifyApiBuilder {

    public static SpotifyApi build() {
        return new SpotifyApi.Builder()
                .setClientId(CONFIG.spotify.clientId)
                .setClientSecret(CONFIG.spotify.clientSecret)
                .setRedirectUri(URI.create(CONFIG.spotify.redirectUri))
                .build();
    }

}
