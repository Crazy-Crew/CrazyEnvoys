package com.badbones69.crazyenvoys.commands.types.admin;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.types.EnvoyCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.api.objects.EnvoyLocation;
import java.util.UUID;

public class CenterCommand extends EnvoyCommand {

    @Command("center")
    @Permission(value = "envoy.center", def = PermissionDefault.OP)
    @Syntax("/envoys center")
    public void execute(final Player player) {
        final UUID uuid = player.getWorld().getUID();

        this.envoyRegistry.getWorld(uuid).ifPresent(world -> {
            final Location location = player.getLocation();

            this.holder.setCenter(world, new EnvoyLocation(
                    uuid,
                    (int) location.getX(),
                    (int) location.getY(),
                    (int) location.getZ()
            ));

            Messages.new_center.sendMessage(player);
        });
    }
}