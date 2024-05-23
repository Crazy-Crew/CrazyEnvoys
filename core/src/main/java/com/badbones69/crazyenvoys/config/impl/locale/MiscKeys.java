package com.badbones69.crazyenvoys.config.impl.locale;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class MiscKeys implements SettingsHolder {

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "All messages related to misc things."
        };

        conf.setComment("misc", header);
    }

    @Comment("A list of available placeholders: {prefix}, {usage}")
    public static final Property<String> correct_usage = newProperty("misc.correct-usage", "{prefix}<red>The correct usage for this command is <yellow>{usage}");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> feature_disabled = newProperty("misc.feature-disabled", "{prefix}<red>This feature is disabled.");

    @Comment("A list of available placeholders: {prefix}, {command}")
    public static final Property<String> unknown_command = newProperty("misc.command-not-found", "{prefix}<red>{command} is not a known command.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> player_only = newProperty("misc.player-only", "{prefix}<red>Only players can use that command.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> not_online = newProperty("misc.not-online", "{prefix}<red>That player is not online at this time.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> not_a_number = newProperty("misc.not-a-number", "{prefix}<red>That is not a number.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> plugin_reloaded = newProperty("misc.config-reload", "{prefix}<gray>You have reloaded all the files.");

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