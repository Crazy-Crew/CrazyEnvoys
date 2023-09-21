package com.badbones69.crazyenvoys.paper.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event is called when a player uses a flare to call an envoy event.
 */
public class FlareUseEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();

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
        return this.player;
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

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}