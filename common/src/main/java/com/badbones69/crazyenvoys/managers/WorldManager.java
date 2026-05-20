package com.badbones69.crazyenvoys.managers;

import com.badbones69.crazyenvoys.CrazyPlugin;
import com.badbones69.crazyenvoys.objects.EnvoyWorld;
import com.badbones69.crazyenvoys.storage.impl.objects.StorageHolder;
import org.jspecify.annotations.NonNull;
import java.util.HashMap;
import java.util.Map;

public class WorldManager {

    private final StorageHolder holder;
    private final CrazyPlugin plugin;

    public WorldManager(@NonNull final CrazyPlugin plugin) {
        this.holder = plugin.getStorageHolder();
        this.plugin = plugin;
    }

    // holds per world settings for quick access like world name, countdown, and location center, read only...
    // if a change is made to the database then write to cache.
    private final Map<String, EnvoyWorld> worlds = new HashMap<>();

    public void addWorld(@NonNull final String world) {

    }

    public void removeWorld(@NonNull final String world) {
        //todo() logic to cancel events in this world.

        this.worlds.remove(world);
    }
}