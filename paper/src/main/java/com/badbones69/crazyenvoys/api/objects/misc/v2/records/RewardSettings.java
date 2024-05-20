package com.badbones69.crazyenvoys.api.objects.misc.v2.records;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.platform.util.ItemUtils;
import com.ryderbelserion.vital.paper.builders.items.ItemBuilder;
import com.ryderbelserion.vital.paper.enums.Support;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public record RewardSettings(String sectionName, String displayName, int chance, boolean dropItems, List<String> messages, List<String> commands, List<String> items) {

    private static final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    public void execute(Player player) {
        this.messages.forEach(message -> {
            // If empty or blank, we do nothing.
            if (message.isBlank() || message.isEmpty()) return;

            // Send the message with PlaceholderAPI support
            player.sendRichMessage(Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(player, message) : message);
        });

        this.commands.forEach(command -> {
            // If empty or blank, we do nothing.
            if (command.isBlank() || command.isEmpty()) return;

            // Dispatch the command!
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
        });
    }

    public List<ItemBuilder> getItems(Player player) {
        return ItemUtils.convertStringList(this.items, sectionName, player);
    }

    public List<ItemBuilder> getItems() {
        return ItemUtils.convertStringList(this.items, sectionName);
    }
}