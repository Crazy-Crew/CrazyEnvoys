package me.BadBones69.envoy.controlers;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.envoy.Main;
import me.BadBones69.envoy.Methods;
import me.BadBones69.envoy.MultiSupport.Support;
import me.BadBones69.envoy.MultiSupport.WorldGuard;
import me.BadBones69.envoy.api.Envoy;
import me.BadBones69.envoy.api.Flare;
import me.BadBones69.envoy.api.Messages;

public class FlareControl implements Listener{
	
	@EventHandler
	public void onFlareActivate(PlayerInteractEvent e){
		Player player = e.getPlayer();
		FileConfiguration config = Main.settings.getConfig();
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			ItemStack flare = Methods.getItemInHand(player);
			if(flare != null){
				if(Flare.isFlare(flare)){
					e.setCancelled(true);
					if(player.hasPermission("envoy.flare.use")){
						if(Envoy.isEnvoyActive()){
							Messages.ALREADY_STARTED.sendMessage(player);
							return;
						}else{
							int online = Bukkit.getServer().getOnlinePlayers().size();
							if(config.getBoolean("Settings.Minimum-Players-Toggle")){
								if(config.getBoolean("Settings.Minimum-Flare-Toggle")){
									if(online < config.getInt("Settings.Minimum-Players")){
										HashMap<String, String> placeholder = new HashMap<String, String>();
										placeholder.put("%amount%", online + "");
										Messages.NOT_ENOUGH_PLAYERS.sendMessage(player, placeholder);
										return;
									}
								}
							}
							Boolean toggle = false;
							if(Support.hasWorldEdit() && Support.hasWorldGuard()){
								if(config.getBoolean("Settings.Flares.World-Guard.Toggle")){
									for(String r : config.getStringList("Settings.Flares.World-Guard.Regions")){
										if(WorldGuard.inRegion(r, player.getLocation())){
											toggle = true;
										}
									}
								}else{
									toggle = true;
								}
							}else{
								toggle = true;
							}
							if(!toggle){
								Messages.NOT_IN_WORLD_GUARD_REGION.sendMessage(player);
								return;
							}
							Messages.USED_FLARE.sendMessage(player);
							Flare.takeFlare(player, flare);
							Envoy.startEnvoyEvent();
							return;
						}
					}else{
						Messages.CANT_USE_FLARES.sendMessage(player);
						return;
					}
				}
			}
		}
	}
	
}