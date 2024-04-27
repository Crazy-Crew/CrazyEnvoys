package com.badbones69.crazyenvoys.support.holograms;

import com.badbones69.crazyenvoys.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.badbones69.crazyenvoys.platform.util.MsgUtil;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.block.Block;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DecentHologramsSupport implements HologramController {
    
    private final Map<Block, Hologram> holograms = new HashMap<>();
    
    public void createHologram(Block block, Tier tier) {
        if (!tier.isHoloEnabled()) return;

        double height = tier.getHoloHeight();

        Hologram hologram = DHAPI.createHologram("CrazyEnvoys-" + UUID.randomUUID(), block.getLocation().add(.5, height, .5));

        hologram.setDisplayRange(tier.getHoloRange());

        tier.getHoloMessage().forEach(line -> DHAPI.addHologramLine(hologram, MsgUtil.color(line)));

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