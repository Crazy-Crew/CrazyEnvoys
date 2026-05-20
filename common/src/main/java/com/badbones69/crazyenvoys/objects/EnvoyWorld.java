package com.badbones69.crazyenvoys.objects;

import com.badbones69.crazyenvoys.EnvoysPlugin;
import com.badbones69.crazyenvoys.storage.impl.objects.StorageHolder;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.CrazyEnvoys;
import us.crazycrew.api.interfaces.IEnvoyWorld;
import us.crazycrew.api.objects.EnvoyLocation;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnvoyWorld implements IEnvoyWorld {

    private final EnvoysPlugin plugin = (EnvoysPlugin) CrazyEnvoys.Provider.getInstance();

    private final StorageHolder holder = this.plugin.getStorageHolder();

    // holds per world locations to spawn in the world, stores x,y,z and the world name.
    // if change is made to database, write to cache.
    // prevent changes if envoy is running as the running envoy relies on this hashmap.
    private final Map<String, EnvoyLocation> active = new HashMap<>();

    private final UUID world;
    private final String name;

    public EnvoyWorld(@NonNull final UUID world, @NonNull final String name) {
        this.world = world;
        this.name = name;
    }

    private String countdown = "0000000000000";

    @Override
    public void init() {
        if (this.holder.hasWorld(this)) {
            this.holder.populate(this);

            return;
        }

        this.holder.addWorld(this);
    }

    @Override
    public void addLocation(@NonNull final String id, final int x, final int y, final int z) {
        this.active.put(id, new EnvoyLocation(x, y, z));
    }

    @Override
    public void removeLocation(@NonNull final String id) {
        this.active.remove(id);
    }

    @Override
    public void setCountdown(@NonNull final String countdown) {
        this.countdown = countdown;
    }

    @Override
    public @NonNull final String getCountdown() {
        return this.countdown;
    }

    @Override
    public @NonNull final String getWorldAsString() {
        return getWorld().toString();
    }

    @Override
    public @NonNull final String getWorldName() {
        return this.name;
    }

    @Override
    public @NonNull final UUID getWorld() {
        return this.world;
    }
}