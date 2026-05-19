package com.badbones69.crazyenvoys.registry;

import com.badbones69.crazyenvoys.EnvoysPlugin;
import us.crazycrew.api.events.AbstractEvent;
import us.crazycrew.api.events.interfaces.IEventHandler;
import us.crazycrew.api.events.interfaces.IEventListener;
import us.crazycrew.api.events.EventHandler;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jspecify.annotations.NullMarked;
import us.crazycrew.api.events.interfaces.IEventRegistry;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@NullMarked
public final class EventRegistry implements IEventRegistry {

    private final FusionKyori fusion;

    public EventRegistry(final EnvoysPlugin plugin) {
        this.fusion = plugin.getFusion();
    }

    @Override
    public void call(final AbstractEvent event) {
        for (final EventHandler handler : event.getHandlers()) {
            try {
                this.fusion.log(Level.INFO, "<yellow>Executing the event %s", handler);

                handler.execute(event);
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void register(final IEventListener listener) {
        for (final Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(IEventHandler.class)) continue;

            final Class<?>[] parameters = method.getParameterTypes();

            if (parameters.length == 0) continue;

            final Class<?> klass = parameters[0];

            if (!AbstractEvent.class.isAssignableFrom(klass)) {
                continue;
            }

            map(true, klass).ifPresent(event -> {
                final List<EventHandler> events = event.getHandlers();

                final EventHandler handler = new EventHandler(listener, method);

                if (events.remove(handler)) {
                    this.fusion.log(Level.INFO, "<yellow>%s which was previously registered as an event is now removed", handler);
                }

                events.add(handler);

                this.fusion.log(Level.INFO, "<yellow>%s has been registered as a new event!", handler);
            });
        }
    }

    @Override
    public Optional<AbstractEvent> map(final boolean skipChecks, final Class<?> event) {
        if (!skipChecks && !AbstractEvent.class.isAssignableFrom(event)) {
            return Optional.empty();
        }

        return Optional.of(event).map(AbstractEvent.class::cast);
    }
}