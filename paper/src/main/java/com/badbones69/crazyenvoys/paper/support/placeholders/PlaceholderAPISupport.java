package com.badbones69.crazyenvoys.paper.support.placeholders;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.api.CrazyManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.common.config.ConfigManager;
import us.crazycrew.crazyenvoys.common.config.types.Config;
import us.crazycrew.crazyenvoys.common.config.types.Messages;
import us.crazycrew.crazyenvoys.paper.api.plugin.CrazyHandler;

public class PlaceholderAPISupport extends PlaceholderExpansion {

    private final @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull ConfigManager configManager = this.crazyHandler.getConfigManager();
    private final @NotNull SettingsManager config = this.configManager.getConfig();
    private final @NotNull SettingsManager messages = this.configManager.getMessages();

    private final @NotNull CrazyManager crazyManager = this.plugin.getCrazyManager();

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String lower = identifier.toLowerCase();

        boolean isEnabled = this.config.getProperty(Config.envoys_grace_period_toggle);

        if (lower.equals("envoys_time")) {
            if (isEnabled) {
                if (this.crazyManager.getCountdownTimer() != null) {
                    int seconds = this.crazyManager.getCountdownTimer().getSecondsLeft();

                    if (seconds != 0) return seconds + this.config.getProperty(Config.envoys_grace_period_time_unit);
                }

                return this.config.getProperty(Config.envoys_grace_period_unlocked);
            }

            return this.config.getProperty(Config.envoys_grace_period_unlocked);
        }

        return switch (lower) {
            case "cooldown" -> this.crazyManager.isEnvoyActive() ? this.messages.getProperty(Messages.hologram_on_going) : this.crazyManager.getNextEnvoyTime();
            case "time_left" -> this.crazyManager.isEnvoyActive() ? this.crazyManager.getEnvoyRunTimeLeft() : this.messages.getProperty(Messages.hologram_not_running);
            case "envoys_left" -> String.valueOf(this.crazyManager.getActiveEnvoys().size());
            default -> "";
        };
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public @NotNull String getIdentifier() {
        return this.plugin.getName().toLowerCase();
    }
    
    @Override
    public @NotNull String getAuthor() {
        return "BadBones69";
    }
    
    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }
}