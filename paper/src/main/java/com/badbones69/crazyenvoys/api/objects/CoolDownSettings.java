package com.badbones69.crazyenvoys.api.objects;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import org.jetbrains.annotations.NotNull;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class CoolDownSettings {

    private final @NotNull CrazyEnvoys plugin = CrazyEnvoys.get();
    private final @NotNull Methods methods = this.plugin.getMethods();

    private final HashMap<UUID, Calendar> cooldown = new HashMap<>();

    public void addCooldown(UUID uuid, String cooldownTimer) {
        this.cooldown.put(uuid, this.methods.getTimeFromString(cooldownTimer));
    }

    public void removeCoolDown(UUID uuid) {
        this.cooldown.remove(uuid);
    }

    public HashMap<UUID, Calendar> getCooldown() {
        return this.cooldown;
    }

    public void clearCoolDowns() {
        this.cooldown.clear();
    }
}