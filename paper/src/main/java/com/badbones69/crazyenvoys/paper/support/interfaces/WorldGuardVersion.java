package com.badbones69.crazyenvoys.paper.support.interfaces;

import org.bukkit.Location;

public interface WorldGuardVersion {
    
    boolean inRegion(String regionName, Location loc);
    
}