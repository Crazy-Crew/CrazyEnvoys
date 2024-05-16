package com.badbones69.crazyenvoys.api.objects;

import com.badbones69.crazyenvoys.platform.util.MiscUtils;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoolDownSettings {

    private final Map<UUID, Calendar> cooldown = new HashMap<>();

    public void addCooldown(UUID uuid, String cooldownTimer) {
        this.cooldown.put(uuid, MiscUtils.getTimeFromString(cooldownTimer));
    }

    public void removeCoolDown(UUID uuid) {
        this.cooldown.remove(uuid);
    }

    public Map<UUID, Calendar> getCooldown() {
        return this.cooldown;
    }

    public void clearCoolDowns() {
        this.cooldown.clear();
    }
}