package com.badbones69.crazyenvoys.commands.v2.envoys.types.admin.envoys;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyStartEvent;
import com.badbones69.crazyenvoys.commands.v2.envoys.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandStop extends BaseCommand {

    @Command(value = "stop", alias = "end")
    @Permission(value = "crazyenvoys.stop", def = PermissionDefault.OP)
    public void stop(final CommandSender sender) {
        if (!this.crazyManager.isEnvoyActive()) {
            Messages.not_started.sendMessage(sender);

            return;
        }

        EnvoyEndEvent event = sender instanceof Player player ? new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.FORCED_END_PLAYER, player) : new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.FORCED_END_CONSOLE);

        this.plugin.getServer().getPluginManager().callEvent(event);

        this.crazyManager.endEnvoyEvent();

        Messages.ended.broadcastMessage(false);
        Messages.force_end.sendMessage(sender);
    }
}