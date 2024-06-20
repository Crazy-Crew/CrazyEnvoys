package com.badbones69.crazyenvoys.util;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.crazycrew.crazyenvoys.core.config.ConfigManager;
import us.crazycrew.crazyenvoys.core.config.types.ConfigKeys;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Matcher.quoteReplacement;

@SuppressWarnings("ALL")
public class MsgUtils {

    private static final CrazyEnvoys plugin = CrazyEnvoys.get();

    public static String color(String message) {
        Matcher matcher = Pattern.compile("#[a-fA-F\\d]{6}").matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static void sendMessage(CommandSender commandSender, String message, boolean prefixToggle) {
        if (message == null || message.isEmpty()) return;

        String prefix = getPrefix();

        if (commandSender instanceof Player player) {
            if (!prefix.isEmpty() && prefixToggle) player.sendMessage(color(message.replaceAll("%prefix%", quoteReplacement(prefix))).replaceAll("%Prefix%", quoteReplacement(prefix))); else player.sendMessage(color(message));

            return;
        }

        if (!prefix.isEmpty() && prefixToggle) commandSender.sendMessage(color(message.replaceAll("%prefix%", quoteReplacement(prefix))).replaceAll("%Prefix%", quoteReplacement(prefix))); else commandSender.sendMessage(color(message));
    }

    public static String getPrefix() {
        return color(ConfigManager.getConfig().getProperty(ConfigKeys.command_prefix));
    }
}