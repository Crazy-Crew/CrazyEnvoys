package com.badbones69.crazyenvoys.commands.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.CrazyEnvoysPlatform;
import com.badbones69.crazyenvoys.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.api.registry.PaperUserRegistry;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.registry.EnvoyRegistry;
import com.badbones69.crazyenvoys.storage.impl.objects.StorageHolder;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NonNull;

@Command(value = "crazyenvoys", alias = {"envoys", "envoy"})
public class EnvoyCommand {

    protected @NonNull final CrazyEnvoys plugin = CrazyEnvoys.get();

    protected @NonNull final CrazyEnvoysPlatform platform = this.plugin.getPlatform();

    protected @NonNull final StorageHolder holder = this.platform.getStorageHolder();

    protected @NonNull final EnvoyRegistry envoyRegistry = this.platform.getEnvoyRegistry();

    protected @NonNull final PaperUserRegistry userRegistry = this.platform.getUserRegistry();

    protected @NonNull final FusionPaper fusion = this.platform.getFusion();

    protected @NonNull final PaperFileManager fileManager = this.fusion.getFileManager();

    protected @NonNull final Server server = this.plugin.getServer();

    protected @NonNull final PluginManager pluginManager = this.server.getPluginManager();

    protected @NonNull final CrazyManager crazyManager = this.plugin.getCrazyManager();

    protected @NonNull final LocationSettings locationSettings = this.plugin.getLocationSettings();

    protected @NonNull final FlareSettings flareSettings = this.plugin.getFlareSettings();

    protected @NonNull final SettingsManager config = ConfigManager.getConfig();

}