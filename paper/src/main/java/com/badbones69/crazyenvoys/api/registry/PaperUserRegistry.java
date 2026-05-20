package com.badbones69.crazyenvoys.api.registry;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.EnvoysPlugin;
import com.badbones69.crazyenvoys.api.registry.adapters.PaperUserAdapter;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.api.interfaces.registry.IUserRegistry;
import us.crazycrew.api.interfaces.user.IUser;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PaperUserRegistry implements IUserRegistry<Player> {

    private final Map<UUID, PaperUserAdapter> users = new HashMap<>();

    private final CrazyEnvoys plugin = CrazyEnvoys.get();

    @Override
    public PaperUserAdapter addUser(@NotNull final Player player) {
        final PaperUserAdapter adapter = new PaperUserAdapter(player);

        return this.users.putIfAbsent(player.getUniqueId(), adapter);
    }

    @Override
    public PaperUserAdapter removeUser(@NotNull final UUID uuid) {
        return this.users.remove(uuid);
    }

    @Override
    public Optional<PaperUserAdapter> getUser(@NotNull final UUID uuid) {
        final Server server = this.plugin.getServer();

        final Player player = server.getPlayer(uuid);

        if (!this.users.containsKey(uuid) && player != null) {
            return Optional.of(addUser(player));
        }

        return Optional.of(this.users.get(uuid));
    }

    @Override
    public @NotNull final IUser getConsole() {
        return this.users.get(EnvoysPlugin.CONSOLE_UUID);
    }
}