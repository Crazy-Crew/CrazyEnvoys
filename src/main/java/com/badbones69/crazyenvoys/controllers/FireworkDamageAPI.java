package com.badbones69.crazyenvoys.controllers;

import com.badbones69.crazyenvoys.api.CrazyManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class FireworkDamageAPI implements Listener {

    public static CrazyManager crazyManager = CrazyManager.getInstance();

    /**
     * @param firework The firework you want to add.
     */
    public static void addFirework(Entity firework) {
        firework.setMetadata("nodamage", new FixedMetadataValue(crazyManager.getPlugin(), true));
    }

    // Ryder Start
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Firework fw) if (fw.hasMetadata("nodamage")) e.setCancelled(true);
    }
    // Ryder End
}