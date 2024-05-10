package com.badbones69.crazyenvoys.api.enums;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public enum PersistentKeys {

    no_firework_damage("firework"),
    envoy_flare("envoy_flare");

    private final @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final String NamespacedKey;

    PersistentKeys(@NotNull final String NamespacedKey) {
        this.NamespacedKey = NamespacedKey;
    }

    public @NotNull final NamespacedKey getNamespacedKey() {
        return new NamespacedKey(this.plugin, this.plugin.getName().toLowerCase() + "_" + this.NamespacedKey);
    }
}