package com.badbones69.crazyenvoys.paper.controllers;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.Methods;
import com.badbones69.crazyenvoys.paper.api.CrazyManager;
import com.badbones69.crazyenvoys.paper.api.enums.Translation;
import com.badbones69.crazyenvoys.paper.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.paper.api.objects.LocationSettings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.UUID;

public class EditControl implements Listener {

    private final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final Methods methods = this.plugin.getMethods();

    private final EditorSettings editorSettings = this.plugin.getEditorSettings();

    private final LocationSettings locationSettings = this.plugin.getLocationSettings();

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent e) {
        final Player player = e.getPlayer();
        final Block block = e.getBlock();

        if (this.editorSettings.isEditor(player)) {
            e.setCancelled(true);

            if (this.methods.getItemInHand(player).getType() == Material.BEDROCK) {
                this.locationSettings.addSpawnLocation(block);

                Translation.add_location.sendMessage(player);

                for (UUID uuid : this.editorSettings.getEditors()) {
                    if (uuid == player.getUniqueId()) player.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        final Player player = e.getPlayer();
        final Block block = e.getBlock();

        if (this.editorSettings.isEditor(player)) {
            e.setCancelled(true);

            if (this.crazyManager.isLocation(block.getLocation())) {
                block.getState().update();

                this.locationSettings.removeSpawnLocation(block);

                Translation.remove_location.sendMessage(player);
            }
        }
    }
}