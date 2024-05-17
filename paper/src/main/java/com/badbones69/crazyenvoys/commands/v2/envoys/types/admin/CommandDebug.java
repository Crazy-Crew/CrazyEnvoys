package com.badbones69.crazyenvoys.commands.v2.envoys.types.admin;

import com.badbones69.crazyenvoys.api.objects.misc.v2.Reward;
import com.badbones69.crazyenvoys.commands.v2.envoys.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.List;

public class CommandDebug extends BaseCommand {

    @Command("debug")
    @Permission(value = "crazyenvoys.debug", def = PermissionDefault.OP)
    public void debug(final Player player, @Suggestion("rewards") final String key) {
        List<Reward> reward = this.crazyHandler.getRewards(key);

        if (reward.isEmpty()) return;

        reward.forEach(type -> type.getItemStacks().forEach(builder -> player.getInventory().setItem(player.getInventory().firstEmpty(), builder.getStack())));
    }
}