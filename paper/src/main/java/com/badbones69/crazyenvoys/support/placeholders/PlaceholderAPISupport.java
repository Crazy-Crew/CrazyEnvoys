package com.badbones69.crazyenvoys.support.placeholders;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.config.types.ConfigKeys;
import com.badbones69.crazyenvoys.config.types.MessageKeys;

public class PlaceholderAPISupport extends PlaceholderExpansion {

    private @NotNull final CrazyEnvoys plugin = CrazyEnvoys.get();

    private @NotNull final SettingsManager config = ConfigManager.getConfig();
    private @NotNull final SettingsManager messages = ConfigManager.getMessages();

    private @NotNull final CrazyManager crazyManager = this.plugin.getCrazyManager();

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
        return this.plugin.getDescription().getAuthors().toString();
    }
    
    @Override
    @NotNull
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }
}