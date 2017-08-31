package me.badbones69.crazyenvoy.api;

import me.badbones69.crazyenvoy.Methods;
import me.badbones69.crazyenvoy.api.FileManager.CustomFile;
import me.badbones69.crazyenvoy.api.FileManager.Files;
import me.badbones69.crazyenvoy.api.enums.Messages;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent.EnvoyEndReason;
import me.badbones69.crazyenvoy.api.events.EnvoyStartEvent;
import me.badbones69.crazyenvoy.api.events.EnvoyStartEvent.EnvoyStartReason;
import me.badbones69.crazyenvoy.api.objects.ItemBuilder;
import me.badbones69.crazyenvoy.api.objects.Prize;
import me.badbones69.crazyenvoy.api.objects.Tier;
import me.badbones69.crazyenvoy.controllers.EditControl;
import me.badbones69.crazyenvoy.controllers.EnvoyControl;
import me.badbones69.crazyenvoy.controllers.FireworkDamageAPI;
import me.badbones69.crazyenvoy.multisupport.HolographicSupport;
import me.badbones69.crazyenvoy.multisupport.Support;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
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
	private BukkitTask runTimeTask;
	private BukkitTask coolDownTask;
	private Calendar nextEnvoy;
	private Calendar envoyTimeLeft;
	private Boolean envoyActive = false;
	private Boolean autoTimer = true;
	private ArrayList<UUID> ignoreMessages = new ArrayList<>();
	private ArrayList<Calendar> warnings = new ArrayList<>();
	private ArrayList<Location> locations = new ArrayList<>();
	private ArrayList<Entity> fallingBlocks = new ArrayList<>();
	private Location center;
	private HashMap<Location, Tier> activeEnvoys = new HashMap<>();
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
		locations.clear();
		plugin = Bukkit.getPluginManager().getPlugin("CrazyEnvoy");
		FileConfiguration data = Files.DATA.getFile();
		FileConfiguration config = Files.CONFIG.getFile();
		envoyTimeLeft = Calendar.getInstance();
		for(String l : data.getStringList("Locations.Spawns")) {
			locations.add(getLocationFromString(l));
		}
		if(Calendar.getInstance().after(getNextEnvoy())) {
			setEnvoyActive(false);
		}
		if(Files.DATA.getFile().contains("Center")) {
			center = getLocationFromString(Files.DATA.getFile().getString("Center"));
		}else {
			center = Bukkit.getWorlds().get(0).getSpawnLocation();
		}
		if(config.getBoolean("Settings.Envoy-Timer-Toggle")) {
			Calendar cal = Calendar.getInstance();
			if(config.getBoolean("Settings.Envoy-Cooldown-Toggle")) {
				autoTimer = true;
				cal.setTimeInMillis(data.getLong("Next-Envoy"));
				if(Calendar.getInstance().after(cal)) {
					cal.setTimeInMillis(getEnvoyCooldown().getTimeInMillis());
				}
			}else {
				autoTimer = false;
				String time = config.getString("Settings.Envoy-Time");
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
			if(file.contains("Settings.Hologram-Hight")) {
				tier.setHoloHight(file.getDouble("Settings.Hologram-Hight"));
			}else {
				tier.setHoloHight(file.getDouble("Settings.Hologram-Height"));
			}
			tier.setHoloMessage(file.getStringList("Settings.Hologram"));
			ItemBuilder placedBlock = new ItemBuilder().setMaterial(file.getString("Settings.Placed-Block"));
			tier.setPlacedBlockMaterial(placedBlock.getMaterial());
			tier.setPlacedBlockMetaData(placedBlock.getMetaData());
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
				Integer chance = file.getInt("Prizes." + prizeID + ".Chance");
				List<String> commands = file.getStringList("Prizes." + prizeID + ".Commands");
				List<String> messages = file.getStringList("Prizes." + prizeID + ".Messages");
				ArrayList<ItemStack> items = new ArrayList<>();
				for(String line : file.getStringList("Prizes." + prizeID + ".Items")) {
					ArrayList<String> lore = new ArrayList<>();
					HashMap<Enchantment, Integer> enchs = new HashMap<>();
					String name = "";
					int amount = 1;
					String item = "Stone";
					Boolean glowing = false;
					Boolean unbreaking = false;
					for(String i : line.split(", ")) {
						if(i.startsWith("Item:")) {
							i = i.replaceAll("Item:", "");
							item = i;
						}else if(i.startsWith("Amount:")) {
							i = i.replaceAll("Amount:", "");
							amount = Integer.parseInt(i);
						}else if(i.startsWith("Name:")) {
							i = i.replaceAll("Name:", "");
							name = Methods.color(i);
						}else if(i.startsWith("Lore:")) {
							i = i.replaceAll("Lore:", "");
							for(String L : i.split(",")) {
								L = Methods.color(L);
								lore.add(L);
							}
						}else if(i.startsWith("Glowing:")) {
							i = i.replaceAll("Glowing:", "");
							glowing = Boolean.parseBoolean(i);
						}else if(i.startsWith("Unbreakable-Item:")) {
							if(i.replaceAll("Unbreakable-Item:", "").equalsIgnoreCase("true")) {
								unbreaking = true;
							}
						}else {
							for(Enchantment enc : Enchantment.values()) {
								if(i.startsWith(enc.getName() + ":") || i.startsWith(Methods.getEnchantmentName(enc) + ":")) {
									String[] breakdown = i.split(":");
									int lvl = Integer.parseInt(breakdown[1]);
									enchs.put(enc, lvl);
								}
							}
						}
					}
					items.add(new ItemBuilder().setMaterial(item).setName(name).setAmount(amount).setLore(lore).setEnchantments(enchs).setGlowing(glowing).setUnbreakable(unbreaking).build());
				}
				tier.addPrize(new Prize(prizeID).setChance(chance).setItems(items).setCommands(commands).setMessages(messages));
			}
			tiers.add(tier);
		}
	}
	
	/**
	 * Run this when you need to save the locations.
	 */
	public void unload() {
		ArrayList<String> locs = new ArrayList<>();
		for(Location loc : locations) {
			try {
				locs.add("World:" + loc.getWorld().getName() + ", X:" + loc.getBlockX() + ", Y:" + loc.getBlockY() + ", Z:" + loc.getBlockZ());
			}catch(Exception e) {
				System.out.print("[CrazyEnvoy] Error when saving the crate location.");
			}
		}
		deSpawnCrates();
		Files.DATA.getFile().set("Next-Envoy", getNextEnvoy().getTimeInMillis());
		try {
			Files.DATA.getFile().set("Center", "World:" + center.getWorld().getName() + ", X:" + center.getBlockX() + ", Y:" + center.getBlockY() + ", Z:" + center.getBlockZ());
		}catch(Exception e) {
			System.out.print("[CrazyEnvoy] Error when saving the center location.");
			
		}
		Files.DATA.getFile().set("Locations.Spawns", locs);
		Files.DATA.saveFile();
		locations.clear();
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
							if(Files.CONFIG.getFile().contains("Settings.Minimum-Players-Toggle") && Files.CONFIG.getFile().contains("Settings.Minimum-Players")) {
								if(Files.CONFIG.getFile().getBoolean("Settings.Minimum-Players-Toggle")) {
									int online = Bukkit.getServer().getOnlinePlayers().size();
									if(online < Files.CONFIG.getFile().getInt("Settings.Minimum-Players")) {
										HashMap<String, String> placeholder = new HashMap<>();
										placeholder.put("%amount%", online + "");
										placeholder.put("%Amount%", online + "");
										Messages.NOT_ENOUGH_PLAYERS.broadcastMessage(false, placeholder);
										setNextEnvoy(getEnvoyCooldown());
										resetWarnings();
										return;
									}
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
	 * @param loc The location you want the tier from.
	 * @return The tier that location is.
	 */
	public Tier getTier(Location loc) {
		return activeEnvoys.get(loc);
	}
	
	/**
	 *
	 * @return True if the envoy event is currently happening and false if not.
	 */
	public Boolean isEnvoyActive() {
		return envoyActive;
	}
	
	/**
	 * Despawns all of the active crates.
	 */
	public void deSpawnCrates() {
		envoyActive = false;
		for(Location loc : getActiveEnvoys()) {
			loc.getBlock().setType(Material.AIR);
			stopSignalFlare(loc);
		}
		for(Entity en : fallingBlocks) {
			en.remove();
		}
		if(Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
			HolographicSupport.removeAllHolograms();
		}
		fallingBlocks.clear();
		activeEnvoys.clear();
	}
	
	/**
	 *
	 * @return All the location the chests will spawn.
	 */
	public ArrayList<Location> getLocations() {
		return locations;
	}
	
	/**
	 *
	 * @param Loc The location that you want to check.
	 */
	public Boolean isLocation(Location Loc) {
		for(Location l : locations) {
			if(l.equals(Loc)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 *
	 * @return All the active envoys that are active.
	 */
	public Set<Location> getActiveEnvoys() {
		return activeEnvoys.keySet();
	}
	
	/**
	 *
	 * @param loc The location your are checking.
	 * @return Turn if it is and false if not.
	 */
	public Boolean isActiveEnvoy(Location loc) {
		return activeEnvoys.containsKey(loc);
	}
	
	/**
	 *
	 * @param loc The location you wish to add.
	 */
	public void addActiveEnvoy(Location loc, Tier tier) {
		activeEnvoys.put(loc, tier);
	}
	
	/**
	 *
	 * @param loc The location you wish to remove.
	 */
	public void removeActiveEnvoy(Location loc) {
		activeEnvoys.remove(loc);
	}
	
	/**
	 *
	 * @param loc The location you want to add.
	 */
	public void addLocation(Location loc) {
		locations.add(loc);
	}
	
	/**
	 *
	 * @param loc The location you want to remove.
	 */
	public void removeLocation(Location loc) {
		if(isLocation(loc)) {
			locations.remove(loc);
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
	 * @return The time till the next envoy.
	 */
	public String getNextEnvoyTime() {
		Calendar cal = getNextEnvoy();
		Calendar C = Calendar.getInstance();
		int total = ((int) (cal.getTimeInMillis() / 1000) - (int) (C.getTimeInMillis() / 1000));
		int D = 0;
		int H = 0;
		int M = 0;
		int S = 0;
		for(; total > 86400; total -= 86400, D++) ;
		for(; total > 3600; total -= 3600, H++) ;
		for(; total >= 60; total -= 60, M++) ;
		S += total;
		String msg = "";
		if(D > 0) msg += D + "d, ";
		if(D > 0 || H > 0) msg += H + "h, ";
		if(D > 0 || H > 0 || M > 0) msg += M + "m, ";
		if(D > 0 || H > 0 || M > 0 || S > 0) msg += S + "s, ";
		if(msg.length() < 2) {
			msg = Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.On-Going");
		}else {
			msg = msg.substring(0, msg.length() - 2);
		}
		return msg;
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
	 * @return All falling blocks are are currently going.
	 */
	public ArrayList<Entity> getFallingBlocks() {
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
		for(String time : Files.CONFIG.getFile().getStringList("Settings.Envoy-Warnings")) {
			addWarning(makeWarning(time));
		}
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
	public ArrayList<Calendar> getWarnings() {
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
			if(i.contains("D") || i.contains("d")) {
				cal.add(Calendar.DATE, -Integer.parseInt(i.replaceAll("D", "").replaceAll("d", "")));
			}
			if(i.contains("H") || i.contains("h")) {
				cal.add(Calendar.HOUR, -Integer.parseInt(i.replaceAll("H", "").replaceAll("h", "")));
			}
			if(i.contains("M") || i.contains("m")) {
				cal.add(Calendar.MINUTE, -Integer.parseInt(i.replaceAll("M", "").replaceAll("m", "")));
			}
			if(i.contains("S") || i.contains("s")) {
				cal.add(Calendar.SECOND, -Integer.parseInt(i.replaceAll("S", "").replaceAll("s", "")));
			}
		}
		return cal;
	}
	
	/**
	 *
	 * @return The time left in the current envoy evnet.
	 */
	public String getEnvoyRunTimeLeft() {
		Calendar cal = envoyTimeLeft;
		Calendar C = Calendar.getInstance();
		int total = ((int) (cal.getTimeInMillis() / 1000) - (int) (C.getTimeInMillis() / 1000));
		int D = 0;
		int H = 0;
		int M = 0;
		int S = 0;
		for(; total > 86400; total -= 86400, D++) ;
		for(; total > 3600; total -= 3600, H++) ;
		for(; total >= 60; total -= 60, M++) ;
		S += total;
		String msg = "";
		if(D > 0) msg += D + "d, ";
		if(D > 0 || H > 0) msg += H + "h, ";
		if(D > 0 || H > 0 || M > 0) msg += M + "m, ";
		if(D > 0 || H > 0 || M > 0 || S > 0) msg += S + "s, ";
		if(msg.length() < 2) {
			msg = Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.Not-Running");
		}else {
			msg = msg.substring(0, msg.length() - 2);
		}
		return msg;
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
	
	/**
	 * Starts the envoy event.
	 */
	@SuppressWarnings("deprecation")
	public void startEnvoyEvent() {
		for(Player player : EditControl.getEditors()) {
			EditControl.removeFakeBlocks(player);
			player.getInventory().removeItem(new ItemStack(Material.BEDROCK, 1));
			Messages.KICKED_FROM_EDITOR_MODE.sendMessage(player);
		}
		EditControl.getEditors().clear();
		if(tiers.size() == 0) {
			Bukkit.broadcastMessage(Methods.getPrefix() + Methods.color("&cNo tiers were found. Please delete the Tiers folder" + " to allow it to remake the default tier files."));
			return;
		}
		deSpawnCrates();
		setEnvoyActive(true);
		int max = getLocations().size();
		if(Files.CONFIG.getFile().getBoolean("Settings.Max-Crate-Toggle")) {
			max = Files.CONFIG.getFile().getInt("Settings.Max-Crates");
			if(max > getLocations().size()) {
				max = getLocations().size();
			}
		}
		ArrayList<Location> dropLocations = new ArrayList<>();
		if(Files.CONFIG.getFile().getBoolean("Settings.Max-Crate-Toggle")) {
			for(int i = 0; i < max; ) {
				Location loc = locations.get(new Random().nextInt(locations.size()));
				if(!dropLocations.contains(loc)) {
					dropLocations.add(loc);
					i++;
				}
			}
		}else {
			dropLocations.addAll(locations);
		}
		if(Files.CONFIG.getFile().getBoolean("Settings.Random-Locations")) {
			dropLocations.clear();
			max = Files.CONFIG.getFile().getInt("Settings.Max-Crates");
			ArrayList<Location> min = getBlocks(center.clone(), Files.CONFIG.getFile().getInt("Settings.Min-Radius"));
			int stop = 0;
			for(int i = 0; i < max && stop < 2000; stop++) {
				int m = Files.CONFIG.getFile().getInt("Settings.Max-Radius");
				Location loc = center.clone();
				loc.add(-(m / 2) + new Random().nextInt(m), 0, -(m / 2) + new Random().nextInt(m));
				loc.setY(255);
				if(!loc.getChunk().isLoaded()) {
					loc.getChunk().load();
				}
				for(; loc.getBlock().getType() == Material.AIR && loc.getBlockY() >= 0; ) {
					if(loc.getBlockY() <= 0) {
						break;
					}
					loc.add(0, -1, 0);
				}
				if(loc.getBlockY() <= 0) {
					continue;
				}
				Location check = loc.clone();
				check.setY(255);
				if(min.contains(check)) {
					continue;
				}
				if(dropLocations.contains(loc.clone().add(0, 1, 0))) {
					continue;
				}
				dropLocations.add(loc.add(0, 1, 0));
				i++;
			}
		}
		HashMap<String, String> placeholder = new HashMap<>();
		placeholder.put("%amount%", max + "");
		placeholder.put("%Amount%", max + "");
		Messages.STARTED.broadcastMessage(false, placeholder);
		for(Location loc : dropLocations) {
			boolean spawnFallingBlock = false;
			for(Entity en : Methods.getNearbyEntities(loc, 40, 40, 40)) {
				if(en instanceof Player) {
					spawnFallingBlock = true;
				}
			}
			if(Files.CONFIG.getFile().contains("Settings.Falling-Block-Toggle")) {
				if(!Files.CONFIG.getFile().getBoolean("Settings.Falling-Block-Toggle")) {
					spawnFallingBlock = false;
				}
			}
			if(spawnFallingBlock) {
				String type = Files.CONFIG.getFile().getString("Settings.Falling-Block");
				int ty = 0;
				if(type.contains(":")) {
					String[] b = type.split(":");
					type = b[0];
					ty = Integer.parseInt(b[1]);
				}
				Material m = Material.matchMaterial(type);
				int height = Files.CONFIG.getFile().getInt("Settings.Fall-Height");
				if(!loc.getChunk().isLoaded()) {
					loc.getChunk().load();
				}
				FallingBlock chest = loc.getWorld().spawnFallingBlock(loc.clone().add(.5, height, .5), m, (byte) ty);
				fallingBlocks.add(chest);
			}else {
				Tier tier = pickRandomTier();
				if(!loc.getChunk().isLoaded()) {
					loc.getChunk().load();
				}
				loc.getBlock().setType(tier.getPlacedBlockMaterial());
				if(Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
					HolographicSupport.createHologram(loc.clone(), tier);
				}
				addActiveEnvoy(loc.clone(), tier);
				if(tier.getSignalFlareToggle()) {
					startSignalFlare(loc.clone(), tier);
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
		}.runTaskLater(plugin, getTimeSeconds(Files.CONFIG.getFile().getString("Settings.Envoy-Run-Time")) * 20);
		envoyTimeLeft = getEnvoyRunTimeCalendar();
	}
	
	/**
	 * Ends the envoy event.
	 */
	public void endEnvoyEvent() {
		deSpawnCrates();
		setEnvoyActive(false);
		cancelEnvoyRunTime();
		if(Files.CONFIG.getFile().getBoolean("Settings.Envoy-Timer-Toggle")) {
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
	
	private void setEnvoyActive(Boolean toggle) {
		envoyActive = toggle;
	}
	
	private Calendar getEnvoyCooldown() {
		Calendar cal = Calendar.getInstance();
		FileConfiguration config = Files.CONFIG.getFile();
		if(config.getBoolean("Settings.Envoy-Cooldown-Toggle")) {
			String time = config.getString("Settings.Envoy-Cooldown");
			for(String i : time.split(" ")) {
				if(i.contains("D") || i.contains("d")) {
					cal.add(Calendar.DATE, Integer.parseInt(i.replaceAll("D", "").replaceAll("d", "")));
				}
				if(i.contains("H") || i.contains("h")) {
					cal.add(Calendar.HOUR, Integer.parseInt(i.replaceAll("H", "").replaceAll("h", "")));
				}
				if(i.contains("M") || i.contains("m")) {
					cal.add(Calendar.MINUTE, Integer.parseInt(i.replaceAll("M", "").replaceAll("m", "")));
				}
				if(i.contains("S") || i.contains("s")) {
					cal.add(Calendar.SECOND, Integer.parseInt(i.replaceAll("S", "").replaceAll("s", "")));
				}
			}
		}else {
			String time = config.getString("Settings.Envoy-Time");
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
		String time = Files.CONFIG.getFile().getString("Settings.Envoy-Run-Time").toLowerCase();
		for(String i : time.split(" ")) {
			if(i.contains("d")) {
				cal.add(Calendar.DATE, Integer.parseInt(i.replaceAll("d", "")));
			}
			if(i.contains("h")) {
				cal.add(Calendar.HOUR, Integer.parseInt(i.replaceAll("h", "")));
			}
			if(i.contains("m")) {
				cal.add(Calendar.MINUTE, Integer.parseInt(i.replaceAll("m", "")));
			}
			if(i.contains("s")) {
				cal.add(Calendar.SECOND, Integer.parseInt(i.replaceAll("s", "")));
			}
		}
		return cal;
	}
	
	private Location getLocationFromString(String locationString) {
		World w = Bukkit.getWorlds().get(0);
		int x = 0;
		int y = 0;
		int z = 0;
		for(String i : locationString.toLowerCase().split(", ")) {
			if(i.startsWith("world:")) {
				w = Bukkit.getWorld(i.replaceAll("world:", ""));
			}
			if(i.startsWith("x:")) {
				x = Integer.parseInt(i.replaceAll("x:", ""));
			}
			if(i.startsWith("y:")) {
				y = Integer.parseInt(i.replaceAll("y:", ""));
			}
			if(i.startsWith("z:")) {
				z = Integer.parseInt(i.replaceAll("z:", ""));
			}
		}
		return new Location(w, x, y, z);
	}
	
	private void playSignal(Location loc, Tier tier) {
		List<Color> colors = tier.getFireworkColors();
		Firework fw = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(colors).trail(true).flicker(false).build());
		fm.setPower(1);
		fw.setFireworkMeta(fm);
		FireworkDamageAPI.addFirework(fw);
	}
	
	private ArrayList<Location> getBlocks(Location loc, int radius) {
		Location loc2 = loc.clone();
		loc.add(-radius, 0, -radius);
		loc2.add(radius, 0, radius);
		ArrayList<Location> locs = new ArrayList<>();
		int topBlockX = (loc.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc.getBlockX());
		int bottomBlockX = (loc.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc.getBlockX());
		int topBlockZ = (loc.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc.getBlockZ());
		int bottomBlockZ = (loc.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc.getBlockZ());
		for(int x = bottomBlockX; x <= topBlockX; x++) {
			for(int z = bottomBlockZ; z <= topBlockZ; z++) {
				if(loc.getWorld().getBlockAt(x, 255, z) != null) {
					locs.add(loc.getWorld().getBlockAt(x, 255, z).getLocation());
				}
			}
		}
		return locs;
	}
	
	private Integer getTimeSeconds(String time) {
		Integer seconds = 0;
		for(String i : time.split(" ")) {
			if(i.contains("D") || i.contains("d")) {
				seconds += Integer.parseInt(i.replaceAll("D", "").replaceAll("d", "")) * 86400;
			}
			if(i.contains("H") || i.contains("h")) {
				seconds += Integer.parseInt(i.replaceAll("H", "").replaceAll("h", "")) * 3600;
			}
			if(i.contains("M") || i.contains("m")) {
				seconds += Integer.parseInt(i.replaceAll("M", "").replaceAll("m", "")) * 60;
			}
			if(i.contains("S") || i.contains("s")) {
				seconds += Integer.parseInt(i.replaceAll("S", "").replaceAll("s", ""));
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