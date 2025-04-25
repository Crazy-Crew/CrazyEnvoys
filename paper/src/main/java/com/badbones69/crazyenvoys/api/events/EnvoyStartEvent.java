package com.badbones69.crazyenvoys.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event is called when the envoy is started for any reason.
 */
public class EnvoyStartEvent extends Event implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player player;
    private final EnvoyStartReason reason;
    
    public EnvoyStartEvent(EnvoyStartReason reason) {
        this.reason = reason;
        this.cancelled = false;
    }
    
    public EnvoyStartEvent(EnvoyStartReason reason, Player player) {
        this.reason = reason;
        this.player = player;
        this.cancelled = false;
    }
    
    /**
     * Get the reason the envoy event started.
     *
     * @return The EnvoyStartReason the event started.
     */
    public EnvoyStartReason getReason() {
        return this.reason;
    }
    
    /**
     * Get the player that started the event.
     *
     * @return The player that started it. This can be null if it auto started.
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

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public enum EnvoyStartReason {
        
        /**
         * Started by a player running the envoy start command.
         */
        FORCED_START_PLAYER("Forced-Start-Player"),
        /**
         * Started by the console running the envoy start command.
         */
        FORCED_START_CONSOLE("Forced-Start-Console"),
        /**
         * Started by the auto timer that starts the envoy after the cooldown ends.
         */
        AUTO_TIMER("Auto-Timer"),
        /**
         * Started because it is the time specified in the config for the envoy to start.
         */
        SPECIFIED_TIME("Specified-Time"),
        /**
         * Started because a player used a flare to start the event.
         */
        FLARE("Flare"),
        /**
         * Started by an outside source that wanted the envoy event to start.
         */
        CUSTOM("Custom");
        
        private final String name;
        
        EnvoyStartReason(String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
}