package me.badbones69.crazyenvoy.api.events;

import me.badbones69.crazyenvoy.api.objects.Prize;
import me.badbones69.crazyenvoy.api.objects.Tier;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * Event is called when a player opens a envoy crate.
 */
public class OpenEnvoyEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Location location;
    private Block block;
    private Player player;
    private Tier tier;
    private List<Prize> prizes;
    
    public OpenEnvoyEvent(Player player, Block block, Tier tier, List<Prize> prizes) {
        this(player, block.getLocation(), tier, prizes);
    }
    
    public OpenEnvoyEvent(Player player, Location location, Tier tier, List<Prize> prizes) {
        this.player = player;
        this.location = location;
        this.block = location.getBlock();
        this.tier = tier;
        this.prizes = prizes;
        this.cancelled = false;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    /**
     * Get the player that opened an envoy crate.
     * @return The player that opened the crate.
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Get the location that the crate was located at.
     * @return The location the crate was located at.
     */
    public Location getLocation() {
        return location;
    }
    
    /**
     * Get the block that was claimed.
     * @return The block that was claimed.
     */
    public Block getBlock() {
        return block;
    }
    
    /**
     * Get the tier of the crate that was opened.
     */
    public Tier getTier() {
        return tier;
    }
    
    /**
     * Get the prize that was won.
     */
    public List<Prize> getPrizes() {
        return prizes;
    }
    
    /**
     * Set the prize that was won.
     * @param prizes The new prize that was won.
     */
    public void setPrize(List<Prize> prizes) {
        this.prizes = prizes;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
}