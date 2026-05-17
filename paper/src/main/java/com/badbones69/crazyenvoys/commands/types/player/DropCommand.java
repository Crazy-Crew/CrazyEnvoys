package com.badbones69.crazyenvoys.commands.types.player;

import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.types.EnvoyCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DropCommand extends EnvoyCommand {

    @Command(value = "drop", alias = {"drops"})
    @Permission(value = "envoy.drops", def = PermissionDefault.TRUE)
    @Syntax("/envoys [page]")
    public void execute(final CommandSender sender, @ArgName("drop") @Suggestion("drops") final int page) {
        final List<String> locs = new ArrayList<>();

        int amount = 1;

        final Map<String, String> placeholders = new HashMap<>();

        for (Block block : this.crazyManager.isEnvoyActive() ? this.crazyManager.getActiveEnvoys() : this.locationSettings.getSpawnLocations()) {
            placeholders.put("{id}", String.valueOf(amount));
            placeholders.put("{world}", block.getWorld().getName());
            placeholders.put("{x}", String.valueOf(block.getX()));
            placeholders.put("{y}", String.valueOf(block.getY()));
            placeholders.put("{z}", String.valueOf(block.getZ()));

            locs.add(Messages.drops_format.getMessage(sender, placeholders));

            amount++;

            placeholders.clear();
        }

        if (this.crazyManager.isEnvoyActive()) {
            Messages.drops_available.sendMessage(sender);
        } else {
            Messages.drops_possibilities.sendMessage(sender);
        }

        for (String dropLocation : Methods.getPage(locs, page)) {
            //sender.sendMessage(MsgUtils.color(dropLocation)); //todo() improve this
        }

        if (!this.crazyManager.isEnvoyActive()) Messages.drops_page.sendMessage(sender);
    }
}