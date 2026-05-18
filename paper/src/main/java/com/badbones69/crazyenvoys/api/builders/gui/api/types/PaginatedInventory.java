package com.badbones69.crazyenvoys.api.builders.gui.api.types;

import com.badbones69.crazyenvoys.api.builders.gui.api.AbstractGui;
import com.badbones69.crazyenvoys.api.objects.misc.Tier;
import com.ryderbelserion.fusion.paper.builders.gui.enums.GuiState;
import com.ryderbelserion.fusion.paper.builders.gui.types.paginated.PaginatedGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PaginatedInventory extends AbstractGui {

    protected final PaginatedGui gui;

    public PaginatedInventory(@NotNull final Player player, @NotNull final Tier tier, @NotNull final String title, final int size) {
        super(player, tier, title, size);

        this.gui = PaginatedGui.gui(this.plugin, title, size).addState(GuiState.block_all_interactions);
    }
}