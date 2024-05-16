package com.badbones69.crazyenvoys.api.objects;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.config.impl.ConfigKeys;
import com.badbones69.crazyenvoys.platform.util.MiscUtil;
import com.ryderbelserion.vital.util.builders.items.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

//todo() add papi support and other shit
public class FlareSettings {
    
    private ItemBuilder flareItemBuilder;
    
    public void load() {
        SettingsManager config = ConfigManager.getConfig();

        this.flareItemBuilder = new ItemBuilder()
                .withType(config.getProperty(ConfigKeys.envoys_flare_item_type))
                .setDisplayName(config.getProperty(ConfigKeys.envoys_flare_item_name))
                .setDisplayLore(config.getProperty(ConfigKeys.envoys_flare_item_lore));
    }
    
    public ItemStack getFlare() {
        return getFlare(1);
    }
    
    public ItemStack getFlare(int amount) {
        return this.flareItemBuilder.setAmount(amount).getStack();
    }

    //todo() switch this to a pdc check.
    public boolean isFlare(ItemStack item) {
        return getFlare().isSimilar(item);
    }
    
    public void giveFlare(Player player) {
        giveFlare(player, 1);
    }
    
    public void giveFlare(Player player, int amount) {
        if (MiscUtil.isInvFull(player)) {
            player.getWorld().dropItem(player.getLocation(), getFlare(amount));
        } else {
            player.getInventory().addItem(getFlare(amount));
        }
    }
    
    public void takeFlare(Player player) {
        player.getInventory().removeItem(getFlare());
    }
}