package com.badbones69.crazyenvoys.api.objects;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.enums.PersistentKeys;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import us.crazycrew.crazyenvoys.core.config.ConfigManager;
import us.crazycrew.crazyenvoys.core.config.types.ConfigKeys;

public class FlareSettings {
    
    private ItemBuilder flareItemBuilder;
    
    public void load() {
        SettingsManager config = ConfigManager.getConfig();

        this.flareItemBuilder = new ItemBuilder()
                .setMaterial(config.getProperty(ConfigKeys.envoys_flare_item_type))
                .setGlow(config.getProperty(ConfigKeys.envoys_flare_item_glowing))
                .setName(config.getProperty(ConfigKeys.envoys_flare_item_name))
                .setLore(config.getProperty(ConfigKeys.envoys_flare_item_lore));
    }
    
    public ItemStack getFlare() {
        return getFlare(1);
    }
    
    public ItemStack getFlare(int amount) {
        ItemStack itemStack = this.flareItemBuilder.setAmount(amount).build();

        itemStack.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(PersistentKeys.envoy_flare.getNamespacedKey(), PersistentDataType.BOOLEAN, true));

        return itemStack;
    }
    
    public boolean isFlare(ItemStack item) {
        if (!item.hasItemMeta()) return false;

        return item.getItemMeta().getPersistentDataContainer().has(PersistentKeys.envoy_flare.getNamespacedKey());
    }
    
    public void giveFlare(Player player) {
        giveFlare(player, 1);
    }
    
    public void giveFlare(Player player, int amount) {
        if (Methods.isInvFull(player)) {
            player.getWorld().dropItem(player.getLocation(), getFlare(amount));
        } else {
            Methods.addItem(player, getFlare(amount));
        }
    }
    
    public void takeFlare(Player player) {
        player.getInventory().removeItem(getFlare());
    }
}