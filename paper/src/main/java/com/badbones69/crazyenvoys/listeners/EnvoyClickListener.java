package com.badbones69.crazyenvoys.listeners;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.builders.types.PrizeGui;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyOpenEvent;
import com.badbones69.crazyenvoys.api.objects.CoolDownSettings;
import com.badbones69.crazyenvoys.api.objects.ItemBuilder;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.api.objects.misc.Prize;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.badbones69.crazyenvoys.config.beans.GuiProperty;
import com.badbones69.crazyenvoys.support.holograms.HologramManager;
import com.badbones69.crazyenvoys.util.MiscUtils;
import com.badbones69.crazyenvoys.util.MsgUtils;
import com.ryderbelserion.fusion.core.api.support.ModSupport;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import com.ryderbelserion.fusion.paper.scheduler.Scheduler;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.config.types.ConfigKeys;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import static java.util.regex.Matcher.quoteReplacement;

public class EnvoyClickListener implements Listener {

    private @NotNull final CrazyEnvoys plugin = CrazyEnvoys.get();
    private @NotNull final SettingsManager config = ConfigManager.getConfig();

    private final FusionPaper fusion = this.plugin.getFusion();

    private final Server server = this.plugin.getServer();

    private @NotNull final CoolDownSettings coolDownSettings = this.plugin.getCoolDownSettings();
    private @NotNull final LocationSettings locationSettings = this.plugin.getLocationSettings();

