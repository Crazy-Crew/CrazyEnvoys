package com.badbones69.crazyenvoys.api.enums;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public enum DataKeys {

    NO_FIREWORK_DAMAGE("envoy_firework", Boolean.class);

    private final @NotNull CrazyEnvoys plugin = CrazyEnvoys.get();

    private final String nameSpaceKey;

    DataKeys(String nameSpaceKey, Object dataType) {
        this.nameSpaceKey = nameSpaceKey;
    }

    public NamespacedKey getKey() {
        return new NamespacedKey(this.plugin, this.nameSpaceKey);
    }

    public String getStringKey() {
        return this.nameSpaceKey;
    }
}