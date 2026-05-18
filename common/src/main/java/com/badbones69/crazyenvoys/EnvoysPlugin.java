package com.badbones69.crazyenvoys;

import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jspecify.annotations.NonNull;

public abstract class EnvoysPlugin<S, K extends FusionKyori<S>> {

    protected final K fusion;

    public EnvoysPlugin(@NonNull final K fusion) {
        this.fusion = fusion;
    }

    public @NonNull K getFusion() {
        return this.fusion;
    }

    public void init() {
        this.fusion.init();
    }
}