    private @NotNull final CrazyManager crazyManager = this.plugin.getCrazyManager();
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getClickedBlock() == null && !this.crazyManager.isEnvoyActive()) return;

        Block block = event.getClickedBlock();

        if (!this.crazyManager.isActiveEnvoy(block)) return;

        if (player.getGameMode() == GameMode.valueOf("SPECTATOR")) return;

        if (player.getGameMode() == GameMode.CREATIVE && !player.hasPermission("envoy.gamemode-bypass")) return;

        event.setCancelled(true);

        // Ryder Start
        Tier tier = this.crazyManager.getTier(event.getClickedBlock());

        if (!player.hasPermission("envoy.bypass")) {
            if (this.config.getProperty(ConfigKeys.envoys_grace_period_toggle) && this.crazyManager.getCountdownTimer().getSecondsLeft() != 0) {
                Map<String, String> placeholder = new HashMap<>();
                placeholder.put("{time}", String.valueOf(this.crazyManager.getCountdownTimer().getSecondsLeft()));

                Messages.countdown_in_progress.sendMessage(player, placeholder);
                return;
            }

            if (tier.isClaimPermissionToggleEnabled() && !player.hasPermission(tier.getClaimPermission())) {
                Messages.no_claim_permission.sendMessage(player);
                return;
            }

            if (this.config.getProperty(ConfigKeys.envoys_grab_cooldown_toggle)) {
                UUID uuid = player.getUniqueId();

                if (this.coolDownSettings.getCooldown().containsKey(uuid) && Calendar.getInstance().before(this.coolDownSettings.getCooldown().get(uuid))) {
                    Map<String, String> placeholder = new HashMap<>();
                    placeholder.put("{time}", Methods.convertTimeToString(this.coolDownSettings.getCooldown().get(uuid)));

                    Messages.cooldown_left.sendMessage(player, placeholder);
                    return;
                }

                this.coolDownSettings.addCooldown(uuid, this.config.getProperty(ConfigKeys.envoys_grab_cooldown_timer));
            }
        }
        // Ryder End

        List<Prize> prizes = tier.getUseChance() ? pickPrizesByChance(tier) : pickRandomPrizes(tier);
        EnvoyOpenEvent envoyOpenEvent = new EnvoyOpenEvent(player, block, tier, prizes);
        this.plugin.getServer().getPluginManager().callEvent(envoyOpenEvent);

        if (envoyOpenEvent.isCancelled()) return;

        if (tier.getFireworkToggle()) Methods.firework(block.getLocation().add(.5, 0, .5), tier.getFireworkColors());

        block.setType(Material.AIR);

        if (this.holograms != null) this.holograms.removeHologram(MiscUtils.toString(block.getLocation()));

        this.crazyManager.stopSignalFlare(block.getLocation());

        Map<String, String> placeholder = new HashMap<>();

        if (this.config.getProperty(ConfigKeys.envoys_announce_player_pickup)) placeholder.put("{tier}", this.crazyManager.getTier(block).getName());

        this.crazyManager.removeActiveEnvoy(block);

        if (tier.getPrizes().isEmpty()) {
            this.plugin.getServer().broadcastMessage(Methods.getPrefix() + MsgUtils.color("&cNo prizes were found in the " + tier + " tier." + " Please add prizes other wise errors will occur."));

            return;
        }

        final boolean isPapiReady = this.fusion.isModReady(ModSupport.placeholder_api);

        for (Prize prize : envoyOpenEvent.getPrizes()) {
            if (!tier.getPrizeMessage().isEmpty() && prize.getMessages().isEmpty()) {
                for (String message : tier.getPrizeMessage()) {
                    if (isPapiReady) {
                        message = PlaceholderAPI.setPlaceholders(player, message);
                    }

                    player.sendMessage(MsgUtils.color(message.replaceAll("\\{player}", player.getName()).replaceAll("\\{reward}", quoteReplacement(prize.getDisplayName())).replaceAll("\\{tier}", tier.getName())));
                }
            } else {
                for (String message : prize.getMessages()) {
                    if (isPapiReady) {
                        message = PlaceholderAPI.setPlaceholders(player, message);
                    }

                    player.sendMessage(MsgUtils.color(message.replaceAll("\\{player}", player.getName()).replaceAll("\\{reward}", quoteReplacement(prize.getDisplayName())).replaceAll("\\{tier}", tier.getName())));
                }
            }

            new FoliaScheduler(this.plugin, Scheduler.global_scheduler) {
                @Override
                public void run() {
                    for (String cmd : prize.getCommands()) {
                        if (isPapiReady) cmd = PlaceholderAPI.setPlaceholders(player, cmd);

                        server.dispatchCommand(server.getConsoleSender(), cmd.replace("{player}", player.getName())
                                .replaceAll("\\{tier}", quoteReplacement(prize.getDisplayName())));
                    }
                }
            }.runNow();

            if (!this.config.getProperty(ConfigKeys.envoy_menu_open)) {
                for (ItemStack item : prize.getItems()) {
                    if (prize.getDropItems()) {
                        event.getClickedBlock().getWorld().dropItem(block.getLocation(), item);
                    } else {
                        if (Methods.isInvFull(player)) {
                            event.getClickedBlock().getWorld().dropItem(block.getLocation(), item);
                        } else {
                            Methods.addItem(player, item);
                        }
                    }
                }

                player.updateInventory();
            } else {
                if (!prize.getItems().isEmpty()) {
                    GuiProperty property = this.config.getProperty(ConfigKeys.envoy_menu);

                    player.openInventory(new PrizeGui(player, property.getTitle(), property.getSize(), tier, prize).build().getInventory());
                }
            }
        }

        if (!this.crazyManager.getActiveEnvoys().isEmpty()) {
            if (this.config.getProperty(ConfigKeys.envoys_announce_player_pickup)) {
                placeholder.put("{player}", player.getName());
                placeholder.put("{amount}", String.valueOf(this.crazyManager.getActiveEnvoys().size()));
                Messages.envoys_remaining.broadcastMessage(true, placeholder);
            }
        } else {
            EnvoyEndEvent envoyEndEvent = new EnvoyEndEvent(EnvoyEndEvent.EnvoyEndReason.ALL_CRATES_COLLECTED);
            this.plugin.getServer().getPluginManager().callEvent(envoyEndEvent);
            this.crazyManager.endEnvoyEvent();
            Messages.ended.broadcastMessage(false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChestSpawn(EntityChangeBlockEvent event) {
        if (!this.crazyManager.isEnvoyActive()) return;

        Entity entity = event.getEntity();

        if (!this.crazyManager.getFallingBlocks().containsKey(entity)) return;

        event.setCancelled(true);

        checkEntity(entity);
    }

    private final HologramManager holograms = this.crazyManager.getHolograms();

    private void checkEntity(Entity entity) {
        Block block = this.crazyManager.getFallingBlocks().get(entity);
        Tier tier = pickRandomTier();

        if (block.getType() != Material.AIR) block = block.getLocation().add(0, 1, 0).getBlock();

        block.setType(new ItemBuilder().setMaterial(tier.getPlacedBlockMaterial()).getMaterial());

        final Location location = block.getLocation();

        if (this.holograms != null) this.holograms.createHologram(location, tier, MiscUtils.toString(location));

        this.crazyManager.removeFallingBlock(entity);
        this.crazyManager.addActiveEnvoy(block, tier);
        this.locationSettings.addActiveLocation(block);

        if (tier.getSignalFlareToggle() && block.getChunk().isLoaded()) this.crazyManager.startSignalFlare(location, tier);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        if (!this.crazyManager.isEnvoyActive()) return;

        for (Entity entity : event.getEntity().getNearbyEntities(1, 1, 1)) {
            if (!this.crazyManager.getFallingBlocks().containsKey(entity)) continue;

            Block block = this.crazyManager.getFallingBlocks().get(entity);
            event.setCancelled(true);

            checkEntity((Entity) block);

            break;
        }
    }
    
    private List<Prize> pickRandomPrizes(Tier tier) {
        List<Prize> prizes = new ArrayList<>();

        int max = tier.getBulkToggle() ? tier.getBulkMax() : 1;

        for (int i = 0; prizes.size() < max && i < 500; i++) {
            Prize prize = tier.getPrizes().get(ThreadLocalRandom.current().nextInt(tier.getPrizes().size()));

            if (!prizes.contains(prize)) prizes.add(prize);
        }

        return prizes;
    }
    
    private List<Prize> pickPrizesByChance(Tier tier) {
        List<Prize> prizes = new ArrayList<>();

        int maxBulk = tier.getBulkToggle() ? tier.getBulkMax() : 1;

        for (int i = 0; prizes.size() < maxBulk && i < 500; i++) {
            for (Prize prize : tier.getPrizes()) {
                if (!prizes.contains(prize) && Methods.isSuccessful(prize.getChance(), 100)) prizes.add(prize);

                if (prizes.size() == maxBulk) break;
            }
        }

        return prizes;
    }
    
    private Tier pickRandomTier() {
        if (this.crazyManager.getTiers().size() == 1) return this.crazyManager.getTiers().getFirst();

        List<Tier> tiers = new ArrayList<>();

        while (tiers.isEmpty()) {
            for (Tier tier : this.crazyManager.getTiers()) {
                if (Methods.isSuccessful(tier.getSpawnChance(), 100)) tiers.add(tier);
            }
        }

        return tiers.get(ThreadLocalRandom.current().nextInt(tiers.size()));
    }
}