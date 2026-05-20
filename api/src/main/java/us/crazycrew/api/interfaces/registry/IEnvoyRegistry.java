package us.crazycrew.api.interfaces.registry;

import org.jspecify.annotations.NonNull;
import us.crazycrew.api.interfaces.IEnvoyWorld;

import java.util.Optional;
import java.util.UUID;

public interface IEnvoyRegistry<E extends IEnvoyWorld> {

    void addWorld(@NonNull final E world);

    void removeWorld(@NonNull final UUID world);

    Optional<E> getWorld(@NonNull final UUID world);

}