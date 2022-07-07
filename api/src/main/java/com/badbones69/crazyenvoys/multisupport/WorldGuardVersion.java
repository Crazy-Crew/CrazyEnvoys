package com.badbones69.crazyenvoys.multisupport;

import org.bukkit.Location;

public interface WorldGuardVersion {
    
    boolean inRegion(String regionName, Location loc);
    
}