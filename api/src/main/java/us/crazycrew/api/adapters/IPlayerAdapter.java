package us.crazycrew.api.adapters;

import org.jspecify.annotations.NonNull;
import us.crazycrew.api.interfaces.user.IUser;
import java.util.Optional;

public interface IPlayerAdapter<T> {

    @NonNull Optional<? extends IUser> getUser(@NonNull final T player);

}