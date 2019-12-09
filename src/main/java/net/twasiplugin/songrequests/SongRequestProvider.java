package net.twasiplugin.songrequests;

import java.util.Arrays;

public enum SongRequestProvider {

    YOUTUBE(2, "YouTube"), SPOTIFY(1, "Spotify");

    private int id;
    private String prettyString;

    SongRequestProvider(int frontendId, String prettyString) {
        this.id = frontendId;
        this.prettyString = prettyString;
    }

    public int getFrontendId() {
        return id;
    }

    public static SongRequestProvider getByFrontendId(int id) {
        return Arrays.stream(SongRequestProvider.values()).filter(e -> e.getFrontendId() == id).findFirst().orElse(null);
    }

    public String toPrettyString() {
        return prettyString;
    }
}
