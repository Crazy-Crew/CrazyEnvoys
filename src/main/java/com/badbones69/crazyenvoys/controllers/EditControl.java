package com.badbones69.crazyenvoys.controllers;

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
    
    private static final ArrayList<Player> editors = new ArrayList<>();
    private static final CrazyManager crazyManager = CrazyManager.getInstance();
    
    public static List<Player> getEditors() {
        return editors;
    }
    
    public static void addEditor(Player player) {
        editors.add(player);
    }
    
    public static void removeEditor(Player player) {
        editors.remove(player);
    }
    
    public static boolean isEditor(Player player) {
        return editors.contains(player);
    }
    
    public static void showFakeBlocks(Player player) {
        for (Block block : crazyManager.getSpawnLocations()) {
            player.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
        }
    }
    
    public static void removeFakeBlocks() {
        for (Block block : crazyManager.getSpawnLocations()) {
            block.getState().update();
        }
    }
    
    /**
     * Clears all the Envoy locations.
     */
    public static void clearEnvoyLocations() {
        crazyManager.clearLocations();
    }
    
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent e) {
        final Player player = e.getPlayer();
        Block block = e.getBlock();

        if (isEditor(player)) {
            e.setCancelled(true);

            if (Methods.getItemInHand(player).getType() == Material.BEDROCK) {
                crazyManager.addLocation(block);
                Messages.ADD_LOCATION.sendMessage(player);

                for (Player editor : editors) {
                    editor.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
                }
            }
        }
    }
    
    @EventHandler
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