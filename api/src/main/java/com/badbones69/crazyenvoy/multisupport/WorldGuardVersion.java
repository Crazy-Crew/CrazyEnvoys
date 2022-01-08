package com.badbones69.crazyenvoy.multisupport;

import org.bukkit.Location;

public interface WorldGuardVersion {
    
    boolean inRegion(String regionName, Location loc);
    
}