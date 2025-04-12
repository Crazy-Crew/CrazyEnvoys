package com.badbones69.crazyenvoys.support.holograms.types;

import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.badbones69.crazyenvoys.support.holograms.HologramManager;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.Server;
import java.util.ArrayList;
import java.util.List;

public class FancyHologramsSupport extends HologramManager {

    private final de.oliver.fancyholograms.api.HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

    @Override
    public void createHologram(final Location location, final Tier tier, final String id) {
        if (!tier.isHoloEnabled()) {
            removeHologram(id);

            return;
        }

        // We don't want to create a new one if one already exists.
        if (exists(id)) return;

        TextHologramData hologramData = new TextHologramData(name(id), location.clone().add(getVector(tier)));

        hologramData.setText(tier.getHoloMessage());

        final Hologram hologram = this.manager.create(hologramData);

        hologram.createHologram();

        final Server server = this.plugin.getServer();

        new FoliaRunnable(this.plugin.getServer().getGlobalRegionScheduler()) {
            @Override
            public void run() {
                server.getOnlinePlayers().forEach(hologram::updateShownStateFor);
            }
        }.run(this.plugin);

        this.manager.addHologram(hologram);
    }

    @Override
    public void removeHologram(final String id) {
        final Hologram hologram = this.manager.getHologram(name(id)).orElse(null);

        if (hologram == null) return;

        FancyHologramsPlugin.get().getHologramThread().submit(() -> this.manager.removeHologram(hologram));
    }

    @Override
    public boolean exists(final String id) {
        return this.manager.getHologram(name(id)).orElse(null) != null;
    }

    @Override
    public void purge(final boolean isShutdown) {
        final String name = this.plugin.getName().toLowerCase();

        final List<String> holograms = new ArrayList<>() {{
            manager.getHolograms().forEach(hologram -> {
                final String id = hologram.getName();

                if (id.startsWith(name + "-")) {
                    add(id.replace(name + "-", ""));
                }
            });
        }};

        holograms.forEach(this::removeHologram);
    }

    @Override
    public final String getName() {
        return "FancyHolograms";
    }
}