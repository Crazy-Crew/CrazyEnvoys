package us.crazycrew.crazyenvoys.core.config.migrate;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.core.enums.FileProperty;

public class ConfigMigration extends PlainMigrationService {

    @Override
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return FileProperty.command_prefix.moveString(reader, configurationData)
                | FileProperty.falling_toggle.moveBoolean(reader, configurationData)
                | FileProperty.falling_block.moveString(reader, configurationData)
                | FileProperty.falling_height.moveInteger(reader, configurationData)
                | FileProperty.max_crate_toggle.moveBoolean(reader, configurationData)
                | FileProperty.random_amount.moveBoolean(reader, configurationData)
                | FileProperty.min_crates.moveInteger(reader, configurationData)
                | FileProperty.max_crates.moveInteger(reader, configurationData)
                | FileProperty.random_locations.moveBoolean(reader, configurationData)
                | FileProperty.max_radius.moveInteger(reader, configurationData)
                | FileProperty.min_radius.moveInteger(reader, configurationData)
                | FileProperty.locations_broadcast.moveBoolean(reader, configurationData)
                | FileProperty.run_time.moveString(reader, configurationData)
                | FileProperty.run_time_toggle.moveBoolean(reader, configurationData)
                | FileProperty.cooldown_toggle.moveBoolean(reader, configurationData)
                | FileProperty.envoys_cooldown.moveString(reader, configurationData)
                | FileProperty.envoys_time.moveString(reader, configurationData)
                | FileProperty.envoys_ignore_empty_server.moveBoolean(reader, configurationData)
                | FileProperty.minimum_players_toggle.moveBoolean(reader, configurationData)
                | FileProperty.minimum_players_count.moveInteger(reader, configurationData)
                | FileProperty.minimum_flare_toggle.moveBoolean(reader, configurationData)
                | FileProperty.minimum_flare_count.moveInteger(reader, configurationData)
                | FileProperty.envoys_flare_item_name.moveString(reader, configurationData)
                | FileProperty.envoys_flare_item_type.moveString(reader, configurationData)
                | FileProperty.envoys_flare_item_lore.moveList(reader, configurationData)
                | FileProperty.envoys_flare_world_guard_toggle.moveBoolean(reader, configurationData)
                | FileProperty.envoys_flare_world_guard_regions.moveList(reader, configurationData)
                | FileProperty.envoys_announce_player_pickup.moveBoolean(reader, configurationData)
                | FileProperty.envoys_grab_cooldown_toggle.moveBoolean(reader, configurationData)
                | FileProperty.envoys_grab_cooldown_timer.moveString(reader, configurationData)
                | FileProperty.envoys_grace_period_toggle.moveBoolean(reader, configurationData)
                | FileProperty.envoys_grace_period_timer.moveInteger(reader, configurationData)
                | FileProperty.envoys_grace_period_unlocked.moveString(reader, configurationData)
                | FileProperty.envoys_grace_period_time_unit.moveString(reader, configurationData)
                | FileProperty.envoys_world_messages.moveBoolean(reader, configurationData)
                | FileProperty.envoys_allowed_worlds.moveList(reader, configurationData)
                | FileProperty.envoys_warnings.moveList(reader, configurationData);
    }
}