package me.badbones69.crazyenvoy.api.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event is called when a player places a new crate spawning location in editor mode.
 */
public class NewDropLocationEvent extends Event implements Cancellable {
	
	private Player player;
	private Location location;
	private Boolean cancelled;
	
	public NewDropLocationEvent(Player player, Location location) {
		this.player = player;
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
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
	
	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}