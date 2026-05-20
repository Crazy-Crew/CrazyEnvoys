package com.badbones69.crazyenvoys.storage;

import com.badbones69.crazyenvoys.EnvoysPlugin;
import com.badbones69.crazyenvoys.registry.EnvoyRegistry;
import com.badbones69.crazyenvoys.storage.impl.file.SqliteFactory;
import com.badbones69.crazyenvoys.storage.impl.objects.StorageHolder;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jspecify.annotations.NonNull;
import java.nio.file.Path;

public class StorageManager {

    private final EnvoyRegistry registry;
    private final FusionKyori fusion;
    private final Path dataPath;

    public StorageManager(@NonNull final EnvoysPlugin plugin) {
        this.registry = plugin.getEnvoyRegistry();
        this.dataPath = plugin.getDataPath();
        this.fusion = plugin.getFusion();
    }

    public StorageHolder init() {
        final String type = "SQLITE".toLowerCase(); //todo() add config option support

        return switch (type) {
            case "sqlite" -> new StorageHolder(new SqliteFactory(this.dataPath.resolve("crazyenvoys.db")), this.fusion, this.registry).init();

            default -> throw new FusionException("Unknown Database Type: %s".formatted(type));
        };
    }
}