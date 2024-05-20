package com.badbones69.crazyenvoys.commands.v2.envoys.types.admin.editor;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.commands.v2.envoys.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public class CommandClear extends BaseCommand {

    private final @NotNull LocationSettings locationSettings = this.plugin.getLocationSettings();

    private final @NotNull EditorSettings editorSettings = this.plugin.getEditorSettings();

    @Command("clear")
    @Permission(value = "crazyenvoys.clear", def = PermissionDefault.OP)
    public void clear(final Player player) {
        if (this.editorSettings.isEditor(player)) {
            // User is in editor mode and is able to clear all locations.
            this.locationSettings.clearSpawnLocations();

            Messages.editor_clear_locations.sendMessage(player);
        } else {
            // User must be in editor mode to clear locations. This is to help prevent accidental clears.
            Messages.editor_clear_failure.sendMessage(player);
        }
    }
}