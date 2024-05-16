package com.badbones69.crazyenvoys.support.holograms;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.badbones69.crazyenvoys.api.objects.misc.TierHologram;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
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

    protected final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);
    
    public abstract void createHologram(final Location location, final Tier tier);

    public abstract void removeHologram(final Location location);

    public abstract void removeAllHolograms(final boolean isShutdown);

    public abstract boolean isEmpty();

    protected @NotNull final String name() {
        return this.plugin.getName().toLowerCase() + "-" + UUID.randomUUID();
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

    protected @NotNull final List<String> lines(@NotNull final TierHologram tierHologram) {
        if (tierHologram.getMessages().isEmpty()) return Collections.emptyList();

        final List<String> lines = new ArrayList<>();

        tierHologram.getMessages().forEach(line -> lines.add(color(line)));

        return lines;
    }
}