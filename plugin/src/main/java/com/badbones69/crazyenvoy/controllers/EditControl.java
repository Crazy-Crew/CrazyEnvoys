package com.badbones69.crazyenvoy.controllers;

import com.badbones69.crazyenvoy.Methods;
import com.badbones69.crazyenvoy.api.CrazyManager;
import com.badbones69.crazyenvoy.api.enums.Messages;
import org.bukkit.Location;
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
    
    @SuppressWarnings({"deprecation", "squid:CallToDeprecatedMethod"})
    public static void showFakeBlocks(Player player) {
        for (Block block : envoy.getSpawnLocations()) {
            player.sendBlockChange(block.getLocation(), Material.BEDROCK, (byte) 0);
        }
    }
    
    @SuppressWarnings({"deprecation", "squid:CallToDeprecatedMethod"})
    public static void removeFakeBlocks(Player player) {
        for (Block block : envoy.getSpawnLocations()) {
            player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
        }
    }
    
    @SuppressWarnings({"deprecation", "squid:CallToDeprecatedMethod"})
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent e) {
        final Player player = e.getPlayer();
        if (isEditor(player)) {
            e.setCancelled(true);
            if (Methods.getItemInHand(player).getType() == Material.BEDROCK) {
                envoy.addLocation(e.getBlock());
                Messages.ADD_LOCATION.sendMessage(player);
                for (Player p : editors) {
                    p.sendBlockChange(e.getBlock().getLocation(), Material.BEDROCK, (byte) 0);
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (isEditor(player)) {
            e.setCancelled(true);
            Location loc = e.getBlock().getLocation();
            if (envoy.isLocation(loc)) {
                e.getBlock().getState().update();
                envoy.removeLocation(loc.getBlock());
                Messages.REMOVE_LOCATION.sendMessage(player);
            }
        }
    }
    
}