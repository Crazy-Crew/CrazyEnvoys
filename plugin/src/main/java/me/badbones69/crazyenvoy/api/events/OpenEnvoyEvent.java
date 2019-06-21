package me.badbones69.crazyenvoy.api.events;

import me.badbones69.crazyenvoy.api.objects.Prize;
import me.badbones69.crazyenvoy.api.objects.Tier;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event is called when a player opens a envoy crate.
 */
public class OpenEnvoyEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private Boolean cancelled;
	private Location location;
	private Player player;
	private Tier tier;
	private Prize prize;
	
	public OpenEnvoyEvent(Player player, Location location, Tier tier, Prize prize) {
		this.player = player;
		this.location = location;
		this.tier = tier;
		this.prize = prize;
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
	 * Get the tier of the crate that was opened.
	 */
	public Tier getTier() {
		return tier;
	}
	/**
	 * Get the prize that was won.
	 */
	public Prize getPrize() {
		return prize;
	}
	/**
	 * Set the prize that was won.
	 * @param prize The new prize that was won.
	 */
	public void setPrize(Prize prize) {
		this.prize = prize;
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