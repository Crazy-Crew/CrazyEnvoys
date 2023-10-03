package com.badbones69.crazyenvoys.paper.listeners;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.Methods;
import com.badbones69.crazyenvoys.paper.api.CrazyManager;
import com.badbones69.crazyenvoys.paper.api.enums.Translation;
import com.badbones69.crazyenvoys.paper.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.paper.api.objects.LocationSettings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class EnvoyEditListener implements Listener {

    private final @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final @NotNull Methods methods = this.plugin.getMethods();

    private final @NotNull EditorSettings editorSettings = this.plugin.getEditorSettings();

    private final @NotNull LocationSettings locationSettings = this.plugin.getLocationSettings();

    private final @NotNull CrazyManager crazyManager = this.plugin.getCrazyManager();
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (!this.editorSettings.isEditor(player)) return;

        event.setCancelled(true);

        if (this.methods.getItemInHand(player).getType() != Material.BEDROCK) return;

        this.locationSettings.addSpawnLocation(block);

        Translation.add_location.sendMessage(player);

        for (UUID uuid : this.editorSettings.getEditors()) {
            if (uuid == player.getUniqueId()) player.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (!this.editorSettings.isEditor(player)) return;

        event.setCancelled(true);

        if (!this.crazyManager.isLocation(block.getLocation())) return;

        block.getState().update();

        this.locationSettings.removeSpawnLocation(block);

        Translation.remove_location.sendMessage(player);
    }
}