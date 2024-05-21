package com.badbones69.crazyenvoys.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazyenvoys.config.impl.ConfigKeys;
import com.badbones69.crazyenvoys.config.impl.MessageKeys;
import com.badbones69.crazyenvoys.config.impl.locale.ErrorKeys;
import com.badbones69.crazyenvoys.config.impl.locale.MiscKeys;
import com.badbones69.crazyenvoys.config.impl.locale.PlayerKeys;
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
    public static void load() {
        if (yamlManager == null) {
            yamlManager = new YamlManager();
        }

        // Create plugin directory
        yamlManager.createPluginDirectory();

        // Create config.yml
        config = SettingsManagerBuilder
                .withYamlFile(yamlManager.getDataFolder().resolve("config.yml"), builder)
                .useDefaultMigrationService()
                //.migrationService(new ConfigMigration())
                .configurationData(ConfigKeys.class)
                .create();

        // Update locale file
        locale(config.getProperty(ConfigKeys.locale_file) + ".yml");

        // Add files
        yamlManager.addFile("users.yml")
                .addFolder("rewards")
                .addFolder("tiers")
                .init();
    }

    /**
     * Refreshes configuration files.
     */
    public static void refresh() {
        // Get old locale file.
        String oldLocale = config.getProperty(ConfigKeys.locale_file) + ".yml";

        // Reload the files
        config.reload();

        // Get new locale file.
        String newLocale = config.getProperty(ConfigKeys.locale_file) + ".yml";

        if (!oldLocale.equals(newLocale)) locale(newLocale); else messages.reload();

        // Refresh files.
        getYamlManager().reloadFiles();

        // Refresh custom files.
        getYamlManager().reloadCustomFiles();
    }

    private static void locale(String path) {
        File locale = yamlManager.getDataFolder().resolve("locale").toFile();

        if (!locale.exists()) {
            FileUtil.extracts(ConfigManager.class, "/locale/", locale.toPath(), true);

            locale.mkdirs();
        }

        messages = SettingsManagerBuilder
                .withYamlFile(new File(locale, path), builder)
                .useDefaultMigrationService()
                //.migrationService(new LocaleMigration())
                .configurationData(PlayerKeys.class, ErrorKeys.class, MiscKeys.class, MessageKeys.class)
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