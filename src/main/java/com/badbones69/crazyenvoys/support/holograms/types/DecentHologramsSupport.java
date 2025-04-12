package com.badbones69.crazyenvoys.support.holograms.types;

import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.badbones69.crazyenvoys.support.holograms.HologramManager;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;

public class DecentHologramsSupport extends HologramManager {

    private final Map<String, Hologram> holograms = new HashMap<>();

    @Override
    public void createHologram(final Location location, final Tier tier, final String id) {
        if (!tier.isHoloEnabled()) {
            removeHologram(id);

            return;
        }

        // We don't want to create a new one if one already exists.
        if (exists(id)) return;

        final Hologram hologram = DHAPI.createHologram(name(id), location.clone().add(getVector(tier)));

        tier.getHoloMessage().forEach(line -> {
            if (line != null) {
                String coloredLine = color(line);

                if (coloredLine != null) {
                    DHAPI.addHologramLine(hologram, coloredLine);
                }
            }
        });

        this.holograms.putIfAbsent(name(id), hologram);
    }

    @Override
    public void removeHologram(final String id) {
        DHAPI.removeHologram(name(id));
    }

    @Override
    public boolean exists(final String id) {
        return DHAPI.getHologram(name(id)) != null;
    }

    @Override
    public void purge(final boolean isShutdown) {
        this.holograms.forEach((key, value) -> {
            removeHologram(key);

            value.delete();
        });

        this.holograms.clear();
    }

    @Override
    public final String getName() {
        return "DecentHolograms";
    }
}