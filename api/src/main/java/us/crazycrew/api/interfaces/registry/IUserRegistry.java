package us.crazycrew.api.interfaces.registry;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.api.interfaces.user.IUser;
import java.util.Optional;
import java.util.UUID;

public interface IUserRegistry<S> {

    Optional<? extends IUser> getUser(@NotNull final UUID uuid);

    IUser removeUser(@NotNull final UUID uuid);

    IUser addUser(@NotNull final S player);

    IUser getConsole();

}