package com.badbones69.crazyenvoys.api.events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event is called when a player places a new crate spawning location in editor mode.
 */
public class NewDropLocationEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Location location;
    private final Block block;
    private boolean cancelled;
    
    public NewDropLocationEvent(Player player, Block block) {
        this(player, block.getLocation());
    }
    
    public NewDropLocationEvent(Player player, Location location) {
        this.player = player;
        this.location = location;
        this.block = location.getBlock();
        this.cancelled = false;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public Block getBlock() {
        return block;
    }
    
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

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}