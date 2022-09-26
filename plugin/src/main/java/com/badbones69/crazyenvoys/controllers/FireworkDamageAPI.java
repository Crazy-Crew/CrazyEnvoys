package com.badbones69.crazyenvoys.controllers;

import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.multisupport.ServerProtocol;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class FireworkDamageAPI implements Listener {

    public static CrazyManager crazyManager = CrazyManager.getInstance();

    static NamespacedKey noDamage = new NamespacedKey(crazyManager.getPlugin(), "no-damage");

    /**
     * @param firework The firework you want to add.
     */
    public static void addFirework(Entity firework) {
        if (ServerProtocol.isNewer(ServerProtocol.v1_10_R1)) {
            PersistentDataContainer container = firework.getPersistentDataContainer();

            container.set(noDamage, PersistentDataType.STRING, "no-damage");
        }
    }

    // Ryder Start
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Firework) {
            Firework fw = (Firework) e.getDamager();

            PersistentDataContainer container = fw.getPersistentDataContainer();

            if (container.has(noDamage, PersistentDataType.STRING)) e.setCancelled(true);
        }
    }
    // Ryder End
}