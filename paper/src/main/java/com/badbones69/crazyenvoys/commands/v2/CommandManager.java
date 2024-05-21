package com.badbones69.crazyenvoys.commands.v2;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyHandler;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.commands.v2.envoys.types.admin.CommandDebug;
import com.badbones69.crazyenvoys.commands.v2.envoys.types.admin.CommandFlare;
import com.badbones69.crazyenvoys.commands.v2.envoys.types.admin.CommandReload;
import com.badbones69.crazyenvoys.commands.v2.envoys.types.admin.editor.CommandClear;
import com.badbones69.crazyenvoys.commands.v2.envoys.types.admin.editor.CommandEditor;
import com.badbones69.crazyenvoys.commands.v2.envoys.types.admin.envoys.CommandCenter;
import com.badbones69.crazyenvoys.commands.v2.envoys.types.admin.envoys.CommandStart;
import com.badbones69.crazyenvoys.commands.v2.envoys.types.admin.envoys.CommandStop;
import com.badbones69.crazyenvoys.commands.v2.envoys.types.player.CommandDrops;
import com.badbones69.crazyenvoys.commands.v2.envoys.types.player.CommandHelp;
import com.badbones69.crazyenvoys.commands.v2.envoys.types.player.CommandIgnore;
import com.badbones69.crazyenvoys.commands.v2.envoys.types.player.CommandTIme;
import com.badbones69.crazyenvoys.commands.v2.relations.ArgumentRelations;
import com.ryderbelserion.vital.paper.builders.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private final static @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);
    private final static @NotNull LocationSettings locationSettings = plugin.getLocationSettings();
    private final static @NotNull CrazyManager crazyManager = plugin.getCrazyManager();
    private final static @NotNull CrazyHandler crazyHandler = plugin.getCrazyHandler();

    private final static @NotNull BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

    /**
     * Loads commands.
     */
    public static void load() {
        new ArgumentRelations().build();

        commandManager.registerSuggestion(SuggestionKey.of("players"), (sender, context) -> plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList());

        commandManager.registerSuggestion(SuggestionKey.of("rewards"), (sender, context) -> crazyHandler.getRewardFiles());

        commandManager.registerSuggestion(SuggestionKey.of("numbers"), (sender, context) -> {
            final List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 64; i++) numbers.add(String.valueOf(i));

            return numbers;
        });

        commandManager.registerSuggestion(SuggestionKey.of("drops"), (sender, context) -> {
            final List<String> numbers = new ArrayList<>();

            int size = crazyManager.isEnvoyActive() ? crazyManager.getActiveEnvoys().size() : locationSettings.getSpawnLocations().size();

            if ((size % 10) > 0) size++;

            for (int i = 1; i <= size; i++) numbers.add(String.valueOf(i));

            return numbers;
        });

        commandManager.registerArgument(PlayerBuilder.class, (sender, context) -> new PlayerBuilder(context));

        List.of(
                new CommandDebug(),
                new CommandReload(),
                new CommandClear(),
                new CommandEditor(),
                new CommandCenter(),
                new CommandStart(),
                new CommandStop(),
                new CommandFlare(),
                new CommandDrops(),
                new CommandHelp(),
                new CommandIgnore(),
                new CommandTIme()
        ).forEach(commandManager::registerCommand);
    }

    public static @NotNull BukkitCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }
}