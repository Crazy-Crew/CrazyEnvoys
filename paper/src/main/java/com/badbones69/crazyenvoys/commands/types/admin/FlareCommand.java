package com.badbones69.crazyenvoys.commands.types.admin;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.types.EnvoyCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;
import java.util.Map;

public class FlareCommand extends EnvoyCommand {

    @Command("flare")
    @Permission(value = "envoy.flare.give", def = PermissionDefault.OP)
    @Syntax("/envoys flare [amount] [player]")
    public void execute(final CommandSender sender, @ArgName("amount") @Suggestion("numbers") final int amount, @ArgName("player") @Optional @Suggestion("players") final Player player) {
        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{player}", player.getName());
        placeholders.put("{amount}", amount + "");

        Messages.give_flare.sendMessage(sender, placeholders);

        if (!sender.getName().equalsIgnoreCase(player.getName())) Messages.given_flare.sendMessage(player, placeholders);

        this.flareSettings.giveFlare(player, amount);
    }
}