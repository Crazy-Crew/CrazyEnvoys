package me.badbones69.crazyenvoy.api.objects;

import me.badbones69.crazyenvoy.Methods;
import me.badbones69.crazyenvoy.api.FileManager.Files;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Flare {
	
	public static ItemStack getFlare(int amount) {
		FileConfiguration config = Files.CONFIG.getFile();
		String id = config.getString("Settings.Flares.Item");
		String name = config.getString("Settings.Flares.Name");
		List<String> lore = config.getStringList("Settings.Flares.Lore");
		return new ItemBuilder().setMaterial(id).setAmount(amount).setName(name).setLore(lore).build();
	}
	
	public static Boolean isFlare(ItemStack item) {
		return getFlare(1).isSimilar(item);
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