package com.badbones69.crazyenvoys.storage;

import com.badbones69.crazyenvoys.EnvoysPlugin;
import com.badbones69.crazyenvoys.storage.impl.file.SqliteFactory;
import com.badbones69.crazyenvoys.storage.impl.objects.StorageHolder;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import org.jspecify.annotations.NonNull;
import java.nio.file.Path;

public class StorageManager {

    private final EnvoysPlugin plugin;
    private final Path dataPath;

    public StorageManager(@NonNull final EnvoysPlugin plugin) {
        this.plugin = plugin;
        this.dataPath = plugin.getDataPath();
    }

    public StorageHolder init() {
        final String type = "SQLITE".toLowerCase(); //todo() add config option support

        return switch (type) {
            case "sqlite" -> new StorageHolder(new SqliteFactory(this.dataPath.resolve("crazyenvoys.db"))).init();

            default -> throw new FusionException("Unknown Database Type: %s".formatted(type));
        };
    }
}