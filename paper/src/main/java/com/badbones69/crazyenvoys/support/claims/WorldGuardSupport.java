package com.badbones69.crazyenvoys.support.claims;

import com.badbones69.crazyenvoys.support.interfaces.WorldGuardVersion;
import org.bukkit.Location;

public class WorldGuardSupport implements WorldGuardVersion {
    
    @Override
    public boolean inRegion(String regionName, Location loc) {
        /*BukkitWorld world = new BukkitWorld(loc.getWorld());
        BlockVector3 vector = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());

        try {
            RegionManager instance = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);

            if (instance != null) {
                ApplicableRegionSet regionSet = instance.getApplicableRegions(vector);

                for (ProtectedRegion region : regionSet) {
                    if (regionName.equalsIgnoreCase(region.getId())) return true;
                }
            }
        } catch (NullPointerException e) {
            return false;
        }*/

        return false;
    }
}