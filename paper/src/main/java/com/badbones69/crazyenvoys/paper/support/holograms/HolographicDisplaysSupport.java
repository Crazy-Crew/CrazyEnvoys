package com.badbones69.crazyenvoys.paper.support.holograms;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.paper.api.objects.misc.Tier;
import com.ryderbelserion.cluster.bukkit.utils.LegacyUtils;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class HolographicDisplaysSupport implements HologramController {
    
    private final HashMap<Block, Hologram> holograms = new HashMap<>();

    private final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final HolographicDisplaysAPI api = HolographicDisplaysAPI.get(this.plugin);

    @Override
    public void createHologram(Block block, Tier tier) {
        if (tier.isHoloEnabled()) return;

        double height = tier.getHoloHeight();

        Hologram hologram = this.api.createHologram(block.getLocation().add(.5, height, .5));

        tier.getHoloMessage().forEach(line -> hologram.getLines().appendText(LegacyUtils.color(line)));

        this.holograms.put(block, hologram);
    }

    @Override
    public void removeHologram(Block block) {
        if (!this.holograms.containsKey(block)) return;

        Hologram hologram = this.holograms.get(block);

        this.holograms.remove(block);
        hologram.delete();
    }

    @Override
    public void removeAllHolograms() {
        this.holograms.forEach((key, value) -> value.delete());
        this.holograms.clear();
    }
}