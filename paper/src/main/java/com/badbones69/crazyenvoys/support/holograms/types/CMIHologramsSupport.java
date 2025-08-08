package com.badbones69.crazyenvoys.support.holograms.types;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Display.CMIBillboard;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.badbones69.crazyenvoys.support.holograms.HologramManager;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import com.ryderbelserion.fusion.paper.scheduler.Scheduler;
import net.Zrips.CMILib.Container.CMILocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class CMIHologramsSupport extends HologramManager {

    private final com.Zrips.CMI.Modules.Holograms.HologramManager hologramManager = CMI.getInstance().getHologramManager();

    @Override
    public void createHologram(final Location location, final Tier tier, final String id) {
        if (!tier.isHoloEnabled()) {
            removeHologram(id);

            return;
        }

        // We don't want to create a new one if one already exists.
        if (exists(id)) return;

        final CMIHologram hologram = new CMIHologram(name(id), new CMILocation(location.clone().add(getVector(tier))));

        hologram.setNewDisplayMethod(true);
        hologram.setBillboard(CMIBillboard.CENTER);

        hologram.setLines(lines(tier));

        this.hologramManager.addHologram(hologram);

        new FoliaScheduler(this.plugin, Scheduler.global_scheduler) {
            @Override
            public void run() {
                location.getNearbyEntitiesByType(Player.class, 5).forEach(player -> hologramManager.handleHoloUpdates(player, hologram.getLocation()));
            }
        }.runNow();
    }

    @Override
    public void removeHologram(final String id) {
        final CMIHologram hologram = this.hologramManager.getByName(name(id));

        if (hologram != null) {
            hologram.remove();
        }
    }

    @Override
    public boolean exists(final String id) {
        return this.hologramManager.getByName(name(id)) != null;
    }

    @Override
    public void purge(final boolean isShutdown) {
        final String name = this.plugin.getName().toLowerCase();

        final List<String> holograms = new ArrayList<>() {{
            hologramManager.getHolograms().forEach((id, hologram) -> {
                if (id.startsWith(name + "-")) {
                    add(id.replace(name + "-", ""));
                }
            });
        }};

        holograms.forEach(this::removeHologram);
    }

    @Override
    public final String getName() {
        return "CMI";
    }
}