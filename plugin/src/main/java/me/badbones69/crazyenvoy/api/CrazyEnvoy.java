package me.badbones69.crazyenvoy.api;

import me.badbones69.crazyenvoy.Methods;
import me.badbones69.crazyenvoy.api.FileManager.CustomFile;
import me.badbones69.crazyenvoy.api.FileManager.Files;
import me.badbones69.crazyenvoy.api.enums.Messages;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent.EnvoyEndReason;
import me.badbones69.crazyenvoy.api.events.EnvoyStartEvent;
import me.badbones69.crazyenvoy.api.events.EnvoyStartEvent.EnvoyStartReason;
import me.badbones69.crazyenvoy.api.objects.*;
import me.badbones69.crazyenvoy.controllers.EditControl;
import me.badbones69.crazyenvoy.controllers.EnvoyControl;
import me.badbones69.crazyenvoy.controllers.FireworkDamageAPI;
import me.badbones69.crazyenvoy.multisupport.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CrazyEnvoy {
	
	private static CrazyEnvoy instance = new CrazyEnvoy();
	private FileManager fileManager = FileManager.getInstance();
	private boolean isLogging = fileManager.isLogging();
	private EnvoySettings envoySettings = EnvoySettings.getInstance();
	private BukkitTask runTimeTask;
	private BukkitTask coolDownTask;
	private Calendar nextEnvoy;
	private Calendar nextClean;
	private Calendar envoyTimeLeft;
	private boolean envoyActive = false;
	private boolean autoTimer = true;
	private WorldGuardVersion worldGuardVersion;
	private List<Material> blacklistedBlocks = new ArrayList<>();
	private List<UUID> ignoreMessages = new ArrayList<>();
	private List<Calendar> warnings = new ArrayList<>();
	private List<Block> spawnLocations = new ArrayList<>();
	private List<Block> spawnedLocations = new ArrayList<>();
	private List<Entity> fallingBlocks = new ArrayList<>();
	private Location center;
	private String centerString;
	private HashMap<Block, Tier> activeEnvoys = new HashMap<>();
	private HashMap<Location, BukkitTask> activeSignals = new HashMap<>();
	private List<Tier> tiers = new ArrayList<>();
	private Plugin plugin;
	
	/**
	 * Get the instance of the envoy plugin.
	 * @return The instance of the envoy plugin.
	 */
	public static CrazyEnvoy getInstance() {
		return instance;
	}
	
	/**
	 * Run this when you need to load the new locations.
	 */
	public void load() {
		if(!envoyActive) {
			envoyActive = false;
		}
		spawnLocations.clear();
		blacklistedBlocks.clear();
		envoySettings.loadSettings();
		plugin = Bukkit.getPluginManager().getPlugin("CrazyEnvoy");
		FileConfiguration data = Files.DATA.getFile();
		envoyTimeLeft = Calendar.getInstance();
		List<String> failedLocations = new ArrayList<>();
		for(String location : data.getStringList("Locations.Spawns")) {
			try {
				spawnLocations.add(getLocationFromString(location).getBlock());
			}catch(Exception ignore) {
				//Error when trying to get location and so it was skipped.
				failedLocations.add(location);
			}
		}
		if(fileManager.isLogging() && !failedLocations.isEmpty()) System.out.println("[CrazyEnvoy] Failed to load " + failedLocations.size() + " locations and will reattempt in 10s.");
		if(Calendar.getInstance().after(getNextEnvoy())) {
			setEnvoyActive(false);
		}
		if(data.contains("Center")) {
			center = getLocationFromString(data.getString("Center"));
			centerString = data.getString("Center");
		}else {
			center = Bukkit.getWorlds().get(0).getSpawnLocation();
		}
		if(envoySettings.isEnvoyRunTimerEnabled()) {
			Calendar cal = Calendar.getInstance();
			if(envoySettings.isEnvoyCooldownEnabled()) {
				autoTimer = true;
				cal.setTimeInMillis(data.getLong("Next-Envoy"));
				if(Calendar.getInstance().after(cal)) {
					cal.setTimeInMillis(getEnvoyCooldown().getTimeInMillis());
				}
			}else {
				autoTimer = false;
				String time = envoySettings.getEnvoyClockTime();
				int hour = Integer.parseInt(time.split(" ")[0].split(":")[0]);
				int min = Integer.parseInt(time.split(" ")[0].split(":")[1]);
				int c = Calendar.AM;
				if(time.split(" ")[1].equalsIgnoreCase("AM")) {
					c = Calendar.AM;
				}else if(time.split(" ")[1].equalsIgnoreCase("PM")) {
					c = Calendar.PM;
				}
				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.getTime(); // Without this makes the hours not change for some reason.
				cal.set(Calendar.MINUTE, min);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.AM_PM, c);
				if(cal.before(Calendar.getInstance())) {
					cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
				}
			}
			nextEnvoy = cal;
			startEnvoyCountDown();
			resetWarnings();
		}else {
			nextEnvoy = Calendar.getInstance();
		}
		//================================== Tiers Load ==================================//
		tiers.clear();
		for(CustomFile customFile : fileManager.getCustomFiles()) {
			Tier tier = new Tier(customFile.getName());
			FileConfiguration file = customFile.getFile();
			tier.setUseChance(file.getBoolean("Settings.Use-Chance"));
			tier.setSpawnChance(file.getInt("Settings.Spawn-Chance"));
			tier.setBulkToggle(file.getBoolean("Settings.Bulk-Prizes.Toggle"));
			tier.setBulkRandom(file.getBoolean("Settings.Bulk-Prizes.Random"));
			tier.setBulkMax(file.getInt("Settings.Bulk-Prizes.Max-Bulk"));
			tier.setHoloToggle(file.getBoolean("Settings.Hologram-Toggle"));
			tier.setHoloHight(file.getDouble("Settings.Hologram-Height", 1.5));
			tier.setHoloMessage(file.getStringList("Settings.Hologram"));
			ItemBuilder placedBlock = new ItemBuilder().setMaterial(file.getString("Settings.Placed-Block"));
			tier.setPlacedBlockMaterial(placedBlock.getMaterial());
			tier.setPlacedBlockMetaData(placedBlock.getDamage());
			tier.setFireworkToggle(file.getBoolean("Settings.Firework-Toggle"));
			for(String color : file.getStringList("Settings.Firework-Colors")) {
				tier.addFireworkColor(Methods.getColor(color));
			}
			tier.setSignalFlareToggle(file.getBoolean("Settings.Signal-Flare.Toggle"));
			tier.setSignalFlareTimer(file.getString("Settings.Signal-Flare.Time"));
			for(String color : file.getStringList("Settings.Signal-Flare.Colors")) {
				tier.addSignalFlareColor(Methods.getColor(color));
			}
			for(String prizeID : file.getConfigurationSection("Prizes").getKeys(false)) {
				String path = "Prizes." + prizeID + ".";
				int chance = file.getInt(path + "Chance");
				List<String> commands = file.getStringList(path + "Commands");
				List<String> messages = file.getStringList(path + "Messages");
				boolean dropItems = file.getBoolean(path + "Drop-Items");
				List<ItemBuilder> items = ItemBuilder.convertStringList(file.getStringList(path + "Items"));
				tier.addPrize(new Prize(prizeID).setChance(chance).setDropItems(dropItems).setItemBuilders(items).setCommands(commands).setMessages(messages));
			}
			tiers.add(tier);
			//Clean up any old spawned crate locations.
			if(data.contains("Locations.Spawned")) {
				spawnedLocations.addAll(getLocationsFromStringList(data.getStringList("Locations.Spawned")));
			}
			nextClean = Calendar.getInstance();
			if(data.contains("Next-Clean")) {
				nextClean.setTimeInMillis(data.getLong("Next-Clean"));
			}else {
				nextClean.add(Calendar.DATE, 1);
				data.set("Next-Clean", nextClean.getTimeInMillis());
				Files.DATA.saveFile();
			}
			cleanLocations();
			//Loading the blacklisted blocks.
			if(Version.getCurrentVersion().isNewer(Version.v1_12_R1)) {
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
			}else {
				blacklistedBlocks.add(Material.WATER);
				blacklistedBlocks.add(Material.matchMaterial("STATIONARY_WATER"));
				blacklistedBlocks.add(Material.matchMaterial("WATER_LILY"));
				blacklistedBlocks.add(Material.LAVA);
				blacklistedBlocks.add(Material.matchMaterial("STATIONARY_LAVA"));
				blacklistedBlocks.add(Material.matchMaterial("CROPS"));
				blacklistedBlocks.add(Material.matchMaterial("LONG_GRASS"));
				blacklistedBlocks.add(Material.matchMaterial("YELLOW_FLOWER"));
				blacklistedBlocks.add(Material.matchMaterial("IRON_FENCE"));
				blacklistedBlocks.add(Material.matchMaterial("IRON_PLATE"));
				blacklistedBlocks.add(Material.IRON_TRAPDOOR);
				blacklistedBlocks.add(Material.matchMaterial("TRAP_DOOR"));
				blacklistedBlocks.add(Material.matchMaterial("FENCE"));
				blacklistedBlocks.add(Material.matchMaterial("FENCE_GATE"));
				blacklistedBlocks.add(Material.ACACIA_FENCE);
				blacklistedBlocks.add(Material.BIRCH_FENCE);
				blacklistedBlocks.add(Material.DARK_OAK_FENCE);
				blacklistedBlocks.add(Material.JUNGLE_FENCE);
				blacklistedBlocks.add(Material.matchMaterial("NETHER_FENCE"));
				blacklistedBlocks.add(Material.SPRUCE_FENCE);
				blacklistedBlocks.add(Material.ACACIA_FENCE_GATE);
				blacklistedBlocks.add(Material.BIRCH_FENCE_GATE);
				blacklistedBlocks.add(Material.DARK_OAK_FENCE_GATE);
				blacklistedBlocks.add(Material.JUNGLE_FENCE_GATE);
				blacklistedBlocks.add(Material.SPRUCE_FENCE_GATE);
				blacklistedBlocks.add(Material.matchMaterial("STAINED_GLASS_PANE"));
				blacklistedBlocks.add(Material.matchMaterial("STONE_SLAB2"));
			}
		}
		if(Support.WORLD_GUARD.isPluginLoaded() && Support.WORLD_EDIT.isPluginLoaded()) {
			worldGuardVersion = Version.getCurrentVersion().isNewer(Version.v1_12_R1) ? new WorldGuard_v7() : new WorldGuard_v6();
		}
		if(!failedLocations.isEmpty()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if(fileManager.isLogging()) System.out.println("[CrazyEnvoy] Attempting to fix " + failedLocations.size() + " locations that failed.");
					int failed = 0;
					int fixed = 0;
					for(String location : failedLocations) {
						try {
							spawnLocations.add(getLocationFromString(location).getBlock());
							fixed++;
						}catch(Exception ignore) {
							//Error when trying to get location and so it was skipped.
							failed++;
						}
					}
					if(fileManager.isLogging() && fixed > 0) System.out.println("[CrazyEnvoy] Was able to fix " + fixed + " locations that failed.");
					if(fileManager.isLogging() && failed > 0) System.out.println("[CrazyEnvoy] Failed to fix " + failed + " locations and will not reattempt.");
				}
			}.runTaskLater(plugin, 200);
		}
		Flare.load();
	}
	
	/**
	 * Run this when you need to save the locations.
	 */
	public void unload() {
		deSpawnCrates();
		Files.DATA.getFile().set("Next-Envoy", getNextEnvoy().getTimeInMillis());
		Files.DATA.saveFile();
		spawnLocations.clear();
		EnvoyControl.clearCooldowns();
	}
	
	/**
	 * Used when the plugin starts to control the count down and when the event starts
	 */
	public void startEnvoyCountDown() {
		cancelEnvoyCooldownTime();
		coolDownTask = new BukkitRunnable() {
			@Override
			public void run() {
				if(!isEnvoyActive()) {
					Calendar cal = Calendar.getInstance();
					cal.clear(Calendar.MILLISECOND);
					for(Calendar warn : getWarnings()) {
						Calendar check = Calendar.getInstance();
						check.setTimeInMillis(warn.getTimeInMillis());
						check.clear(Calendar.MILLISECOND);
						if(check.compareTo(cal) == 0) {
							HashMap<String, String> placeholder = new HashMap<>();
							placeholder.put("%time%", getNextEnvoyTime());
							placeholder.put("%Time%", getNextEnvoyTime());
							Messages.WARNING.broadcastMessage(false, placeholder);
						}
					}
					Calendar next = Calendar.getInstance();
					next.setTimeInMillis(getNextEnvoy().getTimeInMillis());
					next.clear(Calendar.MILLISECOND);
					if(next.compareTo(cal) <= 0) {
						if(!isEnvoyActive()) {
							if(envoySettings.isMinPlayersEnabled()) {
								int online = Bukkit.getServer().getOnlinePlayers().size();
								if(online < envoySettings.getMinPlayers()) {
									HashMap<String, String> placeholder = new HashMap<>();
									placeholder.put("%amount%", online + "");
									placeholder.put("%Amount%", online + "");
									Messages.NOT_ENOUGH_PLAYERS.broadcastMessage(false, placeholder);
									setNextEnvoy(getEnvoyCooldown());
									resetWarnings();
									return;
								}
							}
							if(envoySettings.isRandomLocationsEnabled()) {
								if(center.getWorld() == null) {
									System.out.println("[CrazyEnvoy] The envoy center's world can't be found and so envoy has been canceled.");
									System.out.println("Center String: " + centerString);
									setNextEnvoy(getEnvoyCooldown());
									resetWarnings();
									return;
								}
							}
							EnvoyStartEvent event = new EnvoyStartEvent(autoTimer ? EnvoyStartReason.AUTO_TIMER : EnvoyStartReason.SPECIFIED_TIME);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()) {
								startEnvoyEvent();
							}
						}
					}
				}
			}
		}.runTaskTimer(plugin, 20, 20);
	}
	
	/**
	 *
	 * @param block The location you want the tier from.
	 * @return The tier that location is.
	 */
	public Tier getTier(Block block) {
		return activeEnvoys.get(block);
	}
	
	/**
	 *
	 * @return True if the envoy event is currently happening and false if not.
	 */
	public boolean isEnvoyActive() {
		return envoyActive;
	}
	
	/**
	 * Despawns all of the active crates.
	 */
	public void deSpawnCrates() {
		envoyActive = false;
		cleanLocations();
		for(Block block : getActiveEnvoys()) {
			block.setType(Material.AIR);
			stopSignalFlare(block.getLocation());
		}
		for(Entity en : fallingBlocks) {
			en.remove();
		}
		if(Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
			HolographicSupport.removeAllHolograms();
		}
		if(Support.HOLOGRAMS.isPluginLoaded()) {
			HologramsSupport.removeAllHolograms();
		}
		if(Support.CMI.isPluginLoaded()) {
			CMISupport.removeAllHolograms();
		}
		fallingBlocks.clear();
		activeEnvoys.clear();
	}
	
	public WorldGuardVersion getWorldGuardSupport() {
		return worldGuardVersion;
	}
	
	/**
	 *
	 * @return All the location the chests will spawn.
	 */
	public List<Block> getSpawnLocations() {
		return spawnLocations;
	}
	
	/**
	 *
	 * @param location The location that you want to check.
	 */
	public boolean isLocation(Location location) {
		for(Block block : spawnLocations) {
			if(block.getLocation().equals(location)) {
				return true;
			}
		}
		return false;
	}
	
	public void saveSpawnLocations() {
		ArrayList<String> locs = new ArrayList<>();
		for(Block block : spawnLocations) {
			try {
				if(block.getWorld() != null) {
					locs.add(getStringFromLocation(block.getLocation()));
				}
			}catch(Exception e) {
			}
		}
		Files.DATA.getFile().set("Locations.Spawns", locs);
		Files.DATA.saveFile();
	}
	
	/**
	 *
	 * @return All the active envoys that are active.
	 */
	public Set<Block> getActiveEnvoys() {
		return activeEnvoys.keySet();
	}
	
	/**
	 *
	 * @param block The location your are checking.
	 * @return Turn if it is and false if not.
	 */
	public boolean isActiveEnvoy(Block block) {
		return activeEnvoys.containsKey(block);
	}
	
	/**
	 *
	 * @param block The location you wish to add.
	 */
	public void addActiveEnvoy(Block block, Tier tier) {
		activeEnvoys.put(block, tier);
	}
	
	/**
	 *
	 * @param block The location you wish to remove.
	 */
	public void removeActiveEnvoy(Block block) {
		activeEnvoys.remove(block);
	}
	
	/**
	 *
	 * @param block The location you want to add.
	 */
	public void addLocation(Block block) {
		spawnLocations.add(block);
		saveSpawnLocations();
	}
	
	/**
	 *
	 * @param block The location you want to remove.
	 */
	public void removeLocation(Block block) {
		if(isLocation(block.getLocation())) {
			spawnLocations.remove(block);
			saveSpawnLocations();
		}
	}
	
	/**
	 *
	 * @return The next envoy time as a calendar.
	 */
	public Calendar getNextEnvoy() {
		return nextEnvoy;
	}
	
	/**
	 *
	 * @param cal A calendar that has the next time the envoy will happen.
	 */
	public void setNextEnvoy(Calendar cal) {
		nextEnvoy = cal;
	}
	
	/**
	 *
	 * @return The time till the next envoy.
	 */
	public String getNextEnvoyTime() {
		Calendar cal = getNextEnvoy();
		Calendar C = Calendar.getInstance();
		int total = ((int) (cal.getTimeInMillis() / 1000) - (int) (C.getTimeInMillis() / 1000));
		int day = 0;
		int hour = 0;
		int minute = 0;
		int second = 0;
		for(; total > 86400; total -= 86400, day++) ;
		for(; total > 3600; total -= 3600, hour++) ;
		for(; total >= 60; total -= 60, minute++) ;
		second += total;
		String message = "";
		if(day > 0) message += day + "d, ";
		if(day > 0 || hour > 0) message += hour + "h, ";
		if(day > 0 || hour > 0 || minute > 0) message += minute + "m, ";
		if(day > 0 || hour > 0 || minute > 0 || second > 0) message += second + "s, ";
		if(message.length() < 2) {
			message = Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.On-Going");
		}else {
			message = message.substring(0, message.length() - 2);
		}
		return message;
	}
	
	/**
	 *
	 * @return All falling blocks are are currently going.
	 */
	public List<Entity> getFallingBlocks() {
		return fallingBlocks;
	}
	
	/**
	 *
	 * @param en Remove a falling block from the list.
	 */
	public void removeFallingBlock(Entity en) {
		fallingBlocks.remove(en);
	}
	
	/**
	 * Call when you want to set the new warning.
	 */
	public void resetWarnings() {
		warnings.clear();
		envoySettings.getEnvoyWarnings().forEach(time -> addWarning(makeWarning(time)));
	}
	
	/**
	 *
	 * @param cal When adding a new warning.
	 */
	public void addWarning(Calendar cal) {
		warnings.add(cal);
	}
	
	/**
	 *
	 * @return All the current warnings.
	 */
	public List<Calendar> getWarnings() {
		return warnings;
	}
	
	/**
	 *
	 * @param time The new time for the warning.
	 * @return The new time as a calendar
	 */
	public Calendar makeWarning(String time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(getNextEnvoy().getTimeInMillis());
		for(String i : time.split(" ")) {
			if(i.contains("d")) {
				cal.add(Calendar.DATE, -Integer.parseInt(i.replaceAll("d", "")));
			}else if(i.contains("h")) {
				cal.add(Calendar.HOUR, -Integer.parseInt(i.replaceAll("h", "")));
			}else if(i.contains("m")) {
				cal.add(Calendar.MINUTE, -Integer.parseInt(i.replaceAll("m", "")));
			}else if(i.contains("s")) {
				cal.add(Calendar.SECOND, -Integer.parseInt(i.replaceAll("s", "")));
			}
		}
		return cal;
	}
	
	/**
	 *
	 * @return The time left in the current envoy event.
	 */
	public String getEnvoyRunTimeLeft() {
		Calendar cal = envoyTimeLeft;
		Calendar C = Calendar.getInstance();
		int total = ((int) (cal.getTimeInMillis() / 1000) - (int) (C.getTimeInMillis() / 1000));
		int day = 0;
		int hour = 0;
		int minute = 0;
		int second = 0;
		for(; total > 86400; total -= 86400, day++) ;
		for(; total > 3600; total -= 3600, hour++) ;
		for(; total >= 60; total -= 60, minute++) ;
		second += total;
		String message = "";
		if(day > 0) message += day + "d, ";
		if(day > 0 || hour > 0) message += hour + "h, ";
		if(day > 0 || hour > 0 || minute > 0) message += minute + "m, ";
		if(day > 0 || hour > 0 || minute > 0 || second > 0) message += second + "s, ";
		if(message.length() < 2) {
			message = Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.Not-Running");
		}else {
			message = message.substring(0, message.length() - 2);
		}
		return message;
	}
	
	/**
	 * Call when the run time needs canceled.
	 */
	public void cancelEnvoyRunTime() {
		try {
			runTimeTask.cancel();
		}catch(Exception e) {
		}
	}
	
	/**
	 * Call when the cool down time needs canceled.
	 */
	public void cancelEnvoyCooldownTime() {
		try {
			coolDownTask.cancel();
		}catch(Exception e) {
		}
	}
	
	public List<Block> generateSpawnLocations() {
		List<Block> dropLocations = new ArrayList<>();
		int maxSpawns = envoySettings.isMaxCrateEnabled() ? envoySettings.getMaxCrates() : envoySettings.isRandomLocationsEnabled() ? envoySettings.getMaxCrates() : spawnedLocations.size();
		Random random = new Random();
		if(envoySettings.isRandomLocationsEnabled()) {
			List<Block> minimumRadiusBlocks = getBlocks(center.clone(), envoySettings.getMinRadius());
			for(int stop = 0; dropLocations.size() < maxSpawns; stop++) {
				int maxRadius = envoySettings.getMaxRadius();
				Location location = center.clone();
				location.add(-(maxRadius / 2) + random.nextInt(maxRadius), 0, -(maxRadius / 2) + random.nextInt(maxRadius));
				location = location.getWorld().getHighestBlockAt(location).getLocation();
				if(!location.getChunk().isLoaded()) {
					if(!location.getChunk().load()) {
						continue;
					}
				}
				if(location.getBlockY() <= 0 ||
				minimumRadiusBlocks.contains(location.getBlock()) || minimumRadiusBlocks.contains(location.clone().add(0, 1, 0).getBlock()) ||
				dropLocations.contains(location.getBlock()) || dropLocations.contains(location.clone().add(0, 1, 0).getBlock()) ||
				blacklistedBlocks.contains(location.getBlock().getType())) {
					continue;
				}
				dropLocations.add(location.clone().add(0, 1, 0).getBlock());
			}
		}else {
			if(envoySettings.isMaxCrateEnabled()) {
				for(int i = 0; i < maxSpawns; ) {
					Block block = spawnLocations.get(random.nextInt(spawnLocations.size()));
					if(!dropLocations.contains(block)) {
						dropLocations.add(block);
						i++;
					}
				}
			}else {
				dropLocations.addAll(spawnLocations);
			}
		}
		return dropLocations;
	}
	
	/**
	 * Starts the envoy event.
	 * @return true if the event started successfully and false if it had an issue.
	 */
	public boolean startEnvoyEvent() {
		List<Block> dropLocations = generateSpawnLocations();
		if(dropLocations.isEmpty() || (envoySettings.isRandomLocationsEnabled() && center.getWorld() == null)) {
			setNextEnvoy(getEnvoyCooldown());
			resetWarnings();
			EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.NO_LOCATIONS_FOUND);
			Bukkit.getPluginManager().callEvent(event);
			Messages.NO_SPAWN_LOCATIONS_FOUND.broadcastMessage(false, null);
			return false;
		}
		for(Player player : EditControl.getEditors()) {
			EditControl.removeFakeBlocks(player);
			player.getInventory().removeItem(new ItemStack(Material.BEDROCK, 1));
			Messages.KICKED_FROM_EDITOR_MODE.sendMessage(player);
		}
		EditControl.getEditors().clear();
		if(tiers.size() == 0) {
			Bukkit.broadcastMessage(Methods.getPrefix() + Methods.color("&cNo tiers were found. Please delete the Tiers folder" + " to allow it to remake the default tier files."));
			return false;
		}
		deSpawnCrates();
		setEnvoyActive(true);
		int max = dropLocations.size();
		HashMap<String, String> placeholder = new HashMap<>();
		placeholder.put("%amount%", max + "");
		placeholder.put("%Amount%", max + "");
		Messages.STARTED.broadcastMessage(false, placeholder);
		for(Block block : dropLocations) {
			if(block != null) {
				if(block.getWorld() != null) {
					boolean spawnFallingBlock = false;
					for(Entity entity : Methods.getNearbyEntities(block.getLocation(), 40, 40, 40)) {
						if(entity instanceof Player) {
							spawnFallingBlock = true;
							break;
						}
					}
					if(!envoySettings.isFallingBlocksEnabled()) {
						spawnFallingBlock = false;
					}
					if(spawnFallingBlock) {
						if(!block.getChunk().isLoaded()) {
							block.getChunk().load();
						}
						FallingBlock chest = block.getWorld().spawnFallingBlock(block.getLocation().add(.5, envoySettings.getFallingHeight(), .5), envoySettings.getFallingBlockMaterial(), (byte) envoySettings.getFallingBlockDurability());
						fallingBlocks.add(chest);
					}else {
						Tier tier = pickRandomTier();
						if(!block.getChunk().isLoaded()) {
							block.getChunk().load();
						}
						block.setType(tier.getPlacedBlockMaterial());
						if(tier.isHoloEnabled()) {
							if(Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
								HolographicSupport.createHologram(block.getLocation(), tier);
							}else if(Support.HOLOGRAMS.isPluginLoaded()) {
								HologramsSupport.createHologram(block.getLocation(), tier);
							}else if(Support.CMI.isPluginLoaded()) {
								CMISupport.createHologram(block.getLocation(), tier);
							}
						}
						addActiveEnvoy(block, tier);
						addSpawnedLocation(block);
						if(tier.getSignalFlareToggle()) {
							startSignalFlare(block.getLocation(), tier);
						}
					}
				}
			}
		}
		runTimeTask = new BukkitRunnable() {
			@Override
			public void run() {
				EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.OUT_OF_TIME);
				Bukkit.getPluginManager().callEvent(event);
				Messages.ENDED.broadcastMessage(false, null);
				endEnvoyEvent();
			}
		}.runTaskLater(plugin, getTimeSeconds(envoySettings.getEnvoyRunTimer()) * 20);
		envoyTimeLeft = getEnvoyRunTimeCalendar();
		return true;
	}
	
	/**
	 * Ends the envoy event.
	 */
	public void endEnvoyEvent() {
		deSpawnCrates();
		setEnvoyActive(false);
		cancelEnvoyRunTime();
		if(envoySettings.isEnvoyRunTimerEnabled()) {
			setNextEnvoy(getEnvoyCooldown());
			resetWarnings();
		}
		EnvoyControl.clearCooldowns();
	}
	
	/**
	 * Get a list of all the tiers.
	 * @return List of all the tiers.
	 */
	public List<Tier> getTiers() {
		return tiers;
	}
	
	/**
	 * Get a tier from its name.
	 * @param tierName The name of the tier.
	 * @return Returns a tier or will return null if not tier is found.
	 */
	public Tier getTier(String tierName) {
		for(Tier tier : tiers) {
			if(tier.getName().equalsIgnoreCase(tierName)) {
				return tier;
			}
		}
		return null;
	}
	
	/**
	 *
	 * @param loc The location the signals will be at.
	 * @param tier The tier the signal is.
	 */
	public void startSignalFlare(final Location loc, final Tier tier) {
		BukkitTask task = new BukkitRunnable() {
			@Override
			public void run() {
				playSignal(loc.clone().add(.5, 0, .5), tier);
			}
		}.runTaskTimer(plugin, getTimeSeconds(tier.getSignalFlareTimer()) * 20, getTimeSeconds(tier.getSignalFlareTimer()) * 20);
		activeSignals.put(loc, task);
	}
	
	/**
	 *
	 * @param loc The location that the signal is stopping.
	 */
	public void stopSignalFlare(Location loc) {
		try {
			activeSignals.get(loc).cancel();
		}catch(Exception e) {
		}
		activeSignals.remove(loc);
	}
	
	/**
	 *
	 * @return The center location for the random crates.
	 */
	public Location getCenter() {
		return center;
	}
	
	/**
	 * Sets the center location for the random crates.
	 * @param loc The new center location.
	 */
	public void setCenter(Location loc) {
		center = loc;
		centerString = getStringFromLocation(center);
		Files.DATA.getFile().set("Center", getStringFromLocation(center));
		Files.DATA.saveFile();
	}
	
	/**
	 * Check if a player is ignoring the messages.
	 * @param uuid The player's UUID.
	 * @return True if they are ignoring them and false if not.
	 */
	public boolean isIgnoringMessages(UUID uuid) {
		return ignoreMessages.contains(uuid);
	}
	
	/**
	 * Make a player ignore the messages.
	 * @param uuid The player's UUID.
	 */
	public void addIgnorePlayer(UUID uuid) {
		ignoreMessages.add(uuid);
	}
	
	/**
	 * Make a player stop ignoring the messages.
	 * @param uuid The player's UUID.
	 */
	public void removeIgnorePlayer(UUID uuid) {
		ignoreMessages.remove(uuid);
	}
	
	/**
	 * Get the Plugin of this plugin.
	 * @return The Plugin object.
	 */
	public Plugin getPlugin() {
		return plugin;
	}
	
	public void cleanLocations() {
		ArrayList<Material> crateTypes = new ArrayList<>();
		for(Tier tier : getTiers()) {
			crateTypes.add(tier.getPlacedBlockMaterial());
		}
		List<Block> notFound = new ArrayList<>();
		for(Block spawnedLocation : spawnedLocations) {
			if(spawnedLocation != null) {
				if(crateTypes.contains(spawnedLocation.getType())) {
					spawnedLocation.setType(Material.AIR);
					//				System.out.println("[CrazyEnvoy]: Removed the old crate at location " + getStringFromLocation(spawnedLocation));
				}else {
					notFound.add(spawnedLocation);
				}
				stopSignalFlare(spawnedLocation.getLocation());
				if(Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
					HolographicSupport.removeAllHolograms();
				}else if(Support.HOLOGRAMS.isPluginLoaded()) {
					HologramsSupport.removeAllHolograms();
				}else if(Support.CMI.isPluginLoaded()) {
					CMISupport.removeAllHolograms();
				}
			}
			
		}
		spawnedLocations.clear();
		if(Calendar.getInstance().after(nextClean)) {
			nextClean.add(Calendar.DATE, 1);
			Files.DATA.getFile().set("Locations.Spawned", spawnedLocations);
			Files.DATA.getFile().set("Next-Clean", nextClean.getTimeInMillis());
			Files.DATA.saveFile();
		}else {
			Files.DATA.getFile().set("Locations.Spawned", getStringsFromLocationList(notFound));
			Files.DATA.saveFile();
		}
	}
	
	/**
	 * Add a location to the cleaning list of where crates actually spawned.
	 * @param block block the crate spawned at.
	 */
	public void addSpawnedLocation(Block block) {
		if(!getStringsFromLocationList(spawnedLocations).contains(getStringFromLocation(block.getLocation()))) {
			spawnedLocations.add(block);
			Files.DATA.getFile().set("Locations.Spawned", getStringsFromLocationList(spawnedLocations));
			Files.DATA.saveFile();
		}
	}
	
	public List<Block> getSpawnedLocations() {
		return spawnedLocations;
	}
	
	private void setEnvoyActive(boolean toggle) {
		envoyActive = toggle;
	}
	
	private Calendar getEnvoyCooldown() {
		Calendar cal = Calendar.getInstance();
		if(envoySettings.isEnvoyCooldownEnabled()) {
			String time = envoySettings.getEnvoyCooldown();
			for(String i : time.split(" ")) {
				if(i.contains("d")) {
					cal.add(Calendar.DATE, Integer.parseInt(i.replaceAll("d", "")));
				}else if(i.contains("h")) {
					cal.add(Calendar.HOUR, Integer.parseInt(i.replaceAll("h", "")));
				}else if(i.contains("m")) {
					cal.add(Calendar.MINUTE, Integer.parseInt(i.replaceAll("m", "")));
				}else if(i.contains("s")) {
					cal.add(Calendar.SECOND, Integer.parseInt(i.replaceAll("s", "")));
				}
			}
		}else {
			String time = envoySettings.getEnvoyClockTime();
			int hour = Integer.parseInt(time.split(" ")[0].split(":")[0]);
			int min = Integer.parseInt(time.split(" ")[0].split(":")[1]);
			int c = Calendar.AM;
			if(time.split(" ")[1].equalsIgnoreCase("AM")) {
				c = Calendar.AM;
			}else if(time.split(" ")[1].equalsIgnoreCase("PM")) {
				c = Calendar.PM;
			}
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.getTime(); // Without this makes the hours not change for some reason.
			cal.set(Calendar.MINUTE, min);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.AM_PM, c);
			if(cal.before(Calendar.getInstance())) {
				cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
			}
		}
		return cal;
	}
	
	private Calendar getEnvoyRunTimeCalendar() {
		Calendar cal = Calendar.getInstance();
		String time = envoySettings.getEnvoyRunTimer().toLowerCase();
		for(String i : time.split(" ")) {
			if(i.contains("d")) {
				cal.add(Calendar.DATE, Integer.parseInt(i.replaceAll("d", "")));
			}else if(i.contains("h")) {
				cal.add(Calendar.HOUR, Integer.parseInt(i.replaceAll("h", "")));
			}else if(i.contains("m")) {
				cal.add(Calendar.MINUTE, Integer.parseInt(i.replaceAll("m", "")));
			}else if(i.contains("s")) {
				cal.add(Calendar.SECOND, Integer.parseInt(i.replaceAll("s", "")));
			}
		}
		return cal;
	}
	
	private String getStringFromLocation(Location location) {
		return "World:" + location.getWorld().getName()
		+ ", X:" + location.getBlockX()
		+ ", Y:" + location.getBlockY()
		+ ", Z:" + location.getBlockZ();
	}
	
	private List<String> getStringsFromLocationList(List<Block> stringList) {
		ArrayList<String> strings = new ArrayList<>();
		for(Block block : stringList) {
			strings.add(getStringFromLocation(block.getLocation()));
		}
		return strings;
	}
	
	private Location getLocationFromString(String locationString) {
		World w = Bukkit.getWorlds().get(0);
		int x = 0;
		int y = 0;
		int z = 0;
		for(String i : locationString.toLowerCase().split(", ")) {
			if(i.startsWith("world:")) {
				w = Bukkit.getWorld(i.replaceAll("world:", ""));
			}else if(i.startsWith("x:")) {
				x = Integer.parseInt(i.replaceAll("x:", ""));
			}else if(i.startsWith("y:")) {
				y = Integer.parseInt(i.replaceAll("y:", ""));
			}else if(i.startsWith("z:")) {
				z = Integer.parseInt(i.replaceAll("z:", ""));
			}
		}
		return new Location(w, x, y, z);
	}
	
	private List<Block> getLocationsFromStringList(List<String> locationsList) {
		ArrayList<Block> locations = new ArrayList<>();
		for(String location : locationsList) {
			locations.add(getLocationFromString(location).getBlock());
		}
		return locations;
	}
	
	private void playSignal(Location loc, Tier tier) {
		List<Color> colors = tier.getFireworkColors();
		Firework firework = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fireworkMeta = firework.getFireworkMeta();
		fireworkMeta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(colors).trail(true).flicker(false).build());
		fireworkMeta.setPower(1);
		firework.setFireworkMeta(fireworkMeta);
		FireworkDamageAPI.addFirework(firework);
	}
	
	private List<Block> getBlocks(Location location, int radius) {
		Location locations2 = location.clone();
		location.add(-radius, 0, -radius);
		locations2.add(radius, 0, radius);
		List<Block> locations = new ArrayList<>();
		int topBlockX = (Math.max(location.getBlockX(), locations2.getBlockX()));
		int bottomBlockX = (Math.min(location.getBlockX(), locations2.getBlockX()));
		int topBlockZ = (Math.max(location.getBlockZ(), locations2.getBlockZ()));
		int bottomBlockZ = (Math.min(location.getBlockZ(), locations2.getBlockZ()));
		if(location.getWorld() != null) {
			for(int x = bottomBlockX; x <= topBlockX; x++) {
				for(int z = bottomBlockZ; z <= topBlockZ; z++) {
					locations.add(location.getWorld().getHighestBlockAt(x, z));
				}
			}
		}
		return locations;
	}
	
	private int getTimeSeconds(String time) {
		int seconds = 0;
		for(String i : time.split(" ")) {
			if(i.contains("d")) {
				seconds += Integer.parseInt(i.replaceAll("d", "")) * 86400;
			}else if(i.contains("h")) {
				seconds += Integer.parseInt(i.replaceAll("h", "")) * 3600;
			}else if(i.contains("m")) {
				seconds += Integer.parseInt(i.replaceAll("m", "")) * 60;
			}else if(i.contains("s")) {
				seconds += Integer.parseInt(i.replaceAll("s", ""));
			}
		}
		return seconds;
	}
	
	private Tier pickRandomTier() {
		if(getTiers().size() == 1) {
			return getTiers().get(0);
		}
		ArrayList<Tier> tiers = new ArrayList<>();
		for(; tiers.size() == 0; ) {
			for(Tier tier : this.tiers) {
				if(Methods.isSuccessful(tier.getSpawnChance(), 100)) {
					tiers.add(tier);
				}
			}
		}
		return tiers.get(new Random().nextInt(tiers.size()));
	}
	
}