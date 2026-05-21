package com.badbones69.crazyenvoys.commands.types.admin.migrator.types;

import com.badbones69.crazyenvoys.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenvoys.commands.types.admin.migrator.interfaces.EnvoyMigrator;
import com.badbones69.crazyenvoys.config.types.ConfigKeys;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MojangMappedMigrator extends EnvoyMigrator {

    public MojangMappedMigrator(@NonNull final CommandSender sender) {
        super(sender, MigrationType.MOJANG_MAPPED_ALL);
    }

    @Override
    public void run() {
        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        try {
            this.config.setProperty(ConfigKeys.envoys_flare_item_type, this.config.getProperty(ConfigKeys.envoys_flare_item_type).toLowerCase());
            this.config.setProperty(ConfigKeys.envoy_falling_block_type, this.config.getProperty(ConfigKeys.envoy_falling_block_type).toLowerCase());

            success.add("<green>⤷ config.yml");

            this.config.save();
            this.config.reload();
        } catch (Exception exception) {
            failed.add("<red>⤷ config.yml");
        }

        for (final Path path : this.fusion.getFilesByPath(this.tierPath, ".yml")) {
            final Optional<PaperCustomFile> origin = this.fileManager.getPaperFile(path);

            final String fileName = path.getFileName().toString();

            if (origin.isEmpty()) {
                failed.add("<red>⤷ " + fileName);

                continue;
            }

            final PaperCustomFile customFile = origin.get();

            final YamlConfiguration configuration = customFile.getConfiguration();

            final ConfigurationSection section = configuration.getConfigurationSection("Settings");

            boolean isSave = false;

            if (section != null) {
                if (section.contains("Placed-Block")) {
                    section.set("Placed-Block", section.getString("Placed-Block", "chest").toLowerCase());

                    isSave = true;
                }
            }

            final ConfigurationSection prizes = configuration.getConfigurationSection("Prizes");

            if (prizes != null) {
                for (final String value : prizes.getKeys(false)) {
                    final ConfigurationSection prizeSection = prizes.getConfigurationSection(value);

                    if (prizeSection == null) continue;

                    if (prizeSection.contains("Items")) {
                        final List<String> items = new ArrayList<>();

                        for (final String item : prizeSection.getStringList("Items")) {
                            if (item.isBlank()) continue;

                            String index = item;

                            final String[] splitter = item.split(", ");

                            for (final String option : splitter) {
                                String key = option.split(":")[0];
                                String pair = option.replace(key + ":", "").replace(key, "");

                                switch (key.toLowerCase()) {
                                    case "item" -> index = index.replace(pair, pair.toLowerCase());

                                    default -> {
                                        if (ItemUtils.getEnchantment(pair.toLowerCase()) != null) {
                                            index = index.replace(pair, pair.toLowerCase());
                                        }
                                    }
                                }
                            }

                            items.add(index);
                        }

                        set(prizeSection, "Items", items);

                        isSave = true;
                    }
                }
            }

            try {
                if (isSave) {
                    customFile.save();
                }

                success.add("<green>⤷ " + customFile.getFileName());
            } catch (Exception exception) {
                failed.add("<red>⤷ " + fileName);
            }
        }

        final int convertedCrates = success.size();
        final int failedCrates = failed.size();

        final List<String> files = new ArrayList<>(failedCrates + convertedCrates);

        files.addAll(failed);
        files.addAll(success);

        sendMessage(files, convertedCrates, failedCrates);

        this.fileManager.refresh(false);

        this.crazyManager.reload(false);
    }

    @Override
    public <T> void set(@NonNull final ConfigurationSection section, @NonNull final String path, @NonNull final T value) {
        section.set(path, value);
    }
}