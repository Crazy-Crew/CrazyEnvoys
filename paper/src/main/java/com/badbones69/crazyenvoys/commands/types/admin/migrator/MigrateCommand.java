package com.badbones69.crazyenvoys.commands.types.admin.migrator;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.types.EnvoyCommand;
import com.badbones69.crazyenvoys.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenvoys.commands.types.admin.migrator.types.LegacyColorMigrator;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import java.util.Map;

public class MigrateCommand extends EnvoyCommand {

    @Command("migrate")
    @Permission(value = "crazycrates.migrate", def = PermissionDefault.OP)
    @Flag(flag = "mt", longFlag = "migration_type", argument = String.class, suggestion = "migrators")
    @Flag(flag = "envoy", longFlag = "envoy", argument = String.class, suggestion = "envoys")
    @Flag(flag = "d", longFlag = "data")
    @Syntax("/crazycrates migrate -mt <migration_type> [-c/--crate] <crate_name> [-d/--data]")
    public void migrate(final CommandSender sender, Flags flags) {
        final boolean hasFlag = flags.hasFlag("mt");

        if (!hasFlag) {
            Messages.lacking_flag.sendMessage(sender, Map.of(
                    "{flag}", "-mt",
                    "{usage}", "/crazycrates migrate -mt <migration_type>"
            ));

            return;
        }

        final MigrationType type = MigrationType.fromName(flags.getFlagValue("mt").orElse(null));

        if (type == null) {
            Messages.migration_not_available.sendMessage(sender);

            return;
        }

        switch (type) {
            case LEGACY_COLOR_ALL -> new LegacyColorMigrator(sender).run();
        }
    }
}