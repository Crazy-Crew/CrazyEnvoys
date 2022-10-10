package com.badbones69.crazyenvoys.controllers;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.enums.Messages;
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
    
    private final ArrayList<Player> editors = new ArrayList<>();

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final CrazyManager crazyManager = plugin.getCrazyManager();

    private final Methods methods = plugin.getMethods();
    
    public List<Player> getEditors() {
        return editors;
    }
    
    public void addEditor(Player player) {
        editors.add(player);
    }
    
    public void removeEditor(Player player) {
        editors.remove(player);
    }
    
    public boolean isEditor(Player player) {
        return editors.contains(player);
    }
    
    public void showFakeBlocks(Player player) {
        for (Block block : crazyManager.getSpawnLocations()) {
            player.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
        }
    }
    
    public void removeFakeBlocks() {
        for (Block block : crazyManager.getSpawnLocations()) {
            block.getState().update();
        }
    }
    
    /**
     * Clears all the Envoy locations.
     */
    public void clearEnvoyLocations() {
        crazyManager.clearLocations();
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent e) {
        final Player player = e.getPlayer();
        Block block = e.getBlock();

        if (isEditor(player)) {
            e.setCancelled(true);

            if (methods.getItemInHand(player).getType() == Material.BEDROCK) {
                crazyManager.addLocation(block);
                Messages.ADD_LOCATION.sendMessage(player);

                for (Player editor : editors) {
                    editor.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (isEditor(player)) {
            e.setCancelled(true);

            if (crazyManager.isLocation(block.getLocation())) {
                block.getState().update();

                crazyManager.removeLocation(block);

                Messages.REMOVE_LOCATION.sendMessage(player);
            }
        }
    }
}