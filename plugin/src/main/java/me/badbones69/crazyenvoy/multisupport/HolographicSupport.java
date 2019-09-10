package me.badbones69.crazyenvoy.multisupport;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.badbones69.crazyenvoy.Methods;
import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.FileManager.Files;
import me.badbones69.crazyenvoy.api.objects.Tier;
import org.bukkit.Location;

import java.util.HashMap;

public class HolographicSupport {
	
	private static CrazyEnvoy envoy = CrazyEnvoy.getInstance();
	private static HashMap<Location, Hologram> holograms = new HashMap<>();
	
	public static void registerPlaceHolders() {
		HologramsAPI.registerPlaceholder(envoy.getPlugin(), "{crazyenvoy_cooldown}", 1, () -> {
			if(envoy.isEnvoyActive()) {
				return Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.On-Going");
			}else {
				return envoy.getNextEnvoyTime();
			}
		});
		HologramsAPI.registerPlaceholder(envoy.getPlugin(), "{crazyenvoy_time_left}", 1, () -> {
			if(envoy.isEnvoyActive()) {
				return envoy.getEnvoyRunTimeLeft();
			}else {
				return Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.Not-Running");
			}
		});
		HologramsAPI.registerPlaceholder(envoy.getPlugin(), "{crazyenvoy_crates_left}", .5, () -> envoy.getActiveEnvoys().size() + "");
	}
	
	public static void unregisterPlaceHolders() {
		try {
			HologramsAPI.unregisterPlaceholders(envoy.getPlugin());
		}catch(Exception e) {
		}
	}
	
	public static void createHologram(Location location, Tier tier) {
		double hight = tier.getHoloHight();
		location.setX(location.getBlockX());
		location.setZ(location.getBlockZ());
		Hologram hologram = HologramsAPI.createHologram(envoy.getPlugin(), location.add(.5, hight, .5));
		for(String line : tier.getHoloMessage()) {
			hologram.appendTextLine(Methods.color(line));
		}
		holograms.put(location, hologram);
	}
	
	public static void removeHologram(Location location) {
		if(holograms.containsKey(location)) {
			Hologram hologram = holograms.get(location);
			holograms.remove(location);
			hologram.delete();
		}
	}
	
	public static void removeAllHolograms() {
		for(Location loc : holograms.keySet()) {
			holograms.get(loc).delete();
		}
		holograms.clear();
	}
	
}