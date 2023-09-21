package us.crazycrew.crazyenvoys.common.config.types;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class Messages implements SettingsHolder {

    protected Messages() {}

    @Override
    public void registerComments(@NotNull CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyEnvoys/issues",
                "Features: https://github.com/Crazy-Crew/CrazyEnvoys/issues",
                "",
                "Tips:",
                " 1. Make sure to use the %prefix% to add the prefix in front of messages.",
                " 2. If you wish to use more than one line for a message just go from a line to a list.",
                "Examples:",
                "  Line:",
                "    No-Permission: '%prefix%&cYou do not have permission to use that command.'",
                "  List:",
                "    No-Permission:",
                "      - '%prefix%&cYou do not have permission'",
                "      - '&cto use that command. Please try another.'"
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens.",
                ""
        };
        
        conf.setComment("Messages", header);
    }

    public static final Property<String> no_permission = newProperty("Messages.No-Permission", "%prefix%&cYou do not have permission to use that command.");

    public static final Property<String> no_claim_permission = newProperty("Messages.No-Permission-Claim", "%prefix%&cYou do not have permission to claim that envoy.");

    public static final Property<String> player_only = newProperty("Messages.Players-Only", "%prefix%&cOnly players can use that command.");

    public static final Property<String> not_online = newProperty("Messages.Not-Online", "%prefix%&cThat player is not online at this time.");

    public static final Property<String> not_a_number = newProperty("Messages.Not-A-Number", "%prefix%&cThat is not a number.");

    public static final Property<String> envoy_plugin_reloaded = newProperty("Messages.Reloaded", "%prefix%&7You have just reloaded all the files.");

    public static final Property<String> envoy_already_started = newProperty("Messages.Already-Started", "%prefix%&cThere is already an envoy event running. Please stop it to start a new one.");

    public static final Property<String> envoy_force_start = newProperty("Messages.Force-Start", "%prefix%&7You have just started the envoy.");

    public static final Property<String> envoy_not_started = newProperty("Messages.Not-Started", "%prefix%&cThere is no envoy event going on at this time.");

    public static final Property<String> envoy_force_ended = newProperty("Messages.Force-Ended", "%prefix%&cYou have just ended the envoy.");

    public static final Property<String> envoy_warning = newProperty("Messages.Warning", "%prefix%&c[&4ALERT&c] &7There is an envoy event happening in &6%time%.");

    public static final Property<String> envoy_started = newProperty("Messages.Started", "%prefix%&7An envoy event has just started. &6%amount% &7crates have spawned around spawn for 5m.");

    public static final Property<String> envoys_remaining = newProperty("Messages.Left", "%prefix%&6%player% &7has just found a %tier% envoy. There are now &6%amount% &7left to find.");

    public static final Property<String> envoy_ended = newProperty("Messages.Ended", "%prefix%&cThe envoy event has ended. Thanks for playing and please come back for the next one.");

    public static final Property<String> not_enough_players = newProperty("Messages.Not-Enough-Players", "%prefix%&7Not enough players are online to start the envoy event. Only &6%amount% &7players are online.");

    public static final Property<String> enter_editor_mode = newProperty("Messages.Enter-Editor-Mode", "%prefix%&7You are now in editor mode.");

    public static final Property<String> exit_editor_mode = newProperty("Messages.Leave-Editor-Mode", "%prefix%&7You have now left editor mode.");

    public static final Property<String> envoy_clear_locations = newProperty("Messages.Editor-Clear-Locations", "%prefix%&7You have cleared all the editor spawn locations.");

    public static final Property<String> envoy_clear_failure = newProperty("Messages.Editor-Clear-Failure", "%prefix%&7You must be in Editor mode to clear the spawn locations.");

    public static final Property<String> envoy_kicked_from_editor_mode = newProperty("Messages.Kicked-From-Editor-Mode", "%prefix%&cSorry but an envoy is active. Please stop it or wait till it''s over.");

    public static final Property<String> envoy_add_location = newProperty("Messages.Add-Location", "%prefix%&7You have just added a spawn location.");

    public static final Property<String> envoy_remove_location = newProperty("Messages.Remove-Location", "%prefix%&cYou have just removed a spawn location.");

    public static final Property<String> envoy_time_left = newProperty("Messages.Time-Left", "%prefix%&7The current envoy has &6%time%&7 left.");

    public static final Property<String> envoy_time_till_event = newProperty("Messages.Time-Till-Event", "%prefix%&7The next envoy will start in &6%time%&7.");

    public static final Property<String> envoy_used_flare = newProperty("Messages.Used-Flare", "%prefix%&7You have just started an envoy event with a flare.");

    public static final Property<String> envoy_cant_use_flare = newProperty("Messages.Cant-Use-Flares", "%prefix%&cYou do not have permission to use flares.");

    public static final Property<String> envoy_give_flare = newProperty("Messages.Give-Flare", "%prefix%&7You have just given &6%player% %amount% &7flares.");

    public static final Property<String> envoy_received_flare = newProperty("Messages.Given-Flare", "%prefix%&7You have been given &6%amount% &7flares.");

    public static final Property<String> envoy_new_center = newProperty("Messages.New-Center", "%prefix%&7You have just set a new center for the random envoy crates.");

    public static final Property<String> not_in_world_guard_region = newProperty("Messages.Not-In-World-Guard-Region", "%prefix%&cYou must be in the WarZone to use a flare.");

    public static final Property<String> start_ignoring_messages = newProperty("Messages.Start-Ignoring-Messages", "%prefix%&7You are now ignoring the collecting messages.");

    public static final Property<String> stop_ignoring_messages = newProperty("Messages.Stop-Ignoring-Messages", "%prefix%&7You now see all the collecting messages.");

    public static final Property<String> cooldown_left = newProperty("Messages.Cooldown-Left", "%prefix%&7You still have &6%time% &7till you can collect another crate.");

    public static final Property<String> countdown_in_progress = newProperty("Messages.Countdown-In-Progress", "%prefix%&7You cannot claim any envoys for another &6%time% seconds.");

    public static final Property<String> drops_available = newProperty("Messages.Drops-Available", "%prefix%&7List of all available envoys.");

    public static final Property<String> drops_possibilities = newProperty("Messages.Drops-Possibilities", "%prefix%&7List of location envoy''s may spawn at.");

    public static final Property<String> drops_page = newProperty("Messages.Drops-Page", "%prefix%&7Use /crazyenvoys drops [page] to see more.");

    public static final Property<String> drops_format = newProperty("Messages.Drops-Format", "&7[&6%id%&7]: %world%, %x%, %y%, %z%");

    public static final Property<String> no_spawn_locations_found = newProperty("Messages.No-Spawn-Locations-Found", "%prefix%&cNo spawn locations were found and so the event has been cancelled and the cooldown has been reset.");

    public static final Property<String> command_not_found = newProperty("Messages.Command-Not-Found", "%prefix%&cPlease do /crazyenvoys help for more information.");

    public static final Property<String> hologram_on_going = newProperty("Messages.Hologram-Placeholders.On-Going", "On Going");

    public static final Property<String> hologram_not_running = newProperty("Messages.Hologram-Placeholders.Not-Running", "Not Running");

    public static final Property<String> time_placeholder_day = newProperty("Messages.Time-Placeholders.Day", "d");
    public static final Property<String> time_placeholder_hour = newProperty("Messages.Time-Placeholders.Hour", "h");
    public static final Property<String> time_placeholder_minute = newProperty("Messages.Time-Placeholders.Minute", "m");
    public static final Property<String> time_placeholder_second = newProperty("Messages.Time-Placeholders.Second", "s");

    public static final Property<String> envoy_locations = newProperty("Messages.Crate-Locations", "&e&lAll Envoy Locations: \\n&c[ID], [World]: [X], [Y], [Z] &r%locations%");

    public static final Property<String> location_format = newProperty("Messages.Location-Format", "\\n&8[&3%id%&8] &c%world%: %x%, %y%, %z%");

    public static final Property<List<String>> help = newListProperty("Messages.Help", List.of(
            "&6/crazyenvoys help &7- Shows the envoy help menu.",
            "&6/crazyenvoys reload &7- Reloads all the config files.",
            "&6/crazyenvoys time &7- Shows the time till the envoy starts or ends.",
            "&6/crazyenvoys drops [page] &7- Shows all current crate locations.",
            "&6/crazyenvoys ignore &7- Shuts up the envoy collecting message.",
            "&6/crazyenvoys flare [amount] [player] &7- Give a player a flare to call an envoy event.",
            "&6/crazyenvoys edit &7- Edit the crate locations with bedrock.",
            "&6/crazyenvoys start &7- Force starts the envoy.",
            "&6/crazyenvoys stop &7- Force stops the envoy.",
            "&6/crazyenvoys center &7- Set the center of the random crate drops."
    ));
}