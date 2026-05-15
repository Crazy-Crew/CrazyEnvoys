package com.badbones69.crazyenvoys.commands;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.commands.relations.ArgumentRelations;
import com.badbones69.crazyenvoys.commands.types.admin.migrator.MigrateCommand;
import com.badbones69.crazyenvoys.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenvoys.commands.types.admin.CenterCommand;
import com.badbones69.crazyenvoys.commands.types.admin.ClearCommand;
import com.badbones69.crazyenvoys.commands.types.admin.EditCommand;
import com.badbones69.crazyenvoys.commands.types.admin.FlareCommand;
import com.badbones69.crazyenvoys.commands.types.admin.StartCommand;
import com.badbones69.crazyenvoys.commands.types.admin.StopCommand;
import com.badbones69.crazyenvoys.commands.types.player.DropCommand;
import com.badbones69.crazyenvoys.commands.types.player.HelpCommand;
import com.badbones69.crazyenvoys.commands.types.admin.ReloadCommand;
import com.badbones69.crazyenvoys.commands.types.player.IgnoreCommand;
import com.ryderbelserion.fusion.paper.builders.items.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private static final CrazyEnvoys plugin = CrazyEnvoys.get();

    private static final LocationSettings locationSettings = plugin.getLocationSettings();

    private static final CrazyManager crazyManager = plugin.getCrazyManager();

    private static final Server server = plugin.getServer();

    private static final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

    /**
     * Loads commands.
     */
    public static void load() {
        new ArgumentRelations().build();

        commandManager.registerSuggestion(SuggestionKey.of("players"), (_) -> server.getOnlinePlayers().stream().map(Player::getName).toList());

        commandManager.registerSuggestion(SuggestionKey.of("numbers"), (_) -> {
            final List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 100; i++) numbers.add(String.valueOf(i));

            return numbers;
        });

        commandManager.registerSuggestion(SuggestionKey.of("doubles"), (_) -> {
            final List<String> numbers = new ArrayList<>();

            int count = 0;

            while (count <= 1000) {
                double x = count / 10.0;

                numbers.add(String.valueOf(x));

                count++;
            }

            return numbers;
        });

        commandManager.registerSuggestion(SuggestionKey.of("migrators"), (_) -> {
            final List<String> migrators = new ArrayList<>();

            for (MigrationType value : MigrationType.values()) {
                final String name = value.getName();

                migrators.add(name);
            }

            return migrators;
        });

        commandManager.registerSuggestion(SuggestionKey.of("drops"), (_) -> {
            final List<String> drops = new ArrayList<>();

            int size = crazyManager.isEnvoyActive() ? crazyManager.getActiveEnvoys().size() : locationSettings.getSpawnLocations().size();

            if ((size % 10) > 0) size++;

            for (int i = 1; i <= size; i++) drops.add(i + "");

            return drops;
        });

        commandManager.registerArgument(PlayerBuilder.class, (_, context) -> new PlayerBuilder(context));

        List.of(
                new DropCommand(),
                new HelpCommand(),
                new IgnoreCommand(),

                new MigrateCommand(),

                new CenterCommand(),
                new ClearCommand(),
                new EditCommand(),
                new FlareCommand(),
                new ReloadCommand(),
                new StartCommand(),
                new StopCommand()
        ).forEach(commandManager::registerCommand);
    }

    public static @NotNull BukkitCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }
}