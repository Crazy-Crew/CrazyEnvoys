package me.BadBones69.envoy.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

import me.BadBones69.envoy.Main;
import me.BadBones69.envoy.Methods;
import me.BadBones69.envoy.MultiSupport.HolographicSupport;
import me.BadBones69.envoy.MultiSupport.Support;
import me.BadBones69.envoy.controlers.EditControl;
import me.BadBones69.envoy.controlers.EnvoyControl;
import me.BadBones69.envoy.controlers.FireworkDamageAPI;

public class Envoy {
	
	private static BukkitTask runTimeTask;
	private static BukkitTask coolDownTask;
	private static Calendar nextEnvoy;
	private static Calendar envoyTimeLeft;
	private static Boolean envoyActive = false;
	private static ArrayList<UUID> ignoreMessages = new ArrayList<UUID>();
	private static ArrayList<Calendar> warnings = new ArrayList<Calendar>();
	private static ArrayList<Location> locations = new ArrayList<Location>();
	private static ArrayList<Entity> fallingBlocks = new ArrayList<Entity>();
	private static Location center = Bukkit.getWorlds().get(0).getSpawnLocation();
	private static HashMap<Location, String> activeEnvoys = new HashMap<Location, String>();
	private static HashMap<Location, BukkitTask> activeSignals = new HashMap<Location, BukkitTask>();
	private static Plugin plugin = Bukkit.getPluginManager().getPlugin("CrazyEnvoy");
	
