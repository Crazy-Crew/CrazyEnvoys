package com.badbones69.crazyenvoys.commands.v2.envoys.types.player;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.v2.envoys.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CommandHelp extends BaseCommand {

    @Command
    @Permission(value = "crazyenvoys.time", def = PermissionDefault.TRUE)
    public void time(final CommandSender sender) {
        getTime(sender, this.crazyManager.isEnvoyActive(), this.crazyManager.getEnvoyRunTimeLeft(), this.crazyManager.getNextEnvoyTime());
    }

    @Command("help")
    @Permission(value = "crazyenvoys.help", def = PermissionDefault.TRUE)
    public void help(final CommandSender sender) {
        Messages.help.sendMessage(sender);
    }
}