package com.badbones69.crazyenvoys.api.registry.adapters;

import com.badbones69.crazyenvoys.CrazyPlugin;
import com.badbones69.crazyenvoys.api.CrazyEnvoysPlatform;
import com.badbones69.crazyenvoys.api.adapters.sender.ISenderAdapter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.UUID;

public class PaperSenderAdapter extends ISenderAdapter<CrazyEnvoysPlatform, Component, CommandSender> {

    public PaperSenderAdapter() {
        super();
    }

    @Override
    public UUID getUniqueId(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getUniqueId();
        }

        return CrazyPlugin.CONSOLE_UUID;
    }

    @Override
    public String getName(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getName();
        }

        return CrazyPlugin.CONSOLE_NAME;
    }

    @Override
    public void sendMessage(@NotNull final CommandSender sender, @NotNull final Key id, @NotNull final Map<String, String> placeholders) {
        sender.sendMessage(getComponent(sender, id, placeholders));
    }

    @Override
    public Component getComponent(@NotNull final CommandSender sender, @NotNull final Key id, @NotNull final Map<String, String> placeholders) {
        return Component.empty();
    }

    @Override
    public boolean isConsole(@NotNull final CommandSender sender) {
        return sender instanceof ConsoleCommandSender;
    }
}