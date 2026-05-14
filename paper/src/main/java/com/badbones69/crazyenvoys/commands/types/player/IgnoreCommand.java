package com.badbones69.crazyenvoys.commands.types.player;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.types.EnvoyCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.UUID;

public class IgnoreCommand extends EnvoyCommand {

    @Command("ignore")
    @Permission(value = "envoy.ignore", def = PermissionDefault.TRUE)
    @Flag(flag = "s", longFlag = "silence")
    @Flag(flag = "n", longFlag = "no")
    @Syntax("/envoys ignore [-s|-n]")
    public void execute(final Player player, final Flags flags) {
        final boolean isSilent = flags.hasFlag("s");

        final UUID uuid = player.getUniqueId();

        if (this.crazyManager.isIgnoringMessages(uuid) || flags.hasFlag("no")) {
            this.crazyManager.removeIgnorePlayer(uuid);

            if (!isSilent) Messages.stop_ignoring_messages.sendMessage(player);
        } else {
            this.crazyManager.addIgnorePlayer(uuid);

            if (!isSilent) Messages.start_ignoring_messages.sendMessage(player);
        }
    }
}