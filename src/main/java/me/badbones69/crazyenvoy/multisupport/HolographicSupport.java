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
	private static HashMap<Location, Hologram> holos = new HashMap<>();
	
	public static void registerPlaceHolders() {
		HologramsAPI.registerPlaceholder(envoy.getPlugin(), "{envoy_cooldown}", 1, () -> {
			if(envoy.isEnvoyActive()) {
				return Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.On-Going");
			}else {
				return envoy.getNextEnvoyTime();
			}
		});
		
		HologramsAPI.registerPlaceholder(envoy.getPlugin(), "{envoy_time_left}", 1, () -> {
			if(envoy.isEnvoyActive()) {
				return envoy.getEnvoyRunTimeLeft();
			}else {
				return Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.Not-Running");
			}
		});
		
		HologramsAPI.registerPlaceholder(envoy.getPlugin(), "{envoy_crates_left}", .5, () -> envoy.getActiveEnvoys().size() + "");
		
	}
	
	public static void unregisterPlaceHolders() {
		try {
			HologramsAPI.unregisterPlaceholders(envoy.getPlugin());
		}catch(Exception e) {
		}
	}
	
	public static void createHologram(Location loc, Tier tier) {
		double hight = tier.getHoloHight();
		Hologram hg = HologramsAPI.createHologram(envoy.getPlugin(), loc.add(.5, hight, .5));
		for(String line : tier.getHoloMessage()) {
			hg.appendTextLine(Methods.color(line));
		}
		holos.put(loc, hg);
	}
	
	public static void removeHologram(Location loc) {
		if(holos.containsKey(loc)) {
			Hologram hg = holos.get(loc);
			holos.remove(loc);
			hg.delete();
		}
	}
	
	public static void removeAllHolograms() {
		for(Location loc : holos.keySet()) {
			holos.get(loc).delete();
		}
		holos.clear();
	}
	
}