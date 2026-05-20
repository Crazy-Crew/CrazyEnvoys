package com.badbones69.crazyenvoys;

import com.badbones69.crazyenvoys.registry.EventRegistry;
import com.badbones69.crazyenvoys.storage.StorageManager;
import com.badbones69.crazyenvoys.registry.EnvoyRegistry;
import com.badbones69.crazyenvoys.storage.impl.objects.StorageHolder;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.CrazyEnvoys;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class EnvoysPlugin<S, K extends FusionKyori<S>> extends CrazyEnvoys<S, K> {

    protected EventRegistry eventRegistry;
    protected StorageHolder storageHolder;
    protected EnvoyRegistry envoyRegistry;
    protected final K fusion;

    public EnvoysPlugin(@NonNull final K fusion) {
        this.fusion = fusion;
    }

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