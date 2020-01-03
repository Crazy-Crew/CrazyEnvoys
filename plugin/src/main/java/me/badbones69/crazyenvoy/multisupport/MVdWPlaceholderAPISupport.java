package me.badbones69.crazyenvoy.multisupport;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.FileManager.Files;
import org.bukkit.plugin.Plugin;

public class MVdWPlaceholderAPISupport {
    
    private static CrazyEnvoy envoy = CrazyEnvoy.getInstance();
    
    public static void registerPlaceholders(Plugin plugin) {
        PlaceholderAPI.registerPlaceholder(plugin, "crazyenvoy_cooldown", e -> {
            if (envoy.isEnvoyActive()) {
                return Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.On-Going");
            } else {
                return envoy.getNextEnvoyTime();
            }
        });
        
        PlaceholderAPI.registerPlaceholder(plugin, "crazyenvoy_time_left", e -> {
            if (envoy.isEnvoyActive()) {
                return envoy.getEnvoyRunTimeLeft();
            } else {
                return Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.Not-Running");
            }
        });
        
        PlaceholderAPI.registerPlaceholder(plugin, "crazyenvoy_crates_left", e -> envoy.getActiveEnvoys().size() + "");
    }
    
}