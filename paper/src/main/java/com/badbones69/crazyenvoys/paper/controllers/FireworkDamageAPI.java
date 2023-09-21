package com.badbones69.crazyenvoys.paper.controllers;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class FireworkDamageAPI implements Listener {

    private final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    NamespacedKey noDamage = new NamespacedKey(this.plugin, "no-damage");

    /**
     * @param firework The firework you want to add.
     */
    public void addFirework(Entity firework) {
        PersistentDataContainer container = firework.getPersistentDataContainer();
        container.set(this.noDamage, PersistentDataType.STRING, "no-damage");
    }

    // Ryder Start
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Firework firework) {

            PersistentDataContainer container = firework.getPersistentDataContainer();

            if (container.has(this.noDamage, PersistentDataType.STRING)) e.setCancelled(true);
        }
    }
    // Ryder End
}