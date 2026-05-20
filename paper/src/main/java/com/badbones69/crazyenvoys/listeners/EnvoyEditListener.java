package com.badbones69.crazyenvoys.listeners;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.EnvoysPlugin;
import com.badbones69.crazyenvoys.registry.EnvoyRegistry;
import com.badbones69.crazyenvoys.storage.impl.objects.StorageHolder;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

public class EnvoyEditListener implements Listener {

    private @NotNull final CrazyEnvoys plugin = CrazyEnvoys.get();

    private @NotNull final EnvoysPlugin envoys = this.plugin.getPlugin();

    private @NotNull final StorageHolder holder = this.envoys.getStorageHolder();

    private @NotNull final EnvoyRegistry registry = this.envoys.getEnvoyRegistry();

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlock();

        if (block.isEmpty()) return;

        this.registry.getWorld(block.getWorld().getUID()).ifPresent(world -> this.holder.addLocation(world, block.getX(), block.getY(), block.getZ()));;

        /*Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!this.editorSettings.isEditor(player)) return;

        event.setCancelled(true);

        if (Methods.getItemInHand(player).getType() != Material.BEDROCK) return;

        this.locationSettings.addSpawnLocation(block);

        Messages.add_location.sendMessage(player);

        new FoliaScheduler(this.plugin, Scheduler.global_scheduler) {
            @Override
            public void run() {
                if (!editorSettings.getEditors().contains(player.getUniqueId())) return;

                player.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData()); //todo() improve this
            }
        }.runDelayed(2L);*/
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();

        if (block.isEmpty()) return;

        //this.registry.getWorld(block.getWorld().getName()).ifPresent(world -> {

        //});

        /*Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!this.editorSettings.isEditor(player)) return;

        event.setCancelled(true);

        if (!this.crazyManager.isLocation(block.getLocation())) return;

        block.getState().update();

        this.locationSettings.removeSpawnLocation(block);

        Messages.remove_location.sendMessage(player);*/
    }
}