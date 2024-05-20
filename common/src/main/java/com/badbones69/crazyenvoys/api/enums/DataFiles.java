package com.badbones69.crazyenvoys.api.enums;

import com.badbones69.crazyenvoys.config.ConfigManager;
import com.ryderbelserion.vital.core.config.YamlFile;
import org.jetbrains.annotations.NotNull;

public enum DataFiles {

    data("users.yml");

    private final String fileName;
    private final YamlFile yamlFile;

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    DataFiles(@NotNull final String fileName) {
        this.fileName = fileName;
        this.yamlFile = ConfigManager.getYamlManager().getFile(this.fileName);
    }

    /**
     * @return {@link String}
     */
    public @NotNull final String getFileName() {
        return this.fileName;
    }

    /**
     * @return {@link YamlFile}
     */
    public @NotNull final YamlFile getYamlFile() {
        return this.yamlFile;
    }

    public void save() {
        ConfigManager.getYamlManager().saveFile(getFileName());
    }
}