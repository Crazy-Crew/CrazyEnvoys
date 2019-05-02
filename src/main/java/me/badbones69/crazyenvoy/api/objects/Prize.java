package me.badbones69.crazyenvoy.api.objects;

import me.badbones69.crazyenvoy.Methods;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Prize {
	
	private String prizeID;
	private Integer chance;
	private List<String> messages;
	private List<String> commands;
	private List<ItemStack> items;
	
	public Prize(String prizeID) {
		this.prizeID = prizeID;
		this.chance = 100;
		this.messages = new ArrayList<>();
		this.commands = new ArrayList<>();
		this.items = new ArrayList<>();
	}
	
	/**
	 * Get the prizeID of the prize.
	 * @return The prizeID of the prize.
	 */
	public String getPrizeID() {
		return prizeID;
	}
	
	/**
	 * Get the chance of the prize being won.
	 * @return The chance as an integer.
	 */
	public Integer getChance() {
		return chance;
	}
	
	/**
	 * Set the chance of the prize being picked out of 100.
	 * @param chance The new chance of the prize out of 100.
	 */
	public Prize setChance(Integer chance) {
		this.chance = chance;
		return this;
	}
	
	/**
	 * Get the messages that are sent to the player when winning.
	 * @return The messages that are sent to the player.
	 */
	public List<String> getMessages() {
		return messages;
	}
	
	/**
	 * Set the messages that the player gets.
	 * @param messages The new messages the player gets. This will auto color code the messages.
	 */
	public Prize setMessages(List<String> messages) {
		this.messages.clear();
		for(String message : messages) {
			this.messages.add(Methods.color(message));
		}
		return this;
	}
	
	/**
	 * Get the list of commands the prize runs.
	 */
	public List<String> getCommands() {
		return commands;
	}
	
	/**
	 * Set the list of commands the prize will run.
	 * @param commands List of commands to be ran.
	 */
	public Prize setCommands(List<String> commands) {
		this.commands = commands;
		return this;
	}
	
	/**
	 * Get the items that the prize will give.
	 * @return The items that are won in the prize.
	 */
	public List<ItemStack> getItems() {
		return items;
	}
	
	/**
	 * Set the items that can be found in this prize.
	 * @param items The new items that are won in the prize.
	 */
	public Prize setItems(List<ItemStack> items) {
		this.items = items;
		return this;
	}
	
}