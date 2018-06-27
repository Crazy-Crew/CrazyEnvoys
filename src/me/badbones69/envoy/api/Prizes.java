package me.badbones69.envoy.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import me.badbones69.envoy.Main;
import me.badbones69.envoy.Methods;

public class Prizes {

	private static List<String> tiers = new ArrayList<String>();
	private static HashMap<String, Boolean> useChance = new HashMap<String, Boolean>();
	private static HashMap<String, Integer> spawnChance = new HashMap<String, Integer>();
	private static HashMap<String, List<String>> prizes = new HashMap<String, List<String>>();
	private static HashMap<String, HashMap<String, Integer>> chances = new HashMap<String, HashMap<String, Integer>>();
	private static HashMap<String, HashMap<String, List<String>>> commands = new HashMap<String, HashMap<String, List<String>>>();
	private static HashMap<String, HashMap<String, List<String>>> messages = new HashMap<String, HashMap<String, List<String>>>();
	private static HashMap<String, HashMap<String, ArrayList<ItemStack>>> items = new HashMap<String, HashMap<String, ArrayList<ItemStack>>>();

	/**
	 * Use when you want to get the prizes.
	 */
	public static void loadPrizes() {
		tiers.clear();
		items.clear();
		prizes.clear();
		chances.clear();
		commands.clear();
		messages.clear();
		spawnChance.clear();
		for(String tier : Main.settings.getAllTierNames()) {
			FileConfiguration tfile = Main.settings.getFile(tier);
			useChance.put(tier, tfile.getBoolean("Settings.Use-Chance"));
			ArrayList<String> ps = new ArrayList<String>();
			HashMap<String, Integer> chance = new HashMap<String, Integer>();
			HashMap<String, List<String>> cmds = new HashMap<String, List<String>>();
			HashMap<String, List<String>> msg = new HashMap<String, List<String>>();
			HashMap<String, ArrayList<ItemStack>> its = new HashMap<String, ArrayList<ItemStack>>();
			for(String p : tfile.getConfigurationSection("Prizes").getKeys(false)) {
				ps.add(p);
				chance.put(p, tfile.getInt("Prizes." + p + ".Chance"));
				cmds.put(p, tfile.getStringList("Prizes." + p + ".Commands"));
				msg.put(p, tfile.getStringList("Prizes." + p + ".Messages"));
				ArrayList<ItemStack> iTS = new ArrayList<ItemStack>();
				for(String l : tfile.getStringList("Prizes." + p + ".Items")) {
					ArrayList<String> lore = new ArrayList<String>();
					HashMap<Enchantment, Integer> enchs = new HashMap<Enchantment, Integer>();
					String name = "";
					int amount = 1;
					String item = "Stone";
					Boolean glowing = false;
					Boolean unbreaking = false;
					for(String i : l.split(", ")) {
						if(i.contains("Item:")) {
							i = i.replaceAll("Item:", "");
							item = i;
						}else if(i.contains("Amount:")) {
							i = i.replaceAll("Amount:", "");
							amount = Integer.parseInt(i);
						}else if(i.contains("Name:")) {
							i = i.replaceAll("Name:", "");
							name = Methods.color(i);
						}else if(i.contains("Lore:")) {
							i = i.replaceAll("Lore:", "");
							for(String L : i.split(",")) {
								L = Methods.color(L);
								lore.add(L);
							}
						}else if(i.contains("Glowing:")) {
							i = i.replaceAll("Glowing:", "");
							glowing = Boolean.parseBoolean(i);
						}else if(i.contains("Unbreaking:")) {
							if(i.replaceAll("Unbreaking:", "").equalsIgnoreCase("true")) {
								unbreaking = true;
							}
						}else {
							for(Enchantment enc : Enchantment.values()) {
								if(i.contains(enc.getName() + ":") || i.contains(Methods.getEnchantmentName(enc) + ":")) {
									String[] breakdown = i.split(":");
									int lvl = Integer.parseInt(breakdown[1]);
									enchs.put(enc, lvl);
								}
							}
						}
					}
					if(unbreaking) {
						iTS.add(Methods.addUnbreaking(Methods.addGlow(Methods.makeItem(item, amount, name, lore, enchs), glowing)));
					}else {
						iTS.add(Methods.addGlow(Methods.makeItem(item, amount, name, lore, enchs), glowing));
					}
				}
				its.put(p, iTS);
				chances.put(tier, chance);
				commands.put(tier, cmds);
				messages.put(tier, msg);
				items.put(tier, its);
			}
			prizes.put(tier, ps);
			spawnChance.put(tier, tfile.getInt("Settings.Spawn-Chance"));
			tiers.add(tier);
		}
	}

