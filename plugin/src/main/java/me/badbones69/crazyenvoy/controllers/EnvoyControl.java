package me.badbones69.crazyenvoy.controllers;

import me.badbones69.crazyenvoy.Methods;
import me.badbones69.crazyenvoy.api.CrazyEnvoy;
import me.badbones69.crazyenvoy.api.FileManager.Files;
import me.badbones69.crazyenvoy.api.enums.Messages;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent;
import me.badbones69.crazyenvoy.api.events.EnvoyEndEvent.EnvoyEndReason;
import me.badbones69.crazyenvoy.api.objects.ItemBuilder;
import me.badbones69.crazyenvoy.api.objects.Prize;
import me.badbones69.crazyenvoy.api.objects.Tier;
import me.badbones69.crazyenvoy.multisupport.CMISupport;
import me.badbones69.crazyenvoy.multisupport.HolographicSupport;
import me.badbones69.crazyenvoy.multisupport.Support;
import me.badbones69.crazyenvoy.multisupport.Version;
import org.bukkit.Bukkit;
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

import java.util.*;

public class EnvoyControl implements Listener {
	
	private static HashMap<UUID, Calendar> cooldown = new HashMap<>();
	private CrazyEnvoy envoy = CrazyEnvoy.getInstance();
	
	public static void clearCooldowns() {
		cooldown.clear();
	}
	
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(envoy.isEnvoyActive()) {
			if(e.getClickedBlock() != null) {
				Location loc = e.getClickedBlock().getLocation();
				if(envoy.isActiveEnvoy(loc)) {
					if(Version.getCurrentVersion().getVersionInteger() > Version.v1_7_R4.getVersionInteger()) {
						if(player.getGameMode() == GameMode.valueOf("SPECTATOR")) {
							return;
						}
					}
					e.setCancelled(true);
					if(!player.hasPermission("crazyenvoy.bypass")) {
						if(Files.CONFIG.getFile().contains("Settings.Crate-Collect-Cooldown")) {
							if(Files.CONFIG.getFile().getBoolean("Settings.Crate-Collect-Cooldown.Toggle")) {
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
								cooldown.put(uuid, getTimeFromString(Files.CONFIG.getFile().getString("Settings.Crate-Collect-Cooldown.Time")));
							}
						}
					}
					Tier tier = envoy.getTier(loc);
					if(tier.getFireworkToggle()) {
						Methods.fireWork(loc.clone().add(.5, 0, .5), tier.getFireworkColors());
					}
					e.getClickedBlock().setType(Material.AIR);
					if(Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
						double hight = tier.getHoloHight();
						HolographicSupport.removeHologram(loc.clone().add(.5, hight, .5));
					}else if(Support.CMI.isPluginLoaded()) {
						double hight = tier.getHoloHight();
						CMISupport.removeHologram(loc.clone().add(.5, hight, .5));
					}
					envoy.stopSignalFlare(e.getClickedBlock().getLocation());
					envoy.removeActiveEnvoy(loc);
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
								e.getClickedBlock().getWorld().dropItem(loc, item);
							}else {
								if(Methods.isInvFull(player)) {
									e.getClickedBlock().getWorld().dropItem(loc, item);
								}else {
									player.getInventory().addItem(item);
								}
							}
						}
						player.updateInventory();
					}
					if(envoy.getActiveEnvoys().size() >= 1) {
						if(Files.CONFIG.getFile().contains("Settings.Broadcast-Crate-Pick-Up")) {
							if(Files.CONFIG.getFile().getBoolean("Settings.Broadcast-Crate-Pick-Up")) {
								HashMap<String, String> placeholder = new HashMap<>();
								placeholder.put("%player%", player.getName());
								placeholder.put("%Player%", player.getName());
								placeholder.put("%amount%", envoy.getActiveEnvoys().size() + "");
								placeholder.put("%Amount%", envoy.getActiveEnvoys().size() + "");
								Messages.LEFT.broadcastMessage(true, placeholder);
							}
						}else {
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
						Messages.ENDED.broadcastMessage(false, null);
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
					if(envoy.getFallingBlocks().contains(e.getEntity())) {
						Location loc = e.getBlock().getLocation();
						e.setCancelled(true);
						Tier tier = pickRandomTier();
						if(loc.getBlock().getType() != Material.AIR) {
							loc.add(0, 1, 0);
						}
						loc.getBlock().setType(new ItemBuilder().setMaterial(tier.getPlacedBlockMaterial()).getMaterial());
						if(Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
							if(tier.isHoloEnabled()) {
								HolographicSupport.createHologram(loc.getBlock().getLocation(), tier);
							}
						}else if(Support.CMI.isPluginLoaded()) {
							if(tier.isHoloEnabled()) {
								CMISupport.createHologram(loc.getBlock().getLocation(), tier);
							}
						}
						envoy.removeFallingBlock(e.getEntity());
						envoy.addActiveEnvoy(loc.getBlock().getLocation(), tier);
						envoy.addSpawnedLocation(loc.getBlock().getLocation());
						if(tier.getSignalFlareToggle()) {
							envoy.startSignalFlare(loc.getBlock().getLocation(), tier);
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
					if(envoy.getFallingBlocks().contains(en)) {
						e.setCancelled(true);
						Tier tier = pickRandomTier();
						Location loc = en.getLocation();
						if(loc.getBlock().getType() != Material.AIR) {
							loc.add(0, 1, 0);
						}
						loc.getBlock().setType(new ItemBuilder().setMaterial(tier.getPlacedBlockMaterial()).getMaterial());
						if(Support.HOLOGRAPHIC_DISPLAYS.isPluginLoaded()) {
							if(tier.isHoloEnabled()) {
								HolographicSupport.createHologram(loc.getBlock().getLocation(), tier);
							}
						}else if(Support.CMI.isPluginLoaded()) {
							if(tier.isHoloEnabled()) {
								CMISupport.createHologram(loc.getBlock().getLocation(), tier);
							}
						}
						envoy.removeFallingBlock(en);
						envoy.addActiveEnvoy(loc.getBlock().getLocation(), tier);
						envoy.addSpawnedLocation(loc.getBlock().getLocation());
						if(tier.getSignalFlareToggle()) {
							envoy.startSignalFlare(loc.getBlock().getLocation(), tier);
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
		int D = 0;
		int H = 0;
		int M = 0;
		int S = 0;
		for(; total > 86400; total -= 86400, D++) ;
		for(; total > 3600; total -= 3600, H++) ;
		for(; total >= 60; total -= 60, M++) ;
		S += total;
		String msg = "";
		if(D > 0) msg += D + "d, ";
		if(D > 0 || H > 0) msg += H + "h, ";
		if(D > 0 || H > 0 || M > 0) msg += M + "m, ";
		if(D > 0 || H > 0 || M > 0 || S > 0) msg += S + "s, ";
		if(msg.length() < 2) {
			msg = "0s";
		}else {
			msg = msg.substring(0, msg.length() - 2);
		}
		return msg;
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
		for(; prizes.size() == 0; ) {
			for(Prize prize : tier.getPrizes()) {
				if(Methods.isSuccessful(prize.getChance(), 100)) {
					prizes.add(prize);
				}
			}
		}
		ArrayList<Prize> finlePrizes = new ArrayList<>();
		int max = tier.getBulkToggle() ? tier.getBulkMax() : 1;
		for(int i = 0; finlePrizes.size() < max && i < 500; i++) {
			Prize prize = prizes.get(new Random().nextInt(prizes.size()));
			if(!finlePrizes.contains(prize)) {
				finlePrizes.add(prize);
			}
		}
		return finlePrizes;
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