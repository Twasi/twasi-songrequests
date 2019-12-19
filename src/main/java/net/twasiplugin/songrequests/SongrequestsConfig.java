package net.twasiplugin.songrequests;

import net.twasiplugin.songrequests.SongRequestPreviewSongDTO.ProviderPreviewSongDTO;

import java.util.ArrayList;
import java.util.List;

public class SongrequestsConfig {

    private static List<SongRequestPreviewSongDTO> defaultDtos;

    static {
        defaultDtos = new ArrayList<>();
        ProviderPreviewSongDTO dto = new ProviderPreviewSongDTO();
        dto.duration = 54321;
        dto.startAt = 12345;
        dto.uri = "hi";
        SongRequestPreviewSongDTO dto1 = new SongRequestPreviewSongDTO();
        dto1.spotify = dto;
        dto1.youtube = dto;
        defaultDtos.add(dto1);
    }

    public boolean enableSpotify = true;
    public boolean enableYoutube = true;
    public SpotifyCredentials spotify = new SpotifyCredentials();
    public String youTubeApiKey = "API_KEY";
    public ArrayList<SongRequestPreviewSongDTO> previewSongs = new ArrayList<>(defaultDtos);

    public static class SpotifyCredentials {
        public String clientId = "CLIENT_ID";
        public String clientSecret = "CLIENT_SECRET";
        public String redirectUri = "REDIRECT_URI";
        public String scope = "SCOPES";
    }
}
