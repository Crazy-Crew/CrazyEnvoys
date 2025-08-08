package com.badbones69.crazyenvoys.api.enums;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.nio.file.Path;
import java.util.Optional;

public enum Files {

    users("users.yml");

    private final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final PaperFileManager fileManager = this.plugin.getFileManager();

    private final Path path;

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    Files(final String fileName) {
        this.path = this.plugin.getDataPath().resolve(fileName);
    }

    public final YamlConfiguration getConfiguration() {
        final Optional<PaperCustomFile> key = this.fileManager.getPaperFile(this.path);

        if (key.isEmpty()) {
            throw new FusionException("Cannot find the value in the cache: %s".formatted(this.path));
        }

        return key.get().getConfiguration();
    }

    public void save() {
        final Optional<PaperCustomFile> customFile = this.fileManager.getPaperFile(this.path);

        if (customFile.isEmpty()) return;

        customFile.get().save();
    }

    public void reload() {
        this.fileManager.addPaperFile(this.path, consumer -> {});
    }
}