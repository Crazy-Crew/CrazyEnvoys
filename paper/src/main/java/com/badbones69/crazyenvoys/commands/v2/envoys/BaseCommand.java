package com.badbones69.crazyenvoys.commands.v2.envoys;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyHandler;
import com.badbones69.crazyenvoys.config.ConfigManager;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Command(value = "crazyenvoys", alias = {"envoys", "envoy"})
public abstract class BaseCommand {

    protected @NotNull final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    protected @NotNull final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    protected @NotNull final SettingsManager config = ConfigManager.getConfig();

}