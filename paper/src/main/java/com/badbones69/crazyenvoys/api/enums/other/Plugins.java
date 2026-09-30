package com.badbones69.crazyenvoys.api.enums.other;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public enum Plugins {

    oraxen("Oraxen"),

    nexo("Nexo"),

    items_adder("ItemsAdder"),

    head_database("HeadDatabase"),

    cmi("CMI"),

    fancy_holograms("FancyHolograms"),

    decent_holograms("DecentHolograms"),

    placeholder_api("PlaceholderAPI"),

    worldedit("WorldEdit"),
    worldguard("WorldGuard");

    private final CrazyEnvoys plugin = CrazyEnvoys.get();

    private final Server server = this.plugin.getServer();

    private final PluginManager pluginManager = this.server.getPluginManager();

    private final String name;

    Plugins(@NotNull final String name) {
        this.name = name;
    }

    public final boolean isClassPresent(final String name) {
        boolean isClassPresent = false;

        try {
            Class.forName(name);

            isClassPresent = true;
        } catch (final Exception _) {}

        return isClassPresent;
    }

    public final boolean isEnabled() {
        return this.pluginManager.isPluginEnabled(this.name);
    }

    public @NotNull final String getName() {
        return this.name;
    }
}