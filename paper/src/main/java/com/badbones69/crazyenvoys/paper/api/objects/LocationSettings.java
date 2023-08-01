package com.badbones69.crazyenvoys.paper.api.objects;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.Methods;
import com.badbones69.crazyenvoys.paper.api.FileManager;
import com.badbones69.crazyenvoys.paper.api.FileManager.Files;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.ArrayList;
import java.util.List;

public class LocationSettings {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final Methods methods = plugin.getMethods();

    private final List<Block> spawnLocations = new ArrayList<>();

    /**
     * Ryder Note: This used to be "spawnedLocations".
     */
    private final List<Block> activeLocations = new ArrayList<>();

    private final List<String> failedLocations = new ArrayList<>();

    private final List<Block> dropLocations = new ArrayList<>();

    /**
     * Adds a drop location.
     * @param block - The block to add.
     */
    public void addDropLocations(Block block) {
        if (!dropLocations.contains(block)) dropLocations.add(block);
    }

    /**
     * Removes a drop location.
     * @param block - The block to remove.
     */
    public void removeDropLocation(Block block) {
        dropLocations.remove(block);
    }

    /**
     * Clear drop locations.
     */
    public void clearDropLocations() {
        dropLocations.clear();
    }

    /**
     * Add a list of drop locations.
     * @param blocks - The list of blocks to add.
     */
    public void addAllDropLocations(List<Block> blocks) {
        dropLocations.addAll(blocks);
    }

    /**
     * Fetch all drop locations.
     * @return - The list of drop locations.
     */
    public List<Block> getDropLocations() {
        return dropLocations;
    }

    /**
     * Add failed locations.
     * @param location - The location to add.
     */
    public void addFailedLocations(String location) {
        failedLocations.add(location);
    }

    /**
     * @return - The list of failed locations.
     */
    public List<String> getFailedLocations() {
        return failedLocations;
    }

    /**
     * Add a block to the active locations when an envoy starts.
     * @param block - The block to add.
     */
    public void addActiveLocation(Block block) {
        if (!activeLocations.contains(block)) activeLocations.add(block);
    }

    /**
     * Remove a block from the active locations.
     * @param block - The block to remove
     */
    public void removeActiveLocation(Block block) {
        activeLocations.remove(block);
    }

    /**
     * Clear all active locations.
     */
    public void clearActiveLocations() {
        activeLocations.clear();
    }

    /**
     * @return All active blocks.
     */
    public List<Block> getActiveLocations() {
        return activeLocations;
    }

    /**
     * Add a spawn location at X block.
     * @param block - The block to add.
     */
    public void addSpawnLocation(Block block) {
        spawnLocations.add(block);

        saveLocations();
    }

    public void removeSpawnLocation(Block block) {
        spawnLocations.remove(block);
        saveLocations();
    }

    /**
     * Clear spawn locations.
     */
    public void clearSpawnLocations() {
        spawnLocations.clear();
    }

    /**
     * @return All spawn locations.
     */
    public List<Block> getSpawnLocations() {
        return spawnLocations;
    }

    /**
     * Add all values from the DATA file to spawnLocations.
     */
    public void populateMap() {
        FileConfiguration data = Files.DATA.getFile();

        getSpawnLocations().clear();

        for (String location : data.getStringList("Locations.Spawns")) {
            try {
                getSpawnLocations().add(methods.getBuiltLocation(location).getBlock());
            } catch (Exception ignore) {
                addFailedLocations(location);
            }
        }
    }

    public void fixLocations(FileManager fileManager) {
        if (!getFailedLocations().isEmpty()) {
            if (fileManager.isLogging()) plugin.getLogger().info("Attempting to fix " + getFailedLocations().size() + " locations that failed.");
            int failed = 0;
            int fixed = 0;

            for (String location : getFailedLocations()) {
                try {
                    getSpawnLocations().add(methods.getBuiltLocation(location).getBlock());
                    fixed++;
                } catch (Exception ignore) {
                    failed++;
                }
            }

            if (fixed > 0) plugin.getLogger().info("Was able to fix " + fixed + " locations that failed.");

            if (failed > 0) plugin.getLogger().info("Failed to fix " + failed + " locations and will not reattempt.");
        }
    }

    public void saveLocations() {
        ArrayList<String> locations = new ArrayList<>();

        for (Block block : spawnLocations) {
            try {
                locations.add(methods.getUnBuiltLocation(block.getLocation()));
            } catch (Exception ignored) {}
        }

        Files.DATA.getFile().set("Locations.Spawns", locations);
        Files.DATA.saveFile();
    }
}