package com.badbones69.crazyenvoys;

import com.badbones69.crazyenvoys.api.adapters.PlayerAdapter;
import com.badbones69.crazyenvoys.api.adapters.sender.ISenderAdapter;
import com.badbones69.crazyenvoys.registry.EventRegistry;
import com.badbones69.crazyenvoys.storage.StorageManager;
import com.badbones69.crazyenvoys.registry.EnvoyRegistry;
import com.badbones69.crazyenvoys.storage.impl.objects.StorageHolder;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.CrazyEnvoys;
import us.crazycrew.api.adapters.IPlayerAdapter;
import us.crazycrew.api.objects.EnvoyLocation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public abstract class EnvoysPlugin<L, M, S extends Audience, K extends FusionKyori<S>> extends CrazyEnvoys<S, K> {

    public static final UUID CONSOLE_UUID = new UUID(0, 0);
    public static final String CONSOLE_NAME = "Console";

    protected EventRegistry eventRegistry;
    protected StorageHolder storageHolder;
    protected EnvoyRegistry envoyRegistry;
    protected IPlayerAdapter<?> adapter;
    protected final K fusion;

    public EnvoysPlugin(@NonNull final K fusion) {
        this.fusion = fusion;
    }

    public abstract void sendBlockChange(@NonNull final S player, @NonNull final M material);

    public abstract @NonNull Optional<L> toLocation(@NonNull final EnvoyLocation location);

    public abstract ISenderAdapter getSenderAdapter();

    @Override
    public void init() {
        this.fusion.init();

        Provider.register(this);

        try {
            Files.createDirectories(getDataPath());
        } catch (final IOException ignored) {}

        this.eventRegistry = new EventRegistry(this);

        this.envoyRegistry = new EnvoyRegistry();

        try {
            this.storageHolder = new StorageManager(this).init();
        } catch (final Exception exception) {
            this.fusion.log(Level.ERROR, "Failed to initialize storage impl", exception);
        }
    }

    @Override
    public void post() {
        this.adapter = new PlayerAdapter<>(getUserRegistry(), getContextRegistry());
    }

    @Override
    public @NonNull <C> IPlayerAdapter<C> getPlayerAdapter(@NonNull final Class<C> object) {
        return (IPlayerAdapter<C>) this.adapter;
    }

    @Override
    public @NonNull StorageHolder getStorageHolder() {
        return this.storageHolder;
    }

    @Override
    public @NonNull EventRegistry getEventRegistry() {
        return this.eventRegistry;
    }

    @Override
    public @NonNull EnvoyRegistry getEnvoyRegistry() {
        return this.envoyRegistry;
    }

    @Override
    public @NonNull Path getDataPath() {
        return this.fusion.getDataPath();
    }

    @Override
    public @NonNull K getFusion() {
        return this.fusion;
    }
}