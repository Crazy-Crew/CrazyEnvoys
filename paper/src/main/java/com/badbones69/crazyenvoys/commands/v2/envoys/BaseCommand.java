package com.badbones69.crazyenvoys.commands.v2.envoys;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyHandler;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.config.ConfigManager;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Command(value = "crazyenvoys", alias = {"envoys", "envoy"})
public abstract class BaseCommand {

    protected @NotNull final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    protected @NotNull final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    protected @NotNull final CrazyManager crazyManager = this.plugin.getCrazyManager();

    protected @NotNull final SettingsManager config = ConfigManager.getConfig();

    protected void getTime(CommandSender sender, boolean envoyActive, String envoyRunTimeLeft, String nextEnvoyTime) {
        Map<String, String> placeholder = new HashMap<>();

        if (envoyActive) {
            placeholder.put("{time}", envoyRunTimeLeft);

            Messages.time_left.sendMessage(sender, placeholder);
        } else {
            placeholder.put("{time}", nextEnvoyTime);

            Messages.time_till_event.sendMessage(sender, placeholder);
        }
    }

}