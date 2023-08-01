package com.badbones69.crazyenvoys.paper.api.objects;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.Methods;
import com.badbones69.crazyenvoys.paper.api.FileManager.Files;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FlareSettings {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final Methods methods = plugin.getMethods();
    
    private ItemBuilder flareItemBuilder;
    
    public void load() {
        FileConfiguration config = Files.CONFIG.getFile();
        flareItemBuilder = new ItemBuilder()
        .setMaterial(config.getString("Settings.Flares.Item"))
        .setName(config.getString("Settings.Flares.Name"))
        .setLore(config.getStringList("Settings.Flares.Lore"));
    }
    
    public ItemStack getFlare() {
        return getFlare(1);
    }
    
    public ItemStack getFlare(int amount) {
        return flareItemBuilder.setAmount(amount).build();
    }
    
    public boolean isFlare(ItemStack item) {
        return getFlare().isSimilar(item);
    }
    
    public void giveFlare(Player player) {
        giveFlare(player, 1);
    }
    
    public void giveFlare(Player player, int amount) {
        if (methods.isInvFull(player)) {
            player.getWorld().dropItem(player.getLocation(), getFlare(amount));
        } else {
            player.getInventory().addItem(getFlare(amount));
        }
    }
    
    public void takeFlare(Player player) {
        player.getInventory().removeItem(getFlare());
    }
}