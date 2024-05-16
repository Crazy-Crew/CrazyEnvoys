package com.badbones69.crazyenvoys.platform.util;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.ryderbelserion.vital.common.configuration.YamlFile;
import com.ryderbelserion.vital.common.util.StringUtil;
import com.ryderbelserion.vital.util.DyeUtil;
import com.ryderbelserion.vital.util.ItemUtil;
import com.ryderbelserion.vital.util.builders.items.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.ryderbelserion.vital.util.ItemUtil.getEnchantment;

public class ItemUtils {

    private static @NotNull final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private static @NotNull final CrazyManager crateManager = plugin.getCrazyManager();

    /**
     * Removes an {@link ItemStack} from a {@link Player}'s inventory.
     *
     * @param item the {@link ItemStack}
     * @param player the {@link Player}
     */
    public static void removeItem(@NotNull final ItemStack item, @NotNull final Player player) {
        try {
            if (item.getAmount() <= 1) {
                player.getInventory().removeItem(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        } catch (Exception ignored) {}
    }

    /**
     * Converts an {@link ItemStack} to an {@link ItemBuilder}.
     *
     * @param player {@link Player}
     * @param itemStack the {@link ItemStack}
     * @return the {@link ItemBuilder}
     */
    public static ItemBuilder convertItemStack(Player player, ItemStack itemStack) {
        ItemBuilder itemBuilder = new ItemBuilder(itemStack.getType(), itemStack.getAmount());

        if (player != null) {
            itemBuilder.setPlayer(player);
        }

        return itemBuilder;
    }

    /**
     * Converts an {@link ItemStack} without a {@link Player}.
     *
     * @param itemStack the {@link ItemStack}
     * @return the {@link ItemBuilder}
     */
    public static ItemBuilder convertItemStack(ItemStack itemStack) {
        return convertItemStack(null, itemStack);
    }

    /**
     * Converts a {@link List<String>} to a list of {@link ItemBuilder}.
     *
     * @param itemStrings the {@link List<String>}
     * @return list of {@link ItemBuilder}
     */
    public static List<ItemBuilder> convertStringList(List<String> itemStrings) {
        return convertStringList(itemStrings, null);
    }

    /**
     * Converts a {@link List<String>} to a list of {@link ItemBuilder}.
     *
     * @param itemStrings the {@link List<String>}
     * @param section the section in the {@link YamlFile}
     * @return list of {@link ItemBuilder}
     */
    public static List<ItemBuilder> convertStringList(List<String> itemStrings, String section) {
        return itemStrings.stream().map(itemString -> convertString(itemString, section)).collect(Collectors.toList());
    }

    /**
     * Converts a {@link String} to an {@link ItemBuilder}.
     *
     * @param itemString the {@link String} you wish to convert
     * @return the {@link ItemBuilder}
     */
    public static ItemBuilder convertString(String itemString) {
        return convertString(itemString, null);
    }

    /**
     * Converts a {@link List<String>} to a list of {@link ItemBuilder}.
     *
     * @param itemString the {@link String} you wish to convert
     * @param section the section in the {@link YamlFile}
     * @return the {@link ItemBuilder}
     */
    public static ItemBuilder convertString(String itemString, String section) {
        ItemBuilder itemBuilder = new ItemBuilder();

        try {
            for (String optionString : itemString.split(", ")) {
                String option = optionString.split(":")[0];
                String value = optionString.replace(option + ":", "").replace(option, "");

                switch (option.toLowerCase()) {
                    case "item" -> itemBuilder.withType(value);
                    case "name" -> itemBuilder.setDisplayName(value);
                    case "amount" -> {
                        final Optional<Number> amount = StringUtil.tryParseInt(value);
                        itemBuilder.setAmount(amount.map(Number::intValue).orElse(1));
                    }
                    case "damage" -> {
                        final Optional<Number> amount = StringUtil.tryParseInt(value);
                        itemBuilder.setDamage(amount.map(Number::intValue).orElse(1));
                    }
                    case "lore" -> itemBuilder.setDisplayLore(List.of(value.split(",")));
                    case "player" -> itemBuilder.setPlayer(value);
                    case "unbreakable-item" -> itemBuilder.setUnbreakable(value.isEmpty() || value.equalsIgnoreCase("true"));
                    case "trim-pattern" -> itemBuilder.applyTrimPattern(value);
                    case "trim-material" -> itemBuilder.applyTrimMaterial(value);
                    default -> {
                        if (getEnchantment(option) != null) {
                            final Optional<Number> amount = StringUtil.tryParseInt(value);

                            itemBuilder.addEnchantment(option, amount.map(Number::intValue).orElse(1), true);

                            break;
                        }

                        for (ItemFlag itemFlag : ItemFlag.values()) {
                            if (itemFlag.name().equalsIgnoreCase(option)) {
                                itemBuilder.addItemFlag(itemFlag);

                                break;
                            }
                        }

                        try {
                            DyeColor color = DyeUtil.getDyeColor(value);

                            if (color != null) {
                                PatternType patternType = ItemUtil.getPatternType(option);

                                if (patternType != null) {
                                    itemBuilder.addPattern(patternType, color);
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception exception) {
            itemBuilder.withType(Material.RED_TERRACOTTA).setDisplayName("<red>Error found!, Prize Name: " + section);

            plugin.getLogger().log(Level.WARNING, "An error has occurred with the item builder: ", exception);
        }

        return itemBuilder;
    }
}