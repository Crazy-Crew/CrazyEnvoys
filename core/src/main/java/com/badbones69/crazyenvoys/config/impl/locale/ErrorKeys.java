package com.badbones69.crazyenvoys.config.impl.locale;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class ErrorKeys implements SettingsHolder {

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "All messages related to errors."
        };

        conf.setComment("errors", header);
    }

    //@Comment("A list of available placeholders: {prefix}, {envoy}")
    //public static final Property<String> no_prizes_found = newProperty("errors.no-prizes-found", "{prefix}<red>This crate contains no prizes that you can win.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> internal_error = newProperty("errors.internal-error", "{prefix}<red>An internal error has occurred. Please check the console for the full error.");

    @Comment("A list of available placeholders: {prefix}, {value}")
    public static final Property<String> cannot_be_empty = newProperty("errors.cannot-be-empty", "{prefix}<red>{value} cannot be empty!");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> cannot_be_air = newProperty("errors.cannot-be-air", "{prefix}<red>You can't use air silly!~");

    @Comment("A list of available placeholders: {prefix}, {envoy}, {reward}")
    public static final Property<String> reward_error = newProperty("errors.reward-error", "{prefix}<red>An error has occurred while trying to give you the prize called <gold>{reward}<red> in crate called <gold>{envoy}<red>. Please contact the server owner and show them this error.");
}