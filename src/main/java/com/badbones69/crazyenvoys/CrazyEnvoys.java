package com.badbones69.crazyenvoys;

import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.FileManager;
import com.badbones69.crazyenvoys.api.FileManager.Files;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent.EnvoyEndReason;
import com.badbones69.crazyenvoys.api.objects.*;
import com.badbones69.crazyenvoys.commands.EnvoyCommand;
import com.badbones69.crazyenvoys.commands.EnvoyTab;
import com.badbones69.crazyenvoys.controllers.EditControl;
import com.badbones69.crazyenvoys.controllers.EnvoyControl;
import com.badbones69.crazyenvoys.controllers.FireworkDamageAPI;
import com.badbones69.crazyenvoys.controllers.FlareControl;
import com.badbones69.crazyenvoys.support.PluginSupport;
import com.badbones69.crazyenvoys.support.SkullCreator;
import com.badbones69.crazyenvoys.support.placeholders.PlaceholderAPISupport;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnvoys extends JavaPlugin implements Listener {

    private static CrazyEnvoys plugin;

    private final PluginManager pluginManager = getServer().getPluginManager();

    private CrazyManager crazyManager;

    private FileManager fileManager;

    private Methods methods;

    private SkullCreator skullCreator;

    private FireworkDamageAPI fireworkDamageAPI;

    // Envoy Required Classes
    private LocationSettings locationSettings;
    private EditorSettings editorSettings;
    private EnvoySettings envoySettings;
    private FlareSettings flareSettings;
    private CoolDownSettings coolDownSettings;

    private boolean isEnabled = false;

    @Override
    public void onEnable() {

        try {
            plugin = this;

            fileManager = new FileManager();

            fileManager.logInfo(true)
                    .registerCustomFilesFolder("/tiers")
                    .registerDefaultGenerateFiles("Basic.yml", "/tiers", "/tiers")
                    .registerDefaultGenerateFiles("Lucky.yml", "/tiers", "/tiers")
                    .registerDefaultGenerateFiles("Titan.yml", "/tiers", "/tiers")
                    .setup();

            pluginManager.registerEvents(fireworkDamageAPI = new FireworkDamageAPI(), this);

            locationSettings = new LocationSettings();
            editorSettings = new EditorSettings();
            coolDownSettings = new CoolDownSettings();

            envoySettings = new EnvoySettings();

            flareSettings = new FlareSettings();

            methods = new Methods();

            crazyManager = new CrazyManager();

            skullCreator = new SkullCreator();

            crazyManager.load();

            Messages.addMissingMessages();

            FileConfiguration config = Files.CONFIG.getFile();

            String metricsPath = config.getString("Settings.Toggle-Metrics");

            boolean metricsEnabled = config.getBoolean("Settings.Toggle-Metrics");

            String countDownSetting = config.getString("Settings.Crate-Countdown.Toggle");

            if (metricsPath == null) {
                config.set("Settings.Toggle-Metrics", true);
                Files.CONFIG.saveFile();
            }

            if (metricsEnabled) new Metrics(this, 4537);

            if (countDownSetting == null) {
                config.set("Settings.Crate-Countdown.Toggle", false);
                config.addDefault("Settings.Crate-Countdown.Time", 120);
                Files.CONFIG.saveFile();
            }

            if (PluginSupport.PLACEHOLDER_API.isPluginLoaded()) new PlaceholderAPISupport().register();
        } catch (Exception exception) {
            exception.printStackTrace();

            isEnabled = false;
        }

        enable();

        isEnabled = true;
    }

    @Override
    public void onDisable() {
        if (!isEnabled) return;

        for (Player player : getServer().getOnlinePlayers()) {
            if (editorSettings.isEditor(player)) {
                editorSettings.removeEditor(player);
                editorSettings.removeFakeBlocks();
            }
        }

        if (crazyManager.isEnvoyActive()) {
            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.SHUTDOWN);

            getServer().getPluginManager().callEvent(event);

            crazyManager.endEnvoyEvent();
        }

        crazyManager.unload();
    }

    private void enable() {

        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(new EditControl(), this);
        pluginManager.registerEvents(new EnvoyControl(), this);
        pluginManager.registerEvents(new FlareControl(), this);

        if (PluginSupport.PLACEHOLDER_API.isPluginLoaded()) {
            getLogger().warning("We no longer support placeholders using {}");
            getLogger().warning("We only support %% placeholders i.e %crazyenvoys_cooldown%");
            new PlaceholderAPISupport().register();
        }

        getCommand("envoy").setExecutor(new EnvoyCommand());
        getCommand("envoy").setTabCompleter(new EnvoyTab());
    }

    public static CrazyEnvoys getPlugin() {
        return plugin;
    }

    public CrazyManager getCrazyManager() {
        return crazyManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Methods getMethods() {
        return methods;
    }

    public SkullCreator getSkullCreator() {
        return skullCreator;
    }

    public FireworkDamageAPI getFireworkDamageAPI() {
        return fireworkDamageAPI;
    }

    // Envoy Required Classes

    public LocationSettings getLocationSettings() {
        return locationSettings;
    }

    public EditorSettings getEditorSettings() {
        return editorSettings;
    }

    public FlareSettings getFlareSettings() {
        return flareSettings;
    }

    public EnvoySettings getEnvoySettings() {
        return envoySettings;
    }

    public CoolDownSettings getCoolDownSettings() {
        return coolDownSettings;
    }

    // Controllers
}