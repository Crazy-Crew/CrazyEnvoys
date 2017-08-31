package me.badbones69.crazyenvoy.multisupport;

import org.bukkit.Bukkit;

public enum Support {
	
	HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
	PLACEHOLDER_API("PlaceholderAPI"),
	MVDW_PLACEHOLDER_API("MVdWPlaceholderAPI"),
	WORLD_GUARD("WorldGuard"),
	WORLD_EDIT("WorldEdit");
	
	private String name;
	
	private Support(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Boolean isPluginLoaded() {
		return Bukkit.getServer().getPluginManager().getPlugin(name) != null;
	}
	
}