package me.badbones69.crazyenvoy.multisupport.holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMILocation;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.interfaces.HologramController;
import me.badbones69.crazyenvoy.api.objects.Tier;
import me.badbones69.crazyenvoy.multisupport.Support;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Random;

public class CMISupport implements HologramController {
	
	private CrazyEnvoy envoy = CrazyEnvoy.getInstance();
	private HashMap<Block, CMIHologram> holos = new HashMap<>();
	private HologramManager hologramManager = new HologramManager((CMI) Support.CMI.getPlugin());
	
	public void createHologram(Block block, Tier tier) {
		CMIHologram hologram = new CMIHologram(new Random().nextInt() + "", new CMILocation(block.getLocation().add(.5, tier.getHoloHight(), .5)));
		hologram.setLines(tier.getHoloMessage());
		hologram.update();
		hologramManager.addHologram(hologram);
		holos.put(block, hologram);
	}
	
	public void removeHologram(Block block) {
		if(holos.containsKey(block)) {
			hologramManager.hideHoloForAllPlayers(holos.get(block));
			hologramManager.removeHolo(holos.get(block));
			holos.remove(block);
		}
	}
	
	public void removeAllHolograms() {
		for(Block block : holos.keySet()) {
			hologramManager.hideHoloForAllPlayers(holos.get(block));
			hologramManager.removeHolo(holos.get(block));
		}
		holos.clear();
	}
	
	public String getPluginName() {
		return "CMI";
	}
	
}