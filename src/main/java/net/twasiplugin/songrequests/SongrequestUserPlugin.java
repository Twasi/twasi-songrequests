package net.twasiplugin.songrequests;

import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasiplugin.songrequests.commands.SkipCommand;
import net.twasiplugin.songrequests.commands.SongRequestCommand;

public class SongrequestUserPlugin extends TwasiUserPlugin {

    public SongrequestUserPlugin() {
        registerCommand(SongRequestCommand.class);
        registerCommand(SkipCommand.class);
    }
}
