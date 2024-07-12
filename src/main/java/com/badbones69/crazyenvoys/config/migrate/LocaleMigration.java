package com.badbones69.crazyenvoys.config.migrate;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import com.badbones69.crazyenvoys.api.enums.Properties;
import org.jetbrains.annotations.NotNull;

public class LocaleMigration extends PlainMigrationService {

    @Override
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return Properties.no_permission.moveString(reader, configurationData)
                | Properties.no_claim_permission.moveString(reader, configurationData)
                | Properties.player_only.moveString(reader, configurationData)
                | Properties.not_online.moveString(reader, configurationData)
                | Properties.not_a_number.moveString(reader, configurationData)
                | Properties.plugin_reloaded.moveString(reader, configurationData)
                | Properties.already_started.moveString(reader, configurationData)
                | Properties.force_started.moveString(reader, configurationData)
                | Properties.force_ended.moveString(reader, configurationData)
                | Properties.not_started.moveString(reader, configurationData)
                | Properties.warning.moveString(reader, configurationData)
                | Properties.started.moveList(reader, configurationData)
                | Properties.left.moveString(reader, configurationData)
                | Properties.ended.moveString(reader, configurationData)
                | Properties.not_enough_players.moveString(reader, configurationData)
                | Properties.enter_editor_mode.moveString(reader, configurationData)
                | Properties.leave_editor_mode.moveString(reader, configurationData)
                | Properties.clear_locations.moveString(reader, configurationData)
                | Properties.failed_clear_locations.moveString(reader, configurationData)
                | Properties.kicked_from_editor.moveString(reader, configurationData)
                | Properties.add_location.moveString(reader, configurationData)
                | Properties.remove_location.moveString(reader, configurationData)
                | Properties.time_left.moveString(reader, configurationData)
                | Properties.time_till_event.moveString(reader, configurationData)
                | Properties.envoy_used_flare.moveString(reader, configurationData)
                | Properties.envoy_cant_use_flare.moveString(reader, configurationData)
                | Properties.envoy_give_flare.moveString(reader, configurationData)
                | Properties.envoy_given_flare.moveString(reader, configurationData)
                | Properties.new_center.moveString(reader, configurationData)
                | Properties.not_in_worldguard_region.moveString(reader, configurationData)
                | Properties.start_ignoring_messages.moveString(reader, configurationData)
                | Properties.stop_ignoring_messages.moveString(reader, configurationData)
                | Properties.cooldown_left.moveString(reader, configurationData)
                | Properties.countdown_in_progress.moveString(reader, configurationData)
                | Properties.drops_page.moveString(reader, configurationData)
                | Properties.drops_format.moveString(reader, configurationData)
                | Properties.no_spawn_locations.moveString(reader, configurationData)
                | Properties.command_not_found.moveString(reader, configurationData)
                | Properties.hologram_active.moveString(reader, configurationData)
                | Properties.hologram_not_active.moveString(reader, configurationData)
                | Properties.placeholder_day.moveString(reader, configurationData)
                | Properties.placeholder_hour.moveString(reader, configurationData)
                | Properties.placeholder_minute.moveString(reader, configurationData)
                | Properties.placeholder_second.moveString(reader, configurationData)
                | Properties.crate_locations.moveString(reader, configurationData)
                | Properties.location_format.moveString(reader, configurationData)
                | Properties.help.moveList(reader, configurationData);
    }
}