package com.badbones69.crazyenvoys.paper.listeners;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.Methods;
import com.badbones69.crazyenvoys.paper.api.CrazyManager;
import com.badbones69.crazyenvoys.paper.api.enums.Translation;
import com.badbones69.crazyenvoys.paper.api.events.EnvoyStartEvent;
import com.badbones69.crazyenvoys.paper.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.paper.support.libraries.PluginSupport;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.common.config.ConfigManager;
import us.crazycrew.crazyenvoys.common.config.types.Config;
import us.crazycrew.crazyenvoys.paper.api.plugin.CrazyHandler;
import java.util.HashMap;

public class FlareClickListener implements Listener {

    private final @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull ConfigManager configManager = this.crazyHandler.getConfigManager();
    private final @NotNull SettingsManager config = this.configManager.getConfig();

    private final @NotNull CrazyManager crazyManager = this.plugin.getCrazyManager();
    private final @NotNull Methods methods = this.plugin.getMethods();

    private final @NotNull FlareSettings flareSettings = this.plugin.getFlareSettings();

    @EventHandler(ignoreCancelled = true)
    public void onFlareInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack flare = this.methods.getItemInHand(player);

        if (this.flareSettings.isFlare(flare)) return;

        if (flare == null) return;

        event.setCancelled(true);

        if (!player.hasPermission("envoy.flare.use")) {
            Translation.cant_use_flares.sendMessage(player);
            return;
        }

        if (this.crazyManager.isEnvoyActive()) {
            Translation.already_started.sendMessage(player);
            return;
        }

        int online = this.plugin.getServer().getOnlinePlayers().size();

        if (this.config.getProperty(Config.envoys_flare_minimum_players_toggle) && online < this.config.getProperty(Config.envoys_flare_minimum_players_amount)) {
            HashMap<String, String> placeholder = new HashMap<>();
            placeholder.put("{amount}", String.valueOf(online));
            Translation.not_enough_players.sendMessage(player, placeholder);
            return;
        }

        boolean toggle = false;

        if (PluginSupport.WORLD_EDIT.isPluginEnabled() && PluginSupport.WORLD_GUARD.isPluginEnabled()) {
            if (this.config.getProperty(Config.envoys_world_messages)) {
                for (String region : this.config.getProperty(Config.envoys_flare_world_guard_regions)) {
                    if (this.crazyManager.getWorldGuardPluginSupport().inRegion(region, player.getLocation())) toggle = true;
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

        EnvoyStartEvent envoyStartEvent = new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FLARE);
        this.plugin.getServer().getPluginManager().callEvent(envoyStartEvent);

        if (!envoyStartEvent.isCancelled() && this.crazyManager.startEnvoyEvent()) {
            Translation.used_flare.sendMessage(player);

            this.flareSettings.takeFlare(player);
        }
    }
}