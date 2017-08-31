package me.badbones69.envoy.api;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.badbones69.envoy.Main;
import me.badbones69.envoy.Methods;

public class Flare {
	
	public static ItemStack getFlare(int amount) {
		FileConfiguration config = Main.settings.getConfig();
		String id = config.getString("Settings.Flares.Item");
		String name = config.getString("Settings.Flares.Name");
		List<String> lore = config.getStringList("Settings.Flares.Lore");
		return Methods.makeItem(id, amount, name, lore);
	}
	
	public static Boolean isFlare(ItemStack item) {
		if(getFlare(1).isSimilar(item)) {
			return true;
		}
		return false;
	}
	
	public static void giveFlare(Player player, int amount) {
		if(Methods.isInvFull(player)) {
			player.getWorld().dropItem(player.getLocation(), getFlare(amount));
		}else {
			player.getInventory().addItem(getFlare(amount));
		}
	}
	
	public static void takeFlare(Player player, ItemStack flare) {
		if(flare.getAmount() <= 1) {
			player.getInventory().removeItem(flare);
		}
		if(flare.getAmount() > 1) {
			flare.setAmount(flare.getAmount() - 1);
		}
	}
	
}