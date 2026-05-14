package com.badbones69.crazyenvoys.commands.relations.enums;

public enum MigrationType {

    MOJANG_MAPPED_SINGLE("MojangMappedSingle"),
    MOJANG_MAPPED_ALL("MojangMappedAll"),

    LEGACY_COLOR_ALL("LegacyColorAll"),

    WEIGHT_MIGRATION("WeightMigration"),

    NEW_ITEM_FORMAT("NewItemFormat"),

    ENVOYS_DEPRECATED_ALL("EnvoysDeprecated");

    private final String name;

    MigrationType(String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }

    public static MigrationType fromName(final String name) {
        MigrationType type = null;

        for (MigrationType key : MigrationType.values()) {
            if (key.getName().equalsIgnoreCase(name)) {
                type = key;

                break;
            }
        }

        return type;
    }
}