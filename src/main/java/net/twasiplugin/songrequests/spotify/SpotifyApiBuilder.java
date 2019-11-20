package net.twasiplugin.songrequests.spotify;

import com.wrapper.spotify.SpotifyApi;

import java.net.URI;

import static net.twasiplugin.songrequests.SongrequestsPlugin.CONFIG;

public class SpotifyApiBuilder {

    public static SpotifyApi build() {
        return new SpotifyApi.Builder()
                .setClientId(CONFIG.spotify.clientId)
                .setClientSecret(CONFIG.spotify.clientSecret)
                .setRedirectUri(URI.create(CONFIG.spotify.redirectUri))
                .build();
    }

}
