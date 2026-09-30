package com.badbones69.crazyenvoys.support.holograms.types.fancyholograms;

import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.badbones69.crazyenvoys.support.holograms.HologramManager;
import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.HologramRegistry;
import com.fancyinnovations.fancyholograms.api.data.builder.HologramBuilder;
import com.fancyinnovations.fancyholograms.api.data.builder.TextHologramBuilder;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import com.ryderbelserion.fusion.paper.builders.folia.Scheduler;
import org.bukkit.Location;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public class FancyHologramsV3Support extends HologramManager {

    private final HologramRegistry registry = FancyHolograms.get().getRegistry();

    @Override
    public void createHologram(@NotNull final Location location, @NotNull final Tier tier, @NotNull final String id) {
        if (!tier.isHoloEnabled()) {
            removeHologram(id);

            return;
        }

        // We don't want to create a new one if one already exists.
        if (exists(id)) {
            return;
        }

        final HologramBuilder builder = TextHologramBuilder.create(name(id), location.clone().add(getVector(tier)))
                .text(tier.getHoloMessage())
                .visibilityDistance(tier.getHoloRange());

        final Hologram hologram = builder.buildAndRegister();

        final Server server = this.plugin.getServer();

        new FoliaScheduler(this.plugin, Scheduler.async_scheduler) {
            @Override
            public void run() {
                server.getOnlinePlayers().forEach(hologram::updateFor);
            }
        }.runNow();
    }

    @Override
    public void removeHologram(@NotNull final String id) {
        final String identifier = name(id);

        this.registry.get(identifier).ifPresentOrElse(this.registry::unregister, () -> this.fusion.log(Level.WARNING, "No hologram found with id: %s", identifier));
    }

    @Override
    public boolean exists(@NotNull final String id) {
        return this.registry.contains(name(id));
    }

    @Override
    public void purge(final boolean isShutdown) {
        this.registry.getAll().forEach(hologram -> {
            final String id = hologram.getData().getName();

            if (id.startsWith(this.name)) {
                this.registry.unregister(hologram);
            }
        });
    }

    @Override
    public @NotNull final String getName() {
        return "FancyHolograms";
    }
}