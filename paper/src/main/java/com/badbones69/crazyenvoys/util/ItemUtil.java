package com.badbones69.crazyenvoys.util;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.items.ItemBuilder;
import com.ryderbelserion.fusion.paper.builders.items.types.PatternBuilder;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemUtil {

    private static final CrazyEnvoys plugin = CrazyEnvoys.get();

    private static final FusionPaper fusion = plugin.getFusion();

    public static void addGlow(@NotNull final ItemBuilder builder, final String value) {
        switch (value.toLowerCase()) {
            case "add_glow", "true" -> builder.addEnchantGlint();
            case "remove_glow", "false" -> builder.removeEnchantGlint();
            default -> {}
        }
    }

    public static List<ItemBuilder> convertStringList(@NotNull final List<String> itemStrings, @NotNull final String section) {
        return itemStrings.stream().map(itemString -> convertString(itemString, section)).collect(Collectors.toList());
    }

    public static ItemBuilder convertString(@NotNull final String itemString, @NotNull final String section) {
        ItemBuilder itemBuilder = ItemBuilder.from(ItemType.STONE);

        try {
            for (String optionString : itemString.split(", ")) {
                String option = optionString.split(":")[0];
                String value = optionString.replace(option + ":", "").replace(option, "");

                switch (option.toLowerCase()) {
                    case "item" -> itemBuilder = ItemBuilder.from(value.toLowerCase());
                    case "data" -> itemBuilder.withBase64(value);
                    case "name" -> itemBuilder.withDisplayName(value);
                    case "glowing" -> ItemUtil.addGlow(itemBuilder, value);
                    case "amount" -> {
                        final Optional<Number> amount = StringUtils.tryParseInt(value);
                        itemBuilder.setAmount(amount.map(Number::intValue).orElse(1));
                    }
                    case "damage" -> {
                        final Optional<Number> amount = StringUtils.tryParseInt(value);
                        itemBuilder.setItemDamage(amount.map(Number::intValue).orElse(0));
                    }
                    case "lore" -> itemBuilder.withDisplayLore(List.of(value.split(",")));
                    case "player" -> itemBuilder.asSkullBuilder().withName(value);
                    case "skull" -> itemBuilder.withSkull(value);
                    case "custom-model-data" -> itemBuilder.asCustomBuilder().setCustomModelData(value).build();
                    case "unbreakable-item" -> itemBuilder.setUnbreakable(value.isEmpty() || value.equalsIgnoreCase("true"));
                    case "hide-tool-tip" -> {
                        final boolean isEnabled = StringUtils.tryParseBoolean(value).orElse(false);

                        if (isEnabled) {
                            itemBuilder.hideToolTip();
                        }
                    }
                    case "components" -> {
                        final List<String> components = Arrays.stream(value.split(",")).toList();

                        if (!components.isEmpty()) {
                            itemBuilder.hideComponents(components);
                        }
                    }
                    case "trim" -> { // trim-material, and trim-pattern are now combined i.e. trim:sentry;quartz
                        final String[] index = value.split(";");

                        itemBuilder.setTrim(index[0], index[1]);
                    }
                    case "rgb", "color" -> itemBuilder.setColor(value);
                    default -> {
                        if (com.ryderbelserion.fusion.paper.utils.ItemUtils.getEnchantment(option.toLowerCase()) != null) {
                            final Optional<Number> amount = StringUtils.tryParseInt(value);

                            itemBuilder.addEnchantment(option.toLowerCase(), amount.map(Number::intValue).orElse(1));

                            break;
                        }

                        try {
                            final PatternBuilder pattern = itemBuilder.asPatternBuilder();

                            pattern.addPattern(option, value);
                        } catch (final Exception ignored) {}
                    }
                }
            }
        } catch (final Exception exception) {
            itemBuilder = ItemBuilder.from(ItemType.RED_TERRACOTTA).withDisplayName("<red>Error found with Prize: %s".formatted(section));

            fusion.log(Level.ERROR, "An error has occurred with the prize %s".formatted(section), exception);
        }

        return itemBuilder;
    }

}