package me.BadBones69.envoy.controlers;

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
							player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Already-Started")));
							return;
						}else{
							int online = Bukkit.getServer().getOnlinePlayers().size();
							if(config.getBoolean("Settings.Minimum-Players-Toggle")){
								if(config.getBoolean("Settings.Minimum-Flare-Toggle")){
									if(online < config.getInt("Settings.Minimum-Players")){
										player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Not-Enough-Players")
												.replaceAll("%Amount%", online + "").replaceAll("%amount%", online + "")));
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
								player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Not-In-World-Guard-Region")));
								return;
							}
							player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Used-Flare")));
							Flare.takeFlare(player, flare);
							Envoy.startEnvoyEvent();
							return;
						}
					}else{
						player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Cant-Use-Flares")));
						return;
					}
				}
			}
		}
	}
	
}