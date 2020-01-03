package me.badbones69.crazyenvoy.api.interfaces;

import me.badbones69.crazyenvoy.api.objects.Tier;
import org.bukkit.block.Block;

public interface HologramController {
    
    void createHologram(Block block, Tier tier);
    
    void removeHologram(Block block);
    
    void removeAllHolograms();
    
    String getPluginName();
    
}