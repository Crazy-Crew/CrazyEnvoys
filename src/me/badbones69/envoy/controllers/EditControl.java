package me.badbones69.envoy.controllers;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.badbones69.envoy.Methods;
import me.badbones69.envoy.api.Envoy;
import me.badbones69.envoy.api.Messages;

public class EditControl implements Listener {
	
	private static ArrayList<Player> editors = new ArrayList<Player>();
	private static Plugin plugin = Bukkit.getPluginManager().getPlugin("CrazyEnvoy");
	
	public static ArrayList<Player> getEditors() {
		return editors;
	}
	
	public static void addEditor(Player player) {
		editors.add(player);
	}
	
	public static void removeEditor(Player player) {
		editors.remove(player);
	}
	
	public static Boolean isEditor(Player player) {
		return editors.contains(player);
	}
	
	@SuppressWarnings("deprecation")
	public static void showFakeBlocks(Player player) {
		for(Location loc : Envoy.getLocations()) {
			player.sendBlockChange(loc, Material.BEDROCK, (byte) 0);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void removeFakeBlocks(Player player) {
		for(Location loc : Envoy.getLocations()) {
			player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent e) {
		final Player player = e.getPlayer();
		if(isEditor(player)) {
			e.setCancelled(true);
			if(Methods.getItemInHand(player).getType() == Material.BEDROCK) {
				Envoy.addLocation(e.getBlock().getLocation());
				Messages.ADD_LOCATION.sendMessage(player);
				new BukkitRunnable() {
					@Override
					public void run() {
						for(Player p : editors) {
							p.sendBlockChange(e.getBlock().getLocation(), Material.BEDROCK, (byte) 0);
						}
					}
				}.runTaskLater(plugin, 2);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		if(isEditor(player)) {
			e.setCancelled(true);
			Location loc = e.getBlock().getLocation();
			if(Envoy.isLocation(loc)) {
				e.getBlock().getState().update();
				Envoy.removeLocation(loc);
				Messages.REMOVE_LOCATION.sendMessage(player);
			}
		}
	}
	
}