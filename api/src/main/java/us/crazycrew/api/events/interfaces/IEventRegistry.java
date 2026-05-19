package us.crazycrew.api.events.interfaces;

import us.crazycrew.api.events.AbstractEvent;
import java.util.Optional;

public interface IEventRegistry {

    void register(final IEventListener listener);

    void call(final AbstractEvent event);

    Optional<AbstractEvent> map(final boolean skipChecks, final Class<?> event);

}