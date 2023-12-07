package com.badbones69.crazyenvoys.support.holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.badbones69.crazyenvoys.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import net.Zrips.CMILib.Container.CMILocation;
import org.bukkit.block.Block;
import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("ALL")
public class CMIHologramsSupport implements HologramController {

    private final HashMap<Block, CMIHologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, Tier tier) {
        double height = tier.getHoloHeight();

        CMILocation location = new CMILocation(block.getLocation().add(0.5, height, 0.5));

        CMIHologram hologram = new CMIHologram("CrazyEnvoys-" + UUID.randomUUID(), location);
        hologram.setLines(tier.getHoloMessage());

        CMI.getInstance().getHologramManager().addHologram(hologram);

        this.holograms.put(block, hologram);
    }

    @Override
    public void removeHologram(Block block) {
        if (!this.holograms.containsKey(block)) return;

        CMIHologram hologram = this.holograms.get(block);

        this.holograms.remove(block);

        hologram.remove();
    }

    @Override
    public void removeAllHolograms() {
        this.holograms.forEach((key, value) -> value.remove());
        this.holograms.clear();
    }
}