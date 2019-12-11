package net.twasiplugin.songrequests.commands;

import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.customcommands.TwasiCustomCommandEvent;
import net.twasi.core.plugin.api.customcommands.TwasiPluginCommand;
import net.twasi.core.services.providers.DataService;
import net.twasi.core.translations.renderer.TranslationRenderer;
import net.twasiplugin.songrequests.database.requests.SongRequestDTO;
import net.twasiplugin.songrequests.database.requests.SongRequestRepo;

import java.time.Duration;
import java.util.List;

import static net.twasiplugin.songrequests.SongrequestsPlugin.EVENTS;

public class SkipCommand extends TwasiPluginCommand {

    public SkipCommand(TwasiUserPlugin twasiUserPlugin) {
        super(twasiUserPlugin);
    }

    @Override
    public String getCommandName() {
        return "skip";
    }

    @Override
    public String requirePermissionKey() {
        return "songrequests.skip";
    }

    @Override
    public Duration getCooldown() {
        return Duration.ZERO;
    }

    @Override
    protected boolean execute(TwasiCustomCommandEvent event) {
        TranslationRenderer renderer = event.getRenderer(/*"skip"*/);
        List<SongRequestDTO> queue = EVENTS.getQueue(this.twasiUserPlugin.getTwasiInterface().getStreamer().getUser());
        if (queue.size() > 0) {
            SongRequestDTO update = queue.get(0);
            renderer.bindObject("song", update.getSong());
            update.setSkipped();
            DataService.get().get(SongRequestRepo.class).commit(update);
            event.reply(renderer.render("success"));
        } else {
            event.reply(renderer.render("no-song"));
        }
        return true;
    }
}
