package com.badbones69.crazyenvoys;

import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.CrazyEnvoysPlatform;
import com.badbones69.crazyenvoys.api.objects.CoolDownSettings;
import com.badbones69.crazyenvoys.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class CrazyEnvoys extends JavaPlugin {

    @NotNull
    public static CrazyEnvoys get() {
        return JavaPlugin.getPlugin(CrazyEnvoys.class);
    }

    private CrazyEnvoysPlatform platform;

    private CoolDownSettings coolDownSettings;
    private LocationSettings locationSettings;
    private FlareSettings flareSettings;

    private CrazyManager crazyManager;

    @Override
    public void onEnable() {
        this.platform = new CrazyEnvoysPlatform(this, new FusionPaper(this));
        this.platform.init();

        this.locationSettings = new LocationSettings();
        this.coolDownSettings = new CoolDownSettings();
        this.flareSettings = new FlareSettings();

        this.crazyManager = new CrazyManager();
        this.crazyManager.load();
    }

    @Override
    public void onDisable() {
        this.platform.stop();
    }

    public @NonNull final CrazyEnvoysPlatform getPlatform() {
        return this.platform;
    }

    public final CoolDownSettings getCoolDownSettings() {
        return this.coolDownSettings;
    }

    public final LocationSettings getLocationSettings() {
        return this.locationSettings;
    }

    public final FlareSettings getFlareSettings() {
        return this.flareSettings;
    }

    public final CrazyManager getCrazyManager() {
        return this.crazyManager;
    }
}