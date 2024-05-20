package com.badbones69.crazyenvoys.commands.v2.envoys.types.player;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.v2.envoys.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import java.util.UUID;

public class CommandIgnore extends BaseCommand {

    @Command("ignore")
    @Permission(value = "crazyenvoys.ignore", def = PermissionDefault.TRUE)
    public void ignore(final Player player) {
        UUID uuid = player.getUniqueId();

        if (this.crazyManager.isIgnoringMessages(uuid)) {
            this.crazyManager.removeIgnorePlayer(uuid);

            Messages.stop_ignoring_messages.sendMessage(player);
        } else {
            this.crazyManager.addIgnorePlayer(uuid);

            Messages.start_ignoring_messages.sendMessage(player);
        }
    }
}