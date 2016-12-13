package me.BadBones69.envoy.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

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
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.BadBones69.envoy.Main;
import me.BadBones69.envoy.Methods;
import me.BadBones69.envoy.MultiSupport.HolographicSupport;
import me.BadBones69.envoy.controlers.EditControl;

public class Envoy {
	
	private static BukkitTask runTimeTask;
	private static BukkitTask coolDownTask;
	private static Calendar nextEnvoy;
	private static Calendar envoyTimeLeft;
	private static Boolean envoyActive = false;
	private static ArrayList<Calendar> warnings = new ArrayList<Calendar>();
	private static ArrayList<Location> locations = new ArrayList<Location>();
	private static ArrayList<Entity> fallingBlocks = new ArrayList<Entity>();
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
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(data.getLong("Next-Envoy"));
		if(Calendar.getInstance().after(cal)){
			cal.setTimeInMillis(getEnvoyCooldown().getTimeInMillis());
		}
		nextEnvoy = cal;
		envoyTimeLeft = Calendar.getInstance();
		resetWarnings();
		for(String l : data.getStringList("Locations.Spawns")){
			World w = Bukkit.getWorlds().get(0);
			int x = 0;
			int y = 0;
			int z = 0;
			for(String i : l.split(", ")){
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
			locations.add(new Location(w, x, y, z));
		}
		if(Calendar.getInstance().after(getNextEnvoy())){
			setEnvoyActive(false);
		}
		startEnvoyCountDown();
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
		Main.settings.getData().set("Locations.Spawns", locs);
		Main.settings.saveData();
		locations.clear();
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
							Bukkit.broadcastMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Warning")
									.replaceAll("%Time%", getNextEnvoyTime()).replaceAll("%time%", getNextEnvoyTime())));
						}
					}
					Calendar next = Calendar.getInstance();
					next.setTimeInMillis(getNextEnvoy().getTimeInMillis());
					next.clear(Calendar.MILLISECOND);
					if(next.compareTo(cal) == 0){
						startEnvoy();
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
		for(Location loc : getActiveEvoys()){
			loc.getBlock().setType(Material.AIR);
			stopSignalFlare(loc);
		}
		for(Entity en : fallingBlocks){
			en.remove();
		}
		if(Methods.hasHolographicDisplay()){
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
	public static Set<Location> getActiveEvoys(){
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
	public static void addActiveEvoy(Location loc, String tier){
		activeEnvoys.put(loc, tier);
	}
	
	/**
	 * 
	 * @param loc The location you wish to remove.
	 */
	public static void removeActiveEvoy(Location loc){
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
		String time = Main.settings.getConfig().getString("Settings.Envoy-Cooldown");
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
	
	private static Integer getEnvoyRunTime(){
		Integer seconds = 0;
		String time = Main.settings.getConfig().getString("Settings.Envoy-Run-Time");
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
	public static void startEnvoy(){
		for(Player player : EditControl.getEditors()){
			EditControl.removeFakeBlocks(player);
			player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Kicked-From-Editor-Mode")));
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
		Bukkit.broadcastMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Started")
				.replaceAll("%Amount%", max + "")
				.replaceAll("%amount%", max + "")));
		if(Main.settings.getConfig().getBoolean("Settings.Max-Crate-Toggle")){
			ArrayList<Location> locs = new ArrayList<Location>();
			Random r = new Random();
			for(int i = 0; i < max;){
				Location loc = locations.get(r.nextInt(locations.size()));
				if(!locs.contains(loc)){
					locs.add(loc);
					i++;
				}
			}
			for(Location loc : locs){
				loc.getChunk().load();
				String type = Main.settings.getConfig().getString("Settings.Falling-Block");
				int ty = 0;
				if(type.contains(":")){
					String[] b = type.split(":");
					type = b[0];
					ty = Integer.parseInt(b[1]);
				}
				Material m = Material.matchMaterial(type);
				FallingBlock chest = (FallingBlock) loc.getWorld().spawnFallingBlock(loc.clone().add(.5, 15, .5), m, (byte) ty);
				boolean toggle = false;
				for(Entity en : chest.getNearbyEntities(100, 100, 100)){
					if(en instanceof Player){
						toggle = true;
					}
				}
				if(!toggle){
					String tier = Prizes.pickTierByChance();
					chest.remove();
					loc.getBlock().setType(Methods.makeItem(Main.settings.getFile(tier).getString("Settings.Placed-Block"), 1, "").getType());
					if(Methods.hasHolographicDisplay()){
						HolographicSupport.createHologram(loc.clone().add(.5, 1.5, .5), tier);
					}
					addActiveEvoy(loc, tier);
					if(Main.settings.getFile(tier).getBoolean("Settings.Signal-Flare.Toggle")){
						startSignalFlare(loc, tier);
					}
				}else{
					chest.setDropItem(false);
					fallingBlocks.add(chest);
				}
			}
		}else{
			for(Location loc : locations){
				loc.getChunk().load();
				String type = Main.settings.getConfig().getString("Settings.Falling-Block");
				int ty = 0;
				if(type.contains(":")){
					String[] b = type.split(":");
					type = b[0];
					ty = Integer.parseInt(b[1]);
				}
				Material m = Material.matchMaterial(type);
				FallingBlock chest = (FallingBlock) loc.getWorld().spawnFallingBlock(loc.clone().add(.5, 15, .5), m, (byte) ty);
				boolean toggle = false;
				for(Entity en : chest.getNearbyEntities(100, 100, 100)){
					if(en instanceof Player){
						toggle = true;
					}
				}
				if(!toggle){
					String tier = Prizes.pickTierByChance();
					chest.remove();
					loc.getBlock().setType(Methods.makeItem(Main.settings.getFile(tier).getString("Settings.Placed-Block"), 1, "").getType());
					if(Methods.hasHolographicDisplay()){
						HolographicSupport.createHologram(loc.clone().add(.5, 1.5, .5), tier);
					}
					addActiveEvoy(loc, tier);
					if(Main.settings.getFile(tier).getBoolean("Settings.Signal-Flare.Toggle")){
						startSignalFlare(loc, tier);
					}
				}else{
					chest.setDropItem(false);
					fallingBlocks.add(chest);
				}
			}
		}
		runTimeTask = new BukkitRunnable(){
			@Override
			public void run() {
				Bukkit.broadcastMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Ended")));
				endEnvoy();
			}
		}.runTaskLater(plugin, getEnvoyRunTime() * 20);
		envoyTimeLeft = getEnvoyRunTimeCalendar();
	}
	
	/**
	 * Ends the envoy event.
	 */
	public static void endEnvoy(){
		deSpawnCrates();
		setEnvoyActive(false);
		cancelEnvoyRunTime();
		setNextEnvoy(getEnvoyCooldown());
		resetWarnings();
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
	}
	
}