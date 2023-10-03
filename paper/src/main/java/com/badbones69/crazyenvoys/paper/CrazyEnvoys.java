package com.badbones69.crazyenvoys.paper;

import com.badbones69.crazyenvoys.paper.api.CrazyManager;
import com.badbones69.crazyenvoys.paper.api.FileManager;
import com.badbones69.crazyenvoys.paper.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.paper.api.events.EnvoyEndEvent.EnvoyEndReason;
import com.badbones69.crazyenvoys.paper.api.objects.CoolDownSettings;
import com.badbones69.crazyenvoys.paper.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.paper.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.paper.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.paper.commands.EnvoyCommand;
import com.badbones69.crazyenvoys.paper.commands.EnvoyTab;
import com.badbones69.crazyenvoys.paper.listeners.EnvoyEditListener;
import com.badbones69.crazyenvoys.paper.listeners.EnvoyClickListener;
import com.badbones69.crazyenvoys.paper.listeners.FireworkDamageListener;
import com.badbones69.crazyenvoys.paper.listeners.FlareClickListener;
import com.badbones69.crazyenvoys.paper.support.libraries.PluginSupport;
import com.badbones69.crazyenvoys.paper.support.SkullCreator;
import com.badbones69.crazyenvoys.paper.support.placeholders.PlaceholderAPISupport;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.common.config.types.PluginConfig;
import us.crazycrew.crazyenvoys.paper.api.plugin.CrazyHandler;
import java.util.List;

public class CrazyEnvoys extends JavaPlugin {

    private Methods methods;

    // Envoy Required Classes.
    private EditorSettings editorSettings;
    private FlareSettings flareSettings;
    private CoolDownSettings coolDownSettings;
    private LocationSettings locationSettings;

    private CrazyManager crazyManager;

    private SkullCreator skullCreator;

    private CrazyHandler crazyHandler;

    @Override
    public void onEnable() {
        this.crazyHandler = new CrazyHandler(getDataFolder());
        this.crazyHandler.install();

        this.methods = new Methods();

        this.locationSettings = new LocationSettings();
        this.editorSettings = new EditorSettings();
        this.coolDownSettings = new CoolDownSettings();
        this.flareSettings = new FlareSettings();

        this.crazyManager = new CrazyManager();
        this.skullCreator = new SkullCreator();

        this.crazyManager.load(true);

        enable();
    }

    @Override
    public void onDisable() {
        this.crazyHandler.uninstall();

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

        //this.crazyManager.unload();
    }

    public @NotNull CrazyHandler getCrazyHandler() {
        return this.crazyHandler;
    }

    public boolean isLogging() {
        return this.crazyHandler.getConfigManager().getPluginConfig().getProperty(PluginConfig.verbose_logging);
    }

    private void enable() {
        getServer().getPluginManager().registerEvents(new EnvoyEditListener(), this);
        getServer().getPluginManager().registerEvents(new EnvoyClickListener(), this);
        getServer().getPluginManager().registerEvents(new FlareClickListener(), this);
        getServer().getPluginManager().registerEvents(new FireworkDamageListener(), this);

        if (PluginSupport.PLACEHOLDER_API.isPluginEnabled()) {
            List.of(
                    "We no longer support MVDWPlaceholderAPI",
                    "You are getting this message because PlaceholderAPI is enabled",
                    "If you are already using our new placeholders like %crazyenvoys_cooldown%",
                    "You can ignore this message but anyone else using {crazyenvoys_cooldown} must update."
            ).forEach(LegacyLogger::warn);

            new PlaceholderAPISupport().register();
        }

        registerCommand(getCommand("crazyenvoys"), new EnvoyTab(), new EnvoyCommand());
    }

    private void registerCommand(PluginCommand pluginCommand, TabCompleter tabCompleter, CommandExecutor commandExecutor) {
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);

            if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        }
    }

    public FileManager getFileManager() {
        return this.crazyHandler.getFileManager();
    }

    public Methods getMethods() {
        return this.methods;
    }

    // Envoy Required Classes.
    public EditorSettings getEditorSettings() {
        return this.editorSettings;
    }

    public FlareSettings getFlareSettings() {
        return this.flareSettings;
    }

    public CoolDownSettings getCoolDownSettings() {
        return this.coolDownSettings;
    }

    public LocationSettings getLocationSettings() {
        return this.locationSettings;
    }

    public CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    public SkullCreator getSkullCreator() {
        return this.skullCreator;
    }
}