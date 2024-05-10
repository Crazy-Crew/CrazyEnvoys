package com.badbones69.crazyenvoys.platform.config.types;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class MessageKeys implements SettingsHolder {

    protected MessageKeys() {}

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyEnvoys/issues",
                "Features: https://github.com/Crazy-Crew/CrazyEnvoys/issues",
                "",
                "Tips:",
                " 1. Make sure to use the {prefix} to add the prefix in front of messages.",
                " 2. If you wish to use more than one line for a message just go from a line to a list.",
                "Examples:",
                "  Line:",
                "    No-Permission: '{prefix}<red>You do not have permission to use that command.'",
                "  List:",
                "    No-Permission:",
                "      - '{prefix}<red>You do not have permission'",
                "      - '<red>to use that command. Please try another.'"
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens.",
                ""
        };
        
        conf.setComment("player", header);
    }

    public static final Property<String> no_permission = newProperty("player.no-permission", "{prefix}<red>You do not have permission to use that command.");

    public static final Property<String> no_claim_permission = newProperty("player.no-permission-to-claim", "{prefix}<red>You do not have permission to claim that envoy.");

    public static final Property<String> envoy_already_started = newProperty("envoys.already-started", "{prefix}<red>There is already an envoy event running. Please stop it to start a new one.");

    public static final Property<String> envoy_force_start = newProperty("envoys.force-start", "{prefix}<gray>You have started the envoy.");

    public static final Property<String> envoy_not_started = newProperty("envoys.not-started", "{prefix}<red>There is no envoy event going on at this time.");

    public static final Property<String> envoy_force_ended = newProperty("envoys.force-ended", "{prefix}<red>You have ended the envoy.");

    public static final Property<String> envoy_warning = newProperty("envoys.warning", "{prefix}<red>[<dark_red>ALERT<red>] <gray>There is an envoy event happening in <gold>{time}.");

    public static final Property<List<String>> envoy_started = newListProperty("envoys.started.list", List.of(
            "{prefix}<gray>An envoy event has started. <gold>{amount} <gray>crates have spawned around spawn for 5m."
    ));

    public static final Property<String> envoys_remaining = newProperty("envoys.left", "{prefix}<gold>{player} <gray>has found a tier envoy. There are now <gold>{amount} <gray>left to find.");

    public static final Property<String> envoy_ended = newProperty("envoys.ended", "{prefix}<red>The envoy event has ended. Thanks for playing and please come back for the next one.");

    public static final Property<String> not_enough_players = newProperty("envoys.not-enough-players", "{prefix}<gray>Not enough players are online to start the envoy event. Only <gold>{amount} <gray>players are online.");

    public static final Property<String> enter_editor_mode = newProperty("envoys.enter-editor-mode", "{prefix}<gray>You are now in editor mode.");

    public static final Property<String> exit_editor_mode = newProperty("envoys.leave-editor-mode", "{prefix}<gray>You have now left editor mode.");

    public static final Property<String> envoy_clear_locations = newProperty("envoys.editor-clear-locations", "{prefix}<gray>You have cleared all the editor spawn locations.");

    public static final Property<String> envoy_clear_failure = newProperty("envoys.editor-clear-failure", "{prefix}<gray>You must be in Editor mode to clear the spawn locations.");

    public static final Property<String> envoy_kicked_from_editor_mode = newProperty("envoys.kicked-from-editor-mode", "{prefix}<red>Sorry but an envoy is active. Please stop it or wait till it''s over.");

    public static final Property<String> envoy_add_location = newProperty("envoys.add-location", "{prefix}<gray>You have added a spawn location.");

    public static final Property<String> envoy_remove_location = newProperty("envoys.remove-location", "{prefix}<red>You have removed a spawn location.");

    public static final Property<String> envoy_time_left = newProperty("envoys.time-left", "{prefix}<gray>The current envoy has <gold>{time}<gray> left.");

    public static final Property<String> envoy_time_till_event = newProperty("envoys.time-till-event", "{prefix}<gray>The next envoy will start in <gold>{time}<gray>.");

    public static final Property<String> envoy_used_flare = newProperty("envoys.flare.used-flare", "{prefix}<gray>You have started an envoy event with a flare.");

    public static final Property<String> envoy_cant_use_flare = newProperty("envoys.flare.cant-use-flares", "{prefix}<red>You do not have permission to use flares.");

    public static final Property<String> envoy_give_flare = newProperty("envoys.flare.sent-flare", "{prefix}<gray>You have given <gold>{player} {amount} <gray>flares.");

    public static final Property<String> envoy_received_flare = newProperty("envoys.flare.received-flare", "{prefix}<gray>You have been given <gold>{amount} <gray>flares.");

    public static final Property<String> envoy_new_center = newProperty("envoys.new-center", "{prefix}<gray>You have set a new center for the random envoy crates.");

    public static final Property<String> not_in_world_guard_region = newProperty("envoys.not-in-world-guard-region", "{prefix}<red>You must be in the WarZone to use a flare.");

    public static final Property<String> start_ignoring_messages = newProperty("envoys.start-ignoring-messages", "{prefix}<gray>You are now ignoring the collecting messages.");

    public static final Property<String> stop_ignoring_messages = newProperty("envoys.stop-ignoring-messages", "{prefix}<gray>You now see all the collecting messages.");

    public static final Property<String> cooldown_left = newProperty("envoys.cooldown-left", "{prefix}<gray>You still have <gold>{time} <gray>till you can collect another crate.");

    public static final Property<String> countdown_in_progress = newProperty("envoys.countdown-in-progress", "{prefix}<gray>You cannot claim any envoys for another <gold>{time} seconds.");

    public static final Property<String> drops_available = newProperty("envoys.drops-available", "{prefix}<gray>List of all available envoys.");

    public static final Property<String> drops_possibilities = newProperty("envoys.drops-possibilities", "{prefix}<gray>List of location envoy''s may spawn at.");

    public static final Property<String> drops_page = newProperty("envoys.drops-page", "{prefix}<gray>Use /crazyenvoys drops [page] to see more.");

    public static final Property<String> drops_format = newProperty("envoys.drops-format", "<gray>[<gold>{id}<gray>]: {world}, {x}, {y}, {z}");

    public static final Property<String> no_spawn_locations_found = newProperty("envoys.no-spawn-locations-found", "{prefix}<red>No spawn locations were found and so the event has been cancelled and the cooldown has been reset.");

    public static final Property<String> hologram_on_going = newProperty("envoys.hologram-placeholders.on-going", "On Going");

    public static final Property<String> hologram_not_running = newProperty("envoys.hologram-placeholders.not-running", "Not Running");

    public static final Property<String> time_placeholder_day = newProperty("envoys.time-placeholders.day", "d");
    public static final Property<String> time_placeholder_hour = newProperty("envoys.time-placeholders.hour", "h");
    public static final Property<String> time_placeholder_minute = newProperty("envoys.time-placeholders.minute", "m");
    public static final Property<String> time_placeholder_second = newProperty("envoys.time-placeholders.second", "s");

    public static final Property<String> envoy_locations = newProperty("envoys.envoy-locations", "<bold><gold>All Envoy Locations:</gold></bold> \\n<red>[ID], [World]: [X], [Y], [Z] <reset>{locations}");

    public static final Property<String> location_format = newProperty("envoys.location-format", "\\n<dark_gray>[<dark_aqua>{id}<dark_gray>] <red>{world}: {x}, {y}, {z}");

    public static final Property<String> command_not_found = newProperty("misc.command-not-found", "{prefix}<red>Please do /crazyenvoys help for more information.");

    public static final Property<String> player_only = newProperty("misc.player-only", "{prefix}<red>Only players can use that command.");

    public static final Property<String> not_online = newProperty("misc.not-online", "{prefix}<red>That player is not online at this time.");

    public static final Property<String> not_a_number = newProperty("misc.not-a-number", "{prefix}<red>That is not a number.");

    public static final Property<String> envoy_plugin_reloaded = newProperty("misc.config-reload", "{prefix}<gray>You have reloaded all the files.");

    public static final Property<List<String>> help = newListProperty("misc.help", List.of(
            "<gold>/crazyenvoys help <gray>- Shows the envoy help menu.",
            "<gold>/crazyenvoys reload <gray>- Reloads all the config files.",
            "<gold>/crazyenvoys time <gray>- Shows the time till the envoy starts or ends.",
            "<gold>/crazyenvoys drops [page] <gray>- Shows all current crate locations.",
            "<gold>/crazyenvoys ignore <gray>- Shuts up the envoy collecting message.",
            "<gold>/crazyenvoys flare [amount] [player] <gray>- Give a player a flare to call an envoy event.",
            "<gold>/crazyenvoys edit <gray>- Edit the crate locations with bedrock.",
            "<gold>/crazyenvoys start <gray>- Force starts the envoy.",
            "<gold>/crazyenvoys stop <gray>- Force stops the envoy.",
            "<gold>/crazyenvoys center <gray>- Set the center of the random crate drops."
    ));
}