package com.badbones69.crazyenvoy.api.enums;

import com.badbones69.crazyenvoy.CrazyEnvoy;
import com.badbones69.crazyenvoy.Methods;
import com.badbones69.crazyenvoy.api.CrazyManager;
import com.badbones69.crazyenvoy.api.objects.EnvoySettings;
import com.badbones69.crazyenvoy.api.FileManager.Files;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import static com.badbones69.crazyenvoy.api.CrazyManager.getJavaPlugin;

public enum Messages {
    
    LEFT("Left", "%prefix%&6%player% &7has gotten a crate. There are now &6%amount% &7left to find."),
    ENDED("Ended", "%prefix%&cThe crazyenvoy event has ended. Thanks for playing and please come back for the next one."),
    WARNING("Warning", "%prefix%&c[&4ALERT&c] &7There is an crazyenvoy event happening in &6%time%&7."),
    STARTED("Started", "%prefix%&7An crazyenvoy event has just started. &6%amount% &7crates have spawned around spawn for 5m."),
    ON_GOING("Hologram-Placeholders.On-Going", "On Going"),
    RELOADED("Reloaded", "%prefix%&7You have just reloaded all the files."),
    TIME_LEFT("Time-Left", "%prefix%&7The current crazyenvoy has &6%time%&7 left."),
    USED_FLARE("Used-Flare", "%prefix%&7You have just started an crazyenvoy event with a flare."),
    GIVE_FLARE("Give-Flare", "%prefix%&7You have just given &6%player% %amount% &7flares."),
    NEW_CENTER("New-Center", "%prefix%&7You have just set a new center for the random crazyenvoy crates."),
    NOT_ONLINE("Not-Online", "%prefix%&cThat player is not online at this time."),
    NOT_RUNNING("Hologram-Placeholders.Not-Running", "Not Running"),
    NOT_STARTED("Not-Started", "%prefix%&cThere is no crazyenvoy event going on at this time."),
    GIVEN_FLARE("Given-Flare", "%prefix%&7You have been given &6%amount% &7flares."),
    FORCE_START("Force-Start", "%prefix%&7You have just started the crazyenvoy."),
    FORCE_ENDED("Force-Ended", "%prefix%&cYou have just ended the crazyenvoy."),
    DROPS_PAGE("Drops-Page", "%prefix%&7Use /envoy drops [page] to see more."),
    DROPS_FORMAT("Drops-Format", "&7[&6%id%&7]: %world%, %x%, %y%, %z%"),
    DROPS_AVAILABLE("Drops-Available", "%prefix%&7List of all available envoys."),
    DROPS_POSSIBILITIES("Drops-Possibilities", "%prefix%&7List of location envoy's may spawn at."),
    PLAYERS_ONLY("Players-Only", "%prefix%&cOnly players can use that command."),
    NOT_A_NUMBER("Not-A-Number", "%prefix%&cThat is not a number."),
    ADD_LOCATION("Add-Location", "%prefix%&7You have just added a spawn location."),
    COOLDOWN_LEFT("Cooldown-Left", "%prefix%&7You still have &6%time% &7till you can collect another crate."),
    NO_PERMISSION("No-Permission", "%prefix%&cYou do not have permission to use that command."),
    TIME_TILL_EVENT("Time-Till-Event", "%prefix%&7The next crazyenvoy will start in &6%time%&7."),
    CANT_USE_FLARES("Cant-Use-Flares", "%prefix%&cYou do not have permission to use flares."),
    REMOVE_LOCATION("Remove-Location", "%prefix%&cYou have just removed a spawn location."),
    ALREADY_STARTED("Already-Started", "%prefix%&cThere is already an crazyenvoy event running. Please stop it to start a new one."),
    ENTER_EDITOR_MODE("Enter-Editor-Mode", "%prefix%&7You are now in editor mode."),
    LEAVE_EDITOR_MODE("Leave-Editor-Mode", "%prefix%&7You have now left editor mode."),
    NOT_ENOUGH_PLAYERS("Not-Enough-Players", "%prefix%&7Not enough players are online to start the crazyenvoy event. Only &6%amount% &7players are online."),
    STOP_IGNORING_MESSAGES("Stop-Ignoring-Messages", "%prefix%&7You now see all the collecting messages."),
    START_IGNORING_MESSAGES("Start-Ignoring-Messages", "%prefix%&7You are now ignoring the collecting messages."),
    KICKED_FROM_EDITOR_MODE("Kicked-From-Editor-Mode", "%prefix%&cSorry but an crazyenvoy is active. Please stop it or wait till it's over."),
    NOT_IN_WORLD_GUARD_REGION("Not-In-World-Guard-Region", "%prefix%&cYou must be in the WarZone to use a flare."),
    NO_SPAWN_LOCATIONS_FOUND("No-Spawn-Locations-Found", "%prefix%&cNo spawn locations were found and so the event has been cancelled and the cooldown has been reset."),
    HELP("Help", Arrays.asList(
    "&6/envoy help &7- Shows the envoy help menu.",
    "&6/envoy reload &7- Reloads all the config files.",
    "&6/envoy time &7- Shows the time till the envoy starts or ends.",
    "&6/envoy drops [page] &7- Shows all current crate locations.",
    "&6/envoy ignore &7- Shuts up the envoy collecting message.",
    "&6/envoy flare [amount] [player] &7- Give a player a flare to call an envoy event.",
    "&6/envoy edit &7- Edit the crate locations with bedrock.",
    "&6/envoy start &7- Force starts the envoy.",
    "&6/envoy stop &7- Force stops the envoy.",
    "&6/envoy center &7- Set the center of the random crate drops."));
    
