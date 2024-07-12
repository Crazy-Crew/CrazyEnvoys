package com.badbones69.crazyenvoys.api;

import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.config.types.ConfigKeys;
import com.ryderbelserion.vital.core.Vital;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.io.File;

public class Server extends Vital {

    private final File dataFolder;
    private final ComponentLogger logger;

    public Server(final JavaPlugin plugin) {
        this.dataFolder = plugin.getDataFolder();
        this.logger = plugin.getComponentLogger();

        ConfigManager.load(this.dataFolder, this.logger);
    }

    @Override
    public @NotNull File getDirectory() {
        return this.dataFolder;
    }

    @Override
    public @NotNull ComponentLogger getLogger() {
        return this.logger;
    }

    @Override
    public boolean isLogging() {
        return ConfigManager.getConfig().getProperty(ConfigKeys.verbose_logging);
    }
}