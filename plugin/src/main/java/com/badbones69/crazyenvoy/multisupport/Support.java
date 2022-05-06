package com.badbones69.crazyenvoy.multisupport;

import com.badbones69.crazyenvoy.CrazyEnvoy;

public enum Support {
    
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    DECENT_HOLOGRAMS("DecentHolograms"),
    PLACEHOLDER_API("PlaceholderAPI"),
    MVDW_PLACEHOLDER_API("MVdWPlaceholderAPI"),
    WORLD_GUARD("WorldGuard"),
    WORLD_EDIT("WorldEdit"),
    CMI("CMI-Disabled");// Disabled till I can figure out how to make it work.
    
    private final String name;
    
    Support(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPluginLoaded() {
        if (CrazyEnvoy.getJavaPlugin().getServer().getPluginManager().getPlugin(name) != null) {
            return CrazyEnvoy.getJavaPlugin().isEnabled();
        }
        return false;
    }
}