package com.badbones69.crazyenvoys.controllers;

import com.badbones69.crazyenvoys.multisupport.ServerProtocol;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import java.util.ArrayList;
import java.util.List;

public class FireworkDamageAPI implements Listener {
    
    private static final List<Entity> fireworks = new ArrayList<>();
    
    /**
     * @return All the active fireworks.
     */
    public static List<Entity> getFireworks() {
        return fireworks;
    }
    
    /**
     * @param firework The firework you want to add.
     */
    public static void addFirework(Entity firework) {
        if (ServerProtocol.isNewer(ServerProtocol.v1_10_R1)) fireworks.add(firework);
    }
    
    /**
     * @param firework The firework you are removing.
     */
    public static void removeFirework(Entity firework) {
        fireworks.remove(firework);
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        for (Entity en : e.getEntity().getNearbyEntities(5, 5, 5)) {
            if (getFireworks().contains(en)) e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onFireworkExplode(FireworkExplodeEvent e) {
        final Entity firework = e.getEntity();

        if (getFireworks().contains(firework)) removeFirework(firework);
    }
    
}