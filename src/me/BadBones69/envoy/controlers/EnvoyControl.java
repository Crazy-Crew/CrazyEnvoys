package me.BadBones69.envoy.controlers;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
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
import me.BadBones69.envoy.api.Envoy;
import me.BadBones69.envoy.api.Prizes;

public class EnvoyControl implements Listener{
	
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent e){
		if(Envoy.isEnvoyActive()){
			if(e.getClickedBlock() != null){
				if(Envoy.isActiveEnvoy(e.getClickedBlock().getLocation())){
					Player player = e.getPlayer();
					e.setCancelled(true);
					if(Main.settings.getConfig().getBoolean("Settings.Firework-Toggle")){
						ArrayList<Color> colors = new ArrayList<Color>();
						for(String c : Main.settings.getConfig().getStringList("Settings.Firework-Colors")){
							Color color = Methods.getColor(c);
							if(color != null){
								colors.add(color);
							}
						}
						Methods.fireWork(e.getClickedBlock().getLocation().clone().add(.5, 0, .5), colors);
					}
					e.getClickedBlock().setType(Material.AIR);
					if(Methods.hasHolographicDisplay()){
						HolographicSupport.removeHologram(e.getClickedBlock().getLocation().add(.5, 1.5, .5));
					}
					Envoy.removeActiveEvoy(e.getClickedBlock().getLocation());
					String prize = "";
					if(Prizes.isRandom()){
						prize = Prizes.pickRandomPrize();
					}else{
						prize = Prizes.pickPrizeByChance();
					}
					if(!prize.equals("")){
						for(String msg : Prizes.getMessages(prize)){
							player.sendMessage(Methods.color(msg));
						}
						for(String cmd : Prizes.getCommands(prize)){
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName()));
						}
						for(ItemStack item : Prizes.getItems(prize)){
							if(Methods.isInvFull(player)){
								e.getClickedBlock().getWorld().dropItem(e.getClickedBlock().getLocation(), item);
							}else{
								player.getInventory().addItem(item);
							}
						}
					}else{
						player.sendMessage(Methods.getPrefix() + Methods.color("&cNo prize was found."));
					}
					if(Envoy.getActiveEvoys().size() >= 1){
						Bukkit.broadcastMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Left")
								.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())
								.replaceAll("%Amount%", Envoy.getActiveEvoys().size() + "")
								.replaceAll("%amount%", Envoy.getActiveEvoys().size() +  "")));
					}else{
						Envoy.endEnvoy();
						Bukkit.broadcastMessage(Methods.getPrefix() + Methods.color(Main.settings.getMessages().getString("Messages.Ended")));
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
						e.getBlock().setType(Methods.makeItem(Main.settings.getConfig().getString("Settings.Placed-Block"), 1, "").getType());
						if(Methods.hasHolographicDisplay()){
							HolographicSupport.createHologram(e.getBlock().getLocation().add(.5, 1.5, .5));
						}
						Envoy.removeFallingBlock(e.getEntity());
						Envoy.addActiveEvoy(e.getBlock().getLocation());
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e){
		if(Envoy.isEnvoyActive()){
			for(Entity en : e.getEntity().getNearbyEntities(2, 2, 2)){
				if(!Envoy.getFallingBlocks().isEmpty()){
					if(Envoy.getFallingBlocks().contains(en)){
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
}