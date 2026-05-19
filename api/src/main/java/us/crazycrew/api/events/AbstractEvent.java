package us.crazycrew.api.events;

import us.crazycrew.api.CrazyEnvoys;
import us.crazycrew.api.events.interfaces.IEventRegistry;
import java.util.List;

public abstract class AbstractEvent {

    private final CrazyEnvoys plugin = CrazyEnvoys.Provider.getInstance();

    private final IEventRegistry registry = this.plugin.getEventRegistry();

    public void call() {
        this.registry.call(this);
    }

    public abstract List<EventHandler> getHandlers();
}