	/**
	 * Run this when you need to load the new locations.
	 */
	public static void load(){
		if(!envoyActive){
			envoyActive = false;
		}
		locations.clear();
		FileConfiguration data = Main.settings.getData();
		FileConfiguration config = Main.settings.getConfig();
		envoyTimeLeft = Calendar.getInstance();
		for(String l : data.getStringList("Locations.Spawns")){
			locations.add(getLocationFromString(l));
		}
		if(Calendar.getInstance().after(getNextEnvoy())){
			setEnvoyActive(false);
		}
		if(Main.settings.getData().contains("Center")){
			center = getLocationFromString(Main.settings.getData().getString("Center"));
		}
		if(config.getBoolean("Settings.Envoy-Timer-Toggle")){
			Calendar cal = Calendar.getInstance();
			if(config.getBoolean("Settings.Envoy-Cooldown-Toggle")){
				cal.setTimeInMillis(data.getLong("Next-Envoy"));
				if(Calendar.getInstance().after(cal)){
					cal.setTimeInMillis(getEnvoyCooldown().getTimeInMillis());
				}
			}else{
				String time = config.getString("Settings.Envoy-Time");
				int hour = Integer.parseInt(time.split(" ")[0].split(":")[0]);
				int min = Integer.parseInt(time.split(" ")[0].split(":")[1]);
				int c = Calendar.AM;
				if(time.split(" ")[1].equalsIgnoreCase("AM")){
					c = Calendar.AM;
				}else if(time.split(" ")[1].equalsIgnoreCase("PM")){
					c = Calendar.PM;
				}
				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.getTime(); // Without this makes the hours not change for some reason.
				cal.set(Calendar.MINUTE, min);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.AM_PM, c);
				if(cal.before(Calendar.getInstance())){
					cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
				}
			}
			nextEnvoy = cal;
			startEnvoyCountDown();
			resetWarnings();
		}else{
			nextEnvoy = Calendar.getInstance();
		}
	}
	
	private static Location getLocationFromString(String locationString){
		World w = Bukkit.getWorlds().get(0);
		int x = 0;
		int y = 0;
		int z = 0;
		for(String i : locationString.split(", ")){
			if(i.startsWith("World:")){
				w = Bukkit.getWorld(i.replaceAll("World:", ""));
			}
			if(i.startsWith("X:")){
				x = Integer.parseInt(i.replaceAll("X:", ""));
			}
			if(i.startsWith("Y:")){
				y = Integer.parseInt(i.replaceAll("Y:", ""));
			}
			if(i.startsWith("Z:")){
				z = Integer.parseInt(i.replaceAll("Z:", ""));
			}
		}
		return new Location(w, x, y, z);
	}
	
	/**
	 * Run this when you need to save the locations.
	 */
	public static void unload(){
		ArrayList<String> locs = new ArrayList<String>();
		for(Location loc : locations){
			locs.add("World:" + loc.getWorld().getName() + ", X:" + loc.getBlockX() + ", Y:" + loc.getBlockY() + ", Z:" + loc.getBlockZ());
		}
		deSpawnCrates();
		Main.settings.getData().set("Next-Envoy", getNextEnvoy().getTimeInMillis());
		try{
			Main.settings.getData().set("Center", "World:" + center.getWorld().getName() + ", X:" + center.getBlockX() + ", Y:" + center.getBlockY() + ", Z:" + center.getBlockZ());
		}catch(Exception e){}
		Main.settings.getData().set("Locations.Spawns", locs);
		Main.settings.saveData();
		locations.clear();
		EnvoyControl.clearCooldowns();
	}
	
	/**
	 * Used when the plugin starts to control the count down and when the event starts
	 */
	public static void startEnvoyCountDown(){
		cancelEnvoyCooldownTime();
		coolDownTask = new BukkitRunnable(){
			@Override
			public void run() {
				if(!isEnvoyActive()){
					Calendar cal = Calendar.getInstance();
					cal.clear(Calendar.MILLISECOND);
					for(Calendar warn : getWarnings()){
						Calendar check = Calendar.getInstance();
						check.setTimeInMillis(warn.getTimeInMillis());
						check.clear(Calendar.MILLISECOND);
						if(check.compareTo(cal) == 0){
							HashMap<String, String> placeholder = new HashMap<String, String>();
							placeholder.put("%time%", getNextEnvoyTime());
							Messages.WARNING.broadcastMessage(false, placeholder);
						}
					}
					Calendar next = Calendar.getInstance();
					next.setTimeInMillis(getNextEnvoy().getTimeInMillis());
					next.clear(Calendar.MILLISECOND);
					if(next.compareTo(cal) <= 0){
						if(!isEnvoyActive()){
							if(Main.settings.getConfig().contains("Settings.Minimum-Players-Toggle") && Main.settings.getConfig().contains("Settings.Minimum-Players")){
								if(Main.settings.getConfig().getBoolean("Settings.Minimum-Players-Toggle")){
									int online = Bukkit.getServer().getOnlinePlayers().size();
									if(online < Main.settings.getConfig().getInt("Settings.Minimum-Players")){
										HashMap<String, String> placeholder = new HashMap<String, String>();
										placeholder.put("%amount%", online + "");
										Messages.NOT_ENOUGH_PLAYERS.broadcastMessage(false, placeholder);
										setNextEnvoy(getEnvoyCooldown());
										resetWarnings();
										return;
									}
								}
							}
							startEnvoyEvent();
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
	public static String getTier(Location loc){
		return activeEnvoys.get(loc);
	}
	
	private static void setEnvoyActive(Boolean toggle){
		envoyActive = toggle;
	}
	
	/**
	 * 
	 * @return True if the envoy event is currently happening and false if not.
	 */
	public static Boolean isEnvoyActive(){
		return envoyActive;
	}
	
	/**
	 * Despawns all of the active crates.
	 */
	public static void deSpawnCrates(){
		envoyActive = false;
		for(Location loc : getActiveEnvoys()){
			loc.getBlock().setType(Material.AIR);
			stopSignalFlare(loc);
		}
		for(Entity en : fallingBlocks){
			en.remove();
		}
		if(Support.hasHolographicDisplay()){
			HolographicSupport.removeAllHolograms();
		}
		fallingBlocks.clear();
		activeEnvoys.clear();
	}
	
	/**
	 * 
	 * @return All the location the chests will spawn.
	 */
	public static ArrayList<Location> getLocations(){
		return locations;
	}
	
	/**
	 * 
	 * @param Loc The location that you want to check.
	 * @return
	 */
	public static Boolean isLocation(Location Loc){
		for(Location l : locations){
			if(l.equals(Loc)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @return All the active envoys that are active.
	 */
	public static Set<Location> getActiveEnvoys(){
		return activeEnvoys.keySet();
	}
	
	/**
	 * 
	 * @param loc The location your are checking.
	 * @return Turn if it is and false if not.
	 */
	public static Boolean isActiveEnvoy(Location loc){
		return activeEnvoys.containsKey(loc);
	}
	
	/**
	 * 
	 * @param loc The location you wish to add.
	 */
	public static void addActiveEnvoy(Location loc, String tier){
		activeEnvoys.put(loc, tier);
	}
	
	/**
	 * 
	 * @param loc The location you wish to remove.
	 */
	public static void removeActiveEnvoy(Location loc){
		activeEnvoys.remove(loc);
	}
	
	/**
	 * 
	 * @param loc The location you want to add.
	 */
	public static void addLocation(Location loc){
		locations.add(loc);
	}
	
	/**
	 * 
	 * @param loc The location you want to remove.
	 */
	public static void removeLocation(Location loc){
		if(isLocation(loc)){
			locations.remove(loc);
		}
	}
	
	/**
	 * 
	 * @return The next envoy time as a calendar.
	 */
	public static Calendar getNextEnvoy(){
		return nextEnvoy;
	}
	
	/**
	 * 
	 * @param cal The time of the next envoy.
	 * @return The time till the next envoy.
	 */
	public static String getNextEnvoyTime(){
		Calendar cal = getNextEnvoy();
		Calendar C = Calendar.getInstance();
		int total = ((int) (cal.getTimeInMillis() / 1000) - (int) (C.getTimeInMillis() / 1000));
		int D = 0;
		int H = 0;
		int M = 0;
		int S = 0;
		for(;total > 86400; total -= 86400, D++);
		for(;total > 3600; total -= 3600, H++);
		for(;total >= 60; total -= 60, M++);
		S += total;
		String msg = "";
		if(D > 0) msg += D + "d, ";
		if(D > 0 || H > 0) msg += H + "h, ";
		if(D > 0 || H > 0 || M > 0) msg += M + "m, ";
		if(D > 0 || H > 0 || M > 0 || S > 0) msg += S + "s, ";
		if(msg.length() < 2){
			msg = Main.settings.getMessages().getString("Messages.Hologram-Placeholders.On-Going");
		}else{
			msg = msg.substring(0, msg.length() - 2);
		}
		return msg;
	}
	
	/**
	 * 
	 * @param cal A calendar that has the next time the envoy will happen.
	 */
	public static void setNextEnvoy(Calendar cal){
		nextEnvoy = cal;
	}
	
	/**
	 * 
	 * @return All falling blocks are are currently going.
	 */
	public static ArrayList<Entity> getFallingBlocks(){
		return fallingBlocks;
	}
	
	/**
	 * 
	 * @param en Remove a falling block from the list.
	 */
	public static void removeFallingBlock(Entity en){
		if(fallingBlocks.contains(en)){
			fallingBlocks.remove(en);
		}
	}
	
	/**
	 * Call when you want to set the new warning.
	 */
	public static void resetWarnings(){
		warnings.clear();
		for(String time : Main.settings.getConfig().getStringList("Settings.Envoy-Warnings")){
			addWarning(makeWarning(time));
		}
	}
	
	/**
	 * 
	 * @param cal When adding a new warning.
	 */
	public static void addWarning(Calendar cal){
		warnings.add(cal);
	}
	
	/**
	 * 
	 * @return All the current warnings.
	 */
	public static ArrayList<Calendar> getWarnings(){
		return warnings;
	}
	
	/**
	 * 
	 * @param time The new time for the warning.
	 * @return The new time as a calendar
	 */
	public static Calendar makeWarning(String time){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(getNextEnvoy().getTimeInMillis());
		for(String i : time.split(" ")){
			if(i.contains("D")||i.contains("d")){
				cal.add(Calendar.DATE, -Integer.parseInt(i.replaceAll("D", "").replaceAll("d", "")));
			}
			if(i.contains("H")||i.contains("h")){
				cal.add(Calendar.HOUR, -Integer.parseInt(i.replaceAll("H", "").replaceAll("h", "")));
			}
			if(i.contains("M")||i.contains("m")){
				cal.add(Calendar.MINUTE, -Integer.parseInt(i.replaceAll("M", "").replaceAll("m", "")));
			}
			if(i.contains("S")||i.contains("s")){
				cal.add(Calendar.SECOND, -Integer.parseInt(i.replaceAll("S", "").replaceAll("s", "")));
			}
		}
		return cal;
	}
	
	private static Calendar getEnvoyCooldown(){
		Calendar cal = Calendar.getInstance();
		FileConfiguration config = Main.settings.getConfig();
		if(config.getBoolean("Settings.Envoy-Cooldown-Toggle")){
			String time = config.getString("Settings.Envoy-Cooldown");
			for(String i : time.split(" ")){
				if(i.contains("D") || i.contains("d")){
					cal.add(Calendar.DATE, Integer.parseInt(i.replaceAll("D", "").replaceAll("d", "")));
				}
				if(i.contains("H") || i.contains("h")){
					cal.add(Calendar.HOUR, Integer.parseInt(i.replaceAll("H", "").replaceAll("h", "")));
				}
				if(i.contains("M") || i.contains("m")){
					cal.add(Calendar.MINUTE, Integer.parseInt(i.replaceAll("M", "").replaceAll("m", "")));
				}
				if(i.contains("S") || i.contains("s")){
					cal.add(Calendar.SECOND, Integer.parseInt(i.replaceAll("S", "").replaceAll("s", "")));
				}
			}
		}else{
			String time = config.getString("Settings.Envoy-Time");
			int hour = Integer.parseInt(time.split(" ")[0].split(":")[0]);
			int min = Integer.parseInt(time.split(" ")[0].split(":")[1]);
			int c = Calendar.AM;
			if(time.split(" ")[1].equalsIgnoreCase("AM")){
				c = Calendar.AM;
			}else if(time.split(" ")[1].equalsIgnoreCase("PM")){
				c = Calendar.PM;
			}
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.getTime(); // Without this makes the hours not change for some reason.
			cal.set(Calendar.MINUTE, min);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.AM_PM, c);
			if(cal.before(Calendar.getInstance())){
				cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
			}
		}
		return cal;
	}
	
	private static Calendar getEnvoyRunTimeCalendar(){
		Calendar cal = Calendar.getInstance();
		String time = Main.settings.getConfig().getString("Settings.Envoy-Run-Time");
		for(String i : time.split(" ")){
			if(i.contains("D")||i.contains("d")){
				cal.add(Calendar.DATE, Integer.parseInt(i.replaceAll("D", "").replaceAll("d", "")));
			}
			if(i.contains("H")||i.contains("h")){
				cal.add(Calendar.HOUR, Integer.parseInt(i.replaceAll("H", "").replaceAll("h", "")));
			}
			if(i.contains("M")||i.contains("m")){
				cal.add(Calendar.MINUTE, Integer.parseInt(i.replaceAll("M", "").replaceAll("m", "")));
			}
			if(i.contains("S")||i.contains("s")){
				cal.add(Calendar.SECOND, Integer.parseInt(i.replaceAll("S", "").replaceAll("s", "")));
			}
		}
		return cal;
	}
	
	/**
	 * 
	 * @return The time left in the current envoy evnet.
	 */
	public static String getEnvoyRunTimeLeft(){
		Calendar cal = envoyTimeLeft;
		Calendar C = Calendar.getInstance();
		int total = ((int) (cal.getTimeInMillis() / 1000) - (int) (C.getTimeInMillis() / 1000));
		int D = 0;
		int H = 0;
		int M = 0;
		int S = 0;
		for(;total > 86400; total -= 86400, D++);
		for(;total > 3600; total -= 3600, H++);
		for(;total >= 60; total -= 60, M++);
		S += total;
		String msg = "";
		if(D > 0) msg += D + "d, ";
		if(D > 0 || H > 0) msg += H + "h, ";
		if(D > 0 || H > 0 || M > 0) msg += M + "m, ";
		if(D > 0 || H > 0 || M > 0 || S > 0) msg += S + "s, ";
		if(msg.length() < 2){
			msg = Main.settings.getMessages().getString("Messages.Hologram-Placeholders.Not-Running");
		}else{
			msg = msg.substring(0, msg.length() - 2);
		}
		return msg;
	}
	
	private static Integer getTimeSeconds(String time){
		Integer seconds = 0;
		for(String i : time.split(" ")){
			if(i.contains("D")||i.contains("d")){
				seconds += Integer.parseInt(i.replaceAll("D", "").replaceAll("d", ""))*86400;
			}
			if(i.contains("H")||i.contains("h")){
				seconds += Integer.parseInt(i.replaceAll("H", "").replaceAll("h", ""))*3600;
			}
			if(i.contains("M")||i.contains("m")){
				seconds += Integer.parseInt(i.replaceAll("M", "").replaceAll("m", ""))*60;
			}
			if(i.contains("S")||i.contains("s")){
				seconds += Integer.parseInt(i.replaceAll("S", "").replaceAll("s", ""));
			}
		}
		return seconds;
	}
	
	/**
	 * Call when the run time needs canceled.
	 */
	public static void cancelEnvoyRunTime(){
		try{
			runTimeTask.cancel();
		}catch(Exception e){}
	}
	
	/**
	 * Call when the cool down time needs canceled.
	 */
	public static void cancelEnvoyCooldownTime(){
		try{
			coolDownTask.cancel();
		}catch(Exception e){}
	}
	
	/**
	 * Starts the envoy event.
	 */
	@SuppressWarnings("deprecation")
	public static void startEnvoyEvent(){
		for(Player player : EditControl.getEditors()){
			EditControl.removeFakeBlocks(player);
			player.getInventory().removeItem(new ItemStack(Material.BEDROCK, 1));
			Messages.KICKED_FROM_EDITOR_MODE.sendMessage(player);
		}
		EditControl.getEditors().clear();
		if(Prizes.getTiers().size() == 0){
			Bukkit.broadcastMessage(Methods.getPrefix() + Methods.color("&cNo tiers were found. Please delete the Tiers folder"
					+ " to allow it to remake the default tier files."));
			return;
		}
		deSpawnCrates();
		setEnvoyActive(true);
		int max = getLocations().size();
		if(Main.settings.getConfig().getBoolean("Settings.Max-Crate-Toggle")){
			max = Main.settings.getConfig().getInt("Settings.Max-Crates");
			if(max > getLocations().size()){
				max = getLocations().size();
			}
		}
		ArrayList<Location> locs = new ArrayList<Location>();
		if(Main.settings.getConfig().getBoolean("Settings.Max-Crate-Toggle")){
			for(int i = 0; i < max;){
				Location loc = locations.get(new Random().nextInt(locations.size()));
				if(!locs.contains(loc)){
					locs.add(loc);
					i++;
				}
			}
		}else{
			locs.addAll(locations);
		}
		if(Main.settings.getConfig().getBoolean("Settings.Random-Locations")){
			locs.clear();
			max = Main.settings.getConfig().getInt("Settings.Max-Crates");
			ArrayList<Location> min = getBlocks(center.clone(), Main.settings.getConfig().getInt("Settings.Min-Radius"));
			int stop = 0;
			for(int i = 0; i < max && stop < 2000; stop++){
				int m = Main.settings.getConfig().getInt("Settings.Max-Radius");
				Location loc = center.clone();
				loc.add(-(m/2) + new Random().nextInt(m), 0, -(m/2) + new Random().nextInt(m));
				loc.setY(255);
				if(!loc.getChunk().isLoaded()){
					loc.getChunk().load();
				}
				for(; loc.getBlock().getType() == Material.AIR && loc.getBlockY() >= 0;){
					if(loc.getBlockY() <= 0){
						break;
					}
					loc.add(0, -1, 0);
				}
				if(loc.getBlockY() <= 0){
					continue;
				}
				Location check = loc.clone();
				check.setY(255);
				if(min.contains(check)){
					continue;
				}
				if(locs.contains(loc.clone().add(0, 1, 0))){
					continue;
				}
				locs.add(loc.add(0, 1, 0));
				i++;
			}
		}
		HashMap<String, String> placeholder = new HashMap<String, String>();
		placeholder.put("%amount%", max + "");
		Messages.STARTED.broadcastMessage(false, placeholder);
		for(Location loc : locs){
			boolean spawnFallingBlock = false;
			for(Entity en : Methods.getNearbyEntities(loc, 40, 40, 40)){
				if(en instanceof Player){
					spawnFallingBlock = true;
				}
			}
			if(Main.settings.getConfig().contains("Settings.Falling-Block-Toggle")){
				if(!Main.settings.getConfig().getBoolean("Settings.Falling-Block-Toggle")){
					spawnFallingBlock = false;
				}
			}
			if(spawnFallingBlock){
				String type = Main.settings.getConfig().getString("Settings.Falling-Block");
				int ty = 0;
				if(type.contains(":")){
					String[] b = type.split(":");
					type = b[0];
					ty = Integer.parseInt(b[1]);
				}
				Material m = Material.matchMaterial(type);
				int height = Main.settings.getConfig().getInt("Settings.Fall-Height");
				if(!loc.getChunk().isLoaded()){
					loc.getChunk().load();
				}
				FallingBlock chest = (FallingBlock) loc.getWorld().spawnFallingBlock(loc.clone().add(.5, height, .5), m, (byte) ty);
				fallingBlocks.add(chest);
			}else{
				String tier = Prizes.pickTierByChance();
				if(!loc.getChunk().isLoaded()){
					loc.getChunk().load();
				}
				loc.getBlock().setType(Methods.makeItem(Main.settings.getFile(tier).getString("Settings.Placed-Block"), 1, "").getType());
				if(Support.hasHolographicDisplay()){
					double hight = 1.5;
					if(Main.settings.getFile(tier).contains("Settings.Hologram-Hight")){
						hight = Main.settings.getFile(tier).getDouble("Settings.Hologram-Hight");
					}
					HolographicSupport.createHologram(loc.clone().add(.5, hight, .5), tier);
				}
				addActiveEnvoy(loc.clone(), tier);
				if(Main.settings.getFile(tier).getBoolean("Settings.Signal-Flare.Toggle")){
					startSignalFlare(loc.clone(), tier);
				}
			}
		}
		runTimeTask = new BukkitRunnable(){
			@Override
			public void run() {
				Messages.ENDED.broadcastMessage(false, null);
				endEnvoyEvent();
			}
		}.runTaskLater(plugin, getTimeSeconds(Main.settings.getConfig().getString("Settings.Envoy-Run-Time")) * 20);
		envoyTimeLeft = getEnvoyRunTimeCalendar();
	}
	
	private static ArrayList<Location> getBlocks(Location loc, int radius){
		Location loc2 = loc.clone();
		loc.add(-radius, 0, -radius);
		loc2.add(radius, 0, radius);
		ArrayList<Location> locs = new ArrayList<Location>();
		int topBlockX = (loc.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc.getBlockX());
		int bottomBlockX = (loc.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc.getBlockX());
		int topBlockZ = (loc.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc.getBlockZ());
		int bottomBlockZ = (loc.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc.getBlockZ());
		for(int x = bottomBlockX; x <= topBlockX; x++){
			for(int z = bottomBlockZ; z <= topBlockZ; z++){
				locs.add(loc.getWorld().getBlockAt(x, 255, z).getLocation());
			}
		}
		return locs;
	}
	
	/**
	 * Ends the envoy event.
	 */
	public static void endEnvoyEvent(){
		deSpawnCrates();
		setEnvoyActive(false);
		cancelEnvoyRunTime();
		if(Main.settings.getConfig().getBoolean("Settings.Envoy-Timer-Toggle")){
			setNextEnvoy(getEnvoyCooldown());
			resetWarnings();
		}
		EnvoyControl.clearCooldowns();
	}
	
	/**
	 * 
	 * @param loc The location the signals will be at.
	 * @param tier The tier the signal is.
	 */
	public static void startSignalFlare(final Location loc, final String tier){
		BukkitTask task;
		task = new BukkitRunnable(){
			@Override
			public void run() {
				playSignal(loc.clone().add(.5, 0, .5), tier);
			}
		}.runTaskTimer(plugin, getTimeSeconds(Main.settings.getFile(tier).getString("Settings.Signal-Flare.Time")) * 20,
				getTimeSeconds(Main.settings.getFile(tier).getString("Settings.Signal-Flare.Time")) * 20);
		activeSignals.put(loc, task);
	}
	
	/**
	 * 
	 * @param loc The location that the signal is stopping.
	 */
	public static void stopSignalFlare(Location loc){
		try{
			activeSignals.get(loc).cancel();
		}catch(Exception e){}
		activeSignals.remove(loc);
	}
	
	private static void playSignal(Location loc, String tier) {
		ArrayList<Color> colors = new ArrayList<Color>();
		for(String c : Main.settings.getFile(tier).getStringList("Settings.Signal-Flare.Colors")){
			Color color = Methods.getColor(c);
			if(color != null){
				colors.add(color);
			}
		}
		Firework fw = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffects(FireworkEffect.builder()
				.with(FireworkEffect.Type.BALL_LARGE)
				.withColor(colors)
				.trail(true)
				.flicker(false)
				.build());
		fm.setPower(1);
		fw.setFireworkMeta(fm);
		FireworkDamageAPI.addFirework(fw);
	}
	
	/**
	 * 
	 * @return The center location for the random crates.
	 */
	public static Location getCenter(){
		return center;
	}
	
	/**
	 * Sets the center location for the random crates.
	 * @param loc The new center location.
	 */
	public static void setCenter(Location loc){
		center = loc;
	}
	
	/**
	 * Check if a player is ignoring the messages.
	 * @param uuid The player's UUID.
	 * @return True if they are ignoring them and false if not.
	 */
	public static boolean isIgnoringMessages(UUID uuid){
		return ignoreMessages.contains(uuid);
	}
	
	/**
	 * Make a player ignore the messages.
	 * @param uuid The player's UUID.
	 */
	public static void addIgnorePlayer(UUID uuid){
		ignoreMessages.add(uuid);
	}
	
	/**
	 * Make a player stop ignoring the messages.
	 * @param uuid The player's UUID.
	 */
	public static void removeIgnorePlayer(UUID uuid){
		if(ignoreMessages.contains(uuid)){
			ignoreMessages.remove(uuid);
		}
	}
	
}