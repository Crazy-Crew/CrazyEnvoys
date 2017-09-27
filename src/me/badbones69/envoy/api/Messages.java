package me.badbones69.envoy.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.badbones69.envoy.Main;
import me.badbones69.envoy.Methods;

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
	DROPS_PAGE("Drops-Page", "%prefix%&7Use /envoy drops [page] to see more."),
	DROPS_FORMAT("Drops-Format", "&7[&6%id%&7]: %world%, %x%, %y%, %z%"),
	DROPS_AVAILABLE("Drops-Available", "%prefix%&7List of all available envoys."),
	DROPS_POSSIBILITIES("Drops-Possibilities", "%prefix%&7List of location envoy's may spawn at."),
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
	private String defaultMessage;
	
	private Messages(String path) {
		this.path = "Messages." + path;
	}
	
	private Messages(String path, String defaultMessage) {
		this.path = "Messages." + path;
		this.defaultMessage = defaultMessage;
	}
	
	public String getMessage() {
		return Methods.color(Main.settings.getMessages().contains(path) ? Main.settings.getMessages().getString(path).replaceAll("%prefix%", Methods.getPrefix()) : defaultMessage.replaceAll("%prefix%", Methods.getPrefix()));
	}
	
	public List<String> getMessages() {
		List<String> msgs = new ArrayList<>();
		for(String msg : Main.settings.getMessages().contains(path) ? Main.settings.getMessages().getStringList(path) : Arrays.asList(defaultMessage)) {
			msgs.add(Methods.color(msg.replaceAll("%prefix%", Methods.getPrefix())));
		}
		return msgs;
	}
	
	public String getMessage(HashMap<String, String> placeholder) {
		String msg = getMessage();
		if(placeholder != null) {
			for(String ph : placeholder.keySet()) {
				if(msg.contains(ph)) {
					msg = msg.replaceAll(ph, placeholder.get(ph));
				}
			}
		}
		return msg;
	}
	
	public List<String> getMessages(HashMap<String, String> placeholder) {
		List<String> msgs = new ArrayList<>();
		for(String msg : getMessages()) {
			if(placeholder != null) {
				for(String ph : placeholder.keySet()) {
					if(msg.contains(ph)) {
						msg = msg.replaceAll(ph, placeholder.get(ph));
					}
				}
			}
			msgs.add(msg);
		}
		return msgs;
	}
	
	public Boolean isList() {
		if(Main.settings.getMessages().getStringList(path).isEmpty()) {
			return false;
		}
		return true;
	}
	
	public void sendMessage(Player player) {
		if(isList()) {
			for(String msg : getMessages()) {
				if (!msg.isEmpty()) {
					player.sendMessage(msg);
				}
			}
		}else {
			player.sendMessage(getMessage());
		}
	}
	
	public void sendMessage(CommandSender sender) {
		if(isList()) {
			for(String msg : getMessages()) {
				if (!msg.isEmpty()) {
					sender.sendMessage(msg);
				}
			}
		}else {
			sender.sendMessage(getMessage());
		}
	}
	
	public void sendMessage(Player player, HashMap<String, String> placeholder) {
		if(isList()) {
			for(String msg : getMessages()) {
				if(placeholder != null) {
					for(String ph : placeholder.keySet()) {
						if(msg.contains(ph)) {
							msg = msg.replaceAll(ph, placeholder.get(ph));
						}
					}
				}
				if (!msg.isEmpty()) {
					player.sendMessage(msg);
				}
			}
		}else {
			String msg = getMessage();
			if(placeholder != null) {
				for(String ph : placeholder.keySet()) {
					if(msg.contains(ph)) {
						msg = msg.replaceAll(ph, placeholder.get(ph));
					}
				}
			}
			if (!msg.isEmpty()) {
				player.sendMessage(msg);
			}
		}
	}
	
	public void sendMessage(CommandSender sender, HashMap<String, String> placeholder) {
		if(isList()) {
			for(String msg : getMessages()) {
				if(placeholder != null) {
					for(String ph : placeholder.keySet()) {
						if(msg.contains(ph)) {
							msg = msg.replaceAll(ph, placeholder.get(ph));
						}
					}
				}
				if (!msg.isEmpty()) {
					sender.sendMessage(msg);
				}
			}
		}else {
			String msg = getMessage();
			if(placeholder != null) {
				for(String ph : placeholder.keySet()) {
					if(msg.contains(ph)) {
						msg = msg.replaceAll(ph, placeholder.get(ph));
					}
				}
			}
			if (!msg.isEmpty()) {
				sender.sendMessage(msg);
			}
		}
	}
	
	public void broadcastMessage(Boolean ignore, HashMap<String, String> placeholder) {
		if(Main.settings.getConfig().getBoolean("Settings.World-Messages.Toggle")) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				for(String w : Main.settings.getConfig().getStringList("Settings.World-Messages.Worlds")) {
					if(p.getWorld().getName().equalsIgnoreCase(w)) {
						if(ignore) {
							if(!Envoy.isIgnoringMessages(p.getUniqueId())) {
								sendMessage(p, placeholder);
							}
						}else {
							sendMessage(p, placeholder);
						}
					}
				}
			}
		}else {
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(ignore) {
					if(!Envoy.isIgnoringMessages(p.getUniqueId())) {
						sendMessage(p, placeholder);
					}
				}else {
					sendMessage(p, placeholder);
				}
			}
		}
		if(isList()) {
			for(String msg : getMessages()) {
				if(placeholder != null) {
					for(String ph : placeholder.keySet()) {
						if(msg.contains(ph)) {
							msg = msg.replaceAll(ph, placeholder.get(ph));
						}
					}
				}
				Bukkit.getLogger().log(Level.INFO, msg);
			}
		}else {
			String msg = getMessage();
			if(placeholder != null) {
				for(String ph : placeholder.keySet()) {
					if(msg.contains(ph)) {
						msg = msg.replaceAll(ph, placeholder.get(ph));
					}
				}
			}
			Bukkit.getLogger().log(Level.INFO, msg);
		}
	}
	
}
