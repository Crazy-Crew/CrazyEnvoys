package us.crazycrew.api.storage;

import org.jspecify.annotations.NonNull;
import us.crazycrew.api.interfaces.IEnvoyWorld;
import us.crazycrew.api.objects.EnvoyLocation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class IStorageHolder<E extends IEnvoyWorld> {

    public abstract boolean hasWorld(@NonNull final E world);

    public abstract void addWorld(@NonNull final E world);

    public abstract void setCountdown(@NonNull final E world, @NonNull final String countdown);

    public abstract void setCenter(@NonNull final E world, @NonNull final EnvoyLocation location);

    public abstract void addLocation(@NonNull final E world, final int x, final int y, final int z);

    public abstract boolean removeLocation(@NonNull final String id);

    public abstract void populate(@NonNull final E world);

    public abstract @NonNull IStorageHolder init();

    public abstract boolean tableExists(@NonNull final String table);

    public abstract void stop();

    protected List<String> getTables(@NonNull final Connection connection) throws SQLException {
        final List<String> tables = new ArrayList<>();

        try (final ResultSet result = connection.getMetaData().getTables(connection.getCatalog(), null, "%", null)) {
            while (result.next()) {
                tables.add(result.getString(3).toLowerCase(Locale.ROOT));
            }
        }

        return tables;
    }
}