package com.badbones69.crazyenvoys;

import com.badbones69.crazyenvoys.api.CrazyHandler;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.objects.CoolDownSettings;
import com.badbones69.crazyenvoys.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.commands.v2.CommandManager;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.ryderbelserion.vital.core.config.YamlManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CrazyEnvoys extends JavaPlugin {

    private CoolDownSettings coolDownSettings;
    private LocationSettings locationSettings;
    private EditorSettings editorSettings;
    private FlareSettings flareSettings;

    private CrazyManager crazyManager;
    private CrazyHandler crazyHandler;
    private YamlManager yamlManager;

    private Server paperServer;

    @Override
    public void onEnable() {
        this.paperServer = new Server(getDataFolder(), getLogger());
        this.paperServer.apply();

        this.yamlManager = ConfigManager.getYamlManager();

        this.crazyHandler = new CrazyHandler();
        this.crazyHandler.load();

        CommandManager.load();

        //this.locationSettings = new LocationSettings();
        //this.editorSettings = new EditorSettings();
        //this.coolDownSettings = new CoolDownSettings();
        //this.flareSettings = new FlareSettings();

        //this.crazyManager = new CrazyManager();
        //this.crazyManager.load();

        //getServer().getPluginManager().registerEvents(new EnvoyEditListener(), this);
        //getServer().getPluginManager().registerEvents(new EnvoyClickListener(), this);
        //getServer().getPluginManager().registerEvents(new FlareClickListener(), this);
        //getServer().getPluginManager().registerEvents(new FireworkDamageListener(), this);

        //if (Support.placeholder_api.isEnabled()) {
        //    new PlaceholderAPISupport().register();
        //}

        //MiscUtils.registerCommand(getCommand("crazyenvoys"), new EnvoyTab(), new EnvoyCommand());
    }

    @Override
    public void onDisable() {
        // Cancel the tasks
        getServer().getGlobalRegionScheduler().cancelTasks(this);
        getServer().getAsyncScheduler().cancelTasks(this);

        /*for (Player player : getServer().getOnlinePlayers()) {
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

        this.crazyManager.reload(true);*/
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

    public @NotNull final Server getPaperServer() {
        return this.paperServer;
    }

    public @NotNull final CrazyHandler getCrazyHandler() {
        return this.crazyHandler;
    }

    public @NotNull final CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    public @NotNull final YamlManager getFileManager() {
        return this.yamlManager;
    }
}