package com.badbones69.crazyenvoys.api;

import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.FileManager.CustomFile;
import com.badbones69.crazyenvoys.api.FileManager.Files;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent.EnvoyEndReason;
import com.badbones69.crazyenvoys.api.events.EnvoyStartEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyStartEvent.EnvoyStartReason;
import com.badbones69.crazyenvoys.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.api.objects.*;
import com.badbones69.crazyenvoys.api.objects.misc.Prize;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.badbones69.crazyenvoys.controllers.CountdownTimer;
import com.badbones69.crazyenvoys.controllers.FireworkDamageAPI;
import com.badbones69.crazyenvoys.support.libraries.PluginSupport;
import com.badbones69.crazyenvoys.support.claims.WorldGuardSupport;
import com.badbones69.crazyenvoys.support.holograms.CMIHologramsSupport;
import com.badbones69.crazyenvoys.support.holograms.DecentHologramsSupport;
import com.badbones69.crazyenvoys.support.holograms.HolographicDisplaysSupport;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final FileManager fileManager = plugin.getFileManager();

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
     * Run this when you need to load the new locations.
     */
    public void load() {
        if (!envoyActive) envoyActive = false;

        // Clear all spawn locations.
        locationSettings.clearSpawnLocations();

        blacklistedBlocks.clear();
        envoySettings.loadSettings();
        cachedChances.clear();
        envoySettings.loadSettings();
        FileConfiguration data = Files.DATA.getFile();
        envoyTimeLeft = Calendar.getInstance();

        // Populate the array list.
        locationSettings.populateMap();

        if (fileManager.isLogging() && !locationSettings.getFailedLocations().isEmpty()) plugin.getLogger().info("Failed to load " + locationSettings.getFailedLocations().size() + " locations and will reattempt in 10s.");

        if (Calendar.getInstance().after(getNextEnvoy())) setEnvoyActive(false);

        loadCenter();

        if (envoySettings.isEnvoyRunTimerEnabled()) {
            Calendar cal = Calendar.getInstance();

            if (envoySettings.isEnvoyCooldownEnabled()) {
                autoTimer = true;
                cal.setTimeInMillis(data.getLong("Next-Envoy"));

                if (Calendar.getInstance().after(cal)) cal.setTimeInMillis(getEnvoyCooldown().getTimeInMillis());
            } else {
                autoTimer = false;
                getEnvoyTime(cal);
            }

            nextEnvoy = cal;
            startEnvoyCountDown();
            resetWarnings();
        } else {
            nextEnvoy = Calendar.getInstance();
        }

        //================================== Tiers Load ==================================//
        tiers.clear();

        for (CustomFile tierFile : fileManager.getCustomFiles()) {
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
                file.getStringList("Settings.Firework-Colors").forEach(color -> tier.addFireworkColor(methods.getColor(color)));
            }

            tier.setSignalFlareToggle(file.getBoolean("Settings.Signal-Flare.Toggle"));
            tier.setSignalFlareTimer(file.getString("Settings.Signal-Flare.Time"));

            if (file.getStringList("Settings.Signal-Flare.Colors").isEmpty()) {
                tier.setSignalFlareColors(Arrays.asList(Color.GRAY, Color.BLACK, Color.ORANGE));
            } else {
                file.getStringList("Settings.Signal-Flare.Colors").forEach(color -> tier.addSignalFlareColor(methods.getColor(color)));
            }

            for (String prizeID : file.getConfigurationSection("Prizes").getKeys(false)) {
                String path = "Prizes." + prizeID + ".";
                int chance = file.getInt(path + "Chance");
                List<String> commands = file.getStringList(path + "Commands");
                List<String> messages = file.getStringList(path + "Messages");
                boolean dropItems = file.getBoolean(path + "Drop-Items");
                List<ItemBuilder> items = ItemBuilder.convertStringList(file.getStringList(path + "Items"));
                tier.addPrize(new Prize(prizeID).setChance(chance).setDropItems(dropItems).setItemBuilders(items).setCommands(commands).setMessages(messages));
            }

            tiers.add(tier);
            cleanLocations();

            // Loading the blacklisted blocks.
            blacklistedBlocks.add(Material.WATER);
            blacklistedBlocks.add(Material.LILY_PAD);
            blacklistedBlocks.add(Material.LAVA);
            blacklistedBlocks.add(Material.CHORUS_PLANT);
            blacklistedBlocks.add(Material.KELP_PLANT);
            blacklistedBlocks.add(Material.TALL_GRASS);
            blacklistedBlocks.add(Material.CHORUS_FLOWER);
            blacklistedBlocks.add(Material.SUNFLOWER);
            blacklistedBlocks.add(Material.IRON_BARS);
            blacklistedBlocks.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
            blacklistedBlocks.add(Material.IRON_TRAPDOOR);
            blacklistedBlocks.add(Material.OAK_TRAPDOOR);
            blacklistedBlocks.add(Material.OAK_FENCE);
            blacklistedBlocks.add(Material.OAK_FENCE_GATE);
            blacklistedBlocks.add(Material.ACACIA_FENCE);
            blacklistedBlocks.add(Material.BIRCH_FENCE);
            blacklistedBlocks.add(Material.DARK_OAK_FENCE);
            blacklistedBlocks.add(Material.JUNGLE_FENCE);
            blacklistedBlocks.add(Material.NETHER_BRICK_FENCE);
            blacklistedBlocks.add(Material.SPRUCE_FENCE);
            blacklistedBlocks.add(Material.ACACIA_FENCE_GATE);
            blacklistedBlocks.add(Material.BIRCH_FENCE_GATE);
            blacklistedBlocks.add(Material.DARK_OAK_FENCE_GATE);
            blacklistedBlocks.add(Material.JUNGLE_FENCE_GATE);
            blacklistedBlocks.add(Material.SPRUCE_FENCE_GATE);
            blacklistedBlocks.add(Material.GLASS_PANE);
            blacklistedBlocks.add(Material.STONE_SLAB);
        }

        if (PluginSupport.WORLD_GUARD.isPluginEnabled() && PluginSupport.WORLD_EDIT.isPluginEnabled()) worldGuardSupportVersion = new WorldGuardSupport();

        if (PluginSupport.HOLOGRAPHIC_DISPLAYS.isPluginEnabled()) {
            hologramController = new HolographicDisplaysSupport();
            plugin.getLogger().info("HolographicDisplays support has been enabled.");
        } else if (PluginSupport.DECENT_HOLOGRAMS.isPluginEnabled()) {
            hologramController = new DecentHologramsSupport();
            plugin.getLogger().info("DecentHolograms support has been enabled.");
        } else if (PluginSupport.CMI.isPluginEnabled() && CMIModule.holograms.isEnabled()) {
            hologramController = new CMIHologramsSupport();
            plugin.getLogger().info("CMI Hologram support has been enabled.");
        } else plugin.getLogger().warning("No holograms plugin were found. If using CMI, make sure holograms module is enabled.");

        locationSettings.fixLocations(fileManager);

        flareSettings.load();
    }

    private void getEnvoyTime(Calendar cal) {
        String time = envoySettings.getEnvoyClockTime();
        int hour = Integer.parseInt(time.split(" ")[0].split(":")[0]);
        int min = Integer.parseInt(time.split(" ")[0].split(":")[1]);
        int c = Calendar.AM;

        if (time.split(" ")[1].equalsIgnoreCase("AM")) {
            c = Calendar.AM;
        } else if (time.split(" ")[1].equalsIgnoreCase("PM")) {
            c = Calendar.PM;
        }

        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.getTime(); // Without this makes the hours not change for some reason.
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.AM_PM, c);

        if (cal.before(Calendar.getInstance())) cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
    }

    /**
     * Run this when you need to save the locations.
     */
    public void unload() {
        removeAllEnvoys();

        Files.DATA.getFile().set("Next-Envoy", getNextEnvoy().getTimeInMillis());
        Files.DATA.saveFile();

        locationSettings.clearSpawnLocations();

        coolDownSettings.clearCoolDowns();
    }

    /**
     * Used when the plugin starts to control the count-down and when the event starts
     */
    public void startEnvoyCountDown() {
        cancelEnvoyCooldownTime();
        coolDownTask = new BukkitRunnable() {
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
                            Messages.WARNING.broadcastMessage(false, placeholder);
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
                                Messages.NOT_ENOUGH_PLAYERS.broadcastMessage(false, placeholder);
                                setNextEnvoy(getEnvoyCooldown());
                                resetWarnings();
                                return;
                            }
                        }

                        if (envoySettings.isRandomLocationsEnabled() && center.getWorld() == null) {
                            plugin.getLogger().info("The envoy center's world can't be found and so envoy has been canceled.");
                            plugin.getLogger().info("Center String: " + centerString);
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
        }.runTaskTimer(plugin, 20, 20);
    }

    /**
     * @param block The location you want the tier from.
     * @return The tier that location is.
     */
    public Tier getTier(Block block) {
        return activeEnvoys.get(block);
    }

    /**
     * @return True if the envoy event is currently happening and false if not.
     */
    public boolean isEnvoyActive() {
        return envoyActive;
    }

    /**
     * Despawns all the active crates.
     */
    public void removeAllEnvoys() {
        envoyActive = false;
        cleanLocations();

        for (Block block : getActiveEnvoys()) {
            if (!block.getChunk().isLoaded()) block.getChunk().load();

            block.setType(Material.AIR);
            stopSignalFlare(block.getLocation());
        }

        fallingBlocks.keySet().forEach(Entity :: remove);

        if (hasHologramPlugin()) hologramController.removeAllHolograms();

        fallingBlocks.clear();
        activeEnvoys.clear();
    }

    public WorldGuardSupport getWorldGuardPluginSupport() {
        return worldGuardSupportVersion;
    }

    public HologramController getHologramController() {
        return hologramController;
    }

    public boolean hasHologramPlugin() {
        return hologramController != null;
    }

    /**
     * @return All the envoys that are active.
     */
    public Set<Block> getActiveEnvoys() {
        return activeEnvoys.keySet();
    }

    /**
     * @param block The location you are checking.
     * @return Turn if it is and false if not.
     */
    public boolean isActiveEnvoy(Block block) {
        return activeEnvoys.containsKey(block);
    }

    /**
     * @param block The location you wish to add.
     */
    public void addActiveEnvoy(Block block, Tier tier) {
        activeEnvoys.put(block, tier);
    }

    /**
     * @param block The location you wish to remove.
     */
    public void removeActiveEnvoy(Block block) {
        activeEnvoys.remove(block);
    }

    /**
     * @return The next envoy time as a calendar.
     */
    public Calendar getNextEnvoy() {
        return nextEnvoy;
    }

    /**
     * @param cal A calendar that has the next time the envoy will happen.
     */
    public void setNextEnvoy(Calendar cal) {
        nextEnvoy = cal;
    }

    /**
     * @return The time till the next envoy.
     */
    public String getNextEnvoyTime() {
        String message = methods.convertTimeToString(getNextEnvoy());

        if (message.equals("0" + Messages.SECOND.getMessage())) message = Messages.ON_GOING.getMessage();

        return message;
    }

    /**
     * @return All falling blocks are currently going.
     */
    public Map<Entity, Block> getFallingBlocks() {
        return fallingBlocks;
    }

    /**
     * @param entity Remove a falling block from the list.
     */
    public void removeFallingBlock(Entity entity) {
        fallingBlocks.remove(entity);
    }

    /**
     * Call when you want to set the new warning.
     */
    public void resetWarnings() {
        warnings.clear();
        envoySettings.getEnvoyWarnings().forEach(time -> addWarning(makeWarning(time)));
    }

    /**
     * @param cal When adding a new warning.
     */
    public void addWarning(Calendar cal) {
        warnings.add(cal);
    }

    /**
     * @return All the current warnings.
     */
    public List<Calendar> getWarnings() {
        return warnings;
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
        String message = methods.convertTimeToString(envoyTimeLeft);

        if (message.equals("0" + Messages.SECOND.getMessage())) message = Messages.NOT_RUNNING.getMessage();

        return message;
    }

    /**
     * Call when the run time needs canceled.
     */
    public void cancelEnvoyRunTime() {
        try {
            runTimeTask.cancel();
        } catch (Exception ignored) {}
    }

    /**
     * Call when the cool downtime needs canceled.
     */
    public void cancelEnvoyCooldownTime() {
        try {
            coolDownTask.cancel();
        } catch (Exception ignored) {}
    }

    public List<Block> generateSpawnLocations() {
        int maxSpawns;

        if (envoySettings.isMaxCrateEnabled()) {
            maxSpawns = envoySettings.getMaxCrates();
        } else if (envoySettings.isRandomAmount()) {
            // Generates a random number between the min and max settings
            maxSpawns = this.random.nextInt(envoySettings.getMaxCrates() + 1 - envoySettings.getMinCrates()) + envoySettings.getMinCrates();
        } else {
            maxSpawns = envoySettings.isRandomLocationsEnabled() ? envoySettings.getMaxCrates() : locationSettings.getActiveLocations().size();
        }

        if (maxSpawns > (Math.pow(envoySettings.getMaxRadius() * 2, 2) - Math.pow((envoySettings.getMinRadius() * 2 + 1), 2))) {
            maxSpawns = (int) (Math.pow(envoySettings.getMaxRadius() * 2, 2) - Math.pow((envoySettings.getMinRadius() * 2 + 1), 2));
            plugin.getLogger().warning("Crate spawn amount is larger than the area that was provided. Spawning " + maxSpawns + " crates instead.");
        }

        if (envoySettings.isRandomLocationsEnabled()) {
            if (!testCenter()) return new ArrayList<>();

            List<Block> minimumRadiusBlocks = getBlocks(center.clone(), envoySettings.getMinRadius());

            while (locationSettings.getDropLocations().size() < maxSpawns) {
                int maxRadius = envoySettings.getMaxRadius();
                Location location = center.clone();
                location.add(-(maxRadius) + random.nextInt(maxRadius * 2), 0, -(maxRadius) + random.nextInt(maxRadius * 2));
                location = location.getWorld().getHighestBlockAt(location).getLocation();

                if (!location.getChunk().isLoaded() && !location.getChunk().load()) continue;

                if (location.getBlockY() <= 0 ||
                        minimumRadiusBlocks.contains(location.getBlock()) || minimumRadiusBlocks.contains(location.clone().add(0, 1, 0).getBlock()) ||
                        locationSettings.getDropLocations().contains(location.getBlock()) || locationSettings.getDropLocations().contains(location.clone().add(0, 1, 0).getBlock()) ||
                        blacklistedBlocks.contains(location.getBlock().getType())) continue;

                Block block = location.getBlock();
                if (block.getType() != Material.AIR) block = block.getLocation().add(0, 1, 0).getBlock();

                locationSettings.addDropLocations(block);
            }

            Files.DATA.getFile().set("Locations.Spawned", getBlockList(locationSettings.getDropLocations()));
            Files.DATA.saveFile();
        } else {
            if (envoySettings.isMaxCrateEnabled()) {
                if (locationSettings.getSpawnLocations().size() <= maxSpawns) {
                    locationSettings.addAllDropLocations(locationSettings.getSpawnLocations());
                } else {
                    while (locationSettings.getDropLocations().size() < maxSpawns) {
                        Block block = locationSettings.getSpawnLocations().get(random.nextInt(locationSettings.getSpawnLocations().size()));

                        locationSettings.addDropLocations(block);
                    }
                }
            } else {
                locationSettings.addAllDropLocations(locationSettings.getSpawnLocations());
            }
        }

        boolean envoyLocationsBroadcast = Files.CONFIG.getFile().getBoolean("Settings.Envoy-Locations-Broadcast");

        if (envoyLocationsBroadcast) {
            StringBuilder locations = new StringBuilder();
            int x = 1;
            for (Block b : locationSettings.getSpawnLocations()) {
                locations.append(Messages.LOCATION_FORMAT.getMessage()
                        .replace("%id%", x + "")
                        .replace("%world%", b.getWorld().getName())
                        .replace("%x%", b.getX() + "")
                        .replace("%y%", b.getY() + "")
                        .replace("%z%", b.getZ() + ""));
                x += 1;
            }

            plugin.getServer().broadcast(Messages.CRATE_LOCATIONS.getMessage().replace("%locations%", locations.toString()).translateEscapes(), "envoy.locations");
        }

        return locationSettings.getDropLocations();
    }

    /**
     * Starts the envoy event.
     *
     * @return true if the event started successfully and false if it had an issue.
     */
    public boolean startEnvoyEvent() {
        // Called before locations are generated due to it setting those locations to air and causing
        // crates to spawn in the ground when not using falling blocks.

        if (tiers.isEmpty()) {
            plugin.getServer().broadcastMessage(methods.getPrefix() + methods.color("&cNo tiers were found. Please delete the Tiers folder" + " to allow it to remake the default tier files."));
            return false;
        }

        removeAllEnvoys();

        List<Block> dropLocations = generateSpawnLocations();

        if (envoySettings.isRandomLocationsEnabled() && !isCenterLoaded()) testCenter();

        if (dropLocations.isEmpty() || (envoySettings.isRandomLocationsEnabled() && !isCenterLoaded())) {
            setNextEnvoy(getEnvoyCooldown());
            resetWarnings();
            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.NO_LOCATIONS_FOUND);
            plugin.getServer().getPluginManager().callEvent(event);
            Messages.NO_SPAWN_LOCATIONS_FOUND.broadcastMessage(false);
            return false;
        }

        for (Player player : editorSettings.getEditors()) {
            editorSettings.removeFakeBlocks();
            player.getInventory().removeItem(new ItemStack(Material.BEDROCK, 1));
            Messages.KICKED_FROM_EDITOR_MODE.sendMessage(player);
        }

        editorSettings.getEditors().clear();

        setEnvoyActive(true);
        int max = dropLocations.size();
        HashMap<String, String> placeholder = new HashMap<>();
        placeholder.put("%amount%", max + "");
        placeholder.put("%Amount%", max + "");
        Messages.STARTED.broadcastMessage(false, placeholder);

        if (envoySettings.isEnvoyCountDownEnabled()) {
            countdownTimer = new CountdownTimer(envoySettings.getEnvoyCountDownTimer());

            countdownTimer.scheduleTimer();
        }

        for (Block block : dropLocations) {
            if (block != null && block.getWorld() != null) {
                boolean spawnFallingBlock = false;

                if (envoySettings.isFallingBlocksEnabled()) {
                    for (Entity entity : methods.getNearbyEntities(block.getLocation(), 40, 40, 40)) {
                        if (entity instanceof Player) {
                            spawnFallingBlock = true;
                            break;
                        }
                    }
                }

                if (spawnFallingBlock) {
                    if (!block.getChunk().isLoaded()) block.getChunk().load();

                    int fallingHeight = envoySettings.getFallingHeight();
                    Material fallingBlock = envoySettings.getFallingBlockMaterial();
                    byte fallingDurability = (byte) envoySettings.getFallingBlockDurability();

                    FallingBlock chest = block.getWorld().spawnFallingBlock(block.getLocation().add(.5, fallingHeight, .5), fallingBlock, fallingDurability);
                    chest.setDropItem(false);
                    chest.setHurtEntities(false);
                    fallingBlocks.put(chest, block);

                } else {
                    Tier tier = pickRandomTier();

                    if (!block.getChunk().isLoaded()) block.getChunk().load();

                    block.setType(tier.getPlacedBlockMaterial());

                    if (tier.isHoloEnabled() && hasHologramPlugin()) hologramController.createHologram(block, tier);

                    addActiveEnvoy(block, tier);
                    locationSettings.addActiveLocation(block);

                    if (tier.getSignalFlareToggle()) startSignalFlare(block.getLocation(), tier);
                }
            }
        }

        runTimeTask = new BukkitRunnable() {
            @Override
            public void run() {
                EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.OUT_OF_TIME);
                plugin.getServer().getPluginManager().callEvent(event);
                Messages.ENDED.broadcastMessage(false);
                endEnvoyEvent();
            }
        }.runTaskLater(plugin, getTimeSeconds(envoySettings.getEnvoyRunTimer()) * 20L);

        envoyTimeLeft = getEnvoyRunTimeCalendar();

        return true;
    }

    /**
     * Ends the envoy event.
     */
    public void endEnvoyEvent() {
        removeAllEnvoys();
        setEnvoyActive(false);
        cancelEnvoyRunTime();

        if (envoySettings.isEnvoyRunTimerEnabled()) {
            setNextEnvoy(getEnvoyCooldown());
            resetWarnings();
        }

        coolDownSettings.clearCoolDowns();
    }

    /**
     * Get a list of all the tiers.
     *
     * @return List of all the tiers.
     */
    public List<Tier> getTiers() {
        return tiers;
    }

    /**
     * Get a tier from its name.
     *
     * @param tierName The name of the tier.
     * @return Returns a tier or will return null if not tier is found.
     */
    public Tier getTier(String tierName) {
        for (Tier tier : tiers) {
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
        }.runTaskTimer(plugin, getTimeSeconds(tier.getSignalFlareTimer()) * 20L, getTimeSeconds(tier.getSignalFlareTimer()) * 20L);

        activeSignals.put(loc, task);
    }

    /**
     * @param loc The location that the signal is stopping.
     */
    public void stopSignalFlare(Location loc) {
        try {
            activeSignals.get(loc).cancel();
        } catch (Exception ignored) {}

        activeSignals.remove(loc);
    }

    /**
     * @return The center location for the random crates.
     */
    public Location getCenter() {
        return center;
    }

    /**
     * Sets the center location for the random crates.
     *
     * @param loc The new center location.
     */
    public void setCenter(Location loc) {
        center = loc;
        centerString = methods.getUnBuiltLocation(center);
        Files.DATA.getFile().set("Center", methods.getUnBuiltLocation(center));
        Files.DATA.saveFile();
    }

    /**
     * Check if a player is ignoring the messages.
     *
     * @param uuid The player's UUID.
     * @return True if they are ignoring them and false if not.
     */
    public boolean isIgnoringMessages(UUID uuid) {
        return ignoreMessages.contains(uuid);
    }

    /**
     * Make a player ignore the messages.
     *
     * @param uuid The player's UUID.
     */
    public void addIgnorePlayer(UUID uuid) {
        ignoreMessages.add(uuid);
    }

    /**
     * Make a player stop ignoring the messages.
     *
     * @param uuid The player's UUID.
     */
    public void removeIgnorePlayer(UUID uuid) {
        ignoreMessages.remove(uuid);
    }

    /**
     * Used to clean all spawn locations and set them back to air.
     */
    public void cleanLocations() {
        List<Block> locations = new ArrayList<>(locationSettings.getActiveLocations());

        if (envoySettings.isRandomLocationsEnabled()) {
            locations.addAll(getLocationsFromStringList(Files.DATA.getFile().getStringList("Locations.Spawned")));
        } else {
            locations.addAll(locationSettings.getSpawnLocations());
        }

        for (Block spawnedLocation : locations) {
            if (spawnedLocation != null) {
                if (!spawnedLocation.getChunk().isLoaded()) spawnedLocation.getChunk().load();

                spawnedLocation.setType(Material.AIR);
                stopSignalFlare(spawnedLocation.getLocation());

                if (hasHologramPlugin()) hologramController.removeAllHolograms();
            }
        }

        locationSettings.clearActiveLocations();
        locationSettings.clearDropLocations();

        Files.DATA.getFile().set("Locations.Spawned", new ArrayList<>());
        Files.DATA.saveFile();
    }

    private boolean testCenter() {
        if (!isCenterLoaded()) { // Check to make sure the center exist and if not try to load it again.
            plugin.getLogger().info("Attempting to fix Center location that failed.");
            loadCenter();

            if (!isCenterLoaded()) { // If center still doesn't exist then it cancels the event.
                plugin.getLogger().info("Debug Start");
                plugin.getLogger().info("Center String: \"" + centerString + "'");
                plugin.getLogger().info("Location Object: \"" + center.toString() + "'");
                plugin.getLogger().info("World Exist: \"" + (center.getWorld() != null) + "'");
                plugin.getLogger().info("Debug End");
                plugin.getLogger().info("Failed to fix Center. Will try again next event.");
                return false;
            } else {
                plugin.getLogger().info("Center has been fixed and will continue event.");
            }
        }

        return true;
    }

    private void loadCenter() {
        FileConfiguration data = Files.DATA.getFile();

        if (data.contains("Center")) {
            centerString = data.getString("Center");
            assert centerString != null;
            center = methods.getBuiltLocation(centerString);
        } else {
            center = plugin.getServer().getWorlds().get(0).getSpawnLocation();
        }

        if (center.getWorld() == null) {
            if (fileManager.isLogging()) plugin.getLogger().info("Failed to fix Center. Will try again next event.");
        }
    }

    private boolean isCenterLoaded() {
        return center.getWorld() != null;
    }

    private void setEnvoyActive(boolean toggle) {
        envoyActive = toggle;
    }

    private Calendar getEnvoyCooldown() {
        Calendar cal = Calendar.getInstance();

        if (envoySettings.isEnvoyCooldownEnabled()) {
            String time = envoySettings.getEnvoyCooldown();

            cal = methods.getTimeFromString(time);
        } else {
            getEnvoyTime(cal);
        }

        return cal;
    }

    private Calendar getEnvoyRunTimeCalendar() {
        String time = envoySettings.getEnvoyRunTimer().toLowerCase();

        return methods.getTimeFromString(time);
    }

    private void firework(Location loc, Tier tier) {
        List<Color> colors = tier.getFireworkColors();

        Firework firework = loc.getWorld().spawn(loc, Firework.class);

        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(colors).trail(true).flicker(false).build());
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);

        fireworkDamageAPI.addFirework(firework);
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
        if (cachedChances.isEmpty()) {
            for (Tier tier : tiers) {
                for (int i = 0; i < tier.getSpawnChance(); i++) {
                    cachedChances.add(tier);
                }
            }
        }

        return cachedChances.get(random.nextInt(cachedChances.size()));
    }

    /**
     * @param location The location that you want to check.
     */
    public boolean isLocation(Location location) {
        for (Block block : locationSettings.getSpawnLocations()) {
            if (block.getLocation().equals(location)) return true;
        }

        return false;
    }

    public CountdownTimer getCountdownTimer() {
        return countdownTimer;
    }

    // Get world location.

    private List<String> getBlockList(List<Block> stringList) {
        ArrayList<String> strings = new ArrayList<>();

        for (Block block : stringList) {
            strings.add(methods.getUnBuiltLocation(block.getLocation()));
        }

        return strings;
    }

    private List<Block> getLocationsFromStringList(List<String> locationsList) {
        ArrayList<Block> locations = new ArrayList<>();

        for (String location : locationsList) {
            locations.add(methods.getBuiltLocation(location).getBlock());
        }

        return locations;
    }
}