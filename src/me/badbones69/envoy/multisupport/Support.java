package me.BadBones69.envoy.multisupport;

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
	
	public static boolean hasMVdWPlaceholderAPI(){
		if(Bukkit.getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI") != null){
			return true;
		}
		return false;
	}
	
	public static boolean hasWorldGuard(){
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null){
			return true;
		}
		return false;
	}
	
	public static boolean hasWorldEdit(){
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") != null){
			return true;
		}
		return false;
	}
	
}