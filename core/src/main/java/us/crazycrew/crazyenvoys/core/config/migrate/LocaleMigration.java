package us.crazycrew.crazyenvoys.core.config.migrate;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.core.enums.FileProperty;

public class LocaleMigration extends PlainMigrationService {

    @Override
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return FileProperty.no_permission.moveString(reader, configurationData)
                | FileProperty.no_claim_permission.moveString(reader, configurationData)
                | FileProperty.player_only.moveString(reader, configurationData)
                | FileProperty.not_online.moveString(reader, configurationData)
                | FileProperty.not_a_number.moveString(reader, configurationData)
                | FileProperty.plugin_reloaded.moveString(reader, configurationData)
                | FileProperty.already_started.moveString(reader, configurationData)
                | FileProperty.force_started.moveString(reader, configurationData)
                | FileProperty.force_ended.moveString(reader, configurationData)
                | FileProperty.not_started.moveString(reader, configurationData)
                | FileProperty.warning.moveString(reader, configurationData)
                | FileProperty.started.moveList(reader, configurationData)
                | FileProperty.left.moveString(reader, configurationData)
                | FileProperty.ended.moveString(reader, configurationData)
                | FileProperty.not_enough_players.moveString(reader, configurationData)
                | FileProperty.enter_editor_mode.moveString(reader, configurationData)
                | FileProperty.leave_editor_mode.moveString(reader, configurationData)
                | FileProperty.clear_locations.moveString(reader, configurationData)
                | FileProperty.failed_clear_locations.moveString(reader, configurationData)
                | FileProperty.kicked_from_editor.moveString(reader, configurationData)
                | FileProperty.add_location.moveString(reader, configurationData)
                | FileProperty.remove_location.moveString(reader, configurationData)
                | FileProperty.time_left.moveString(reader, configurationData)
                | FileProperty.time_till_event.moveString(reader, configurationData)
                | FileProperty.envoy_used_flare.moveString(reader, configurationData)
                | FileProperty.envoy_cant_use_flare.moveString(reader, configurationData)
                | FileProperty.envoy_give_flare.moveString(reader, configurationData)
                | FileProperty.envoy_given_flare.moveString(reader, configurationData)
                | FileProperty.new_center.moveString(reader, configurationData)
                | FileProperty.not_in_worldguard_region.moveString(reader, configurationData)
                | FileProperty.start_ignoring_messages.moveString(reader, configurationData)
                | FileProperty.stop_ignoring_messages.moveString(reader, configurationData)
                | FileProperty.cooldown_left.moveString(reader, configurationData)
                | FileProperty.countdown_in_progress.moveString(reader, configurationData)
                | FileProperty.drops_page.moveString(reader, configurationData)
                | FileProperty.drops_format.moveString(reader, configurationData)
                | FileProperty.no_spawn_locations.moveString(reader, configurationData)
                | FileProperty.command_not_found.moveString(reader, configurationData)
                | FileProperty.hologram_active.moveString(reader, configurationData)
                | FileProperty.hologram_not_active.moveString(reader, configurationData)
                | FileProperty.placeholder_day.moveString(reader, configurationData)
                | FileProperty.placeholder_hour.moveString(reader, configurationData)
                | FileProperty.placeholder_minute.moveString(reader, configurationData)
                | FileProperty.placeholder_second.moveString(reader, configurationData)
                | FileProperty.crate_locations.moveString(reader, configurationData)
                | FileProperty.location_format.moveString(reader, configurationData)
                | FileProperty.help.moveList(reader, configurationData);
    }
}