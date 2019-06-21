package me.badbones69.crazyenvoy.multisupport;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.objects.Tier;
import org.bukkit.Location;

import java.util.HashMap;

public class CMISupport {
	
	private static CrazyEnvoy envoy = CrazyEnvoy.getInstance();
	private static HashMap<Location, CMIHologram> holos = new HashMap<>();
	private static HologramManager hologramManager = new HologramManager((CMI) Support.CMI.getPlugin());
	
	public static void createHologram(Location loc, Tier tier) {
		CMIHologram hologram = new CMIHologram("", loc.add(.5, tier.getHoloHight(), .5));
		hologram.setLines(tier.getHoloMessage());
		hologramManager.addHologram(hologram);
		hologram.update();
		holos.put(loc, hologram);
	}
	
	public static void removeHologram(Location loc) {
		if(holos.containsKey(loc)) {
			CMIHologram hologram = holos.get(loc);
			holos.remove(loc);
			hologramManager.removeHolo(hologram);
		}
	}
	
	public static void removeAllHolograms() {
		for(Location loc : holos.keySet()) {
			hologramManager.removeHolo(holos.get(loc));
		}
		holos.clear();
	}
	
}