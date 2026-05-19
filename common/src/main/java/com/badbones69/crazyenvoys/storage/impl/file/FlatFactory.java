package com.badbones69.crazyenvoys.storage.impl.file;

import com.badbones69.crazyenvoys.storage.impl.ConnectionFactory;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;

public abstract class FlatFactory extends ConnectionFactory {

    protected HikariDataSource source;
    protected final Path path;

    public FlatFactory(@NotNull final Path path) {
        this.path = path;
    }

    protected abstract String url();

    @Override
    public @Nullable Connection getConnection() throws SQLException {
        if (this.source.isClosed()) {
            throw new FusionException("Failed to get connection from pool. (Source returned closed)");
        }

        return this.source.getConnection();
    }

    @Override
    public void init() {
        CompletableFuture.runAsync(() -> {
            try (final Connection connection = getConnection()) {
                if (connection == null) return;

                try (final Statement statement = connection.createStatement()) {
                    statement.addBatch(this.create_envoy_locations_table);
                    statement.addBatch(this.create_envoy_worlds_table);

                    statement.executeBatch();
                }
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void stop() {
        try {
            final Connection connection = getConnection();

            if (connection != null) {
                connection.close();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean isRunning() {
        return this.source != null && this.source.isRunning();
    }

    public @NotNull final Path getPath() {
        return this.path;
    }
}