package com.badbones69.crazyenvoys.api;

import com.badbones69.crazyenvoys.EnvoysPlugin;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.audience.Audience;
import org.jspecify.annotations.NonNull;

public class PaperEnvoysPlugin extends EnvoysPlugin<Audience, FusionPaper> {

    public PaperEnvoysPlugin(@NonNull final FusionPaper fusion) {
        super(fusion);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public @NonNull FusionPaper getFusion() {
        return this.fusion;
    }
}