package com.badbones69.crazyenvoys.api.objects.misc.v2;

import org.simpleyaml.configuration.ConfigurationSection;
import java.util.List;

public class Tier {

    private final List<String> bundles;

    private final boolean claimPermissionToggle;
    private final String claimPermission;

    private final boolean useChance;
    private final int maxRange;
    private final int chance;

    private final String block;

    private final boolean bulkPrizeToggle;
    private final boolean bulkPrizeRandomToggle;
    private final int bulkPrizeMaxAmount;

    private final TierHologram hologram;

    public Tier(ConfigurationSection section) {
        this.bundles = section.getStringList("prizes");

        this.claimPermissionToggle = section.getBoolean("claim-permission.toggle", false);
        this.claimPermission = section.getString("claim.permission.value", "");

        this.useChance = section.getBoolean("chance-settings.use-chance", true);
        this.maxRange = section.getInt("chance-settings.max-range", 100);
        this.chance = section.getInt("chance-settings.chance", 85);

        this.block = section.getString("envoy-settings.block");

        this.bulkPrizeToggle = section.getBoolean("envoy-settings.prizes.bulk.toggle", false);
        this.bulkPrizeRandomToggle = section.getBoolean("envoy-settings.prizes.bulk.random", true);
        this.bulkPrizeMaxAmount = section.getInt("envoy-settings.prizes.bulk.max", 3);

        this.hologram = new TierHologram(
                section.getBoolean("envoy-settings.holograms.toggle", true),
                section.getDouble("envoy-settings.holograms.height", 1.5),
                section.getInt("envoy-settings.holograms.range", 8),
                section.getString("envoy-settings.holograms.color", "transparent"),
                section.getStringList("envoy-settings.holograms.messages")
        );
    }

    public final List<String> getBundles() {
        return this.bundles;
    }

    public TierHologram getHologram() {
        return this.hologram;
    }
}