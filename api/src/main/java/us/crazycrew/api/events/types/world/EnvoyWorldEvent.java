package us.crazycrew.api.events.types.world;

import org.jspecify.annotations.NonNull;
import us.crazycrew.api.interfaces.IEnvoyWorld;
import us.crazycrew.api.events.AbstractEvent;
import us.crazycrew.api.events.EventHandler;
import java.util.ArrayList;
import java.util.List;

public class EnvoyWorldEvent extends AbstractEvent {

    private static final List<EventHandler> handlers = new ArrayList<>();

    private final IEnvoyWorld world;

    public EnvoyWorldEvent(@NonNull final IEnvoyWorld world) {
        this.world = world;
    }

    public @NonNull final IEnvoyWorld getWorld() {
        return this.world;
    }

    @Override
    public @NonNull List<EventHandler> getHandlers() {
        return handlers;
    }
}