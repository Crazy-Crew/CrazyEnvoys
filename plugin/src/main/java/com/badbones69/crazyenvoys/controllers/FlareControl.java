package com.badbones69.crazyenvoys.controllers;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyStartEvent;
import com.badbones69.crazyenvoys.api.objects.EnvoySettings;
import com.badbones69.crazyenvoys.api.objects.Flare;
import com.badbones69.crazyenvoys.multisupport.Support;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;

public class FlareControl implements Listener {
    
    private final CrazyManager crazyManager = CrazyManager.getInstance();
    private final EnvoySettings envoySettings = EnvoySettings.getInstance();
    
    @EventHandler
    public void onFlareActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack flare = Methods.getItemInHand(player);

            if (flare != null && Flare.isFlare(flare)) {
                e.setCancelled(true);

                if (player.hasPermission("crazyManager.flare.use")) {
                    if (crazyManager.isEnvoyActive()) {
                        Messages.ALREADY_STARTED.sendMessage(player);
                    } else {
                        int online = crazyManager.getPlugin().getServer().getOnlinePlayers().size();

                        if (envoySettings.isMinPlayersEnabled() && envoySettings.isMinFlareEnabled() && online < envoySettings.getMinPlayers()) {
                            HashMap<String, String> placeholder = new HashMap<>();
                            placeholder.put("%amount%", online + "");
                            placeholder.put("%Amount%", online + "");
                            Messages.NOT_ENOUGH_PLAYERS.sendMessage(player, placeholder);
                            return;
                        }

                        boolean toggle = false;

                        if (Support.WORLD_EDIT.isPluginLoaded() && Support.WORLD_GUARD.isPluginLoaded()) {
                            if (envoySettings.isWorldMessagesEnabled()) {
                                for (String region : envoySettings.getFlaresRegions()) {
                                    if (crazyManager.getWorldGuardSupport().inRegion(region, player.getLocation())) {
                                        toggle = true;
                                    }
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
                        crazyManager.getPlugin().getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled() && crazyManager.startEnvoyEvent()) {
                            Messages.USED_FLARE.sendMessage(player);
                            Flare.takeFlare(player);
                        }
                    }
                } else {
                    Messages.CANT_USE_FLARES.sendMessage(player);
                }
            }
        }
    }
    
}