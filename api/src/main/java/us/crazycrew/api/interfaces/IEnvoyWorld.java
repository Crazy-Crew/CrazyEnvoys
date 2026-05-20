package us.crazycrew.api.interfaces;

import org.jspecify.annotations.NonNull;
import us.crazycrew.api.objects.EnvoyLocation;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface IEnvoyWorld {

    void addLocation(@NonNull final String id, final int x, final int y, final int z);

    void removeLocation(@NonNull final String id);

    void setCountdown(@NonNull final String countdown);

    void setCenter(@NonNull final EnvoyLocation center);

    @NonNull EnvoyLocation getCenter();

    @NonNull String getCountdown();

    @NonNull Map<String, EnvoyLocation> getActiveMarkers();

    @NonNull Optional<EnvoyLocation> getLocationByCoordinates(final int x, final int y, final int z);

    @NonNull String getWorldAsString();

    @NonNull String getWorldName();

    @NonNull UUID getWorld();

    void init();

}