package com.badbones69.crazyenvoys.platform.util;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.enums.PersistentKeys;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.config.impl.ConfigKeys;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MiscUtils {

    private final static @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    public static String getPrefix() {
        return ConfigManager.getConfig().getProperty(ConfigKeys.command_prefix);
    }

    public static ItemStack getItemInHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public static Calendar getTimeFromString(String time) {
        Calendar cal = Calendar.getInstance();

        for (String i : time.split(" ")) {
            if (i.contains("D") || i.contains("d")) cal.add(Calendar.DATE, Integer.parseInt(i.replace("D", "").replace("d", "")));

            if (i.contains("H") || i.contains("h")) cal.add(Calendar.HOUR, Integer.parseInt(i.replace("H", "").replace("h", "")));

            if (i.contains("M") || i.contains("m")) cal.add(Calendar.MINUTE, Integer.parseInt(i.replace("M", "").replace("m", "")));

            if (i.contains("S") || i.contains("s")) cal.add(Calendar.SECOND, Integer.parseInt(i.replace("S", "").replace("s", "")));
        }

        return cal;
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    public static boolean isOnline(String name) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public static Player getPlayer(String name) {
        return plugin.getServer().getPlayer(name);
    }

    public static boolean isInvFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public static void firework(Location loc, List<Color> colors) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(colors).trail(false).flicker(false).build());
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);

        PersistentDataContainer container = firework.getPersistentDataContainer();

        container.set(PersistentKeys.no_firework_damage.getNamespacedKey(), PersistentDataType.BOOLEAN, true);

        detonate(firework);
    }

    private static void detonate(Firework firework) {
        new FoliaRunnable(firework.getScheduler(), null) {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runDelayed(plugin, 2);
    }

    public static List<String> getPage(List<String> list, Integer page) {
        List<String> locations = new ArrayList<>();

        if (page <= 0) page = 1;

        int max = 10;
        int index = page * max - max;
        int endIndex = index >= list.size() ? list.size() - 1 : index + max;

        for (; index < endIndex; index++) {
            if (index < list.size()) locations.add(list.get(index));
        }

        for (; locations.isEmpty(); page--) {
            if (page <= 0) break;

            index = page * max - max;
            endIndex = index >= list.size() ? list.size() - 1 : index + max;

            for (; index < endIndex; index++) {
                if (index < list.size()) locations.add(list.get(index));
            }
        }

        return locations;
    }

    public static boolean isSuccessful(int min, int max) {
        if (max <= min || max <= 0) return true;

        int chance = 1 + new Random().nextInt(max);

        return chance <= min;
    }

    public static List<Entity> getNearbyEntities(Location loc, double x, double y, double z) {
        List<Entity> out = new ArrayList<>();

        if (loc.getWorld() != null) {
            //todo() this needs testing and clarification on if Marker.class is even correct.
            Marker entity = loc.getWorld().spawn(loc.subtract(0, 0, 0), Marker.class, CreatureSpawnEvent.SpawnReason.CUSTOM);

            out = entity.getNearbyEntities(x, y, z);
            entity.remove();
        }

        return out;
    }

    public static boolean isLogging() {
        return ConfigManager.getConfig().getProperty(ConfigKeys.verbose_logging);
    }

    public static void registerCommand(PluginCommand pluginCommand, TabCompleter tabCompleter, CommandExecutor commandExecutor) {
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);

            if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        }
    }

    public static String convertTimeToString(Calendar timeTill) {
        Calendar rightNow = Calendar.getInstance();
        int total = ((int) (timeTill.getTimeInMillis() / 1000) - (int) (rightNow.getTimeInMillis() / 1000));
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;

        for (; total > 86400; total -= 86400, day++) ;
        for (; total > 3600; total -= 3600, hour++) ;
        for (; total >= 60; total -= 60, minute++) ;

        second += total;
        String message = "";

        if (day > 0) message += day + Messages.day.getMessage() + ", ";
        if (day > 0 || hour > 0) message += hour + Messages.hour.getMessage() + ", ";
        if (day > 0 || hour > 0 || minute > 0) message += minute + Messages.minute.getMessage() + ", ";
        if (day > 0 || hour > 0 || minute > 0 || second > 0) message += second + Messages.second.getMessage() + ", ";

        if (message.length() < 2) {
            message = "0" + Messages.second.getMessage();
        } else {
            message = message.substring(0, message.length() - 2);
        }

        return message;
    }

    public static String getUnBuiltLocation(Location location) {
        return "World:" + location.getWorld().getName() + ", X:" + location.getBlockX() + ", Y:" + location.getBlockY() + ", Z:" + location.getBlockZ();
    }

    public static Location getBuiltLocation(String locationString) {
        World w = plugin.getServer().getWorlds().getFirst();
        int x = 0;
        int y = 0;
        int z = 0;

        for (String i : locationString.toLowerCase().split(", ")) {
            if (i.startsWith("world:")) {
                w = plugin.getServer().getWorld(i.replace("world:", ""));
            } else if (i.startsWith("x:")) {
                x = Integer.parseInt(i.replace("x:", ""));
            } else if (i.startsWith("y:")) {
                y = Integer.parseInt(i.replace("y:", ""));
            } else if (i.startsWith("z:")) {
                z = Integer.parseInt(i.replace("z:", ""));
            }
        }

        return new Location(w, x, y, z);
    }

    public static List<String> getPlaceholders(List<String> message, HashMap<String, String> lorePlaceholders) {
        List<String> lore = new ArrayList<>();

        for (String msg : message) {
            for (String placeholder : lorePlaceholders.keySet()) {
                msg = msg.replace(placeholder, lorePlaceholders.get(placeholder)).replace(placeholder.toLowerCase(), lorePlaceholders.get(placeholder));
            }

            lore.add(msg);
        }

        return lore;
    }
}