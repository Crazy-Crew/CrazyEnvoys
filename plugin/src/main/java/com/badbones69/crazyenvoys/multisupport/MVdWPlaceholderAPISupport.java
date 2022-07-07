package com.badbones69.crazyenvoys.multisupport;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.FileManager;
import org.bukkit.plugin.Plugin;

public class MVdWPlaceholderAPISupport {
    
    private static final CrazyManager crazyManager = CrazyManager.getInstance();
    
    public static void registerPlaceholders(Plugin plugin) {
        PlaceholderAPI.registerPlaceholder(plugin, "crazyenvoy_cooldown", e -> {
            if (crazyManager.isEnvoyActive()) {
                return FileManager.Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.On-Going");
            } else {
                return crazyManager.getNextEnvoyTime();
            }
        });
        
        PlaceholderAPI.registerPlaceholder(plugin, "crazyenvoy_time_left", e -> {
            if (crazyManager.isEnvoyActive()) {
                return crazyManager.getEnvoyRunTimeLeft();
            } else {
                return FileManager.Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.Not-Running");
            }
        });

        PlaceholderAPI.registerPlaceholder(plugin, "crazyenvoy_crates_left", e -> crazyManager.getActiveEnvoys().size() + "");
    }
    
}