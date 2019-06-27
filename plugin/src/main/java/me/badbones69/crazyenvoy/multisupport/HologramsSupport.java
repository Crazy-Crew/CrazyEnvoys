package me.badbones69.crazyenvoy.multisupport;

import com.sainttx.holograms.HologramPlugin;
import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.line.TextLine;
import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.objects.Tier;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Random;

public class HologramsSupport {
	
	private static CrazyEnvoy envoy = CrazyEnvoy.getInstance();
	private static HashMap<Location, Hologram> holograms = new HashMap<>();
	private static HologramManager hologramManager = JavaPlugin.getPlugin(HologramPlugin.class).getHologramManager();
	
	public static void createHologram(Location location, Tier tier) {
		double hight = tier.getHoloHight() - .5;//Doing this as Holograms seems to add .5 height when adding lines or something..
		Hologram hologram = new Hologram(new Random().nextInt() + "", location.clone().add(.5, hight, .5));
		for(String line : tier.getHoloMessage()) {
			hologram.addLine(new TextLine(hologram, line));
		}
		hologramManager.addActiveHologram(hologram);
		holograms.put(location.add(.5, tier.getHoloHight(), .5), hologram);
	}
	
	public static void removeHologram(Location location) {
		if(holograms.containsKey(location)) {
			Hologram hologram = holograms.get(location);
			hologramManager.deleteHologram(hologram);
			holograms.remove(location);
		}
	}
	
	public static void removeAllHolograms() {
		for(Location location : holograms.keySet()) {
			Hologram hologram = holograms.get(location);
			hologramManager.deleteHologram(hologram);
		}
		holograms.clear();
	}
	
}