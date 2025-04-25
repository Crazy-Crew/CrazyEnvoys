package com.badbones69.crazyenvoys.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event is called when the envoy event has ended for any reason.
 */
public class EnvoyEndEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private final EnvoyEndReason reason;
    
    public EnvoyEndEvent(EnvoyEndReason reason) {
        this.reason = reason;
    }
    
    public EnvoyEndEvent(EnvoyEndReason reason, Player player) {
        this.reason = reason;
        this.player = player;
    }
    
    /**
     * Get the reason the envoy ended.
     *
     * @return The reason it has ended.
     */
    public EnvoyEndReason getReason() {
        return this.reason;
    }
    
    /**
     * Get the player that ended the event.
     *
     * @return The player that ended it. This can be null if it auto ended.
     */
    public Player getPlayer() {
        return this.player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public enum EnvoyEndReason {
        
        /**
         * Ended because a player forced the envoy event to end.
         */
        FORCED_END_PLAYER("Forced-End-Player"),
        /**
         * Ended because the console forced the envoy event to end.
         */
        FORCED_END_CONSOLE("Forced-End-Console"),
        /**
         * Ended because the envoy ran out of time.
         */
        OUT_OF_TIME("Out-Of-Time"),
        /**
         * Ended because all the crates were collected by the players.
         */
        ALL_CRATES_COLLECTED("All-Crates-Collected"),
        /**
         * Ends because no spawn locations for the crates were found.
         */
        NO_LOCATIONS_FOUND("No-Locations-Found"),
        /**
         * If the plugin reloads it ends the current envoy.
         */
        RELOAD("Reload"),
        /**
         * If the plugin is either unloaded, server reload, stop, or restarting.
         */
        SHUTDOWN("Shutdown"),
        /**
         * Ended because some outside source commanded it to end.
         */
        CUSTOM("Custom");
        
        private final String name;
        
        EnvoyEndReason(String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
}