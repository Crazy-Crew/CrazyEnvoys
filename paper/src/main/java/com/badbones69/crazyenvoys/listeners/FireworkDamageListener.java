package com.badbones69.crazyenvoys.listeners;

import com.badbones69.crazyenvoys.api.enums.PersistentKeys;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class FireworkDamageListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onFireworkDamage(EntityDamageEvent event) {
        Entity directEntity = event.getDamageSource().getDirectEntity();

        if (directEntity instanceof Firework firework) {
            PersistentDataContainer container = firework.getPersistentDataContainer();

            if (container.has(PersistentKeys.no_firework_damage.getNamespacedKey())) {
                event.setCancelled(true);
            }
        }
    }
}