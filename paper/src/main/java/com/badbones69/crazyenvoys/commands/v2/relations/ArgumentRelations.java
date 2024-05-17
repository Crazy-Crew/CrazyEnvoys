package com.badbones69.crazyenvoys.commands.v2.relations;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.v2.MessageManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ArgumentRelations extends MessageManager {

    private String getContext(String command, String order) {
        if (command.isEmpty() || order.isEmpty()) return "";

        String usage = "";

        /*switch (command) {
            case "transfer" -> usage = order + " <crate-name> <player-name> <amount>";
            case "debug", "open", "set" -> usage = order + " <crate-name>";
            case "tp" -> usage = order + "<id>";
            case "additem" -> usage = order + " <crate-name> <prize-number> <chance> [tier]";
            case "preview", "forceopen" -> usage = order + " <crate-name> <player-name>";
            case "open-others" -> usage = order + " <crate-name> <player-name> [key-type]";
            case "mass-open" -> usage = order + " <crate-name> <key-type> <amount>";
            case "give-random" -> usage = order + " <key-type> <amount> <player-name>";
            case "give", "take" -> usage = order + " <key-type> <crate-name> <amount> <player-name>";
            case "giveall" -> usage = order + " <key-type> <crate-name> <amount>";
            case "admin" -> usage = order;
        }*/

        return usage;
    }

    @Override
    public void build() {
        this.commandManager.registerMessage(BukkitMessageKey.UNKNOWN_COMMAND, (sender, context) -> send(sender, Messages.unknown_command.getMessage(sender, "{command}", context.getInvalidInput())));

        this.commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> send(sender, Messages.correct_usage.getMessage(sender, "{usage}", context.getArgumentName())));

        this.commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> send(sender, Messages.no_permission.getMessage(sender, "{permission}", context.getPermission().toString())));

        this.commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> send(sender, Messages.must_be_a_player.getMessage(sender)));

        this.commandManager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> send(sender, Messages.must_be_console_sender.getMessage(sender)));
    }

    @Override
    public void send(@NotNull CommandSender sender, @NotNull String component) {
        sender.sendRichMessage(component);
    }
}