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
import com.ryderbelserion.vital.paper.VitalPaper;
import com.ryderbelserion.vital.paper.api.enums.Support;
import com.ryderbelserion.vital.paper.api.files.FileManager;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.support.MetricsWrapper;
import java.util.Locale;

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

    private HeadDatabaseAPI api;

    private FileManager fileManager;

    private VitalPaper paper;

    @Override
    public void onEnable() {
        this.paper = new VitalPaper(this);

        ConfigManager.load(getDataFolder(), getComponentLogger());

        if (Support.head_database.isEnabled()) {
            this.api = new HeadDatabaseAPI();
        }

        this.fileManager = new FileManager();
        this.fileManager.addFile("users.yml").addFolder("tiers").init();

        new MetricsWrapper(4514);

        this.locationSettings = new LocationSettings();
        this.editorSettings = new EditorSettings();
        this.coolDownSettings = new CoolDownSettings();
        this.flareSettings = new FlareSettings();

        this.crazyManager = new CrazyManager();
        this.crazyManager.load();

        getServer().getPluginManager().registerEvents(new EnvoyEditListener(), this);
        getServer().getPluginManager().registerEvents(new EnvoyClickListener(), this);
        getServer().getPluginManager().registerEvents(new FlareClickListener(), this);
        getServer().getPluginManager().registerEvents(new FireworkDamageListener(), this);

        getServer().getPluginManager().registerEvents(new PrizeGui(), this);

        if (Support.placeholder_api.isEnabled()) {
            new PlaceholderAPISupport().register();
        }

        registerCommand(getCommand("crazyenvoys"), new EnvoyTab(), new EnvoyCommand());

        if (isLogging()) {
            getComponentLogger().info("Done ({})!", String.format(Locale.ROOT, "%.3fs", (double) (System.nanoTime() - this.startTime) / 1.0E9D));
        }
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

    public final boolean isLogging() {
        return this.paper.isVerbose();
    }

    public final VitalPaper getPaper() {
        return this.paper;
    }

    private void registerCommand(PluginCommand pluginCommand, TabCompleter tabCompleter, CommandExecutor commandExecutor) {
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);

            if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        }
    }

    public @Nullable final HeadDatabaseAPI getApi() {
        if (this.api == null) {
            return null;
        }

        return this.api;
    }

    public final FileManager getFileManager() {
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