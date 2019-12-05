package net.twasiplugin.songrequests;

import java.util.Arrays;

public enum SongRequestProvider {

    YOUTUBE(2), SPOTIFY(1);

    private int id;

    SongRequestProvider(int frontendId) {
        this.id = frontendId;
    }

    public int getFrontendId() {
        return id;
    }

    public static SongRequestProvider getByFrontendId(int id) {
        return Arrays.stream(SongRequestProvider.values()).filter(e -> e.getFrontendId() == id).findFirst().orElse(null);
    }
}
