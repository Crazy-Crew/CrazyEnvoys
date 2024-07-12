package com.badbones69.crazyenvoys.api.enums;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public enum PersistentKeys {

    no_firework_damage("firework"),
    envoy_flare("envoy_flare");

    private final @NotNull CrazyEnvoys plugin = CrazyEnvoys.get();

    private final String NamespacedKey;

    PersistentKeys(String NamespacedKey) {
        this.NamespacedKey = NamespacedKey;
    }

    public NamespacedKey getNamespacedKey() {
        return new NamespacedKey(this.plugin, this.plugin.getName().toLowerCase() + "_" + this.NamespacedKey);
    }
}