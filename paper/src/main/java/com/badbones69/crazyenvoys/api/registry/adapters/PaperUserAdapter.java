package com.badbones69.crazyenvoys.api.registry.adapters;

import com.badbones69.crazyenvoys.CrazyPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.api.interfaces.user.IUser;
import java.util.UUID;

public class PaperUserAdapter extends IUser {

    protected Player player;

    public PaperUserAdapter(@Nullable final CommandSender sender) {
        if (sender instanceof Player reference) {
            this.player = reference;
        }
    }

    public boolean isEditorMode = false;

    public PaperUserAdapter() {
        this(null);
    }

    @Override
    public final boolean isEditorMode() {
        return this.isEditorMode;
    }

    @Override
    public @NotNull final UUID getUniqueId() {
        return this.player == null ? CrazyPlugin.CONSOLE_UUID : this.player.getUniqueId();
    }

    @Override
    public @NotNull final String getUsername() {
        return this.player == null ? CrazyPlugin.CONSOLE_NAME : this.player.getName();
    }
}