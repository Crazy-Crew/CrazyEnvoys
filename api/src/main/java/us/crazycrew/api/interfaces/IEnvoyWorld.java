package us.crazycrew.api.interfaces;

import org.jspecify.annotations.NonNull;

public interface IEnvoyWorld {

    void addLocation(@NonNull final String id, final int x, final int y, final int z);

    void removeLocation(@NonNull final String id);

    String getWorld();

    void init();

}