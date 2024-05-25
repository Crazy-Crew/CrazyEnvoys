package com.badbones69.crazyenvoys.api;

import ch.jalu.configme.SettingsManager;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.enums.DataFiles;
import com.badbones69.crazyenvoys.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.api.objects.misc.v2.Tier;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.config.impl.ConfigKeys;
import com.badbones69.crazyenvoys.platform.util.MiscUtils;
import com.badbones69.crazyenvoys.support.holograms.types.CMIHologramsSupport;
import com.badbones69.crazyenvoys.support.holograms.types.DecentHologramsSupport;
import com.ryderbelserion.vital.core.config.YamlFile;
import com.ryderbelserion.vital.core.config.YamlManager;
import com.ryderbelserion.vital.paper.enums.Support;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvoyHandler {

    private @NotNull final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);
    private final @NotNull LocationSettings locations = this.plugin.getLocationSettings();
    private @NotNull final YamlManager manager = ConfigManager.getYamlManager();
    private @NotNull final SettingsManager config = ConfigManager.getConfig();

    private HologramController hologram;

    private Calendar envoyTimeLeft;
    private boolean isEnvoyActive = false;
    private Calendar envoyNext;

    public void loadEnvoys() {
        this.locations.clearSpawnLocations();
        this.locations.populateMap();

        YamlFile configuration = DataFiles.data.getYamlFile();

        this.envoyTimeLeft = Calendar.getInstance();

        List<String> failedLocations = this.locations.getFailedLocations();

        if (MiscUtils.isLogging() && !failedLocations.isEmpty()) this.plugin.getLogger().severe("Failed to load " + failedLocations.size() + " locations and will reattempt in 10s.");


    }

    /**
     * Remove all active envoys.
     */
    public void removeEnvoys() {
        this.isEnvoyActive = false;

        for (final Block block : getActiveEnvoys().keySet()) {
            Chunk chunk = block.getChunk();

            if (!chunk.isLoaded()) chunk.load();

            block.setType(Material.AIR);

            stopSignalFlare(block.getLocation());
        }

        if (isHologramPluginEnabled()) this.hologram.removeAllHolograms();

        purgeFallingBlocks();
        purgeActiveEnvoys();
    }

    public void startEnvoy() {

    }

    public void stopEnvoy() {

    }

    /**
     * @return true or false
     */
    public final boolean isEnvoyActive() {
        return this.isEnvoyActive;
    }

    /**
     * Purge all spawn locations and set them to {@link Material#AIR}
     */
    public void purgeLocations() {
        final List<Block> locations = new ArrayList<>(this.locations.getActiveLocations());

        if (this.config.getProperty(ConfigKeys.envoys_random_locations)) {
            locations.addAll(Collections.emptyList());
        } else {
            locations.addAll(this.locations.getSpawnLocations());
        }

        locations.forEach(location -> {
            if (location == null) return;

            Chunk chunk = location.getChunk();

            if (!chunk.isLoaded()) chunk.load();

            location.setType(Material.AIR);

            stopSignalFlare(location.getLocation());
        });

        if (isHologramPluginEnabled()) this.hologram.removeAllHolograms();

        this.locations.clearActiveLocations();
        this.locations.clearDropLocations();

        DataFiles data = DataFiles.data;

        data.getYamlFile().set("Locations.Spawned", new ArrayList<>());
        data.save();
    }

    /**
     * @return the {@link Calendar}
     */
    public final Calendar getEnvoyNext() {
        return this.envoyNext;
    }

    /**
     * Set the calendar instance for the next envoy.
     *
     * @param envoyNext the {@link Calendar}
     */
    public void setEnvoyNext(final Calendar envoyNext) {
        this.envoyNext = envoyNext;
    }

    /**
     * Load hologram implementations.
     */
    public void loadHolograms() {
        if (isHologramPluginEnabled()) {
            this.plugin.getLogger().warning("The hologram implementation is already enabled.");

            return;
        }

        if (Support.decent_holograms.isEnabled()) {
            this.hologram = new DecentHologramsSupport();
        } else if (Support.cmi.isEnabled() && CMIModule.holograms.isEnabled()) {
            this.hologram = new CMIHologramsSupport();
        } else {
            this.plugin.getLogger().warning("No holograms plugin were found. If using CMI, make sure holograms module is enabled.");
        }
    }

    /**
     * @return true or false
     */
    public boolean isHologramPluginEnabled() {
        return this.hologram != null;
    }

    private final Map<Location, ScheduledTask> activeSignals = new HashMap<>();

    /**
     * Starts a signal flare at a specific {@link Location}.
     *
     * @param location the {@link Location}
     * @param tier the {@link Tier}
     */
    public void startSignalFlare(final Location location, final Tier tier) {
        String time = tier.getFlareSettings().time();

        ScheduledTask task = new FoliaRunnable(this.plugin.getServer().getRegionScheduler(), location) {
            @Override
            public void run() {

            }
        }.runAtFixedRate(this.plugin, getTimeSeconds(time) * 20L, getTimeSeconds(time) * 20L);

        this.activeSignals.put(location, task);
    }

    /**
     * Removes a signal flare and cancels the {@link ScheduledTask}.
     *
     * @param location the {@link Location}
     */
    public void stopSignalFlare(final Location location) {
        ScheduledTask task = this.activeSignals.remove(location);

        if (task != null) {
            task.cancel();
        }
    }

    private final Map<Block, Tier> activeEnvoys = new HashMap<>();

    /**
     * Checks if an envoy is active.
     *
     * @param block the {@link Block}
     * @return true or false
     */
    public final boolean isActiveEnvoy(final Block block) {
        return this.activeEnvoys.containsKey(block);
    }

    /**
     * Adds a {@link Block} paired with a {@link Tier} to the map.
     *
     * @param block the {@link Block}
     * @param tier the {@link Tier}
     */
    public void addActiveEnvoy(final Block block, final Tier tier) {
        this.activeEnvoys.put(block, tier);
    }

    /**
     * Removes an envoy from active envoys.
     *
     * @param block the {@link Block}
     */
    public void removeActiveEnvoy(final Block block) {
        this.activeEnvoys.remove(block);
    }

    /**
     * Gets a {@link Tier} from the active envoys.
     *
     * @param block the {@link Block}
     * @return the {@link Tier}
     */
    public final Tier getTierFromEnvoy(final Block block) {
        return this.activeEnvoys.get(block);
    }

    /**
     * @return a map of active envoys
     */
    public final Map<Block, Tier> getActiveEnvoys() {
        return Collections.unmodifiableMap(this.activeEnvoys);
    }

    /**
     * Purge falling blocks
     */
    public void purgeActiveEnvoys() {
        this.activeEnvoys.clear();
    }

    private final Map<Entity, Block> fallingBlocks = new HashMap<>();

    /**
     * Removes an {@link Entity} from a map.
     *
     * @param entity the {@link Entity} to remove from the falling blocks map
     */
    public void removeFallingBlock(final Entity entity) {
        this.fallingBlocks.remove(entity);
    }

    /**
     * Purge falling blocks including the entities.
     */
    public void purgeFallingBlocks() {
        this.fallingBlocks.keySet().forEach(Entity::remove);

        this.fallingBlocks.clear();
    }

    /**
     * @return a map of falling blocks
     */
    public final Map<Entity, Block> getFallingBlocks() {
        return Collections.unmodifiableMap(this.fallingBlocks);
    }

    /**
     * Checks if a {@link Location} is a spawn location.
     *
     * @param location the {@link Location}
     * @return true or false
     */
    public final boolean isLocation(Location location) {
        for (final Block block : this.locations.getSpawnLocations()) {
            if (block.getLocation().equals(location)) return true;
        }

        return false;
    }

    /**
     * Get the current time of an envoy.
     *
     * @param calendar the {@link Calendar}
     */
    private void getEnvoyTime(Calendar calendar) {
        String time = this.config.getProperty(ConfigKeys.envoys_time);

        int hour = Integer.parseInt(time.split(" ")[0].split(":")[0]);
        int min = Integer.parseInt(time.split(" ")[0].split(":")[1]);

        int calender = Calendar.AM;

        if (time.split(" ")[1].equalsIgnoreCase("PM")) calender = Calendar.PM;

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.getTime(); // Without this, the hours do not change for some reason.
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.AM_PM, calender);

        if (calendar.before(Calendar.getInstance())) calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
    }

    /**
     * Converts a {@link String} to an {@link Integer}.
     *
     * @param time the {@link String}
     * @return the {@link Integer}
     */
    private int getTimeSeconds(String time) {
        int seconds = 0;

        for (final String key : time.split(" ")) {
            if (key.contains("d")) {
                seconds += Integer.parseInt(key.replace("d", "")) * 86400;
            } else if (key.contains("h")) {
                seconds += Integer.parseInt(key.replace("h", "")) * 3600;
            } else if (key.contains("m")) {
                seconds += Integer.parseInt(key.replace("m", "")) * 60;
            } else if (key.contains("s")) {
                seconds += Integer.parseInt(key.replace("s", ""));
            }
        }

        return seconds;
    }

    /**
     * Loops through a location and gets a list of blocks
     *
     * @param location the {@link Location}
     * @param radius the radius to check
     * @return a list of blocks
     */
    private List<Block> getBlocksRadius(final Location location, final int radius) {
        Location locations2 = location.clone();

        location.add(-radius, 0, -radius);
        locations2.add(radius, 0, radius);

        final List<Block> locations = new ArrayList<>();

        final int topBlockX = (Math.max(location.getBlockX(), locations2.getBlockX()));
        final int bottomBlockX = (Math.min(location.getBlockX(), locations2.getBlockX()));
        final int topBlockZ = (Math.max(location.getBlockZ(), locations2.getBlockZ()));
        final int bottomBlockZ = (Math.min(location.getBlockZ(), locations2.getBlockZ()));

        if (location.getWorld() != null) {
            for (int x = bottomBlockX; x <= topBlockX; x++) {
                for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                    locations.add(location.getWorld().getHighestBlockAt(x, z));
                }
            }
        }

        return locations;
    }

    /**
     * Convert a {@link List<Block>} to a list of strings.
     *
     * @param list the {@link List<Block>}
     * @return a list of strings
     */
    private List<String> getBlocksFromList(List<Block> list) {
        List<String> strings = new ArrayList<>();

        for (Block block : list) {
            strings.add(MiscUtils.getUnBuiltLocation(block.getLocation()));
        }

        return strings;
    }

    /**
     * Convert a {@link List<String>} to a list of blocks.
     *
     * @param list the {@link List<String>}
     * @return a list of blocks
     */
    private List<Block> getLocationsFromList(List<String> list) {
        List<Block> locations = new ArrayList<>();

        for (String location : list) {
            locations.add(MiscUtils.getBuiltLocation(location).getBlock());
        }

        return locations;
    }
}