package com.badbones69.crazyenvoys.api.objects.misc;

import com.ryderbelserion.fusion.paper.builders.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class Prize {
    
    private final String prizeID;
    private int chance;
    private boolean dropItems;
    private final List<String> messages;
    private List<String> commands;
    private List<ItemBuilder> builders;
    private String displayName;
    
    public Prize(String prizeID) {
        this.prizeID = prizeID;
        this.chance = 100;
        this.dropItems = false;
        this.displayName = "";
        this.messages = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.builders = new ArrayList<>();
    }

    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Get the prizeID of the prize.
     *
     * @return The prizeID of the prize.
     */
    public String getPrizeID() {
        return this.prizeID;
    }
    
    /**
     * Get the chance of the prize being won.
     *
     * @return The chance as an integer.
     */
    public int getChance() {
        return this.chance;
    }
    
    /**
     * Set the chance of the prize being picked out of 100.
     *
     * @param chance The new chance of the prize out of 100.
     */
    public Prize setChance(int chance) {
        this.chance = chance;

        return this;
    }

    public Prize setDisplayName(String displayName) {
        this.displayName = displayName;

        return this;
    }
    
    /**
     * Check if the items from Items: drop to the floor or go into the player's inventory.
     *
     * @return True if drops to the ground and false if it goes into their inventory.
     */
    public boolean getDropItems() {
        return this.dropItems;
    }
    
    /**
     * Make the items from the Items: option either drop on the ground or go into their inventory.
     *
     * @param dropItems The option to drop items on the floor or into their inventory.
     */
    public Prize setDropItems(boolean dropItems) {
        this.dropItems = dropItems;

        return this;
    }
    
    /**
     * Get the messages that are sent to the player when winning.
     *
     * @return The messages that are sent to the player.
     */
    public List<String> getMessages() {
        return this.messages;
    }
    
    /**
     * Set the messages that the player gets.
     *
     * @param messages The new messages the player gets. This will auto color code the messages.
     */
    public Prize setMessages(@NotNull final List<String> messages) {
        this.messages.clear();

        this.messages.addAll(messages);

        return this;
    }
    
    /**
     * Get the list of commands the prize runs.
     */
    public List<String> getCommands() {
        return this.commands;
    }
    
    /**
     * Set the list of commands the prize will run.
     *
     * @param commands List of commands to be run.
     */
    public Prize setCommands(@NotNull final List<String> commands) {
        this.commands = commands;

        return this;
    }
    
    /**
     * Get the items that the prize will give.
     *
     * @return The items that are won in the prize.
     */
    public List<ItemStack> getItems() {
        return this.builders.stream().map(ItemBuilder::asItemStack).toList();
    }

    public List<ItemBuilder> getItemBuilders() {
        return this.builders;
    }
    
    public Prize setItemBuilders(@NotNull final List<ItemBuilder> itemBuilders) {
        this.builders = itemBuilders;

        return this;
    }
}