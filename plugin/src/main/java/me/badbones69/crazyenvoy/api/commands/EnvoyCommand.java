package me.badbones69.crazyenvoy.api.commands;

import me.badbones69.crazyenvoy.Methods;
import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.FileManager;
import me.badbones69.crazyenvoy.api.enums.Messages;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent.EnvoyEndReason;
import me.badbones69.crazyenvoy.api.events.EnvoyStartEvent;
import me.badbones69.crazyenvoy.api.events.EnvoyStartEvent.EnvoyStartReason;
import me.badbones69.crazyenvoy.api.objects.Flare;
import me.badbones69.crazyenvoy.controllers.EditControl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EnvoyCommand implements CommandExecutor {
	
	private CrazyEnvoy envoy = CrazyEnvoy.getInstance();
	private FileManager fileManager = FileManager.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
		if(args.length <= 0) {
			if(!hasPermission(sender, "time")) {
				Messages.NO_PERMISSION.sendMessage(sender);
				return true;
			}
			Bukkit.dispatchCommand(sender, "envoy time");
			return true;
		}else {
			switch(args[0].toLowerCase()) {
				case "help":
					if(hasPermission(sender, "help")) {
						Messages.HELP.sendMessage(sender);
					}else {
						Messages.NO_PERMISSION.sendMessage(sender);
					}
					return true;
				case "reload":
					if(hasPermission(sender, "reload")) {
						if(envoy.isEnvoyActive()) {
							EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.RELOAD);
							Bukkit.getPluginManager().callEvent(event);
							envoy.endEnvoyEvent();
						}
						envoy.unload();
						try {
							fileManager.setup(envoy.getPlugin());
						}catch(Exception e) {
						}
						envoy.load();
						Messages.RELOADED.sendMessage(sender);
					}else {
						Messages.NO_PERMISSION.sendMessage(sender);
					}
					return true;
				case "ignore":
				case "stfu":
					if(hasPermission(sender, "ignore")) {
						if(sender instanceof Player) {
							Player player = (Player) sender;
							UUID uuid = player.getUniqueId();
							if(envoy.isIgnoringMessages(uuid)) {
								envoy.removeIgnorePlayer(uuid);
								Messages.STOP_IGNORING_MESSAGES.sendMessage(player);
							}else {
								envoy.addIgnorePlayer(uuid);
								Messages.START_IGNORING_MESSAGES.sendMessage(player);
							}
						}else {
							Messages.PLAYERS_ONLY.sendMessage(sender);
						}
					}else {
						Messages.NO_PERMISSION.sendMessage(sender);
					}
					return true;
				case "center":
					if(hasPermission(sender, "center")) {
						envoy.setCenter(((Player) sender).getLocation());
						Messages.NEW_CENTER.sendMessage(sender);
					}else {
						Messages.NO_PERMISSION.sendMessage(sender);
					}
					return true;
				case "flare":// /Envoy Flare [Amount] [Player]
					if(hasPermission(sender, "flare.give")) {
						int amount = 1;
						Player player;
						if(args.length >= 2) {
							if(Methods.isInt(args[1])) {
								amount = Integer.parseInt(args[1]);
							}else {
								Messages.NOT_A_NUMBER.sendMessage(sender);
								return true;
							}
						}
						if(args.length >= 3) {
							if(Methods.isOnline(args[2])) {
								player = Methods.getPlayer(args[2]);
							}else {
								Messages.NOT_ONLINE.sendMessage(sender);
								return true;
							}
						}else {
							if(!(sender instanceof Player)) {
								Messages.PLAYERS_ONLY.sendMessage(sender);
								return true;
							}else {
								player = (Player) sender;
							}
						}
						HashMap<String, String> placeholder = new HashMap<>();
						placeholder.put("%player%", player.getName());
						placeholder.put("%Player%", player.getName());
						placeholder.put("%amount%", amount + "");
						placeholder.put("%Amount%", amount + "");
						Messages.GIVE_FLARE.sendMessage(sender, placeholder);
						if(!sender.getName().equalsIgnoreCase(player.getName())) {
							Messages.GIVEN_FLARE.sendMessage(player, placeholder);
						}
						Flare.giveFlare(player, amount);
					}else {
						Messages.NO_PERMISSION.sendMessage(sender);
					}
					return true;
				case "drops":
				case "drop":
					if(hasPermission(sender, "drops")) {
						ArrayList<String> locs = new ArrayList<>();
						int page = 1;
						if(args.length >= 2) {
							if(Methods.isInt(args[1])) {
								page = Integer.parseInt(args[1]);
							}else {
								Messages.NOT_A_NUMBER.sendMessage(sender);
								return true;
							}
						}
						int i = 1;
						HashMap<String, String> ph = new HashMap<>();
						for(Block block : envoy.isEnvoyActive() ? envoy.getActiveEnvoys() : envoy.getSpawnLocations()) {
							ph.put("%id%", i + "");
							ph.put("%world%", block.getWorld().getName());
							ph.put("%x%", block.getX() + "");
							ph.put("%y%", block.getY() + "");
							ph.put("%z%", block.getZ() + "");
							locs.add(Messages.DROPS_FORMAT.getMessage(ph));
							i++;
							ph.clear();
						}
						if(envoy.isEnvoyActive()) {
							Messages.DROPS_AVAILABLE.sendMessage(sender);
						}else {
							Messages.DROPS_POSSIBILITIES.sendMessage(sender);
						}
						for(String dropLocation : Methods.getPage(locs, page)) {
							sender.sendMessage(dropLocation);
						}
						if(!envoy.isEnvoyActive()) {
							Messages.DROPS_PAGE.sendMessage(sender);
						}
					}else {
						Messages.NO_PERMISSION.sendMessage(sender);
					}
					return true;
				case "time":
					if(hasPermission(sender, "time")) {
						HashMap<String, String> placeholder = new HashMap<>();
						if(envoy.isEnvoyActive()) {
							placeholder.put("%time%", envoy.getEnvoyRunTimeLeft());
							placeholder.put("%Time%", envoy.getEnvoyRunTimeLeft());
							Messages.TIME_LEFT.sendMessage(sender, placeholder);
						}else {
							placeholder.put("%time%", envoy.getNextEnvoyTime());
							Messages.TIME_TILL_EVENT.sendMessage(sender, placeholder);
						}
					}else {
						Messages.NO_PERMISSION.sendMessage(sender);
					}
					return true;
				case "start":
				case "begin":
					if(hasPermission(sender, "start")) {
						if(envoy.isEnvoyActive()) {
							Messages.ALREADY_STARTED.sendMessage(sender);
						}else {
							EnvoyStartEvent event;
							if(sender instanceof Player) {
								event = new EnvoyStartEvent(EnvoyStartReason.FORCED_START_PLAYER, (Player) sender);
							}else {
								event = new EnvoyStartEvent(EnvoyStartReason.FORCED_START_CONSOLE);
							}
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()) {
								if(envoy.startEnvoyEvent()) {
									Messages.FORCE_START.sendMessage(sender);
								}
							}
						}
					}else {
						Messages.NO_PERMISSION.sendMessage(sender);
					}
					return true;
				case "stop":
				case "end":
					if(hasPermission(sender, "stop")) {
						if(envoy.isEnvoyActive()) {
							EnvoyEndEvent event;
							if(sender instanceof Player) {
								event = new EnvoyEndEvent(EnvoyEndReason.FORCED_END_PLAYER, (Player) sender);
							}else {
								event = new EnvoyEndEvent(EnvoyEndReason.FORCED_END_CONSOLE);
							}
							Bukkit.getPluginManager().callEvent(event);
							envoy.endEnvoyEvent();
							Messages.ENDED.broadcastMessage(false);
							Messages.FORCE_ENDED.sendMessage(sender);
						}else {
							Messages.NOT_STARTED.sendMessage(sender);
						}
					}else {
						Messages.NO_PERMISSION.sendMessage(sender);
					}
					return true;
				case "edit":
					if(hasPermission(sender, "edit")) {
						if(envoy.isEnvoyActive()) {
							Messages.KICKED_FROM_EDITOR_MODE.sendMessage(sender);
						}else {
							Player player = (Player) sender;
							if(EditControl.isEditor(player)) {
								EditControl.removeEditor(player);
								EditControl.removeFakeBlocks(player);
								player.getInventory().remove(Material.BEDROCK);
								Messages.LEAVE_EDITOR_MODE.sendMessage(player);
							}else {
								EditControl.addEditor(player);
								EditControl.showFakeBlocks(player);
								player.getInventory().addItem(new ItemStack(Material.BEDROCK, 1));
								Messages.ENTER_EDITOR_MODE.sendMessage(player);
							}
						}
					}else {
						Messages.NO_PERMISSION.sendMessage(sender);
					}
					return true;
			}
			sender.sendMessage(Methods.getPrefix() + Methods.color("&cPlease do /envoy help for more information."));
			return true;
		}
	}
	
	private boolean hasPermission(CommandSender sender, String node) {
		if(sender instanceof Player) {
			return sender.hasPermission("envoy." + node) || sender.hasPermission("envoy.bypass");
		}
		return true;
	}
	
}