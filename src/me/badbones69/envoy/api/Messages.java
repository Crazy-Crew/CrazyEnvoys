package me.BadBones69.envoy.api;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.BadBones69.envoy.Main;
import me.BadBones69.envoy.Methods;

public enum Messages {
	
	LEFT("Left"),
	ENDED("Ended"),
	WARNING("Warning"),
	STARTED("Started"),
	ON_GOING("On-Going"),
	RELOADED("Reloaded"),
	TIME_LEFT("Time-Left"),
	USED_FLARE("Used-Flare"),
	GIVE_FLARE("Give-Flare"),
	NEW_CENTER("New-Center"),
	NOT_ONLINE("Not-Online"),
	NOT_RUNNING("Not-Running"),
	NOT_STARTED("Not-Started"),
	GIVEN_FLARE("Given-Flare"),
	FORCE_START("Force-Start"),
	FORCE_ENDED("Force-Ended"),
	PLAYERS_ONLY("Players-Only"),
	NOT_A_NUMBER("Not-A-Number"),
	ADD_LOCATION("Add-Location"),
	COOLDOWN_LEFT("Cooldown-Left"),
	NO_PERMISSION("No-Permission"),
	TIME_TILL_EVENT("Time-Till-Event"),
	CANT_USE_FLARES("Cant-Use-Flares"),
	REMOVE_LOCATION("Remove-Location"),
	ALREADY_STARTED("Already-Started"),
	ENTER_EDITOR_MODE("Enter-Editor-Mode"),
	LEAVE_EDITOR_MODE("Leave-Editor-Mode"),
	NOT_ENOUGH_PLAYERS("Not-Enough-Players"),
	STOP_IGNORING_MESSAGES("Stop-Ignoring-Messages"),
	START_IGNORING_MESSAGES("Start-Ignoring-Messages"),
	KICKED_FROM_EDITOR_MODE("Kicked-From-Editor-Mode"),
	NOT_IN_WORLD_GUARD_REGION("Not-In-World-Guard-Region");
	
	private String path;
	
	private Messages(String path){
		this.path = path;
	}
	
	public void sendMessage(Player player){
		if(!Main.settings.getMessages().getStringList("Messages." + path).isEmpty()){
			for(String msg : Main.settings.getMessages().getStringList("Messages." + path)){
				player.sendMessage(Methods.color(msg).replaceAll("%prefix%", Methods.getPrefix()));
			}
		}else{
			String msg = Methods.color(Main.settings.getMessages().getString("Messages." + path)).replaceAll("%prefix%", Methods.getPrefix());
			player.sendMessage(msg);
		}
	}
	
	public void sendMessage(CommandSender sender){
		if(!Main.settings.getMessages().getStringList("Messages." + path).isEmpty()){
			for(String msg : Main.settings.getMessages().getStringList("Messages." + path)){
				sender.sendMessage(Methods.color(msg).replaceAll("%prefix%", Methods.getPrefix()));
			}
		}else{
			String msg = Methods.color(Main.settings.getMessages().getString("Messages." + path)).replaceAll("%prefix%", Methods.getPrefix());
			sender.sendMessage(msg);
		}
	}
	
	public void sendMessage(Player player, HashMap<String, String> placeholder){
		if(!Main.settings.getMessages().getStringList("Messages." + path).isEmpty()){
			for(String msg : Main.settings.getMessages().getStringList("Messages." + path)){
				if(placeholder != null){
					for(String ph : placeholder.keySet()){
						if(msg.contains(ph)){
							msg = msg.replaceAll(ph, placeholder.get(ph));
						}
					}
				}
				player.sendMessage(Methods.color(msg).replaceAll("%prefix%", Methods.getPrefix()));
			}
		}else{
			String msg = Methods.color(Main.settings.getMessages().getString("Messages." + path)).replaceAll("%prefix%", Methods.getPrefix());
			if(placeholder != null){
				for(String ph : placeholder.keySet()){
					if(msg.contains(ph)){
						msg = msg.replaceAll(ph, placeholder.get(ph));
					}
				}
			}
			player.sendMessage(msg);
		}
	}
	
	public void sendMessage(CommandSender sender, HashMap<String, String> placeholder){
		if(!Main.settings.getMessages().getStringList("Messages." + path).isEmpty()){
			for(String msg : Main.settings.getMessages().getStringList("Messages." + path)){
				if(placeholder != null){
					for(String ph : placeholder.keySet()){
						if(msg.contains(ph)){
							msg = msg.replaceAll(ph, placeholder.get(ph));
						}
					}
				}
				sender.sendMessage(Methods.color(msg).replaceAll("%prefix%", Methods.getPrefix()));
			}
		}else{
			String msg = Methods.color(Main.settings.getMessages().getString("Messages." + path)).replaceAll("%prefix%", Methods.getPrefix());
			if(placeholder != null){
				for(String ph : placeholder.keySet()){
					if(msg.contains(ph)){
						msg = msg.replaceAll(ph, placeholder.get(ph));
					}
				}
			}
			sender.sendMessage(msg);
		}
	}
	
	public void broadcastMessage(Boolean ignore, HashMap<String, String> placeholder){
		if(Main.settings.getConfig().getBoolean("Settings.World-Messages.Toggle")){
			for(Player p : Bukkit.getOnlinePlayers()){
				for(String w : Main.settings.getConfig().getStringList("Settings.World-Messages.Worlds")){
					if(p.getWorld().getName().equalsIgnoreCase(w)){
						if(ignore){
							if(!Envoy.isIgnoringMessages(p.getUniqueId())){
								sendMessage(p, placeholder);
							}
						}else{
							sendMessage(p, placeholder);
						}
					}
				}
			}
		}else{
			for(Player p : Bukkit.getOnlinePlayers()){
				if(ignore){
					if(!Envoy.isIgnoringMessages(p.getUniqueId())){
						sendMessage(p, placeholder);
					}
				}else{
					sendMessage(p, placeholder);
				}
			}
		}
		if(!Main.settings.getMessages().getStringList("Messages." + path).isEmpty()){
			for(String msg : Main.settings.getMessages().getStringList("Messages." + path)){
				if(placeholder != null){
					for(String ph : placeholder.keySet()){
						if(msg.contains(ph)){
							msg = msg.replaceAll(ph, placeholder.get(ph));
						}
					}
				}
				Bukkit.getLogger().log(Level.INFO, Methods.color(msg).replaceAll("%prefix%", Methods.getPrefix()));
			}
		}else{
			String msg = Methods.color(Main.settings.getMessages().getString("Messages." + path)).replaceAll("%prefix%", Methods.getPrefix());
			if(placeholder != null){
				for(String ph : placeholder.keySet()){
					if(msg.contains(ph)){
						msg = msg.replaceAll(ph, placeholder.get(ph));
					}
				}
			}
			Bukkit.getLogger().log(Level.INFO, msg);
		}
	}
	
}