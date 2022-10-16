package com.badbones69.crazyenvoys.api.objects;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.FileManager.Files;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.*;

public class LocationSettings {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    // private final FileConfiguration data = Files.DATA.getFile();

    /**
     * Clear all Envoy locations.
     */
    public void clearLocations() {
        spawnLocations.clear();
        saveSpawnLocations();
    }

    // Active Locations.

    // Locations of spawned envoys.
    private final List<Block> activeLocations = new ArrayList<>();

    /**
     * Add a spawn block.
     * @param block - The block to add.
     */
    public void addActiveLocation(Block block) {
        if (!activeLocations.contains(block)) activeLocations.add(block);
    }

    /**
     * Remove a spawn block
     * @param block - The block to remove.
     */
    public void removeActiveLocation(Block block) {
        activeLocations.remove(block);
    }

    public List<Block> getActiveLocations() {
        return activeLocations;
    }

    // Spawn Locations
    private final List<Block> spawnLocations = new ArrayList<>();

    private final List<String> failedLocations = new ArrayList<>();


    public void addSpawnLocation(Block block) {
        spawnLocations.add(block);
        saveSpawnLocations();
    }

    public void removeSpawnLocation(Block block) {
        if (isLocation(block.getLocation())) {
            spawnLocations.remove(block);
            saveSpawnLocations();
        }
    }

    public List<Block> getSpawnLocations() {
        return spawnLocations;
    }

    public List<String> getFailedLocations() {
        return failedLocations;
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

    public void saveSpawnLocations() {
        ArrayList<String> locations = new ArrayList<>();

        for (Block block : spawnLocations) {
            try {
                locations.add(getLocation(block.getLocation()));
            } catch (Exception ignored) {}
        }

        Files.DATA.getFile().set("Locations.Spawns", locations);
        Files.DATA.saveFile();
    }

    // Utils

    public String getLocation(Location location) {
        return "World:" + location.getWorld().getName() + ", X:" + location.getBlockX() + ", Y:" + location.getBlockY() + ", Z:" + location.getBlockZ();
    }

    public Location getBlockLocation(String locationString) {
        World world = plugin.getServer().getWorlds().get(0);
        int x = 0;
        int y = 0;
        int z = 0;

        for (String i : locationString.toLowerCase().split(", ")) {
            if (i.startsWith("World:")) {
                world = plugin.getServer().getWorld(i.replace("World:", ""));
            } else if (i.startsWith("X:")) {
                x = Integer.parseInt(i.replace("X:", ""));
            } else if (i.startsWith("Y:")) {
                y = Integer.parseInt(i.replace("Y:", ""));
            } else if (i.startsWith("Z:")) {
                z = Integer.parseInt(i.replace("Z:", ""));
            }
        }

        return new Location(world, x, y, z);
    }
}