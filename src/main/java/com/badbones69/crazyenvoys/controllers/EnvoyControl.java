package com.badbones69.crazyenvoys.controllers;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyOpenEvent;
import com.badbones69.crazyenvoys.api.objects.EnvoySettings;
import com.badbones69.crazyenvoys.api.objects.ItemBuilder;
import com.badbones69.crazyenvoys.api.objects.Prize;
import com.badbones69.crazyenvoys.api.objects.Tier;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EnvoyControl implements Listener {

    private final CrazyEnvoys plugin = CrazyEnvoys.getPlugin();

    private final CrazyManager crazyManager = plugin.getCrazyManager();

    private final Methods methods = plugin.getMethods();

    private final EnvoySettings envoySettings = plugin.getEnvoySettings();
    
    private final HashMap<UUID, Calendar> cooldown = new HashMap<>();
    private final Random random = new Random();
    
    public void clearCooldowns() {
        cooldown.clear();
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getClickedBlock() != null && crazyManager.isEnvoyActive()) {
            Block block = e.getClickedBlock();

            if (crazyManager.isActiveEnvoy(e.getClickedBlock())) {
                if (player.getGameMode() == GameMode.valueOf("SPECTATOR")) return;

                if (player.getGameMode() == GameMode.CREATIVE && !player.hasPermission("envoy.gamemode-bypass")) return;

                e.setCancelled(true);

                // Ryder Start

                Tier tier = crazyManager.getTier(e.getClickedBlock());

                if (!player.hasPermission("envoy.bypass") && tier.isClaimPermissionToggleEnabled() && !player.hasPermission(tier.getClaimPermission())) {
                    player.sendMessage(Messages.NO_PERMISSION_CLAIM.getMessage());
                    return;
                }

                // Ryder End

                if (!player.hasPermission("envoy.bypass") && envoySettings.isCrateCooldownEnabled()) {
                    UUID uuid = player.getUniqueId();

                    if (cooldown.containsKey(uuid) && Calendar.getInstance().before(cooldown.get(uuid))) {
                        HashMap<String, String> placeholder = new HashMap<>();
                        placeholder.put("%Time%", methods.convertTimeToString(cooldown.get(uuid)));
                        Messages.COOLDOWN_LEFT.sendMessage(player, placeholder);
                        return;
                    }

                    cooldown.put(uuid, getTimeFromString(envoySettings.getCrateCooldownTimer()));
                }

                List<Prize> prizes = tier.getUseChance() ? pickPrizesByChance(tier) : pickRandomPrizes(tier);
                EnvoyOpenEvent envoyOpenEvent = new EnvoyOpenEvent(player, block, tier, prizes);
                plugin.getServer().getPluginManager().callEvent(envoyOpenEvent);

                if (!envoyOpenEvent.isCancelled()) {
                    if (tier.getFireworkToggle()) methods.firework(block.getLocation().add(.5, 0, .5), tier.getFireworkColors());

                    e.getClickedBlock().setType(Material.AIR);

                    if (crazyManager.hasHologramPlugin()) crazyManager.getHologramController().removeHologram(e.getClickedBlock());

                    crazyManager.stopSignalFlare(e.getClickedBlock().getLocation());

                    HashMap<String, String> placeholder = new HashMap<>();

                    if (envoySettings.isPickupBroadcastEnabled()) placeholder.put("%Tier%", crazyManager.getTier(block).getName());

                    crazyManager.removeActiveEnvoy(block);

                    if (tier.getPrizes().isEmpty()) {
                        plugin.getServer().broadcastMessage(methods.getPrefix() + methods.color("&cNo prizes were found in the " + tier + " tier." + " Please add prizes other wise errors will occur."));
                        return;
                    }

                    for (Prize prize : envoyOpenEvent.getPrizes()) {
                        for (String msg : prize.getMessages()) {
                            player.sendMessage(methods.color(msg));
                        }

                        for (String cmd : prize.getCommands()) {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd.replace("%Player%", player.getName()).replace("%player%", player.getName()));
                        }

                        for (ItemStack item : prize.getItems()) {
                            if (prize.getDropItems()) {
                                e.getClickedBlock().getWorld().dropItem(block.getLocation(), item);
                            } else {
                                if (methods.isInvFull(player)) {
                                    e.getClickedBlock().getWorld().dropItem(block.getLocation(), item);
                                } else {
                                    player.getInventory().addItem(item);
                                }
                            }
                        }

                        player.updateInventory();
                    }

                    if (!crazyManager.getActiveEnvoys().isEmpty()) {
                        if (envoySettings.isPickupBroadcastEnabled()) {
                            placeholder.put("%Player%", player.getName());
                            placeholder.put("%Amount%", crazyManager.getActiveEnvoys().size() + "");
                            Messages.LEFT.broadcastMessage(true, placeholder);
                        }
                    } else {
                        EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.ALL_CRATES_COLLECTED);
                        plugin.getServer().getPluginManager().callEvent(event);
                        crazyManager.endEnvoyEvent();
                        Messages.ENDED.broadcastMessage(false);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChestSpawn(EntityChangeBlockEvent e) {
        if (crazyManager.isEnvoyActive()) {
            Entity entity = e.getEntity();

            if (crazyManager.getFallingBlocks().containsKey(entity)) {
                e.setCancelled(true);
                checkEntity(entity);
            }
        }
    }

    private void checkEntity(Entity entity) {
        Block block = crazyManager.getFallingBlocks().get(entity);
        Tier tier = pickRandomTier();

        if (block.getType() != Material.AIR) block = block.getLocation().add(0, 1, 0).getBlock();

        block.setType(new ItemBuilder().setMaterial(tier.getPlacedBlockMaterial()).getMaterial());

        if (tier.isHoloEnabled() && crazyManager.hasHologramPlugin()) crazyManager.getHologramController().createHologram(block, tier);

        crazyManager.removeFallingBlock(entity);
        crazyManager.addActiveEnvoy(block, tier);
        crazyManager.addSpawnedLocation(block);

        if (tier.getSignalFlareToggle()) crazyManager.startSignalFlare(block.getLocation(), tier);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent e) {
        if (crazyManager.isEnvoyActive()) {
            for (Entity entity : e.getEntity().getNearbyEntities(1, 1, 1)) {
                if (crazyManager.getFallingBlocks().containsKey(entity)) {
                    Block block = crazyManager.getFallingBlocks().get(entity);
                    e.setCancelled(true);

                    checkEntity((Entity) block);

                    break;
                }
            }
        }
    }
    
    private Calendar getTimeFromString(String time) {
        Calendar cal = Calendar.getInstance();

        for (String i : time.split(" ")) {
            if (i.contains("D") || i.contains("d")) cal.add(Calendar.DATE, Integer.parseInt(i.replace("D", "").replace("d", "")));

            if (i.contains("H") || i.contains("h")) cal.add(Calendar.HOUR, Integer.parseInt(i.replace("H", "").replace("h", "")));

            if (i.contains("M") || i.contains("m")) cal.add(Calendar.MINUTE, Integer.parseInt(i.replace("M", "").replace("m", "")));

            if (i.contains("S") || i.contains("s")) cal.add(Calendar.SECOND, Integer.parseInt(i.replace("S", "").replace("s", "")));
        }

        return cal;
    }
    
    private List<Prize> pickRandomPrizes(Tier tier) {
        ArrayList<Prize> prizes = new ArrayList<>();
        int max = tier.getBulkToggle() ? tier.getBulkMax() : 1;

        for (int i = 0; prizes.size() < max && i < 500; i++) {
            Prize prize = tier.getPrizes().get(random.nextInt(tier.getPrizes().size()));

            if (!prizes.contains(prize)) prizes.add(prize);
        }

        return prizes;
    }
    
    private List<Prize> pickPrizesByChance(Tier tier) {
        ArrayList<Prize> prizes = new ArrayList<>();
        int maxBulk = tier.getBulkToggle() ? tier.getBulkMax() : 1;

        for (int i = 0; prizes.size() < maxBulk && i < 500; i++) {
            for (Prize prize : tier.getPrizes()) {
                if (!prizes.contains(prize) && methods.isSuccessful(prize.getChance(), 100)) prizes.add(prize);

                if (prizes.size() == maxBulk) break;
            }
        }

        return prizes;
    }
    
    private Tier pickRandomTier() {
        if (crazyManager.getTiers().size() == 1) return crazyManager.getTiers().get(0);

        ArrayList<Tier> tiers = new ArrayList<>();

        while (tiers.isEmpty()) {
            for (Tier tier : crazyManager.getTiers()) {
                if (methods.isSuccessful(tier.getSpawnChance(), 100)) tiers.add(tier);
            }
        }

        return tiers.get(random.nextInt(tiers.size()));
    }
}