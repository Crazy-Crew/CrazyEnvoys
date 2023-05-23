package com.badbones69.crazyenvoys.support.holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.badbones69.crazyenvoys.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import org.bukkit.block.Block;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("ALL")
public class CMIHologramsSupport implements HologramController {

    private final HashMap<Block, CMIHologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, Tier tier) {
        double height = tier.getHoloHeight();
        CMIHologram hologram = new CMIHologram(ThreadLocalRandom.current().nextInt() + "", block.getLocation().add(.5, height, .5));

        hologram.setLines(tier.getHoloMessage());

        CMI.getInstance().getHologramManager().addHologram(hologram);

        holograms.put(block, hologram);
    }

    @Override
    public void removeHologram(Block block) {
        if (!holograms.containsKey(block)) return;

        CMIHologram hologram = holograms.get(block);

        holograms.remove(block);

        hologram.remove();
    }

    @Override
    public void removeAllHolograms() {
        holograms.forEach((key, value) -> value.remove());
        holograms.clear();
    }
}