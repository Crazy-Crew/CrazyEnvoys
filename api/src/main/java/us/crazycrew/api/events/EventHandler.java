package us.crazycrew.api.events;

import us.crazycrew.api.events.interfaces.IEventListener;
import org.jspecify.annotations.NullMarked;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("ClassCanBeRecord")
@NullMarked
public final class EventHandler {

    private final IEventListener listener;
    private final Method method;

    public EventHandler(final IEventListener listener, final Method method) {
        this.listener = listener;
        this.method = method;
    }

    public IEventListener getListener() {
        return this.listener;
    }

    public Method getMethod() {
        return this.method;
    }

    public void execute(final AbstractEvent event) {
        this.method.trySetAccessible();

        try {
            this.method.invoke(this.listener, event);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }
}