package com.badbones69.crazyenvoys;

import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.FileManager;
import com.badbones69.crazyenvoys.api.FileManager.Files;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent.EnvoyEndReason;
import com.badbones69.crazyenvoys.commands.EnvoyCommand;
import com.badbones69.crazyenvoys.commands.EnvoyTab;
import com.badbones69.crazyenvoys.controllers.EditControl;
import com.badbones69.crazyenvoys.controllers.EnvoyControl;
import com.badbones69.crazyenvoys.controllers.FireworkDamageAPI;
import com.badbones69.crazyenvoys.controllers.FlareControl;
import com.badbones69.crazyenvoys.multisupport.PlaceholderAPISupport;
import com.badbones69.crazyenvoys.multisupport.Support;
import com.badbones69.crazyenvoys.multisupport.holograms.HolographicSupport;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnvoys extends JavaPlugin implements Listener {
    
    private final FileManager fileManager = FileManager.getInstance();
    private final CrazyManager crazyManager = CrazyManager.getInstance();
    
    @Override
    public void onEnable() {

        crazyManager.loadPlugin(this);

        String homeFolder = "/tiers";

        fileManager.logInfo(true)
        .registerCustomFilesFolder(homeFolder)
        .registerDefaultGenerateFiles("Basic.yml", homeFolder, homeFolder)
        .registerDefaultGenerateFiles("Lucky.yml", homeFolder, homeFolder)
        .registerDefaultGenerateFiles("Titan.yml", homeFolder, homeFolder)
        .setup(this);
        
        Messages.addMissingMessages();
        
        crazyManager.load();
        
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(new EditControl(), this);
        pluginManager.registerEvents(new EnvoyControl(), this);
        pluginManager.registerEvents(new FlareControl(), this);

        pluginManager.registerEvents(new FireworkDamageAPI(), this);

        boolean metricsEnabled = Files.CONFIG.getFile().getBoolean("Settings.Toggle-Metrics");

        if (Files.CONFIG.getFile().getString("Settings.Toggle-Metrics") != null) {
            if (metricsEnabled) new Metrics(this, 4537);
        } else {
            getLogger().warning("Metrics was automatically enabled.");
            getLogger().warning("Please add Toggle-Metrics: false to the top of your config.yml");
            getLogger().warning("https://github.com/Crazy-Crew/Crazy-Crates/blob/main/src/main/resources/config.yml");

            getLogger().warning("An example if confused is linked above.");

            new Metrics(this, 4537);
        }
        
        if (Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) HolographicSupport.registerPlaceHolders();
        if (Support.PLACEHOLDER_API.isPluginLoaded()) new PlaceholderAPISupport().register();
        //if (Support.MVDW_PLACEHOLDER_API.isPluginLoaded()) MVdWPlaceholderAPISupport.registerPlaceholders(this);
        
        getCommand("envoy").setExecutor(new EnvoyCommand());
        getCommand("envoy").setTabCompleter(new EnvoyTab());
    }
    
    @Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            if (EditControl.isEditor(player)) {
                EditControl.removeEditor(player);
                EditControl.removeFakeBlocks();
            }
        }

        if (Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
            HolographicSupport.unregisterPlaceHolders();
        }

        if (crazyManager.isEnvoyActive()) {
            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.SHUTDOWN);
            getServer().getPluginManager().callEvent(event);
            crazyManager.endEnvoyEvent();
        }

        crazyManager.unload();
    }
}