    private String path;
    private String defaultMessage;
    private List<String> defaultListMessage;
    private CrazyManager envoy = CrazyManager.getInstance();
    private EnvoySettings envoySettings = EnvoySettings.getInstance();
    
    private Messages(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }
    
    private Messages(String path, List<String> defaultListMessage) {
        this.path = path;
        this.defaultListMessage = defaultListMessage;
    }
    
    public static String convertList(List<String> list) {
        StringBuilder message = new StringBuilder();
        for (String line : list) {
            message.append(Methods.color(line)).append("\n");
        }
        return message.toString();
    }
    
    public static void addMissingMessages() {
        FileConfiguration messages = Files.MESSAGES.getFile();
        boolean saveFile = false;
        for (Messages message : values()) {
            if (!messages.contains("Messages." + message.getPath())) {
                saveFile = true;
                if (message.getDefaultMessage() != null) {
                    messages.set("Messages." + message.getPath(), message.getDefaultMessage());
                } else {
                    messages.set("Messages." + message.getPath(), message.getDefaultListMessage());
                }
            }
        }
        if (saveFile) {
            Files.MESSAGES.saveFile();
        }
    }
    
    public String getMessage() {
        return getMessage(true);
    }
    
    public String getMessage(String placeholder, String replacement) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);
        return getMessage(placeholders, true);
    }
    
    public String getMessage(Map<String, String> placeholders) {
        return getMessage(placeholders, true);
    }
    
    public String getMessageNoPrefix() {
        return getMessage(false);
    }
    
    public String getMessageNoPrefix(String placeholder, String replacement) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);
        return getMessage(placeholders, false);
    }
    
    public String getMessageNoPrefix(Map<String, String> placeholders) {
        return getMessage(placeholders, false);
    }
    
    private String getMessage(boolean prefix) {
        return getMessage(new HashMap<>(), prefix);
    }
    
    private String getMessage(Map<String, String> placeholders, boolean prefix) {
        String message;
        boolean isList = isList();
        boolean exists = exists();
        if (isList) {
            if (exists) {
                message = Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path)));
            } else {
                message = Methods.color(convertList(getDefaultListMessage()));
            }
        } else {
            if (exists) {
                message = Methods.color(Files.MESSAGES.getFile().getString("Messages." + path));
            } else {
                message = Methods.color(getDefaultMessage());
            }
        }
        if (prefix && !isList) {//If the message needs a prefix.
            placeholders.put("%Prefix%", Methods.getPrefix());
        }
        for (Entry<String, String> placeholder : placeholders.entrySet()) {
            message = message.replace(placeholder.getKey(), placeholder.getValue())
            .replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
        }
        return Methods.color(message);
    }
    
    public void sendMessage(Player player) {
        sendMessage(player, new HashMap<>());
    }
    
    public void sendMessage(Player player, Map<String, String> placeholder) {
        player.sendMessage(getMessage(placeholder));
    }
    
    public void sendMessage(CommandSender sender) {
        sendMessage(sender, new HashMap<>());
    }
    
    public void sendMessage(CommandSender sender, Map<String, String> placeholder) {
        sender.sendMessage(getMessage(placeholder));
    }
    
    public void broadcastMessage(boolean ignore) {
        broadcastMessage(ignore, new HashMap<>());
    }
    
    public void broadcastMessage(boolean ignore, Map<String, String> placeholder) {
        if (envoySettings.isWorldMessagesEnabled()) {
            for (Player player : getJavaPlugin().getServer().getOnlinePlayers()) {
                for (String world : envoySettings.getWorldMessagesWorlds()) {
                    if (player.getWorld().getName().equalsIgnoreCase(world)) {
                        if (ignore) {
                            if (!envoy.isIgnoringMessages(player.getUniqueId())) sendMessage(player, placeholder);
                        } else {
                            sendMessage(player, placeholder);
                        }
                    }
                }
            }
        } else {
            for (Player player : getJavaPlugin().getServer().getOnlinePlayers()) {
                if (ignore) {
                    if (!envoy.isIgnoringMessages(player.getUniqueId())) {
                        sendMessage(player, placeholder);
                    }
                } else {
                    sendMessage(player, placeholder);
                }
            }
        }
        getJavaPlugin().getServer().getLogger().log(Level.INFO, getMessage(placeholder));
    }

    private boolean exists() {
        return Files.MESSAGES.getFile().contains("Messages." + path);
    }
    
    private boolean isList() {
        if (Files.MESSAGES.getFile().contains("Messages." + path)) {
            return !Files.MESSAGES.getFile().getStringList("Messages." + path).isEmpty();
        } else {
            return defaultMessage == null;
        }
    }
    
    private String getPath() {
        return path;
    }
    
    private String getDefaultMessage() {
        return defaultMessage;
    }
    
    private List<String> getDefaultListMessage() {
        return defaultListMessage;
    }
}