package us.crazycrew.crazyenvoys.core.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.configuration.file.YamlConfiguration;
import us.crazycrew.crazyenvoys.core.config.migrate.ConfigMigration;
import us.crazycrew.crazyenvoys.core.config.migrate.LocaleMigration;
import us.crazycrew.crazyenvoys.core.config.types.ConfigKeys;
import us.crazycrew.crazyenvoys.core.config.types.MessageKeys;
import java.io.File;
import java.util.concurrent.CompletableFuture;

public class ConfigManager {

    private static SettingsManager messages;
    private static SettingsManager config;

    public static void load(final File dataFolder, final ComponentLogger logger) {
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        File configFile = new File(dataFolder, "config.yml");

        config = SettingsManagerBuilder
                .withYamlFile(configFile, builder)
                .migrationService(new ConfigMigration())
                .configurationData(ConfigurationDataBuilder.createConfiguration(ConfigKeys.class))
                .create();

        copyPluginConfig(dataFolder, logger, config);

        File localeDir = new File(dataFolder, "locale");

        if (!localeDir.exists()) localeDir.mkdirs();

        File messagesFile = new File(localeDir, config.getProperty(ConfigKeys.locale_file) + ".yml");

        // Migrate if this is found.
        File oldFile = new File(dataFolder, "Messages.yml");
        if (oldFile.exists() && !messagesFile.exists()) {
            oldFile.renameTo(messagesFile);
        }

        messages = SettingsManagerBuilder
                .withYamlFile(messagesFile, builder)
                .migrationService(new LocaleMigration())
                .configurationData(MessageKeys.class)
                .create();

        File file = new File(dataFolder, "data.yml");
        if (file.exists()) file.renameTo(new File(dataFolder, "users.yml"));
    }

    public static void refresh() {
        config.reload();
        messages.reload();
    }

    public static SettingsManager getConfig() {
        return config;
    }

    public static SettingsManager getMessages() {
        return messages;
    }

    private static void copyPluginConfig(final File dataFolder, final ComponentLogger logger, final SettingsManager config) {
        File input = new File(dataFolder, "plugin-config.yml");

        if (!input.exists()) return;

        YamlConfiguration configuration = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        String language = configuration.getString("language", "en-US");

        boolean verbose = configuration.getBoolean("verbose_logging", false);

        String prefix = configuration.getString("command_prefix", "&8[&dCrazyEnvoys&8]: ");
        String consolePrefix = configuration.getString("console_prefix", "&8[&cCrazyEnvoys&8] ");

        config.setProperty(ConfigKeys.locale_file, language);
        config.setProperty(ConfigKeys.verbose_logging, verbose);
        config.setProperty(ConfigKeys.command_prefix, prefix);
        config.setProperty(ConfigKeys.console_prefix, consolePrefix);

        // Save to file.
        config.save();

        // Delete old file.
        if (input.delete()) logger.warn("Successfully migrated {}.yml", input.getName());
    }
}