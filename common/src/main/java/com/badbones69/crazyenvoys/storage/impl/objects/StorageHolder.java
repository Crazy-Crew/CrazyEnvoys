package com.badbones69.crazyenvoys.storage.impl.objects;

import com.badbones69.crazyenvoys.objects.EnvoyWorld;
import com.badbones69.crazyenvoys.storage.impl.ConnectionFactory;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.storage.IStorageHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class StorageHolder extends IStorageHolder<EnvoyWorld> {

    private final ConnectionFactory factory;

    public StorageHolder(@NonNull final ConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void addWorld(@NonNull final EnvoyWorld world) {
        CompletableFuture.runAsync(() -> {
           try (final Connection connection = this.factory.getConnection()) {
               try (final PreparedStatement statement = connection.prepareStatement(
                       "insert into envoy_worlds(world, countdown, x, y, z)"
               )) {
                   statement.setString(1, world.getWorld());

                   statement.setString(2, "0000000000000");

                   statement.setInt(3, 0);
                   statement.setInt(4, 0);
                   statement.setInt(5, 0);
               }
            } catch (final SQLException exception) {
               exception.printStackTrace();
           }
        });
    }

    @Override
    public @NonNull final StorageHolder init() {
        this.factory.init();

        return this;
    }

    @Override
    public void stop() {
        this.factory.stop();
    }
}