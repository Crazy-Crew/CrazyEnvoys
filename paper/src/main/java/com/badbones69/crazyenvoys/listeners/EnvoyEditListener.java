package com.badbones69.crazyenvoys.listeners;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyEnvoysPlatform;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.enums.PersistentKeys;
import com.badbones69.crazyenvoys.api.registry.PaperUserRegistry;
import com.badbones69.crazyenvoys.registry.EnvoyRegistry;
import com.badbones69.crazyenvoys.storage.impl.objects.StorageHolder;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import com.ryderbelserion.fusion.paper.builders.folia.Scheduler;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class EnvoyEditListener implements Listener {

    private @NotNull final CrazyEnvoys plugin = CrazyEnvoys.get();

    private @NotNull final CrazyEnvoysPlatform platform = this.plugin.getPlatform();

    private @NotNull final StorageHolder holder = this.platform.getStorageHolder();

    private @NotNull final EnvoyRegistry envoyRegistry = this.platform.getEnvoyRegistry();

    private @NotNull final PaperUserRegistry userRegistry = this.platform.getUserRegistry();

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlock();

        if (block.isEmpty()) return;

        final Player player = event.getPlayer();

        this.userRegistry.getUser(player.getUniqueId()).ifPresent(user -> {
            if (!user.isEditorMode) return;

            final ItemStack itemStack = event.getItemInHand();

            if (itemStack.isEmpty()) return;

            final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

            if (!container.has(PersistentKeys.envoy_wand.getNamespacedKey())) return;

            event.setCancelled(true);

            this.envoyRegistry.getWorld(block.getWorld().getUID()).ifPresent(world -> {
                this.holder.addLocation(world, block.getX(), block.getY(), block.getZ());

                Messages.add_location.sendMessage(player);

                new FoliaScheduler(this.plugin, Scheduler.global_scheduler) {
                    @Override
                    public void run() {
                        platform.sendBlockChange(player, Material.BEDROCK);
                    }
                }.runDelayed(2);
            });
        });
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();

        if (block.isEmpty()) return;

        final Player player = event.getPlayer();

        this.userRegistry.getUser(player.getUniqueId()).ifPresent(user -> {
            if (!user.isEditorMode) return;

            final ItemStack itemStack = player.getActiveItem();

            if (itemStack.isEmpty()) return;

            final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

            if (!container.has(PersistentKeys.envoy_wand.getNamespacedKey())) return;

            this.envoyRegistry.getWorld(block.getWorld().getUID()).ifPresent(world -> world.getLocationByCoordinates(block.getX(), block.getY(), block.getZ()).ifPresent(location -> {
                final UUID uuid = location.getWorld();
                final String id = uuid.toString();

                if (this.holder.removeLocation(id)) {
                    Messages.remove_location.sendMessage(player);

                    block.getState(true).update();

                    world.removeLocation(id);
                }
            }));
        });
    }
}