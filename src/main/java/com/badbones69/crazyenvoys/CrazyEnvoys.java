package com.badbones69.crazyenvoys;

import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.FileManager;
import com.badbones69.crazyenvoys.api.FileManager.Files;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent.EnvoyEndReason;
import com.badbones69.crazyenvoys.api.objects.EnvoySettings;
import com.badbones69.crazyenvoys.api.objects.FlareSettings;
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
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnvoys extends JavaPlugin implements Listener {

    private static CrazyEnvoys plugin;

    private CrazyManager crazyManager;

    private FileManager fileManager;

    private Methods methods;

    private SkullCreator skullCreator;

    private FireworkDamageAPI fireworkDamageAPI;

    private EditControl editControl;
    private EnvoyControl envoyControl;
    private FlareControl flareControl;

    private EnvoySettings envoySettings;

    private FlareSettings flareSettings;

    private boolean isEnabled = false;
    
    @Override
    public void onEnable() {

        try {
            plugin = this;

            fileManager = new FileManager();

            envoySettings = new EnvoySettings();

            flareSettings = new FlareSettings();

            methods = new Methods();

            crazyManager = new CrazyManager();

            skullCreator = new SkullCreator();

            fileManager.logInfo(true)
                    .registerCustomFilesFolder("/tiers")
                    .registerDefaultGenerateFiles("Basic.yml", "/tiers", "/tiers")
                    .registerDefaultGenerateFiles("Lucky.yml", "/tiers", "/tiers")
                    .registerDefaultGenerateFiles("Titan.yml", "/tiers", "/tiers")
                    .setup();

            Messages.addMissingMessages();

            String metricsPath = Files.CONFIG.getFile().getString("Settings.Toggle-Metrics");

            boolean metricsEnabled = Files.CONFIG.getFile().getBoolean("Settings.Toggle-Metrics");

            if (metricsPath != null) {
                if (metricsEnabled) new Metrics(this, 4514);
            } else {
                getLogger().warning("Metrics was automatically enabled.");
                getLogger().warning("Please add Toggle-Metrics: false to the top of your config.yml.");
                getLogger().warning("https://github.com/Crazy-Crew/CrazyEnvoys/blob/main/src/main/resources/config.yml");
                getLogger().warning("An example if confused is linked above.");

                new Metrics(this, 4514);
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
            if (editControl.isEditor(player)) {
                editControl.removeEditor(player);
                editControl.removeFakeBlocks();
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

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(editControl = new EditControl(), this);
        pluginManager.registerEvents(envoyControl = new EnvoyControl(), this);
        pluginManager.registerEvents(flareControl = new FlareControl(), this);

        pluginManager.registerEvents(fireworkDamageAPI = new FireworkDamageAPI(), this);

        crazyManager.load();

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

    public EnvoyControl getEnvoyControl() {
        return envoyControl;
    }

    public EditControl getEditControl() {
        return editControl;
    }

    public FlareControl getFlareControl() {
        return flareControl;
    }

    public FlareSettings getFlareSettings() {
        return flareSettings;
    }

    public EnvoySettings getEnvoySettings() {
        return envoySettings;
    }
}