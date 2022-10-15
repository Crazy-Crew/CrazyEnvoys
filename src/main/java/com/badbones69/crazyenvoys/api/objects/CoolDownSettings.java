package com.badbones69.crazyenvoys.api.objects;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class CoolDownSettings {

    private final HashMap<UUID, Calendar> cooldown = new HashMap<>();

    public void addCooldown(UUID uuid, String cooldownTimer) {
        cooldown.put(uuid, getTimeFromString(cooldownTimer));
    }

    public void removeCoolDown(UUID uuid) {
        cooldown.remove(uuid);
    }

    public HashMap<UUID, Calendar> getCooldown() {
        return cooldown;
    }

    public void clearCoolDowns() {
        cooldown.clear();
    }

    private Calendar getTimeFromString(String time) {
        Calendar cal = Calendar.getInstance();

        for (String i : time.split(" ")) {
            if (i.contains("D") || i.contains("d")) cal.add(Calendar.DATE, Integer.parseInt(i.replace("D", "").replace("d", "")));

            if (i.contains("H") || i.contains("h")) cal.add(Calendar.HOUR, Integer.parseInt(i.replace("H", "").replace("h", "")));

            if (i.contains("M") || i.contains("m")) cal.add(Calendar.MINUTE, Integer.parseInt(i.replace("M", "").replace("m", "")));

            if (i.contains("S") || i.contains("s")) cal.add(Calendar.SECOND, Integer.parseInt(i.replace("S", "").replace("s", "")));
        }

        return cal;
    }
}