package com.badbones69.crazyenvoys.api;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.CrazyPlugin;
import com.badbones69.crazyenvoys.api.enums.Permissions;
import com.badbones69.crazyenvoys.api.registry.PaperContextRegistry;
import com.badbones69.crazyenvoys.api.registry.PaperUserRegistry;
import com.badbones69.crazyenvoys.api.registry.adapters.PaperSenderAdapter;
import com.badbones69.crazyenvoys.api.registry.adapters.PaperUserAdapter;
import com.badbones69.crazyenvoys.commands.CommandManager;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.listeners.EnvoyCacheListener;
import com.badbones69.crazyenvoys.listeners.EnvoyClickListener;
import com.badbones69.crazyenvoys.listeners.EnvoyEditListener;
import com.badbones69.crazyenvoys.listeners.EnvoyWorldListener;
import com.badbones69.crazyenvoys.listeners.FireworkDamageListener;
import com.badbones69.crazyenvoys.listeners.FlareClickListener;
import com.badbones69.crazyenvoys.objects.EnvoyWorld;
import com.badbones69.crazyenvoys.support.MetricsWrapper;
import com.badbones69.crazyenvoys.support.placeholders.PlaceholderAPISupport;
import com.ryderbelserion.fusion.core.api.constants.ModSupport;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.objects.EnvoyLocation;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class CrazyEnvoysPlatform extends CrazyPlugin<Location, Material, Audience, FusionPaper> {

    private final PluginManager pluginManager;
    private final CrazyEnvoys plugin;
    private final Server server;

    public CrazyEnvoysPlatform(@NonNull final CrazyEnvoys plugin, @NonNull final FusionPaper fusion) {
        super(fusion);

        this.plugin = plugin;
        this.server = this.plugin.getServer();
        this.pluginManager = this.server.getPluginManager();
    }

    private PaperContextRegistry contextRegistry;
    private PaperUserRegistry userRegistry;
    private PaperSenderAdapter userAdapter;

    private PaperFileManager fileManager;

    @Override
    public void init() {
        super.init();

        this.fileManager = this.fusion.getFileManager();

        final Path path = getDataPath();

        this.fileManager.addPaperFile(path.resolve("users.yml"))
                .addPaperFolder(path.resolve("tiers"));

        ConfigManager.load(path.toFile(), this.plugin.getComponentLogger());

        this.contextRegistry = new PaperContextRegistry();
        this.userRegistry = new PaperUserRegistry();

        this.userAdapter = new PaperSenderAdapter();

        registerPermissions();

        List.of(
                new EnvoyCacheListener(),
                new EnvoyWorldListener(),
                new EnvoyEditListener(),

                new FireworkDamageListener(),
                new EnvoyClickListener(),
                new FlareClickListener()
        ).forEach(event -> this.pluginManager.registerEvents(event, this.plugin));

        final List<World> worlds = this.server.getWorlds();

        for (final World world : worlds) {
            this.storageHolder.addWorld(new EnvoyWorld(world.getUID(), world.getKey().toString()));
        }

        if (this.fusion.isModReady(ModSupport.placeholder_api)) {
            new PlaceholderAPISupport().register();
        }

        CommandManager.load();

        new MetricsWrapper(4514);

        this.fusion.log(Level.INFO, "Done (%s)!", String.format(Locale.ROOT, "%.3fs", (double) (System.nanoTime() - this.startTime) / 1.0E9D));
    }

    @Override
    public void stop() {
        for (final Player player : this.server.getOnlinePlayers()) {
            final Optional<PaperUserAdapter> optional = this.userRegistry.getUser(player.getUniqueId());

            if (optional.isEmpty()) continue;

            final PaperUserAdapter user = optional.get();

            if (!user.isEditorMode) continue;

            user.isEditorMode = false;

            sendBlockChange(player, Material.AIR);
        }

        /*if (this.crazyManager.isEnvoyActive()) {
            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.SHUTDOWN);

            getServer().getPluginManager().callEvent(event);

            this.crazyManager.endEnvoyEvent();
        }

        this.crazyManager.reload(true);*/
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
    public void registerPermissions() {
        Arrays.stream(Permissions.values()).toList().forEach(permission -> this.pluginManager.addPermission(new Permission(
                permission.getPermission(),
                permission.getDescription(),
                permission.isDefault(),
                permission.getChildren()
        )));
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
    public @NonNull final PaperUserRegistry getUserRegistry() {
        return this.userRegistry;
    }

    @Override
    public @NonNull final FusionPaper getFusion() {
        return this.fusion;
    }
}