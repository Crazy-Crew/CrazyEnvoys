package us.crazycrew.crazyenvoys.common.config.migrate;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.common.config.types.MessageKeys;
import java.util.List;

public class LocaleMigrationService extends PlainMigrationService {

    @Override
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        if (!reader.contains(MessageKeys.envoy_started.getPath())) {
            String oldKey = reader.getString("envoys.started");

            if (oldKey != null) {
                configurationData.setValue(MessageKeys.envoy_started, List.of(oldKey));
            }

            return MIGRATION_REQUIRED;
        }

        return NO_MIGRATION_NEEDED;
    }
}