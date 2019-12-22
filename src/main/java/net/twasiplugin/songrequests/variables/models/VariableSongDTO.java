package net.twasiplugin.songrequests.variables.models;

import net.twasi.core.plugin.api.variables.objectvariables.TwasiObjectVariable;
import net.twasiplugin.songrequests.SongRequestProvider;
import net.twasiplugin.songrequests.api.ws.songrequest.models.RequesterDTO;
import net.twasiplugin.songrequests.api.ws.songrequest.models.SongDTO;

import java.util.ArrayList;
import java.util.List;

@TwasiObjectVariable.Resolvable
public class VariableSongDTO {

    public final String name;
    public final String artist;
    public final RequesterDTO requester;
    public final String url;
    public final String uri;
    public final String provider;
    public final int seconds;
    public final String duration;

    public VariableSongDTO(String name, String artist, RequesterDTO requester, String url, String uri, String provider, int seconds, String duration) {
        this.name = name;
        this.artist = artist;
        this.requester = requester;
        this.url = url;
        this.uri = uri;
        this.provider = provider;
        this.seconds = seconds;
        this.duration = duration;
    }

    public static VariableSongDTO from(SongDTO dto) {
        return new VariableSongDTO(
                dto.name,
                getArtistString(dto.artists),
                dto.requester,
                dto.url,
                dto.uri,
                SongRequestProvider.getByFrontendId(dto.provider).toPrettyString(),
                dto.duration / 1000,
                formatDuration(dto.duration)
        );
    }

    private static String formatDuration(int duration) {
        duration /= 1000;
        int min = duration / 60;
        int sec = duration % 60;
        return (min < 10 ? "0" + min : "" + min) + (sec < 10 ? "0" + sec : sec);
    }

    private static String getArtistString(List<String> artists) {
        List<String> features = new ArrayList<>(artists.subList(1, artists.size()));
        return artists.get(0) + (features.size() > 0 ? " feat. " + String.join(" & ", features) : "");
    }
}
