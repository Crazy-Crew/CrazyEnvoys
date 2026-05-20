package com.badbones69.crazyenvoys.api.enums;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Permissions {

    CRAZYENVOYS_BYPASS("bypass", "Allows players to send virtual keys to another player.", PermissionDefault.OP),
    CRAZYENVOYS_LOCATIONS("locations", "Shows the help menu for Crazy Crates.", PermissionDefault.FALSE),
    CRAZYENVOYS_FLARE_USE("flare.use", "Permission to prevent a player from getting keys.", PermissionDefault.FALSE);

    private final String node;
    private final String description;
    private final PermissionDefault isDefault;
    private final Map<String, Boolean> children;

    /**
     * A constructor to build a permission
     *
     * @param node the default permission
     * @param description the permission description
     */
    Permissions(@NotNull final String node, @NotNull final String description, @NotNull final PermissionDefault isDefault, @NotNull final Map<String, Boolean> children) {
        this.node = node;
        this.description = description;

        this.isDefault = isDefault;

        this.children = children;
    }

    /**
     * A constructor to build a permission
     *
     * @param node the default permission
     * @param description the permission description
     */
    Permissions(@NotNull final String node, @NotNull final String description, @NotNull final PermissionDefault isDefault) {
        this.node = node;
        this.description = description;

        this.isDefault = isDefault;
        this.children = new HashMap<>();
    }

    /**
     * Get a built permission with no action type.
     *
     * @return a completed permission
     */
    public @NotNull final String getPermission() {
        return "envoy." + this.node;
    }

    /**
     * Get the description of the permission.
     *
     * @return the description
     */
    public @NotNull final String getDescription() {
        return this.description;
    }

    public @NotNull final PermissionDefault isDefault() {
        return this.isDefault;
    }

    public @NotNull final Map<String, Boolean> getChildren() {
        return Collections.unmodifiableMap(this.children);
    }

    public final boolean hasPermission(@NotNull final Player player) {
        return player.hasPermission(getPermission());
    }
}