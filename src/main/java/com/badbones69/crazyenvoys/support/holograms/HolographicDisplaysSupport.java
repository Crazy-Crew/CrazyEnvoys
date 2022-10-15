package com.badbones69.crazyenvoys.support.holograms;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.block.Block;
import java.util.HashMap;

public class HolographicDisplaysSupport implements HologramController {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final Methods methods = plugin.getMethods();

    private final HashMap<Block, Hologram> holograms = new HashMap<>();
    
    public void createHologram(Block block, Tier tier) {
        double height = tier.getHoloHeight();

        Hologram hologram = HologramsAPI.createHologram(plugin, block.getLocation().add(.5, height, .5));

        tier.getHoloMessage().stream().map(methods::color).forEach(hologram :: appendTextLine);

        holograms.put(block, hologram);
    }
    
    public void removeHologram(Block block) {
        if (!holograms.containsKey(block)) return;

        Hologram hologram = holograms.get(block);

        holograms.remove(block);
        hologram.delete();
    }
    
    public void removeAllHolograms() {
        holograms.keySet().forEach(block -> holograms.get(block).delete());
        holograms.clear();
    }
}