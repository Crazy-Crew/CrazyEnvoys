package com.badbones69.crazyenvoys.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event is called when a player uses a flare to call an envoy event.
 */
public class FlareUseEvent extends Event implements Cancellable {
    
    private final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private final Player player;
    
    public FlareUseEvent(Player player) {
        this.player = player;
        this.cancelled = false;
    }
    
    /**
     * Get the player that called the envoy event.
     *
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