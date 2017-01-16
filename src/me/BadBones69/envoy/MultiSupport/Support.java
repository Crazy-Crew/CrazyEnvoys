package me.BadBones69.envoy.MultiSupport;

import org.bukkit.Bukkit;

public class Support {
	
	public static boolean hasHolographicDisplay(){
		if(Bukkit.getServer().getPluginManager().getPlugin("HolographicDisplays") != null){
			return true;
		}
		return false;
	}
	
	public static boolean hasPlaceholderAPI(){
		if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
			return true;
		}
		return false;
	}
	
}