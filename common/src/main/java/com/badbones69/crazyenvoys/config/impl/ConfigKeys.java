package com.badbones69.crazyenvoys.config.impl;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class ConfigKeys implements SettingsHolder {
    
    protected ConfigKeys() {}

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Features: https://github.com/Crazy-Crew/CrazyEnvoys/discussions/categories/features",
                "Issues: https://github.com/Crazy-Crew/CrazyEnvoys/issues",
                ""
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens.",
                ""
        };
        
        conf.setComment("root", header);
    }

    @Comment({
            "Choose the language you prefer to use on your server!",
            "",
            "Currently Available:",
            " > en-US ( English )",
            " > es-PE ( Portuguese )",
            "",
            "If you do not see your language above, You can contribute by modifying the current en-US.yml",
            "https://github.com/Crazy-Crew/CrazyEnvoys/blob/main/paper/src/main/resources/locale/en-US.yml",
            "Submit your finalized config using https://bin.bloom.host/ and send it to us in https://discord.gg/badbones-s-live-chat-182615261403283459",
            ""
    })
    public static final Property<String> locale_file = PropertyInitializer.newProperty("root.language", "en-US");

    @Comment("Whether you want CrazyEnvoys to shut up or not, This option is ignored by errors.")
    public static final Property<Boolean> verbose_logging = PropertyInitializer.newProperty("root.verbose_logging", true);

    @Comment({
            "Sends anonymous statistics about how the plugin is used to bstats.org.",
            "bstats is a service for plugin developers to find out how the plugin being used,",
            "This information helps us figure out how to better improve the plugin."
    })
    public static final Property<Boolean> toggle_metrics = PropertyInitializer.newProperty("root.toggle_metrics", true);

    @Comment("The command prefix you want shown in front of commands!")
    public static final Property<String> command_prefix = PropertyInitializer.newProperty("root.command_prefix", "<dark_gray>[<light_purple>CrazyEnvoys<dark_gray>]: ");

    @Comment("The console prefix you want shown when the logging messages show up!")
    public static final Property<String> console_prefix = PropertyInitializer.newProperty("root.console_prefix" ,"<dark_gray>[<red>CrazyEnvoys<dark_gray>] ");

    @Comment("Whether a block should fall when an envoy spawns.")
    public static final Property<Boolean> envoy_falling_block_toggle = newProperty("envoys.falling-blocks.toggle", true);

    @Comment("The block type falling when an envoy spawns.")
    public static final Property<String> envoy_falling_block_type = newProperty("envoys.falling-blocks.type", "beacon");

    @Comment("How high should the block spawn when an envoy spawns?")
    public static final Property<Integer> envoy_falling_height = newProperty("envoys.falling-blocks.height", 15);

    @Comment({
            "Whether to always spawn the max amount of crates possible set below, If the option is false. Spawn locations set using /envoy edit will instead spawn.",
            "Note: You should only use this if random-locations is false."
    })
    public static final Property<Boolean> envoys_max_drops_toggle = newProperty("envoys.generation.max-drops-toggle", false);

    @Comment("The max amount of crates that will spawn.")
    public static final Property<Integer> envoys_max_drops = newProperty("envoys.generation.max-drops-amount", 20);

    @Comment("The min amount of crates that will spawn.")
    public static final Property<Integer> envoys_min_drops = newProperty("envoys.generation.min-drops-amount", 7);

    @Comment({
            "This option will spawn a random number of envoys between min-drops-amount and max-drops-amount.",
            "Note: You must set max-drops-toggle to false otherwise this will not function."
    })
    public static final Property<Boolean> envoys_random_drops = newProperty("envoys.generation.random-drops", true);

    @Comment("A list of blocks that envoys cannot be spawned on.")
    public static final Property<List<String>> envoys_blacklist_blocks = newListProperty("envoys.blacklisted-blocks", List.of(
            "water",
            "lava"
    ));

    @Comment({
            "This option will spawn envoys at complete random",
            "Note: You need to use /envoy center at wherever you want the center to be.",
            "Second Note: You may have to tweak the options above."
    })
    public static final Property<Boolean> envoys_random_locations = newProperty("envoys.generation.random-locations.toggle", true);

    @Comment("The maximum distance the envoys will fall from the center.")
    public static final Property<Integer> envoys_max_radius = newProperty("envoys.generation.random-locations.max-radius", 300);

    @Comment("The minimum distance from the middle the envoys will fall from the center.")
    public static final Property<Integer> envoys_min_radius = newProperty("envoys.generation.random-locations.min-radius", 20);

    @Comment("If all drop locations should be broadcast when an envoy starts.")
    public static final Property<Boolean> envoys_locations_broadcast = newProperty("envoys.broadcast", false);

    @Comment({
            "This option decides whether to run envoys at an interval or on a schedule.",
            "Note: If the option is false, It will only start via a command or a player's flare."
    })
    public static final Property<Boolean> envoys_run_time_toggle = newProperty("envoys.schedule.run-time.toggle", true);

    @Comment("This option decides how long the envoy event should last.")
    public static final Property<String> envoys_run_time = newProperty("envoys.schedule.run-time.value", "5m");

    @Comment("This option decides if the envoy event has a cooldown i.e 59 minutes or activates at a specific time of day.")
    public static final Property<Boolean> envoys_countdown = newProperty("envoys.schedule.countdown.toggle", true);

    @Comment("The time till the envoy event will happen again.")
    public static final Property<String> envoys_cooldown = newProperty("envoys.schedule.countdown.time", "1h");

    @Comment("A specified time of the day that an envoy will happen. Please use 1-12 for hours and 0-59 for minutes.")
    public static final Property<String> envoys_time = newProperty("envoys.schedule.countdown.timestamp", "2:00 AM");

    @Comment("If the envoy event should spawn if the server is empty.")
    public static final Property<Boolean> envoys_ignore_empty_server = newProperty("envoys.schedule.ignore-empty-server", true);

    @Comment("If the envoy event should require a minimum number of players. This option is no longer required for the flare requirements.")
    public static final Property<Boolean> envoys_minimum_players_toggle = newProperty("envoys.required-players.default.toggle", false);

    @Comment("The minimum amount of players needed to start an envoy.")
    public static final Property<Integer> envoys_minimum_players_amount = newProperty("envoys.required-players.default.amount", 12);

    @Comment("If the flare should require a minimum amount of players.")
    public static final Property<Boolean> envoys_flare_minimum_players_toggle = newProperty("flare.required-players.toggle", false);

    @Comment("The minimum amount of players needed to use a flare.")
    public static final Property<Integer> envoys_flare_minimum_players_amount = newProperty("flare.required-players.amount", 6);

    @Comment("The name of the flare item.")
    public static final Property<String> envoys_flare_item_name = newProperty("flare.item.name", "<bold><gray>(<dark_red>!<gray>)</bold> <red>Flare");

    @Comment("The material of the flare item.")
    public static final Property<String> envoys_flare_item_type = newProperty("flare.item.type", "redstone_torch");

    @Comment("The lore of the flare item.")
    public static final Property<List<String>> envoys_flare_item_lore = newListProperty("flare.item.lore", List.of(
            "<gray>Right click me to",
            "<gray>start an envoy event."
    ));

    @Comment("If they are limited to use flares only in specified regions.")
    public static final Property<Boolean> envoys_flare_world_guard_toggle = newProperty("flare.support.worldguard.toggle", false);

    @Comment("The name of the regions you can use flares in.")
    public static final Property<List<String>> envoys_flare_world_guard_regions = newListProperty("flare.support.worldguard.regions", List.of(
            "Warzone"
    ));

    @Comment("Broadcast a message when a player picks up an envoy.")
    public static final Property<Boolean> envoys_announce_player_pickup = newProperty("envoys.announce-player-pickup", true);

    @Comment("If the player should wait before being able to grab another envoy.")
    public static final Property<Boolean> envoys_grab_cooldown_toggle = newProperty("envoys.grab-cooldown.toggle", false);

    @Comment("The amount of time a player has to wait before grabbing another envoy.")
    public static final Property<String> envoys_grab_cooldown_timer = newProperty("envoys.grab-cooldown.time", "15s");

    @Comment("If there should be a grace period when an envoy spawns. What this means is the player will have to wait a certain amount of time.")
    public static final Property<Boolean> envoys_grace_period_toggle = newProperty("envoys.grace-period.toggle", false);

    @Comment("The time they have to wait to claim the envoy.")
    public static final Property<Integer> envoys_grace_period_timer = newProperty("envoys.grace-period.timer", 120);

    @Comment("The message shown above the envoy when the grace period is over.")
    public static final Property<String> envoys_grace_period_unlocked = newProperty("envoys.grace-period.message", "<red>Ready to claim.");

    @Comment("The unit of time to show at the end of the countdown since it's in seconds. The space is required!")
    public static final Property<String> envoys_grace_period_time_unit = newProperty("envoys.grace-period.time-unit", " seconds.");

    @Comment("If the broadcast messages are only in specific worlds.")
    public static final Property<Boolean> envoys_world_messages = newProperty("envoys.world-messages.toggle", false);

    @Comment("The worlds you wish to have messages show up in.")
    public static final Property<List<String>> envoys_allowed_worlds = newListProperty("envoys.world-messages.worlds", List.of(
            "world"
    ));

    @Comment("Pick what time stamps the warning messages will appear at.")
    public static final Property<List<String>> envoys_warnings = newListProperty("envoys.warnings", List.of(
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
}