package com.badbones69.crazyenvoys.support;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import org.bstats.bukkit.Metrics;

public class MetricsHandler {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    public void start() {
        new Metrics(plugin, 4537);

        plugin.getLogger().info("Metrics has been enabled.");
    }
}