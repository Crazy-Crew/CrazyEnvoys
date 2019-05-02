package me.badbones69.crazyenvoy;

import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.FileManager.Files;
import me.badbones69.crazyenvoy.controllers.FireworkDamageAPI;
import me.badbones69.crazyenvoy.multisupport.Version;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Methods {
	
	private static CrazyEnvoy envoy = CrazyEnvoy.getInstance();
	
	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public static String removeColor(String msg) {
		return ChatColor.stripColor(msg);
	}
	
	public static String getPrefix() {
		return color(Files.CONFIG.getFile().getString("Settings.Prefix"));
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getItemInHand(Player player) {
		if(Version.getCurrentVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()) {
			return player.getInventory().getItemInMainHand();
		}else {
			return player.getItemInHand();
		}
	}
	
	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		}catch(NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	public static boolean isOnline(String name) {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			if(player.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public static Player getPlayer(String name) {
		return Bukkit.getServer().getPlayer(name);
	}
	
	public static boolean isInvFull(Player player) {
		return player.getInventory().firstEmpty() == -1;
	}
	
	public static Entity fireWork(Location loc, List<Color> colors) {
		Firework firework = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fireworkMeta = firework.getFireworkMeta();
		fireworkMeta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(colors).trail(false).flicker(false).build());
		fireworkMeta.setPower(0);
		firework.setFireworkMeta(fireworkMeta);
		FireworkDamageAPI.addFirework(firework);
		detonate(firework);
		return firework;
	}
	
	private static void detonate(final Firework f) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(envoy.getPlugin(), f::detonate, 2);
	}
	
	public static Color getColor(String color) {
		if(color.equalsIgnoreCase("AQUA")) return Color.AQUA;
		if(color.equalsIgnoreCase("BLACK")) return Color.BLACK;
		if(color.equalsIgnoreCase("BLUE")) return Color.BLUE;
		if(color.equalsIgnoreCase("FUCHSIA")) return Color.FUCHSIA;
		if(color.equalsIgnoreCase("GRAY")) return Color.GRAY;
		if(color.equalsIgnoreCase("GREEN")) return Color.GREEN;
		if(color.equalsIgnoreCase("LIME")) return Color.LIME;
		if(color.equalsIgnoreCase("MAROON")) return Color.MAROON;
		if(color.equalsIgnoreCase("NAVY")) return Color.NAVY;
		if(color.equalsIgnoreCase("OLIVE")) return Color.OLIVE;
		if(color.equalsIgnoreCase("ORANGE")) return Color.ORANGE;
		if(color.equalsIgnoreCase("PURPLE")) return Color.PURPLE;
		if(color.equalsIgnoreCase("RED")) return Color.RED;
		if(color.equalsIgnoreCase("SILVER")) return Color.SILVER;
		if(color.equalsIgnoreCase("TEAL")) return Color.TEAL;
		if(color.equalsIgnoreCase("WHITE")) return Color.WHITE;
		if(color.equalsIgnoreCase("YELLOW")) return Color.YELLOW;
		return Color.WHITE;
	}
	
	public static List<String> getPage(List<String> list, Integer page) {
		List<String> locations = new ArrayList<>();
		if(page <= 0) {
			page = 1;
		}
		int max = 10;
		int index = page * max - max;
		int endIndex = index >= list.size() ? list.size() - 1 : index + max;
		for(; index < endIndex; index++) {
			if(index < list.size()) {
				locations.add(list.get(index));
			}
		}
		for(; locations.size() == 0; page--) {
			if(page <= 0) {
				break;
			}
			index = page * max - max;
			endIndex = index >= list.size() ? list.size() - 1 : index + max;
			for(; index < endIndex; index++) {
				if(index < list.size()) {
					locations.add(list.get(index));
				}
			}
		}
		return locations;
	}
	
	public static boolean isSuccessful(int min, int max) {
		if(max <= min || max <= 0) {
			return true;
		}
		Random number = new Random();
		int chance = 1 + number.nextInt(max);
		return chance >= 1 && chance <= min;
	}
	
	public static void hasUpdate() {
		try {
			HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=32870").getBytes(StandardCharsets.UTF_8));
			String oldVersion = envoy.getPlugin().getDescription().getVersion();
			String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
			if(!newVersion.equals(oldVersion)) {
				Bukkit.getConsoleSender().sendMessage(getPrefix() + color("&cYour server is running &7v" + oldVersion + "&c and the newest is &7v" + newVersion + "&c."));
			}
		}catch(Exception e) {
		}
	}
	
	public static void hasUpdate(Player player) {
		try {
			HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=32870").getBytes(StandardCharsets.UTF_8));
			String oldVersion = envoy.getPlugin().getDescription().getVersion();
			String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
			if(!newVersion.equals(oldVersion)) {
				player.sendMessage(getPrefix() + color("&cYour server is running &7v" + oldVersion + "&c and the newest is &7v" + newVersion + "&c."));
			}
		}catch(Exception e) {
		}
	}
	
	public static String getEnchantmentName(Enchantment en) {
		HashMap<String, String> enchants = new HashMap<>();
		enchants.put("ARROW_DAMAGE", "Power");
		enchants.put("ARROW_FIRE", "Flame");
		enchants.put("ARROW_INFINITE", "Infinity");
		enchants.put("ARROW_KNOCKBACK", "Punch");
		enchants.put("DAMAGE_ALL", "Sharpness");
		enchants.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
		enchants.put("DAMAGE_UNDEAD", "Smite");
		enchants.put("DEPTH_STRIDER", "Depth_Strider");
		enchants.put("DIG_SPEED", "Efficiency");
		enchants.put("DURABILITY", "Unbreaking");
		enchants.put("FIRE_ASPECT", "Fire_Aspect");
		enchants.put("KNOCKBACK", "KnockBack");
		enchants.put("LOOT_BONUS_BLOCKS", "Fortune");
		enchants.put("LOOT_BONUS_MOBS", "Looting");
		enchants.put("LUCK", "Luck_Of_The_Sea");
		enchants.put("LURE", "Lure");
		enchants.put("OXYGEN", "Respiration");
		enchants.put("PROTECTION_ENVIRONMENTAL", "Protection");
		enchants.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
		enchants.put("PROTECTION_FALL", "Feather_Falling");
		enchants.put("PROTECTION_FIRE", "Fire_Protection");
		enchants.put("PROTECTION_PROJECTILE", "Projectile_Protection");
		enchants.put("SILK_TOUCH", "Silk_Touch");
		enchants.put("THORNS", "Thorns");
		enchants.put("WATER_WORKER", "Aqua_Affinity");
		enchants.put("BINDING_CURSE", "Curse_Of_Binding");
		enchants.put("MENDING", "Mending");
		enchants.put("FROST_WALKER", "Frost_Walker");
		enchants.put("VANISHING_CURSE", "Curse_Of_Vanishing");
		if(enchants.get(en.getName()) == null) {
			return "None Found";
		}
		return enchants.get(en.getName());
	}
	
	@SuppressWarnings("deprecation")
	public static List<Entity> getNearbyEntities(Location loc, double x, double y, double z) {
		FallingBlock ent = loc.getWorld().spawnFallingBlock(loc.subtract(0, 0, 0), Material.AIR, (byte) 0);
		List<Entity> out = ent.getNearbyEntities(x, y, z);
		ent.remove();
		return out;
	}
	
}