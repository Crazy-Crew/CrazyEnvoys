package us.crazycrew.crazyenvoys.common.config.types;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class Config implements SettingsHolder {
    
    protected Config() {}

    @Override
    public void registerComments(@NotNull CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyEnvoys/issues",
                "Features: https://github.com/Crazy-Crew/CrazyEnvoys/issues",
                ""
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens.",
                ""
        };
        
        conf.setComment("Settings", header);
    }
    
    @Comment("The prefix that shows up for all commands.")
    public static final Property<String> command_prefix = newProperty("Settings.Prefix", "&7[&cCrazyEnvoys&7] ");
    
    @Comment({
            "Sends anonymous statistics about how the plugin is used to bstats.org.",
            "bstats is a service for plugin developers to find out how the plugin being used,",
            "This information helps us figure out how to better improve the plugin."
    })
    public static final Property<Boolean> toggle_metrics = newProperty("Settings.Toggle-Metrics", true);

    @Comment("Toggle if it spawns a falling block or if the crates just appear.")
    public static final Property<Boolean> falling_block_toggle = newProperty("Settings.Falling-Block-Toggle", true);

    @Comment("The block that will be falling.")
    public static final Property<String> falling_block = newProperty("Settings.Falling-Block", "BEACON");

    @Comment("How high the falling blocks spawn.")
    public static final Property<Integer> falling_height = newProperty("Settings.Fall-Height", 15);

    @Comment({
            "This option should ONLY be used if Random-Locations is false.",
            "If true then Max-Crates amount of crates will spawn and if false and using set spawn locations, then all crates will spawn."
    })
    public static final Property<Boolean> max_crate_toggle = newProperty("Settings.Max-Crate-Toggle", false);

    @Comment("If true, selects a random number of crates between the Min-Crates and Max-Crates values. Max-Crate-Toggle has to be false for this to work.")
    public static final Property<Boolean> random_amount = newProperty("Settings.Random-Amount", false);

    @Comment("The min amount of crates that will spawn.")
    public static final Property<Integer> min_crates = newProperty("Settings.Min-Crates", 7);

    @Comment("The max amount of crates that will spawn.")
    public static final Property<Integer> max_crates = newProperty("Settings.Max-Crates", 20);

    @Comment("If true you will need to make sure to set the center location.")
    public static final Property<Boolean> random_locations = newProperty("Settings.Random-Locations", false);

    @Comment("The maximum distance the crates will fall from the center.")
    public static final Property<Integer> max_radius = newProperty("Settings.Max-Radius", 300);

    @Comment("The minimum distance from the middle the crates will fall from.")
    public static final Property<Integer> min_radius = newProperty("Settings.Min-Radius", 20);

    @Comment("If envoy locations should be broadcast whenever an envoy event starts.")
    public static final Property<Boolean> envoy_locations_broadcast = newProperty("Settings.Envoy-Locations.Broadcast", false);

    @Comment("The amount of time the envoy event will last.")
    public static final Property<String> envoy_run_time = newProperty("Settings.Envoy-Run-Time", "5m");

    @Comment("If the envoys happen on an interval. If false the only wait it will start is if it's started with the command or flare.")
    public static final Property<Boolean> envoy_timer_toggle = newProperty("Settings.Envoy-Timer-Toggle", true);

    @Comment("Toggle if it uses a cooldown or a specified time of the day.")
    public static final Property<Boolean> envoy_cooldown_toggle = newProperty("Settings.Envoy-Cooldown-Toggle", true);

    @Comment("Toggle if you want CrazyEnvoys to ignore when player counts are 0.")
    public static final Property<Boolean> envoy_filter_players_zero = newProperty("Settings.Envoy-Filter-Players-Zero", true);

    @Comment("The time till the envoy event will happen again.")
    public static final Property<String> envoy_cooldown = newProperty("Settings.Envoy-Cooldown", "1h");

    @Comment("A specified time of the day that an envoy will happen. Please use 1-12 for hours and 0-59 for minutes.")
    public static final Property<String> envoy_time = newProperty("Settings.Envoy-Time", "2:00 AM");

    @Comment("When the envoy is about to start, toggle if there needs to be a minimum amount of players")
    public static final Property<Boolean> minimum_players_toggle = newProperty("Settings.Minimum-Players-Toggle", false);

    @Comment({
            "If you want to require flares to meet the minimum player requirement.",
            "`Minimum-Players-Toggle` must be enabled for this to function."
    })
    public static final Property<Boolean> minimum_flare_toggle = newProperty("Settings.Minimum-Flare-Toggle", false);

    @Comment("The minimum amount of players needed to start an envoy.")
    public static final Property<Integer> minimum_players = newProperty("Settings.Minimum-Players", 12);

    @Comment("Whether to broadcast when a player picks up an envoy.")
    public static final Property<Boolean> broadcast_envoy_pick_up = newProperty("Settings.Broadcast-Crate-Pick-Up", true);

    @Comment("Whether they have to wait to claim the envoy.")
    public static final Property<Boolean> envoy_collect_cooldown_toggle = newProperty("Settings.Crate-Collect-Cooldown.Toggle", false);

    @Comment("The time they have to wait to claim the envoy.")
    public static final Property<String> envoy_collect_cooldown_time = newProperty("Settings.Crate-Collect-Cooldown.Time", "15s");

    @Comment("If there should be a grace period when envoys spawn.")
    public static final Property<Boolean> envoy_countdown_toggle = newProperty("Settings.Crate-Countdown.Toggle", false);

    @Comment("How long in seconds till the envoys can be opened?")
    public static final Property<Integer> envoy_countdown_time = newProperty("Settings.Crate-Countdown.Time", 120);

    @Comment("The message to show if the countdown is up or the toggle is set to false.")
    public static final Property<String> envoy_countdown_message = newProperty("Settings.Crate-Countdown.Message", "&cReady to claim.");

    @Comment("This needs to have a space, so it's not crunched next to the countdown.")
    public static final Property<String> envoy_countdown_message_other = newProperty("Settings.Crate-Countdown.Message-Seconds", " seconds.");

    @Comment("Toggle if the broadcast messages are only in specific worlds.")
    public static final Property<Boolean> envoy_world_messages = newProperty("Settings.World-Messages.Toggle", false);

    @Comment("The worlds you wish to have messages show up in.")
    public static final Property<List<String>> envoy_allowed_worlds = newListProperty("Settings.World-Messages.Worlds", List.of(
            "world"
    ));

    @Comment("Pick what time stamps the warning messages will appear at.")
    public static final Property<List<String>> envoy_warnings = newListProperty("Settings.Envoy-Warnings", List.of(
            "30m",
            "15m",
            "10m",
            "1m",
            "30s",
            "20s",
            "10s",
            "5s",
            "3s",
            "1s"
    ));

    @Comment("What the item for the flare is.")
    public static final Property<String> envoy_flare_item = newProperty("Settings.Flares.Item", "REDSTONE_TORCH");

    @Comment("The name of the flare item.")
    public static final Property<String> envoy_flare_item_name = newProperty("Settings.Flares.Name", "&7&l(&4&l!&7&l) &cFlare");

    @Comment("The lore of the flare item.")
    public static final Property<List<String>> envoy_flare_item_lore = newListProperty("Settings.Flares.Lore", List.of(
            "&7Right click me to",
            "&7start an envoy event."
    ));

    @Comment("If they are limited to use flares only in specified regions.")
    public static final Property<Boolean> envoy_flare_world_guard_toggle = newProperty("Settings.Flares.World-Guard.Toggle", false);

    @Comment("The name of the regions you can use flares in.")
    public static final Property<List<String>> envoy_flare_world_guard_regions = newListProperty("Settings.Flares.World-Guard.Regions", List.of(
            "WarZone"
    ));

}