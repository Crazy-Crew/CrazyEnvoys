package me.BadBones69.envoy;

import java.io.IOException;
import java.util.ArrayList;

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

import me.BadBones69.envoy.MultiSupport.HolographicSupport;
import me.BadBones69.envoy.MultiSupport.MVdWPlaceholderAPISupport;
import me.BadBones69.envoy.MultiSupport.PlaceholderAPISupport;
import me.BadBones69.envoy.MultiSupport.Support;
import me.BadBones69.envoy.api.Envoy;
import me.BadBones69.envoy.api.Flare;
import me.BadBones69.envoy.api.Prizes;
import me.BadBones69.envoy.controlers.EditControl;
import me.BadBones69.envoy.controlers.EnvoyControl;
import me.BadBones69.envoy.controlers.FireworkDamageAPI;
import me.BadBones69.envoy.controlers.FlareControl;

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
			try{
				Metrics metrics = new Metrics(this); metrics.start();
			}catch (IOException e) {}
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
					sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.No-Permission")));
					return true;
				}
				Bukkit.dispatchCommand(sender, "envoy time");
				return true;
			}else{
				if(args[0].equalsIgnoreCase("help")){
					if(!(sender.hasPermission("envoy.help") || sender.hasPermission("envoy.bypass"))){
						sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.No-Permission")));
						return true;
					}
					sender.sendMessage(Methods.color("&6/Envoy help &7- Shows the envoy help menu."));
					sender.sendMessage(Methods.color("&6/Envoy reload &7- Reloads all the config files."));
					sender.sendMessage(Methods.color("&6/Envoy time &7- Shows the time till the envoy starts or ends."));
					sender.sendMessage(Methods.color("&6/Envoy drops &7- Shows all current crate locations."));
					sender.sendMessage(Methods.color("&6/Envoy flare [amount] [player] &7- Give a player a flare to call an envoy event."));
					sender.sendMessage(Methods.color("&6/Envoy edit &7- Edit the crate locations with bedrock."));
					sender.sendMessage(Methods.color("&6/Envoy start &7- Force starts the envoy."));
					sender.sendMessage(Methods.color("&6/Envoy stop &7- Force stops the envoy."));
					sender.sendMessage(Methods.color("&6/Envoy center &7- Set the center of the random crate drops."));
					return true;
				}
				if(args[0].equalsIgnoreCase("reload")){
					if(!(sender.hasPermission("envoy.reload") || sender.hasPermission("envoy.bypass"))){
						sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.No-Permission")));
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
					sender.sendMessage(Methods.getPrefix() + Methods.color(settings.getMessages().getString("Messages.Reloaded")));
					return true;
				}
				if(args[0].equalsIgnoreCase("center")){// /Envoy Center
					if(!(sender.hasPermission("envoy.center") || sender.hasPermission("envoy.bypass"))){
						sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.No-Permission")));
						return true;
					}
					Envoy.setCenter(((Player) sender).getLocation());
					sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.New-Center")));
					return true;
				}
				if(args[0].equalsIgnoreCase("flare")){// /Envoy Flare [Amount] [Player]
					if(!(sender.hasPermission("envoy.flare.give") || sender.hasPermission("envoy.bypass"))){
						sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.No-Permission")));
						return true;
					}
					int amount = 1;
					Player player = null;
					if(args.length >= 2){
						if(Methods.isInt(args[1])){
							amount = Integer.parseInt(args[1]);
						}else{
							sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Not-A-Number")));
							return true;
						}
					}
					if(args.length>=3){
						if(Methods.isOnline(args[2])){
							player = Methods.getPlayer(args[2]);
						}else{
							sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Not-Online")));
							return true;
						}
					}else{
						if(!(sender instanceof Player)){
							sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Players-Only")));
							return true;
						}else{
							player = (Player) sender;
						}
					}
					sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Give-Flare")
							.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())
							.replaceAll("%Amount%", amount + "").replaceAll("%amount%", amount + "")));
					if(!sender.getName().equalsIgnoreCase(player.getName())){
						player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Given-Flare")
								.replaceAll("%Amount%", amount + "").replaceAll("%amount%", amount + "")));
					}
					Flare.giveFlare(player, amount);
					return true;
				}
				if(args[0].equalsIgnoreCase("drops") || args[0].equalsIgnoreCase("drop")){
					if(!(sender.hasPermission("envoy.drops") || sender.hasPermission("envoy.bypass"))){
						sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.No-Permission")));
						return true;
					}
					ArrayList<String> locs = new ArrayList<String>();
					int page = 1;
					if(args.length >= 2){
						if(Methods.isInt(args[1])){
							page = Integer.parseInt(args[1]);
						}else{
							sender.sendMessage(Methods.getPrefix() + Methods.color(settings.getMessages().getString("Messages.Not-A-Number")));
							return true;
						}
					}
					if(Envoy.isEnvoyActive()){
						int i = 1;
						for(Location loc : Envoy.getActiveEvoys()){
							locs.add("&7[&6" + i + "&7]: " + loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
							i++;
						}
					}else{
						int i = 1;
						for(Location loc : Envoy.getLocations()){
							locs.add("&7[&6" + i + "&7]: " + loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
							i++;
						}
					}
					if(Envoy.isEnvoyActive()){
						sender.sendMessage(Methods.getPrefix() + Methods.color("&7List of all available envoys."));
					}else{
						sender.sendMessage(Methods.getPrefix() + Methods.color("&7List of location envoy's may spawn at."));
					}
					for(String loc : Methods.getPage(locs, page)){
						sender.sendMessage(Methods.color("&6" + loc));
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("time")){
					if(!(sender.hasPermission("envoy.time") || sender.hasPermission("envoy.bypass"))){
						sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.No-Permission")));
						return true;
					}
					if(Envoy.isEnvoyActive()){
						sender.sendMessage(Methods.getPrefix() + Methods.color(settings.getMessages().getString("Messages.Time-Left")
								.replaceAll("%Time%", Envoy.getEnvoyRunTimeLeft()).replaceAll("%time%", Envoy.getEnvoyRunTimeLeft())));
					}else{
						sender.sendMessage(Methods.getPrefix() + Methods.color(settings.getMessages().getString("Messages.Time-Till-Event")
								.replaceAll("%Time%", Envoy.getNextEnvoyTime()).replaceAll("%time%", Envoy.getNextEnvoyTime())));
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("begin")){
					if(!(sender.hasPermission("envoy.start") || sender.hasPermission("envoy.bypass"))){
						sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.No-Permission")));
						return true;
					}
					if(Envoy.isEnvoyActive()){
						sender.sendMessage(Methods.getPrefix() + Methods.color(settings.getMessages().getString("Messages.Already-Started")));
					}else{
						Envoy.startEnvoyEvent();
						sender.sendMessage(Methods.getPrefix() + Methods.color(settings.getMessages().getString("Messages.Force-Start")));
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("end")){
					if(!(sender.hasPermission("envoy.stop") || sender.hasPermission("envoy.bypass"))){
						sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.No-Permission")));
						return true;
					}
					if(Envoy.isEnvoyActive()){
						Envoy.endEnvoyEvent();
						Methods.broadcastMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Ended")));
						sender.sendMessage(Methods.getPrefix() + Methods.color(settings.getMessages().getString("Messages.Force-Ended")));
					}else{
						sender.sendMessage(Methods.getPrefix() + Methods.color(settings.getMessages().getString("Messages.Not-Started")));
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("edit")){
					if(!(sender.hasPermission("envoy.edit") || sender.hasPermission("envoy.bypass"))){
						sender.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.No-Permission")));
						return true;
					}
					if(Envoy.isEnvoyActive()){
						sender.sendMessage(Methods.getPrefix() + Methods.color(settings.getMessages().getString("Messages.Kicked-From-Editor-Mode")));
					}else{
						Player player = (Player) sender;
						if(EditControl.isEditor(player)){
							EditControl.removeEditor(player);
							EditControl.removeFakeBlocks(player);
							player.getInventory().remove(Material.BEDROCK);
							player.sendMessage(Methods.getPrefix() + Methods.color(settings.getMessages().getString("Messages.Leave-Editor-Mode")));
						}else{
							EditControl.addEditor(player);
							EditControl.showFakeBlocks(player);
							player.getInventory().addItem(new ItemStack(Material.BEDROCK, 1));
							player.sendMessage(Methods.getPrefix() + Methods.color(settings.getMessages().getString("Messages.Enter-Editor-Mode")));
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