	/**
	 * 
	 * @return All the tier.
	 */
	public static List<String> getTiers() {
		return tiers;
	}

	/**
	 * 
	 * @param tier The tier you wish to get the spawn chance from.
	 * @return The chance of the tier spawning.
	 */
	public static Integer getSpawnChance(String tier) {
		return spawnChance.get(tier);
	}

	/**
	 * 
	 * @return A tier based on the spawn chance.
	 */
	public static String pickTierByChance() {
		if(getTiers().size() == 1) {
			return getTiers().get(0);
		}
		ArrayList<String> T = new ArrayList<String>();
		for(; T.size() == 0;) {
			for(String tier : tiers) {
				if(Methods.isSuccessful(spawnChance.get(tier), 100)) {
					T.add(tier);
				}
			}
		}
		return T.get(new Random().nextInt(T.size()));
	}

	/**
	 * 
	 * @return All the prize names.
	 */
	public static List<String> getPrizes(String tier) {
		return prizes.get(tier);
	}

	/**
	 * 
	 * @return If true then prizes are random and if false it uses chance.
	 */
	public static Boolean useChance(String tier) {
		return useChance.get(tier);
	}

	/**
	 * 
	 * @return A random prizes.
	 */
	public static ArrayList<String> pickRandomPrizes(String tier) {
		ArrayList<String> p = new ArrayList<String>();
		int max = getPrizeAmount(tier);
		for(int i = 0; p.size() < max && i < 500; i++) {
			String prize = prizes.get(tier).get(new Random().nextInt(prizes.get(tier).size()));
			if(!p.contains(prize)) {
				p.add(prize);
			}else {
				continue;
			}
		}
		return p;
	}

	/**
	 * 
	 * @return A prize based on the chance the prize has.
	 */
	public static ArrayList<String> pickPrizesByChance(String tier) {
		ArrayList<String> P = new ArrayList<String>();
		for(; P.size() == 0;) {
			for(String prize : getPrizes(tier)) {
				if(Methods.isSuccessful(chances.get(tier).get(prize), 100)) {
					P.add(prize);
				}
			}
		}
		ArrayList<String> p = new ArrayList<String>();
		int max = getPrizeAmount(tier);
		for(int i = 0; p.size() < max && i < 500; i++) {
			String prize = P.get(new Random().nextInt(P.size()));
			if(!p.contains(prize)) {
				p.add(prize);
			}else {
				continue;
			}
		}
		return p;
	}

	/**
	 * 
	 * @param prize The prize you want.
	 * @return The commands for that prize.
	 */
	public static List<String> getCommands(String tier, String prize) {
		return commands.get(tier).get(prize);
	}

	/**
	 * 
	 * @param prize The prize you want.
	 * @return The messages you want to get from that prize.
	 */
	public static List<String> getMessages(String tier, String prize) {
		return messages.get(tier).get(prize);
	}

	/**
	 * 
	 * @param prize The prize you want.
	 * @return The items you want to get from that prize.
	 */
	public static ArrayList<ItemStack> getItems(String tier, String prize) {
		return items.get(tier).get(prize);
	}

	private static Integer getPrizeAmount(String tier) {
		int amount = 1;
		if(Main.settings.getFile(tier).getBoolean("Settings.Bulk-Prizes.Toggle")) {
			amount = Main.settings.getFile(tier).getInt("Settings.Bulk-Prizes.Max-Bulk");
			if(Main.settings.getFile(tier).getBoolean("Settings.Bulk-Prizes.Random")) {
				amount = 1 + new Random().nextInt(amount);
			}
		}
		return amount;
	}

}
