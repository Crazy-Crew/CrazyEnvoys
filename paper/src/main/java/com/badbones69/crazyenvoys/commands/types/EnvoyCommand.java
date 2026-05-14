package com.badbones69.crazyenvoys.commands.types;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NonNull;

@Command(value = "crazyenvoys", alias = {"envoys", "envoy"})
public class EnvoyCommand {

    protected @NonNull final CrazyEnvoys plugin = CrazyEnvoys.get();

    protected @NonNull final PaperFileManager fileManager = this.plugin.getFileManager();

    protected @NonNull final Server server = this.plugin.getServer();

    protected @NonNull final PluginManager pluginManager = this.server.getPluginManager();

    protected @NonNull final CrazyManager crazyManager = this.plugin.getCrazyManager();

    protected @NonNull final FusionPaper fusion = this.plugin.getFusion();

    protected @NonNull final EditorSettings editorSettings = this.plugin.getEditorSettings();

    protected @NonNull final LocationSettings locationSettings = this.plugin.getLocationSettings();

    protected @NonNull final FlareSettings flareSettings = this.plugin.getFlareSettings();

    protected @NonNull final SettingsManager config = ConfigManager.getConfig();

}