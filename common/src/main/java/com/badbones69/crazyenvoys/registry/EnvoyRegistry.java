package com.badbones69.crazyenvoys.registry;

import com.badbones69.crazyenvoys.objects.EnvoyWorld;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.interfaces.registry.IEnvoyRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class EnvoyRegistry implements IEnvoyRegistry<EnvoyWorld> {

    private final Map<UUID, EnvoyWorld> worlds = new HashMap<>();

    @Override
    public void addWorld(@NonNull final EnvoyWorld world) {
        this.worlds.putIfAbsent(world.getWorld(), world);
    }

    @Override
    public void updateWorld(@NonNull final EnvoyWorld world) {
        this.worlds.put(world.getWorld(), world);
    }

    @Override
    public void removeWorld(@NonNull final UUID world) {
        this.worlds.remove(world);
    }

    @Override
    public final boolean hasWorld(@NonNull final UUID world) {
        return this.worlds.containsKey(world);
    }

    @Override
    public @NonNull final Optional<EnvoyWorld> getWorld(@NonNull final UUID world) {
        return Optional.ofNullable(this.worlds.get(world));
    }

    @Override
    public final boolean isEnvoyActive(@NonNull final UUID world) {
        return hasWorld(world) && this.worlds.get(world).isActive();
    }
}