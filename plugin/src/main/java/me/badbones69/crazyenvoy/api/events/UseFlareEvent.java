package me.badbones69.crazyenvoy.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event is called when a player uses a flare to call an envoy event.
 */
public class UseFlareEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player player;
    
    public UseFlareEvent(Player player) {
        this.player = player;
        this.cancelled = false;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    /**
     * Get the player that called the envoy event.
     * @return The player that used the flare.
     */
    public Player getPlayer() {
        return player;
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