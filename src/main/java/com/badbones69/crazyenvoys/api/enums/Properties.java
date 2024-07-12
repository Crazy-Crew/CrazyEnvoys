package com.badbones69.crazyenvoys.api.enums;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;
import com.badbones69.crazyenvoys.config.types.ConfigKeys;
import com.badbones69.crazyenvoys.config.types.MessageKeys;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public enum Properties {

    no_permission(MessageKeys.no_permission, newProperty("Messages.No-Permission", MessageKeys.no_permission.getDefaultValue())),
    no_claim_permission(MessageKeys.no_claim_permission, newProperty("Messages.No-Permission-Claim", MessageKeys.no_claim_permission.getDefaultValue())),

    player_only(MessageKeys.player_only, newProperty("Messages.Players-Only", MessageKeys.player_only.getDefaultValue())),
    not_online(MessageKeys.not_online, newProperty("Messages.Not-Online", MessageKeys.not_online.getDefaultValue())),
    not_a_number(MessageKeys.not_a_number, newProperty("Messages.Not-A-Number", MessageKeys.not_a_number.getDefaultValue())),

    plugin_reloaded(MessageKeys.envoy_plugin_reloaded, newProperty("Messages.Reloaded", MessageKeys.envoy_plugin_reloaded.getDefaultValue())),

    already_started(MessageKeys.envoy_already_started, newProperty("Messages.Already-Started", MessageKeys.envoy_already_started.getDefaultValue())),
    force_started(MessageKeys.envoy_force_start, newProperty("Messages.Force-Start", MessageKeys.envoy_force_start.getDefaultValue())),
    force_ended(MessageKeys.envoy_force_ended, newProperty("Messages.Force-Ended", MessageKeys.envoy_force_ended.getDefaultValue())),

    not_started(MessageKeys.envoy_not_started, newProperty("Messages.Not-Started", MessageKeys.envoy_not_started.getDefaultValue())),

    warning(MessageKeys.envoy_warning, newProperty("Messages.Warning", MessageKeys.envoy_warning.getDefaultValue())),
    other_started(MessageKeys.envoy_started, newListProperty("Envoys.Started", MessageKeys.envoy_started.getDefaultValue()), Collections.emptyList()),
    started(MessageKeys.envoy_started, newListProperty("Messages.Started", MessageKeys.envoy_started.getDefaultValue()), Collections.emptyList()),
    left(MessageKeys.envoys_remaining, newProperty("Messages.Left", MessageKeys.envoys_remaining.getDefaultValue())),
    ended(MessageKeys.envoy_ended, newProperty("Messages.Ended", MessageKeys.envoy_ended.getDefaultValue())),

    not_enough_players(MessageKeys.not_enough_players, newProperty("Messages.Not-Enough-Players", MessageKeys.not_enough_players.getDefaultValue())),

    enter_editor_mode(MessageKeys.enter_editor_mode, newProperty("Messages.Enter-Editor-Mode", MessageKeys.enter_editor_mode.getDefaultValue())),
    leave_editor_mode(MessageKeys.exit_editor_mode, newProperty("Messages.Leave-Editor-Mode", MessageKeys.exit_editor_mode.getDefaultValue())),
    clear_locations(MessageKeys.envoy_clear_locations, newProperty("Messages.Editor-Clear-Locations", MessageKeys.envoy_clear_locations.getDefaultValue())),
    failed_clear_locations(MessageKeys.envoy_clear_failure, newProperty("Messages.Editor-Clear-Failure", MessageKeys.envoy_clear_failure.getDefaultValue())),
    kicked_from_editor(MessageKeys.envoy_kicked_from_editor_mode, newProperty("Messages.Kicked-From-Editor-Mode", MessageKeys.envoy_kicked_from_editor_mode.getDefaultValue())),

    add_location(MessageKeys.envoy_add_location, newProperty("Messages.Add-Location", MessageKeys.envoy_add_location.getDefaultValue())),
    remove_location(MessageKeys.envoy_remove_location, newProperty("Messages.Remove-Location", MessageKeys.envoy_remove_location.getDefaultValue())),

    time_left(MessageKeys.envoy_time_left, newProperty("Messages.Time-Left", MessageKeys.envoy_time_left.getDefaultValue())),
    time_till_event(MessageKeys.envoy_time_till_event, newProperty("Messages.Time-Till-Event", MessageKeys.envoy_time_till_event.getDefaultValue())),

    envoy_used_flare(MessageKeys.envoy_used_flare, newProperty("Messages.Used-Flare", MessageKeys.envoy_used_flare.getDefaultValue())),
    envoy_cant_use_flare(MessageKeys.envoy_cant_use_flare, newProperty("Messages.Cant-Use-Flares", MessageKeys.envoy_cant_use_flare.getDefaultValue())),
    envoy_give_flare(MessageKeys.envoy_give_flare, newProperty("Messages.Give-Flare", MessageKeys.envoy_give_flare.getDefaultValue())),
    envoy_given_flare(MessageKeys.envoy_received_flare, newProperty("Messages.Given-Flare", MessageKeys.envoy_received_flare.getDefaultValue())),

    new_center(MessageKeys.envoy_new_center, newProperty("Messages.New-Center", MessageKeys.envoy_new_center.getDefaultValue())),
    not_in_worldguard_region(MessageKeys.not_in_world_guard_region, newProperty("Messages.Not-In-World-Guard-Region", MessageKeys.not_in_world_guard_region.getDefaultValue())),

    start_ignoring_messages(MessageKeys.start_ignoring_messages, newProperty("Messages.Start-Ignoring-Messages", MessageKeys.start_ignoring_messages.getDefaultValue())),
    stop_ignoring_messages(MessageKeys.stop_ignoring_messages, newProperty("Messages.Stop-Ignoring-Messages", MessageKeys.stop_ignoring_messages.getDefaultValue())),

    cooldown_left(MessageKeys.cooldown_left, newProperty("Messages.Cooldown-Left", MessageKeys.cooldown_left.getDefaultValue())),
    countdown_in_progress(MessageKeys.countdown_in_progress, newProperty("Messages.Countdown-In-Progress", MessageKeys.countdown_in_progress.getDefaultValue())),

    drops_available(MessageKeys.drops_available, newProperty("Messages.Drops-Available", MessageKeys.drops_available.getDefaultValue())),
    drops_possibilities(MessageKeys.drops_possibilities, newProperty("Messages.Drops-Possibilities", MessageKeys.drops_possibilities.getDefaultValue())),

    drops_page(MessageKeys.drops_page, newProperty("Messages.Drops-Page", MessageKeys.drops_page.getDefaultValue())),
    drops_format(MessageKeys.drops_format, newProperty("Messages.Drops-Format", MessageKeys.drops_format.getDefaultValue())),

    no_spawn_locations(MessageKeys.no_spawn_locations_found, newProperty("Messages.No-Spawn-Locations-Found", MessageKeys.no_spawn_locations_found.getDefaultValue())),

    command_not_found(MessageKeys.command_not_found, newProperty("Messages.Command-Not-Found", MessageKeys.command_not_found.getDefaultValue())),

    hologram_active(MessageKeys.hologram_on_going, newProperty("Messages.Hologram-Placeholders.On-Going", MessageKeys.hologram_on_going.getDefaultValue())),
    hologram_not_active(MessageKeys.hologram_not_running, newProperty("Messages.Messages.Hologram-Placeholders.Not-Running", MessageKeys.hologram_not_running.getDefaultValue())),

    placeholder_day(MessageKeys.time_placeholder_day, newProperty("Messages.Time-Placeholders.Day", MessageKeys.time_placeholder_day.getDefaultValue())),
    placeholder_hour(MessageKeys.time_placeholder_hour, newProperty("Messages.Time-Placeholders.Hour", MessageKeys.time_placeholder_hour.getDefaultValue())),
    placeholder_minute(MessageKeys.time_placeholder_minute, newProperty("Messages.Time-Placeholders.Minute", MessageKeys.time_placeholder_minute.getDefaultValue())),
    placeholder_second(MessageKeys.time_placeholder_second, newProperty("Messages.Time-Placeholders.Second", MessageKeys.time_placeholder_second.getDefaultValue())),

    crate_locations(MessageKeys.envoy_locations, newProperty("Messages.Crate-Locations", MessageKeys.envoy_locations.getDefaultValue())),
    location_format(MessageKeys.location_format, newProperty("Messages.Location-Format", MessageKeys.location_format.getDefaultValue())),
    help(MessageKeys.help, newListProperty("Messages.Help", MessageKeys.help.getDefaultValue()), Collections.emptyList()),

    command_prefix(ConfigKeys.command_prefix, newProperty("Settings.Prefix", ConfigKeys.command_prefix.getDefaultValue())),
    falling_toggle(ConfigKeys.envoy_falling_block_toggle, newProperty("Settings.Falling-Block-Toggle", ConfigKeys.envoy_falling_block_toggle.getDefaultValue()), false),
    falling_block(ConfigKeys.envoy_falling_block_type, newProperty("Settings.Falling-Block", ConfigKeys.envoy_falling_block_type.getDefaultValue())),
    falling_height(ConfigKeys.envoy_falling_height, newProperty("Settings.Fall-Height", ConfigKeys.envoy_falling_height.getDefaultValue()), 1),

    max_crate_toggle(ConfigKeys.envoys_max_drops_toggle, newProperty("Settings.Max-Crate-Toggle", ConfigKeys.envoys_max_drops_toggle.getDefaultValue()), false),

    random_amount(ConfigKeys.envoys_random_drops, newProperty("Settings.Random-Amount", ConfigKeys.envoys_random_drops.getDefaultValue()), false),
    min_crates(ConfigKeys.envoys_min_drops, newProperty("Settings.Min-Crates", ConfigKeys.envoys_min_drops.getDefaultValue()), 1),
    max_crates(ConfigKeys.envoys_max_drops, newProperty("Settings.Max-Crates", ConfigKeys.envoys_max_drops.getDefaultValue()), 1),

    random_locations(ConfigKeys.envoys_random_locations, newProperty("Settings.Random-Locations", ConfigKeys.envoys_random_locations.getDefaultValue()), false),

    max_radius(ConfigKeys.envoys_max_radius, newProperty("Settings.Max-Radius", ConfigKeys.envoys_max_radius.getDefaultValue()), 1),
    min_radius(ConfigKeys.envoys_min_radius, newProperty("Settings.Min-Radius", ConfigKeys.envoys_min_radius.getDefaultValue()), 1),

    locations_broadcast(ConfigKeys.envoys_locations_broadcast, newProperty("Settings.Envoy-Locations.Broadcast", ConfigKeys.envoys_locations_broadcast.getDefaultValue()), false),

    run_time(ConfigKeys.envoys_run_time, newProperty("Settings.Envoy-Run-Time", ConfigKeys.envoys_run_time.getDefaultValue())),
    run_time_toggle(ConfigKeys.envoys_run_time_toggle, newProperty("Settings.Envoy-Timer-Toggle", ConfigKeys.envoys_run_time_toggle.getDefaultValue()), false),

    cooldown_toggle(ConfigKeys.envoys_countdown, newProperty("Settings.Envoy-Cooldown-Toggle", ConfigKeys.envoys_countdown.getDefaultValue()), false),

    envoys_cooldown(ConfigKeys.envoys_cooldown, newProperty("Settings.Envoy-Cooldown", ConfigKeys.envoys_cooldown.getDefaultValue())),
    envoys_time(ConfigKeys.envoys_time, newProperty("Settings.Envoy-Time", ConfigKeys.envoys_time.getDefaultValue())),

    envoys_ignore_empty_server(ConfigKeys.envoys_ignore_empty_server, newProperty("Settings.Envoy-Filter-Players-Zero", ConfigKeys.envoys_ignore_empty_server.getDefaultValue()), false),

    minimum_players_toggle(ConfigKeys.envoys_minimum_players_toggle, newProperty("Settings.Minimum-Players-Toggle", ConfigKeys.envoys_minimum_players_toggle.getDefaultValue()), false),
    minimum_players_count(ConfigKeys.envoys_minimum_players_amount, newProperty("Settings.Minimum-Players", ConfigKeys.envoys_minimum_players_amount.getDefaultValue()), 1),

    minimum_flare_toggle(ConfigKeys.envoys_flare_minimum_players_toggle, newProperty("Settings.Minimum-Flare-Toggle", ConfigKeys.envoys_flare_minimum_players_toggle.getDefaultValue()), false),
    minimum_flare_count(ConfigKeys.envoys_flare_minimum_players_amount, newProperty("Settings.Minimum-Players", ConfigKeys.envoys_flare_minimum_players_amount.getDefaultValue()), 1),

    envoys_flare_item_name(ConfigKeys.envoys_flare_item_name, newProperty("Settings.Flares.Item", ConfigKeys.envoys_flare_item_name.getDefaultValue())),
    envoys_flare_item_type(ConfigKeys.envoys_flare_item_type, newProperty("Settings.Flares.Name", ConfigKeys.envoys_flare_item_type.getDefaultValue())),
    envoys_flare_item_lore(ConfigKeys.envoys_flare_item_lore, newListProperty("Settings.Flares.Lore", ConfigKeys.envoys_flare_item_lore.getDefaultValue()), Collections.emptyList()),

    envoys_flare_world_guard_toggle(ConfigKeys.envoys_flare_minimum_players_toggle, newProperty("Settings.Flares.World-Guard.Toggle", ConfigKeys.envoys_flare_minimum_players_toggle.getDefaultValue()), false),
    envoys_flare_world_guard_regions(ConfigKeys.envoys_flare_world_guard_regions, newListProperty("Settings.Flares.World-Guard.Regions", ConfigKeys.envoys_flare_world_guard_regions.getDefaultValue()), Collections.emptyList()),

    envoys_announce_player_pickup(ConfigKeys.envoys_announce_player_pickup, newProperty("Settings.Broadcast-Crate-Pick-Up", ConfigKeys.envoys_announce_player_pickup.getDefaultValue()), false),
    envoys_grab_cooldown_toggle(ConfigKeys.envoys_grab_cooldown_toggle, newProperty("Settings.Crate-Collect-Cooldown.Toggle", ConfigKeys.envoys_grab_cooldown_toggle.getDefaultValue()), false),
    envoys_grab_cooldown_timer(ConfigKeys.envoys_grab_cooldown_timer, newProperty("Settings.Crate-Collect-Cooldown.Time", ConfigKeys.envoys_grab_cooldown_timer.getDefaultValue())),

    envoys_grace_period_toggle(ConfigKeys.envoys_grace_period_toggle, newProperty("Settings.Crate-Countdown.Toggle", ConfigKeys.envoys_grace_period_toggle.getDefaultValue()), false),
    envoys_grace_period_timer(ConfigKeys.envoys_grace_period_timer, newProperty("Settings.Crate-Countdown.Time", ConfigKeys.envoys_grace_period_timer.getDefaultValue()), 1),
    envoys_grace_period_unlocked(ConfigKeys.envoys_grace_period_unlocked, newProperty("Settings.Crate-Countdown.Message", ConfigKeys.envoys_grace_period_unlocked.getDefaultValue())),
    envoys_grace_period_time_unit(ConfigKeys.envoys_grace_period_time_unit, newProperty("Settings.Crate-Countdown.Message-Seconds", ConfigKeys.envoys_grace_period_time_unit.getDefaultValue())),

    envoys_world_messages(ConfigKeys.envoys_world_messages, newProperty("Settings.World-Messages.Toggle", ConfigKeys.envoys_world_messages.getDefaultValue()), false),

    envoys_allowed_worlds(ConfigKeys.envoys_allowed_worlds, newListProperty("Settings.World-Messages.Worlds", ConfigKeys.envoys_allowed_worlds.getDefaultValue()), Collections.emptyList()),
    envoys_warnings(ConfigKeys.envoys_warnings, newListProperty("Settings.Envoy-Warnings", ConfigKeys.envoys_warnings.getDefaultValue()), Collections.emptyList());

    private Property<String> newString;
    private Property<String> oldString;

    /**
     * A constructor moving the new and old string property for migration
     *
     * @param newString the new property
     * @param oldString the old property
     */
    Properties(Property<String> newString, Property<String> oldString) {
        this.newString = newString;
        this.oldString = oldString;
    }

    /**
     * Moves the old value to the new value
     *
     * @param reader the config reader
     * @param configuration the configuration data
     * @return true or false
     */
    public boolean moveString(PropertyReader reader, ConfigurationData configuration) {
        String key = reader.getString(this.oldString.getPath());

        if (key == null) return false;

        configuration.setValue(this.newString, replace(this.oldString.determineValue(reader).getValue()));

        return true;
    }

    private Property<Boolean> newBoolean;
    private Property<Boolean> oldBoolean;

    /**
     * A constructor consisting of the new and old boolean property for migration
     *
     * @param newBoolean the new property
     * @param oldBoolean the old property
     * @param dummy only to differentiate from previous constructors
     */
    Properties(Property<Boolean> newBoolean, Property<Boolean> oldBoolean, boolean dummy) {
        this.newBoolean = newBoolean;
        this.oldBoolean = oldBoolean;
    }

    /**
     * Moves the old value to the new value
     *
     * @param reader the config reader
     * @param configuration the configuration data
     * @return true or false
     */
    public boolean moveBoolean(PropertyReader reader, ConfigurationData configuration) {
        Boolean key = reader.getBoolean(this.oldBoolean.getPath());

        if (key == null) return false;

        configuration.setValue(this.newBoolean, this.oldBoolean.determineValue(reader).getValue());

        return true;
    }

    private Property<Integer> newInteger;
    private Property<Integer> oldInteger;

    /**
     * A constructor consisting of the new and old int property for migration
     *
     * @param newInteger the new property
     * @param oldInteger the old property
     * @param dummy only to differentiate from previous constructors
     */
    Properties(Property<Integer> newInteger, Property<Integer> oldInteger, int dummy) {
        this.newInteger = newInteger;
        this.oldInteger = oldInteger;
    }

    /**
     * Moves the old value to the new value
     *
     * @param reader the config reader
     * @param configuration the configuration data
     * @return true or false
     */
    public boolean moveInteger(PropertyReader reader, ConfigurationData configuration) {
        Integer key = reader.getInt(this.oldInteger.getPath());

        if (key == null) return false;

        configuration.setValue(this.newInteger, this.oldInteger.determineValue(reader).getValue());

        return true;
    }

    private Property<List<String>> newList;
    private Property<List<String>> oldList;

    /**
     * A constructor consisting of the new and old list property for migration
     *
     * @param newList the new property
     * @param oldList the old property
     * @param dummy only to differentiate from previous constructors
     */
    Properties(Property<List<String>> newList, Property<List<String>> oldList, List<String> dummy) {
        this.newList = newList;
        this.oldList = oldList;
    }

    /**
     * Moves the old value to the new value
     *
     * @param reader the config reader
     * @param configuration the configuration data
     * @return true or false
     */
    public boolean moveList(PropertyReader reader, ConfigurationData configuration) {
        List<?> key = reader.getList(this.oldList.getPath());

        if (key == null) return false;

        List<String> list = new ArrayList<>();

        this.oldList.determineValue(reader).getValue().forEach(line -> list.add(replace(line)));

        configuration.setValue(this.newList, list);

        return true;
    }

    /**
     * Replaces old placeholders in the option when migrating.
     *
     * @param message the message to check
     * @return the finalized message to set
     */
    private String replace(final String message) {
        return message.replaceAll("%prefix%", "{prefix}")
                .replaceAll("%Prefix%", "{prefix}")
                .replaceAll("%time%", "{time}")
                .replaceAll("%amount%", "{amount}")
                .replaceAll("%tier%", "{tier}")
                .replaceAll("%player%", "{player}")
                .replaceAll("%world%", "{world}")
                .replaceAll("%x%", "{x}")
                .replaceAll("%y%", "{y}")
                .replaceAll("%z%", "{z}")
                .replaceAll("%id%", "{id}")
                .replaceAll("%locations%", "{locations}");
    }
}