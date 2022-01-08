package com.badbones69.crazyenvoy.multisupport;

import com.badbones69.crazyenvoy.api.CrazyManager;
import com.badbones69.crazyenvoy.api.FileManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPISupport extends PlaceholderExpansion {
    
    private CrazyManager envoy = CrazyManager.getInstance();
    private Plugin plugin;
    
    public PlaceholderAPISupport(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String lower = identifier.toLowerCase();
        switch (lower) {
            case "cooldown":
                return envoy.isEnvoyActive() ? FileManager.Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.On-Going") : envoy.getNextEnvoyTime();
            case "time_left":
                return envoy.isEnvoyActive() ? envoy.getEnvoyRunTimeLeft() : FileManager.Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.Not-Running");
            case "crates_left":
                return String.valueOf(envoy.getActiveEnvoys().size());
            default:
                return "";
        }
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String getIdentifier() {
        return "crazyenvoy";
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