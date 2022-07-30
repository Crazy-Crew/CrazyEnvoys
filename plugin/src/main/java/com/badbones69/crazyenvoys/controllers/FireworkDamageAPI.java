package com.badbones69.crazyenvoys.controllers;

import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.multisupport.ServerProtocol;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class FireworkDamageAPI implements Listener {

    public static CrazyManager crazyManager = CrazyManager.getInstance();

    /**
     * @param firework The firework you want to add.
     */
    public static void addFirework(Entity firework) {
        if (ServerProtocol.isNewer(ServerProtocol.v1_10_R1)) firework.setMetadata("nodamage", new FixedMetadataValue(crazyManager.getPlugin(), true));
    }

    // Ryder Start
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Firework) {
            Firework fw = (Firework) e.getDamager();
            if (fw.hasMetadata("nodamage")) e.setCancelled(true);
        }
    }
    // Ryder End
}