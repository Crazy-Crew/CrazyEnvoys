package com.badbones69.crazyenvoys.commands.v2.envoys.types.player;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.commands.v2.envoys.BaseCommand;
import com.badbones69.crazyenvoys.platform.util.MiscUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDrops extends BaseCommand {

    private final @NotNull LocationSettings locationSettings = this.plugin.getLocationSettings();

    @Command("time")
    @Permission(value = "crazyenvoys.time", def = PermissionDefault.TRUE)
    public void time(final CommandSender sender, @Suggestion("drops") int page) {
        List<String> locs = new ArrayList<>();

        int amount = 1;
        Map<String, String> placeholders = new HashMap<>();

        for (Block block : this.crazyManager.isEnvoyActive() ? this.crazyManager.getActiveEnvoys() : this.locationSettings.getSpawnLocations()) {
            placeholders.put("{id}", String.valueOf(amount));
            placeholders.put("{world}", block.getWorld().getName());
            placeholders.put("{x}", String.valueOf(block.getX()));
            placeholders.put("{y}", String.valueOf(block.getY()));
            placeholders.put("{z}", String.valueOf(block.getZ()));

            locs.add(Messages.drops_format.getMessage(placeholders));

            amount++;
            placeholders.clear();
        }

        if (this.crazyManager.isEnvoyActive()) {
            Messages.drops_available.sendMessage(sender);
        } else {
            Messages.drops_possibilities.sendMessage(sender);
        }

        for (String dropLocation : MiscUtils.getPage(locs, page)) {
            sender.sendRichMessage(dropLocation);
        }

        if (!this.crazyManager.isEnvoyActive()) Messages.drops_page.sendMessage(sender);
    }
}