package us.crazycrew.api.events.types.world.sub;

import org.jspecify.annotations.NonNull;
import us.crazycrew.api.interfaces.IEnvoyWorld;
import us.crazycrew.api.events.types.world.EnvoyWorldEvent;

public class EnvoyWorldUnloadEvent extends EnvoyWorldEvent {

    public EnvoyWorldUnloadEvent(@NonNull final IEnvoyWorld world) {
        super(world);
    }
}