package com.badbones69.crazyenvoys.commands.types.admin.migrator.types;

import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenvoys.commands.types.admin.migrator.interfaces.EnvoyMigrator;
import com.badbones69.crazyenvoys.config.beans.GuiProperty;
import com.badbones69.crazyenvoys.config.types.ConfigKeys;
import com.ryderbelserion.fusion.kyori.utils.AdvUtils;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LegacyColorMigrator extends EnvoyMigrator {

    public LegacyColorMigrator(@NonNull final CommandSender sender) {
        super(sender, MigrationType.LEGACY_COLOR_ALL);
    }

    @Override
    public void run() {
        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        try {
            this.config.setProperty(ConfigKeys.command_prefix, AdvUtils.convert(this.config.getProperty(ConfigKeys.command_prefix), true));
            this.config.setProperty(ConfigKeys.console_prefix, AdvUtils.convert(this.config.getProperty(ConfigKeys.console_prefix), true));

            this.config.setProperty(ConfigKeys.envoys_flare_item_name, AdvUtils.convert(this.config.getProperty(ConfigKeys.envoys_flare_item_name), true));
            this.config.setProperty(ConfigKeys.envoys_flare_item_lore, AdvUtils.convert(this.config.getProperty(ConfigKeys.envoys_flare_item_lore), true));

            this.config.setProperty(ConfigKeys.envoys_grace_period_unlocked, AdvUtils.convert(this.config.getProperty(ConfigKeys.envoys_grace_period_unlocked), true));

            final GuiProperty property = this.config.getProperty(ConfigKeys.envoy_menu);

            property.setTitle(AdvUtils.convert(property.getTitle(), true));

            this.config.setProperty(ConfigKeys.envoy_menu, property);

            success.add("<green>⤷ config.yml");

            this.config.save();
            this.config.reload();
        } catch (Exception exception) {
            failed.add("<red>⤷ config.yml");
        }

        try {
            for (final Messages message : Messages.values()) {
                message.migrate();
            }

            success.add("<green>⤷ messages.yml");

            this.messages.save();
            this.messages.reload();
        } catch (Exception exception) {
            failed.add("<red>⤷ messages.yml");
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
                if (section.contains("Prize-Message")) {
                    set(section, "Prize-Message", AdvUtils.convert(section.getStringList("Prize-Message"), true));

                    isSave = true;
                }

                if (section.contains("Hologram")) {
                    set(section, "Hologram", AdvUtils.convert(section.getStringList("Hologram"), true));

                    isSave = true;
                }
            }

            final ConfigurationSection prizes = configuration.getConfigurationSection("Prizes");

            if (prizes != null) {
                for (final String value : prizes.getKeys(false)) {
                    final ConfigurationSection prizeSection = prizes.getConfigurationSection(value);

                    if (prizeSection == null) continue;

                    if (prizeSection.contains("DisplayName")) {
                        set(prizeSection, "DisplayName", AdvUtils.convert(prizeSection.getString("DisplayName", ""), true));

                        isSave = true;
                    }

                    if (prizeSection.contains("Messages")) {
                        set(prizeSection, "Messages", AdvUtils.convert(prizeSection.getStringList("Messages"), true));

                        isSave = true;
                    }

                    if (prizeSection.contains("Items")) {
                        set(prizeSection, "Items", AdvUtils.convert(prizeSection.getStringList("Items"), true));

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