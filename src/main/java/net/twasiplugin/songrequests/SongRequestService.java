package net.twasiplugin.songrequests;

import net.twasi.core.database.models.User;
import net.twasi.core.services.IService;

import java.util.ArrayList;
import java.util.List;

public class SongRequestService implements IService {

    private List<SongRequestController> controllers = new ArrayList<>();

    public SongRequestController getControllerForUser(User user) {
        SongRequestController ctrl = controllers
                .stream().filter(c -> c.getUser().getId().equals(user.getId()))
                .findFirst().orElse(null);

        if (ctrl == null) controllers.add(ctrl = new SongRequestController(user));
        return ctrl;
    }

}
