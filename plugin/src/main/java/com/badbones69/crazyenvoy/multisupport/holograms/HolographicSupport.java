package com.badbones69.crazyenvoy.multisupport.holograms;

import com.badbones69.crazyenvoy.Methods;
import com.badbones69.crazyenvoy.api.CrazyManager;
import com.badbones69.crazyenvoy.api.FileManager;
import com.badbones69.crazyenvoy.api.interfaces.HologramController;
import com.badbones69.crazyenvoy.api.objects.Tier;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.block.Block;

import java.util.HashMap;

public class HolographicSupport implements HologramController {
    
    private static CrazyManager envoy = CrazyManager.getInstance();
    private HashMap<Block, Hologram> holograms = new HashMap<>();
    
    public void createHologram(Block block, Tier tier) {
        double hight = tier.getHoloHight();
        Hologram hologram = HologramsAPI.createHologram(envoy.getPlugin(), block.getLocation().add(.5, hight, .5));
        for (String line : tier.getHoloMessage()) {
            hologram.appendTextLine(Methods.color(line));
        }
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
        for (Block block : holograms.keySet()) {
            holograms.get(block).delete();
        }
        holograms.clear();
    }
    
    public String getPluginName() {
        return "HolographicDisplays";
    }
    
    public static void registerPlaceHolders() {
        HologramsAPI.registerPlaceholder(envoy.getPlugin(), "{crazyenvoy_cooldown}", 1, () -> {
            if (envoy.isEnvoyActive()) {
                return FileManager.Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.On-Going");
            } else {
                return envoy.getNextEnvoyTime();
            }
        });
        HologramsAPI.registerPlaceholder(envoy.getPlugin(), "{crazyenvoy_time_left}", 1, () -> {
            if (envoy.isEnvoyActive()) {
                return envoy.getEnvoyRunTimeLeft();
            } else {
                return FileManager.Files.MESSAGES.getFile().getString("Messages.Hologram-Placeholders.Not-Running");
            }
        });
        HologramsAPI.registerPlaceholder(envoy.getPlugin(), "{crazyenvoy_crates_left}", .5, () -> envoy.getActiveEnvoys().size() + "");
    }
    
    public static void unregisterPlaceHolders() {
        try {
            HologramsAPI.unregisterPlaceholders(envoy.getPlugin());
        } catch (Exception e) {
        }
    }
    
}