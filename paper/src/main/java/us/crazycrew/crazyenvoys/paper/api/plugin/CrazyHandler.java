package us.crazycrew.crazyenvoys.paper.api.plugin;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.api.FileManager;
import com.ryderbelserion.cluster.bukkit.BukkitPlugin;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.common.CrazyEnvoysPlugin;
import us.crazycrew.crazyenvoys.common.config.ConfigManager;
import us.crazycrew.crazyenvoys.common.config.types.Config;
import us.crazycrew.crazyenvoys.common.config.types.PluginConfig;
import us.crazycrew.crazyenvoys.paper.support.MetricsHandler;

import java.io.File;

public class CrazyHandler extends CrazyEnvoysPlugin {

    private final @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private BukkitPlugin bukkitPlugin;
    private MetricsHandler metrics;
    private FileManager fileManager;

    public CrazyHandler(File dataFolder) {
        super(dataFolder);
    }

    public void install() {
        this.bukkitPlugin = new BukkitPlugin(this.plugin);
        this.bukkitPlugin.enable();

        super.enable();

        LegacyLogger.setName(getConfigManager().getPluginConfig().getProperty(PluginConfig.console_prefix));

        this.fileManager = new FileManager();
        this.fileManager.registerCustomFilesFolder("/tiers")
                .registerDefaultGenerateFiles("Basic.yml", "/tiers", "/tiers")
                .registerDefaultGenerateFiles("Lucky.yml", "/tiers", "/tiers")
                .registerDefaultGenerateFiles("Titan.yml", "/tiers", "/tiers")
                .setup();

        boolean metrics = this.plugin.getCrazyHandler().getConfigManager().getConfig().getProperty(Config.toggle_metrics);

        this.metrics = new MetricsHandler();
        if (metrics) this.metrics.start();
    }

    public void uninstall() {
        // Disable crazyenvoys api.
        super.disable();

        // Disable cluster bukkit api.
        this.bukkitPlugin.disable();
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

    public @NotNull MetricsHandler getMetrics() {
        return this.metrics;
    }
}