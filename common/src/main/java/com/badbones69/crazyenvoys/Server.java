package com.badbones69.crazyenvoys;

import com.badbones69.crazyenvoys.config.ConfigManager;
import com.ryderbelserion.vital.common.AbstractPlugin;
import com.ryderbelserion.vital.common.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class Server extends AbstractPlugin {

    private final File directory;
    private final Logger logger;
    private final File tiers;

    public Server(@NotNull final File directory, @NotNull final Logger logger) {
        this.directory = directory;
        this.tiers = new File(this.directory, "tiers");
        this.logger = logger;
    }

    /**
     * Loads the plugin.
     */
    public void apply() {
        ConfigManager.load();
    }

    /**
     * Reloads the plugin.
     */
    public void reload() {
        ConfigManager.refresh();
    }

    /**
     * @return the crates folder
     */
    public @NotNull final File getTierFolder() {
        return this.tiers;
    }

    /**
     * @return the list of files in the tiers folder
     */
    public @NotNull final List<String> getTierFiles() {
        return FileUtil.getFiles(getTierFolder().toPath(), ".yml", true);
    }

    /**
     * @return the plugin directory
     */
    @Override
    public @NotNull final File getDirectory() {
        return this.directory;
    }

    /**
     * @return the plugin logger
     */
    @Override
    public @NotNull final Logger getLogger() {
        return this.logger;
    }
}