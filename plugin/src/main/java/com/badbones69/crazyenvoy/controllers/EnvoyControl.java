package com.badbones69.crazyenvoy.controllers;

import com.badbones69.crazyenvoy.Methods;
import com.badbones69.crazyenvoy.api.CrazyManager;
import com.badbones69.crazyenvoy.api.enums.Messages;
import com.badbones69.crazyenvoy.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoy.api.events.OpenEnvoyEvent;
import com.badbones69.crazyenvoy.api.objects.EnvoySettings;
import com.badbones69.crazyenvoy.api.objects.ItemBuilder;
import com.badbones69.crazyenvoy.api.objects.Prize;
import com.badbones69.crazyenvoy.api.objects.Tier;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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
    private CrazyManager envoy = CrazyManager.getInstance();
    private EnvoySettings envoySettings = EnvoySettings.getInstance();
    private Random random = new Random();
    
    public static void clearCooldowns() {
        cooldown.clear();
    }
    
    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getClickedBlock() != null && envoy.isEnvoyActive()) {
            Block block = e.getClickedBlock();
            if (envoy.isActiveEnvoy(e.getClickedBlock())) {
                if (player.getGameMode() == GameMode.valueOf("SPECTATOR")) {
                    return;
                }
                if (player.getGameMode() == GameMode.CREATIVE && !player.hasPermission("envoy.gamemode-bypass")) {
                    return;
                }
                e.setCancelled(true);
                if (!player.hasPermission("envoy.bypass") && envoySettings.isCrateCooldownEnabled()) {
                    UUID uuid = player.getUniqueId();
                    if (cooldown.containsKey(uuid) && Calendar.getInstance().before(cooldown.get(uuid))) {
                        HashMap<String, String> placeholder = new HashMap<>();
                        placeholder.put("%time%", getTimeLeft(cooldown.get(uuid)));
                        placeholder.put("%Time%", getTimeLeft(cooldown.get(uuid)));
                        Messages.COOLDOWN_LEFT.sendMessage(player, placeholder);
                        return;
                    }
                    cooldown.put(uuid, getTimeFromString(envoySettings.getCrateCooldownTimer()));
                }
                Tier tier = envoy.getTier(e.getClickedBlock());
                List<Prize> prizes = tier.getUseChance() ? pickPrizesByChance(tier) : pickRandomPrizes(tier);
                OpenEnvoyEvent openEnvoyEvent = new OpenEnvoyEvent(player, block, tier, prizes);
                CrazyManager.getJavaPlugin().getServer().getPluginManager().callEvent(openEnvoyEvent);
                if (!openEnvoyEvent.isCancelled()) {
                    if (tier.getFireworkToggle()) {
                        Methods.fireWork(block.getLocation().add(.5, 0, .5), tier.getFireworkColors());
                    }
                    e.getClickedBlock().setType(Material.AIR);
                    if (envoy.hasHologramPlugin()) {
                        envoy.getHologramController().removeHologram(e.getClickedBlock());
                    }
                    envoy.stopSignalFlare(e.getClickedBlock().getLocation());
                    envoy.removeActiveEnvoy(block);
                    if (tier.getPrizes().isEmpty()) {
                        CrazyManager.getJavaPlugin().getServer().broadcastMessage(Methods.getPrefix() + Methods.color("&cNo prizes were found in the " + tier + " tier." + " Please add prizes other wise errors will occur."));
                        return;
                    }
                    for (Prize prize : openEnvoyEvent.getPrizes()) {
                        for (String msg : prize.getMessages()) {
                            player.sendMessage(Methods.color(msg));
                        }
                        for (String cmd : prize.getCommands()) {
                            CrazyManager.getJavaPlugin().getServer().dispatchCommand(CrazyManager.getJavaPlugin().getServer().getConsoleSender(), cmd.replace("%Player%", player.getName()).replace("%player%", player.getName()));
                        }
                        for (ItemStack item : prize.getItems()) {
                            if (prize.getDropItems()) {
                                e.getClickedBlock().getWorld().dropItem(block.getLocation(), item);
                            } else {
                                if (Methods.isInvFull(player)) {
                                    e.getClickedBlock().getWorld().dropItem(block.getLocation(), item);
                                } else {
                                    player.getInventory().addItem(item);
                                }
                            }
                        }
                        player.updateInventory();
                    }
                    if (!envoy.getActiveEnvoys().isEmpty()) {
                        if (envoySettings.isPickupBroadcastEnabled()) {
                            HashMap<String, String> placeholder = new HashMap<>();
                            placeholder.put("%player%", player.getName());
                            placeholder.put("%Player%", player.getName());
                            placeholder.put("%amount%", envoy.getActiveEnvoys().size() + "");
                            placeholder.put("%Amount%", envoy.getActiveEnvoys().size() + "");
                            Messages.LEFT.broadcastMessage(true, placeholder);
                        }
                    } else {
                        EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.ALL_CRATES_COLLECTED);
                        CrazyManager.getJavaPlugin().getServer().getPluginManager().callEvent(event);
                        envoy.endEnvoyEvent();
                        Messages.ENDED.broadcastMessage(false);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onChestSpawn(EntityChangeBlockEvent e) {
        if (envoy.isEnvoyActive()) {
            Entity entity = e.getEntity();
            if (envoy.getFallingBlocks().containsKey(entity)) {
                Block block = envoy.getFallingBlocks().get(entity);
                e.setCancelled(true);
                Tier tier = pickRandomTier();
                if (block.getType() != Material.AIR) {
                    block = block.getLocation().add(0, 1, 0).getBlock();
                }
                block.setType(new ItemBuilder().setMaterial(tier.getPlacedBlockMaterial()).getMaterial());
                if (tier.isHoloEnabled() && envoy.hasHologramPlugin()) {
                    envoy.getHologramController().createHologram(block, tier);
                }
                envoy.removeFallingBlock(entity);
                envoy.addActiveEnvoy(block, tier);
                envoy.addSpawnedLocation(block);
                if (tier.getSignalFlareToggle()) {
                    envoy.startSignalFlare(block.getLocation(), tier);
                }
            }
        }
    }
    
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        if (envoy.isEnvoyActive()) {
            for (Entity entity : e.getEntity().getNearbyEntities(1, 1, 1)) {
                if (envoy.getFallingBlocks().containsKey(entity)) {
                    Block block = envoy.getFallingBlocks().get(entity);
                    e.setCancelled(true);
                    Tier tier = pickRandomTier();
                    if (block.getType() != Material.AIR) {
                        block = block.getLocation().add(0, 1, 0).getBlock();
                    }
                    block.setType(new ItemBuilder().setMaterial(tier.getPlacedBlockMaterial()).getMaterial());
                    if (tier.isHoloEnabled() && envoy.hasHologramPlugin()) {
                        envoy.getHologramController().createHologram(block, tier);
                    }
                    envoy.removeFallingBlock(entity);
                    envoy.addActiveEnvoy(block, tier);
                    envoy.addSpawnedLocation(block);
                    if (tier.getSignalFlareToggle()) {
                        envoy.startSignalFlare(block.getLocation(), tier);
                    }
                    break;
                }
            }
        }
    }
    
    private Calendar getTimeFromString(String time) {
        Calendar cal = Calendar.getInstance();
        for (String i : time.split(" ")) {
            if (i.contains("D") || i.contains("d")) {
                cal.add(Calendar.DATE, Integer.parseInt(i.replace("D", "").replace("d", "")));
            }
            if (i.contains("H") || i.contains("h")) {
                cal.add(Calendar.HOUR, Integer.parseInt(i.replace("H", "").replace("h", "")));
            }
            if (i.contains("M") || i.contains("m")) {
                cal.add(Calendar.MINUTE, Integer.parseInt(i.replace("M", "").replace("m", "")));
            }
            if (i.contains("S") || i.contains("s")) {
                cal.add(Calendar.SECOND, Integer.parseInt(i.replace("S", "").replace("s", "")));
            }
        }
        return cal;
    }
    
    private String getTimeLeft(Calendar timeTill) {
        Calendar rightNow = Calendar.getInstance();
        int total = ((int) (timeTill.getTimeInMillis() / 1000) - (int) (rightNow.getTimeInMillis() / 1000));
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;
        for (; total > 86400; total -= 86400, day++) ;
        for (; total > 3600; total -= 3600, hour++) ;
        for (; total >= 60; total -= 60, minute++) ;
        second += total;
        String message = "";
        if (day > 0) message += day + "d, ";
        if (day > 0 || hour > 0) message += hour + "h, ";
        if (day > 0 || hour > 0 || minute > 0) message += minute + "m, ";
        if (day > 0 || hour > 0 || minute > 0 || second > 0) message += second + "s, ";
        if (message.length() < 2) {
            message = "0s";
        } else {
            message = message.substring(0, message.length() - 2);
        }
        return message;
    }
    
    private List<Prize> pickRandomPrizes(Tier tier) {
        ArrayList<Prize> prizes = new ArrayList<>();
        int max = tier.getBulkToggle() ? tier.getBulkMax() : 1;
        for (int i = 0; prizes.size() < max && i < 500; i++) {
            Prize prize = tier.getPrizes().get(random.nextInt(tier.getPrizes().size()));
            if (!prizes.contains(prize)) {
                prizes.add(prize);
            }
        }
        return prizes;
    }
    
    private List<Prize> pickPrizesByChance(Tier tier) {
        ArrayList<Prize> prizes = new ArrayList<>();
        int maxBulk = tier.getBulkToggle() ? tier.getBulkMax() : 1;
        for (int i = 0; prizes.size() < maxBulk && i < 500; i++) {
            for (Prize prize : tier.getPrizes()) {
                if (!prizes.contains(prize) && Methods.isSuccessful(prize.getChance(), 100)) {
                    prizes.add(prize);
                }
                if (prizes.size() == maxBulk) {
                    break;
                }
            }
        }
        return prizes;
    }
    
    private Tier pickRandomTier() {
        if (envoy.getTiers().size() == 1) {
            return envoy.getTiers().get(0);
        }
        ArrayList<Tier> tiers = new ArrayList<>();
        while (tiers.isEmpty()) {
            for (Tier tier : envoy.getTiers()) {
                if (Methods.isSuccessful(tier.getSpawnChance(), 100)) {
                    tiers.add(tier);
                }
            }
        }
        return tiers.get(random.nextInt(tiers.size()));
    }
}