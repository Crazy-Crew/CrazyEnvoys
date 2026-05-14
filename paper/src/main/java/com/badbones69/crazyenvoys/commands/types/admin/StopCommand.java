package com.badbones69.crazyenvoys.commands.types.admin;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.commands.types.EnvoyCommand;
import com.badbones69.crazyenvoys.config.types.ConfigKeys;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class StopCommand extends EnvoyCommand {

    @Command(value = "stop", alias = {"end"})
    @Permission(value = "envoy.stop", def = PermissionDefault.OP)
    @Syntax("/envoys stop")
    public void stop(final CommandSender sender) {
        if (!this.crazyManager.isEnvoyActive()) {
            Messages.not_started.sendMessage(sender);

            return;
        }

        EnvoyEndEvent event;

        if (sender instanceof Player player) {
            event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.FORCED_END_PLAYER, player);
        } else {
            event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.FORCED_END_CONSOLE);
        }

        this.pluginManager.callEvent(event);
        this.crazyManager.endEnvoyEvent();

        Messages.ended.broadcastMessage(this.config.getProperty(ConfigKeys.envoys_ignore_behaviour_ended));

        Messages.force_end.sendMessage(sender);
    }
}