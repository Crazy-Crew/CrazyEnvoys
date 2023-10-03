package com.badbones69.crazyenvoys.paper.support.holograms;

import com.badbones69.crazyenvoys.paper.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.paper.api.objects.misc.Tier;
import com.ryderbelserion.cluster.bukkit.utils.LegacyUtils;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.block.Block;
import java.util.HashMap;
import java.util.UUID;

public class DecentHologramsSupport implements HologramController {
    
    private final HashMap<Block, Hologram> holograms = new HashMap<>();
    
    public void createHologram(Block block, Tier tier) {
        double height = tier.getHoloHeight();
        Hologram hologram = DHAPI.createHologram("CrazyEnvoys-" + UUID.randomUUID(), block.getLocation().add(.5, height, .5));

        tier.getHoloMessage().forEach(line -> DHAPI.addHologramLine(hologram, LegacyUtils.color(line)));

        this.holograms.put(block, hologram);
    }
    
    public void removeHologram(Block block) {
        if (!this.holograms.containsKey(block)) return;

        Hologram hologram = this.holograms.get(block);

        this.holograms.remove(block);

        hologram.delete();
    }
    
    public void removeAllHolograms() {
        this.holograms.forEach((key, value) -> value.delete());
        this.holograms.clear();
    }
}