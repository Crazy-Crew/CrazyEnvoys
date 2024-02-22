package com.badbones69.crazyenvoys.listeners;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyStartEvent;
import com.badbones69.crazyenvoys.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.support.libraries.PluginSupport;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.common.config.ConfigManager;
import us.crazycrew.crazyenvoys.common.config.types.ConfigKeys;
import us.crazycrew.crazyenvoys.api.plugin.CrazyHandler;
import java.util.HashMap;
import java.util.Map;

public class FlareClickListener implements Listener {

    @NotNull
    private final CrazyEnvoys plugin = CrazyEnvoys.get();
    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    @NotNull
    private final ConfigManager configManager = this.crazyHandler.getConfigManager();
    @NotNull
    private final SettingsManager config = this.configManager.getConfig();

    @NotNull
    private final CrazyManager crazyManager = this.plugin.getCrazyManager();
    @NotNull
    private final Methods methods = this.plugin.getMethods();

    @NotNull
    private final FlareSettings flareSettings = this.plugin.getFlareSettings();

    @EventHandler(ignoreCancelled = true)
    public void onFlareInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack flare = this.methods.getItemInHand(player);

            if (flare != null && this.flareSettings.isFlare(flare)) {
                event.setCancelled(true);

                if (!player.hasPermission("envoy.flare.use")) {
                    Messages.cant_use_flares.sendMessage(player);
                    return;
                }

                if (this.crazyManager.isEnvoyActive()) {
                    Messages.already_started.sendMessage(player);
                    return;
                }

                int online = this.plugin.getServer().getOnlinePlayers().size();

                if (this.config.getProperty(ConfigKeys.envoys_flare_minimum_players_toggle) && online < this.config.getProperty(ConfigKeys.envoys_flare_minimum_players_amount)) {
                    Map<String, String> placeholder = new HashMap<>();
                    placeholder.put("{amount}", String.valueOf(online));
                    Messages.not_enough_players.sendMessage(player, placeholder);
                    return;
                }

                boolean toggle = false;

                if (PluginSupport.WORLD_EDIT.isPluginEnabled() && PluginSupport.WORLD_GUARD.isPluginEnabled()) {
                    if (this.config.getProperty(ConfigKeys.envoys_world_messages)) {
                        for (String region : this.config.getProperty(ConfigKeys.envoys_flare_world_guard_regions)) {
                            if (this.crazyManager.getWorldGuardPluginSupport().inRegion(region, player.getLocation())) toggle = true;
                        }
                    } else {
                        toggle = true;
                    }
                } else {
                    toggle = true;
                }

                if (!toggle) {
                    Messages.not_in_world_guard_region.sendMessage(player);
                    return;
                }

                EnvoyStartEvent envoyStartEvent = new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FLARE);
                this.plugin.getServer().getPluginManager().callEvent(envoyStartEvent);

                if (!envoyStartEvent.isCancelled() && this.crazyManager.startEnvoyEvent()) {
                    Messages.used_flare.sendMessage(player);

                    this.flareSettings.takeFlare(player);
                }
            }
        }
    }
}