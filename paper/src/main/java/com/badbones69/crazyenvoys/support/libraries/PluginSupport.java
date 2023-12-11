package com.badbones69.crazyenvoys.support.libraries;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import org.jetbrains.annotations.NotNull;

public enum PluginSupport {

    DECENT_HOLOGRAMS("DecentHolograms"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays"),
    CMI("CMI"),
    PLACEHOLDER_API("PlaceholderAPI"),
    WORLD_GUARD("WorldGuard"),
    WORLD_EDIT("WorldEdit"),
    ORAXEN("Oraxen"),
    ITEMS_ADDER("ItemsAdder");

    private final String name;

    private final @NotNull CrazyEnvoys plugin = CrazyEnvoys.get();

    PluginSupport(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isPluginEnabled() {
        return this.plugin.getServer().getPluginManager().isPluginEnabled(this.name);
    }
}