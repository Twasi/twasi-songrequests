package net.twasiplugin.songrequests.variables;

import net.twasi.core.interfaces.api.TwasiInterface;
import net.twasi.core.models.Message.TwasiMessage;
import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.variables.objectvariables.TwasiObjectVariable;
import net.twasi.core.services.providers.DataService;
import net.twasiplugin.songrequests.database.requests.SongRequestDTO;
import net.twasiplugin.songrequests.database.requests.SongRequestRepo;
import net.twasiplugin.songrequests.variables.models.VariableSongDTO;

import java.util.Arrays;
import java.util.List;

@TwasiObjectVariable.Properties(key = "name")
public class SongRequestVariable extends TwasiObjectVariable<VariableSongDTO> {

    private static SongRequestRepo repo = DataService.get().get(SongRequestRepo.class);

    public SongRequestVariable(TwasiUserPlugin owner) {
        super(owner);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("songrequests", "sr");
    }

    @Override
    public VariableSongDTO getObject(String s, TwasiInterface twasiInterface, List<String> list, TwasiMessage twasiMessage) {
        int offset = list.size() > 0 ? Integer.parseInt(list.get(0)) : 0;
        List<SongRequestDTO> requestsByUser = repo.getRequestsByUser(twasiInterface.getStreamer().getUser(), offset > 0, 1, offset > 0 ? offset : -1 * offset);
        if (requestsByUser.size() > 0)
            return VariableSongDTO.from(requestsByUser.get(0).getSong());
        else return null;
    }

    @Override
    public String handleNullObject() {
        return "Kein Song"; // TODO translate
    }
}
