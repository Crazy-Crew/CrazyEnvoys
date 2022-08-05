package com.badbones69.crazyenvoys.multisupport;

import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.FileManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderAPISupport extends PlaceholderExpansion {
    
    private final CrazyManager crazyManager = CrazyManager.getInstance();
    
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String lower = identifier.toLowerCase();

        switch (lower) {
            case "cooldown":
                return crazyManager.isEnvoyActive() ? FileManager.Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.On-Going") : crazyManager.getNextEnvoyTime();
            case "time_left":
                return crazyManager.isEnvoyActive() ? crazyManager.getEnvoyRunTimeLeft() : FileManager.Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.Not-Running");
            case "crates_left":
                return String.valueOf(crazyManager.getActiveEnvoys().size());
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
        return "crazyenvoys";
    }
    
    @Override
    public String getAuthor() {
        return "BadBones69";
    }
    
    @Override
    public String getVersion() {
        return crazyManager.getPlugin().getDescription().getVersion();
    }
    
}