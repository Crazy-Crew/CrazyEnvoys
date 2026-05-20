package com.badbones69.crazyenvoys.api;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.EnvoysPlugin;
import com.badbones69.crazyenvoys.api.registry.PaperContextRegistry;
import com.badbones69.crazyenvoys.api.registry.PaperUserRegistry;
import com.badbones69.crazyenvoys.api.registry.adapters.PaperSenderAdapter;
import com.badbones69.crazyenvoys.listeners.EnvoyCacheListener;
import com.badbones69.crazyenvoys.objects.EnvoyWorld;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.objects.EnvoyLocation;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PaperEnvoysPlugin extends EnvoysPlugin<Location, Material, Audience, FusionPaper> {

    private final CrazyEnvoys plugin;
    private final Server server;

    public PaperEnvoysPlugin(@NonNull final CrazyEnvoys plugin, @NonNull final FusionPaper fusion) {
        super(fusion);

        this.plugin = plugin;
        this.server = this.plugin.getServer();
    }

    private PaperContextRegistry contextRegistry;
    private PaperUserRegistry userRegistry;
    private PaperSenderAdapter userAdapter;

    @Override
    public void init() {
        super.init();

        this.contextRegistry = new PaperContextRegistry();
        this.userRegistry = new PaperUserRegistry();

        this.userAdapter = new PaperSenderAdapter();

        final PluginManager pluginManager = this.server.getPluginManager();

        List.of(
            new EnvoyCacheListener()
        ).forEach(event -> pluginManager.registerEvents(event, this.plugin));

        final List<World> worlds = this.server.getWorlds();

        for (final World world : worlds) {
            this.storageHolder.addWorld(new EnvoyWorld(world.getUID(), world.getKey().toString()));
        }
    }

    @Override
    public void sendBlockChange(@NonNull final Audience sender, @NonNull final Material material) {
        if (!(sender instanceof Player player)) {
            this.fusion.log(Level.WARNING, "You must be a player to use this method!");

            return;
        }

        this.envoyRegistry.getWorld(player.getWorld().getUID()).ifPresent(world -> {
            final Map<String, EnvoyLocation> markers = world.getActiveMarkers();

            for (final EnvoyLocation marker : markers.values()) {
                toLocation(marker).ifPresent(location -> player.sendBlockChange(location, material.createBlockData()));
            }
        });
    }

    @Override
    public @NonNull Optional<Location> toLocation(@NonNull final EnvoyLocation location) {
        final World world = this.server.getWorld(location.getWorld());

        if (world == null) {
            return Optional.empty();
        }

        return Optional.of(new Location(world, location.getX(), location.getY(), location.getZ()));
    }

    @Override
    public @NonNull final PaperContextRegistry getContextRegistry() {
        return this.contextRegistry;
    }

    @Override
    public @NonNull final PaperSenderAdapter getSenderAdapter() {
        return this.userAdapter;
    }

    @Override
    public @NonNull PaperUserRegistry getUserRegistry() {
        return this.userRegistry;
    }

    @Override
    public @NonNull FusionPaper getFusion() {
        return this.fusion;
    }
}