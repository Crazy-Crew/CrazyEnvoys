package me.badbones69.crazyenvoy.multisupport;

import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.FileManager.Files;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPISupport extends EZPlaceholderHook {
	
	public PlaceholderAPISupport(Plugin plugin) {
		super(plugin, "crazyenvoy");
	}
	private CrazyEnvoy envoy = CrazyEnvoy.getInstance();
	
	@Override
	public String onPlaceholderRequest(Player player, String placeHolder) {
		if(placeHolder.equalsIgnoreCase("cooldown")) {
			if(envoy.isEnvoyActive()) {
				return Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.On-Going");
			}else {
				return envoy.getNextEnvoyTime();
			}
		}
		if(placeHolder.equalsIgnoreCase("time_left")) {
			if(envoy.isEnvoyActive()) {
				return envoy.getEnvoyRunTimeLeft();
			}else {
				return Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.Not-Running");
			}
		}
		if(placeHolder.equalsIgnoreCase("crates_left")) {
			return envoy.getActiveEnvoys().size() + "";
		}
		return null;
	}
	
}