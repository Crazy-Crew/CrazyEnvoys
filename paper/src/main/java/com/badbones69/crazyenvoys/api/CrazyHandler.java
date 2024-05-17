package com.badbones69.crazyenvoys.api;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.objects.misc.v2.Reward;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.ryderbelserion.vital.common.configuration.YamlFile;
import com.ryderbelserion.vital.common.configuration.YamlManager;
import com.ryderbelserion.vital.common.configuration.objects.CustomFile;
import com.ryderbelserion.vital.common.util.FileUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.ConfigurationSection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrazyHandler {

    private final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final YamlManager manager = ConfigManager.getYamlManager();

    private final Map<String, ArrayList<Reward>> rewards = new HashMap<>();

    /**
     * Loads rewards into memory.
     */
    public void apply() {
        List<String> files = getRewardFiles();

        files.forEach(bundleName -> {
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
    }

    /**
     * Returns a list of rewards.
     *
     * @param bundleName the name of the bundle
     * @return a list of rewards
     */
    public List<Reward> getRewards(String bundleName) {
        return this.rewards.getOrDefault(bundleName, new ArrayList<>());
    }

    /**
     * Adds a {@link Reward} to the {@link java.util.HashMap}.
     *
     * @param bundleName the name of the bundle
     * @param key the configuration section
     */
    public void addReward(String bundleName, ConfigurationSection key) {
        Reward reward = new Reward(
                key.getName(),
                key.getString("displayname", "N/A"),
                key.getInt("chance", 10),
                key.getBoolean("drop-items", false), key.getStringList("messages"), key.getStringList("items"));

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
    public void removeReward(String bundleName) {
        this.rewards.remove(bundleName);
    }

    /**
     * @return a list of bundles from the rewards directory
     */
    public @NotNull final List<String> getRewardFiles() {
        return FileUtil.getFiles(this.plugin.getDataFolder().toPath().resolve("rewards"), ".yml", true);
    }
}