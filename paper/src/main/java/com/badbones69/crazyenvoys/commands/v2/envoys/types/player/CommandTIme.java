package com.badbones69.crazyenvoys.commands.v2.envoys.types.player;

import com.badbones69.crazyenvoys.commands.v2.envoys.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CommandTIme extends BaseCommand {

    @Command("time")
    @Permission(value = "crazyenvoys.time", def = PermissionDefault.TRUE)
    public void time(final CommandSender sender) {
        getTime(sender, this.crazyManager.isEnvoyActive(), this.crazyManager.getEnvoyRunTimeLeft(), this.crazyManager.getNextEnvoyTime());
    }
}