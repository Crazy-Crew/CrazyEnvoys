package com.badbones69.crazyenvoys.commands;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class EnvoyTab implements TabCompleter {

    private final @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final @NotNull CrazyManager crazyManager = this.plugin.getCrazyManager();

    private final @NotNull LocationSettings locationSettings = this.plugin.getLocationSettings();

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) { // /envoy
            if (hasPermission(sender, "help")) completions.add("help");
            if (hasPermission(sender, "reload")) completions.add("reload");
            if (hasPermission(sender, "time")) completions.add("time");
            if (hasPermission(sender, "drops")) completions.add("drops");
            if (hasPermission(sender, "ignore")) completions.add("ignore");
            if (hasPermission(sender, "flare.give")) completions.add("flare");
            if (hasPermission(sender, "edit")) completions.add("edit");
            if (hasPermission(sender, "start")) completions.add("start");
            if (hasPermission(sender, "stop")) completions.add("stop");
            if (hasPermission(sender, "center")) completions.add("center");

            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        } else if (args.length == 2) {// /envoy arg0
            switch (args[0].toLowerCase()) {
                case "drop", "drops" -> {
                    if (hasPermission(sender, "drops")) {
                        int size = this.crazyManager.isEnvoyActive() ? this.crazyManager.getActiveEnvoys().size() : this.locationSettings.getSpawnLocations().size();

                        if ((size % 10) > 0) size++;

                        for (int i = 1; i <= size; i++) completions.add(i + "");
                    }
                }
                case "flare" -> {
                    if (hasPermission(sender, "flare.give")) for (int i = 1; i <= 64; i++) completions.add(i + "");
                }
            }

            return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
        } else if (args.length == 3) { // /envoy arg0 arg1
            if ("flare".equalsIgnoreCase(args[0])) {
                if (hasPermission(sender, "flare.give")) this.plugin.getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));
            }

            return StringUtil.copyPartialMatches(args[2], completions, new ArrayList<>());
        }

        return new ArrayList<>();
    }

    private boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission("envoy." + node) || sender.hasPermission("envoy.bypass");
    }
}