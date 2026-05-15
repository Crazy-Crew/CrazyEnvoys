package com.badbones69.crazyenvoys.commands.types.admin.migrator.interfaces;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.enums.Messages;
import com.badbones69.crazyenvoys.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenvoys.config.ConfigManager;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.NonNull;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class EnvoyMigrator {

    protected final CrazyEnvoys plugin = CrazyEnvoys.get();

    protected final FusionPaper fusion = this.plugin.getFusion();

    protected final PaperFileManager fileManager = this.plugin.getFileManager();

    protected final CrazyManager crazyManager = this.plugin.getCrazyManager();

    protected final SettingsManager config = ConfigManager.getConfig();

    protected final SettingsManager messages = ConfigManager.getMessages();

    protected final Path dataPath = this.plugin.getDataPath();

    protected final Path tierPath = this.dataPath.resolve("tiers");

    protected final CommandSender sender;
    protected final MigrationType type;
    protected final long startTime;

    public EnvoyMigrator(@NonNull final CommandSender sender, @NonNull final MigrationType type) {
        this.startTime = System.nanoTime();
        this.sender = sender;
        this.type = type;
    }

    protected String envoyName;

    public EnvoyMigrator(@NonNull final CommandSender sender, @NonNull final MigrationType type, @NonNull final String envoyName) {
        this(sender, type);

        this.envoyName = envoyName;
    }

    public abstract <T> void set(@NonNull final ConfigurationSection section, @NonNull final String path, @NonNull final T value);

    public abstract void run();

    public void sendMessage(@NonNull final List<String> files, final int success, final int failed) {
        Messages.successfully_migrated.sendMessage(this.sender, Map.of(
                "{files}", files.size() > 1 ? StringUtils.toString(files) : files.isEmpty() ? "N/A" : files.getFirst(),
                "{succeeded_amount}", String.valueOf(success),
                "{failed_amount}", String.valueOf(failed),
                "{type}", type.getName(),
                "{time}", time()
        ));
    }

    public @NonNull final String time() {
        final double time = (double) (System.nanoTime() - this.startTime) / 1.0E9D;

        return String.format(Locale.ROOT, "%.3fs", time);
    }
}