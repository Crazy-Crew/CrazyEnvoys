package com.badbones69.crazyenvoys.commands.types.player;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.types.EnvoyCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;
import java.util.Map;

public class HelpCommand extends EnvoyCommand {

    @Command
    @Permission(value = "envoy.time", def = PermissionDefault.TRUE)
    @Syntax("/envoys")
    public void execute(final CommandSender sender) {
        final Map<String, String> placeholders = new HashMap<>();

        if (this.crazyManager.isEnvoyActive()) {
            placeholders.put("{time}", this.crazyManager.getEnvoyRunTimeLeft());

            Messages.time_left.sendMessage(sender, placeholders);
        } else {
            placeholders.put("{time}", this.crazyManager.getNextEnvoyTime());

            Messages.time_till_event.sendMessage(sender, placeholders);
        }
    }

    @Command("time")
    @Permission(value = "envoy.time", def = PermissionDefault.TRUE)
    @Syntax("/envoys time")
    public void time(final CommandSender sender) {
        execute(sender);
    }

    @Command("help")
    @Permission(value = "envoy.help", def = PermissionDefault.TRUE)
    @Syntax("/envoys help")
    public void help(final CommandSender sender) {
        Messages.help.sendMessage(sender);
    }
}