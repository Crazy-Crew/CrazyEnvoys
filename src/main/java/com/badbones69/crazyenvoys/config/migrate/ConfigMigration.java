package com.badbones69.crazyenvoys.config.migrate;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import com.badbones69.crazyenvoys.api.enums.Properties;
import org.jetbrains.annotations.NotNull;

public class ConfigMigration extends PlainMigrationService {

    @Override
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return Properties.command_prefix.moveString(reader, configurationData)
                | Properties.falling_toggle.moveBoolean(reader, configurationData)
                | Properties.falling_block.moveString(reader, configurationData)
                | Properties.falling_height.moveInteger(reader, configurationData)
                | Properties.max_crate_toggle.moveBoolean(reader, configurationData)
                | Properties.random_amount.moveBoolean(reader, configurationData)
                | Properties.min_crates.moveInteger(reader, configurationData)
                | Properties.max_crates.moveInteger(reader, configurationData)
                | Properties.random_locations.moveBoolean(reader, configurationData)
                | Properties.max_radius.moveInteger(reader, configurationData)
                | Properties.min_radius.moveInteger(reader, configurationData)
                | Properties.locations_broadcast.moveBoolean(reader, configurationData)
                | Properties.run_time.moveString(reader, configurationData)
                | Properties.run_time_toggle.moveBoolean(reader, configurationData)
                | Properties.cooldown_toggle.moveBoolean(reader, configurationData)
                | Properties.envoys_cooldown.moveString(reader, configurationData)
                | Properties.envoys_time.moveString(reader, configurationData)
                | Properties.envoys_ignore_empty_server.moveBoolean(reader, configurationData)
                | Properties.minimum_players_toggle.moveBoolean(reader, configurationData)
                | Properties.minimum_players_count.moveInteger(reader, configurationData)
                | Properties.minimum_flare_toggle.moveBoolean(reader, configurationData)
                | Properties.minimum_flare_count.moveInteger(reader, configurationData)
                | Properties.envoys_flare_item_name.moveString(reader, configurationData)
                | Properties.envoys_flare_item_type.moveString(reader, configurationData)
                | Properties.envoys_flare_item_lore.moveList(reader, configurationData)
                | Properties.envoys_flare_world_guard_toggle.moveBoolean(reader, configurationData)
                | Properties.envoys_flare_world_guard_regions.moveList(reader, configurationData)
                | Properties.envoys_announce_player_pickup.moveBoolean(reader, configurationData)
                | Properties.envoys_grab_cooldown_toggle.moveBoolean(reader, configurationData)
                | Properties.envoys_grab_cooldown_timer.moveString(reader, configurationData)
                | Properties.envoys_grace_period_toggle.moveBoolean(reader, configurationData)
                | Properties.envoys_grace_period_timer.moveInteger(reader, configurationData)
                | Properties.envoys_grace_period_unlocked.moveString(reader, configurationData)
                | Properties.envoys_grace_period_time_unit.moveString(reader, configurationData)
                | Properties.envoys_world_messages.moveBoolean(reader, configurationData)
                | Properties.envoys_allowed_worlds.moveList(reader, configurationData)
                | Properties.envoys_warnings.moveList(reader, configurationData);
    }
}