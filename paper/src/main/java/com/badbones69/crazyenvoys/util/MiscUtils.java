package com.badbones69.crazyenvoys.util;

import org.bukkit.Location;

public class MiscUtils {

    public static String toString(final Location location) {
        return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
    }
}