package com.badbones69.crazyenvoys.api.builders;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.badbones69.crazyenvoys.util.MsgUtils;
import com.ryderbelserion.vital.paper.api.enums.Support;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class InventoryBuilder implements InventoryHolder, Listener {

    protected @NotNull final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    protected @NotNull final Server server = this.plugin.getServer();

    private Inventory inventory;
    private Player player;
    private String title;
    private Tier tier;
    private int size;
    private int page;

    public InventoryBuilder(@NotNull final Player player, @NotNull final String title, final int size) {
        this.player = player;
        this.title = title;
        this.size = size;

        String inventoryTitle = Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), this.title) : this.title;

        this.inventory = this.server.createInventory(this, this.size, MsgUtils.color(inventoryTitle));
    }

    public InventoryBuilder(@NotNull final Player player, @NotNull final String title, final int size, @NotNull final Tier tier) {
        this(player, title, size);

        this.tier = tier;
    }

    public InventoryBuilder() {}

    public abstract InventoryBuilder build();

    public abstract void run(InventoryClickEvent event);

    public abstract void run(InventoryDragEvent event);

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        run(event);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        run(event);
    }

    public void size(final int size) {
        this.size = size;
    }

    public final int getSize() {
        return this.size;
    }

    public void setPage(final int page) {
        this.page = page;
    }

    public final int getPage() {
        return this.page;
    }

    public @NotNull final Tier getTier() {
        return this.tier;
    }

    public void title(@NotNull final String title) {
        this.title = title;
    }

    public final boolean contains(@NotNull final String message) {
        return this.title.contains(message);
    }

    public @NotNull final Player getPlayer() {
        return this.player;
    }

    public @NotNull final InventoryView getView() {
        return getPlayer().getOpenInventory();
    }

    @Override
    public @NotNull final Inventory getInventory() {
        return this.inventory;
    }
}