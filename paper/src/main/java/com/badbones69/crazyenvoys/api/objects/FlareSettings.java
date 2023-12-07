package com.badbones69.crazyenvoys.api.objects;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.common.config.types.Config;

public class FlareSettings {

    private final @NotNull CrazyEnvoys plugin = CrazyEnvoys.get();

    private final @NotNull Methods methods = this.plugin.getMethods();
    
    private ItemBuilder flareItemBuilder;
    
    public void load() {
        SettingsManager config = this.plugin.getCrazyHandler().getConfigManager().getConfig();
        this.flareItemBuilder = new ItemBuilder()
                .setMaterial(config.getProperty(Config.envoys_flare_item_type))
                .setName(config.getProperty(Config.envoys_flare_item_name))
                .setLore(config.getProperty(Config.envoys_flare_item_lore));
    }
    
    public ItemStack getFlare() {
        return getFlare(1);
    }
    
    public ItemStack getFlare(int amount) {
        return this.flareItemBuilder.setAmount(amount).build();
    }
    
    public boolean isFlare(ItemStack item) {
        return getFlare().isSimilar(item);
    }
    
    public void giveFlare(Player player) {
        giveFlare(player, 1);
    }
    
    public void giveFlare(Player player, int amount) {
        if (this.methods.isInvFull(player)) {
            player.getWorld().dropItem(player.getLocation(), getFlare(amount));
        } else {
            player.getInventory().addItem(getFlare(amount));
        }
    }
    
    public void takeFlare(Player player) {
        player.getInventory().removeItem(getFlare());
    }
}