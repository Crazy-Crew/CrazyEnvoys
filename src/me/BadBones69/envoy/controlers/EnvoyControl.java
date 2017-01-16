package me.BadBones69.envoy.controlers;

import java.util.ArrayList;

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

import me.BadBones69.envoy.Main;
import me.BadBones69.envoy.Methods;
import me.BadBones69.envoy.MultiSupport.HolographicSupport;
import me.BadBones69.envoy.MultiSupport.Support;
import me.BadBones69.envoy.api.Envoy;
import me.BadBones69.envoy.api.Prizes;

public class EnvoyControl implements Listener{
	
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(Envoy.isEnvoyActive()){
			if(e.getClickedBlock() != null){
				Location loc = e.getClickedBlock().getLocation();
				if(Envoy.isActiveEnvoy(loc)){
					if(GameMode.valueOf("SPECTATOR") != null){
						if(player.getGameMode() == GameMode.valueOf("SPECTATOR")){
							return;
						}
					}
					String tier = Envoy.getTier(loc);
					e.setCancelled(true);
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
						HolographicSupport.removeHologram(loc.clone().add(.5, 1.5, .5));
					}
					Envoy.stopSignalFlare(e.getClickedBlock().getLocation());
					Envoy.removeActiveEvoy(loc);
					ArrayList<String> prizes = new ArrayList<String>();
					if(Prizes.getPrizes(tier).size() == 0){
						Methods.broadcastMessage(Methods.getPrefix() + Methods.color("&cNo prizes were found in the " + tier + " tier."
								+ " Please add prizes other wise errors will occur."));
						return;
					}
					if(Prizes.isRandom(tier)){
						prizes = Prizes.pickRandomPrizes(tier);
					}else{
						prizes = Prizes.pickPrizesByChance(tier);
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
					}
					if(Envoy.getActiveEvoys().size() >= 1){
						Methods.broadcastMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Left")
											.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())
											.replaceAll("%Amount%", Envoy.getActiveEvoys().size() + "")
											.replaceAll("%amount%", Envoy.getActiveEvoys().size() +  "")));
					}else{
						Envoy.endEnvoyEvent();
						Methods.broadcastMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Ended")));
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
								HolographicSupport.createHologram(loc.getBlock().getLocation().add(.5, 1.5, .5), tier);
							}
						}
						Envoy.removeFallingBlock(e.getEntity());
						Envoy.addActiveEvoy(loc.getBlock().getLocation(), tier);
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
								HolographicSupport.createHologram(loc.getBlock().getLocation().add(.5, 1.5, .5), tier);
							}
						}
						Envoy.removeFallingBlock(en);
						Envoy.addActiveEvoy(loc.getBlock().getLocation(), tier);
						if(Main.settings.getFile(tier).getBoolean("Settings.Signal-Flare.Toggle")){
							Envoy.startSignalFlare(loc.getBlock().getLocation(), tier);
						}
					}
				}
			}
		}
	}
	
}