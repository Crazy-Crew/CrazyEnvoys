package com.badbones69.crazyenvoy.multisupport;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

public class WorldGuard_v7 implements WorldGuardVersion {
    
    @Override
    public boolean inRegion(String regionName, Location loc) {
        BukkitWorld world = new BukkitWorld(loc.getWorld());
        BlockVector3 v = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
        try {
            ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
            for (ProtectedRegion region : set) {
                if (regionName.equalsIgnoreCase(region.getId())) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }
    
}