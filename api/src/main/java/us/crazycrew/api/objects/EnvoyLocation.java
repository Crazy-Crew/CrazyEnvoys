package us.crazycrew.api.objects;

import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class EnvoyLocation {

    private final UUID world;
    private final int x;
    private final int y;
    private final int z;

    public EnvoyLocation(@NonNull final UUID world, final int x, final int y, final int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public @NonNull final UUID getWorld() {
        return this.world;
    }

    public final int getX() {
        return this.x;
    }

    public final int getY() {
        return this.y;
    }

    public final int getZ() {
        return this.z;
    }
}