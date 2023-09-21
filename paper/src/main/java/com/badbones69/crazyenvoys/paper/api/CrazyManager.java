package com.badbones69.crazyenvoys.paper.api;

import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.Methods;
import com.badbones69.crazyenvoys.paper.api.FileManager.CustomFile;
import com.badbones69.crazyenvoys.paper.api.FileManager.Files;
import com.badbones69.crazyenvoys.paper.api.enums.Translation;
import com.badbones69.crazyenvoys.paper.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.paper.api.events.EnvoyEndEvent.EnvoyEndReason;
import com.badbones69.crazyenvoys.paper.api.events.EnvoyStartEvent;
import com.badbones69.crazyenvoys.paper.api.events.EnvoyStartEvent.EnvoyStartReason;
import com.badbones69.crazyenvoys.paper.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.paper.api.objects.CoolDownSettings;
import com.badbones69.crazyenvoys.paper.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.paper.api.objects.EnvoySettings;
import com.badbones69.crazyenvoys.paper.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenvoys.paper.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.paper.api.objects.misc.Prize;
import com.badbones69.crazyenvoys.paper.api.objects.misc.Tier;
import com.badbones69.crazyenvoys.paper.controllers.CountdownTimer;
import com.badbones69.crazyenvoys.paper.controllers.FireworkDamageAPI;
import com.badbones69.crazyenvoys.paper.support.holograms.CMIHologramsSupport;
import com.badbones69.crazyenvoys.paper.support.holograms.HolographicDisplaysSupport;
import com.badbones69.crazyenvoys.paper.support.libraries.PluginSupport;
import com.badbones69.crazyenvoys.paper.support.claims.WorldGuardSupport;
import com.badbones69.crazyenvoys.paper.support.holograms.DecentHologramsSupport;
import com.ryderbelserion.cluster.bukkit.items.utils.DyeUtils;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import com.ryderbelserion.cluster.bukkit.utils.LegacyUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.common.config.types.Config;
import us.crazycrew.crazyenvoys.paper.support.MetricsHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class CrazyManager {

    private final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final FileManager fileManager = this.plugin.getFileManager();

    private final Methods methods = plugin.getMethods();

    private final EnvoySettings envoySettings = plugin.getEnvoySettings();
    private final FlareSettings flareSettings = plugin.getFlareSettings();
    private final EditorSettings editorSettings = plugin.getEditorSettings();
    private final CoolDownSettings coolDownSettings = plugin.getCoolDownSettings();
    private final LocationSettings locationSettings = plugin.getLocationSettings();

    private final FireworkDamageAPI fireworkDamageAPI = plugin.getFireworkDamageAPI();
    private CountdownTimer countdownTimer;

    private BukkitTask runTimeTask;
    private BukkitTask coolDownTask;
    private Calendar nextEnvoy;
    private Calendar envoyTimeLeft;
    private boolean envoyActive = false;
    private boolean autoTimer = true;
    private WorldGuardSupport worldGuardSupportVersion;
    private HologramController hologramController;
    private Location center;
    private String centerString;

    private final HashMap<Block, Tier> activeEnvoys = new HashMap<>();
    private final HashMap<Location, BukkitTask> activeSignals = new HashMap<>();

    private final HashMap<Entity, Block> fallingBlocks = new HashMap<>();

    private final List<Tier> tiers = new ArrayList<>();
    private final List<Tier> cachedChances = new ArrayList<>();
    private final List<Material> blacklistedBlocks = new ArrayList<>();
    private final List<UUID> ignoreMessages = new ArrayList<>();
    private final List<Calendar> warnings = new ArrayList<>();

    private final Random random = new Random();

    /**
     * Run this when you are starting up the server.
     */
    public void load(boolean serverStart) {
        if (serverStart) {

        }

        loadEnvoys();
    }

    private void getEnvoyTime(Calendar cal) {
        String time = envoySettings.getEnvoyClockTime();
        int hour = Integer.parseInt(time.split(" ")[0].split(":")[0]);
        int min = Integer.parseInt(time.split(" ")[0].split(":")[1]);
        int calender = Calendar.AM;

        if (time.split(" ")[1].equalsIgnoreCase("PM")) calender = Calendar.PM;

        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.getTime(); // Without this makes the hours not change for some reason.
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.AM_PM, calender);

        if (cal.before(Calendar.getInstance())) cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
    }

    /**
     * Run this when you need to reload the plugin or shut it down.
     */
    public void reload(boolean serverStop) {
        MetricsHandler metricsHandler = this.plugin.getCrazyHandler().getMetrics();

        if (serverStop) {
            metricsHandler.stop();

            removeAllEnvoys();

            Files.DATA.getFile().set("Next-Envoy", getNextEnvoy().getTimeInMillis());
            Files.DATA.saveFile();

            locationSettings.clearSpawnLocations();

            coolDownSettings.clearCoolDowns();

            return;
        }

        this.plugin.getCrazyHandler().getConfigManager().reload();

        boolean metrics = this.plugin.getCrazyHandler().getConfigManager().getConfig().getProperty(Config.toggle_metrics);

        if (metrics) {
            metricsHandler.start();
        } else {
            metricsHandler.stop();
        }

        removeAllEnvoys();

        Files.DATA.getFile().set("Next-Envoy", getNextEnvoy().getTimeInMillis());
        Files.DATA.saveFile();

        locationSettings.clearSpawnLocations();

        coolDownSettings.clearCoolDowns();

        loadEnvoys();
    }

    private void loadEnvoys() {
        // Clear all spawn locations.
        this.locationSettings.clearSpawnLocations();

        this.blacklistedBlocks.clear();
        this.envoySettings.loadSettings();
        this.cachedChances.clear();
        this.envoySettings.loadSettings();
        FileConfiguration data = Files.DATA.getFile();
        this.envoyTimeLeft = Calendar.getInstance();

        // Populate the array list.
        this.locationSettings.populateMap();

        if (this.plugin.isLogging() && !this.locationSettings.getFailedLocations().isEmpty()) LegacyLogger.error("Failed to load " + this.locationSettings.getFailedLocations().size() + " locations and will reattempt in 10s.");

        if (Calendar.getInstance().after(getNextEnvoy())) setEnvoyActive(false);

        loadCenter();

        if (this.envoySettings.isEnvoyRunTimerEnabled()) {
            Calendar cal = Calendar.getInstance();

            if (this.envoySettings.isEnvoyCooldownEnabled()) {
                this.autoTimer = true;
                cal.setTimeInMillis(data.getLong("Next-Envoy"));

                if (Calendar.getInstance().after(cal)) cal.setTimeInMillis(getEnvoyCooldown().getTimeInMillis());
            } else {
                this.autoTimer = false;
                getEnvoyTime(cal);
            }

            this.nextEnvoy = cal;
            startEnvoyCountDown();
            resetWarnings();
        } else {
            this.nextEnvoy = Calendar.getInstance();
        }

        //================================== Tiers Load ==================================//
        this.tiers.clear();

        for (CustomFile tierFile : this.fileManager.getCustomFiles()) {
            Tier tier = new Tier(tierFile.getName());
            FileConfiguration file = tierFile.getFile();
            tier.setClaimPermissionToggle(file.getBoolean("Settings.Claim-Permission"));
            tier.setClaimPermission(file.getString("Settings.Claim-Permission-Name"));
            tier.setUseChance(file.getBoolean("Settings.Use-Chance"));
            tier.setSpawnChance(file.getInt("Settings.Spawn-Chance"));
            tier.setBulkToggle(file.getBoolean("Settings.Bulk-Prizes.Toggle"));
            tier.setBulkRandom(file.getBoolean("Settings.Bulk-Prizes.Random"));
            tier.setBulkMax(file.getInt("Settings.Bulk-Prizes.Max-Bulk"));
            tier.setHoloToggle(file.getBoolean("Settings.Hologram-Toggle"));
            tier.setHoloHeight(file.getDouble("Settings.Hologram-Height", 1.5));
            tier.setHoloMessage(file.getStringList("Settings.Hologram"));
            ItemBuilder placedBlock = new ItemBuilder().setMaterial(file.getString("Settings.Placed-Block"));
            tier.setPlacedBlockMaterial(placedBlock.getMaterial());
            tier.setPlacedBlockMetaData(placedBlock.getDamage());

            tier.setFireworkToggle(file.getBoolean("Settings.Firework-Toggle"));

            if (file.getStringList("Settings.Firework-Colors").isEmpty()) {
                tier.setFireworkColors(Arrays.asList(Color.GRAY, Color.BLACK, Color.ORANGE));
            } else {
                file.getStringList("Settings.Firework-Colors").forEach(color -> tier.addFireworkColor(DyeUtils.getColor(color)));
            }

            if (file.contains("Settings.Prize-Message") && !file.getStringList("Settings.Prize-Message").isEmpty()) {
                tier.setPrizeMessage(file.getStringList("Settings.Prize-Message"));
            }

            tier.setSignalFlareToggle(file.getBoolean("Settings.Signal-Flare.Toggle"));
            tier.setSignalFlareTimer(file.getString("Settings.Signal-Flare.Time"));

            if (file.getStringList("Settings.Signal-Flare.Colors").isEmpty()) {
                tier.setSignalFlareColors(Arrays.asList(Color.GRAY, Color.BLACK, Color.ORANGE));
            } else {
                file.getStringList("Settings.Signal-Flare.Colors").forEach(color -> tier.addSignalFlareColor(DyeUtils.getColor(color)));
            }

            for (String prizeID : file.getConfigurationSection("Prizes").getKeys(false)) {
                String path = "Prizes." + prizeID + ".";
                int chance = file.getInt(path + "Chance");
                String displayName = file.contains(path + "DisplayName") ? file.getString(path + "DisplayName") : "";
                List<String> commands = file.getStringList(path + "Commands");
                List<String> messages = file.getStringList(path + "Messages");
                boolean dropItems = file.getBoolean(path + "Drop-Items");
                List<ItemBuilder> items = ItemBuilder.convertStringList(file.getStringList(path + "Items"));
                tier.addPrize(new Prize(prizeID).setDisplayName(displayName).setChance(chance).setDropItems(dropItems).setItemBuilders(items).setCommands(commands).setMessages(messages));
            }

            this.tiers.add(tier);
            cleanLocations();

            // Loading the blacklisted blocks.
            this.blacklistedBlocks.add(Material.WATER);
            this.blacklistedBlocks.add(Material.LILY_PAD);
            this.blacklistedBlocks.add(Material.LAVA);
            this.blacklistedBlocks.add(Material.CHORUS_PLANT);
            this.blacklistedBlocks.add(Material.KELP_PLANT);
            this.blacklistedBlocks.add(Material.TALL_GRASS);
            this.blacklistedBlocks.add(Material.CHORUS_FLOWER);
            this.blacklistedBlocks.add(Material.SUNFLOWER);
            this.blacklistedBlocks.add(Material.IRON_BARS);
            this.blacklistedBlocks.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
            this.blacklistedBlocks.add(Material.IRON_TRAPDOOR);
            this.blacklistedBlocks.add(Material.OAK_TRAPDOOR);
            this.blacklistedBlocks.add(Material.OAK_FENCE);
            this.blacklistedBlocks.add(Material.OAK_FENCE_GATE);
            this.blacklistedBlocks.add(Material.ACACIA_FENCE);
            this.blacklistedBlocks.add(Material.BIRCH_FENCE);
            this.blacklistedBlocks.add(Material.DARK_OAK_FENCE);
            this.blacklistedBlocks.add(Material.JUNGLE_FENCE);
            this.blacklistedBlocks.add(Material.NETHER_BRICK_FENCE);
            this.blacklistedBlocks.add(Material.SPRUCE_FENCE);
            this.blacklistedBlocks.add(Material.ACACIA_FENCE_GATE);
            this.blacklistedBlocks.add(Material.BIRCH_FENCE_GATE);
            this.blacklistedBlocks.add(Material.DARK_OAK_FENCE_GATE);
            this.blacklistedBlocks.add(Material.JUNGLE_FENCE_GATE);
            this.blacklistedBlocks.add(Material.SPRUCE_FENCE_GATE);
            this.blacklistedBlocks.add(Material.GLASS_PANE);
            this.blacklistedBlocks.add(Material.STONE_SLAB);
        }

        if (PluginSupport.WORLD_GUARD.isPluginEnabled() && PluginSupport.WORLD_EDIT.isPluginEnabled()) this.worldGuardSupportVersion = new WorldGuardSupport();

        if (PluginSupport.DECENT_HOLOGRAMS.isPluginEnabled()) {
            this.hologramController = new DecentHologramsSupport();
            LegacyLogger.success("DecentHolograms support has been enabled.");
        } else if (PluginSupport.CMI.isPluginEnabled() && CMIModule.holograms.isEnabled()) {
            this.hologramController = new CMIHologramsSupport();
            LegacyLogger.success("CMI Hologram support has been enabled.");
        } else if (PluginSupport.HOLOGRAPHIC_DISPLAYS.isPluginEnabled()) {
            this.hologramController = new HolographicDisplaysSupport();
            LegacyLogger.success("Holographic Displays support has been enabled.");}
        else LegacyLogger.warn("No holograms plugin were found. If using CMI, make sure holograms module is enabled.");

        this.locationSettings.fixLocations();

        this.flareSettings.load();
    }

    /**
     * Used when the plugin starts to control the count-down and when the event starts
     */
    public void startEnvoyCountDown() {
        cancelEnvoyCooldownTime();
        this.coolDownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isEnvoyActive()) {
                    Calendar cal = Calendar.getInstance();
                    cal.clear(Calendar.MILLISECOND);

                    // Ryder Start
                    int online = plugin.getServer().getOnlinePlayers().size();

                    if (online == 0 && envoySettings.isEnvoyFilterEnabled()) return;
                    // Ryder End

                    for (Calendar warn : getWarnings()) {
                        Calendar check = Calendar.getInstance();
                        check.setTimeInMillis(warn.getTimeInMillis());
                        check.clear(Calendar.MILLISECOND);

                        if (check.compareTo(cal) == 0) {
                            HashMap<String, String> placeholder = new HashMap<>();
                            placeholder.put("%time%", getNextEnvoyTime());
                            placeholder.put("%Time%", getNextEnvoyTime());
                            Translation.warning.broadcastMessage(false, placeholder);
                        }
                    }

                    Calendar next = Calendar.getInstance();
                    next.setTimeInMillis(getNextEnvoy().getTimeInMillis());
                    next.clear(Calendar.MILLISECOND);

                    if (next.compareTo(cal) <= 0 && !isEnvoyActive()) {
                        if (envoySettings.isMinPlayersEnabled()) {

                            if (online < envoySettings.getMinPlayers()) {
                                HashMap<String, String> placeholder = new HashMap<>();
                                placeholder.put("%amount%", online + "");
                                placeholder.put("%Amount%", online + "");
                                Translation.not_enough_players.broadcastMessage(false, placeholder);
                                setNextEnvoy(getEnvoyCooldown());
                                resetWarnings();
                                return;
                            }
                        }

                        if (envoySettings.isRandomLocationsEnabled() && center.getWorld() == null) {
                            LegacyLogger.warn("The envoy center's world can't be found and so envoy has been canceled.");
                            LegacyLogger.warn("Center String: " + centerString);
                            setNextEnvoy(getEnvoyCooldown());
                            resetWarnings();
                            return;
                        }

                        EnvoyStartEvent event = new EnvoyStartEvent(autoTimer ? EnvoyStartReason.AUTO_TIMER : EnvoyStartReason.SPECIFIED_TIME);
                        plugin.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) startEnvoyEvent();
                    }
                }
            }
        }.runTaskTimer(this.plugin, 20, 20);
    }

    /**
     * @param block The location you want the tier from.
     * @return The tier that location is.
     */
    public Tier getTier(Block block) {
        return this.activeEnvoys.get(block);
    }

    /**
     * @return True if the envoy event is currently happening and false if not.
     */
    public boolean isEnvoyActive() {
        return this.envoyActive;
    }

    /**
     * Despawns all the active crates.
     */
    public void removeAllEnvoys() {
        this.envoyActive = false;
        cleanLocations();

        for (Block block : getActiveEnvoys()) {
            if (!block.getChunk().isLoaded()) block.getChunk().load();

            block.setType(Material.AIR);
            stopSignalFlare(block.getLocation());
        }

        this.fallingBlocks.keySet().forEach(Entity :: remove);

        if (hasHologramPlugin()) this.hologramController.removeAllHolograms();

        this.fallingBlocks.clear();
        this.activeEnvoys.clear();
    }

    public WorldGuardSupport getWorldGuardPluginSupport() {
        return this.worldGuardSupportVersion;
    }

    public HologramController getHologramController() {
        return this.hologramController;
    }

    public boolean hasHologramPlugin() {
        return this.hologramController != null;
    }

    /**
     * @return All the envoys that are active.
     */
    public Set<Block> getActiveEnvoys() {
        return this.activeEnvoys.keySet();
    }

    /**
     * @param block The location you are checking.
     * @return Turn if it is and false if not.
     */
    public boolean isActiveEnvoy(Block block) {
        return this.activeEnvoys.containsKey(block);
    }

    /**
     * @param block The location you wish to add.
     */
    public void addActiveEnvoy(Block block, Tier tier) {
        this.activeEnvoys.put(block, tier);
    }

    /**
     * @param block The location you wish to remove.
     */
    public void removeActiveEnvoy(Block block) {
        this.activeEnvoys.remove(block);
    }

    /**
     * @return The next envoy time as a calendar.
     */
    public Calendar getNextEnvoy() {
        return this.nextEnvoy;
    }

    /**
     * @param cal A calendar that has the next time the envoy will happen.
     */
    public void setNextEnvoy(Calendar cal) {
        this.nextEnvoy = cal;
    }

    /**
     * @return The time till the next envoy.
     */
    public String getNextEnvoyTime() {
        String message = this.methods.convertTimeToString(getNextEnvoy());

        if (message.equals("0" + Translation.second.getString())) message = Translation.on_going.getString();

        return message;
    }

    /**
     * @return All falling blocks are currently going.
     */
    public Map<Entity, Block> getFallingBlocks() {
        return this.fallingBlocks;
    }

    /**
     * @param entity Remove a falling block from the list.
     */
    public void removeFallingBlock(Entity entity) {
        this.fallingBlocks.remove(entity);
    }

    /**
     * Call when you want to set the new warning.
     */
    public void resetWarnings() {
        this.warnings.clear();
        this.envoySettings.getEnvoyWarnings().forEach(time -> addWarning(makeWarning(time)));
    }

    /**
     * @param cal When adding a new warning.
     */
    public void addWarning(Calendar cal) {
        this.warnings.add(cal);
    }

    /**
     * @return All the current warnings.
     */
    public List<Calendar> getWarnings() {
        return this.warnings;
    }

    /**
     * @param time The new time for the warning.
     * @return The new time as a calendar
     */
    public Calendar makeWarning(String time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getNextEnvoy().getTimeInMillis());

        for (String i : time.split(" ")) {
            if (i.contains("d")) {
                cal.add(Calendar.DATE, -Integer.parseInt(i.replace("d", "")));
            } else if (i.contains("h")) {
                cal.add(Calendar.HOUR, -Integer.parseInt(i.replace("h", "")));
            } else if (i.contains("m")) {
                cal.add(Calendar.MINUTE, -Integer.parseInt(i.replace("m", "")));
            } else if (i.contains("s")) {
                cal.add(Calendar.SECOND, -Integer.parseInt(i.replace("s", "")));
            }
        }

        return cal;
    }

    /**
     * @return The time left in the current envoy event.
     */
    public String getEnvoyRunTimeLeft() {
        String message = this.methods.convertTimeToString(this.envoyTimeLeft);

        if (message.equals("0" + Translation.second.getString())) message = Translation.not_running.getString();

        return message;
    }

    /**
     * Call when the run time needs canceled.
     */
    public void cancelEnvoyRunTime() {
        try {
            this.runTimeTask.cancel();
        } catch (Exception ignored) {}
    }

    /**
     * Call when the cool downtime needs canceled.
     */
    public void cancelEnvoyCooldownTime() {
        try {
            this.coolDownTask.cancel();
        } catch (Exception ignored) {}
    }

    public List<Block> generateSpawnLocations() {
        int maxSpawns;

        if (this.envoySettings.isMaxCrateEnabled()) {
            maxSpawns = this.envoySettings.getMaxCrates();
        } else if (this.envoySettings.isRandomAmount()) {
            // Generates a random number between the min and max settings
            maxSpawns = this.random.nextInt(this.envoySettings.getMaxCrates() + 1 - this.envoySettings.getMinCrates()) + this.envoySettings.getMinCrates();
        } else {
            maxSpawns = this.envoySettings.isRandomLocationsEnabled() ? this.envoySettings.getMaxCrates() : this.locationSettings.getActiveLocations().size();
        }

        if (maxSpawns > (Math.pow(this.envoySettings.getMaxRadius() * 2, 2) - Math.pow((this.envoySettings.getMinRadius() * 2 + 1), 2))) {
            maxSpawns = (int) (Math.pow(this.envoySettings.getMaxRadius() * 2, 2) - Math.pow((this.envoySettings.getMinRadius() * 2 + 1), 2));
            LegacyLogger.warn("Crate spawn amount is larger than the area that was provided. Spawning " + maxSpawns + " crates instead.");
        }

        if (this.envoySettings.isRandomLocationsEnabled()) {
            if (!testCenter()) return new ArrayList<>();

            List<Block> minimumRadiusBlocks = getBlocks(this.center.clone(), this.envoySettings.getMinRadius());

            while (this.locationSettings.getDropLocations().size() < maxSpawns) {
                int maxRadius = this.envoySettings.getMaxRadius();
                Location location = this.center.clone();
                location.add(-(maxRadius) + this.random.nextInt(maxRadius * 2), 0, -(maxRadius) + this.random.nextInt(maxRadius * 2));
                location = location.getWorld().getHighestBlockAt(location).getLocation();

                if (!location.getChunk().isLoaded() && !location.getChunk().load()) continue;

                if (location.getBlockY() <= location.getWorld().getMinHeight() ||
                        minimumRadiusBlocks.contains(location.getBlock()) || minimumRadiusBlocks.contains(location.clone().add(0, 1, 0).getBlock()) ||
                        this.locationSettings.getDropLocations().contains(location.getBlock()) || this.locationSettings.getDropLocations().contains(location.clone().add(0, 1, 0).getBlock()) ||
                        this.blacklistedBlocks.contains(location.getBlock().getType())) continue;

                Block block = location.getBlock();
                if (block.getType() != Material.AIR) block = block.getLocation().add(0, 1, 0).getBlock();

                this.locationSettings.addDropLocations(block);
            }

            Files.DATA.getFile().set("Locations.Spawned", getBlockList(locationSettings.getDropLocations()));
            Files.DATA.saveFile();
        } else {
            if (this.envoySettings.isMaxCrateEnabled() || this.envoySettings.isRandomAmount()) {
                if (this.locationSettings.getSpawnLocations().size() <= maxSpawns) {
                    this.locationSettings.addAllDropLocations(this.locationSettings.getSpawnLocations());
                } else {
                    while (this.locationSettings.getDropLocations().size() < maxSpawns) {
                        Block block = this.locationSettings.getSpawnLocations().get(this.random.nextInt(this.locationSettings.getSpawnLocations().size()));

                        this.locationSettings.addDropLocations(block);
                    }
                }
            } else {
                this.locationSettings.addAllDropLocations(this.locationSettings.getSpawnLocations());
            }
        }

        boolean envoyLocationsBroadcast = this.plugin.getCrazyHandler().getConfigManager().getConfig().getProperty(Config.envoy_locations_broadcast);

        if (envoyLocationsBroadcast) {
            StringBuilder locations = getStringBuilder();

            this.plugin.getServer().broadcast(Translation.envoy_locations.getMessage("%locations%", locations.toString().translateEscapes()).asString(), "envoy.locations");
        }

        return this.locationSettings.getDropLocations();
    }

    @NotNull
    private StringBuilder getStringBuilder() {
        StringBuilder locations = new StringBuilder();

        int x = 1;
        for (Block block : this.locationSettings.getDropLocations()) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%id%", String.valueOf(x));
            placeholders.put("%world%", block.getWorld().getName());
            placeholders.put("%x%", String.valueOf(block.getX()));
            placeholders.put("%y%", String.valueOf(block.getY()));
            placeholders.put("%z%", String.valueOf(block.getZ()));

            locations.append(Translation.location_format.getMessage(placeholders).asString());
            x += 1;
        }

        return locations;
    }

    /**
     * Starts the envoy event.
     *
     * @return true if the event started successfully and false if it had an issue.
     */
    public boolean startEnvoyEvent() {
        // Called before locations are generated due to it setting those locations to air and causing
        // crates to spawn in the ground when not using falling blocks.

        if (this.tiers.isEmpty()) {
            this.plugin.getServer().broadcastMessage(this.methods.getPrefix() + LegacyUtils.color("&cNo tiers were found. Please delete the Tiers folder to allow it to remake the default tier files."));
            return false;
        }

        removeAllEnvoys();

        List<Block> dropLocations = generateSpawnLocations();

        if (this.envoySettings.isRandomLocationsEnabled() && isCenterLoaded()) testCenter();

        if (dropLocations.isEmpty() || (this.envoySettings.isRandomLocationsEnabled() && isCenterLoaded())) {
            setNextEnvoy(getEnvoyCooldown());
            resetWarnings();
            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.NO_LOCATIONS_FOUND);
            this.plugin.getServer().getPluginManager().callEvent(event);
            Translation.no_spawn_locations_found.broadcastMessage(false);
            return false;
        }

        for (Player player : this.editorSettings.getEditors()) {
            this.editorSettings.removeFakeBlocks();
            player.getInventory().removeItem(new ItemStack(Material.BEDROCK, 1));
            Translation.kicked_from_editor_mode.sendMessage(player);
        }

        this.editorSettings.getEditors().clear();

        setEnvoyActive(true);
        int max = dropLocations.size();
        HashMap<String, String> placeholder = new HashMap<>();
        placeholder.put("%amount%", max + "");
        placeholder.put("%Amount%", max + "");
        Translation.started.broadcastMessage(false, placeholder);

        if (this.envoySettings.isEnvoyCountDownEnabled()) {
            this.countdownTimer = new CountdownTimer(this.envoySettings.getEnvoyCountDownTimer());

            this.countdownTimer.scheduleTimer();
        }

        for (Block block : dropLocations) {
            if (block != null) {
                boolean spawnFallingBlock = false;

                if (this.envoySettings.isFallingBlocksEnabled()) {
                    for (Entity entity : this.methods.getNearbyEntities(block.getLocation(), 40, 40, 40)) {
                        if (entity instanceof Player) {
                            spawnFallingBlock = true;
                            break;
                        }
                    }
                }

                if (spawnFallingBlock) {
                    if (!block.getChunk().isLoaded()) block.getChunk().load();

                    int fallingHeight = this.envoySettings.getFallingHeight();
                    Material fallingBlock = this.envoySettings.getFallingBlockMaterial();
                    byte fallingDurability = (byte) this.envoySettings.getFallingBlockDurability();

                    FallingBlock chest = block.getWorld().spawnFallingBlock(block.getLocation().add(.5, fallingHeight, .5), fallingBlock, fallingDurability);
                    chest.setDropItem(false);
                    chest.setHurtEntities(false);
                    this.fallingBlocks.put(chest, block);
                } else {
                    Tier tier = pickRandomTier();

                    if (!block.getChunk().isLoaded()) block.getChunk().load();

                    block.setType(tier.getPlacedBlockMaterial());

                    if (tier.isHoloEnabled() && hasHologramPlugin()) this.hologramController.createHologram(block, tier);

                    addActiveEnvoy(block, tier);
                    this.locationSettings.addActiveLocation(block);

                    if (tier.getSignalFlareToggle() && block.getChunk().isLoaded()) startSignalFlare(block.getLocation(), tier);
                }
            }
        }

        this.runTimeTask = new BukkitRunnable() {
            @Override
            public void run() {
                EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.OUT_OF_TIME);
                plugin.getServer().getPluginManager().callEvent(event);
                Translation.ended.broadcastMessage(false);
                endEnvoyEvent();
            }
        }.runTaskLater(this.plugin, getTimeSeconds(this.envoySettings.getEnvoyRunTimer()) * 20L);

        this.envoyTimeLeft = getEnvoyRunTimeCalendar();

        return true;
    }

    /**
     * Ends the envoy event.
     */
    public void endEnvoyEvent() {
        removeAllEnvoys();
        setEnvoyActive(false);
        cancelEnvoyRunTime();

        if (this.envoySettings.isEnvoyRunTimerEnabled()) {
            setNextEnvoy(getEnvoyCooldown());
            resetWarnings();
        }

        this.coolDownSettings.clearCoolDowns();
    }

    /**
     * Get a list of all the tiers.
     *
     * @return List of all the tiers.
     */
    public List<Tier> getTiers() {
        return this.tiers;
    }

    /**
     * Get a tier from its name.
     *
     * @param tierName The name of the tier.
     * @return Returns a tier or will return null if not tier is found.
     */
    public Tier getTier(String tierName) {
        for (Tier tier : this.tiers) {
            if (tier.getName().equalsIgnoreCase(tierName)) return tier;
        }

        return null;
    }

    /**
     * @param loc The location the signals will be at.
     * @param tier The tier the signal is.
     */
    public void startSignalFlare(final Location loc, final Tier tier) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                firework(loc.clone().add(.5, 0, .5), tier);
            }
        }.runTaskTimer(this.plugin, getTimeSeconds(tier.getSignalFlareTimer()) * 20L, getTimeSeconds(tier.getSignalFlareTimer()) * 20L);

        this.activeSignals.put(loc, task);
    }

    /**
     * @param loc The location that the signal is stopping.
     */
    public void stopSignalFlare(Location loc) {
        try {
            this.activeSignals.get(loc).cancel();
        } catch (Exception ignored) {}

        this.activeSignals.remove(loc);
    }

    /**
     * @return The center location for the random crates.
     */
    public Location getCenter() {
        return this.center;
    }

    /**
     * Sets the center location for the random crates.
     *
     * @param loc The new center location.
     */
    public void setCenter(Location loc) {
        this.center = loc;
        this.centerString = this.methods.getUnBuiltLocation(this.center);
        Files.DATA.getFile().set("Center", this.centerString);
        Files.DATA.saveFile();
    }

    /**
     * Check if a player is ignoring the messages.
     *
     * @param uuid The player's UUID.
     * @return True if they are ignoring them and false if not.
     */
    public boolean isIgnoringMessages(UUID uuid) {
        return this.ignoreMessages.contains(uuid);
    }

    /**
     * Make a player ignore the messages.
     *
     * @param uuid The player's UUID.
     */
    public void addIgnorePlayer(UUID uuid) {
        this.ignoreMessages.add(uuid);
    }

    /**
     * Make a player stop ignoring the messages.
     *
     * @param uuid The player's UUID.
     */
    public void removeIgnorePlayer(UUID uuid) {
        this.ignoreMessages.remove(uuid);
    }

    /**
     * Used to clean all spawn locations and set them back to air.
     */
    public void cleanLocations() {
        List<Block> locations = new ArrayList<>(this.locationSettings.getActiveLocations());

        if (this.envoySettings.isRandomLocationsEnabled()) {
            locations.addAll(getLocationsFromStringList(Files.DATA.getFile().getStringList("Locations.Spawned")));
        } else {
            locations.addAll(this.locationSettings.getSpawnLocations());
        }

        for (Block spawnedLocation : locations) {
            if (spawnedLocation != null) {
                if (!spawnedLocation.getChunk().isLoaded()) spawnedLocation.getChunk().load();

                spawnedLocation.setType(Material.AIR);
                stopSignalFlare(spawnedLocation.getLocation());

                if (hasHologramPlugin()) this.hologramController.removeAllHolograms();
            }
        }

        this.locationSettings.clearActiveLocations();
        this.locationSettings.clearDropLocations();

        Files.DATA.getFile().set("Locations.Spawned", new ArrayList<>());
        Files.DATA.saveFile();
    }

    private boolean testCenter() {
        if (isCenterLoaded()) { // Check to make sure the center exist and if not try to load it again.
            LegacyLogger.warn("Attempting to fix Center location that failed.");
            loadCenter();

            if (isCenterLoaded()) { // If center still doesn't exist then it cancels the event.
                LegacyLogger.debug("Debug Start");
                LegacyLogger.debug("Center String: \"" + centerString + "'");
                LegacyLogger.debug("Location Object: \"" + center.toString() + "'");
                LegacyLogger.debug("World Exist: \"" + (center.getWorld() != null) + "'");
                LegacyLogger.debug("Debug End");
                LegacyLogger.error("Failed to fix Center. Will try again next event.");
                return false;
            } else {
                LegacyLogger.success("Center has been fixed and will continue event.");
            }
        }

        return true;
    }

    private void loadCenter() {
        FileConfiguration data = Files.DATA.getFile();

        if (data.contains("Center")) {
            this.centerString = data.getString("Center");
            if (this.centerString != null) this.center = this.methods.getBuiltLocation(centerString);
        } else {
            this.center = this.plugin.getServer().getWorlds().get(0).getSpawnLocation();
        }

        if (this.center.getWorld() == null) {
            if (this.plugin.isLogging()) LegacyLogger.error("Failed to fix Center. Will try again next event.");
        }
    }

    private boolean isCenterLoaded() {
        return this.center.getWorld() == null;
    }

    private void setEnvoyActive(boolean toggle) {
        this.envoyActive = toggle;
    }

    private Calendar getEnvoyCooldown() {
        Calendar cal = Calendar.getInstance();

        if (this.envoySettings.isEnvoyCooldownEnabled()) {
            String time = this.envoySettings.getEnvoyCooldown();

            cal = this.methods.getTimeFromString(time);
        } else {
            getEnvoyTime(cal);
        }

        return cal;
    }

    private Calendar getEnvoyRunTimeCalendar() {
        String time = this.envoySettings.getEnvoyRunTimer().toLowerCase();

        return this.methods.getTimeFromString(time);
    }

    private void firework(Location loc, Tier tier) {
        List<Color> colors = tier.getFireworkColors();

        Firework firework = loc.getWorld().spawn(loc, Firework.class);

        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(colors).trail(true).flicker(false).build());
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);

        this.fireworkDamageAPI.addFirework(firework);
    }

    //TODO find a better away of doing this as it causes crashes with big radius.
    private List<Block> getBlocks(Location location, int radius) {
        Location locations2 = location.clone();
        location.add(-radius, 0, -radius);
        locations2.add(radius, 0, radius);
        List<Block> locations = new ArrayList<>();
        int topBlockX = (Math.max(location.getBlockX(), locations2.getBlockX()));
        int bottomBlockX = (Math.min(location.getBlockX(), locations2.getBlockX()));
        int topBlockZ = (Math.max(location.getBlockZ(), locations2.getBlockZ()));
        int bottomBlockZ = (Math.min(location.getBlockZ(), locations2.getBlockZ()));

        if (location.getWorld() != null) {
            for (int x = bottomBlockX; x <= topBlockX; x++) {
                for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                    locations.add(location.getWorld().getHighestBlockAt(x, z));
                }
            }
        }

        return locations;
    }

    private int getTimeSeconds(String time) {
        int seconds = 0;

        for (String i : time.split(" ")) {
            if (i.contains("d")) {
                seconds += Integer.parseInt(i.replace("d", "")) * 86400;
            } else if (i.contains("h")) {
                seconds += Integer.parseInt(i.replace("h", "")) * 3600;
            } else if (i.contains("m")) {
                seconds += Integer.parseInt(i.replace("m", "")) * 60;
            } else if (i.contains("s")) {
                seconds += Integer.parseInt(i.replace("s", ""));
            }
        }

        return seconds;
    }

    private Tier pickRandomTier() {
        if (this.cachedChances.isEmpty()) {
            for (Tier tier : this.tiers) {
                for (int i = 0; i < tier.getSpawnChance(); i++) {
                    this.cachedChances.add(tier);
                }
            }
        }

        return this.cachedChances.get(this.random.nextInt(this.cachedChances.size()));
    }

    /**
     * @param location The location that you want to check.
     */
    public boolean isLocation(Location location) {
        for (Block block : this.locationSettings.getSpawnLocations()) {
            if (block.getLocation().equals(location)) return true;
        }

        return false;
    }

    public CountdownTimer getCountdownTimer() {
        return this.countdownTimer;
    }

    // Get world location.

    private List<String> getBlockList(List<Block> stringList) {
        ArrayList<String> strings = new ArrayList<>();

        for (Block block : stringList) {
            strings.add(this.methods.getUnBuiltLocation(block.getLocation()));
        }

        return strings;
    }

    private List<Block> getLocationsFromStringList(List<String> locationsList) {
        ArrayList<Block> locations = new ArrayList<>();

        for (String location : locationsList) {
            locations.add(this.methods.getBuiltLocation(location).getBlock());
        }

        return locations;
    }
}