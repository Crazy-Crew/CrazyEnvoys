package com.badbones69.crazyenvoys.paper.api.objects;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;

public class EditorSettings {

    private final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final LocationSettings locationSettings = this.plugin.getLocationSettings();

    private final ArrayList<Player> editors = new ArrayList<>();

    public List<Player> getEditors() {
        return this.editors;
    }

    public void addEditor(Player player) {
        this.editors.add(player);
    }

    public void removeEditor(Player player) {
        this.editors.remove(player);
    }

    public boolean isEditor(Player player) {
        return this.editors.contains(player);
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