package me.badbones69.envoy.multisupport;

import org.bukkit.plugin.Plugin;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import me.badbones69.envoy.Main;
import me.badbones69.envoy.api.Envoy;

public class MVdWPlaceholderAPISupport {

	public static void registerPlaceholders(Plugin plugin) {
		PlaceholderAPI.registerPlaceholder(plugin, "envoy_cooldown", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(Envoy.isEnvoyActive()) {
					return Main.settings.getMessages().getString("Messages.Hologram-Placeholders.On-Going");
				}else {
					return Envoy.getNextEnvoyTime();
				}
			}
		});

		PlaceholderAPI.registerPlaceholder(plugin, "envoy_time_left", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				if(Envoy.isEnvoyActive()) {
					return Envoy.getEnvoyRunTimeLeft();
				}else {
					return Main.settings.getMessages().getString("Messages.Hologram-Placeholders.Not-Running");
				}
			}
		});

		PlaceholderAPI.registerPlaceholder(plugin, "envoy_crates_left", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				return Envoy.getActiveEnvoys().size() + "";
			}
		});
	}

}
