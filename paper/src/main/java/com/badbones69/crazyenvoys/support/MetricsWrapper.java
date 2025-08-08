package com.badbones69.crazyenvoys.support;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import org.bstats.bukkit.Metrics;

public class MetricsWrapper {

    public MetricsWrapper(final int serviceId) {
        new Metrics(CrazyEnvoys.get(), serviceId);
    }
}