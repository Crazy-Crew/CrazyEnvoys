package com.badbones69.crazyenvoys.listeners;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class EnvoyEditListener implements Listener {

    private @NotNull final CrazyEnvoys plugin = CrazyEnvoys.get();

    private @NotNull final Methods methods = this.plugin.getMethods();

    private @NotNull final EditorSettings editorSettings = this.plugin.getEditorSettings();

    private @NotNull final LocationSettings locationSettings = this.plugin.getLocationSettings();

    private @NotNull final CrazyManager crazyManager = this.plugin.getCrazyManager();
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!this.editorSettings.isEditor(player)) return;

        event.setCancelled(true);

        if (this.methods.getItemInHand(player).getType() != Material.BEDROCK) return;

        this.locationSettings.addSpawnLocation(block);

        Messages.add_location.sendMessage(player);

        for (UUID uuid : this.editorSettings.getEditors()) {
            if (uuid == player.getUniqueId()) player.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!this.editorSettings.isEditor(player)) return;

        event.setCancelled(true);

        if (!this.crazyManager.isLocation(block.getLocation())) return;

        block.getState().update();

        this.locationSettings.removeSpawnLocation(block);

        Messages.remove_location.sendMessage(player);
    }
}