package me.badbones69.crazyenvoy.api.objects;

import me.badbones69.crazyenvoy.Methods;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Prize {
    
    private String prizeID;
    private int chance;
    private boolean dropItems;
    private List<String> messages;
    private List<String> commands;
    private List<ItemStack> items;
    private List<ItemBuilder> itemBuilders;
    
    public Prize(String prizeID) {
        this.prizeID = prizeID;
        this.chance = 100;
        this.dropItems = false;
        this.messages = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.items = new ArrayList<>();
        this.itemBuilders = new ArrayList<>();
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
    public int getChance() {
        return chance;
    }
    
    /**
     * Set the chance of the prize being picked out of 100.
     * @param chance The new chance of the prize out of 100.
     */
    public Prize setChance(int chance) {
        this.chance = chance;
        return this;
    }
    
    /**
     * Check if the items from Items: drop to the floor or go into the player's inventory.
     * @return True if drops to the ground and false if goes into their inventory.
     */
    public boolean getDropItems() {
        return dropItems;
    }
    
    /**
     * Make the items from the Items: option either drop on the ground or go into their inventory.
     * @param dropItems The option to drop items on the floor or into their inventory.
     */
    public Prize setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
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
        for (String message : messages) {
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
    
    public List<ItemBuilder> getItemBuilders() {
        return itemBuilders;
    }
    
    public Prize setItemBuilders(List<ItemBuilder> itemBuilders) {
        this.itemBuilders = itemBuilders;
        itemBuilders.forEach(itemBuilder -> items.add(itemBuilder.build()));
        return this;
    }
    
}