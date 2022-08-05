package com.badbones69.crazyenvoys.commands;

import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.CrazyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class EnvoyTab implements TabCompleter {
    
    private final CrazyManager crazyManager = CrazyManager.getInstance();
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) { // /crazyenvoys
            if (Methods.hasPermission(sender, "help")) completions.add("help");
            if (Methods.hasPermission(sender, "reload")) completions.add("reload");
            if (Methods.hasPermission(sender, "time")) completions.add("time");
            if (Methods.hasPermission(sender, "drops")) completions.add("drops");
            if (Methods.hasPermission(sender, "ignore")) completions.add("ignore");
            if (Methods.hasPermission(sender, "flare.give")) completions.add("flare");
            if (Methods.hasPermission(sender, "edit")) completions.add("edit");
            if (Methods.hasPermission(sender, "start")) completions.add("start");
            if (Methods.hasPermission(sender, "stop")) completions.add("stop");
            if (Methods.hasPermission(sender, "center")) completions.add("center");

            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        } else if (args.length == 2) {// /crazyenvoys arg0
            switch (args[0].toLowerCase()) {
                case "drop":
                case "drops":
                    if (Methods.hasPermission(sender, "drops")) {
                        int size = crazyManager.isEnvoyActive() ? crazyManager.getActiveEnvoys().size() : crazyManager.getSpawnLocations().size();

                        if ((size % 10) > 0) {
                            size++;
                        }

                        for (int i = 1; i <= size; i++) completions.add(i + "");
                    }
                    break;
                case "flare":
                    if (Methods.hasPermission(sender, "flare.give")) for (int i = 1; i <= 64; i++) completions.add(i + "");
                    break;
            }

            return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
        } else if (args.length == 3) { // /crazyenvoys arg0 arg1
            if ("flare".equalsIgnoreCase(args[0])) {
                if (Methods.hasPermission(sender, "flare.give")) crazyManager.getPlugin().getServer().getOnlinePlayers().forEach(onlinePlayer -> completions.add(onlinePlayer.getName()));
            }

            return StringUtil.copyPartialMatches(args[2], completions, new ArrayList<>());
        }

        return new ArrayList<>();
    }
}