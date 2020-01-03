package me.badbones69.crazyenvoy;

import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.FileManager;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent.EnvoyEndReason;
import me.badbones69.crazyenvoy.commands.EnvoyCommand;
import me.badbones69.crazyenvoy.commands.EnvoyTab;
import me.badbones69.crazyenvoy.controllers.*;
import me.badbones69.crazyenvoy.multisupport.MVdWPlaceholderAPISupport;
import me.badbones69.crazyenvoy.multisupport.PlaceholderAPISupport;
import me.badbones69.crazyenvoy.multisupport.Support;
import me.badbones69.crazyenvoy.multisupport.Version;
import me.badbones69.crazyenvoy.multisupport.holograms.HolographicSupport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
    
    private FileManager fileManager = FileManager.getInstance();
    private CrazyEnvoy envoy = CrazyEnvoy.getInstance();
    
    @Override
    public void onEnable() {
        String homeFolder = Version.getCurrentVersion().isNewer(Version.v1_12_R1) ? "/Tiers1.13-Up" : "/Tiers1.12.2-Down";
        fileManager.logInfo(true)
        .registerCustomFilesFolder("/Tiers")
        .registerDefaultGenerateFiles("Basic.yml", "/Tiers", homeFolder)
        .registerDefaultGenerateFiles("Lucky.yml", "/Tiers", homeFolder)
        .registerDefaultGenerateFiles("Titan.yml", "/Tiers", homeFolder)
        .setup(this);
        envoy.load();
        Methods.hasUpdate();
        new Metrics(this);
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(this, this);
        pm.registerEvents(new EditControl(), this);
        pm.registerEvents(new EnvoyControl(), this);
        pm.registerEvents(new FlareControl(), this);
        try {
            if (Version.getCurrentVersion().isNewer(Version.v1_10_R1)) {
                pm.registerEvents(new FireworkDamageAPI(this), this);
            }
        } catch (Exception e) {
        }
        if (Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
            HolographicSupport.registerPlaceHolders();
        }
        if (Support.PLACEHOLDER_API.isPluginLoaded()) {
            new PlaceholderAPISupport(this).register();
        }
        if (Support.MVDW_PLACEHOLDER_API.isPluginLoaded()) {
            MVdWPlaceholderAPISupport.registerPlaceholders(this);
        }
        getCommand("envoy").setExecutor(new EnvoyCommand());
        getCommand("envoy").setTabCompleter(new EnvoyTab());
    }
    
    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (EditControl.isEditor(player)) {
                EditControl.removeEditor(player);
                EditControl.removeFakeBlocks(player);
            }
        }
        if (Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
            HolographicSupport.unregisterPlaceHolders();
        }
        if (envoy.isEnvoyActive()) {
            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.SHUTDOWN);
            Bukkit.getPluginManager().callEvent(event);
            envoy.endEnvoyEvent();
        }
        envoy.unload();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getName().equals("BadBones69")) {
                    player.sendMessage(Methods.getPrefix() + Methods.color("&7This server is running your Crazy envoy Plugin. " + "&7It is running version &av" + envoy.getPlugin().getDescription().getVersion() + "&7."));
                }
                if (player.isOp()) {
                    Methods.hasUpdate(player);
                }
            }
        }.runTaskLaterAsynchronously(this, 20);
    }
    
}