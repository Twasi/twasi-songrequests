package net.twasiplugin.songrequests;

public class SongrequestsConfig {

    public boolean enableSpotify = true;
    public boolean enableYoutube = true;
    public SpotifyCredentials spotify = new SpotifyCredentials();
    public String youTubeApiKey = "API_KEY";

    public static class SpotifyCredentials {
        public String clientId = "CLIENT_ID";
        public String clientSecret = "CLIENT_SECRET";
        public String redirectUri = "REDIRECT_URI";
        public String scope = "SCOPES";
    }
}
