package com.badbones69.crazyenvoys.objects;

import org.jspecify.annotations.NonNull;

public class EnvoyCenter {

    private final String world;
    private final int x;
    private final int y;
    private final int z;

    public EnvoyCenter(@NonNull final String world, final int x, final int y, final int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}