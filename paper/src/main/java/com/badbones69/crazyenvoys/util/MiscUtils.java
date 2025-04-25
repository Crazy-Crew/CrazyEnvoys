package com.badbones69.crazyenvoys.util;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import org.bukkit.Location;

public class MiscUtils {

    private static final CrazyEnvoys plugin = CrazyEnvoys.get();

    public static String toString(final Location location) {
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ();
    }

    public static boolean isLogging() {
        return plugin.isLogging();
    }
}