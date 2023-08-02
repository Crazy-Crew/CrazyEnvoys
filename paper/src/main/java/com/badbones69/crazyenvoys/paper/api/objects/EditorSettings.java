package com.badbones69.crazyenvoys.paper.api.objects;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class EditorSettings {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final LocationSettings locationSettings = plugin.getLocationSettings();

    private final ArrayList<Player> editors = new ArrayList<>();

    public List<Player> getEditors() {
        return editors;
    }

    public void addEditor(Player player) {
        editors.add(player);
    }

    public void removeEditor(Player player) {
        editors.remove(player);
    }

    public boolean isEditor(Player player) {
        return editors.contains(player);
    }

    public void showFakeBlocks(Player player) {
        for (Block block : locationSettings.getSpawnLocations()) {
            player.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
        }
    }

    public void removeFakeBlocks() {
        for (Block block : locationSettings.getSpawnLocations()) {
            block.getState().update();
        }
    }
}