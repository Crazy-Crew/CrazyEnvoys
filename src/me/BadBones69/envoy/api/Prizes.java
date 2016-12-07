package me.BadBones69.envoy.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.envoy.Main;
import me.BadBones69.envoy.Methods;

public class Prizes {
	
	private static Boolean isRandom;
	private static ArrayList<String> prizes = new ArrayList<String>();
	private static HashMap<String, Integer> chances = new HashMap<String, Integer>();
	private static HashMap<String, List<String>> commands = new HashMap<String, List<String>>();
	private static HashMap<String, List<String>> messages = new HashMap<String, List<String>>();
	private static HashMap<String, ArrayList<ItemStack>> items = new HashMap<String, ArrayList<ItemStack>>();
	
	/**
	 * Use when you want to get the prizes.
	 */
	public static void loadPrizes(){
		FileConfiguration config = Main.settings.getConfig();
		prizes.clear();
		chances.clear();
		commands.clear();
		messages.clear();
		items.clear();
		isRandom = config.getBoolean("Settings.Use-Chance");
		for(String p : config.getConfigurationSection("Prizes").getKeys(false)){
			int chance = config.getInt("Prizes." + p + ".Chance");
			List<String> cmds = config.getStringList("Prizes." + p + ".Commands");
			List<String> msg = config.getStringList("Prizes." + p + ".Messages");
			ArrayList<ItemStack> its = new ArrayList<ItemStack>();
			for(String l : config.getStringList("Prizes." + p + ".Items")){
				ArrayList<String> lore = new ArrayList<String>();
				HashMap<Enchantment, Integer> enchs = new HashMap<Enchantment, Integer>();
				String name = "";
				int amount = 1;
				String m = "Stone";
				for(String i : l.split(", ")){
					if(i.contains("Item:")){
						i = i.replaceAll("Item:", "");
						m = i;
					}
					if(i.contains("Amount:")){
						i = i.replaceAll("Amount:", "");
						amount = Integer.parseInt(i);
					}
					if(i.contains("Name:")){
						i = i.replaceAll("Name:", "");
						name = Methods.color(i);
					}
					if(i.contains("Lore:")){
						i = i.replaceAll("Lore:", "");
						for(String L : i.split(",")){
							L = Methods.color(L);
							lore.add(L);
						}
					}
					for(Enchantment enc : Enchantment.values()){
						if(i.contains(enc.getName() + ":") || i.contains(Methods.getEnchantmentName(enc) + ":")){
							String[] breakdown = i.split(":");
							int lvl = Integer.parseInt(breakdown[1]);
							enchs.put(enc, lvl);
						}
					}
				}
				its.add(Methods.makeItem(m, amount, name, lore, enchs));
			}
			prizes.add(p);
			chances.put(p, chance);
			commands.put(p, cmds);
			messages.put(p, msg);
			items.put(p, its);
		}
	}
	
	/**
	 * 
	 * @return All the prize names.
	 */
	public static ArrayList<String> getPrizes(){
		return prizes;
	}
	
	/**
	 * 
	 * @return If true then prizes are random and if false it uses chance.
	 */
	public static Boolean isRandom(){
		return isRandom;
	}
	
	/**
	 * 
	 * @return A random prize.
	 */
	public static String pickRandomPrize(){
		Random r = new Random();
		return prizes.get(r.nextInt(prizes.size()));
	}
	
	/**
	 * 
	 * @return A prize based on the chance the prize has.
	 */
	public static String pickPrizeByChance(){
		Random r = new Random();
		ArrayList<String> P = new ArrayList<String>();
		for(; P.size() == 0;){
			for(String prize : prizes){
				if(Methods.isSuccessful(chances.get(prize), 100)){
					P.add(prize);
				}
			}
		}
		return P.get(r.nextInt(P.size()));
	}
	
	/**
	 * 
	 * @param prize The prize you want.
	 * @return The commands for that prize.
	 */
	public static List<String> getCommands(String prize){
		return commands.get(prize);
	}
	
	/**
	 * 
	 * @param prize The prize you want.
	 * @return The messages you want to get from that prize.
	 */
	public static List<String> getMessages(String prize){
		return messages.get(prize);
	}
	
	/**
	 * 
	 * @param prize The prize you want.
	 * @return The items you want to get from that prize.
	 */
	public static ArrayList<ItemStack> getItems(String prize){
		return items.get(prize);
	}
	
}