package com.badbones69.crazyenvoys.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class EnvoyWorldListener implements Listener {

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        //final World world = event.getWorld();

        //new EnvoyWorldLoadEvent(new EnvoyWorld(world.getName()));
    }
}