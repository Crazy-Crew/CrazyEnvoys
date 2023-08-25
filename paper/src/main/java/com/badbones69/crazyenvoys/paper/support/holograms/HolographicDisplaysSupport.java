package com.badbones69.crazyenvoys.paper.support.holograms;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.paper.api.objects.misc.Tier;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.block.Block;
import java.util.HashMap;

public class HolographicDisplaysSupport implements HologramController {
    
    private final HashMap<Block, Hologram> holograms = new HashMap<>();

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final HolographicDisplaysAPI api = HolographicDisplaysAPI.get(plugin);

    @Override
    public void createHologram(Block block, Tier tier) {
        if (tier.isHoloEnabled()) return;

        double height = tier.getHoloHeight();

        Hologram hologram = api.createHologram(block.getLocation().add(.5, height, .5));

        tier.getHoloMessage().forEach(line -> hologram.getLines().appendText(this.plugin.getMethods().color(line)));

        holograms.put(block, hologram);
    }

    @Override
    public void removeHologram(Block block) {
        if (!holograms.containsKey(block)) return;

        Hologram hologram = holograms.get(block);

        holograms.remove(block);
        hologram.delete();
    }

    @Override
    public void removeAllHolograms() {
        holograms.forEach((key, value) -> value.delete());
        holograms.clear();
    }
}