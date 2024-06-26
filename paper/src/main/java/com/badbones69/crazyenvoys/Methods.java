package com.badbones69.crazyenvoys;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.api.enums.PersistentKeys;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import org.bukkit.entity.Marker;
import org.bukkit.event.entity.CreatureSpawnEvent;
import us.crazycrew.crazyenvoys.core.config.types.ConfigKeys;
import com.badbones69.crazyenvoys.util.MsgUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.core.config.ConfigManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Methods {

    private final @NotNull CrazyEnvoys plugin = CrazyEnvoys.get();

    private final @NotNull SettingsManager config = ConfigManager.getConfig();

    public String getPrefix() {
        return MsgUtils.color(this.config.getProperty(ConfigKeys.command_prefix));
    }

    public String getPrefix(String message) {
        return MsgUtils.color(this.config.getProperty(ConfigKeys.command_prefix) + message);
    }

    public ItemStack getItemInHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public Calendar getTimeFromString(String time) {
        Calendar cal = Calendar.getInstance();

        for (String i : time.split(" ")) {
            if (i.contains("D") || i.contains("d")) cal.add(Calendar.DATE, Integer.parseInt(i.replace("D", "").replace("d", "")));

            if (i.contains("H") || i.contains("h")) cal.add(Calendar.HOUR, Integer.parseInt(i.replace("H", "").replace("h", "")));

            if (i.contains("M") || i.contains("m")) cal.add(Calendar.MINUTE, Integer.parseInt(i.replace("M", "").replace("m", "")));

            if (i.contains("S") || i.contains("s")) cal.add(Calendar.SECOND, Integer.parseInt(i.replace("S", "").replace("s", "")));
        }

        return cal;
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    public boolean isOnline(String name) {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public Player getPlayer(String name) {
        return this.plugin.getServer().getPlayer(name);
    }

    public boolean isInvFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public void firework(Location loc, List<Color> colors) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(colors).trail(false).flicker(false).build());
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);

        PersistentDataContainer container = firework.getPersistentDataContainer();

        container.set(PersistentKeys.no_firework_damage.getNamespacedKey(), PersistentDataType.BOOLEAN, true);

        detonate(firework);
    }

    private void detonate(Firework firework) {
        new FoliaRunnable(this.plugin.getServer().getRegionScheduler(), firework.getLocation()) {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runDelayed(this.plugin, 2);
    }

    public List<String> getPage(List<String> list, Integer page) {
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

    public boolean isSuccessful(final int min, final int max) {
        if (max <= min || max <= 0) return true;

        final int chance = 1 + ThreadLocalRandom.current().nextInt(max);

        return chance <= min;
    }

    public List<Entity> getNearbyEntities(Location loc, double x, double y, double z) {
        List<Entity> out = new ArrayList<>();

        if (loc.getWorld() != null) {
            Marker entity = loc.getWorld().spawn(loc.subtract(0, 0, 0), Marker.class, CreatureSpawnEvent.SpawnReason.CUSTOM);

            out = entity.getNearbyEntities(x, y, z);

            entity.remove();
        }

        return out;
    }

    public String convertTimeToString(Calendar timeTill) {
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

        if (day > 0) message += day + Messages.day.getStringMessage() + ", ";
        if (day > 0 || hour > 0) message += hour + Messages.hour.getStringMessage() + ", ";
        if (day > 0 || hour > 0 || minute > 0) message += minute + Messages.minute.getStringMessage() + ", ";
        if (day > 0 || hour > 0 || minute > 0 || second > 0) message += second + Messages.second.getStringMessage() + ", ";

        if (message.length() < 2) {
            message = "0" + Messages.second.getStringMessage();
        } else {
            message = message.substring(0, message.length() - 2);
        }

        return message;
    }

    public String getUnBuiltLocation(Location location) {
        return "World:" + location.getWorld().getName() + ", X:" + location.getBlockX() + ", Y:" + location.getBlockY() + ", Z:" + location.getBlockZ();
    }

    public Location getBuiltLocation(String locationString) {
        World w = this.plugin.getServer().getWorlds().getFirst();
        int x = 0;
        int y = 0;
        int z = 0;

        for (String i : locationString.toLowerCase().split(", ")) {
            if (i.startsWith("world:")) {
                w = this.plugin.getServer().getWorld(i.replace("world:", ""));
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

    public List<String> getPlaceholders(List<String> message, HashMap<String, String> lorePlaceholders) {
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