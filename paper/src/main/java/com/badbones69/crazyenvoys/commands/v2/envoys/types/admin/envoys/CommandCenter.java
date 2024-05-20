package com.badbones69.crazyenvoys.commands.v2.envoys.types.admin.envoys;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.v2.envoys.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandCenter extends BaseCommand {

    @Command("center")
    @Permission(value = "crazyenvoys.center", def = PermissionDefault.OP)
    public void center(final Player player) {
        this.crazyManager.setCenter(player.getLocation());

        Messages.new_center.sendMessage(player);
    }
}