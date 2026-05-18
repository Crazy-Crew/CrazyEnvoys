package com.badbones69.crazyenvoys.enums;

import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.files.types.configurate.JsonCustomFile;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.BasicConfigurationNode;
import java.nio.file.Path;
import java.util.Optional;

public enum FileKeys {

    locations("locations.json", FileType.JSON);

    private final FusionKyori fusion = (FusionKyori) FusionProvider.getInstance();

    private final FileManager fileManager = this.fusion.getFileManager();

    private final Path dataPath = this.fusion.getDataPath();

    private final FileType type;
    private final Path path;

    FileKeys(@NonNull final String file, @NonNull final FileType type) {
        this.path = this.dataPath.resolve(file);
        this.type = type;
    }

    public @NonNull final BasicConfigurationNode getBasicConfiguration() {
        return getJsonFile().getConfiguration();
    }

    public @NonNull final JsonCustomFile getJsonFile() {
        Optional<JsonCustomFile> key = this.fileManager.getJsonFile(this.path);

        if (key.isEmpty()) {
            throw new FusionException("Cannot find the value in the cache: %s".formatted(this.path));
        }

        return key.get();
    }

    public void save() {
        getJsonFile().save();
    }

    public @NonNull final FileType getType() {
        return this.type;
    }

    public @NonNull final Path getPath() {
        return this.path;
    }
}