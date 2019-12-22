package net.twasiplugin.songrequests.commands;

import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.customcommands.TwasiPluginCommand;

public class WrongSongCommand extends TwasiPluginCommand {

    public WrongSongCommand(TwasiUserPlugin twasiUserPlugin) {
        super(twasiUserPlugin);
    }

    @Override
    public String getCommandName() {
        return "wrongsong";
    }
}
