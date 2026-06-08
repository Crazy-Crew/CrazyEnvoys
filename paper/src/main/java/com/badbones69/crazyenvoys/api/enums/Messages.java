package com.badbones69.crazyenvoys.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.kyori.utils.AdvUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.config.types.ConfigKeys;
import com.badbones69.crazyenvoys.config.types.MessageKeys;
import org.jspecify.annotations.NonNull;
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
    must_be_console_sender(MessageKeys.must_be_console_sender),
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
    command_not_found(MessageKeys.unknown_command),
    correct_usage(MessageKeys.correct_usage),
    day(MessageKeys.time_placeholder_day),
    hour(MessageKeys.time_placeholder_hour),
    minute(MessageKeys.time_placeholder_minute),
    second(MessageKeys.time_placeholder_second),
    envoy_locations(MessageKeys.envoy_locations),
    location_format(MessageKeys.location_format),

    error_migrating(MessageKeys.error_migrating),
    migration_not_available(MessageKeys.migration_not_available),
    migration_plugin_not_enabled(MessageKeys.migration_plugin_not_enabled),
    migration_no_crates_available(MessageKeys.migration_no_crates_available),
    successfully_migrated(MessageKeys.successfully_migrated, true),
    successfully_migrated_users(MessageKeys.successfully_migrated_users, true),

    lacking_flag(MessageKeys.lacking_flag),

    help(MessageKeys.help, true);

    private Property<String> property;

    private Property<List<String>> listProperty;

    private boolean isList = false;

    /**
     * Used for strings
     *
     * @param property the property
     */
    Messages(@NonNull final Property<String> property) {
        this.property = property;
    }

    /**
     * Used for string lists
     *
     * @param listProperty the list property
     * @param isList Defines if it's a list or not.
     */
    Messages(@NonNull final Property<List<String>> listProperty, final boolean isList) {
        this.listProperty = listProperty;

        this.isList = isList;
    }

    private @NotNull final CrazyEnvoys plugin = CrazyEnvoys.get();
    private @NotNull final SettingsManager messages = ConfigManager.getMessages();
    private @NotNull final SettingsManager config = ConfigManager.getConfig();
    private @NotNull final CrazyManager crazyManager = this.plugin.getCrazyManager();

    private final FusionPaper fusion = this.plugin.getFusion();

    public @NonNull final String getString() {
        return this.messages.getProperty(this.property);
    }

    public @NonNull final List<String> getList() {
        return this.messages.getProperty(this.listProperty);
    }

    public void sendMessage(@NotNull final Audience sender, @NotNull final String placeholder, @NotNull final String replacement) {
        final State state = this.config.getProperty(ConfigKeys.message_state);

        switch (state) {
            case send_message -> sendRichMessage(sender, placeholder, replacement);
            case send_actionbar -> sendActionBar(sender, placeholder, replacement);
        }
    }

    public void sendMessage(@NotNull final Audience sender, @NotNull final Map<String, String> placeholders) {
        final State state = this.config.getProperty(ConfigKeys.message_state);

        switch (state) {
            case send_message -> sendRichMessage(sender, placeholders);
            case send_actionbar -> sendActionBar(sender, placeholders);
        }
    }

    public void sendMessage(@NotNull final Audience sender) {
        final State state = this.config.getProperty(ConfigKeys.message_state);

        switch (state) {
            case send_message -> sendRichMessage(sender);
            case send_actionbar -> sendActionBar(sender);
        }
    }

    public void sendRichMessage(@NotNull final Audience sender, @NotNull final String placeholder, @NotNull final String replacement) {
        sendRichMessage(sender, Map.of(placeholder, replacement));
    }

    public void sendRichMessage(@NotNull final Audience sender, @NotNull final Map<String, String> placeholders) {
        final String value = getMessage(sender, placeholders);

        if (value.isBlank()) return;

        sender.sendMessage(this.fusion.asComponent(value));
    }

    public void sendRichMessage(@NotNull final Audience sender) {
        sendRichMessage(sender, Map.of());
    }

    public void sendActionBar(@NotNull final Audience sender, @NotNull final String placeholder, @NotNull final String replacement) {
        sendActionBar(sender, Map.of(placeholder, replacement));
    }

    public void sendActionBar(@NotNull final Audience sender, @NotNull final Map<String, String> placeholders) {
        final String value = getMessage(sender, placeholders);

        if (value.isBlank()) return;

        if (sender instanceof Player player) {
            player.sendActionBar(this.fusion.asComponent(value));
        } else {
            sender.sendMessage(this.fusion.asComponent(value));
        }
    }

    public void sendActionBar(@NotNull final Audience sender) {
        sendActionBar(sender, Map.of());
    }

    public String getMessage(@NotNull final Audience sender, @NotNull final String placeholder, @NotNull final String replacement) {
        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put(placeholder, replacement);

        return getMessage(sender, placeholders);
    }

    public String getMessage(@NotNull final Audience sender, @NotNull final Map<String, String> placeholders) {
        return parse(sender, placeholders);
    }

    public String getMessage(@NotNull final Audience sender) {
        return getMessage(sender, new HashMap<>());
    }

    public String getMessage() {
        return getMessage(Audience.empty(), new HashMap<>());
    }

    public void broadcast(final boolean isIgnoring, @NonNull final Map<String, String> placeholders) {
        broadcast(isIgnoring, "", placeholders);
    }

    public void broadcast(final boolean isIgnoring, @NonNull final String permission, @NonNull final Map<String, String> placeholders) {
        final Server server = this.plugin.getServer();

        final SettingsManager config = ConfigManager.getConfig();

        // Send in console because we should lol.
        sendMessage(server.getConsoleSender(), placeholders);

        if (config.getProperty(ConfigKeys.envoys_world_messages)) {
            final List<String> worlds = config.getProperty(ConfigKeys.envoys_allowed_worlds);

            for (final Player player : server.getOnlinePlayers()) {
                final String worldName = player.getWorld().getName();

                if (!worlds.contains(worldName)) continue;

                if (isIgnoring && this.crazyManager.isIgnoringMessages(player.getUniqueId())) continue;

                if (!permission.isBlank() && !player.hasPermission(permission)) continue;

                sendMessage(player, placeholders);
            }

            return;
        }

        for (final Player player : server.getOnlinePlayers()) {
            if (isIgnoring && this.crazyManager.isIgnoringMessages(player.getUniqueId())) continue;

            if (!permission.isBlank() && !player.hasPermission(permission)) continue;

            sendMessage(player, placeholders);
        }
    }

    public void broadcast(final boolean isIgnoring) {
        broadcast(isIgnoring, new HashMap<>());
    }

    public void migrate() {
        if (this.isList) {
            this.messages.setProperty(this.listProperty, AdvUtils.convert(this.messages.getProperty(this.listProperty), true));

            return;
        }

        this.messages.setProperty(this.property, AdvUtils.convert(this.messages.getProperty(this.property), true));
    }

    private @NonNull String parse(@NotNull final Audience sender, @NonNull final Map<String, String> placeholders) {
        final Map<String, String> origin = new HashMap<>(placeholders);

        origin.putIfAbsent("{prefix}", this.config.getProperty(ConfigKeys.command_prefix));

        return this.fusion.parse(sender, this.isList ? StringUtils.toString(getList()) : getString(), origin);
    }
}