package com.badbones69.crazyenvoy.multisupport;

import com.badbones69.crazyenvoy.api.CrazyManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public enum Support {
    
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    PLACEHOLDER_API("PlaceholderAPI"),
    MVDW_PLACEHOLDER_API("MVdWPlaceholderAPI"),
    WORLD_GUARD("WorldGuard"),
    WORLD_EDIT("WorldEdit"),
    CMI("CMI-Disabled");// Disabled till I can figure out how to make it work.
    
    private final String name;
    
    private Support(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPluginLoaded() {
        if (CrazyManager.getJavaPlugin().getServer().getPluginManager().getPlugin(name) != null) {
            return CrazyManager.getJavaPlugin().isEnabled();
        }
        return false;
    }
}