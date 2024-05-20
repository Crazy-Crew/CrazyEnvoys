package com.badbones69.crazyenvoys.api.objects.misc.v2;

import com.badbones69.crazyenvoys.api.objects.misc.v2.records.ChanceSettings;
import com.badbones69.crazyenvoys.api.objects.misc.v2.records.HologramSettings;
import com.ryderbelserion.vital.paper.util.ItemUtil;
import org.bukkit.Material;
import org.simpleyaml.configuration.ConfigurationSection;
import java.util.ArrayList;
import java.util.List;

public class Tier {

    private final List<String> bundles = new ArrayList<>();

    private final boolean permissionToggle;
    private final String permissionValue;

    private final ChanceSettings chanceSettings;

    // Envoy settings
    private final HologramSettings hologram;
    private final Material block;

    public Tier(final ConfigurationSection section) {
        this.bundles.addAll(section.getStringList("bundles"));

        this.permissionToggle = section.getBoolean("claim-permission.toggle", false);
        this.permissionValue = section.getString("claim-permission.value", "");

        this.chanceSettings = new ChanceSettings(section.getBoolean("chance-settings.use-chance", true), section.getInt("chance-settings.max-range", 100), section.getInt("chance-settings.chance", 5));

        final ConfigurationSection settings = section.getConfigurationSection("envoy-settings");

        this.hologram = new HologramSettings(
                settings.getBoolean("holograms.toggle", true),
                settings.getDouble("holograms.height", 1.5),
                settings.getInt("holograms.range", 8),
                settings.getString("holograms.color", "transparent"),
                settings.getStringList("holograms.messages")
        );

        this.block = ItemUtil.getMaterial(settings.getString("block", "chest"));
    }

    public final List<String> getBundles() {
        return this.bundles;
    }

    public final boolean isPermissionToggle() {
        return this.permissionToggle;
    }

    public final String getPermissionValue() {
        return this.permissionValue;
    }

    public final ChanceSettings getChanceSettings() {
        return this.chanceSettings;
    }

    public final HologramSettings getHologram() {
        return this.hologram;
    }

    public final Material getBlock() {
        return this.block;
    }
}