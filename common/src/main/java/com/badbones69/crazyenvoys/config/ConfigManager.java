package com.badbones69.crazyenvoys.config;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.api.enums.CustomFiles;
import com.badbones69.crazyenvoys.config.impl.ConfigKeys;
import com.badbones69.crazyenvoys.config.impl.MessageKeys;
import com.ryderbelserion.vital.common.configuration.YamlManager;
import org.jetbrains.annotations.ApiStatus;

public class ConfigManager {

    private static YamlManager yamlManager;

    /**
     * Loads configuration files.
     */
    @ApiStatus.Internal
    public static void load() {
        if (yamlManager == null) yamlManager = new YamlManager();

        // Create directory
        yamlManager.createPluginDirectory();

        // Add files
        yamlManager
                .addFile("config.yml", ConfigKeys.class)
                .addFile("locale", CustomFiles.config.getSettingsManager().getProperty(ConfigKeys.locale_file) + ".yml", MessageKeys.class)
                .addStaticFile("users.yml")
                .addFolder("tiers")
                .init();
    }

    /**
     * Refreshes configuration files.
     */
    public static void refresh() {
        yamlManager.removeFile(CustomFiles.config.getSettingsManager().getProperty(ConfigKeys.locale_file) + ".yml", false);

        // Refresh configme files.
        getYamlManager().reloadFiles();

        yamlManager.addFile("locale", CustomFiles.config.getSettingsManager().getProperty(ConfigKeys.locale_file) + ".yml", MessageKeys.class);

        // Refresh custom files.
        getYamlManager().reloadCustomFiles();

        // Refresh other files.
        getYamlManager().reloadStaticFiles();
    }

    /**
     * @return gets config.yml
     */
    public static SettingsManager getConfig() {
        return CustomFiles.config.getSettingsManager();
    }

    /**
     * @return gets messages.yml
     */
    public static SettingsManager getMessages() {
        return yamlManager.getFile(getConfig().getProperty(ConfigKeys.locale_file) + ".yml");
    }

    /**
     * @return yamlmanager object
     */
    public static YamlManager getYamlManager() {
        return yamlManager;
    }
}