package com.badbones69.crazyenvoys.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.config.impl.ConfigKeys;
import com.badbones69.crazyenvoys.config.impl.MessageKeys;
import com.badbones69.crazyenvoys.config.impl.locale.ErrorKeys;
import com.badbones69.crazyenvoys.config.impl.locale.MiscKeys;
import com.badbones69.crazyenvoys.config.impl.locale.PlayerKeys;
import com.ryderbelserion.vital.core.util.StringUtil;
import com.ryderbelserion.vital.paper.enums.Support;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Messages {

    feature_disabled(MiscKeys.feature_disabled),
    cannot_be_empty(ErrorKeys.cannot_be_empty),
    unknown_command(MiscKeys.unknown_command),
    internal_error(ErrorKeys.internal_error),
    cannot_be_air(ErrorKeys.cannot_be_air),
    correct_usage(MiscKeys.correct_usage),
    reward_error(ErrorKeys.reward_error),

    must_be_console_sender(PlayerKeys.must_be_console_sender),
    no_claim_permission(PlayerKeys.no_claim_permission),
    inventory_not_empty(PlayerKeys.inventory_not_empty),
    must_be_a_player(PlayerKeys.must_be_a_player),
    no_permission(PlayerKeys.no_permission),
    same_player(PlayerKeys.same_player),

    command_not_found(MiscKeys.unknown_command),
    not_a_number(MiscKeys.not_a_number),
    reloaded(MiscKeys.plugin_reloaded),
    player_only(MiscKeys.player_only),
    not_online(MiscKeys.not_online),
    help(MiscKeys.help, true),

    kicked_from_editor_mode(MessageKeys.envoy_kicked_from_editor_mode),
    not_in_world_guard_region(MessageKeys.not_in_world_guard_region),
    no_spawn_locations_found(MessageKeys.no_spawn_locations_found),
    start_ignoring_messages(MessageKeys.start_ignoring_messages),
    stop_ignoring_messages(MessageKeys.stop_ignoring_messages),
    editor_clear_locations(MessageKeys.envoy_clear_locations),
    countdown_in_progress(MessageKeys.countdown_in_progress),
    editor_clear_failure(MessageKeys.envoy_clear_failure),
    drops_possibilities(MessageKeys.drops_possibilities),
    not_enough_players(MessageKeys.not_enough_players),

    remove_location(MessageKeys.envoy_remove_location),
    enter_editor_mode(MessageKeys.enter_editor_mode),
    leave_editor_mode(MessageKeys.exit_editor_mode),

    not_running(MessageKeys.hologram_not_running),
    on_going(MessageKeys.hologram_on_going),

    location_format(MessageKeys.location_format),
    drops_available(MessageKeys.drops_available),
    add_location(MessageKeys.envoy_add_location),
    envoy_locations(MessageKeys.envoy_locations),

    minute(MessageKeys.time_placeholder_minute),
    second(MessageKeys.time_placeholder_second),
    hour(MessageKeys.time_placeholder_hour),
    day(MessageKeys.time_placeholder_day),

    force_start(MessageKeys.envoy_force_start),
    not_started(MessageKeys.envoy_not_started),
    force_end(MessageKeys.envoy_force_ended),

    cant_use_flares(MessageKeys.envoy_cant_use_flare),
    given_flare(MessageKeys.envoy_received_flare),
    used_flare(MessageKeys.envoy_used_flare),
    give_flare(MessageKeys.envoy_give_flare),

    new_center(MessageKeys.envoy_new_center),

    drops_page(MessageKeys.drops_page),
    drops_format(MessageKeys.drops_format),

    already_started(MessageKeys.envoy_already_started),
    time_till_event(MessageKeys.envoy_time_till_event),
    envoys_remaining(MessageKeys.envoys_remaining),
    started(MessageKeys.envoy_started, true),
    warning(MessageKeys.envoy_warning),
    ended(MessageKeys.envoy_ended),

    time_left(MessageKeys.envoy_time_left),
    cooldown_left(MessageKeys.cooldown_left);

    private Property<String> property;

    private Property<List<String>> properties;
    private boolean isList = false;

    Messages(@NotNull final Property<String> property) {
        this.property = property;
    }

    Messages(@NotNull final Property<List<String>> properties, final boolean isList) {
        this.properties = properties;
        this.isList = isList;
    }

    private final SettingsManager config = ConfigManager.getConfig();

    private boolean isList() {
        return this.isList;
    }

    public String getString() {
        return ConfigManager.getMessages().getProperty(this.property);
    }

    public List<String> getList() {
        return ConfigManager.getMessages().getProperty(this.properties);
    }

    public String getMessage() {
        return getMessage(null, new HashMap<>());
    }

    public String getMessage(@Nullable final CommandSender sender) {
        if (sender instanceof Player player) {
            return getMessage(player, new HashMap<>());
        }

        return getMessage(null, new HashMap<>());
    }

    public String getMessage(@NotNull final Map<String, String> placeholders) {
        return getMessage(null, placeholders);
    }

    public String getMessage(@NotNull final String placeholder, @NotNull final String replacement) {
        return getMessage(null, placeholder, replacement);
    }

    public String getMessage(@Nullable final CommandSender sender, @NotNull final String placeholder, @NotNull final String replacement) {
        Map<String, String> placeholders = new HashMap<>() {{
            put(placeholder, replacement);
        }};

        if (sender instanceof Player player) {
            return getMessage(player, placeholders);
        }

        return getMessage(null, placeholders);
    }

    public String getMessage(@Nullable final CommandSender sender, @NotNull final Map<String, String> placeholders) {
        if (sender instanceof Player player) {
            return getMessage(player, placeholders);
        }

        return getMessage(null, placeholders);
    }

    public String getMessage(@Nullable final Player player, @NotNull final Map<String, String> placeholders) {
        String prefix = this.config.getProperty(ConfigKeys.command_prefix);

        String message = parse(placeholders);

        if (Support.placeholder_api.isEnabled() && player != null) {
            return PlaceholderAPI.setPlaceholders(player, message.replaceAll("\\{prefix}", prefix));
        }

        return message.replaceAll("\\{prefix}", prefix);
    }

    private @NotNull String parse(@NotNull final Map<String, String> placeholders) {
        String message;

        if (isList()) {
            message = StringUtils.chomp(StringUtil.convertList(getList()));
        } else {
            message = getString();
        }

        if (!placeholders.isEmpty()) {
            for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                message = message.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
            }
        }

        return message;
    }

    public void sendMessage(Player player) {
        sendMessage(player, new HashMap<>());
    }

    public void sendMessage(Player player, Map<String, String> placeholder) {
        String message = getMessage(placeholder);

        if (message.isEmpty() || message.isBlank()) {
            return;
        }

        player.sendRichMessage(message);
    }

    public void sendMessage(CommandSender sender) {
        sendMessage(sender, new HashMap<>());
    }

    public void sendMessage(CommandSender sender, Map<String, String> placeholder) {
        String message = getMessage(placeholder);

        if (message.isEmpty() || message.isBlank()) {
            return;
        }

        sender.sendRichMessage(message);
    }

    public void broadcastMessage(boolean ignore) {
        broadcastMessage(ignore, new HashMap<>());
    }

    private final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);
    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

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