package com.badbones69.crazyenvoys.api.objects.misc.v2.records;

import com.badbones69.crazyenvoys.platform.util.ItemUtils;
import com.ryderbelserion.vital.paper.builders.items.ItemBuilder;
import org.bukkit.entity.Player;
import java.util.List;

public record RewardSettings(String sectionName, String displayName, int chance, boolean dropItems, List<String> messages, List<String> commands, List<String> items) {

    public List<ItemBuilder> getItems(Player player) {
        return ItemUtils.convertStringList(this.items, sectionName, player);
    }

    public List<ItemBuilder> getItems() {
        return ItemUtils.convertStringList(this.items, sectionName);
    }
}