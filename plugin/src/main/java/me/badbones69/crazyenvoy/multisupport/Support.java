package me.badbones69.crazyenvoy.multisupport;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public enum Support {
    
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    HOLOGRAMS("Holograms"),
    PLACEHOLDER_API("PlaceholderAPI"),
    MVDW_PLACEHOLDER_API("MVdWPlaceholderAPI"),
    WORLD_GUARD("WorldGuard"),
    WORLD_EDIT("WorldEdit"),
    CMI("CMI-Disabled");// Disabled till I can figure out how to make it work.
    
    private String name;
    
    private Support(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public Plugin getPlugin() {
        return Bukkit.getServer().getPluginManager().getPlugin(name);
    }
    
    public boolean isPluginLoaded() {
        if (Bukkit.getServer().getPluginManager().getPlugin(name) != null) {
            return Bukkit.getPluginManager().getPlugin(name).isEnabled();
        }
        return false;
    }
    
}