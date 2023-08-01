package com.badbones69.crazyenvoys.paper.support.placeholders;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.api.CrazyManager;
import com.badbones69.crazyenvoys.paper.api.FileManager;
import com.badbones69.crazyenvoys.paper.api.objects.EnvoySettings;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderAPISupport extends PlaceholderExpansion {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final CrazyManager crazyManager = plugin.getCrazyManager();

    private final EnvoySettings envoySettings = plugin.getEnvoySettings();

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String lower = identifier.toLowerCase();

        boolean isEnabled = FileManager.Files.CONFIG.getFile().getBoolean("Settings.Crate-Countdown.Toggle", false);

        if (lower.equals("crates_time")) {
            if (isEnabled) {
                if (crazyManager.getCountdownTimer() != null) {
                    int seconds = crazyManager.getCountdownTimer().getSecondsLeft();

                    if (seconds != 0) return seconds + envoySettings.getEnvoyCountDownMessageSeconds();
                }

                return envoySettings.getEnvoyCountDownMessage();
            }

            return envoySettings.getEnvoyCountDownMessage();
        }

        return switch (lower) {
            case "cooldown" ->
                    crazyManager.isEnvoyActive() ? FileManager.Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.On-Going") : crazyManager.getNextEnvoyTime();
            case "time_left" ->
                    crazyManager.isEnvoyActive() ? crazyManager.getEnvoyRunTimeLeft() : FileManager.Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.Not-Running");
            case "crates_left" -> String.valueOf(crazyManager.getActiveEnvoys().size());
            default -> "";
        };
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String getIdentifier() {
        return "crazyenvoys";
    }
    
    @Override
    public String getAuthor() {
        return "BadBones69";
    }
    
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
}