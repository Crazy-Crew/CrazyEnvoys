package com.badbones69.crazyenvoys.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazyenvoys.config.impl.ConfigKeys;
import com.badbones69.crazyenvoys.config.impl.MessageKeys;
import com.ryderbelserion.vital.core.config.YamlManager;
import com.ryderbelserion.vital.core.util.FileUtil;
import org.jetbrains.annotations.ApiStatus;
import java.io.File;

public class ConfigManager {

    private static final YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

    private static YamlManager yamlManager;

    private static SettingsManager config;

    private static SettingsManager messages;

    /**
     * Loads configuration files.
     */
    @ApiStatus.Internal
    public static void load(File dataFolder) {
        if (yamlManager == null) {
            yamlManager = new YamlManager();
        }

        // Create plugin directory
        yamlManager.createPluginDirectory();

        // Create config.yml
        config = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "config.yml"), builder)
                //.migrationService(new ConfigMigration())
                .configurationData(ConfigKeys.class)
                .create();

        File localeDir = new File(dataFolder, "locale");

        // Update locale file
        locale(localeDir);

        // Add files
        yamlManager.addFile("users.yml")
                .addFolder("rewards")
                .addFolder("tiers")
                .init();
    }

    /**
     * Refreshes configuration files.
     */
    public static void refresh(File dataFolder) {
        // Get old locale file.
        String oldLocale = config.getProperty(ConfigKeys.locale_file) + ".yml";

        // Reload the files
        config.reload();
        messages.reload();

        // Get new locale file.
        String newLocale = config.getProperty(ConfigKeys.locale_file) + ".yml";

        if (!oldLocale.equals(newLocale)) {
            File localeDir = new File(dataFolder, "locale");

            locale(localeDir);
        }

        // Refresh files.
        getYamlManager().reloadFiles();

        // Refresh custom files.
        getYamlManager().reloadCustomFiles();
    }

    private static void locale(File localeDir) {
        if (!localeDir.exists()) {
            FileUtil.extracts(ConfigManager.class, "/locale/", localeDir.toPath(), true);

            localeDir.mkdirs();
        }

        messages = SettingsManagerBuilder
                .withYamlFile(new File(localeDir, config.getProperty(ConfigKeys.locale_file) + ".yml"), builder)
                //.migrationService(new LocaleMigration())
                .configurationData(MessageKeys.class)
                .create();
    }

    /**
     * @return gets config.yml
     */
    public static SettingsManager getConfig() {
        return config;
    }

    /**
     * @return gets locale file
     */
    public static SettingsManager getMessages() {
        return messages;
    }

    /**
     * @return yamlmanager object
     */
    public static YamlManager getYamlManager() {
        return yamlManager;
    }
}