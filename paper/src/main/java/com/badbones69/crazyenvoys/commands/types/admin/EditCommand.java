package com.badbones69.crazyenvoys.commands.types.admin;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.types.EnvoyCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemType;
import org.bukkit.permissions.PermissionDefault;

public class EditCommand extends EnvoyCommand {

    @Command(value = "edit")
    @Permission(value = "envoy.edit", def = PermissionDefault.OP)
    @Syntax("/envoys edit")
    public void edit(final Player player) {
        if (this.crazyManager.isEnvoyActive()) {
            Messages.kicked_from_editor_mode.sendMessage(player);

            return;
        }

        final Inventory inventory = player.getInventory();

        if (this.editorSettings.isEditor(player)) {
            this.editorSettings.removeEditor(player);
            this.editorSettings.removeFakeBlocks(player);

            inventory.remove(Material.BEDROCK); //todo() pdc

            Messages.leave_editor_mode.sendMessage(player);

        } else {
            this.editorSettings.addEditor(player);
            this.editorSettings.showFakeBlocks(player);

            inventory.addItem(ItemType.BEDROCK.createItemStack()); //todo() pdc

            Messages.enter_editor_mode.sendMessage(player);
        }
    }
}