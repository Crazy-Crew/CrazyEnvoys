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
import com.badbones69.crazyenvoys.controllers.*;
import com.badbones69.crazyenvoys.support.MetricsHandler;
import com.badbones69.crazyenvoys.support.libraries.PluginSupport;
import com.badbones69.crazyenvoys.support.SkullCreator;
import com.badbones69.crazyenvoys.support.libraries.UpdateChecker;
import com.badbones69.crazyenvoys.support.placeholders.PlaceholderAPISupport;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnvoys extends JavaPlugin implements Listener {

    private static CrazyEnvoys plugin;

    private final PluginManager pluginManager = getServer().getPluginManager();

    private FileManager fileManager;

    private Methods methods;

    // Envoy Required Classes.
    private EditorSettings editorSettings;
    private EnvoySettings envoySettings;
    private FlareSettings flareSettings;
    private CoolDownSettings coolDownSettings;
    private LocationSettings locationSettings;

    private CrazyManager crazyManager;

    private SkullCreator skullCreator;

    private FireworkDamageAPI fireworkDamageAPI;

    @Override
    public void onEnable() {
        plugin = this;

        fileManager = new FileManager();

        fileManager.logInfo(true)
                .registerCustomFilesFolder("/tiers")
                .registerDefaultGenerateFiles("Basic.yml", "/tiers", "/tiers")
                .registerDefaultGenerateFiles("Lucky.yml", "/tiers", "/tiers")
                .registerDefaultGenerateFiles("Titan.yml", "/tiers", "/tiers")
                .setup();

        pluginManager.registerEvents(fireworkDamageAPI = new FireworkDamageAPI(), this);

        methods = new Methods();

        locationSettings = new LocationSettings();
        editorSettings = new EditorSettings();
        coolDownSettings = new CoolDownSettings();
        envoySettings = new EnvoySettings();
        flareSettings = new FlareSettings();

        crazyManager = new CrazyManager();

        skullCreator = new SkullCreator();

        crazyManager.load();

        Messages.addMissingMessages();

        FileConfiguration config = Files.CONFIG.getFile();

        String metricsPath = config.getString("Settings.Toggle-Metrics");

        boolean metricsEnabled = config.getBoolean("Settings.Toggle-Metrics");

        String countDownSetting = config.getString("Settings.Crate-Countdown.Toggle");

        String envoyLocationBroadcast = config.getString("Settings.Envoy-Locations-Broadcast");

        String updater = config.getString("Settings.Update-Checker");
        String version = config.getString("Settings.Config-Version");

        if (metricsPath == null) {
            config.set("Settings.Toggle-Metrics", true);
            Files.CONFIG.saveFile();
        }

        if (version == null) {
            config.set("Settings.Config-Version", 1);

            Files.CONFIG.saveFile();
        }

        if (updater == null) {
            config.set("Settings.Update-Checker", true);

            Files.CONFIG.saveFile();
        }

        if (countDownSetting == null) {
            config.set("Settings.Crate-Countdown.Toggle", false);
            config.set("Settings.Crate-Countdown.Time", 120);
            config.set("Settings.Crate-Countdown.Message", "&cReady to claim.");
            config.set("Settings.Crate-Countdown.Message-Seconds", " seconds.");
            Files.CONFIG.saveFile();
        }

        if (envoyLocationBroadcast == null) {
            config.set("Settings.Envoy-Locations-Broadcast", false);
            Files.CONFIG.saveFile();
        }

        int configVersion = 1;
        if (configVersion != config.getInt("Settings.Config-Version") && version != null) {
            plugin.getLogger().warning("========================================================================");
            plugin.getLogger().warning("You have an outdated config, Please run the command /envoys update!");
            plugin.getLogger().warning("This will take a backup of your entire folder & update your configs.");
            plugin.getLogger().warning("Default values will be used in place of missing options!");
            plugin.getLogger().warning("If you have any issues, Please contact Discord Support.");
            plugin.getLogger().warning("https://discord.gg/crazycrew");
            plugin.getLogger().warning("========================================================================");
        }

        if (metricsEnabled) {
            MetricsHandler metricsHandler = new MetricsHandler();

            metricsHandler.start();
        }

        checkUpdate();

        if (PluginSupport.PLACEHOLDER_API.isPluginEnabled()) new PlaceholderAPISupport().register();

        enable();
    }

    @Override
    public void onDisable() {
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

    private void checkUpdate() {
        FileConfiguration config = Files.CONFIG.getFile();

        boolean updaterEnabled = config.getBoolean("Settings.Update-Checker");

        if (!updaterEnabled) return;

        getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            UpdateChecker updateChecker = new UpdateChecker(32870);

            try {
                if (updateChecker.hasUpdate() && !getDescription().getVersion().contains("Beta")) {
                    getLogger().warning("CrazyEnvoys has a new update available! New version: " + updateChecker.getNewVersion());
                    getLogger().warning("Current Version: v" + getDescription().getVersion());
                    getLogger().warning("Download: " + updateChecker.getResourcePage());

                    return;
                }

                getLogger().info("Plugin is up to date! - " + updateChecker.getNewVersion());
            } catch (Exception exception) {
                getLogger().warning("Could not check for updates! Perhaps the call failed or you are using a snapshot build:");
                getLogger().warning("You can turn off the update checker in config.yml if on a snapshot build.");
            }
        });
    }

    private void enable() {
        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(new EditControl(), this);
        pluginManager.registerEvents(new EnvoyControl(), this);
        pluginManager.registerEvents(new FlareControl(), this);

        if (PluginSupport.PLACEHOLDER_API.isPluginEnabled()) {
            getLogger().warning("We no longer support placeholders using {}");
            getLogger().warning("We only support %% placeholders i.e %crazyenvoys_cooldown%");
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

    public static CrazyEnvoys getPlugin() {
        return plugin;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Methods getMethods() {
        return methods;
    }

    // Envoy Required Classes.
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

    public LocationSettings getLocationSettings() {
        return locationSettings;
    }

    public CrazyManager getCrazyManager() {
        return crazyManager;
    }

    public SkullCreator getSkullCreator() {
        return skullCreator;
    }

    public FireworkDamageAPI getFireworkDamageAPI() {
        return fireworkDamageAPI;
    }
}