package com.badbones69.crazyenvoys.paper.controllers;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.Methods;
import com.badbones69.crazyenvoys.paper.api.CrazyManager;
import com.badbones69.crazyenvoys.paper.api.enums.Messages;
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
import java.util.HashMap;

public class FlareControl implements Listener {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final CrazyManager crazyManager = plugin.getCrazyManager();
    private final Methods methods = plugin.getMethods();
    private final EnvoySettings envoySettings = plugin.getEnvoySettings();

    private final FlareSettings flareSettings = plugin.getFlareSettings();

    @EventHandler(ignoreCancelled = true)
    public void onFlareActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack flare = methods.getItemInHand(player);

            if (flare != null && flareSettings.isFlare(flare)) {
                e.setCancelled(true);

                if (player.hasPermission("envoy.flare.use")) {
                    if (crazyManager.isEnvoyActive()) {
                        Messages.ALREADY_STARTED.sendMessage(player);
                    } else {
                        int online = plugin.getServer().getOnlinePlayers().size();

                        if (envoySettings.isMinPlayersEnabled() && envoySettings.isMinFlareEnabled() && online < envoySettings.getMinPlayers()) {
                            HashMap<String, String> placeholder = new HashMap<>();
                            placeholder.put("%amount%", online + "");
                            placeholder.put("%Amount%", online + "");
                            Messages.NOT_ENOUGH_PLAYERS.sendMessage(player, placeholder);
                            return;
                        }

                        boolean toggle = false;

                        if (PluginSupport.WORLD_EDIT.isPluginEnabled() && PluginSupport.WORLD_GUARD.isPluginEnabled()) {
                            if (envoySettings.isWorldMessagesEnabled()) {
                                for (String region : envoySettings.getFlaresRegions()) {
                                    if (crazyManager.getWorldGuardPluginSupport().inRegion(region, player.getLocation())) toggle = true;
                                }
                            } else {
                                toggle = true;
                            }
                        } else {
                            toggle = true;
                        }

                        if (!toggle) {
                            Messages.NOT_IN_WORLD_GUARD_REGION.sendMessage(player);
                            return;
                        }

                        EnvoyStartEvent event = new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FLARE);
                        plugin.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled() && crazyManager.startEnvoyEvent()) {
                            Messages.USED_FLARE.sendMessage(player);

                            flareSettings.takeFlare(player);
                        }
                    }
                } else {
                    Messages.CANT_USE_FLARES.sendMessage(player);
                }
            }
        }
    }
}