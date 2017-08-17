package me.badbones69.envoy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.badbones69.envoy.api.Envoy;
import me.badbones69.envoy.api.Flare;
import me.badbones69.envoy.api.Messages;
import me.badbones69.envoy.api.Prizes;
import me.badbones69.envoy.controlers.EditControl;
import me.badbones69.envoy.controlers.EnvoyControl;
import me.badbones69.envoy.controlers.FireworkDamageAPI;
import me.badbones69.envoy.controlers.FlareControl;
import me.badbones69.envoy.multisupport.HolographicSupport;
import me.badbones69.envoy.multisupport.MVdWPlaceholderAPISupport;
import me.badbones69.envoy.multisupport.PlaceholderAPISupport;
import me.badbones69.envoy.multisupport.Support;
import me.badbones69.envoy.multisupport.Version;

public class Main extends JavaPlugin implements Listener{
	
	public static SettingsManager settings = SettingsManager.getInstance();
	
	@Override
	public void onEnable(){
		settings.setup(this);
		Envoy.load();
		Prizes.loadPrizes();
		Methods.hasUpdate();
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new EditControl(), this);
		pm.registerEvents(new EnvoyControl(), this);
		pm.registerEvents(new FlareControl(), this);
		try{
			if(Version.getVersion().getVersionInteger() >= Version.v1_11_R1.getVersionInteger()){
				pm.registerEvents(new FireworkDamageAPI(this), this);
			}
		}catch(Exception e){}
		if(Support.hasHolographicDisplay()){
			HolographicSupport.registerPlaceHolders();
		}
		if(Support.hasPlaceholderAPI()){
			new PlaceholderAPISupport(this).hook();
		}
		if(Support.hasMVdWPlaceholderAPI()){
			MVdWPlaceholderAPISupport.registerPlaceholders(this);
		}
		if(settings.getConfig().getBoolean("Settings.Toggle-Metrics")){
			try {
				new MCUpdate(this, true);
			} catch (IOException e) {}
		}
	}
	
	@Override
	public void onDisable(){
		for(Player player : Bukkit.getOnlinePlayers()){
			if(EditControl.isEditor(player)){
				EditControl.removeEditor(player);
				EditControl.removeFakeBlocks(player);
			}
		}
		if(Support.hasHolographicDisplay()){
			HolographicSupport.unregisterPlaceHolders();
		}
		if(Envoy.isEnvoyActive()){
			Envoy.endEnvoyEvent();
		}
		Envoy.unload();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args){
		if(commandLable.equalsIgnoreCase("envoy")){
			if(args.length <= 0){
				if(!(sender.hasPermission("envoy.time") || sender.hasPermission("envoy.bypass"))){
					Messages.NO_PERMISSION.sendMessage(sender);
					return true;
				}
				Bukkit.dispatchCommand(sender, "envoy time");
				return true;
			}else{
				if(args[0].equalsIgnoreCase("help")){
					if(!(sender.hasPermission("envoy.help") || sender.hasPermission("envoy.bypass"))){
						Messages.NO_PERMISSION.sendMessage(sender);
						return true;
					}
					sender.sendMessage(Methods.color("&6/Envoy help &7- Shows the envoy help menu."));
					sender.sendMessage(Methods.color("&6/Envoy reload &7- Reloads all the config files."));
					sender.sendMessage(Methods.color("&6/Envoy time &7- Shows the time till the envoy starts or ends."));
					sender.sendMessage(Methods.color("&6/Envoy drops [page] &7- Shows all current crate locations."));
					sender.sendMessage(Methods.color("&6/Envoy ignore &7- Shuts up the envoy collecting message."));
					sender.sendMessage(Methods.color("&6/Envoy flare [amount] [player] &7- Give a player a flare to call an envoy event."));
					sender.sendMessage(Methods.color("&6/Envoy edit &7- Edit the crate locations with bedrock."));
					sender.sendMessage(Methods.color("&6/Envoy start &7- Force starts the envoy."));
					sender.sendMessage(Methods.color("&6/Envoy stop &7- Force stops the envoy."));
					sender.sendMessage(Methods.color("&6/Envoy center &7- Set the center of the random crate drops."));
					return true;
				}
				if(args[0].equalsIgnoreCase("reload")){
					if(!(sender.hasPermission("envoy.reload") || sender.hasPermission("envoy.bypass"))){
						Messages.NO_PERMISSION.sendMessage(sender);
						return true;
					}
					try{
						settings.reloadConfig();
						settings.reloadData();
						settings.reloadMessages();
						settings.reloadTiers();
					}catch(Exception e){}
					settings.setup(this);
					if(Envoy.isEnvoyActive()){
						Envoy.endEnvoyEvent();
					}
					Envoy.unload();
					Envoy.load();
					Prizes.loadPrizes();
					Messages.RELOADED.sendMessage(sender);
					return true;
				}
				if(args[0].equalsIgnoreCase("ignore") || args[0].equalsIgnoreCase("stfu")){
					if(!(sender.hasPermission("envoy.ignore") || sender.hasPermission("envoy.bypass"))){
						Messages.NO_PERMISSION.sendMessage(sender);
						return true;
					}
					if(sender instanceof Player){
						Player player = (Player) sender;
						UUID uuid = player.getUniqueId();
						if(Envoy.isIgnoringMessages(uuid)){
							Envoy.removeIgnorePlayer(uuid);
							Messages.STOP_IGNORING_MESSAGES.sendMessage(player);
						}else{
							Envoy.addIgnorePlayer(uuid);
							Messages.START_IGNORING_MESSAGES.sendMessage(player);
						}
					}else{
						Messages.PLAYERS_ONLY.sendMessage(sender);
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("center")){// /Envoy Center
					if(!(sender.hasPermission("envoy.center") || sender.hasPermission("envoy.bypass"))){
						Messages.NO_PERMISSION.sendMessage(sender);
						return true;
					}
					Envoy.setCenter(((Player) sender).getLocation());
					Messages.NEW_CENTER.sendMessage(sender);
					return true;
				}
				if(args[0].equalsIgnoreCase("flare")){// /Envoy Flare [Amount] [Player]
					if(!(sender.hasPermission("envoy.flare.give") || sender.hasPermission("envoy.bypass"))){
						Messages.NO_PERMISSION.sendMessage(sender);
						return true;
					}
					int amount = 1;
					Player player = null;
					if(args.length >= 2){
						if(Methods.isInt(args[1])){
							amount = Integer.parseInt(args[1]);
						}else{
							Messages.NOT_A_NUMBER.sendMessage(sender);
							return true;
						}
					}
					if(args.length>=3){
						if(Methods.isOnline(args[2])){
							player = Methods.getPlayer(args[2]);
						}else{
							Messages.NOT_ONLINE.sendMessage(sender);
							return true;
						}
					}else{
						if(!(sender instanceof Player)){
							Messages.PLAYERS_ONLY.sendMessage(sender);
							return true;
						}else{
							player = (Player) sender;
						}
					}
					HashMap<String, String> placeholder = new HashMap<String, String>();
					placeholder.put("%player%", player.getName());
					placeholder.put("%Player%", player.getName());
					placeholder.put("%amount%", amount + "");
					placeholder.put("%Amount%", amount + "");
					Messages.GIVE_FLARE.sendMessage(sender, placeholder);
					if(!sender.getName().equalsIgnoreCase(player.getName())){
						Messages.GIVEN_FLARE.sendMessage(player, placeholder);
					}
					Flare.giveFlare(player, amount);
					return true;
				}
				if(args[0].equalsIgnoreCase("drops") || args[0].equalsIgnoreCase("drop")){
					if(!(sender.hasPermission("envoy.drops") || sender.hasPermission("envoy.bypass"))){
						Messages.NO_PERMISSION.sendMessage(sender);
						return true;
					}
					ArrayList<String> locs = new ArrayList<String>();
					int page = 1;
					if(args.length >= 2){
						if(Methods.isInt(args[1])){
							page = Integer.parseInt(args[1]);
						}else{
							Messages.NOT_A_NUMBER.sendMessage(sender);
							return true;
						}
					}
					int i = 1;
					HashMap<String, String> ph = new HashMap<String, String>();
					for(Location loc : Envoy.isEnvoyActive() ? Envoy.getActiveEnvoys() : Envoy.getLocations()){
						ph.put("%id%", i + "");
						ph.put("%world%", loc.getWorld().getName());
						ph.put("%x%", loc.getBlockX() + "");
						ph.put("%y%", loc.getBlockY() + "");
						ph.put("%z%", loc.getBlockZ() + "");
						locs.add(Messages.DROPS_FORMAT.getMessage(ph));
						i++;
						ph.clear();
					}
					if(Envoy.isEnvoyActive()){
						Messages.DROPS_AVAILABLE.sendMessage(sender);
					}else{
						Messages.DROPS_POSSIBILITIES.sendMessage(sender);
					}
					for(String dropLocation : Methods.getPage(locs, page)){
						sender.sendMessage(dropLocation);
					}
					if(!Envoy.isEnvoyActive()){
						Messages.DROPS_PAGE.sendMessage(sender);
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("time")){
					if(!(sender.hasPermission("envoy.time") || sender.hasPermission("envoy.bypass"))){
						Messages.NO_PERMISSION.sendMessage(sender);
						return true;
					}
					HashMap<String, String> placeholder = new HashMap<String, String>();
					if(Envoy.isEnvoyActive()){
						placeholder.put("%time%", Envoy.getEnvoyRunTimeLeft());
						placeholder.put("%Time%", Envoy.getEnvoyRunTimeLeft());
						Messages.TIME_LEFT.sendMessage(sender, placeholder);
					}else{
						placeholder.put("%time%", Envoy.getNextEnvoyTime());
						Messages.TIME_TILL_EVENT.sendMessage(sender, placeholder);
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("begin")){
					if(!(sender.hasPermission("envoy.start") || sender.hasPermission("envoy.bypass"))){
						Messages.NO_PERMISSION.sendMessage(sender);
						return true;
					}
					if(Envoy.isEnvoyActive()){
						Messages.ALREADY_STARTED.sendMessage(sender);
					}else{
						Envoy.startEnvoyEvent();
						Messages.FORCE_START.sendMessage(sender);
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("end")){
					if(!(sender.hasPermission("envoy.stop") || sender.hasPermission("envoy.bypass"))){
						Messages.NO_PERMISSION.sendMessage(sender);
						return true;
					}
					if(Envoy.isEnvoyActive()){
						Envoy.endEnvoyEvent();
						Messages.ENDED.broadcastMessage(false, null);
						Messages.FORCE_ENDED.sendMessage(sender);
					}else{
						Messages.NOT_STARTED.sendMessage(sender);
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("edit")){
					if(!(sender.hasPermission("envoy.edit") || sender.hasPermission("envoy.bypass"))){
						Messages.NO_PERMISSION.sendMessage(sender);
						return true;
					}
					if(Envoy.isEnvoyActive()){
						Messages.KICKED_FROM_EDITOR_MODE.sendMessage(sender);
					}else{
						Player player = (Player) sender;
						if(EditControl.isEditor(player)){
							EditControl.removeEditor(player);
							EditControl.removeFakeBlocks(player);
							player.getInventory().remove(Material.BEDROCK);
							Messages.LEAVE_EDITOR_MODE.sendMessage(player);
						}else{
							EditControl.addEditor(player);
							EditControl.showFakeBlocks(player);
							player.getInventory().addItem(new ItemStack(Material.BEDROCK, 1));
							Messages.ENTER_EDITOR_MODE.sendMessage(player);
						}
					}
					return true;
				}
				sender.sendMessage(Methods.getPrefix() + Methods.color("&cPlease do /envoy help for more information."));
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		final Player player = e.getPlayer();
		new BukkitRunnable(){
			@Override
			public void run() {
				if(player.getName().equals("BadBones69")){
					player.sendMessage(Methods.getPrefix() + Methods.color("&7This server is running your Crazy Envoy Plugin. "
						+ "&7It is running version &av" + Bukkit.getServer().getPluginManager().getPlugin("CrazyEnvoy").getDescription().getVersion() + "&7."));
				}
				if(player.isOp()){
					Methods.hasUpdate(player);
				}
			}
		}.runTaskLaterAsynchronously(this, 20);
	}
	
}