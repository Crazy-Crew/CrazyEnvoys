package com.badbones69.crazyenvoys.paper.api.interfaces;

import com.badbones69.crazyenvoys.paper.api.objects.misc.Tier;
import org.bukkit.block.Block;

public interface HologramController {
    
    void createHologram(Block block, Tier tier);
    
    void removeHologram(Block block);
    
    void removeAllHolograms();

}