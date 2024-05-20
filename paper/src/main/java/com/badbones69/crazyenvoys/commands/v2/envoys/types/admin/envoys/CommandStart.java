package com.badbones69.crazyenvoys.commands.v2.envoys.types.admin.envoys;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyStartEvent;
import com.badbones69.crazyenvoys.commands.v2.envoys.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandStart extends BaseCommand {

    @Command(value = "start", alias = "begin")
    @Permission(value = "crazyenvoys.start", def = PermissionDefault.OP)
    public void start(final CommandSender sender) {
        if (this.crazyManager.isEnvoyActive()) {
            Messages.already_started.sendMessage(sender);

            return;
        }

        EnvoyStartEvent event = sender instanceof Player player ? new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FORCED_START_PLAYER, player) : new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FORCED_START_CONSOLE);

        this.plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled() && this.crazyManager.startEnvoyEvent()) Messages.force_start.sendMessage(sender);
    }
}