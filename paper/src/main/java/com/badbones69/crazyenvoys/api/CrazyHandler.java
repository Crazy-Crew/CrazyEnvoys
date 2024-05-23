package com.badbones69.crazyenvoys.api;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.enums.PersistentKeys;
import com.badbones69.crazyenvoys.api.objects.misc.v2.Tier;
import com.badbones69.crazyenvoys.api.objects.misc.v2.records.RewardSettings;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.badbones69.crazyenvoys.config.impl.ConfigKeys;
import com.ryderbelserion.vital.core.config.YamlFile;
import com.ryderbelserion.vital.core.config.YamlManager;
import com.ryderbelserion.vital.core.config.objects.CustomFile;
import com.ryderbelserion.vital.core.util.FileUtil;
import com.ryderbelserion.vital.paper.builders.items.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.ConfigurationSection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrazyHandler {

    private @NotNull final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);
    private @NotNull final YamlManager manager = ConfigManager.getYamlManager();
    private @NotNull final SettingsManager config = ConfigManager.getConfig();

    private final Map<String, ArrayList<RewardSettings>> rewards = new HashMap<>();
    private final Map<String, Tier> tiers = new HashMap<>();

    /**
     * Loads rewards/tiers into memory.
     */
    public void load() {
        // Clears the rewards
        this.rewards.clear();

        // Re-populate the map
        List<String> rewards = getRewardFiles();

        rewards.forEach(bundleName -> {
            if (bundleName.isEmpty() || bundleName.isBlank()) return;

            CustomFile customFile = this.manager.getCustomFile(bundleName);

            if (customFile == null) return;

            YamlFile file = customFile.getYamlFile();

            ConfigurationSection section = file.getConfigurationSection("rewards");

            if (section == null || section.isEmpty()) return;

            section.getKeys(false).forEach(order -> {
                ConfigurationSection reward = section.getConfigurationSection(order);
                addReward(bundleName, reward);
            });
        });

        // Clears the tiers
        this.tiers.clear();

        // Re-populate the arraylist
        List<String> tiers = getTierFiles();

        tiers.forEach(tierName -> {
            if (tierName.isEmpty() || tierName.isBlank()) return;

            CustomFile customFile = this.manager.getCustomFile(tierName);

            if (customFile == null) return;

            YamlFile file = customFile.getYamlFile();

            ConfigurationSection section = file.getConfigurationSection("envoy");

            Tier tier = new Tier(tierName, section);

            this.tiers.put(tierName, tier);
        });

        // Create the signal flare item
        buildSignalFlare();
    }

    public void reload() {
        this.plugin.getPaperServer().reload();

        load();
    }

    /**
     * Gets a {@link Tier} from the {@link Map}
     *
     * @param tierName the name of the {@link Tier}
     * @return the {@link Tier}
     */
    public @Nullable final Tier getTier(@NotNull final String tierName) {
        return getTiers().getOrDefault(tierName, null);
    }

    /**
     * @return a map of {@link Tier}
     */
    public final Map<String, Tier> getTiers() {
        return this.tiers;
    }

    /**
     * Returns a list of {@link RewardSettings}
     *
     * @param bundleName the name of the bundle
     * @return a list of {@link RewardSettings}
     */
    public final List<RewardSettings> getRewards(@NotNull final String bundleName) {
        return this.rewards.getOrDefault(bundleName, new ArrayList<>());
    }

    /**
     * Adds a {@link RewardSettings} to the {@link java.util.HashMap}.
     *
     * @param bundleName the name of the bundle
     * @param key the configuration section
     */
    public void addReward(@NotNull final String bundleName, @NotNull final ConfigurationSection key) {
        RewardSettings reward = new RewardSettings(
                key.getName(),
                key.getString("displayname", "N/A"),
                key.getInt("chance", 10),
                key.getBoolean("drop-items", false), key.getStringList("messages"), key.getStringList("commands"), key.getStringList("items"));

        // If it contains the bundle name, just simply add
        if (this.rewards.containsKey(bundleName)) {
            this.rewards.get(bundleName).add(reward);

            return;
        }

        // Add the initial reward.
        this.rewards.put(bundleName, new ArrayList<>() {{
            add(reward);
        }});
    }

    /**
     * Removes a bundle from the {@link java.util.HashMap}.
     *
     * @param bundleName the name of the bundle
     */
    public void removeReward(@NotNull final String bundleName) {
        this.rewards.remove(bundleName);
    }

    private ItemBuilder signalFlare;

    public void buildSignalFlare() {
        this.signalFlare = new ItemBuilder().withType(this.config.getProperty(ConfigKeys.envoys_flare_item_type))
                .setDisplayName(this.config.getProperty(ConfigKeys.envoys_flare_item_name))
                .setDisplayLore(this.config.getProperty(ConfigKeys.envoys_flare_item_lore))
                .setPersistentString(PersistentKeys.envoy_flare.getNamespacedKey(), "1");
    }

    /**
     * Gives a signal flare to a {@link Player}.
     *
     * @param player the {@link Player}
     */
    public void give(@NotNull final Player player) {
        give(player, 1);
    }

    /**
     * Gives a signal flare to a {@link Player}.
     *
     * @param player the {@link Player}
     * @param amount the amount to give
     */
    public void give(@NotNull final Player player, final int amount) {
        PlayerInventory inventory = player.getInventory();

        if (!inventory.isEmpty()) {
            player.getWorld().dropItem(player.getLocation(), build(player, amount));

            return;
        }

        inventory.setItem(inventory.firstEmpty(), build(player, amount));
    }

    /**
     * Builds a signal flare to give it to a player.
     *
     * @param player the {@link Player}
     * @return the {@link ItemStack}
     */
    public ItemStack build(@NotNull final Player player) {
        return build(player, 1);
    }

    /**
     * Builds a signal flare to give it to a player.
     *
     * @param player the {@link Player}
     * @param amount the amount of signal flares to give
     * @return the {@link ItemStack}
     */
    public ItemStack build(@NotNull final Player player, final int amount) {
        return this.signalFlare.setPlayer(player).setAmount(amount).getStack();
    }

    /**
     * Checks if an {@link ItemStack} is a flare!
     *
     * @param itemStack the {@link ItemStack} to check
     * @return true or false
     */
    public boolean isFlare(@NotNull final ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return false;

        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();

        return container.has(PersistentKeys.envoy_flare.getNamespacedKey());
    }

    /**
     * Take a flare out of the {@link Player} {@link PlayerInventory}.
     *
     * @param player the {@link Player}
     */
    public void takeFlare(@NotNull final Player player) {
        PlayerInventory inventory = player.getInventory();

        // We don't need to remove anything if inventory is empty.
        if (inventory.isEmpty()) return;

        // Remove item from any slot
        inventory.removeItemAnySlot(build(player));
    }

    /**
     * @return a list of bundles from the rewards directory
     */
    public @NotNull final List<String> getTierFiles() {
        return FileUtil.getFiles(this.plugin.getDataFolder().toPath().resolve("tiers"), ".yml", true);
    }

    /**
     * @return a list of bundles from the rewards directory
     */
    public @NotNull final List<String> getRewardFiles() {
        return FileUtil.getFiles(this.plugin.getDataFolder().toPath().resolve("rewards"), ".yml", true);
    }
}