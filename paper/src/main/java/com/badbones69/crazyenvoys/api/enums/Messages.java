package com.badbones69.crazyenvoys.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.util.MsgUtils;
import com.ryderbelserion.fusion.core.api.support.ModSupport;
import com.ryderbelserion.fusion.paper.FusionPaper;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.config.types.ConfigKeys;
import com.badbones69.crazyenvoys.config.types.MessageKeys;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Messages {

    ended(MessageKeys.envoy_ended, true),
    warning(MessageKeys.envoy_warning),
    started(MessageKeys.envoy_started, true),
    started_player(MessageKeys.envoy_started_player, true),
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
    envoys_remaining(MessageKeys.envoys_remaining, true),
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
    time_till_event(MessageKeys.envoy_time_till_event, true),
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

    private @NotNull final CrazyEnvoys plugin = CrazyEnvoys.get();
    private @NotNull final SettingsManager messages = ConfigManager.getMessages();
    private @NotNull final SettingsManager config = ConfigManager.getConfig();
    private @NotNull final CrazyManager crazyManager = this.plugin.getCrazyManager();

    private final FusionPaper fusion = this.plugin.getFusion();

    public String getString() {
        return this.messages.getProperty(this.property);
    }

    public List<String> getList() {
        return this.messages.getProperty(this.listProperty);
    }

    private boolean isList() {
        return this.isList;
    }

    public String getMessage() {
        return getMessage(null, new HashMap<>());
    }

    public String getMessage(final String placeholder, final String replacement) {
        return getMessage(null, placeholder, replacement);
    }

    public String getMessage(final Map<String, String> placeholders) {
        return getMessage(null, placeholders);
    }

    public String getMessage(@Nullable final CommandSender sender) {
        return getMessage(sender, new HashMap<>());
    }

    public String getMessage(@Nullable final CommandSender sender, @NotNull final String placeholder, @NotNull final String replacement) {
        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put(placeholder, replacement);

        return getMessage(sender, placeholders);
    }

    public String getMessage(@Nullable final CommandSender sender, @NotNull final Map<String, String> placeholders) {
        return parse(sender, placeholders).replaceAll("\\{prefix}", MsgUtils.getPrefix());
    }

    public void sendMessage(final CommandSender sender, final String placeholder, final String replacement) {
        sender.sendMessage(getMessage(sender, placeholder, replacement));
    }

    public void sendMessage(final CommandSender sender, final Map<String, String> placeholders) {
        sender.sendMessage(getMessage(sender, placeholders));
    }

    public void sendMessage(final CommandSender sender) {
        sender.sendMessage(getMessage(sender));
    }

    private @NotNull String parse(@Nullable final CommandSender sender, @NotNull final Map<String, String> placeholders) {
        String message;

        if (isList()) {
            message = StringUtils.chomp(this.fusion.getStringUtils().toString(getList()));
        } else {
            message = getString();
        }

        if (sender != null) {
            if (sender instanceof Player player) {
                if (this.fusion.isModReady(ModSupport.placeholder_api)) {
                    message = PlaceholderAPI.setPlaceholders(player, message);
                }
            }
        }

        if (!placeholders.isEmpty()) {
            for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                message = message.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
            }
        }

        return MsgUtils.color(message);
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
}