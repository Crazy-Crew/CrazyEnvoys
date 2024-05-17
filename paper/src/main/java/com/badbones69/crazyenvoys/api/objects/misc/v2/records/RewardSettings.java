package com.badbones69.crazyenvoys.api.objects.misc.v2.records;

import com.badbones69.crazyenvoys.platform.util.ItemUtils;
import com.ryderbelserion.vital.util.builders.items.ItemBuilder;
import java.util.List;

public record RewardSettings(String sectionName, String displayName, int chance, boolean dropItems, List<String> messages, List<String> items) {

    public List<ItemBuilder> getItemStacks() {
        return ItemUtils.convertStringList(this.items, sectionName);
    }
}