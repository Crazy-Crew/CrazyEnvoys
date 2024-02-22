package com.badbones69.crazyenvoys.support.holograms;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.other.MsgUtils;
import java.util.HashMap;
import java.util.Map;

public class HolographicDisplaysSupport implements HologramController {

    private final @NotNull CrazyEnvoys plugin = CrazyEnvoys.get();

    private final @NotNull HolographicDisplaysAPI api = HolographicDisplaysAPI.get(this.plugin);

    private final Map<Block, Hologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, Tier tier) {
        if (!tier.isHoloEnabled()) return;

        double height = tier.getHoloHeight();

        Hologram hologram = this.api.createHologram(block.getLocation().add(.5, height, .5));

        tier.getHoloMessage().forEach(line -> hologram.getLines().appendText(MsgUtils.color(line)));

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