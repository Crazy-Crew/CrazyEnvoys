package com.badbones69.crazyenvoys.commands.types.admin;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.types.EnvoyCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class ClearCommand extends EnvoyCommand {

    @Command(value = "clear")
    @Permission(value = "envoy.clear", def = PermissionDefault.OP)
    @Syntax("/envoys clear")
    public void clear(final Player player) {
        this.userRegistry.getUser(player.getUniqueId()).ifPresent(user -> {
            if (user.isEditorMode) {
                // User is in editor mode and is able to clear all locations.
                this.locationSettings.clearSpawnLocations();

                Messages.editor_clear_locations.sendMessage(player);

                return;
            }

            // User must be in editor mode to clear locations. This is to help prevent accidental clears.
            Messages.editor_clear_failure.sendMessage(player);
        });
    }
}