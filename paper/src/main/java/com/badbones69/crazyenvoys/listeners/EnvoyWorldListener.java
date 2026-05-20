package com.badbones69.crazyenvoys.listeners;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.EnvoysPlugin;
import com.badbones69.crazyenvoys.objects.EnvoyWorld;
import com.badbones69.crazyenvoys.registry.EnvoyRegistry;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import us.crazycrew.api.events.types.world.sub.EnvoyWorldLoadEvent;
import us.crazycrew.api.events.types.world.sub.EnvoyWorldUnloadEvent;

public class EnvoyWorldListener implements Listener {

    private final CrazyEnvoys plugin = CrazyEnvoys.get();

    private final EnvoysPlugin envoys = this.plugin.getPlugin();

    private final EnvoyRegistry registry = this.envoys.getEnvoyRegistry();

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        final World origin = event.getWorld();

        final EnvoyWorld world = new EnvoyWorld(origin.getUID(), origin.getKey().toString());

        new EnvoyWorldLoadEvent(world).call();

        world.init();

        this.registry.addWorld(world);
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        final World origin = event.getWorld();

        final EnvoyWorld world = new EnvoyWorld(origin.getUID(), origin.getKey().toString());

        new EnvoyWorldUnloadEvent(world).call();

        this.registry.removeWorld(world.getWorld());
    }
}