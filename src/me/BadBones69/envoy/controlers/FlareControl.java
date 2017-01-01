package me.BadBones69.envoy.controlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.envoy.Main;
import me.BadBones69.envoy.Methods;
import me.BadBones69.envoy.api.Envoy;
import me.BadBones69.envoy.api.Flare;

public class FlareControl implements Listener{
	
	@EventHandler
	public void onFlareActivate(PlayerInteractEvent e){
		Player player = e.getPlayer();
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
							if(Main.settings.getConfig().getBoolean("Settings.Minimum-Players-Toggle")){
								if(Main.settings.getConfig().getBoolean("Settings.Minimum-Flare-Toggle")){
									if(online < Main.settings.getConfig().getInt("Settings.Minimum-Players")){
										player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Not-Enough-Players")
												.replaceAll("%Amount%", online + "").replaceAll("%amount%", online + "")));
										return;
									}
								}
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