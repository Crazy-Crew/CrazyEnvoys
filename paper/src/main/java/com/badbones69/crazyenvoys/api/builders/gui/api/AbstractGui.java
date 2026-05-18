package com.badbones69.crazyenvoys.api.builders.gui.api;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractGui {

    protected @NotNull final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    protected @NotNull final FusionPaper fusion = this.plugin.getFusion();

    protected @NotNull final Server server = this.plugin.getServer();

    protected final Player player;
    protected final Tier tier;
    protected final int size;
    protected String title;

    public AbstractGui(@NotNull final Player player, @NotNull final Tier tier, @NotNull final String title, final int size) {
        this.player = player;
        this.tier = tier;
        this.title = title;
        this.size = size;
    }

    public abstract void build();
}