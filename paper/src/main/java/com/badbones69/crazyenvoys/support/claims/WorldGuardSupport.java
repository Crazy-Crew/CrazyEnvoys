package com.badbones69.crazyenvoys.support.claims;

import com.badbones69.crazyenvoys.support.interfaces.WorldGuardVersion;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

public class WorldGuardSupport implements WorldGuardVersion {
    
    @Override
    public boolean inRegion(String regionName, Location loc) {
        BukkitWorld world = new BukkitWorld(loc.getWorld());
        BlockVector3 vector = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());

        try {
            ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(vector);

            for (ProtectedRegion region : set) {
                if (regionName.equalsIgnoreCase(region.getId())) return true;
            }
        } catch (NullPointerException e) {
            return false;
        }

        return false;
    }
}