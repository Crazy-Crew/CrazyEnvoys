package com.badbones69.crazyenvoys.paper.commands;

import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import com.badbones69.crazyenvoys.paper.Methods;
import com.badbones69.crazyenvoys.paper.api.CrazyManager;
import com.badbones69.crazyenvoys.paper.api.enums.Translation;
import com.badbones69.crazyenvoys.paper.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.paper.api.events.EnvoyStartEvent;
import com.badbones69.crazyenvoys.paper.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.paper.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.paper.api.objects.LocationSettings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EnvoyCommand implements CommandExecutor {

    private final @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final @NotNull Methods methods = this.plugin.getMethods();

    private final @NotNull EditorSettings editorSettings = this.plugin.getEditorSettings();
    private final @NotNull LocationSettings locationSettings = this.plugin.getLocationSettings();
    private final @NotNull FlareSettings flareSettings = this.plugin.getFlareSettings();

    private final @NotNull CrazyManager crazyManager = this.plugin.getCrazyManager();
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (args.length == 0) {
            if (!hasPermission(sender, "time")) {
                Translation.no_permission.sendMessage(sender);
                return true;
            }

            this.plugin.getServer().dispatchCommand(sender, "envoy time");
        } else {
            switch (args[0].toLowerCase()) {
                case "help" -> {
                    if (hasPermission(sender, "help")) {
                        Translation.help.sendMessage(sender);
                    } else {
                        Translation.no_permission.sendMessage(sender);
                    }

                    return true;
                }

                case "reload" -> {
                    if (hasPermission(sender, "reload")) {
                        if (this.crazyManager.isEnvoyActive()) {
                            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.RELOAD);
                            this.plugin.getServer().getPluginManager().callEvent(event);
                            this.crazyManager.endEnvoyEvent();
                        }

                        this.crazyManager.reload(false);

                        Translation.reloaded.sendMessage(sender);
                    } else {
                        Translation.no_permission.sendMessage(sender);
                    }

                    return true;
                }

                case "ignore" -> {
                    if (hasPermission(sender, "ignore")) {
                        if (sender instanceof Player player) {
                            UUID uuid = player.getUniqueId();

                            if (this.crazyManager.isIgnoringMessages(uuid)) {
                                this.crazyManager.removeIgnorePlayer(uuid);
                                Translation.stop_ignoring_messages.sendMessage(player);
                            } else {
                                this.crazyManager.addIgnorePlayer(uuid);
                                Translation.start_ignoring_messages.sendMessage(player);
                            }
                        } else {
                            Translation.player_only.sendMessage(sender);
                        }
                    } else {
                        Translation.no_permission.sendMessage(sender);
                    }

                    return true;
                }

                case "center" -> {
                    if (hasPermission(sender, "center")) {
                        if (sender != plugin.getServer().getConsoleSender()) {
                            this.crazyManager.setCenter(((Player) sender).getLocation());
                            Translation.new_center.sendMessage(sender);
                        } else {
                            Translation.player_only.sendMessage(sender);
                        }
                    } else {
                        Translation.no_permission.sendMessage(sender);
                    }

                    return true;
                }

                case "flare" -> { // /envoy flare [Amount] [Player]
                    if (hasPermission(sender, "flare.give")) {
                        int amount = 1;
                        Player player;

                        if (args.length >= 2) {
                            if (this.methods.isInt(args[1])) {
                                amount = Integer.parseInt(args[1]);
                            } else {
                                Translation.not_a_number.sendMessage(sender);
                                return true;
                            }
                        }

                        if (args.length >= 3) {
                            if (this.methods.isOnline(args[2])) {
                                player = this.methods.getPlayer(args[2]);
                            } else {
                                Translation.not_online.sendMessage(sender);
                                return true;
                            }
                        } else {
                            if (!(sender instanceof Player)) {
                                Translation.player_only.sendMessage(sender);
                                return true;
                            } else {
                                player = (Player) sender;
                            }
                        }

                        HashMap<String, String> placeholder = new HashMap<>();
                        placeholder.put("{player}", player.getName());
                        placeholder.put("{amount}", amount + "");
                        Translation.give_flare.sendMessage(sender, placeholder);

                        if (!sender.getName().equalsIgnoreCase(player.getName())) Translation.given_flare.sendMessage(player, placeholder);

                        this.flareSettings.giveFlare(player, amount);
                    } else {
                        Translation.no_permission.sendMessage(sender);
                    }

                    return true;
                }

                case "drops", "drop" -> {
                    if (hasPermission(sender, "drops")) {
                        ArrayList<String> locs = new ArrayList<>();
                        int page = 1;

                        if (args.length >= 2) {
                            if (this.methods.isInt(args[1])) {
                                page = Integer.parseInt(args[1]);
                            } else {
                                Translation.not_a_number.sendMessage(sender);
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
                            locs.add(Translation.drops_format.getMessage(placeholders).toString());
                            amount++;
                            placeholders.clear();
                        }

                        if (this.crazyManager.isEnvoyActive()) {
                            Translation.drops_available.sendMessage(sender);
                        } else {
                            Translation.drops_possibilities.sendMessage(sender);
                        }

                        for (String dropLocation : this.methods.getPage(locs, page)) {
                            sender.sendMessage(dropLocation);
                        }

                        if (!this.crazyManager.isEnvoyActive()) Translation.drops_page.sendMessage(sender);

                    } else {
                        Translation.no_permission.sendMessage(sender);
                    }

                    return true;
                }

                case "time" -> {
                    if (hasPermission(sender, "time")) {
                        HashMap<String, String> placeholder = new HashMap<>();

                        if (this.crazyManager.isEnvoyActive()) {
                            placeholder.put("{time}", this.crazyManager.getEnvoyRunTimeLeft());
                            Translation.time_left.sendMessage(sender, placeholder);
                        } else {
                            placeholder.put("{time}", this.crazyManager.getNextEnvoyTime());
                            Translation.time_till_event.sendMessage(sender, placeholder);
                        }
                    } else {
                        Translation.no_permission.sendMessage(sender);
                    }

                    return true;
                }

                case "start", "begin" -> {
                    if (hasPermission(sender, "start")) {
                        if (this.crazyManager.isEnvoyActive()) {
                            Translation.already_started.sendMessage(sender);
                        } else {
                            EnvoyStartEvent event;

                            if (sender instanceof Player) {
                                event = new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FORCED_START_PLAYER, (Player) sender);
                            } else {
                                event = new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FORCED_START_CONSOLE);
                            }

                            this.plugin.getServer().getPluginManager().callEvent(event);

                            if (!event.isCancelled() && this.crazyManager.startEnvoyEvent()) Translation.force_start.sendMessage(sender);
                        }
                    } else {
                        Translation.no_permission.sendMessage(sender);
                    }

                    return true;
                }

                case "stop", "end" -> {
                    if (hasPermission(sender, "stop")) {
                        if (this.crazyManager.isEnvoyActive()) {
                            EnvoyEndEvent event;

                            if (sender instanceof Player) {
                                event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.FORCED_END_PLAYER, (Player) sender);
                            } else {
                                event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.FORCED_END_CONSOLE);
                            }

                            this.plugin.getServer().getPluginManager().callEvent(event);
                            this.crazyManager.endEnvoyEvent();
                            Translation.ended.broadcastMessage(false);
                            Translation.force_end.sendMessage(sender);
                        } else {
                            Translation.not_started.sendMessage(sender);
                        }
                    } else {
                        Translation.no_permission.sendMessage(sender);
                    }

                    return true;
                }

                case "edit" -> {
                    if (hasPermission(sender, "edit")) {
                        if (this.crazyManager.isEnvoyActive()) {
                            Translation.kicked_from_editor_mode.sendMessage(sender);
                        } else {
                            Player player = (Player) sender;

                            if (this.editorSettings.isEditor(player)) {
                                this.editorSettings.removeEditor(player);
                                this.editorSettings.removeFakeBlocks();
                                player.getInventory().remove(Material.BEDROCK);
                                Translation.leave_editor_mode.sendMessage(player);
                            } else {
                                this.editorSettings.addEditor(player);
                                this.editorSettings.showFakeBlocks(player);
                                player.getInventory().addItem(new ItemStack(Material.BEDROCK, 1));
                                Translation.enter_editor_mode.sendMessage(player);
                            }
                        }
                    } else {
                        Translation.no_permission.sendMessage(sender);
                    }

                    return true;
                }

                case "clear" -> {
                    if (hasPermission(sender, "clear")) {
                        Player player = (Player) sender;

                        if (this.editorSettings.isEditor(player)) {
                            // User is in editor mode and is able to clear all locations.
                            this.locationSettings.clearSpawnLocations();
                            Translation.editor_clear_locations.sendMessage(sender);
                        } else {
                            // User must be in editor mode to clear locations. This is to help prevent accidental clears.
                            Translation.editor_clear_failure.sendMessage(sender);
                        }
                    } else {
                        Translation.no_permission.sendMessage(sender);
                    }

                    return true;
                }
            }

            Translation.command_not_found.sendMessage(sender);
        }

        return true;
    }
    
    private boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission("envoy." + node) || sender.hasPermission("envoy.admin");
    }
}