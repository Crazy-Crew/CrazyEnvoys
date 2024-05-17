package com.badbones69.crazyenvoys.api.objects.misc.v2.records;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public record HologramSettings(boolean enabled, double height, int range, @NotNull String color, @NotNull List<String> messages) {}