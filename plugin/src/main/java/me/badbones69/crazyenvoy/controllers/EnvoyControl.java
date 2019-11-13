package me.badbones69.crazyenvoy.controllers;

import me.badbones69.crazyenvoy.Methods;
import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.enums.Messages;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent.EnvoyEndReason;
import me.badbones69.crazyenvoy.api.objects.EnvoySettings;
import me.badbones69.crazyenvoy.api.objects.ItemBuilder;
import me.badbones69.crazyenvoy.api.objects.Prize;
import me.badbones69.crazyenvoy.api.objects.Tier;
import me.badbones69.crazyenvoy.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EnvoyControl implements Listener {
	
	private static HashMap<UUID, Calendar> cooldown = new HashMap<>();
	private CrazyEnvoy envoy = CrazyEnvoy.getInstance();
	private EnvoySettings envoySettings = EnvoySettings.getInstance();
	
	public static void clearCooldowns() {
		cooldown.clear();
	}
	
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(envoy.isEnvoyActive()) {
			if(e.getClickedBlock() != null) {
				Block block = e.getClickedBlock();
				if(envoy.isActiveEnvoy(e.getClickedBlock())) {
					if(Version.getCurrentVersion().isNewer(Version.v1_7_R4)) {
						if(player.getGameMode() == GameMode.valueOf("SPECTATOR")) {
							return;
						}
					}
					if(player.getGameMode() == GameMode.CREATIVE) {
						if(!player.hasPermission("envoy.gamemode-bypass")) {
							return;
						}
					}
					e.setCancelled(true);
					if(!player.hasPermission("envoy.bypass")) {
						if(envoySettings.isCrateCooldownEnabled()) {
							UUID uuid = player.getUniqueId();
							if(cooldown.containsKey(uuid)) {
								if(Calendar.getInstance().before(cooldown.get(uuid))) {
									HashMap<String, String> placeholder = new HashMap<>();
									placeholder.put("%time%", getTimeLeft(cooldown.get(uuid)));
									placeholder.put("%Time%", getTimeLeft(cooldown.get(uuid)));
									Messages.COOLDOWN_LEFT.sendMessage(player, placeholder);
									return;
								}
							}
							cooldown.put(uuid, getTimeFromString(envoySettings.getCrateCooldownTimer()));
						}
					}
					Tier tier = envoy.getTier(e.getClickedBlock());
					if(tier.getFireworkToggle()) {
						Methods.fireWork(block.getLocation().add(.5, 0, .5), tier.getFireworkColors());
					}
					e.getClickedBlock().setType(Material.AIR);
					if(envoy.hasHologramPlugin()) {
						envoy.getHologramController().removeHologram(e.getClickedBlock());
					}
					envoy.stopSignalFlare(e.getClickedBlock().getLocation());
					envoy.removeActiveEnvoy(block);
					ArrayList<Prize> prizes;
					if(tier.getPrizes().size() == 0) {
						Bukkit.broadcastMessage(Methods.getPrefix() + Methods.color("&cNo prizes were found in the " + tier + " tier." + " Please add prizes other wise errors will occur."));
						return;
					}
					if(tier.getUseChance()) {
						prizes = pickPrizesByChance(tier);
					}else {
						prizes = pickRandomPrizes(tier);
					}
					for(Prize prize : prizes) {
						for(String msg : prize.getMessages()) {
							player.sendMessage(Methods.color(msg));
						}
						for(String cmd : prize.getCommands()) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName()));
						}
						for(ItemStack item : prize.getItems()) {
							if(prize.getDropItems()) {
								e.getClickedBlock().getWorld().dropItem(block.getLocation(), item);
							}else {
								if(Methods.isInvFull(player)) {
									e.getClickedBlock().getWorld().dropItem(block.getLocation(), item);
								}else {
									player.getInventory().addItem(item);
								}
							}
						}
						player.updateInventory();
					}
					if(envoy.getActiveEnvoys().size() >= 1) {
						if(envoySettings.isPickupBroadcastEnabled()) {
							HashMap<String, String> placeholder = new HashMap<>();
							placeholder.put("%player%", player.getName());
							placeholder.put("%Player%", player.getName());
							placeholder.put("%amount%", envoy.getActiveEnvoys().size() + "");
							placeholder.put("%Amount%", envoy.getActiveEnvoys().size() + "");
							Messages.LEFT.broadcastMessage(true, placeholder);
						}
					}else {
						EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.ALL_CRATES_COLLECTED);
						Bukkit.getPluginManager().callEvent(event);
						envoy.endEnvoyEvent();
						Messages.ENDED.broadcastMessage(false);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onChestSpawn(EntityChangeBlockEvent e) {
		if(envoy.isEnvoyActive()) {
			if(e.getEntity() instanceof FallingBlock) {
				if(!envoy.getFallingBlocks().isEmpty()) {
					if(envoy.getFallingBlocks().containsKey(entity)) {
						Block block = envoy.getFallingBlocks().get(entity);
						e.setCancelled(true);
						Tier tier = pickRandomTier();
						block.setType(new ItemBuilder().setMaterial(tier.getPlacedBlockMaterial()).getMaterial());
						if(tier.isHoloEnabled()) {
							if(envoy.hasHologramPlugin()) {
								envoy.getHologramController().createHologram(block, tier);
							}
						}
						envoy.removeFallingBlock(e.getEntity());
						envoy.addActiveEnvoy(block, tier);
						envoy.addSpawnedLocation(block);
						if(tier.getSignalFlareToggle()) {
							envoy.startSignalFlare(block.getLocation(), tier);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e) {
		if(envoy.isEnvoyActive()) {
			for(Entity en : e.getEntity().getNearbyEntities(0, 0, 0)) {
				if(!envoy.getFallingBlocks().isEmpty()) {
					if(envoy.getFallingBlocks().containsKey(entity)) {
						Block block = envoy.getFallingBlocks().get(entity);
						e.setCancelled(true);
						Tier tier = pickRandomTier();
						if(block.getType() != Material.AIR) {
							block = block.getLocation().add(0, 1, 0).getBlock();
						}
						block.setType(new ItemBuilder().setMaterial(tier.getPlacedBlockMaterial()).getMaterial());
						if(tier.isHoloEnabled()) {
							if(envoy.hasHologramPlugin()) {
								envoy.getHologramController().createHologram(block, tier);
							}
						}
						envoy.removeFallingBlock(en);
						envoy.addActiveEnvoy(block, tier);
						envoy.addSpawnedLocation(block);
						if(tier.getSignalFlareToggle()) {
							envoy.startSignalFlare(block.getLocation(), tier);
						}
					}
				}
			}
		}
	}
	
	private Calendar getTimeFromString(String time) {
		Calendar cal = Calendar.getInstance();
		for(String i : time.split(" ")) {
			if(i.contains("D") || i.contains("d")) {
				cal.add(Calendar.DATE, Integer.parseInt(i.replaceAll("D", "").replaceAll("d", "")));
			}
			if(i.contains("H") || i.contains("h")) {
				cal.add(Calendar.HOUR, Integer.parseInt(i.replaceAll("H", "").replaceAll("h", "")));
			}
			if(i.contains("M") || i.contains("m")) {
				cal.add(Calendar.MINUTE, Integer.parseInt(i.replaceAll("M", "").replaceAll("m", "")));
			}
			if(i.contains("S") || i.contains("s")) {
				cal.add(Calendar.SECOND, Integer.parseInt(i.replaceAll("S", "").replaceAll("s", "")));
			}
		}
		return cal;
	}
	
	private String getTimeLeft(Calendar timeTill) {
		Calendar C = Calendar.getInstance();
		int total = ((int) (timeTill.getTimeInMillis() / 1000) - (int) (C.getTimeInMillis() / 1000));
		int day = 0;
		int hour = 0;
		int minute = 0;
		int second = 0;
		for(; total > 86400; total -= 86400, day++) ;
		for(; total > 3600; total -= 3600, hour++) ;
		for(; total >= 60; total -= 60, minute++) ;
		second += total;
		String message = "";
		if(day > 0) message += day + "d, ";
		if(day > 0 || hour > 0) message += hour + "h, ";
		if(day > 0 || hour > 0 || minute > 0) message += minute + "m, ";
		if(day > 0 || hour > 0 || minute > 0 || second > 0) message += second + "s, ";
		if(message.length() < 2) {
			message = "0s";
		}else {
			message = message.substring(0, message.length() - 2);
		}
		return message;
	}
	
	private ArrayList<Prize> pickRandomPrizes(Tier tier) {
		ArrayList<Prize> prizes = new ArrayList<>();
		int max = tier.getBulkToggle() ? tier.getBulkMax() : 1;
		for(int i = 0; prizes.size() < max && i < 500; i++) {
			Prize prize = tier.getPrizes().get(new Random().nextInt(tier.getPrizes().size()));
			if(!prizes.contains(prize)) {
				prizes.add(prize);
			}
		}
		return prizes;
	}
	
	private ArrayList<Prize> pickPrizesByChance(Tier tier) {
		ArrayList<Prize> prizes = new ArrayList<>();
		int max = tier.getBulkToggle() ? tier.getBulkMax() : 1;
		for(int i = 0; prizes.size() < max && i < 500; i++) {
			Prize prize = tier.getPrizes().get(new Random().nextInt(tier.getPrizes().size()));
			if(!prizes.contains(prize)) {
				prizes.add(prize);
			}
		}
		return prizes;
	}
	
	private Tier pickRandomTier() {
		if(envoy.getTiers().size() == 1) {
			return envoy.getTiers().get(0);
		}
		ArrayList<Tier> tiers = new ArrayList<>();
		for(; tiers.size() == 0; ) {
			for(Tier tier : envoy.getTiers()) {
				if(Methods.isSuccessful(tier.getSpawnChance(), 100)) {
					tiers.add(tier);
				}
			}
		}
		return tiers.get(new Random().nextInt(tiers.size()));
	}
	
}