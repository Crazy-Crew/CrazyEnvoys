package com.badbones69.crazyenvoys.api.objects;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditorSettings {

    @NotNull
    private final CrazyEnvoys plugin = CrazyEnvoys.get();

    @NotNull
    private final LocationSettings locationSettings = this.plugin.getLocationSettings();

    private final List<UUID> editors = new ArrayList<>();

    public List<UUID> getEditors() {
        return this.editors;
    }

    public void addEditor(Player player) {
        this.editors.add(player.getUniqueId());
    }

    public void removeEditor(Player player) {
        this.editors.remove(player.getUniqueId());
    }

    public boolean isEditor(Player player) {
        return this.editors.contains(player.getUniqueId());
    }

    public void showFakeBlocks(Player player) {
        for (Block block : this.locationSettings.getSpawnLocations()) {
            player.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
        }
    }

    public void removeFakeBlocks() {
        for (Block block : this.locationSettings.getSpawnLocations()) {
            block.getState().update();
        }
    }
}