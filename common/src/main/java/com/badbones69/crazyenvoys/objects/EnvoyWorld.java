package com.badbones69.crazyenvoys.objects;

import org.jspecify.annotations.NonNull;
import us.crazycrew.api.interfaces.IEnvoyWorld;
import us.crazycrew.api.objects.EnvoyLocation;
import java.util.HashMap;
import java.util.Map;

public class EnvoyWorld implements IEnvoyWorld {

    // holds per world locations to spawn in the world, stores x,y,z and the world name.
    // if change is made to database, write to cache.
    // prevent changes if envoy is running as the running envoy relies on this hashmap.
    private final Map<String, EnvoyLocation> active = new HashMap<>();

    private final String world;

    public EnvoyWorld(@NonNull final String world) {
        this.world = world;
    }

    @Override
    public void init() {

    }

    @Override
    public void addLocation(@NonNull final String id, final int x, final int y, final int z) {

    }

    @Override
    public void removeLocation(@NonNull final String id) {
        this.active.remove(id);
    }

    @Override
    public @NonNull final String getWorld() {
        return this.world;
    }
}