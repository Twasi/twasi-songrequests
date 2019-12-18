package net.twasiplugin.songrequests;

public class SongRequestPreviewSongDTO {

    public ProviderPreviewSongDTO spotify;
    public ProviderPreviewSongDTO youtube;

    public static class ProviderPreviewSongDTO {
        public long startAt;
        public long duration;
        public String uri;
    }

}
