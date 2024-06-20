package us.crazycrew.crazyenvoys.core;

import com.ryderbelserion.vital.core.Vital;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.core.config.ConfigManager;
import us.crazycrew.crazyenvoys.core.config.types.ConfigKeys;
import java.io.File;
import java.util.logging.Logger;

public class Server extends Vital {

    private final File dataFolder;
    private final Logger logger;

    public Server(final JavaPlugin plugin) {
        this.dataFolder = plugin.getDataFolder();
        this.logger = plugin.getLogger();

        ConfigManager.load(this.dataFolder, this.logger);
    }

    @Override
    public @NotNull File getDirectory() {
        return this.dataFolder;
    }

    @Override
    public @NotNull Logger getLogger() {
        return this.logger;
    }

    @Override
    public boolean isLogging() {
        return ConfigManager.getConfig().getProperty(ConfigKeys.verbose_logging);
    }
}