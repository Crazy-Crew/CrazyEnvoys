package com.badbones69.crazyenvoys.api;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.EnvoysPlugin;
import com.badbones69.crazyenvoys.objects.EnvoyWorld;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Server;
import org.bukkit.World;
import org.jspecify.annotations.NonNull;
import java.util.List;

public class PaperEnvoysPlugin extends EnvoysPlugin<Audience, FusionPaper> {

    private final CrazyEnvoys plugin;

    public PaperEnvoysPlugin(@NonNull final CrazyEnvoys plugin, @NonNull final FusionPaper fusion) {
        super(fusion);

        this.plugin = plugin;
    }

    @Override
    public void init() {
        super.init();

        final Server server = this.plugin.getServer();

        final List<World> worlds = server.getWorlds();

        for (final World world : worlds) {
            this.storageHolder.addWorld(new EnvoyWorld(world.getUID(), world.getKey().toString()));
        }
    }

    @Override
    public @NonNull FusionPaper getFusion() {
        return this.fusion;
    }
}