package com.badbones69.crazyenvoys.multisupport;

import com.badbones69.crazyenvoys.api.CrazyManager;

public enum Support {
    
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    DECENT_HOLOGRAMS("DecentHolograms"),
    PLACEHOLDER_API("PlaceholderAPI"),
    MVDW_PLACEHOLDER_API("MVdWPlaceholderAPI"),
    WORLD_GUARD("WorldGuard"),
    WORLD_EDIT("WorldEdit"),
    CMI("CMI-Disabled"); // Disabled till I can figure out how to make it work.
    
    private final String name;

    private final CrazyManager crazyManager = CrazyManager.getInstance();
    
    Support(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isPluginLoaded() {
        if (crazyManager.getPlugin().getServer().getPluginManager().getPlugin(name) != null) return crazyManager.getPlugin().isEnabled();

        return false;
    }
}