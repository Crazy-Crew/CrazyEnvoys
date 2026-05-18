package com.badbones69.crazyenvoys.api.builders.gui;

import com.badbones69.crazyenvoys.api.builders.gui.api.types.StaticInventory;
import com.badbones69.crazyenvoys.api.enums.PersistentKeys;
import com.badbones69.crazyenvoys.api.objects.misc.Prize;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class PrizeGui extends StaticInventory {

    private final Prize prize;

    public PrizeGui(@NotNull final Player player, @NotNull final Tier tier, @NotNull final Prize prize, @NotNull final String title, final int size) {
        super(player, tier, title, size);

        this.prize = prize;
    }

    @Override
    public void build() {
        final String id = this.prize.getPrizeID();

        this.prize.getItemBuilders().forEach(builder -> {
            builder.setPersistentString(PersistentKeys.prize_item.getNamespacedKey(), id);

            this.gui.addSlotAction(builder.asItemStack(this.player), event -> {
                final ItemStack itemStack = event.getCurrentItem();

                if (itemStack == null || itemStack.isEmpty()) return;

                final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

                if (!container.has(PersistentKeys.prize_item.getNamespacedKey())) return;

                final PlayerInventory inventory = this.player.getInventory();

                itemStack.editPersistentDataContainer(box -> box.remove(PersistentKeys.prize_item.getNamespacedKey()));

                inventory.setMaxStackSize(64);

                inventory.addItem(itemStack);
            });
        });

        this.gui.open(this.player);
    }
}