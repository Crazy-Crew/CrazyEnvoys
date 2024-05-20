package com.badbones69.crazyenvoys.commands.v2.envoys.types.admin.editor;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.commands.v2.envoys.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public class CommandEditor extends BaseCommand {

    private final @NotNull EditorSettings editorSettings = this.plugin.getEditorSettings();

    @Command("edit")
    @Permission(value = "crazyenvoys.edit", def = PermissionDefault.OP)
    public void debug(final Player player) {
        if (this.crazyManager.isEnvoyActive()) {
            Messages.kicked_from_editor_mode.sendMessage(player);

            return;
        }

        if (this.editorSettings.isEditor(player)) {
            this.editorSettings.removeEditor(player);
            this.editorSettings.removeFakeBlocks();

            player.getInventory().remove(Material.BEDROCK);

            Messages.leave_editor_mode.sendMessage(player);
        } else {
            this.editorSettings.addEditor(player);
            this.editorSettings.showFakeBlocks(player);

            player.getInventory().addItem(new ItemStack(Material.BEDROCK, 1));

            Messages.enter_editor_mode.sendMessage(player);
        }
    }
}