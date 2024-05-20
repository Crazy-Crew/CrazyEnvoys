package com.badbones69.crazyenvoys.listeners;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyStartEvent;
import com.badbones69.crazyenvoys.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.config.impl.ConfigKeys;
import com.badbones69.crazyenvoys.platform.util.MiscUtils;
import com.ryderbelserion.vital.paper.enums.Support;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class FlareClickListener implements Listener {

    private final @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final @NotNull SettingsManager config = ConfigManager.getConfig();

    private final @NotNull CrazyManager crazyManager = this.plugin.getCrazyManager();

    private final @NotNull FlareSettings flareSettings = this.plugin.getFlareSettings();

    @EventHandler(ignoreCancelled = true)
    public void onFlareInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack flare = MiscUtils.getItemInHand(player);

            if (this.flareSettings.isFlare(flare)) {
                event.setCancelled(true);

                if (!player.hasPermission("crazyenvoys.flare.use")) {
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

                if (Support.worldedit.isEnabled() && Support.worldguard.isEnabled()) {
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