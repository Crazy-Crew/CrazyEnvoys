package com.badbones69.crazyenvoys.controllers;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import java.util.ArrayList;
import java.util.List;

public class EditControl implements Listener {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final CrazyManager crazyManager = plugin.getCrazyManager();

    private final EditorSettings editorSettings = plugin.getEditorSettings();
    private final LocationSettings locationSettings = plugin.getLocationSettings();

    private final Methods methods = plugin.getMethods();
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent e) {
        final Player player = e.getPlayer();
        Block block = e.getBlock();

        if (editorSettings.isEditor(player)) {
            e.setCancelled(true);

            if (methods.getItemInHand(player).getType() == Material.BEDROCK) {
                crazyManager.addLocation(block);
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

            if (locationSettings.isLocation(block.getLocation())) {
                block.getState().update();

                crazyManager.removeLocation(block);

                Messages.REMOVE_LOCATION.sendMessage(player);
            }
        }
    }
}