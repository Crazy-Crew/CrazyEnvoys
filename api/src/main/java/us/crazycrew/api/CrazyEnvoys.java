package us.crazycrew.api;

import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.adapters.IPlayerAdapter;
import us.crazycrew.api.events.interfaces.IEventRegistry;
import us.crazycrew.api.interfaces.registry.IContextRegistry;
import us.crazycrew.api.interfaces.registry.IEnvoyRegistry;
import us.crazycrew.api.interfaces.registry.IUserRegistry;
import us.crazycrew.api.storage.IStorageHolder;
import java.nio.file.Path;

public abstract class CrazyEnvoys<S, K extends FusionKyori<S>> {

    public abstract <C> @NonNull IPlayerAdapter<C> getPlayerAdapter(@NonNull final Class<C> object);

    public abstract @NonNull IContextRegistry getContextRegistry();

    public abstract @NonNull IStorageHolder getStorageHolder();

    public abstract @NonNull IEventRegistry getEventRegistry();

    public abstract @NonNull IEnvoyRegistry getEnvoyRegistry();

    public abstract @NonNull IUserRegistry getUserRegistry();

    public abstract @NonNull Path getDataPath();

    public abstract @NonNull K getFusion();

    public abstract void post();

    public abstract void init();

    public abstract void stop();

    public static class Provider {
        private static CrazyEnvoys instance;

        @ApiStatus.Internal
        private Provider() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }

        public static CrazyEnvoys getInstance() {
            return instance;
        }

        @ApiStatus.Internal
        public static void register(@NonNull final CrazyEnvoys instance) {
            Provider.instance = instance;
        }

        @ApiStatus.Internal
        public static void unregister() {
            Provider.instance = null;
        }
    }
}