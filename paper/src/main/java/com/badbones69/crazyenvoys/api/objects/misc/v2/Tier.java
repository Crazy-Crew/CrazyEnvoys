package com.badbones69.crazyenvoys.api.objects.misc.v2;

import com.badbones69.crazyenvoys.api.objects.misc.v2.records.BulkSettings;
import com.badbones69.crazyenvoys.api.objects.misc.v2.records.ChanceSettings;
import com.badbones69.crazyenvoys.api.objects.misc.v2.records.FireworkSettings;
import com.badbones69.crazyenvoys.api.objects.misc.v2.records.FlareSettings;
import com.badbones69.crazyenvoys.api.objects.misc.v2.records.HologramSettings;
import com.ryderbelserion.vital.paper.util.ItemUtil;
import org.bukkit.Material;
import org.simpleyaml.configuration.ConfigurationSection;
import java.util.ArrayList;
import java.util.List;

public class Tier {

    private final String tierName;

    private final List<String> messages = new ArrayList<>();
    private final List<String> bundles = new ArrayList<>();

    private final boolean requirePermission;
    private final String requiredPermission;

    private final FireworkSettings fireworkSettings;
    private final HologramSettings hologramSettings;
    private final ChanceSettings chanceSettings;
    private final FlareSettings flareSettings;
    private final BulkSettings bulkSettings;

    private final Material block;

    public Tier(final String tierName, final ConfigurationSection section) {
        this.tierName = tierName;

        this.messages.addAll(section.getStringList("messages"));
        this.bundles.addAll(section.getStringList("bundles"));

        this.requirePermission = section.getBoolean("claim-permission.toggle", false);
        this.requiredPermission = section.getString("claim-permission.value", "");

        this.chanceSettings = new ChanceSettings(section.getBoolean("chance-settings.use-chance", true), section.getInt("chance-settings.max-range", 100), section.getInt("chance-settings.chance", 5));

        final ConfigurationSection settings = section.getConfigurationSection("cosmetic-settings");

        this.hologramSettings = new HologramSettings(
                settings.getBoolean("holograms.toggle", true),
                settings.getDouble("holograms.height", 1.5),
                settings.getInt("holograms.range", 8),
                settings.getString("holograms.color", "transparent"),
                settings.getStringList("holograms.messages")
        );

        this.block = ItemUtil.getMaterial(settings.getString("block", "chest"));

        final ConfigurationSection prizes = settings.getConfigurationSection("prizes");

        this.bulkSettings = new BulkSettings(prizes.getBoolean("bulk.toggle", false), prizes.getBoolean("bulk.random", true), prizes.getInt("bulk.max", 3));

        this.fireworkSettings = new FireworkSettings(prizes.getBoolean("fireworks.toggle", true), prizes.getStringList("fireworks.colors"));

        this.flareSettings = new FlareSettings(prizes.getBoolean("signal-flare.toggle", true), prizes.getInt("signal-flare.time", 15), prizes.getStringList("signal-flare.colors"));
    }

    public final FireworkSettings getFireworkSettings() {
        return fireworkSettings;
    }

    public final HologramSettings getHologramSettings() {
        return this.hologramSettings;
    }

    public final ChanceSettings getChanceSettings() {
        return this.chanceSettings;
    }

    public final FlareSettings getFlareSettings() {
        return this.flareSettings;
    }

    public final boolean isPermissionRequired() {
        return this.requirePermission;
    }

    public final String getRequiredPermission() {
        return this.requiredPermission;
    }

    public final BulkSettings getBulkSettings() {
        return this.bulkSettings;
    }

    public final List<String> getMessages() {
        return this.messages;
    }

    public final List<String> getBundles() {
        return this.bundles;
    }

    public final Material getBlock() {
        return this.block;
    }
}