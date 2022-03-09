package com.badbones69.crazyenvoy.multisupport;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

public class WorldGuard_v6 implements WorldGuardVersion {
    
    @Override
    public boolean inRegion(String regionName, Location loc) {
        ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(loc.getWorld()).getApplicableRegions(loc);
        for (ProtectedRegion region : set) {
            if (regionName.equalsIgnoreCase(region.getId())) {
                return true;
            }
        }
        return false;
    }
    
}