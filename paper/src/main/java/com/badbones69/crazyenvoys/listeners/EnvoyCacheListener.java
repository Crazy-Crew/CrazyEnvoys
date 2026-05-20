package com.badbones69.crazyenvoys.listeners;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.PaperEnvoysPlugin;
import com.badbones69.crazyenvoys.api.registry.PaperUserRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EnvoyCacheListener implements Listener {

    private final CrazyEnvoys plugin = CrazyEnvoys.get();

    private final PaperEnvoysPlugin platform = this.plugin.getPlugin();

    private final PaperUserRegistry userRegistry = this.platform.getUserRegistry();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        this.userRegistry.addUser(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        this.userRegistry.removeUser(player.getUniqueId());
    }
}