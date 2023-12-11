package us.crazycrew.crazyenvoys.support;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import org.bstats.bukkit.Metrics;
import org.jetbrains.annotations.NotNull;

public class MetricsHandler {

    private final @NotNull CrazyEnvoys plugin = CrazyEnvoys.get();

    private Metrics metrics;

    public void start() {
        if (this.metrics != null) {
            if (this.plugin.isLogging()) this.plugin.getLogger().info("Metrics is already enabled.");
            return;
        }

        this.metrics = new Metrics(this.plugin, 4537);

        if (this.plugin.isLogging()) this.plugin.getLogger().info("Metrics has been enabled.");
    }

    public void stop() {
        if (this.metrics == null) {
            if (this.plugin.isLogging()) this.plugin.getLogger().warning("Metrics isn't enabled so we do nothing.");
            return;
        }

        this.metrics.shutdown();
        this.metrics = null;

        if (this.plugin.isLogging()) this.plugin.getLogger().info("Metrics has been turned off.");
    }
}