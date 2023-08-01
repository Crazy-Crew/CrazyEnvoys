package com.badbones69.crazyenvoys.paper.controllers;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.Methods;
import com.badbones69.crazyenvoys.paper.api.CrazyManager;
import com.badbones69.crazyenvoys.paper.api.enums.Messages;
import com.badbones69.crazyenvoys.paper.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.paper.api.objects.LocationSettings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class EditControl implements Listener {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final Methods methods = plugin.getMethods();

    private final EditorSettings editorSettings = plugin.getEditorSettings();

    private final LocationSettings locationSettings = plugin.getLocationSettings();

    private final CrazyManager crazyManager = plugin.getCrazyManager();
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent e) {
        final Player player = e.getPlayer();
        Block block = e.getBlock();

        if (editorSettings.isEditor(player)) {
            e.setCancelled(true);

            if (methods.getItemInHand(player).getType() == Material.BEDROCK) {
                locationSettings.addSpawnLocation(block);

                Messages.ADD_LOCATION.sendMessage(player);

                for (Player editor : editorSettings.getEditors()) {
                    editor.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (editorSettings.isEditor(player)) {
            e.setCancelled(true);

            if (crazyManager.isLocation(block.getLocation())) {
                block.getState().update();

                locationSettings.removeSpawnLocation(block);

                Messages.REMOVE_LOCATION.sendMessage(player);
            }
        }
    }
}