package com.badbones69.crazyenvoys.listeners;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import com.ryderbelserion.fusion.paper.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class EnvoyEditListener implements Listener {

    private @NotNull final CrazyEnvoys plugin = CrazyEnvoys.get();

    private @NotNull final EditorSettings editorSettings = this.plugin.getEditorSettings();

    private @NotNull final LocationSettings locationSettings = this.plugin.getLocationSettings();

    private @NotNull final CrazyManager crazyManager = this.plugin.getCrazyManager();
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        Player player = event.getPlayer();
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
                player.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
            }
        }.runDelayed(2L);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!this.editorSettings.isEditor(player)) return;

        event.setCancelled(true);

        if (!this.crazyManager.isLocation(block.getLocation())) return;

        block.getState().update();

        this.locationSettings.removeSpawnLocation(block);

        Messages.remove_location.sendMessage(player);
    }
}