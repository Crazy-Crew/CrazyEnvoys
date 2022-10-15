package com.badbones69.crazyenvoys.api.objects;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.FileManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.*;

public class LocationSettings {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final List<Block> spawnLocations = new ArrayList<>();
    private final List<Block> activeLocations = new ArrayList<>();

    private final List<String> failedLocations = new ArrayList<>();

    private final FileConfiguration data = FileManager.Files.DATA.getFile();

    /**
     * Clear all Envoy locations.
     */
    public void clearLocations() {
        clearSpawnLocations();
        saveSpawnLocations();
    }

    // Spawn Locations.

    /**
     * Add a spawn block.
     * @param block - The block to add.
     */
    public void addSpawnBlock(Block block) {
        spawnLocations.add(block);
    }

    /**
     * Remove a spawn block
     * @param block - The block to remove.
     */
    public void removeSpawnBlock(Block block) {
        spawnLocations.remove(block);
    }

    /**
     * Add spawn locations.
     */
    public void addSpawnLocations() {
        for (String location : data.getStringList("Locations.Spawns")) {
            try {
                spawnLocations.add(getLocationFromString(location).getBlock());

                System.out.println(spawnLocations.size());

                System.out.println(location);
            } catch (Exception ignore) {
                failedLocations.add(location);
            }
        }
    }

    public void clearSpawnLocations() {
        spawnLocations.clear();
    }

    /**
     * @return All the locations the chests will spawn.
     */
    public List<Block> getSpawnLocations() {
        return spawnLocations;
    }

    /**
     * @param location The location that you want to check.
     */
    public boolean isLocation(Location location) {
        for (Block block : spawnLocations) {
            if (block.getLocation().equals(location)) return true;
        }

        return false;
    }

    /**
     * Save all spawn locations.
     */
    public void saveSpawnLocations() {
        ArrayList<String> locations = new ArrayList<>();

        for (Block block : spawnLocations) {
            try {
                locations.add(getStringFromLocation(block.getLocation()));
            } catch (Exception ignored) {}
        }

        FileManager.Files.DATA.getFile().set("Locations.Spawns", locations);
        FileManager.Files.DATA.saveFile();
    }

    // Active Locations.

    /**
     * Add a location to the cleaning list of where crates actually spawned.
     *
     * @param block block the crate spawned at.
     */
    public void addActiveLocation(Block block) {
        activeLocations.add(block);
    }

    /**
     * @return All the active locations.
     */
    public List<Block> getActiveLocations() {
        return activeLocations;
    }

    /**
     * Clear active locations.
     */
    public void clearActiveLocations() {
        activeLocations.clear();
    }

    // Failed Locations.

    /**
     * Add failed locations to spawn locations.
     */
    public void addFailedSpawnLocations() {
        int failed = 0;
        int fixed = 0;

        for (String location : failedLocations) {
            try {
                spawnLocations.add(getLocationFromString(location).getBlock());
                fixed++;
            } catch (Exception ignore) {
                failed++;
            }
        }

        if (fixed > 0) plugin.getLogger().info("Was able to fix " + fixed + " locations that failed.");

        if (failed > 0) plugin.getLogger().info("Failed to fix " + failed + " locations and will not reattempt.");
    }

    /**
     * @return All failed locations.
     */
    public List<String> getFailedLocations() {
        return failedLocations;
    }

    /**
     * Clear all failed locations.
     */
    public void clearFailedLocations() {
        failedLocations.clear();
    }

    // Utils

    public String getStringFromLocation(Location location) {
        return "World:" + location.getWorld().getName() + ", X:" + location.getBlockX() + ", Y:" + location.getBlockY() + ", Z:" + location.getBlockZ();
    }

    public Location getLocationFromString(String locationString) {
        World w = plugin.getServer().getWorlds().get(0);
        int x = 0;
        int y = 0;
        int z = 0;

        for (String i : locationString.toLowerCase().split(", ")) {
            if (i.startsWith("World:")) {
                w = plugin.getServer().getWorld(i.replace("World:", ""));
            } else if (i.startsWith("X:")) {
                x = Integer.parseInt(i.replace("X:", ""));
            } else if (i.startsWith("Y:")) {
                y = Integer.parseInt(i.replace("Y:", ""));
            } else if (i.startsWith("Z:")) {
                z = Integer.parseInt(i.replace("Z:", ""));
            }
        }

        return new Location(w, x, y, z);
    }
}