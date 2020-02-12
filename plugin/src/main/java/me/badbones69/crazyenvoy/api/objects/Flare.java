package me.badbones69.crazyenvoy.api.objects;

import me.badbones69.crazyenvoy.Methods;
import me.badbones69.crazyenvoy.api.FileManager.Files;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Flare {
    
    private static ItemBuilder flareItemBuilder;
    
    public static void load() {
        FileConfiguration config = Files.CONFIG.getFile();
        flareItemBuilder = new ItemBuilder()
        .setMaterial(config.getString("Settings.Flares.Item"))
        .setName(config.getString("Settings.Flares.Name"))
        .setLore(config.getStringList("Settings.Flares.Lore"));
    }
    
    public static ItemStack getFlare() {
        return getFlare(1);
    }
    
    public static ItemStack getFlare(int amount) {
        return flareItemBuilder.setAmount(amount).build();
    }
    
    public static boolean isFlare(ItemStack item) {
        return getFlare().isSimilar(item);
    }
    
    public static void giveFlare(Player player) {
        giveFlare(player, 1);
    }
    
    public static void giveFlare(Player player, int amount) {
        if (Methods.isInvFull(player)) {
            player.getWorld().dropItem(player.getLocation(), getFlare(amount));
        } else {
            player.getInventory().addItem(getFlare(amount));
        }
    }
    
    public static void takeFlare(Player player) {
        player.getInventory().removeItem(getFlare());
    }
    
}