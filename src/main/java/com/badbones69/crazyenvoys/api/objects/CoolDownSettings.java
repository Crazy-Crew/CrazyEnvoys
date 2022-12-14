package com.badbones69.crazyenvoys.api.objects;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class CoolDownSettings {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();
    private final Methods methods = plugin.getMethods();

    private final HashMap<UUID, Calendar> cooldown = new HashMap<>();

    public void addCooldown(UUID uuid, String cooldownTimer) {
        cooldown.put(uuid, methods.getTimeFromString(cooldownTimer));
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
}