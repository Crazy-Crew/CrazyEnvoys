package com.badbones69.crazyenvoys.commands.types.admin;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.types.EnvoyCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CenterCommand extends EnvoyCommand {

    @Command("center")
    @Permission(value = "envoy.center", def = PermissionDefault.OP)
    @Syntax("/envoys center")
    public void execute(final Player player) {
        this.crazyManager.setCenter(player.getLocation());

        Messages.new_center.sendMessage(player);
    }
}