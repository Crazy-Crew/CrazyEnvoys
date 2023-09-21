package com.badbones69.crazyenvoys.paper.controllers;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.Methods;
import com.badbones69.crazyenvoys.paper.api.CrazyManager;
import com.badbones69.crazyenvoys.paper.api.enums.Translation;
import com.badbones69.crazyenvoys.paper.api.events.EnvoyStartEvent;
import com.badbones69.crazyenvoys.paper.api.objects.EnvoySettings;
import com.badbones69.crazyenvoys.paper.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.paper.support.libraries.PluginSupport;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;

public class FlareControl implements Listener {

    private final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();
    private final Methods methods = this.plugin.getMethods();
    private final EnvoySettings envoySettings = this.plugin.getEnvoySettings();

    private final FlareSettings flareSettings = this.plugin.getFlareSettings();

    @EventHandler(ignoreCancelled = true)
    public void onFlareActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getAction() != Action.RIGHT_CLICK_AIR || e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack flare = this.methods.getItemInHand(player);

        if (this.flareSettings.isFlare(flare)) return;

        if (flare == null) return;

        e.setCancelled(true);

        if (!player.hasPermission("envoy.flare.use")) {
            Translation.cant_use_flares.sendMessage(player);
            return;
        }

        if (this.crazyManager.isEnvoyActive()) {
            Translation.already_started.sendMessage(player);
            return;
        }

        int online = this.plugin.getServer().getOnlinePlayers().size();

        if (this.envoySettings.isMinPlayersEnabled() && this.envoySettings.isMinFlareEnabled() && online < this.envoySettings.getMinPlayers()) {
            HashMap<String, String> placeholder = new HashMap<>();
            placeholder.put("%amount%", String.valueOf(online));
            placeholder.put("%Amount%", String.valueOf(online));
            Translation.not_enough_players.sendMessage(player, placeholder);
            return;
        }

        boolean toggle = false;

        if (PluginSupport.WORLD_EDIT.isPluginEnabled() && PluginSupport.WORLD_GUARD.isPluginEnabled()) {
            if (this.envoySettings.isWorldMessagesEnabled()) {
                for (String region : this.envoySettings.getFlaresRegions()) {
                    if (this.crazyManager.getWorldGuardPluginSupport().inRegion(region, player.getLocation()))
                        toggle = true;
                }
            } else {
                toggle = true;
            }
        } else {
            toggle = true;
        }

        if (!toggle) {
            Translation.not_in_world_guard_region.sendMessage(player);
            return;
        }

        EnvoyStartEvent event = new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FLARE);
        this.plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled() && this.crazyManager.startEnvoyEvent()) {
            Translation.used_flare.sendMessage(player);

            this.flareSettings.takeFlare(player);
        }
    }
}