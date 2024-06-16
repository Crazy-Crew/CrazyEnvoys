package com.badbones69.crazyenvoys.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.util.MsgUtils;
import com.ryderbelserion.vital.core.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.core.config.ConfigManager;
import us.crazycrew.crazyenvoys.core.config.types.ConfigKeys;
import us.crazycrew.crazyenvoys.core.config.types.MessageKeys;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Messages {

    ended(MessageKeys.envoy_ended),
    warning(MessageKeys.envoy_warning),
    started(MessageKeys.envoy_started, true),
    on_going(MessageKeys.hologram_on_going),
    not_running(MessageKeys.hologram_not_running),
    reloaded(MessageKeys.envoy_plugin_reloaded),
    time_left(MessageKeys.envoy_time_left),
    used_flare(MessageKeys.envoy_used_flare),
    give_flare(MessageKeys.envoy_give_flare),
    new_center(MessageKeys.envoy_new_center),
    not_online(MessageKeys.not_online),
    given_flare(MessageKeys.envoy_received_flare),
    force_start(MessageKeys.envoy_force_start),
    not_started(MessageKeys.envoy_not_started),
    envoys_remaining(MessageKeys.envoys_remaining),
    force_end(MessageKeys.envoy_force_ended),
    drops_page(MessageKeys.drops_page),
    drops_format(MessageKeys.drops_format),
    drops_available(MessageKeys.drops_available),
    drops_possibilities(MessageKeys.drops_possibilities),
    player_only(MessageKeys.player_only),
    not_a_number(MessageKeys.not_a_number),
    add_location(MessageKeys.envoy_add_location),
    remove_location(MessageKeys.envoy_remove_location),
    cooldown_left(MessageKeys.cooldown_left),
    countdown_in_progress(MessageKeys.countdown_in_progress),
    no_permission(MessageKeys.no_permission),
    no_claim_permission(MessageKeys.no_claim_permission),
    time_till_event(MessageKeys.envoy_time_till_event),
    cant_use_flares(MessageKeys.envoy_cant_use_flare),
    already_started(MessageKeys.envoy_already_started),
    enter_editor_mode(MessageKeys.enter_editor_mode),
    leave_editor_mode(MessageKeys.exit_editor_mode),
    editor_clear_locations(MessageKeys.envoy_clear_locations),
    editor_clear_failure(MessageKeys.envoy_clear_failure),
    not_enough_players(MessageKeys.not_enough_players),
    stop_ignoring_messages(MessageKeys.stop_ignoring_messages),
    start_ignoring_messages(MessageKeys.start_ignoring_messages),
    kicked_from_editor_mode(MessageKeys.envoy_kicked_from_editor_mode),
    not_in_world_guard_region(MessageKeys.not_in_world_guard_region),
    no_spawn_locations_found(MessageKeys.no_spawn_locations_found),
    command_not_found(MessageKeys.command_not_found),
    day(MessageKeys.time_placeholder_day),
    hour(MessageKeys.time_placeholder_hour),
    minute(MessageKeys.time_placeholder_minute),
    second(MessageKeys.time_placeholder_second),
    envoy_locations(MessageKeys.envoy_locations),
    location_format(MessageKeys.location_format),
    help(MessageKeys.help, true);

    private Property<String> property;

    private Property<List<String>> listProperty;

    private boolean isList = false;

    private String message;

    /**
     * Used for strings
     *
     * @param property the property
     */
    Messages(Property<String> property) {
        this.property = property;
    }

    /**
     * Used for string lists
     *
     * @param listProperty the list property
     * @param isList Defines if it's a list or not.
     */
    Messages(Property<List<String>> listProperty, boolean isList) {
        this.listProperty = listProperty;

        this.isList = isList;
    }
    @NotNull
    private final CrazyEnvoys plugin = CrazyEnvoys.get();
    @NotNull
    private final SettingsManager messages = ConfigManager.getMessages();
    @NotNull
    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    @NotNull
    private List<String> getPropertyList(Property<List<String>> properties) {
        return this.messages.getProperty(properties);
    }

    @NotNull
    private String getProperty(Property<String> property) {
        return this.messages.getProperty(property);
    }

    private boolean isList() {
        return this.isList;
    }

    public String getString() {
        return getMessage().toString();
    }

    public Messages getMessage() {
        return getMessage(new HashMap<>());
    }

    public Messages getMessage(String placeholder, String replacement) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);

        return getMessage(placeholders);
    }

    public Messages getMessage(Map<String, String> placeholders) {
        // Get the string first.
        String message;

        if (isList()) {
            message = StringUtil.convertList(getPropertyList(this.listProperty));
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
            message = StringUtil.convertList(getPropertyList(this.listProperty));
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
            message = StringUtil.convertList(getPropertyList(this.listProperty));
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
        String message = getMessage(placeholder).asString();

        if (message.isEmpty() || message.isBlank()) {
            return;
        }

        player.sendMessage(message);
    }

    public void sendMessage(CommandSender sender) {
        sendMessage(sender, new HashMap<>());
    }

    public void sendMessage(CommandSender sender, Map<String, String> placeholder) {
        String message = getMessage(placeholder).asString();

        if (message.isEmpty() || message.isBlank()) {
            return;
        }

        sender.sendMessage(message);
    }

    public void broadcastMessage(boolean ignore) {
        broadcastMessage(ignore, new HashMap<>());
    }

    public void broadcastMessage(boolean ignore, Map<String, String> placeholder) {
        // Send in console because we should lol.
        sendMessage(this.plugin.getServer().getConsoleSender(), placeholder);

        if (ConfigManager.getConfig().getProperty(ConfigKeys.envoys_world_messages)) {
            for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                for (String world : ConfigManager.getConfig().getProperty(ConfigKeys.envoys_allowed_worlds)) {
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
        return MsgUtils.color(this.message.replaceAll("\\{prefix}", ConfigManager.getConfig().getProperty(ConfigKeys.command_prefix)));
    }

    public List<String> toListString() {
        ArrayList<String> components = new ArrayList<>();

        getPropertyList(this.listProperty).forEach(line -> components.add(MsgUtils.color(line)));

        return components;
    }
}