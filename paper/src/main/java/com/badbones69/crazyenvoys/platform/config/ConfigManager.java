package com.badbones69.crazyenvoys.platform.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazyenvoys.platform.config.types.ConfigKeys;
import com.badbones69.crazyenvoys.platform.config.types.MessageKeys;
import java.io.File;

public class ConfigManager {

    private static SettingsManager config;

    private static SettingsManager messages;

    public static void load(File dataFolder) {
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        File configFile = new File(dataFolder, "config.yml");

        config = SettingsManagerBuilder
                .withYamlFile(configFile, builder)
                .useDefaultMigrationService()
                .configurationData(ConfigurationDataBuilder.createConfiguration(ConfigKeys.class))
                .create();

        File localeDir = new File(dataFolder, "locale");

        if (!localeDir.exists()) localeDir.mkdirs();

        File file = new File(localeDir, config.getProperty(ConfigKeys.locale_file) + ".yml");

        messages = SettingsManagerBuilder
                .withYamlFile(file, builder)
                .useDefaultMigrationService()
                .configurationData(MessageKeys.class)
                .create();
    }

    public static void reload() {
        config.reload();

        messages.reload();
    }

    public static SettingsManager getConfig() {
        return config;
    }

    public static SettingsManager getMessages() {
        return messages;
    }
}