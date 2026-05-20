package us.crazycrew.api.interfaces;

import org.jspecify.annotations.NonNull;
import java.util.UUID;

public interface IEnvoyWorld {

    void addLocation(@NonNull final String id, final int x, final int y, final int z);

    void removeLocation(@NonNull final String id);

    void setCountdown(@NonNull final String countdown);

    String getCountdown();

    String getWorldAsString();

    String getWorldName();

    UUID getWorld();

    void init();

}