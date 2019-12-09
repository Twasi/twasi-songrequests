package net.twasiplugin.songrequests.database.usersettings;

import net.twasi.core.database.lib.Repository;
import net.twasi.core.database.models.User;

public class SongRequestSettingsRepo extends Repository<SongRequestSettingsDTO> {

    public SongRequestSettingsDTO getByUser(User user) {
        SongRequestSettingsDTO dto = query().field("user").equal(user.getId()).get();
        if (dto == null) {
            dto = new SongRequestSettingsDTO();
            dto.setUser(user.getId());
            add(dto);
            return getByUser(user);
        }
        return dto;
    }

}
