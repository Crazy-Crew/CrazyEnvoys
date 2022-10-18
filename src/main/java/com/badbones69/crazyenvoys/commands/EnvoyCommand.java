package com.badbones69.crazyenvoys.commands;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.FileManager;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyStartEvent;
import com.badbones69.crazyenvoys.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EnvoyCommand implements CommandExecutor {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final FileManager fileManager = plugin.getFileManager();

    private final Methods methods = plugin.getMethods();

    private final EditorSettings editorSettings = plugin.getEditorSettings();
    private final LocationSettings locationSettings = plugin.getLocationSettings();
    private final FlareSettings flareSettings = plugin.getFlareSettings();

    private final CrazyManager crazyManager = plugin.getCrazyManager();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 0) {
            
            if (!hasPermission(sender, "time")) {
                Messages.NO_PERMISSION.sendMessage(sender);
                return true;
            }

            plugin.getServer().dispatchCommand(sender, "envoy time");
        } else {
            switch (args[0].toLowerCase()) {
                case "help" -> {
                    if (hasPermission(sender, "help")) {
                        Messages.HELP.sendMessage(sender);
                    } else {
                        Messages.NO_PERMISSION.sendMessage(sender);
                    }

                    return true;
                }
                case "reload" -> {
                    if (hasPermission(sender, "reload")) {
                        if (crazyManager.isEnvoyActive()) {
                            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.RELOAD);
                            plugin.getServer().getPluginManager().callEvent(event);
                            crazyManager.endEnvoyEvent();
                        }

                        crazyManager.unload();

                        try {
                            fileManager.setup();
                        } catch (Exception ignored) {}

                        crazyManager.load();
                        Messages.RELOADED.sendMessage(sender);
                    } else {
                        Messages.NO_PERMISSION.sendMessage(sender);
                    }

                    return true;
                }
                case "ignore" -> {
                    if (hasPermission(sender, "ignore")) {
                        if (sender instanceof Player player) {
                            UUID uuid = player.getUniqueId();

                            if (crazyManager.isIgnoringMessages(uuid)) {
                                crazyManager.removeIgnorePlayer(uuid);
                                Messages.STOP_IGNORING_MESSAGES.sendMessage(player);
                            } else {
                                crazyManager.addIgnorePlayer(uuid);
                                Messages.START_IGNORING_MESSAGES.sendMessage(player);
                            }
                        } else {
                            Messages.PLAYERS_ONLY.sendMessage(sender);
                        }
                    } else {
                        Messages.NO_PERMISSION.sendMessage(sender);
                    }

                    return true;
                }
                case "center" -> {
                    if (hasPermission(sender, "center")) {
                        if (sender != plugin.getServer().getConsoleSender()) {
                            crazyManager.setCenter(((Player) sender).getLocation());
                            Messages.NEW_CENTER.sendMessage(sender);
                        } else {
                            Messages.PLAYERS_ONLY.sendMessage(sender);
                        }
                    } else {
                        Messages.NO_PERMISSION.sendMessage(sender);
                    }

                    return true;
                }
                case "flare" -> { // /envoy flare [Amount] [Player]
                    if (hasPermission(sender, "flare.give")) {
                        int amount = 1;
                        Player player;

                        if (args.length >= 2) {
                            if (methods.isInt(args[1])) {
                                amount = Integer.parseInt(args[1]);
                            } else {
                                Messages.NOT_A_NUMBER.sendMessage(sender);
                                return true;
                            }
                        }

                        if (args.length >= 3) {
                            if (methods.isOnline(args[2])) {
                                player = methods.getPlayer(args[2]);
                            } else {
                                Messages.NOT_ONLINE.sendMessage(sender);
                                return true;
                            }
                        } else {
                            if (!(sender instanceof Player)) {
                                Messages.PLAYERS_ONLY.sendMessage(sender);
                                return true;
                            } else {
                                player = (Player) sender;
                            }
                        }

                        HashMap<String, String> placeholder = new HashMap<>();
                        placeholder.put("%player%", player.getName());
                        placeholder.put("%Player%", player.getName());
                        placeholder.put("%amount%", amount + "");
                        placeholder.put("%Amount%", amount + "");
                        Messages.GIVE_FLARE.sendMessage(sender, placeholder);

                        if (!sender.getName().equalsIgnoreCase(player.getName())) Messages.GIVEN_FLARE.sendMessage(player, placeholder);

                        flareSettings.giveFlare(player, amount);
                    } else {
                        Messages.NO_PERMISSION.sendMessage(sender);
                    }

                    return true;
                }
                case "drops", "drop" -> {
                    if (hasPermission(sender, "drops")) {
                        ArrayList<String> locs = new ArrayList<>();
                        int page = 1;

                        if (args.length >= 2) {
                            if (methods.isInt(args[1])) {
                                page = Integer.parseInt(args[1]);
                            } else {
                                Messages.NOT_A_NUMBER.sendMessage(sender);
                                return true;
                            }
                        }

                        int i = 1;
                        HashMap<String, String> ph = new HashMap<>();

                        for (Block block : crazyManager.isEnvoyActive() ? crazyManager.getActiveEnvoys() : locationSettings.getSpawnLocations()) {
                            ph.put("%id%", i + "");
                            ph.put("%world%", block.getWorld().getName());
                            ph.put("%x%", block.getX() + "");
                            ph.put("%y%", block.getY() + "");
                            ph.put("%z%", block.getZ() + "");
                            locs.add(Messages.DROPS_FORMAT.getMessage(ph));
                            i++;
                            ph.clear();
                        }

                        if (crazyManager.isEnvoyActive()) {
                            Messages.DROPS_AVAILABLE.sendMessage(sender);
                        } else {
                            Messages.DROPS_POSSIBILITIES.sendMessage(sender);
                        }

                        for (String dropLocation : methods.getPage(locs, page)) {
                            sender.sendMessage(dropLocation);
                        }

                        if (!crazyManager.isEnvoyActive()) Messages.DROPS_PAGE.sendMessage(sender);

                    } else {
                        Messages.NO_PERMISSION.sendMessage(sender);
                    }

                    return true;
                }
                case "time" -> {
                    if (hasPermission(sender, "time")) {
                        HashMap<String, String> placeholder = new HashMap<>();

                        if (crazyManager.isEnvoyActive()) {
                            placeholder.put("%Time%", crazyManager.getEnvoyRunTimeLeft());
                            Messages.TIME_LEFT.sendMessage(sender, placeholder);
                        } else {
                            placeholder.put("%time%", crazyManager.getNextEnvoyTime());
                            Messages.TIME_TILL_EVENT.sendMessage(sender, placeholder);
                        }
                    } else {
                        Messages.NO_PERMISSION.sendMessage(sender);
                    }

                    return true;
                }
                case "start", "begin" -> {
                    if (hasPermission(sender, "start")) {
                        if (crazyManager.isEnvoyActive()) {
                            Messages.ALREADY_STARTED.sendMessage(sender);
                        } else {
                            EnvoyStartEvent event;

                            if (sender instanceof Player) {
                                event = new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FORCED_START_PLAYER, (Player) sender);
                            } else {
                                event = new EnvoyStartEvent(EnvoyStartEvent.EnvoyStartReason.FORCED_START_CONSOLE);
                            }

                            plugin.getServer().getPluginManager().callEvent(event);

                            if (!event.isCancelled() && crazyManager.startEnvoyEvent()) Messages.FORCE_START.sendMessage(sender);
                        }
                    } else {
                        Messages.NO_PERMISSION.sendMessage(sender);
                    }

                    return true;
                }
                case "stop", "end" -> {
                    if (hasPermission(sender, "stop")) {
                        if (crazyManager.isEnvoyActive()) {
                            EnvoyEndEvent event;

                            if (sender instanceof Player) {
                                event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.FORCED_END_PLAYER, (Player) sender);
                            } else {
                                event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.FORCED_END_CONSOLE);
                            }

                            plugin.getServer().getPluginManager().callEvent(event);
                            crazyManager.endEnvoyEvent();
                            Messages.ENDED.broadcastMessage(false);
                            Messages.FORCE_ENDED.sendMessage(sender);
                        } else {
                            Messages.NOT_STARTED.sendMessage(sender);
                        }
                    } else {
                        Messages.NO_PERMISSION.sendMessage(sender);
                    }

                    return true;
                }
                case "edit" -> {
                    if (hasPermission(sender, "edit")) {
                        if (crazyManager.isEnvoyActive()) {
                            Messages.KICKED_FROM_EDITOR_MODE.sendMessage(sender);
                        } else {
                            Player player = (Player) sender;

                            if (editorSettings.isEditor(player)) {
                                editorSettings.removeEditor(player);
                                editorSettings.removeFakeBlocks();
                                player.getInventory().remove(Material.BEDROCK);
                                Messages.LEAVE_EDITOR_MODE.sendMessage(player);
                            } else {
                                editorSettings.addEditor(player);
                                editorSettings.showFakeBlocks(player);
                                player.getInventory().addItem(new ItemStack(Material.BEDROCK, 1));
                                Messages.ENTER_EDITOR_MODE.sendMessage(player);
                            }
                        }
                    } else {
                        Messages.NO_PERMISSION.sendMessage(sender);
                    }

                    return true;
                }
                case "clear" -> {
                    if (hasPermission(sender, "clear")) {
                        Player player = (Player) sender;

                        if (editorSettings.isEditor(player)) {
                            // User is in editor mode and is able to clear all locations.
                            locationSettings.clearSpawnLocations();
                            Messages.EDITOR_CLEAR_LOCATIONS.sendMessage(sender);
                        } else {
                            // User must be in editor mode to clear locations. This is to help prevent accidental clears.
                            Messages.EDITOR_CLEAR_FAILURE.sendMessage(sender);
                        }
                    } else {
                        Messages.NO_PERMISSION.sendMessage(sender);
                    }

                    return true;
                }
            }

            Messages.COMMAND_NOT_FOUND.sendMessage(sender);
        }

        return true;
    }
    
    private boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission("envoy." + node) || sender.hasPermission("envoy.admin");
    }
}