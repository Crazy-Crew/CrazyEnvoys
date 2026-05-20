package com.badbones69.crazyenvoys.api.adapters;

import org.jspecify.annotations.NonNull;
import us.crazycrew.api.adapters.IPlayerAdapter;
import us.crazycrew.api.interfaces.registry.IContextRegistry;
import us.crazycrew.api.interfaces.registry.IUserRegistry;
import us.crazycrew.api.interfaces.user.IUser;
import java.util.Optional;

public class PlayerAdapter<P> implements IPlayerAdapter<P> {

    private final IUserRegistry<?> userRegistry;
    private final IContextRegistry<P> contextRegistry;

    public PlayerAdapter(@NonNull final IUserRegistry<?> userRegistry, @NonNull final IContextRegistry<P> contextRegistry) {
        this.userRegistry = userRegistry;
        this.contextRegistry = contextRegistry;
    }

    @Override
    public @NonNull final Optional<? extends IUser> getUser(@NonNull final P player) {
        return this.userRegistry.getUser(this.contextRegistry.getUUID(player));
    }
}