package us.crazycrew.api.events.types.world.sub;

import org.jspecify.annotations.NonNull;
import us.crazycrew.api.interfaces.IEnvoyWorld;
import us.crazycrew.api.events.types.world.EnvoyWorldEvent;

public class EnvoyWorldLoadEvent extends EnvoyWorldEvent {

    public EnvoyWorldLoadEvent(@NonNull final IEnvoyWorld world) {
        super(world);
    }
}