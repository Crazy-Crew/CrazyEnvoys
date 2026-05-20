package com.badbones69.crazyenvoys.api.registry;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.api.interfaces.registry.IContextRegistry;
import java.util.UUID;

public class PaperContextRegistry implements IContextRegistry<Player> {

    @Override
    public @NotNull final UUID getUUID(@NotNull final Player player) {
        return player.getUniqueId();
    }
}