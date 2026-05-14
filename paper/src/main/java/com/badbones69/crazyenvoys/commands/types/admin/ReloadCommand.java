package com.badbones69.crazyenvoys.commands.types.admin;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.commands.types.EnvoyCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class ReloadCommand extends EnvoyCommand {

    @Command("reload")
    @Permission(value = "envoy.reload", def = PermissionDefault.OP)
    @Syntax("/envoys reloadd")
    public void execute(final CommandSender sender) {
        if (this.crazyManager.isEnvoyActive()) {
            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.RELOAD);

            this.pluginManager.callEvent(event);

            this.crazyManager.endEnvoyEvent();
        }

        this.fusion.reload();

        this.fileManager.refresh(false);

        this.crazyManager.reload(false);

        Messages.reloaded.sendMessage(sender);
    }
}