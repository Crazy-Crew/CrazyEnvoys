package com.badbones69.crazyenvoys.commands;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyStartEvent;
import com.badbones69.crazyenvoys.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import org.bukkit.command.ConsoleCommandSender;
import us.crazycrew.crazyenvoys.other.MsgUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EnvoyCommand implements CommandExecutor {

    private final @NotNull CrazyEnvoys plugin = CrazyEnvoys.get();

    private final @NotNull Methods methods = this.plugin.getMethods();

    private final @NotNull EditorSettings editorSettings = this.plugin.getEditorSettings();
    private final @NotNull LocationSettings locationSettings = this.plugin.getLocationSettings();
    private final @NotNull FlareSettings flareSettings = this.plugin.getFlareSettings();

    private final @NotNull CrazyManager crazyManager = this.plugin.getCrazyManager();
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (args.length == 0) {
            if (!hasPermission(sender, "time") || !(sender instanceof ConsoleCommandSender)) {
                Messages.no_permission.sendMessage(sender);
                return true;
            }

            this.plugin.getServer().dispatchCommand(sender, "envoy time");
        } else {
            switch (args[0].toLowerCase()) {
                case "help" -> {
                    if (!hasPermission(sender, "help") && !(sender instanceof ConsoleCommandSender)) {
                        Messages.no_permission.sendMessage(sender);
                        return true;
                    }

                    Messages.help.sendMessage(sender);

                    return true;
                }

                case "reload" -> {
                    if (!hasPermission(sender, "reload") && !(sender instanceof ConsoleCommandSender)) {
                        Messages.no_permission.sendMessage(sender);
                        return true;
                    }

                    if (this.crazyManager.isEnvoyActive()) {
                        EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.RELOAD);
                        this.plugin.getServer().getPluginManager().callEvent(event);
                        this.crazyManager.endEnvoyEvent();
                    }

                    this.plugin.getFileManager().setup();

                    this.crazyManager.reload(false);

                    Messages.reloaded.sendMessage(sender);

                    return true;
                }

                case "ignore" -> {
                    if (!(sender instanceof Player player)) {
                        Messages.player_only.sendMessage(sender);
                        return true;
                    }

                    if (!hasPermission(player, "ignore")) {
                        Messages.no_permission.sendMessage(player);
                        return true;
                    }

                    UUID uuid = player.getUniqueId();

                    if (this.crazyManager.isIgnoringMessages(uuid)) {
                        this.crazyManager.removeIgnorePlayer(uuid);
                        Messages.stop_ignoring_messages.sendMessage(player);
                    } else {
                        this.crazyManager.addIgnorePlayer(uuid);
                        Messages.start_ignoring_messages.sendMessage(player);
                    }

                    return true;
                }

                case "center" -> {
                    if (!(sender instanceof Player player)) {
                        Messages.player_only.sendMessage(sender);
                        return true;
                    }

                    if (!hasPermission(player, "center")) {
                        Messages.no_permission.sendMessage(player);
                        return true;
                    }

                    this.crazyManager.setCenter(player.getLocation());
                    Messages.new_center.sendMessage(player);

                    return true;
                }

                case "flare" -> { // /envoy flare [Amount] [Player]
                    if (!hasPermission(sender, "flare.give") && !(sender instanceof ConsoleCommandSender)) {
                        Messages.no_permission.sendMessage(sender);
                        return true;
                    }

                    int amount = 1;
                    Player player;

                    if (args.length >= 2) {
                        if (this.methods.isInt(args[1])) {
                            amount = Integer.parseInt(args[1]);
                        } else {
                            Messages.not_a_number.sendMessage(sender);
                            return true;
                        }
                    }

                    if (args.length >= 3) {
                        if (this.methods.isOnline(args[2])) {
                            player = this.methods.getPlayer(args[2]);
                        } else {
                            Messages.not_online.sendMessage(sender);
                            return true;
                        }
                    } else {
                        if (!(sender instanceof Player)) {
                            Messages.player_only.sendMessage(sender);
                            return true;
                        } else {
                            player = (Player) sender;
                        }
                    }

                    HashMap<String, String> placeholder = new HashMap<>();
                    placeholder.put("{player}", player.getName());
                    placeholder.put("{amount}", amount + "");
                    Messages.give_flare.sendMessage(sender, placeholder);

                    if (!sender.getName().equalsIgnoreCase(player.getName())) Messages.given_flare.sendMessage(player, placeholder);

                    this.flareSettings.giveFlare(player, amount);

                    return true;
                }

                case "drops", "drop" -> {
                    if (!hasPermission(sender, "drops") && !(sender instanceof ConsoleCommandSender)) {
                        Messages.no_permission.sendMessage(sender);
                        return true;
                    }

                    ArrayList<String> locs = new ArrayList<>();
                    int page = 1;

                    if (args.length >= 2) {
                        if (this.methods.isInt(args[1])) {
                            page = Integer.parseInt(args[1]);
                        } else {
                            Messages.not_a_number.sendMessage(sender);
                            return true;
                        }
                    }

                    int amount = 1;
                    HashMap<String, String> placeholders = new HashMap<>();

                    for (Block block : this.crazyManager.isEnvoyActive() ? this.crazyManager.getActiveEnvoys() : this.locationSettings.getSpawnLocations()) {
                        placeholders.put("{id}", String.valueOf(amount));
                        placeholders.put("{world}", block.getWorld().getName());
                        placeholders.put("{x}", String.valueOf(block.getX()));
                        placeholders.put("{y}", String.valueOf(block.getY()));
                        placeholders.put("{z}", String.valueOf(block.getZ()));
                        locs.add(Messages.drops_format.getStringMessage(placeholders));
                        amount++;
                        placeholders.clear();
                    }

                    if (this.crazyManager.isEnvoyActive()) {
                        Messages.drops_available.sendMessage(sender);
                    } else {
                        Messages.drops_possibilities.sendMessage(sender);
                    }

                    for (String dropLocation : this.methods.getPage(locs, page)) {
                        sender.sendMessage(MsgUtils.color(dropLocation));
                    }

                    if (!this.crazyManager.isEnvoyActive()) Messages.drops_page.sendMessage(sender);

                    return true;
                }

                case "time" -> {
                    if (!hasPermission(sender, "time") && !(sender instanceof ConsoleCommandSender)) {
                        Messages.no_permission.sendMessage(sender);
                        return true;
                    }

                    HashMap<String, String> placeholder = new HashMap<>();

                    if (this.crazyManager.isEnvoyActive()) {
                        placeholder.put("{time}", this.crazyManager.getEnvoyRunTimeLeft());
                        Messages.time_left.sendMessage(sender, placeholder);
                    } else {
                        placeholder.put("{time}", this.crazyManager.getNextEnvoyTime());
                        Messages.time_till_event.sendMessage(sender, placeholder);
                    }

                    return true;
                }

                case "start", "begin" -> {
                    if (!hasPermission(sender, "start") && !(sender instanceof ConsoleCommandSender)) {
                        Messages.no_permission.sendMessage(sender);
                        return true;
                    }

                    if (this.crazyManager.isEnvoyActive()) {
                        Messages.already_started.sendMessage(sender);
                        return true;
                    }

                    EnvoyStartEvent event;

                    if (sender instanceof Player player) {
                        event = new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FORCED_START_PLAYER, player);
                    } else {
                        event = new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FORCED_START_CONSOLE);
                    }

                    this.plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled() && this.crazyManager.startEnvoyEvent()) Messages.force_start.sendMessage(sender);

                    return true;
                }

                case "stop", "end" -> {
                    if (!hasPermission(sender, "stop") && !(sender instanceof ConsoleCommandSender)) {
                        Messages.no_permission.sendMessage(sender);
                        return true;
                    }

                    if (!this.crazyManager.isEnvoyActive()) {
                        Messages.not_started.sendMessage(sender);
                        return true;
                    }

                    EnvoyEndEvent event;

                    if (sender instanceof Player player) {
                        event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.FORCED_END_PLAYER, player);
                    } else {
                        event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.FORCED_END_CONSOLE);
                    }

                    this.plugin.getServer().getPluginManager().callEvent(event);
                    this.crazyManager.endEnvoyEvent();
                    Messages.ended.broadcastMessage(false);
                    Messages.force_end.sendMessage(sender);

                    return true;
                }

                case "edit" -> {
                    if (!(sender instanceof Player player)) {
                        Messages.player_only.sendMessage(sender);
                        return true;
                    }

                    if (!hasPermission(player, "edit")) {
                        Messages.no_permission.sendMessage(player);
                        return true;
                    }

                    if (!this.crazyManager.isEnvoyActive()) {
                        Messages.kicked_from_editor_mode.sendMessage(player);
                        return true;
                    }

                    if (this.editorSettings.isEditor(player)) {
                        this.editorSettings.removeEditor(player);
                        this.editorSettings.removeFakeBlocks();
                        player.getInventory().remove(Material.BEDROCK);
                        Messages.leave_editor_mode.sendMessage(player);
                    } else {
                        this.editorSettings.addEditor(player);
                        this.editorSettings.showFakeBlocks(player);
                        player.getInventory().addItem(new ItemStack(Material.BEDROCK, 1));
                        Messages.enter_editor_mode.sendMessage(player);
                    }

                    return true;
                }

                case "clear" -> {
                    if (!(sender instanceof Player player)) {
                        Messages.player_only.sendMessage(sender);
                        return true;
                    }

                    if (!hasPermission(player, "clear")) {
                        Messages.no_permission.sendMessage(player);
                        return true;
                    }

                    if (this.editorSettings.isEditor(player)) {
                        // User is in editor mode and is able to clear all locations.
                        this.locationSettings.clearSpawnLocations();
                        Messages.editor_clear_locations.sendMessage(player);
                    } else {
                        // User must be in editor mode to clear locations. This is to help prevent accidental clears.
                        Messages.editor_clear_failure.sendMessage(player);
                    }

                    return true;
                }
            }

            Messages.command_not_found.sendMessage(sender);
        }

        return true;
    }
    
    private boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission("envoy." + node) || sender.hasPermission("envoy.admin");
    }
}