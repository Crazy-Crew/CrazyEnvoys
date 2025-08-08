package com.badbones69.crazyenvoys;

import com.badbones69.crazyenvoys.api.builders.types.PrizeGui;
import com.badbones69.crazyenvoys.commands.EnvoyCommand;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent.EnvoyEndReason;
import com.badbones69.crazyenvoys.api.objects.CoolDownSettings;
import com.badbones69.crazyenvoys.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.commands.EnvoyTab;
import com.badbones69.crazyenvoys.listeners.EnvoyEditListener;
import com.badbones69.crazyenvoys.listeners.EnvoyClickListener;
import com.badbones69.crazyenvoys.listeners.FireworkDamageListener;
import com.badbones69.crazyenvoys.listeners.FlareClickListener;
import com.badbones69.crazyenvoys.support.placeholders.PlaceholderAPISupport;
import com.ryderbelserion.fusion.core.api.support.ModSupport;
import com.ryderbelserion.fusion.core.files.enums.FileAction;
import com.ryderbelserion.fusion.core.files.enums.FileType;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.support.MetricsWrapper;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

public class CrazyEnvoys extends JavaPlugin {

    @NotNull
    public static CrazyEnvoys get() {
        return JavaPlugin.getPlugin(CrazyEnvoys.class);
    }

    private final long startTime;

    public CrazyEnvoys() {
        this.startTime = System.nanoTime();
    }

    private EditorSettings editorSettings;
    private FlareSettings flareSettings;
    private CoolDownSettings coolDownSettings;
    private LocationSettings locationSettings;

    private CrazyManager crazyManager;

    private PaperFileManager fileManager;
    private FusionPaper fusion;

    @Override
    public void onEnable() {
        this.fusion = new FusionPaper(this);

        final Path path = getDataPath();

        ConfigManager.load(getDataFolder(), getComponentLogger());

        this.fileManager = this.fusion.getFileManager();
        this.fileManager.addPaperFile(path.resolve("users.yml"), consumer -> consumer.addAction(FileAction.EXTRACT_FILE))
            .addFolder(path.resolve("tiers"), FileType.PAPER);

        new MetricsWrapper(4514);

        this.locationSettings = new LocationSettings();
        this.editorSettings = new EditorSettings();
        this.coolDownSettings = new CoolDownSettings();
        this.flareSettings = new FlareSettings();

        this.crazyManager = new CrazyManager();
        this.crazyManager.load();

        final PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new EnvoyEditListener(), this);
        pluginManager.registerEvents(new EnvoyClickListener(), this);
        pluginManager.registerEvents(new FlareClickListener(), this);
        pluginManager.registerEvents(new FireworkDamageListener(), this);

        pluginManager.registerEvents(new PrizeGui(), this);

        if (this.fusion.isModReady(ModSupport.placeholder_api)) {
            new PlaceholderAPISupport().register();
        }

        registerCommand(getCommand("crazyenvoys"), new EnvoyTab(), new EnvoyCommand());

        this.fusion.log("info", "Done ({})!", String.format(Locale.ROOT, "%.3fs", (double) (System.nanoTime() - this.startTime) / 1.0E9D));
    }

    @Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            if (this.editorSettings.isEditor(player)) {
                this.editorSettings.removeEditor(player);
                this.editorSettings.removeFakeBlocks();
            }
        }

        if (this.crazyManager.isEnvoyActive()) {
            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.SHUTDOWN);

            getServer().getPluginManager().callEvent(event);

            this.crazyManager.endEnvoyEvent();
        }

        this.crazyManager.reload(true);
    }

    public FusionPaper getFusion() {
        return this.fusion;
    }

    private void registerCommand(PluginCommand pluginCommand, TabCompleter tabCompleter, CommandExecutor commandExecutor) {
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);

            if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        }
    }

    public final Optional<HeadDatabaseAPI> getApi() {
        return this.fusion.getHeadDatabaseAPI();
    }

    public final PaperFileManager getFileManager() {
        return this.fileManager;
    }

    public final EditorSettings getEditorSettings() {
        return this.editorSettings;
    }

    public final FlareSettings getFlareSettings() {
        return this.flareSettings;
    }

    public final CoolDownSettings getCoolDownSettings() {
        return this.coolDownSettings;
    }

    public final LocationSettings getLocationSettings() {
        return this.locationSettings;
    }

    public final CrazyManager getCrazyManager() {
        return this.crazyManager;
    }
}