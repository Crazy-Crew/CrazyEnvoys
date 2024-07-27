package com.badbones69.crazyenvoys.api.builders.types;

import com.badbones69.crazyenvoys.api.builders.InventoryBuilder;
import com.badbones69.crazyenvoys.api.enums.PersistentKeys;
import com.badbones69.crazyenvoys.api.objects.misc.Prize;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PrizeGui extends InventoryBuilder {

    private Prize prize;

    public PrizeGui(final Player player, final String title, final int size, final Tier tier, final Prize prize) {
        super(player, title, size, tier);

        this.prize = prize;
    }

    public PrizeGui() {}

    @Override
    public InventoryBuilder build() {
        final Inventory inventory = getInventory();

        this.prize.getItems().forEach(item -> {
            item.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(PersistentKeys.prize_item.getNamespacedKey(), PersistentDataType.STRING, prize.getPrizeID()));

            inventory.setItem(inventory.firstEmpty(), item);
        });

        return this;
    }

    @Override
    public void run(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof PrizeGui holder)) return;

        event.setCancelled(true);

        final Player player = holder.getPlayer();

        if (event.getCurrentItem() == null) return;

        final ItemStack itemStack = event.getCurrentItem();

        if (!itemStack.hasItemMeta()) return;

        final ItemMeta itemMeta = itemStack.getItemMeta();

        final PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        if (!pdc.has(PersistentKeys.prize_item.getNamespacedKey())) return;

        final PlayerInventory playerInventory = player.getInventory();

        playerInventory.setMaxStackSize(64);

        itemStack.editMeta(meta -> meta.getPersistentDataContainer().remove(PersistentKeys.prize_item.getNamespacedKey()));

        playerInventory.addItem(itemStack);

        event.setCurrentItem(null);

        if (inventory.isEmpty()) {
            inventory.close();
        }
    }

    @Override
    public void run(InventoryDragEvent event) {
        final Inventory inventory = event.getView().getTopInventory();

        if (!(inventory.getHolder(false) instanceof PrizeGui)) return;

        event.setCancelled(true);
    }
}