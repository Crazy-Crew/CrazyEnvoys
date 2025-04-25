package com.badbones69.crazyenvoys.api.events;

import com.badbones69.crazyenvoys.api.objects.misc.Prize;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import java.util.List;

/**
 * Event is called when a player opens a envoy crate.
 */
public class EnvoyOpenEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location location;
    private final Block block;
    private final Player player;
    private final Tier tier;
    private List<Prize> prizes;
    
    public EnvoyOpenEvent(Player player, Block block, Tier tier, List<Prize> prizes) {
        this(player, block.getLocation(), tier, prizes);
    }
    
    public EnvoyOpenEvent(Player player, Location location, Tier tier, List<Prize> prizes) {
        this.player = player;
        this.location = location;
        this.block = location.getBlock();
        this.tier = tier;
        this.prizes = prizes;
        this.cancelled = false;
    }
    
    /**
     * Get the player that opened an envoy crate.
     *
     * @return The player that opened the crate.
     */
    public Player getPlayer() {
        return this.player;
    }
    
    /**
     * Get the location that the crate was located at.
     *
     * @return The location the crate was located at.
     */
    public Location getLocation() {
        return this.location;
    }
    
    /**
     * Get the block that was claimed.
     *
     * @return The block that was claimed.
     */
    public Block getBlock() {
        return this.block;
    }
    
    /**
     * Get the tier of the crate that was opened.
     */
    public Tier getTier() {
        return this.tier;
    }
    
    /**
     * Get the prize that was won.
     */
    public List<Prize> getPrizes() {
        return this.prizes;
    }
    
    /**
     * Set the prize that was won.
     *
     * @param prizes The new prize that was won.
     */
    public void setPrize(List<Prize> prizes) {
        this.prizes = prizes;
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }
}