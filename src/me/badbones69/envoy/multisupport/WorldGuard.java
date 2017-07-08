package me.BadBones69.envoy.multisupport;

import org.bukkit.Location;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuard {
	
	public static boolean inRegion(String regionName, Location loc){
		ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(loc.getWorld()).getApplicableRegions(loc);
		for(ProtectedRegion region : set){
			if(regionName.equalsIgnoreCase(region.getId())){
				return true;
			}
		}
		return false;
	}
	
}