package com.badbones69.crazyenvoys.commands.v2;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class MessageManager {

    protected @NotNull final BukkitCommandManager<CommandSender> commandManager = CommandManager.getCommandManager();

    public abstract void build();

    public abstract void send(@NotNull CommandSender sender, @NotNull String component);

}