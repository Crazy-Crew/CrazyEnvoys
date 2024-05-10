package com.badbones69.crazyenvoys;

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
import com.badbones69.crazyenvoys.platform.config.ConfigManager;
import com.badbones69.crazyenvoys.platform.util.MiscUtil;
import com.badbones69.crazyenvoys.support.placeholders.PlaceholderAPISupport;
import com.ryderbelserion.vital.enums.Support;
import com.ryderbelserion.vital.files.yaml.FileManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CrazyEnvoys extends JavaPlugin {

    private CoolDownSettings coolDownSettings;
    private LocationSettings locationSettings;
    private EditorSettings editorSettings;
    private FlareSettings flareSettings;

    private CrazyManager crazyManager;
    private FileManager fileManager;

    @Override
    public void onEnable() {
        ConfigManager.load(getDataFolder());

        this.locationSettings = new LocationSettings();
        this.editorSettings = new EditorSettings();
        this.coolDownSettings = new CoolDownSettings();
        this.flareSettings = new FlareSettings();

        this.fileManager = new FileManager(getDataFolder().toPath(), getLogger());
        this.fileManager
                .addDefaultFile("tiers", "Basic.yml")
                .addDefaultFile("tiers", "Lucky.yml")
                .addDefaultFile("tiers", "Titan.yml")
                .apply();;

        this.crazyManager = new CrazyManager();
        this.crazyManager.load();

        getServer().getPluginManager().registerEvents(new EnvoyEditListener(), this);
        getServer().getPluginManager().registerEvents(new EnvoyClickListener(), this);
        getServer().getPluginManager().registerEvents(new FlareClickListener(), this);
        getServer().getPluginManager().registerEvents(new FireworkDamageListener(), this);

        if (Support.placeholder_api.isEnabled()) {
            new PlaceholderAPISupport().register();
        }

        MiscUtil.registerCommand(getCommand("crazyenvoys"), new EnvoyTab(), new EnvoyCommand());
    }

    @Override
    public void onDisable() {
        // Cancel the tasks
        getServer().getGlobalRegionScheduler().cancelTasks(this);
        getServer().getAsyncScheduler().cancelTasks(this);

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

    public @NotNull final LocationSettings getLocationSettings() {
        return this.locationSettings;
    }
    public @NotNull final CoolDownSettings getCoolDownSettings() {
        return this.coolDownSettings;
    }
    public @NotNull final EditorSettings getEditorSettings() {
        return this.editorSettings;
    }
    public @NotNull final FlareSettings getFlareSettings() {
        return this.flareSettings;
    }

    public @NotNull final CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    public @NotNull final FileManager getFileManager() {
        return this.fileManager;
    }
}