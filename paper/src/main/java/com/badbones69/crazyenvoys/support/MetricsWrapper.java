package com.badbones69.crazyenvoys.support;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import org.bstats.bukkit.Metrics;

public class MetricsWrapper {

    private final CrazyEnvoys plugin = CrazyEnvoys.get();

    public MetricsWrapper(final int serviceId) {
        new Metrics(this.plugin, serviceId);
    }
}