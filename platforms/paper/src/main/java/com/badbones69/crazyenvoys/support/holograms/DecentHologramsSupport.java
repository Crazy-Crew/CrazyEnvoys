package com.badbones69.crazyenvoys.support.holograms;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.block.Block;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class DecentHologramsSupport implements HologramController {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final Methods methods = plugin.getMethods();
    
    private final HashMap<Block, Hologram> holograms = new HashMap<>();
    
    public void createHologram(Block block, Tier tier) {
        double height = tier.getHoloHeight();
        Hologram hologram = DHAPI.createHologram(ThreadLocalRandom.current().nextInt() + "", block.getLocation().add(.5, height, .5));

        tier.getHoloMessage().forEach(line -> DHAPI.addHologramLine(hologram, methods.color(line)));

        holograms.put(block, hologram);
    }
    
    public void removeHologram(Block block) {
        if (!holograms.containsKey(block)) return;

        Hologram hologram = holograms.get(block);

        holograms.remove(block);

        hologram.delete();
    }
    
    public void removeAllHolograms() {
        holograms.forEach((key, value) -> value.delete());
        holograms.clear();
    }
}