package com.badbones69.crazyenvoys.api.builders.gui.api;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyEnvoysPlatform;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public abstract class AbstractGui {

    protected @NonNull final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    protected @NonNull final CrazyEnvoysPlatform platform = this.plugin.getPlatform();

    protected @NonNull final FusionPaper fusion = this.platform.getFusion();

    protected @NonNull final Server server = this.plugin.getServer();

    protected final Player player;
    protected final Tier tier;
    protected final int size;
    protected String title;

    public AbstractGui(@NonNull final Player player, @NonNull final Tier tier, @NonNull final String title, final int size) {
        this.player = player;
        this.tier = tier;
        this.title = title;
        this.size = size;
    }

    public abstract void build();
}