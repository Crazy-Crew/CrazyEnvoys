package me.badbones69.crazyenvoy.multisupport;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

public class WorldGuardSupport {
	
	// World Guard 6
	public static boolean inRegion(String regionName, Location loc) {
		ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(loc.getWorld()).getApplicableRegions(loc);
		for(ProtectedRegion region : set) {
			if(regionName.equalsIgnoreCase(region.getId())) {
				return true;
			}
		}
		return false;
	}
	
	//	// World Guard 7
	//	public static boolean inRegion1(String regionName, Location loc) {
	//		BukkitWorld world = new BukkitWorld(loc.getWorld());
	//		BlockVector3 v = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
	//		try {
	//			ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
	//			for(ProtectedRegion region : set) {
	//				if(regionName.equalsIgnoreCase(region.getId())) {
	//					return true;
	//				}
	//			}
	//		}catch(NullPointerException e) {
	//			return false;
	//		}
	//		return false;
	//	}
	
}