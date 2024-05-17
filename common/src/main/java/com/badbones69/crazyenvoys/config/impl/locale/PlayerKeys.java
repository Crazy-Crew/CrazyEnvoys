package com.badbones69.crazyenvoys.config.impl.locale;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class PlayerKeys implements SettingsHolder {

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

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> must_be_a_player = newProperty("player.requirements.must-be-player", "{prefix}<red>You must be a player to use this command.");

    public static final Property<String> no_claim_permission = newProperty("player.no-permission-to-claim", "{prefix}<red>You do not have permission to claim that envoy.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> must_be_console_sender = newProperty("player.requirements.must-be-console-sender", "{prefix}<red>You must be using console to use this command.");

    @Comment("A list of available placeholders: {prefix}, {player}")
    public static final Property<String> not_online = newProperty("player.target-not-online", "{prefix}<red>{player} <gray>is not online.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> same_player = newProperty("player.target-same-player", "{prefix}<red>You can''t use this command on yourself.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> no_permission = newProperty("player.no-permission", "{prefix}<red>You do not have permission to use that command/menu!");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> inventory_not_empty = newProperty("player.inventory-not-empty", "{prefix}<red>Inventory is not empty, Please make room before opening <gold>{crate}.");

}