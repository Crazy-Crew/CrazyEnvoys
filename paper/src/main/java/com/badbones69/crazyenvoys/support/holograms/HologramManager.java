package com.badbones69.crazyenvoys.support.holograms;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public abstract class HologramManager {

    protected CrazyEnvoys plugin = CrazyEnvoys.get();
    
    public abstract void createHologram(final Location location, final Tier tier, final String id);

    public abstract void removeHologram(final String id);

    public abstract boolean exists(final String id);

    public abstract void purge(final boolean isShutdown);

    public abstract String getName();

    protected @NotNull final String name() {
        return this.plugin.getName().toLowerCase() + "-" + UUID.randomUUID();
    }

    protected @NotNull final String name(final String id) {
        return this.plugin.getName().toLowerCase() + "-" + id;
    }

    protected @NotNull final Vector getVector(@NotNull final Tier tier) {
        return new Vector(0.5, tier.getHoloHeight(), 0.5);
    }

    protected @Nullable final String color(@NotNull final String message) {
        if (message.isEmpty()) return null;

        final Matcher matcher = Pattern.compile("#[a-fA-F\\d]{6}").matcher(message);
        final StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    protected @NotNull final List<String> lines(@NotNull final Tier tier) {
        if (tier.getHoloMessage().isEmpty()) return Collections.emptyList();

        final List<String> lines = new ArrayList<>();

        tier.getHoloMessage().forEach(line -> lines.add(color(line)));

        return lines;
    }
}