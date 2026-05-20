package us.crazycrew.api.interfaces.registry;

import org.jspecify.annotations.NonNull;
import us.crazycrew.api.interfaces.IEnvoyWorld;
import java.util.Optional;
import java.util.UUID;

public interface IEnvoyRegistry<E extends IEnvoyWorld> {

    void addWorld(@NonNull final E world);

    void removeWorld(@NonNull final UUID world);

    void updateWorld(@NonNull final E world);

    boolean hasWorld(@NonNull final UUID world);

    @NonNull Optional<E> getWorld(@NonNull final UUID world);

    boolean isEnvoyActive(@NonNull final UUID world);

}