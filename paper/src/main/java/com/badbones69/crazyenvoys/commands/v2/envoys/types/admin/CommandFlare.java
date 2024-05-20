package com.badbones69.crazyenvoys.commands.v2.envoys.types.admin;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.commands.v2.envoys.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class CommandFlare extends BaseCommand {

    private final @NotNull FlareSettings flareSettings = this.plugin.getFlareSettings();

    @Command("flare")
    @Permission(value = "crazyenvoys.flare.give", def = PermissionDefault.OP)
    public void flare(final CommandSender sender, @Suggestion("numbers") final int amount, @Suggestion("players") final Player receiver) {
        Map<String, String> placeholder = new HashMap<>();

        String name = receiver.getName();

        placeholder.put("{player}", name);
        placeholder.put("{amount}", amount + "");

        Messages.give_flare.sendMessage(sender, placeholder);

        if (!sender.getName().equalsIgnoreCase(name)) Messages.given_flare.sendMessage(receiver, placeholder);

        this.flareSettings.giveFlare(receiver, amount);
    }
}