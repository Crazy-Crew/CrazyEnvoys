package me.badbones69.crazyenvoy.commands;

import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class EnvoyTab implements TabCompleter {
    
    private CrazyEnvoy envoy = CrazyEnvoy.getInstance();
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String commandLable, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {// /envoy
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
                case "drop":
                case "drops":
                    int size = envoy.isEnvoyActive() ? envoy.getActiveEnvoys().size() : envoy.getSpawnLocations().size();
                    if ((size % 10) > 0) {
                        size++;
                    }
                    for (int i = 1; i <= size; i++) completions.add(i + "");
                    break;
                case "flare":
                    for (int i = 1; i <= 64; i++) completions.add(i + "");
                    break;
            }
            return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
        } else if (args.length == 3) {// /envoy arg0 arg1
            if ("flare".equalsIgnoreCase(args[0])) {
                Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
            }
            return StringUtil.copyPartialMatches(args[2], completions, new ArrayList<>());
        }
        return new ArrayList<>();
    }
    
    private boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission("envoy." + node) || sender.hasPermission("envoy.bypass");
    }
    
}