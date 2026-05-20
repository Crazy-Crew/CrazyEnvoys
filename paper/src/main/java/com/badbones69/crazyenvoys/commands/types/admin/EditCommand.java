package com.badbones69.crazyenvoys.commands.types.admin;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.enums.PersistentKeys;
import com.badbones69.crazyenvoys.commands.types.EnvoyCommand;
import com.ryderbelserion.fusion.paper.builders.items.ItemBuilder;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionDefault;
import java.util.UUID;

public class EditCommand extends EnvoyCommand {

    @Command(value = "edit")
    @Permission(value = "envoy.edit", def = PermissionDefault.OP)
    @Syntax("/envoys edit")
    public void edit(final Player player) {
        if (this.envoyRegistry.isEnvoyActive(player.getWorld().getUID())) {
            Messages.kicked_from_editor_mode.sendMessage(player);

            return;
        }

        final PlayerInventory inventory = player.getInventory();
        final UUID uuid = player.getUniqueId();

        this.userRegistry.getUser(uuid).ifPresent(user -> {
            if (user.isEditorMode) {
                user.isEditorMode = false;

                this.platform.sendBlockChange(player, Material.AIR);

                for (final ItemStack itemStack : inventory.getContents()) {
                    if (itemStack == null || itemStack.isEmpty()) continue;

                    final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

                    if (!container.has(PersistentKeys.envoy_wand.getNamespacedKey())) continue;

                    inventory.remove(itemStack);
                }

                Messages.leave_editor_mode.sendMessage(player);

                return;
            }

            user.isEditorMode = true;

            this.platform.sendBlockChange(player, Material.BEDROCK);

            inventory.addItem(ItemBuilder.from(ItemType.BEDROCK).setPersistentBoolean(PersistentKeys.envoy_wand.getNamespacedKey(), true).asItemStack());

            Messages.enter_editor_mode.sendMessage(player);
        });
    }
}