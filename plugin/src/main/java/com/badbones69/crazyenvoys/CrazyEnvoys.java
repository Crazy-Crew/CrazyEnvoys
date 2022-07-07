package com.badbones69.crazyenvoys;

import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.FileManager;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent.EnvoyEndReason;
import com.badbones69.crazyenvoys.commands.EnvoyCommand;
import com.badbones69.crazyenvoys.commands.EnvoyTab;
import com.badbones69.crazyenvoys.controllers.*;
import com.badbones69.crazyenvoys.multisupport.MVdWPlaceholderAPISupport;
import com.badbones69.crazyenvoys.multisupport.PlaceholderAPISupport;
import com.badbones69.crazyenvoys.multisupport.Support;
import com.badbones69.crazyenvoys.multisupport.ServerProtocol;
import com.badbones69.crazyenvoys.multisupport.holograms.HolographicSupport;
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

        String homeFolder = ServerProtocol.isNewer(ServerProtocol.v1_12_R1) ? "/tiers1.13-Up" : "/tiers1.12.2-Down";
        fileManager.logInfo(true)
        .registerCustomFilesFolder("/tiers")
        .registerDefaultGenerateFiles("Basic.yml", "/tiers", homeFolder)
        .registerDefaultGenerateFiles("Lucky.yml", "/tiers", homeFolder)
        .registerDefaultGenerateFiles("Titan.yml", "/tiers", homeFolder)
        .setup();
        
        Messages.addMissingMessages();
        
        crazyManager.load();
        
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(new EditControl(), this);
        pluginManager.registerEvents(new EnvoyControl(), this);
        pluginManager.registerEvents(new FlareControl(), this);
        
        try {
            if (ServerProtocol.isNewer(ServerProtocol.v1_10_R1)) {
                pluginManager.registerEvents(new FireworkDamageAPI(), this);
            }
        } catch (Exception ignored) {}
        
        if (Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) HolographicSupport.registerPlaceHolders();
        if (Support.PLACEHOLDER_API.isPluginLoaded()) new PlaceholderAPISupport().register();
        if (Support.MVDW_PLACEHOLDER_API.isPluginLoaded()) MVdWPlaceholderAPISupport.registerPlaceholders(this);
        
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