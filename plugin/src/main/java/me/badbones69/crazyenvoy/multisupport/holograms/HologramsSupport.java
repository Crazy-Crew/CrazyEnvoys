package me.badbones69.crazyenvoy.multisupport.holograms;

import com.sainttx.holograms.HologramPlugin;
import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.line.TextLine;
import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.interfaces.HologramController;
import me.badbones69.crazyenvoy.api.objects.Tier;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Random;

public class HologramsSupport implements HologramController {
    
    private CrazyEnvoy envoy = CrazyEnvoy.getInstance();
    private HashMap<Block, Hologram> holograms = new HashMap<>();
    private HologramManager hologramManager = JavaPlugin.getPlugin(HologramPlugin.class).getHologramManager();
    
    public void createHologram(Block block, Tier tier) {
        double hight = tier.getHoloHight() - .5;//Doing this as Holograms seems to add .5 height when adding lines or something..
        Hologram hologram = new Hologram(new Random().nextInt() + "", block.getLocation().add(.5, hight, .5));
        for (String line : tier.getHoloMessage()) {
            hologram.addLine(new TextLine(hologram, line));
        }
        hologramManager.addActiveHologram(hologram);
        holograms.put(block, hologram);
    }
    
    public void removeHologram(Block block) {
        if (holograms.containsKey(block)) {
            Hologram hologram = holograms.get(block);
            hologramManager.deleteHologram(hologram);
            holograms.remove(block);
        }
    }
    
    public void removeAllHolograms() {
        for (Block block : holograms.keySet()) {
            Hologram hologram = holograms.get(block);
            hologramManager.deleteHologram(hologram);
        }
        holograms.clear();
    }
    
    public String getPluginName() {
        return "Holograms";
    }
    
}