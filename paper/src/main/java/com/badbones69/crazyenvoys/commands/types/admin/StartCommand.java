package com.badbones69.crazyenvoys.commands.types.admin;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.types.EnvoyCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class StartCommand extends EnvoyCommand {

    @Command(value = "start", alias = {"begin"})
    @Permission(value = "envoy.start", def = PermissionDefault.OP)
    @Syntax("/envoys start [world]")
    public void start(final CommandSender sender, @Suggestion("worlds") final String name) {
        final NamespacedKey key = NamespacedKey.fromString(name);

        if (key == null) {
            return;
        }

        final World bukkitWorld = this.server.getWorld(key);

        if (bukkitWorld == null) {
            return;
        }

        if (this.envoyRegistry.isEnvoyActive(bukkitWorld.getUID())) {
            Messages.already_started.sendMessage(sender);

            return;
        }

        /*this.envoyRegistry.getWorld(bukkitWorld.getUID()).ifPresent(world -> {
            sender.sendRichMessage("<red>World Name: %s".formatted(world.getWorldName()));

            sender.sendRichMessage("<red>Size: %s".formatted(world.getActiveMarkers().size()));

            sender.sendRichMessage("<red>Countdown: %s".formatted(world.getCountdown()));

            world.getActiveMarkers().forEach((id, marker) -> {
                sender.sendRichMessage("<red>Envoy ID: %s".formatted(id));
                sender.sendRichMessage("<yellow>Envoy Location: %s, %s, %s".formatted(marker.getX(), marker.getY(), marker.getZ()));
            });
        });

        EnvoyStartEvent event;

        Player starter = null;

        if (sender instanceof Player player) {
            event = new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FORCED_START_PLAYER, player);

            starter = player;
        } else {
            event = new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FORCED_START_CONSOLE);
        }

        this.pluginManager.callEvent(event);

        if (!event.isCancelled() && this.crazyManager.startEnvoyEvent(starter)) Messages.force_start.sendMessage(sender);*/
    }
}