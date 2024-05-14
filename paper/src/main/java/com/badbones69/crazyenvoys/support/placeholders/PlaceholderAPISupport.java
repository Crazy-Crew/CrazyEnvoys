package com.badbones69.crazyenvoys.support.placeholders;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.platform.config.types.ConfigKeys;
import com.badbones69.crazyenvoys.platform.config.types.MessageKeys;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPISupport extends PlaceholderExpansion {

    private final @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final @NotNull SettingsManager config = ConfigManager.getConfig();

    private final @NotNull SettingsManager messages = ConfigManager.getMessages();

    private final @NotNull CrazyManager crazyManager = this.plugin.getCrazyManager();

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        String lower = identifier.toLowerCase();

        boolean isEnabled = this.config.getProperty(ConfigKeys.envoys_grace_period_toggle);

        if (lower.equals("envoys_time")) {
            if (isEnabled) {
                if (this.crazyManager.getCountdownTimer() != null) {
                    int seconds = this.crazyManager.getCountdownTimer().getSecondsLeft();

                    if (seconds != 0) return seconds + this.config.getProperty(ConfigKeys.envoys_grace_period_time_unit);
                }

                return this.config.getProperty(ConfigKeys.envoys_grace_period_unlocked);
            }

            return this.config.getProperty(ConfigKeys.envoys_grace_period_unlocked);
        }

        return switch (lower) {
            case "cooldown" -> this.crazyManager.isEnvoyActive() ? this.messages.getProperty(MessageKeys.hologram_on_going) : this.crazyManager.getNextEnvoyTime();
            case "time_left" -> this.crazyManager.isEnvoyActive() ? this.crazyManager.getEnvoyRunTimeLeft() : this.messages.getProperty(MessageKeys.hologram_not_running);
            case "envoys_left" -> String.valueOf(this.crazyManager.getActiveEnvoys().size());
            default -> "";
        };
    }
    
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return this.plugin.getName().toLowerCase();
    }
    
    @Override
    @NotNull
    public String getAuthor() {
        return "BadBones69";
    }
    
    @Override
    @NotNull
    public String getVersion() {
        return this.plugin.getPluginMeta().getVersion();
    }
}