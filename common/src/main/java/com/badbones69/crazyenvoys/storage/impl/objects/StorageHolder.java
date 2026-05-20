package com.badbones69.crazyenvoys.storage.impl.objects;

import com.badbones69.crazyenvoys.objects.EnvoyWorld;
import com.badbones69.crazyenvoys.registry.EnvoyRegistry;
import com.badbones69.crazyenvoys.storage.impl.ConnectionFactory;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.objects.EnvoyLocation;
import us.crazycrew.api.storage.IStorageHolder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StorageHolder extends IStorageHolder<EnvoyWorld> {

    private final EnvoyRegistry envoyRegistry;
    private final ConnectionFactory factory;
    private final FusionKyori fusion;

    public StorageHolder(@NonNull final ConnectionFactory factory, @NonNull final FusionKyori fusion, @NonNull final EnvoyRegistry envoyRegistry) {
        this.envoyRegistry = envoyRegistry;
        this.factory = factory;
        this.fusion = fusion;
    }

    @Override
    public boolean hasWorld(@NonNull final EnvoyWorld world) {
        return CompletableFuture.supplyAsync(() -> {
            boolean hasWorld = false;

            try (final Connection connection = this.factory.getConnection()) {
                try (final PreparedStatement statement =
                             connection.prepareStatement("select 1 from envoy_worlds where world=?")) {
                    statement.setString(1, world.getWorldAsString());

                    final ResultSet rs = statement.executeQuery();

                    if (rs.next()) {
                        hasWorld = true;

                        return hasWorld;
                    }
                }
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }

            return hasWorld;
        }).join();
    }

    @Override
    public void addWorld(@NonNull final EnvoyWorld world) {
        if (tableExists("envoy_worlds") && hasWorld(world)) {
            this.fusion.log(Level.WARNING, "<red>The world <yellow>%s <red>already is in the database!", world.getWorldName());

            this.envoyRegistry.addWorld(world);

            return;
        }

        CompletableFuture.runAsync(() -> {
           try (final Connection connection = this.factory.getConnection(); final PreparedStatement statement =
                   connection.prepareStatement("insert into envoy_worlds(world, name, countdown, x, y, z) values(?, ?, ?, ?, ?, ?)")) {
               statement.setString(1, world.getWorldAsString());
               statement.setString(2, world.getWorldName());

               statement.setString(3, world.getCountdown());

               statement.setInt(4, 0);
               statement.setInt(5, 0);
               statement.setInt(6, 0);

               statement.executeUpdate();

               this.envoyRegistry.addWorld(world);
            } catch (final SQLException exception) {
               exception.printStackTrace();
           }
        });
    }

    @Override
    public void populate(@NonNull final EnvoyWorld world) {
        CompletableFuture.runAsync(() -> {
            try (final Connection connection = this.factory.getConnection(); final PreparedStatement statement =
                    connection.prepareStatement("select countdown from envoy_worlds where world=?")) {
                statement.setString(1, world.getWorldAsString());

                final ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    world.setCountdown(rs.getString("countdown"));
                }
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void setCountdown(@NonNull final EnvoyWorld world, @NonNull final String countdown) {
        final EnvoyWorld index = world;

        CompletableFuture.runAsync(() -> {
           try (final Connection connection = this.factory.getConnection(); final PreparedStatement statement =
                   connection.prepareStatement("update envoy_worlds set countdown=? where world=?")) {
               statement.setString(1, countdown);
               statement.setString(2, index.getWorldAsString());

               statement.executeUpdate();

               index.setCountdown(countdown);

               this.envoyRegistry.updateWorld(index);
           } catch (final SQLException exception) {
               exception.printStackTrace();
           }
        });
    }

    @Override
    public void setCenter(@NonNull final EnvoyWorld world, @NonNull final EnvoyLocation location) {
        final EnvoyWorld index = world;

        CompletableFuture.runAsync(() -> {
           try (final Connection connection = this.factory.getConnection(); final PreparedStatement statement =
                   connection.prepareStatement("update envoy_worlds set x=?, y=?, z=? where world=?")) {
               statement.setInt(1, location.getX());
               statement.setInt(2, location.getY());
               statement.setInt(3, location.getZ());

               statement.setString(4, index.getWorldAsString());

               statement.executeUpdate();

               index.setCenter(location);

               this.envoyRegistry.updateWorld(index);
           } catch (final SQLException exception) {
               exception.printStackTrace();
           }
        });
    }

    @Override
    public void addLocation(@NonNull final EnvoyWorld world, final int x, final int y, final int z) {
        final EnvoyWorld index = world;

        final String id = UUID.randomUUID().toString();

        CompletableFuture.runAsync(() -> {
           try (final Connection connection = this.factory.getConnection(); final PreparedStatement statement =
                   connection.prepareStatement("insert into envoy_locations(id, world, name, x, y, z) values(?, ?, ?, ?, ?, ?)")) {

               statement.setString(1, id);
               statement.setString(2, index.getWorldAsString());
               statement.setString(3, index.getWorldName());
               statement.setInt(4, x);
               statement.setInt(5, y);
               statement.setInt(6, z);

               statement.executeUpdate();

               index.addLocation(id, x, y, z);
           } catch (final SQLException exception) {
               exception.printStackTrace();
           }
        });
    }

    @Override
    public boolean removeLocation(@NonNull final String id) {
        return CompletableFuture.supplyAsync(() -> {
            boolean isValid = false;

            try (final Connection connection = this.factory.getConnection(); final PreparedStatement statement =
                    connection.prepareStatement("delete from envoy_locations where id=?")) {

                statement.setString(1, id);

                statement.executeUpdate();

                isValid = true;

                return isValid;
            } catch (final SQLException exception) {
                exception.printStackTrace();

                return isValid;
            }
        }).join();
    }

    @Override
    public @NonNull final StorageHolder init() {
        this.factory.init();

        return this;
    }

    @Override
    public boolean tableExists(@NonNull final String table) {
        try (final ResultSet resultSet = this.factory.getConnection().getMetaData().getTables(
                null,
                null,
                table,
                null
        )) {
            return resultSet.next();
        } catch (SQLException exception) {
            exception.printStackTrace();

            return false;
        }
    }

    @Override
    public void stop() {
        this.factory.stop();
    }
}