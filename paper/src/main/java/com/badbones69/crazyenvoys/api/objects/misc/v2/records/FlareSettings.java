package com.badbones69.crazyenvoys.api.objects.misc.v2.records;

import java.util.List;

public record FlareSettings(boolean fireworkToggle, int time, List<String> colors) {}