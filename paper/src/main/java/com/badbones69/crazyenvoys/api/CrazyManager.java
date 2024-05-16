package com.badbones69.crazyenvoys.api;

import ch.jalu.configme.SettingsManager;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.enums.DataFiles;
import com.badbones69.crazyenvoys.api.objects.misc.Prize;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.config.impl.ConfigKeys;
import com.badbones69.crazyenvoys.platform.util.ItemUtils;
import com.badbones69.crazyenvoys.platform.util.MiscUtils;
import com.badbones69.crazyenvoys.api.enums.PersistentKeys;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent.EnvoyEndReason;
import com.badbones69.crazyenvoys.api.events.EnvoyStartEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyStartEvent.EnvoyStartReason;
import com.badbones69.crazyenvoys.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.api.objects.CoolDownSettings;
import com.badbones69.crazyenvoys.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.badbones69.crazyenvoys.listeners.timer.CountdownTimer;
import com.badbones69.crazyenvoys.support.holograms.types.CMIHologramsSupport;
import com.badbones69.crazyenvoys.support.claims.WorldGuardSupport;
import com.badbones69.crazyenvoys.support.holograms.types.DecentHologramsSupport;
import com.ryderbelserion.vital.common.configuration.YamlFile;
import com.ryderbelserion.vital.common.configuration.YamlManager;
import com.ryderbelserion.vital.common.configuration.objects.CustomFile;
import com.ryderbelserion.vital.common.util.AdvUtil;
import com.ryderbelserion.vital.enums.Support;
import com.ryderbelserion.vital.util.DyeUtil;
import com.ryderbelserion.vital.util.builders.items.ItemBuilder;
import com.ryderbelserion.vital.util.scheduler.FoliaRunnable;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
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

    private final @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);
    private final @NotNull YamlManager fileManager = this.plugin.getFileManager();
    private final @NotNull SettingsManager config = ConfigManager.getConfig();

    private final @NotNull CoolDownSettings coolDownSettings = this.plugin.getCoolDownSettings();

    private final @NotNull LocationSettings locationSettings = this.plugin.getLocationSettings();

    private final @NotNull EditorSettings editorSettings = this.plugin.getEditorSettings();

    private final @NotNull FlareSettings flareSettings = this.plugin.getFlareSettings();
    
    private CountdownTimer countdownTimer;

    private ScheduledTask runTimeTask;
    private ScheduledTask coolDownTask;
    private Calendar nextEnvoy;
    private Calendar envoyTimeLeft;
    private boolean envoyActive = false;
    private boolean autoTimer = true;
    private WorldGuardSupport worldGuardSupportVersion;
    private HologramController hologramController;
    private Location center;
    private String centerString;

    private final Map<Block, Tier> activeEnvoys = new HashMap<>();
    private final Map<Location, ScheduledTask> activeSignals = new HashMap<>();

    private final Map<Entity, Block> fallingBlocks = new HashMap<>();

    private final List<Tier> tiers = new ArrayList<>();
    private final List<Tier> cachedChances = new ArrayList<>();
    private final List<Material> blacklistedBlocks = new ArrayList<>();
    private final List<UUID> ignoreMessages = new ArrayList<>();
    private final List<Calendar> warnings = new ArrayList<>();

    /**
     * Run this when you are starting up the server.
     */
    public void load() {
        loadEnvoys();
    }

    private void getEnvoyTime(Calendar cal) {
        String time = this.config.getProperty(ConfigKeys.envoys_time);
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
        YamlFile configuration = DataFiles.data.getYamlFile();

        if (serverStop) {
            removeAllEnvoys();

            configuration.set("Next-Envoy", getNextEnvoy().getTimeInMillis());
            DataFiles.data.save();

            this.locationSettings.clearSpawnLocations();

            this.coolDownSettings.clearCoolDowns();

            return;
        }

        ConfigManager.refresh();

        removeAllEnvoys();

        configuration.set("Next-Envoy", getNextEnvoy().getTimeInMillis());
        DataFiles.data.save();

        this.locationSettings.clearSpawnLocations();

        this.coolDownSettings.clearCoolDowns();

        loadEnvoys();
    }

    private void loadEnvoys() {
        // Clear all spawn locations.
        this.locationSettings.clearSpawnLocations();

        this.blacklistedBlocks.clear();
        this.cachedChances.clear();

        YamlFile configuration = DataFiles.data.getYamlFile();

        this.envoyTimeLeft = Calendar.getInstance();

        // Populate the array list.
        this.locationSettings.populateMap();

        if (MiscUtils.isLogging() && !this.locationSettings.getFailedLocations().isEmpty()) this.plugin.getLogger().severe("Failed to load " + this.locationSettings.getFailedLocations().size() + " locations and will reattempt in 10s.");

        if (Calendar.getInstance().after(getNextEnvoy())) setEnvoyActive(false);

        loadCenter();

        if (this.config.getProperty(ConfigKeys.envoys_run_time_toggle)) {
            Calendar cal = Calendar.getInstance();

            if (this.config.getProperty(ConfigKeys.envoys_countdown)) {
                this.autoTimer = true;
                cal.setTimeInMillis(configuration.getLong("Next-Envoy"));

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
            Tier tier = new Tier(tierFile.getStrippedName());
            YamlFile file = tierFile.getYamlFile();
            tier.setClaimPermissionToggle(file.getBoolean("Settings.Claim-Permission"));
            tier.setClaimPermission(file.getString("Settings.Claim-Permission-Name"));
            tier.setUseChance(file.getBoolean("Settings.Use-Chance"));
            tier.setSpawnChance(file.getInt("Settings.Spawn-Chance"));
            tier.setBulkToggle(file.getBoolean("Settings.Bulk-Prizes.Toggle"));
            tier.setBulkRandom(file.getBoolean("Settings.Bulk-Prizes.Random"));
            tier.setBulkMax(file.getInt("Settings.Bulk-Prizes.Max-Bulk"));
            tier.setHoloToggle(file.getBoolean("Settings.Hologram-Toggle"));
            tier.setHoloRange(file.getInt("Settings.Hologram-Range", 8));
            tier.setHoloHeight(file.getDouble("Settings.Hologram-Height", 1.5));
            tier.setHoloMessage(file.getStringList("Settings.Hologram"));

            ItemBuilder placedBlock = new ItemBuilder().withType(file.getString("Settings.Placed-Block", "chest"));
            tier.setPlacedBlockMaterial(placedBlock.getType());

            tier.setFireworkToggle(file.getBoolean("Settings.Firework-Toggle"));

            if (file.getStringList("Settings.Firework-Colors").isEmpty()) {
                tier.setFireworkColors(Arrays.asList(Color.GRAY, Color.BLACK, Color.ORANGE));
            } else {
                file.getStringList("Settings.Firework-Colors").forEach(color -> tier.addFireworkColor(DyeUtil.getColor(color)));
            }

            if (file.contains("Settings.Prize-Message") && !file.getStringList("Settings.Prize-Message").isEmpty()) {
                List<String> array = new ArrayList<>();

                file.getStringList("Settings.Prize-Message").forEach(line -> array.add(line.replaceAll("%reward%", "{reward}").replaceAll("%tier%", "{tier}")));

                tier.setPrizeMessage(array);
            }

            tier.setSignalFlareToggle(file.getBoolean("Settings.Signal-Flare.Toggle"));
            tier.setSignalFlareTimer(file.getString("Settings.Signal-Flare.Time"));

            if (file.getStringList("Settings.Signal-Flare.Colors").isEmpty()) {
                tier.setSignalFlareColors(Arrays.asList(Color.GRAY, Color.BLACK, Color.ORANGE));
            } else {
                file.getStringList("Settings.Signal-Flare.Colors").forEach(color -> tier.addSignalFlareColor(DyeUtil.getColor(color)));
            }

            for (String prizeID : file.getConfigurationSection("Prizes").getKeys(false)) {
                String path = "Prizes." + prizeID + ".";
                int chance = file.getInt(path + "Chance");
                String displayName = file.contains(path + "DisplayName") ? file.getString(path + "DisplayName") : "";

                List<String> commands = new ArrayList<>();

                file.getStringList(path + "Commands").forEach(line -> commands.add(line.replaceAll("%reward%", "{reward}")
                        .replaceAll("%player%", "{player}")
                        .replaceAll("%Player%", "{player}")
                        .replaceAll("%tier%", "{tier}")));

                List<String> messages = new ArrayList<>();

                file.getStringList(path + "Messages").forEach(line -> messages.add(line.replaceAll("%reward%", "{reward}")
                        .replaceAll("%player%", "{player}")
                        .replaceAll("%Player%", "{player}")
                        .replaceAll("%tier%", "{tier}")));

                boolean dropItems = file.getBoolean(path + "Drop-Items");

                List<ItemBuilder> items = ItemUtils.convertStringList(file.getStringList(path + "Items"), prizeID);
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

        if (Support.worldedit.isEnabled() && Support.worldguard.isEnabled()) this.worldGuardSupportVersion = new WorldGuardSupport();

        if (Support.decent_holograms.isEnabled()) {
            this.hologramController = new DecentHologramsSupport();

            this.plugin.getLogger().info("DecentHolograms support has been enabled.");
        } else if (Support.cmi.isEnabled() && CMIModule.holograms.isEnabled()) {
            this.hologramController = new CMIHologramsSupport();
            this.plugin.getLogger().info("CMI Hologram support has been enabled.");
        } else this.plugin.getLogger().warning("No holograms plugin were found. If using CMI, make sure holograms module is enabled.");

        this.locationSettings.fixLocations();

        this.flareSettings.load();
    }

    /**
     * Used when the plugin starts to control the count-down and when the event starts
     */
    public void startEnvoyCountDown() {
        cancelEnvoyCooldownTime();

        this.coolDownTask = new FoliaRunnable(this.plugin.getServer().getGlobalRegionScheduler()) {
            @Override
            public void run() {
                if (!isEnvoyActive()) {
                    Calendar cal = Calendar.getInstance();
                    cal.clear(Calendar.MILLISECOND);

                    // Ryder Start
                    int online = plugin.getServer().getOnlinePlayers().size();

                    if (online == 0 && config.getProperty(ConfigKeys.envoys_ignore_empty_server)) return;
                    // Ryder End

                    for (Calendar warn : getWarnings()) {
                        Calendar check = Calendar.getInstance();
                        check.setTimeInMillis(warn.getTimeInMillis());
                        check.clear(Calendar.MILLISECOND);

                        if (check.compareTo(cal) == 0) {
                            Map<String, String> placeholder = new HashMap<>();
                            placeholder.put("{time}", getNextEnvoyTime());

                            Messages.warning.broadcastMessage(false, placeholder);
                        }
                    }

                    Calendar next = Calendar.getInstance();
                    next.setTimeInMillis(getNextEnvoy().getTimeInMillis());
                    next.clear(Calendar.MILLISECOND);

                    if (next.compareTo(cal) <= 0 && !isEnvoyActive()) {
                        if (config.getProperty(ConfigKeys.envoys_minimum_players_toggle)) {
                            if (online < config.getProperty(ConfigKeys.envoys_minimum_players_amount)) {
                                Map<String, String> placeholder = new HashMap<>();
                                placeholder.put("{amount}", online + "");

                                Messages.not_enough_players.broadcastMessage(false, placeholder);

                                setNextEnvoy(getEnvoyCooldown());
                                resetWarnings();

                                return;
                            }
                        }

                        if (config.getProperty(ConfigKeys.envoys_random_locations) && center.getWorld() == null) {
                            plugin.getLogger().warning("The envoy center's world can't be found and so envoy has been canceled.");
                            plugin.getLogger().warning("Center String: " + centerString);

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
        }.runAtFixedRate(this.plugin, 20, 20);
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
            //todo() load chunk async
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
        String message = MiscUtils.convertTimeToString(getNextEnvoy());

        if (message.equals("0" + Messages.second.getString())) message = Messages.on_going.getString();

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
        this.config.getProperty(ConfigKeys.envoys_warnings).forEach(time -> addWarning(makeWarning(time)));
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
        String message = MiscUtils.convertTimeToString(this.envoyTimeLeft);

        if (message.equals("0" + Messages.second.getString())) message = Messages.not_running.getString();

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

        if (this.config.getProperty(ConfigKeys.envoys_max_drops_toggle)) {
            maxSpawns = this.config.getProperty(ConfigKeys.envoys_max_drops);
        } else if (this.config.getProperty(ConfigKeys.envoys_random_drops)) {
            // Generates a random number between the min and max settings
            maxSpawns = new Random().nextInt(this.config.getProperty(ConfigKeys.envoys_max_drops) + 1 - this.config.getProperty(ConfigKeys.envoys_min_drops)) + this.config.getProperty(ConfigKeys.envoys_min_drops);
        } else {
            maxSpawns = this.config.getProperty(ConfigKeys.envoys_random_locations) ? this.config.getProperty(ConfigKeys.envoys_max_drops) : this.locationSettings.getActiveLocations().size();
        }

        if (maxSpawns > (Math.pow(this.config.getProperty(ConfigKeys.envoys_max_radius) * 2, 2) - Math.pow((this.config.getProperty(ConfigKeys.envoys_min_radius) * 2 + 1), 2))) {
            maxSpawns = (int) (Math.pow(this.config.getProperty(ConfigKeys.envoys_max_radius) * 2, 2) - Math.pow((this.config.getProperty(ConfigKeys.envoys_min_radius) * 2 + 1), 2));
            this.plugin.getLogger().warning("Crate spawn amount is larger than the area that was provided. Spawning " + maxSpawns + " crates instead.");
        }

        if (this.config.getProperty(ConfigKeys.envoys_random_locations)) {
            if (!testCenter()) return new ArrayList<>();

            List<Block> minimumRadiusBlocks = getBlocks(this.center.clone(), this.config.getProperty(ConfigKeys.envoys_min_radius));

            while (this.locationSettings.getDropLocations().size() < maxSpawns) {
                int maxRadius = this.config.getProperty(ConfigKeys.envoys_max_radius);
                Location location = this.center.clone();
                location.add(-(maxRadius) + new Random().nextInt(maxRadius * 2), 0, -(maxRadius) + new Random().nextInt(maxRadius * 2));
                location = location.getWorld().getHighestBlockAt(location).getLocation();

                //todo() load chunk async
                if (!location.getChunk().isLoaded() && !location.getChunk().load()) continue;

                if (location.getBlockY() <= location.getWorld().getMinHeight() ||
                        minimumRadiusBlocks.contains(location.getBlock()) || minimumRadiusBlocks.contains(location.clone().add(0, 1, 0).getBlock()) ||
                        this.locationSettings.getDropLocations().contains(location.getBlock()) || this.locationSettings.getDropLocations().contains(location.clone().add(0, 1, 0).getBlock()) ||
                        this.blacklistedBlocks.contains(location.getBlock().getType())) continue;

                Block block = location.getBlock();
                if (block.getType() != Material.AIR) block = block.getLocation().add(0, 1, 0).getBlock();

                this.locationSettings.addDropLocations(block);
            }

            DataFiles.data.getYamlFile().set("Locations.Spawned", getBlockList(locationSettings.getDropLocations()));
            DataFiles.data.save();
        } else {
            if (this.config.getProperty(ConfigKeys.envoys_max_drops_toggle) || this.config.getProperty(ConfigKeys.envoys_random_drops)) {
                if (this.locationSettings.getSpawnLocations().size() <= maxSpawns) {
                    this.locationSettings.addAllDropLocations(this.locationSettings.getSpawnLocations());
                } else {
                    while (this.locationSettings.getDropLocations().size() < maxSpawns) {
                        Block block = this.locationSettings.getSpawnLocations().get(new Random().nextInt(this.locationSettings.getSpawnLocations().size()));

                        this.locationSettings.addDropLocations(block);
                    }
                }
            } else {
                this.locationSettings.addAllDropLocations(this.locationSettings.getSpawnLocations());
            }
        }

        boolean envoyLocationsBroadcast = ConfigManager.getConfig().getProperty(ConfigKeys.envoys_locations_broadcast);

        if (envoyLocationsBroadcast) {
            StringBuilder locations = getStringBuilder();

            this.plugin.getServer().broadcast(AdvUtil.parse(Messages.envoy_locations.getMessage("{locations}", locations.toString().translateEscapes()).asString().translateEscapes()), "envoy.locations");
        }

        return this.locationSettings.getDropLocations();
    }

    @NotNull
    private StringBuilder getStringBuilder() {
        StringBuilder locations = new StringBuilder();

        int x = 1;
        for (Block block : this.locationSettings.getDropLocations()) {
            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{id}", String.valueOf(x));
            placeholders.put("{world}", block.getWorld().getName());
            placeholders.put("{x}", String.valueOf(block.getX()));
            placeholders.put("{y}", String.valueOf(block.getY()));
            placeholders.put("{z}", String.valueOf(block.getZ()));

            locations.append(Messages.location_format.getMessage(placeholders).asString().translateEscapes());
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
            this.plugin.getServer().broadcast(AdvUtil.parse(MiscUtils.getPrefix() + "<red>No tiers were found. Please delete the Tiers folder to allow it to remake the default tier files."));

            return false;
        }

        removeAllEnvoys();

        List<Block> dropLocations = generateSpawnLocations();

        if (this.config.getProperty(ConfigKeys.envoys_random_drops) && isCenterLoaded()) testCenter();

        if (dropLocations.isEmpty() || (this.config.getProperty(ConfigKeys.envoys_random_drops) && isCenterLoaded())) {
            setNextEnvoy(getEnvoyCooldown());
            resetWarnings();

            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.NO_LOCATIONS_FOUND);
            this.plugin.getServer().getPluginManager().callEvent(event);

            Messages.no_spawn_locations_found.broadcastMessage(false);

            return false;
        }

        for (UUID uuid : this.editorSettings.getEditors()) {
            Player player = this.plugin.getServer().getPlayer(uuid);

            this.editorSettings.removeFakeBlocks();

            if (player != null) {
                player.getInventory().removeItem(new ItemStack(Material.BEDROCK, 1));

                Messages.kicked_from_editor_mode.sendMessage(player);
            }
        }

        this.editorSettings.getEditors().clear();

        setEnvoyActive(true);
        int max = dropLocations.size();

        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("{amount}", String.valueOf(max));

        Messages.started.broadcastMessage(false, placeholder);

        if (this.config.getProperty(ConfigKeys.envoys_grace_period_toggle)) {
            this.countdownTimer = new CountdownTimer(this.config.getProperty(ConfigKeys.envoys_grace_period_timer), this.plugin);
            this.countdownTimer.scheduleTimer();
        }

        for (Block block : dropLocations) {
            if (block != null) {
                boolean spawnFallingBlock = false;

                if (this.config.getProperty(ConfigKeys.envoy_falling_block_toggle)) {
                    for (Entity entity : MiscUtils.getNearbyEntities(block.getLocation(), 40, 40, 40)) {
                        if (entity instanceof Player) {
                            spawnFallingBlock = true;
                            break;
                        }
                    }
                }

                if (spawnFallingBlock) {
                    //todo() load chunk async
                    if (!block.getChunk().isLoaded()) block.getChunk().load();

                    int fallingHeight = this.config.getProperty(ConfigKeys.envoy_falling_height);
                    Material material = Material.valueOf(this.config.getProperty(ConfigKeys.envoy_falling_block_type));

                    FallingBlock fallingBlock = block.getWorld().spawn(block.getLocation().add(.5, fallingHeight, .5), FallingBlock.class);
                    fallingBlock.setBlockData(material.createBlockData());

                    fallingBlock.setDropItem(false);
                    fallingBlock.setHurtEntities(false);

                    this.fallingBlocks.put(fallingBlock, block);
                } else {
                    Tier tier = pickRandomTier();

                    //todo() load chunk async
                    if (!block.getChunk().isLoaded()) block.getChunk().load();

                    block.setType(tier.getPlacedBlockMaterial());

                    if (tier.isHoloEnabled() && hasHologramPlugin()) this.hologramController.createHologram(block, tier);

                    addActiveEnvoy(block, tier);

                    this.locationSettings.addActiveLocation(block);

                    if (tier.getSignalFlareToggle() && block.getChunk().isLoaded()) startSignalFlare(block.getLocation(), tier);
                }
            }
        }

        this.runTimeTask = new FoliaRunnable(this.plugin.getServer().getGlobalRegionScheduler()) {
            @Override
            public void run() {
                EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.OUT_OF_TIME);
                plugin.getServer().getPluginManager().callEvent(event);

                Messages.ended.broadcastMessage(false);

                endEnvoyEvent();
            }
        }.runAtFixedRate(this.plugin, 0L, getTimeSeconds(this.config.getProperty(ConfigKeys.envoys_run_time)) * 20L);

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

        if (this.config.getProperty(ConfigKeys.envoys_run_time_toggle)) {
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
        @NotNull ScheduledTask task = new FoliaRunnable(this.plugin.getServer().getRegionScheduler(), loc) {
            @Override
            public void run() {

            }
        }.runAtFixedRate(this.plugin, getTimeSeconds(tier.getSignalFlareTimer()) * 20L, getTimeSeconds(tier.getSignalFlareTimer()) * 20L);

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
        this.centerString = MiscUtils.getUnBuiltLocation(this.center);

        DataFiles.data.getYamlFile().set("Center", this.centerString);
        DataFiles.data.save();
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

        if (this.config.getProperty(ConfigKeys.envoys_random_locations)) {
            locations.addAll(getLocationsFromStringList(DataFiles.data.getYamlFile().getStringList("Locations.Spawned")));
        } else {
            locations.addAll(this.locationSettings.getSpawnLocations());
        }

        for (Block spawnedLocation : locations) {
            if (spawnedLocation != null) {
                //todo() load chunk async
                if (!spawnedLocation.getChunk().isLoaded()) spawnedLocation.getChunk().load();

                spawnedLocation.setType(Material.AIR);
                stopSignalFlare(spawnedLocation.getLocation());

                if (hasHologramPlugin()) this.hologramController.removeAllHolograms();
            }
        }

        this.locationSettings.clearActiveLocations();
        this.locationSettings.clearDropLocations();

        DataFiles.data.getYamlFile().set("Locations.Spawned", new ArrayList<>());
        DataFiles.data.save();
    }

    private boolean testCenter() {
        if (isCenterLoaded()) { // Check to make sure the center exist and if not try to load it again.
            this.plugin.getLogger().warning("Attempting to fix Center location that failed.");
            loadCenter();

            if (isCenterLoaded()) { // If center still doesn't exist then it cancels the event.
                this.plugin.getLogger().warning("Debug Start");
                this.plugin.getLogger().warning("Center String: \"" + centerString + "'");
                this.plugin.getLogger().warning("Location Object: \"" + center.toString() + "'");
                this.plugin.getLogger().warning("World Exist: \"" + (center.getWorld() != null) + "'");
                this.plugin.getLogger().warning("Debug End");
                this.plugin.getLogger().severe("Failed to fix Center. Will try again next event.");
                return false;
            } else {
                this.plugin.getLogger().info("Center has been fixed and will continue event.");
            }
        }

        return true;
    }

    private void loadCenter() {
        YamlFile configuration = DataFiles.data.getYamlFile();

        if (configuration.contains("Center")) {
            this.centerString = configuration.getString("Center");
            if (this.centerString != null) this.center = MiscUtils.getBuiltLocation(centerString);
        } else {
            this.center = this.plugin.getServer().getWorlds().getFirst().getSpawnLocation();
        }

        if (this.center.getWorld() == null) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().severe("Failed to fix Center. Will try again next event.");
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

        if (this.config.getProperty(ConfigKeys.envoys_countdown)) {
            String time = this.config.getProperty(ConfigKeys.envoys_cooldown);

            cal = MiscUtils.getTimeFromString(time);
        } else {
            getEnvoyTime(cal);
        }

        return cal;
    }

    private Calendar getEnvoyRunTimeCalendar() {
        String time = this.config.getProperty(ConfigKeys.envoys_run_time).toLowerCase();

        return MiscUtils.getTimeFromString(time);
    }

    private void firework(Location loc, Tier tier) {
        List<Color> colors = tier.getFireworkColors();

        Firework firework = loc.getWorld().spawn(loc, Firework.class);

        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(colors).trail(true).flicker(false).build());
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);

        PersistentDataContainer container = firework.getPersistentDataContainer();

        container.set(PersistentKeys.no_firework_damage.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
    }

    //todo find a better away of doing this as it causes crashes with big radius.
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

        return this.cachedChances.get(new Random().nextInt(this.cachedChances.size()));
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
        List<String> strings = new ArrayList<>();

        for (Block block : stringList) {
            strings.add(MiscUtils.getUnBuiltLocation(block.getLocation()));
        }

        return strings;
    }

    private List<Block> getLocationsFromStringList(List<String> locationsList) {
        List<Block> locations = new ArrayList<>();

        for (String location : locationsList) {
            locations.add(MiscUtils.getBuiltLocation(location).getBlock());
        }

        return locations;
    }
}