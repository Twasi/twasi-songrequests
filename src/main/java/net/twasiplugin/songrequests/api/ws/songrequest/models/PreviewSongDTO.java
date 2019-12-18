package net.twasiplugin.songrequests.api.ws.songrequest.models;

import net.twasiplugin.songrequests.SongRequestPreviewSongDTO;
import net.twasiplugin.songrequests.SongRequestPreviewSongDTO.ProviderPreviewSongDTO;

public class PreviewSongDTO {

    public PreviewSongSongDTO song;
    public PreviewSongStartAtDTO spotifyDuration;
    public PreviewSongStartAtDTO youtubeDuration;

    public PreviewSongDTO(PreviewSongSongDTO song, PreviewSongStartAtDTO spotifyDuration, PreviewSongStartAtDTO youtubeDuration) {
        this.song = song;
        this.spotifyDuration = spotifyDuration;
        this.youtubeDuration = youtubeDuration;
    }

    public static class PreviewSongSongDTO {
        public String name;
        public String artist;
        public String cover;
        public String youtube;
        public String spotify;

        public PreviewSongSongDTO(String name, String artist, String cover, String youtube, String spotify) {
            this.name = name;
            this.artist = artist;
            this.cover = cover;
            this.youtube = youtube;
            this.spotify = spotify;
        }

        public static PreviewSongSongDTO from(SongDTO dto, String youtube) {
            return new PreviewSongSongDTO(
                    dto.name,
                    dto.artists.get(0),
                    dto.covers.get(0),
                    youtube,
                    dto.uri
            );
        }
    }

    public static class PreviewSongStartAtDTO {
        public long startAt;
        public long duration;

        public PreviewSongStartAtDTO(long startAt, long duration) {
            this.startAt = startAt;
            this.duration = duration;
        }
    }

    public static PreviewSongDTO from(SongDTO dto, String youtubeUri, PreviewSongStartAtDTO spotifyDuration, PreviewSongStartAtDTO youtubeDuration) {
        return new PreviewSongDTO(
                PreviewSongSongDTO.from(dto, youtubeUri),
                spotifyDuration,
                youtubeDuration
        );
    }

    public static PreviewSongDTO from(SongDTO dto, SongRequestPreviewSongDTO previewSongDTO) {
        ProviderPreviewSongDTO youtube = previewSongDTO.youtube;
        ProviderPreviewSongDTO spotify = previewSongDTO.spotify;
        return from(
                dto,
                youtube.uri,
                new PreviewSongStartAtDTO(spotify.startAt, spotify.duration),
                new PreviewSongStartAtDTO(youtube.startAt, youtube.duration)
        );
    }

}
