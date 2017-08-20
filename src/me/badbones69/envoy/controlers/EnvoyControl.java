package me.badbones69.envoy.controlers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.badbones69.envoy.Main;
import me.badbones69.envoy.Methods;
import me.badbones69.envoy.api.Envoy;
import me.badbones69.envoy.api.Messages;
import me.badbones69.envoy.api.Prizes;
import me.badbones69.envoy.multisupport.HolographicSupport;
import me.badbones69.envoy.multisupport.Support;
import me.badbones69.envoy.multisupport.Version;

public class EnvoyControl implements Listener{
	
	private static HashMap<UUID, Calendar> cooldown = new HashMap<UUID, Calendar>();
	
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(Envoy.isEnvoyActive()){
			if(e.getClickedBlock() != null){
				Location loc = e.getClickedBlock().getLocation();
				if(Envoy.isActiveEnvoy(loc)){
					if(Version.getVersion().getVersionInteger() > Version.v1_7_R4.getVersionInteger()){
						if(player.getGameMode() == GameMode.valueOf("SPECTATOR")){
							return;
						}
					}
					e.setCancelled(true);
					if(!player.hasPermission("envoy.bypass")){
						if(Main.settings.getConfig().contains("Settings.Crate-Collect-Cooldown")){
							if(Main.settings.getConfig().getBoolean("Settings.Crate-Collect-Cooldown.Toggle")){
								UUID uuid = player.getUniqueId();
								if(cooldown.containsKey(uuid)){
									if(Calendar.getInstance().before(cooldown.get(uuid))){
										HashMap<String, String> placeholder = new HashMap<String, String>();
										placeholder.put("%time%", getTimeLeft(cooldown.get(uuid)));
										placeholder.put("%Time%", getTimeLeft(cooldown.get(uuid)));
										Messages.COOLDOWN_LEFT.sendMessage(player, placeholder);
										return;
									}
								}
								cooldown.put(uuid, getTimeFromString(Main.settings.getConfig().getString("Settings.Crate-Collect-Cooldown.Time")));
							}
						}
					}
					String tier = Envoy.getTier(loc);
					if(Main.settings.getFile(tier).getBoolean("Settings.Firework-Toggle")){
						ArrayList<Color> colors = new ArrayList<Color>();
						for(String c : Main.settings.getFile(tier).getStringList("Settings.Firework-Colors")){
							Color color = Methods.getColor(c);
							if(color != null){
								colors.add(color);
							}
						}
						Methods.fireWork(loc.clone().add(.5, 0, .5), colors);
					}
					e.getClickedBlock().setType(Material.AIR);
					if(Support.hasHolographicDisplay()){
						double hight = 1.5;
						if(Main.settings.getFile(tier).contains("Settings.Hologram-Hight")){
							hight = Main.settings.getFile(tier).getDouble("Settings.Hologram-Hight");
						}
						HolographicSupport.removeHologram(loc.clone().add(.5, hight, .5));
					}
					Envoy.stopSignalFlare(e.getClickedBlock().getLocation());
					Envoy.removeActiveEnvoy(loc);
					ArrayList<String> prizes = new ArrayList<String>();
					if(Prizes.getPrizes(tier).size() == 0){
						Bukkit.broadcastMessage(Methods.getPrefix() + Methods.color("&cNo prizes were found in the " + tier + " tier."
								+ " Please add prizes other wise errors will occur."));
						return;
					}
					if(Prizes.useChance(tier)){
						prizes = Prizes.pickPrizesByChance(tier);
					}else{
						prizes = Prizes.pickRandomPrizes(tier);
					}
					for(String prize : prizes){
						for(String msg : Prizes.getMessages(tier, prize)){
							player.sendMessage(Methods.color(msg));
						}
						for(String cmd : Prizes.getCommands(tier, prize)){
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName()));
						}
						for(ItemStack item : Prizes.getItems(tier, prize)){
							if(Methods.isInvFull(player)){
								e.getClickedBlock().getWorld().dropItem(loc, item);
							}else{
								player.getInventory().addItem(item);
							}
						}
						player.updateInventory();
					}
					if(Envoy.getActiveEnvoys().size() >= 1){
						if(Main.settings.getConfig().contains("Settings.Broadcast-Crate-Pick-Up")){
							if(Main.settings.getConfig().getBoolean("Settings.Broadcast-Crate-Pick-Up")){
								HashMap<String, String> placeholder = new HashMap<String, String>();
								placeholder.put("%player%", player.getName());
								placeholder.put("%Player%", player.getName());
								placeholder.put("%amount%", Envoy.getActiveEnvoys().size() + "");
								placeholder.put("%Amount%", Envoy.getActiveEnvoys().size() + "");
								Messages.LEFT.broadcastMessage(true, placeholder);
							}
						}else{
							HashMap<String, String> placeholder = new HashMap<String, String>();
							placeholder.put("%player%", player.getName());
							placeholder.put("%Player%", player.getName());
							placeholder.put("%amount%", Envoy.getActiveEnvoys().size() + "");
							placeholder.put("%Amount%", Envoy.getActiveEnvoys().size() + "");
							Messages.LEFT.broadcastMessage(true, placeholder);
						}
					}else{
						Envoy.endEnvoyEvent();
						Messages.ENDED.broadcastMessage(false, null);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onChestSpawn(EntityChangeBlockEvent e){
		if(Envoy.isEnvoyActive()){
			if(e.getEntity() instanceof FallingBlock){
				if(!Envoy.getFallingBlocks().isEmpty()){
					if(Envoy.getFallingBlocks().contains(e.getEntity())){
						Location loc = e.getBlock().getLocation();
						e.setCancelled(true);
						String tier = Prizes.pickTierByChance();
						if(loc.getBlock().getType() != Material.AIR){
							loc.add(0, 1, 0);
						}
						loc.getBlock().setType(Methods.makeItem(Main.settings.getFile(tier).getString("Settings.Placed-Block"), 1, "").getType());
						if(Support.hasHolographicDisplay()){
							if(Main.settings.getFile(tier).getBoolean("Settings.Hologram-Toggle")){
								HolographicSupport.createHologram(loc.getBlock().getLocation(), tier);
							}
						}
						Envoy.removeFallingBlock(e.getEntity());
						Envoy.addActiveEnvoy(loc.getBlock().getLocation(), tier);
						if(Main.settings.getFile(tier).getBoolean("Settings.Signal-Flare.Toggle")){
							Envoy.startSignalFlare(loc.getBlock().getLocation(), tier);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e){
		if(Envoy.isEnvoyActive()){
			for(Entity en : e.getEntity().getNearbyEntities(0, 0, 0)){
				if(!Envoy.getFallingBlocks().isEmpty()){
					if(Envoy.getFallingBlocks().contains(en)){
						e.setCancelled(true);
						String tier = Prizes.pickTierByChance();
						Location loc = en.getLocation();
						if(loc.getBlock().getType() != Material.AIR){
							loc.add(0, 1, 0);
						}
						loc.getBlock().setType(Methods.makeItem(Main.settings.getFile(tier).getString("Settings.Placed-Block"), 1, "").getType());
				        if(Support.hasHolographicDisplay()){
							if(Main.settings.getFile(tier).getBoolean("Settings.Hologram-Toggle")){
								HolographicSupport.createHologram(loc.getBlock().getLocation(), tier);
							}
						}
						Envoy.removeFallingBlock(en);
						Envoy.addActiveEnvoy(loc.getBlock().getLocation(), tier);
						if(Main.settings.getFile(tier).getBoolean("Settings.Signal-Flare.Toggle")){
							Envoy.startSignalFlare(loc.getBlock().getLocation(), tier);
						}
					}
				}
			}
		}
	}
	
	public static void clearCooldowns(){
		cooldown.clear();
	}
	
	private Calendar getTimeFromString(String time){
		Calendar cal = Calendar.getInstance();
		for(String i : time.split(" ")){
			if(i.contains("D")||i.contains("d")){
				cal.add(Calendar.DATE, Integer.parseInt(i.replaceAll("D", "").replaceAll("d", "")));
			}
			if(i.contains("H")||i.contains("h")){
				cal.add(Calendar.HOUR, Integer.parseInt(i.replaceAll("H", "").replaceAll("h", "")));
			}
			if(i.contains("M")||i.contains("m")){
				cal.add(Calendar.MINUTE, Integer.parseInt(i.replaceAll("M", "").replaceAll("m", "")));
			}
			if(i.contains("S")||i.contains("s")){
				cal.add(Calendar.SECOND, Integer.parseInt(i.replaceAll("S", "").replaceAll("s", "")));
			}
		}
		return cal;
	}
	
	private String getTimeLeft(Calendar timeTill){
		Calendar C = Calendar.getInstance();
		int total = ((int) (timeTill.getTimeInMillis() / 1000) - (int) (C.getTimeInMillis() / 1000));
		int D = 0;
		int H = 0;
		int M = 0;
		int S = 0;
		for(;total > 86400; total -= 86400, D++);
		for(;total > 3600; total -= 3600, H++);
		for(;total >= 60; total -= 60, M++);
		S += total;
		String msg = "";
		if(D > 0) msg += D + "d, ";
		if(D > 0 || H > 0) msg += H + "h, ";
		if(D > 0 || H > 0 || M > 0) msg += M + "m, ";
		if(D > 0 || H > 0 || M > 0 || S > 0) msg += S + "s, ";
		if(msg.length() < 2){
			msg = "0s";
		}else{
			msg = msg.substring(0, msg.length() - 2);
		}
		return msg;
	}
	
}