package com.badbones69.crazyenvoys.multisupport.holograms;

import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.FileManager;
import com.badbones69.crazyenvoys.api.interfaces.HologramController;
import com.badbones69.crazyenvoys.api.objects.Tier;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.block.Block;
import java.util.HashMap;

public class HolographicSupport implements HologramController {
    
    private static final CrazyManager crazyManager = CrazyManager.getInstance();
    private final HashMap<Block, Hologram> holograms = new HashMap<>();
    
    public void createHologram(Block block, Tier tier) {
        double height = tier.getHoloHeight();
        Hologram hologram = HologramsAPI.createHologram(crazyManager.getPlugin(), block.getLocation().add(.5, height, .5));
        tier.getHoloMessage().stream().map(Methods :: color).forEach(hologram :: appendTextLine);
        holograms.put(block, hologram);
    }
    
    public void removeHologram(Block block) {
        if (holograms.containsKey(block)) {
            Hologram hologram = holograms.get(block);
            holograms.remove(block);
            hologram.delete();
        }
    }
    
    public void removeAllHolograms() {
        holograms.keySet().forEach(block -> holograms.get(block).delete());
        holograms.clear();
    }
    
    public String getPluginName() {
        return "HolographicDisplays";
    }
    
    public static void registerPlaceHolders() {
        HologramsAPI.registerPlaceholder(crazyManager.getPlugin(), "{crazyenvoy_cooldown}", 1, () -> {
            if (crazyManager.isEnvoyActive()) {
                return FileManager.Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.On-Going");
            } else {
                return crazyManager.getNextEnvoyTime();
            }
        });

        HologramsAPI.registerPlaceholder(crazyManager.getPlugin(), "{crazyenvoy_time_left}", 1, () -> {
            if (crazyManager.isEnvoyActive()) {
                return crazyManager.getEnvoyRunTimeLeft();
            } else {
                return FileManager.Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.Not-Running");
            }
        });

        HologramsAPI.registerPlaceholder(crazyManager.getPlugin(), "{crazyenvoy_crates_left}", .5, () -> crazyManager.getActiveEnvoys().size() + "");
    }
    
    public static void unregisterPlaceHolders() {
        try {
            HologramsAPI.unregisterPlaceholders(crazyManager.getPlugin());
        } catch (Exception ignored) {}
    }
}