package me.BadBones69.envoy.multisupport;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.BadBones69.envoy.Main;
import me.BadBones69.envoy.api.Envoy;
import me.clip.placeholderapi.external.EZPlaceholderHook;

public class PlaceholderAPISupport extends EZPlaceholderHook{
	
	public PlaceholderAPISupport(Plugin plugin) {
		super(plugin, "envoy");
	}

	@Override
	public String onPlaceholderRequest(Player player, String placeHolder) {
		if(placeHolder.equalsIgnoreCase("cooldown")){
			if(Envoy.isEnvoyActive()){
				return Main.settings.getMessages().getString("Messages.Hologram-Placeholders.On-Going");
			}else{
				return Envoy.getNextEnvoyTime();
			}
		}
		if(placeHolder.equalsIgnoreCase("time_left")){
			if(Envoy.isEnvoyActive()){
				return Envoy.getEnvoyRunTimeLeft();
			}else{
				return Main.settings.getMessages().getString("Messages.Hologram-Placeholders.Not-Running");
			}
		}
		if(placeHolder.equalsIgnoreCase("crates_left")){
			return Envoy.getActiveEnvoys().size() + "";
		}
		return null;
	}
	
}