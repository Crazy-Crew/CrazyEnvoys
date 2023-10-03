package com.badbones69.crazyenvoys.paper.api.objects;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.Methods;
import com.badbones69.crazyenvoys.paper.api.FileManager;
import com.badbones69.crazyenvoys.paper.api.FileManager.Files;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LocationSettings {

    private final @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final @NotNull Methods methods = plugin.getMethods();

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
        if (!this.dropLocations.contains(block)) this.dropLocations.add(block);
    }

    /**
     * Removes a drop location.
     * @param block - The block to remove.
     */
    public void removeDropLocation(Block block) {
        this.dropLocations.remove(block);
    }

    /**
     * Clear drop locations.
     */
    public void clearDropLocations() {
        this.dropLocations.clear();
    }

    /**
     * Add a list of drop locations.
     * @param blocks - The list of blocks to add.
     */
    public void addAllDropLocations(List<Block> blocks) {
        this.dropLocations.addAll(blocks);
    }

    /**
     * Fetch all drop locations.
     * @return - The list of drop locations.
     */
    public List<Block> getDropLocations() {
        return this.dropLocations;
    }

    /**
     * Add failed locations.
     * @param location - The location to add.
     */
    public void addFailedLocations(String location) {
        this.failedLocations.add(location);
    }

    /**
     * @return - The list of failed locations.
     */
    public List<String> getFailedLocations() {
        return this.failedLocations;
    }

    /**
     * Add a block to the active locations when an envoy starts.
     * @param block - The block to add.
     */
    public void addActiveLocation(Block block) {
        if (!this.activeLocations.contains(block)) this.activeLocations.add(block);
    }

    /**
     * Remove a block from the active locations.
     * @param block - The block to remove
     */
    public void removeActiveLocation(Block block) {
        this.activeLocations.remove(block);
    }

    /**
     * Clear all active locations.
     */
    public void clearActiveLocations() {
        this.activeLocations.clear();
    }

    /**
     * @return All active blocks.
     */
    public List<Block> getActiveLocations() {
        return this.activeLocations;
    }

    /**
     * Add a spawn location at X block.
     * @param block - The block to add.
     */
    public void addSpawnLocation(Block block) {
        this.spawnLocations.add(block);

        saveLocations();
    }

    public void removeSpawnLocation(Block block) {
        this.spawnLocations.remove(block);
        saveLocations();
    }

    /**
     * Clear spawn locations.
     */
    public void clearSpawnLocations() {
        this.spawnLocations.clear();
    }

    /**
     * @return All spawn locations.
     */
    public List<Block> getSpawnLocations() {
        return this.spawnLocations;
    }

    /**
     * Add all values from the DATA file to spawnLocations.
     */
    public void populateMap() {
        FileConfiguration users = Files.USERS.getFile();

        getSpawnLocations().clear();

        for (String location : users.getStringList("Locations.Spawns")) {
            try {
                getSpawnLocations().add(this.methods.getBuiltLocation(location).getBlock());
            } catch (Exception ignore) {
                addFailedLocations(location);
            }
        }
    }

    public void fixLocations() {
        if (!getFailedLocations().isEmpty()) {
            if (this.plugin.isLogging()) LegacyLogger.warn("Attempting to fix " + getFailedLocations().size() + " locations that failed.");
            int failed = 0;
            int fixed = 0;

            for (String location : getFailedLocations()) {
                try {
                    getSpawnLocations().add(this.methods.getBuiltLocation(location).getBlock());
                    fixed++;
                } catch (Exception ignore) {
                    failed++;
                }
            }

            if (fixed > 0) LegacyLogger.success("Was able to fix " + fixed + " locations that failed.");

            if (failed > 0) LegacyLogger.error("Failed to fix " + failed + " locations and will not reattempt.");
        }
    }

    public void saveLocations() {
        ArrayList<String> locations = new ArrayList<>();

        for (Block block : this.spawnLocations) {
            try {
                locations.add(this.methods.getUnBuiltLocation(block.getLocation()));
            } catch (Exception ignored) {}
        }

        Files.USERS.getFile().set("Locations.Spawns", locations);
        Files.USERS.saveFile();
    }
}