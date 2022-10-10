package com.badbones69.crazyenvoys.api.interfaces;

import com.badbones69.crazyenvoys.api.objects.Tier;
import org.bukkit.block.Block;

public interface HologramController {
    
    void createHologram(Block block, Tier tier);
    
    void removeHologram(Block block);
    
    void removeAllHolograms();
}