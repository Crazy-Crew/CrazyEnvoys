package com.badbones69.crazyenvoys.api.plugin;

import com.badbones69.crazyenvoys.api.FileManager;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.common.CrazyEnvoysPlugin;
import us.crazycrew.crazyenvoys.common.config.ConfigManager;
import com.badbones69.crazyenvoys.api.plugin.migration.MigrationService;

import java.io.File;

public class CrazyHandler extends CrazyEnvoysPlugin {

    private FileManager fileManager;

    public CrazyHandler(File dataFolder) {
        super(dataFolder);
    }

    public void install() {
        MigrationService migrationService = new MigrationService();
        migrationService.migrate();

        super.enable();

        this.fileManager = new FileManager();
        this.fileManager.registerCustomFilesFolder("/tiers")
                .registerDefaultGenerateFiles("Basic.yml", "/tiers", "/tiers")
                .registerDefaultGenerateFiles("Lucky.yml", "/tiers", "/tiers")
                .registerDefaultGenerateFiles("Titan.yml", "/tiers", "/tiers")
                .setup();
    }

    public void uninstall() {
        // Disable crazyenvoys api.
        super.disable();
    }

    /**
     * Inherited methods.
     */
    @Override
    public @NotNull ConfigManager getConfigManager() {
        return super.getConfigManager();
    }

    public @NotNull FileManager getFileManager() {
        return this.fileManager;
    }
}