package com.badbones69.crazyenvoys.commands.v2.envoys.types.admin;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.commands.v2.envoys.BaseCommand;
import com.badbones69.crazyenvoys.config.ConfigManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CommandReload extends BaseCommand {

    @Command("reload")
    @Permission(value = "crazyenvoys.reload", def = PermissionDefault.OP)
    public void debug(final CommandSender sender) {
        if (this.crazyManager.isEnvoyActive()) {
            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.RELOAD);

            this.plugin.getServer().getPluginManager().callEvent(event);

            this.crazyManager.endEnvoyEvent();
        }

        ConfigManager.refresh(this.plugin.getDataFolder());

        this.crazyHandler.refresh();

        Messages.reloaded.sendMessage(sender);
    }
}