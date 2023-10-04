package com.badbones69.crazyenvoys.paper.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.api.CrazyManager;
import com.ryderbelserion.cluster.api.utils.MiscUtils;
import com.ryderbelserion.cluster.bukkit.utils.LegacyUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.common.config.ConfigManager;
import us.crazycrew.crazyenvoys.common.config.types.Config;
import us.crazycrew.crazyenvoys.common.config.types.Messages;
import us.crazycrew.crazyenvoys.common.config.types.PluginConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Translation {

    left(Messages.envoy_time_left),
    ended(Messages.envoy_ended),
    warning(Messages.envoy_warning),
    started(Messages.envoy_started),
    on_going(Messages.hologram_on_going),
    not_running(Messages.hologram_not_running),
    reloaded(Messages.envoy_plugin_reloaded),
    time_left(Messages.envoy_time_left),
    used_flare(Messages.envoy_used_flare),
    give_flare(Messages.envoy_give_flare),
    new_center(Messages.envoy_new_center),
    not_online(Messages.not_online),
    given_flare(Messages.envoy_received_flare),
    force_start(Messages.envoy_force_start),
    not_started(Messages.envoy_not_started),
    envoys_remaining(Messages.envoys_remaining),
    force_end(Messages.envoy_force_ended),
    drops_page(Messages.drops_page),
    drops_format(Messages.drops_format),
    drops_available(Messages.drops_available),
    drops_possibilities(Messages.drops_possibilities),
    player_only(Messages.player_only),
    not_a_number(Messages.not_a_number),
    add_location(Messages.envoy_add_location),
    remove_location(Messages.envoy_remove_location),
    cooldown_left(Messages.cooldown_left),
    countdown_in_progress(Messages.countdown_in_progress),
    no_permission(Messages.no_permission),
    no_claim_permission(Messages.no_claim_permission),
    time_till_event(Messages.envoy_time_till_event),
    cant_use_flares(Messages.envoy_cant_use_flare),
    already_started(Messages.envoy_already_started),
    enter_editor_mode(Messages.enter_editor_mode),
    leave_editor_mode(Messages.exit_editor_mode),
    editor_clear_locations(Messages.envoy_clear_locations),
    editor_clear_failure(Messages.envoy_clear_failure),
    not_enough_players(Messages.not_enough_players),
    stop_ignoring_messages(Messages.stop_ignoring_messages),
    start_ignoring_messages(Messages.start_ignoring_messages),
    kicked_from_editor_mode(Messages.envoy_kicked_from_editor_mode),
    not_in_world_guard_region(Messages.not_in_world_guard_region),
    no_spawn_locations_found(Messages.no_spawn_locations_found),
    command_not_found(Messages.command_not_found),
    day(Messages.time_placeholder_day),
    hour(Messages.time_placeholder_hour),
    minute(Messages.time_placeholder_minute),
    second(Messages.time_placeholder_second),
    envoy_locations(Messages.envoy_locations),
    location_format(Messages.location_format),
    help(Messages.help, true);

    private Property<String> property;

    private Property<List<String>> listProperty;

    private boolean isList = false;

    private String message;

    /**
     * Used for strings
     *
     * @param property the property
     */
    Translation(Property<String> property) {
        this.property = property;
    }

    /**
     * Used for string lists
     *
     * @param listProperty the list property
     * @param isList Defines if it's a list or not.
     */
    Translation(Property<List<String>> listProperty, boolean isList) {
        this.listProperty = listProperty;

        this.isList = isList;
    }

    private final @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);
    private final @NotNull ConfigManager configManager = this.plugin.getCrazyHandler().getConfigManager();
    private final @NotNull SettingsManager messages = this.configManager.getMessages();
    private final @NotNull CrazyManager crazyManager = this.plugin.getCrazyManager();

    private boolean isList() {
        return this.isList;
    }

    private @NotNull List<String> getPropertyList(Property<List<String>> properties) {
        return this.messages.getProperty(properties);
    }

    private @NotNull String getProperty(Property<String> property) {
        return this.messages.getProperty(property);
    }

    public String getString() {
        return getMessage().toString();
    }

    public Translation getMessage() {
        return getMessage(new HashMap<>());
    }

    public Translation getMessage(String placeholder, String replacement) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);

        return getMessage(placeholders);
    }

    public Translation getMessage(Map<String, String> placeholders) {
        // Get the string first.
        String message;

        if (isList()) {
            message = MiscUtils.convertList(getPropertyList(this.listProperty));
        } else {
            message = getProperty(this.property);
        }

        if (!placeholders.isEmpty()) {
            for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                message = message.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
            }
        }

        this.message = message;

        return this;
    }

    public String getStringMessage(Map<String, String> placeholders) {
        // Get the string first.
        String message;

        if (isList()) {
            message = MiscUtils.convertList(getPropertyList(this.listProperty));
        } else {
            message = getProperty(this.property);
        }

        if (!placeholders.isEmpty()) {
            for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                message = message.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
            }
        }

        this.message = message;

        return this.message;
    }

    public String getStringMessage() {
        // Get the string first.
        String message;

        if (isList()) {
            message = MiscUtils.convertList(getPropertyList(this.listProperty));
        } else {
            message = getProperty(this.property);
        }

        this.message = message;

        return this.message;
    }

    public void sendMessage(Player player) {
        sendMessage(player, new HashMap<>());
    }

    public void sendMessage(Player player, Map<String, String> placeholder) {
        player.sendMessage(getMessage(placeholder).asString());
    }

    public void sendMessage(CommandSender sender) {
        sendMessage(sender, new HashMap<>());
    }

    public void sendMessage(CommandSender sender, Map<String, String> placeholder) {
        sender.sendMessage(getMessage(placeholder).asString());
    }

    public void broadcastMessage(boolean ignore) {
        broadcastMessage(ignore, new HashMap<>());
    }

    public void broadcastMessage(boolean ignore, Map<String, String> placeholder) {
        if (this.plugin.getCrazyHandler().getConfigManager().getConfig().getProperty(Config.envoys_world_messages)) {
            for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                for (String world : this.plugin.getCrazyHandler().getConfigManager().getConfig().getProperty(Config.envoys_allowed_worlds)) {
                    if (player.getWorld().getName().equalsIgnoreCase(world)) {
                        if (ignore) {
                            if (!this.crazyManager.isIgnoringMessages(player.getUniqueId())) sendMessage(player, placeholder);
                        } else {
                            sendMessage(player, placeholder);
                        }
                    }
                }
            }
        } else {
            for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                if (ignore) {
                    if (!this.crazyManager.isIgnoringMessages(player.getUniqueId())) sendMessage(player, placeholder);
                } else {
                    sendMessage(player, placeholder);
                }
            }
        }
    }

    public String asString() {
        return LegacyUtils.color(this.message.replaceAll("\\{prefix}", this.configManager.getPluginConfig().getProperty(PluginConfig.command_prefix)));
    }

    public List<String> toListString() {
        ArrayList<String> components = new ArrayList<>();

        getPropertyList(this.listProperty).forEach(line -> components.add(LegacyUtils.color(line)));

        return components;
    }
}