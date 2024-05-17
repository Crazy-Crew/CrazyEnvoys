package com.badbones69.crazyenvoys.api.objects.misc.v2;

import org.simpleyaml.configuration.ConfigurationSection;

public class Tier {

    private final TierHologram hologram;

    public Tier(ConfigurationSection section) {
        this.hologram = new TierHologram(
                section.getBoolean("envoy-settings.holograms.toggle", true),
                section.getDouble("envoy-settings.holograms.height", 1.5),
                section.getInt("envoy-settings.holograms.range", 8),
                section.getString("envoy-settings.holograms.color", "transparent"),
                section.getStringList("envoy-settings.holograms.messages")
        );
    }

    public TierHologram getHologram() {
        return this.hologram;
    }
}