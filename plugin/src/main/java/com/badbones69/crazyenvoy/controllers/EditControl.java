package com.badbones69.crazyenvoy.controllers;

import com.badbones69.crazyenvoy.Methods;
import com.badbones69.crazyenvoy.api.CrazyManager;
import com.badbones69.crazyenvoy.api.enums.Messages;
import com.badbones69.crazyenvoy.multisupport.Version;
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
    
    private static ArrayList<Player> editors = new ArrayList<>();
    private static CrazyManager envoy = CrazyManager.getInstance();
    
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
        for (Block block : envoy.getSpawnLocations()) {
            if (Version.isNewer(Version.v1_12_R1)) {
                player.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
            } else {
                player.sendBlockChange(block.getLocation(), Material.BEDROCK, (byte) 0);
            }
        }
    }
    
    public static void removeFakeBlocks(Player player) {
        for (Block block : envoy.getSpawnLocations()) {
            block.getState().update();
        }
    }
    
    /**
     * Clears all the Envoy locations.
     */
    public static void clearEnvoyLocations() {
        envoy.clearLocations();
    }
    
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent e) {
        final Player player = e.getPlayer();
        Block block = e.getBlock();
        if (isEditor(player)) {
            e.setCancelled(true);
            if (Methods.getItemInHand(player).getType() == Material.BEDROCK) {
                envoy.addLocation(block);
                Messages.ADD_LOCATION.sendMessage(player);
                for (Player editor : editors) {
                    if (Version.isNewer(Version.v1_12_R1)) {
                        editor.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
                    } else {
                        editor.sendBlockChange(block.getLocation(), Material.BEDROCK, (byte) 0);
                    }
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
            if (envoy.isLocation(block.getLocation())) {
                block.getState().update();
                envoy.removeLocation(block);
                Messages.REMOVE_LOCATION.sendMessage(player);
            }
        }
    }
    
}