package com.badbones69.crazyenvoys.support;

import com.badbones69.crazyenvoys.CrazyEnvoys;

public enum PluginSupport {
    
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    DECENT_HOLOGRAMS("DecentHolograms"),
    PLACEHOLDER_API("PlaceholderAPI"),
    WORLD_GUARD("WorldGuard"),
    WORLD_EDIT("WorldEdit");

    private final String name;

    private static final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    PluginSupport(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isPluginLoaded() {
        return plugin.getServer().getPluginManager().getPlugin(name) != null;
    }
}