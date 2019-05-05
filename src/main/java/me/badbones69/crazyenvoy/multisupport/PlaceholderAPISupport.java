package me.badbones69.crazyenvoy.multisupport;

import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.FileManager.Files;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPISupport extends PlaceholderExpansion {
	
	private CrazyEnvoy envoy = CrazyEnvoy.getInstance();
	private Plugin plugin;
	
	public PlaceholderAPISupport(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public String onRequest(OfflinePlayer player, String identifier) {
		if(identifier.equalsIgnoreCase("cooldown")) {
			if(envoy.isEnvoyActive()) {
				return Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.On-Going");
			}else {
				return envoy.getNextEnvoyTime();
			}
		}
		if(identifier.equalsIgnoreCase("time_left")) {
			if(envoy.isEnvoyActive()) {
				return envoy.getEnvoyRunTimeLeft();
			}else {
				return Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.Not-Running");
			}
		}
		if(identifier.equalsIgnoreCase("crates_left")) {
			return envoy.getActiveEnvoys().size() + "";
		}
		return "";
	}
	
	@Override
	public boolean persist() {
		return true;
	}
	
	@Override
	public String getIdentifier() {
		return "crazyenvoy";
	}
	
	@Override
	public String getAuthor() {
		return "BadBones69";
	}
	
	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}
	
}