package us.crazycrew.crazyenvoys.paper.api.plugin.migration;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import com.badbones69.crazyenvoys.paper.CrazyEnvoys;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenvoys.common.config.types.Config;
import us.crazycrew.crazyenvoys.common.config.types.Messages;
import us.crazycrew.crazyenvoys.common.config.types.PluginConfig;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MigrationService {

    private final @NotNull CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private SettingsManager pluginConfig;

    public void migrate() {
        // Migrate some options from the config.yml to plugin-config.yml
        copyPluginConfig();

        // Migrate what's left from Config-Backup.yml to config.yml then delete the backup file.
        copyConfig();

        // Migrate all messages to en-US.yml then delete Messages.yml
        copyMessages();

        // Rename file if found.
        File file = new File(this.plugin.getDataFolder(), "users.yml");
        if (file.exists()) file.renameTo(new File(this.plugin.getDataFolder(), "users.yml"));
    }

    private void copyPluginConfig() {
        File input = new File(this.plugin.getDataFolder(),"config.yml");

        YamlConfiguration file = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        // Check if this exists, and if not we return.
        if (file.getString("Settings.Prefix") == null) return;

        // Fetch the values I want to migrate
        String oldPrefix = file.getString("Settings.Prefix");
        boolean oldMetrics = file.getBoolean("Settings.Toggle-Metrics");

        // Create the plugin-config.yml file.
        File pluginConfigFile = new File(this.plugin.getDataFolder(), "plugin-config.yml");

        // Bind it to settings manager
        this.pluginConfig = SettingsManagerBuilder
                .withYamlFile(pluginConfigFile)
                .useDefaultMigrationService()
                .configurationData(PluginConfig.class)
                .create();

        this.pluginConfig.setProperty(PluginConfig.toggle_metrics, oldMetrics);

        if (oldPrefix != null) {
            this.pluginConfig.setProperty(PluginConfig.command_prefix, oldPrefix);
        }

        file.set("Settings.Prefix", null);
        file.set("Settings.Toggle-Metrics", null);

        try {
            this.pluginConfig.save();

            file.save(input);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void copyConfig() {
        File input = new File(this.plugin.getDataFolder(), "config.yml");

        YamlConfiguration file = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();
        
        // If the config configuration doesn't have this. we do nothing.
        if (file.getString("Settings.Falling-Block-Toggle") == null) return;
        
        // Rename config.yml to this.
        File backupFile = new File(this.plugin.getDataFolder(), "Config-Backup.yml");
        input.renameTo(backupFile);

        // Bind it to settings manager
        SettingsManager config = SettingsManagerBuilder
                .withYamlFile(input)
                .useDefaultMigrationService()
                .configurationData(Config.class)
                .create();

        boolean fallingBlockToggle = file.getBoolean("Settings.Falling-Block-Toggle");

        String fallingBlockType = file.getString("Settings.Falling-Block");

        int fallingBlockHeight = file.getInt("Settings.Fall-Height");

        boolean maxCrateToggle = file.getBoolean("Settings.Max-Crate-Toggle");

        boolean randomAmount = file.getBoolean("Settings.Random-Amount");

        int minCrates = file.getInt("Settings.Min-Crates");

        int maxCrates = file.getInt("Settings.Max-Crates");

        boolean randomLocations = file.getBoolean("Settings.Random-Locations");

        int maxRadius = file.getInt("Settings.Max-Radius");

        int minRadius = file.getInt("Settings.Min-Radius");

        boolean envoyLocationsBroadcast = file.getBoolean("Settings.Envoy-Locations.Broadcast");

        String envoyRunTime = file.getString("Settings.Envoy-Run-Time");

        boolean envoyTimerToggle = file.getBoolean("Settings.Envoy-Timer-Toggle");

        boolean envoyCooldownToggle = file.getBoolean("Settings.Envoy-Cooldown-Toggle");

        boolean envoyFilterPLayers = file.getBoolean("Settings.Envoy-Filter-Players-Zero");

        String envoyCooldown = file.getString("Settings.Envoy-Cooldown");
        String envoyTime = file.getString("Settings.Envoy-Time");

        boolean minimumPlayersToggle = file.getBoolean("Settings.Minimum-Players-Toggle");

        boolean minimumFlareToggle = file.getBoolean("Settings.Minimum-Flare-Toggle");

        int minimumPlayersRequired = file.getInt("Settings.Minimum-Players");

        boolean broadcastEnvoyPickUp = file.getBoolean("Settings.Broadcast-Crate-Pick-Up");

        boolean envoyCollectCooldownToggle = file.getBoolean("Settings.Crate-Collect-Cooldown.Toggle");

        String envoyCollectCooldownTime = file.getString("Settings.Crate-Collect-Cooldown.Time");

        boolean envoyCountdownToggle = file.getBoolean("Settings.Crate-Countdown.Toggle");
        int envoyCountdownTime = file.getInt("Settings.Crate-Countdown.Time");

        String envoyCountdownMessage = file.getString("Settings.Crate-Countdown.Message");

        String envoyCountdownMessageOther = file.getString("Settings.Crate-Countdown.Message-Seconds");

        boolean envoyWorldMessages = file.getBoolean("Settings.World-Messages.Toggle");

        List<String> envoyAllowedWorlds = file.getStringList("Settings.World-Messages.Worlds");

        List<String> envoyWarnings = file.getStringList("Settings.Envoy-Warnings");

        String envoyFlareItem = file.getString("Settings.Flares.Item");

        String envoyFlareName = file.getString("Settings.Flares.Name");

        List<String> envoyFlareItemLore = file.getStringList("Settings.Flares.Lore");

        boolean envoyFlareWorldGuard = file.getBoolean("Settings.Flares.World-Guard.Toggle");

        List<String> envoyFlareWorldGuardRegions = file.getStringList("Settings.Flares.World-Guard.Regions");

        config.setProperty(Config.envoy_falling_block_toggle, fallingBlockToggle);
        config.setProperty(Config.envoy_falling_block_type, fallingBlockType);
        config.setProperty(Config.envoy_falling_height, fallingBlockHeight);

        config.setProperty(Config.envoys_max_drops_toggle, maxCrateToggle);
        config.setProperty(Config.envoys_max_drops, maxCrates);
        config.setProperty(Config.envoys_min_drops, minCrates);

        config.setProperty(Config.envoys_random_drops, randomAmount);

        config.setProperty(Config.envoys_random_locations, randomLocations);
        config.setProperty(Config.envoys_max_radius, maxRadius);
        config.setProperty(Config.envoys_min_radius, minRadius);

        config.setProperty(Config.envoys_locations_broadcast, envoyLocationsBroadcast);

        config.setProperty(Config.envoys_run_time_toggle, envoyTimerToggle);
        config.setProperty(Config.envoys_run_time, envoyRunTime);

        config.setProperty(Config.envoys_countdown, envoyCooldownToggle);
        config.setProperty(Config.envoys_cooldown, envoyCooldown);
        config.setProperty(Config.envoys_time, envoyTime);

        config.setProperty(Config.envoys_ignore_empty_server, envoyFilterPLayers);

        config.setProperty(Config.envoys_minimum_players_toggle, minimumPlayersToggle);
        config.setProperty(Config.envoys_minimum_players_amount, minimumPlayersRequired);

        config.setProperty(Config.envoys_flare_minimum_players_toggle, minimumFlareToggle);
        config.setProperty(Config.envoys_flare_minimum_players_amount, minimumPlayersRequired);

        config.setProperty(Config.envoys_flare_item_name, envoyFlareName);
        config.setProperty(Config.envoys_flare_item_type, envoyFlareItem);
        config.setProperty(Config.envoys_flare_item_lore, envoyFlareItemLore);
        config.setProperty(Config.envoys_flare_world_guard_toggle, envoyFlareWorldGuard);
        config.setProperty(Config.envoys_flare_world_guard_regions, envoyFlareWorldGuardRegions);

        config.setProperty(Config.envoys_announce_player_pickup, broadcastEnvoyPickUp);

        config.setProperty(Config.envoys_grab_cooldown_toggle, envoyCollectCooldownToggle);
        config.setProperty(Config.envoys_grab_cooldown_timer, envoyCollectCooldownTime);

        config.setProperty(Config.envoys_grace_period_toggle, envoyCountdownToggle);
        config.setProperty(Config.envoys_grace_period_timer, envoyCountdownTime);
        config.setProperty(Config.envoys_grace_period_unlocked, envoyCountdownMessage);
        config.setProperty(Config.envoys_grace_period_time_unit, envoyCountdownMessageOther);

        config.setProperty(Config.envoys_world_messages, envoyWorldMessages);
        config.setProperty(Config.envoys_allowed_worlds, envoyAllowedWorlds);
        config.setProperty(Config.envoys_warnings, envoyWarnings);

        // Save new config.
        config.save();

        // Delete old file.
        backupFile.delete();
    }

    private void copyMessages() {
        File input = new File(this.plugin.getDataFolder(), "Messages.yml");

        // If the input does not exist, We don't need to do anything else.
        if (!input.exists()) return;

        // Load configuration of input.
        YamlConfiguration file = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        // Check if directory exists and create it if not.
        File localeDir = new File(this.plugin.getDataFolder(), "locale");
        if (!localeDir.exists()) localeDir.mkdirs();

        // Create messages file.
        File messagesFile = new File(localeDir, this.pluginConfig.getProperty(PluginConfig.locale_file) + ".yml");
        SettingsManager messages = SettingsManagerBuilder
                .withYamlFile(messagesFile)
                .useDefaultMigrationService()
                .configurationData(Messages.class)
                .create();
        
        String noPermission = convert("{prefix}" + file.getString("Messages.No-Permission"));
        String noClaimPermission = convert("{prefix}" + file.getString("Messages.No-Permission-Claim"));
        
        String playerOnly = convert("{prefix}" + file.getString("Messages.Players-Only"));
        String notOnline = convert("{prefix}" + file.getString("Messages.Not-Online"));
        String notANumber = convert("{prefix}" + file.getString("Messages.Not-A-Number"));
        
        String reloaded = convert("{prefix}" + file.getString("Messages.Reloaded"));
        
        String alreadyStarted = convert("{prefix}" + file.getString("Messages.Already-Started"));
        String forceStart = convert("{prefix}" + file.getString("Messages.Force-Start"));
        String notStarted = convert("{prefix}" + file.getString("Messages.Not-Started"));
        String forceEnded = convert("{prefix}" + file.getString("Messages.Force-Ended"));
        
        String warning = convert("{prefix}" + file.getString("Messages.Warning"));
        String started = convert("{prefix}" + file.getString("Messages.Started"));
        String left = convert("{prefix}" + file.getString("Messages.Left"));
        String ended = convert("{prefix}" + file.getString("Messages.Ended"));
        
        String notEnoughPlayers = convert("{prefix}" + file.getString("Messages.Not-Enough-Players"));
        
        String enterEditorMode = convert("{prefix}" + file.getString("Messages.Enter-Editor-Mode"));
        String leaveEditorMode = convert("{prefix}" + file.getString("Messages.Leave-Editor-Mode"));
        
        String editorClearLocations = convert("{prefix}" + file.getString("Messages.Editor-Clear-Locations"));
        String editorClearFailure = convert("{prefix}" + file.getString("Messages.Editor-Clear-Failure"));
        
        String kickedFromEditor = convert("{prefix}" + file.getString("Messages.Kicked-From-Editor-Mode"));
        
        String addLocation = convert("{prefix}" + file.getString("Messages.Add-Location"));
        String removeLocation = convert("{prefix}" + file.getString("Messages.Remove-Location"));
        
        String timeLeft = convert("{prefix}" + file.getString("Messages.Time-Left"));
        String timeTillEvent = convert("{prefix}" + file.getString("Messages.Time-Till-Event"));
        String usedFlare = convert("{prefix}" + file.getString("Messages.Used-Flare"));
        
        String cantUseFlares = convert("{prefix}" + file.getString("Messages.Cant-Use-Flares"));
        String giveFlare = convert("{prefix}" + file.getString("Messages.Give-Flare"));
        String givenFlare = convert("{prefix}" + file.getString("Messages.Given-Flare"));
        
        String newCenter = convert("{prefix}" + file.getString("Messages.New-Center"));
        
        String notInWorldGuardRegion = convert("{prefix}" + file.getString("Messages.Not-In-World-Guard-Region"));
        
        String startIgnoringMessages = convert("{prefix}" + file.getString("Messages.Start-Ignoring-Messages"));
        String stopIgnoringMessages = convert("{prefix}" + file.getString("Messages.Stop-Ignoring-Messages"));
        
        String cooldownLeft = convert("{prefix}" + file.getString("Messages.Cooldown-Left"));
        
        String countdownInProgress = convert("{prefix}" + file.getString("Messages.Countdown-In-Progress"));
        
        String dropsAvailable = convert("{prefix}" + file.getString("Messages.Drops-Available"));
        String dropsPossible = convert("{prefix}" + file.getString("Messages.Drops-Possibilities"));
        
        String dropsPage = convert("{prefix}" + file.getString("Messages.Drops-Page"));
        
        String dropsFormat = convert("{prefix}" + file.getString("Messages.Drops-Format"));
        
        String noSpawnLocations = convert("{prefix}" + file.getString("Messages.No-Spawn-Locations-Found"));
        
        String commandNotFound = convert("{prefix}" + file.getString("Messages.Command-Not-Found"));
        
        String hologramOnGoing = convert("{prefix}" + file.getString("Messages.Hologram-Placeholders.On-Going"));
        String hologramNotRunning = convert("{prefix}" + file.getString("Messages.Hologram-Placeholders.Not-Running"));
        
        String timeDay = convert("{prefix}" + file.getString("Messages.Time-Placeholders.Day"));
        String timeHour = convert("{prefix}" + file.getString("Messages.Time-Placeholders.Hour"));
        String timeMinute = convert("{prefix}" + file.getString("Messages.Time-Placeholders.Minute"));
        String timeSecond = convert("{prefix}" + file.getString("Messages.Time-Placeholders.Second"));
        
        String crateLocations = convert("{prefix}" + file.getString("Messages.Crate-Locations"));
        
        String locationFormat = convert("{prefix}" + file.getString("Messages.Location-Format"));
        
        List<String> help = file.getStringList("Messages.Help");

        messages.setProperty(Messages.no_permission, noPermission);
        messages.setProperty(Messages.no_claim_permission, noClaimPermission);

        messages.setProperty(Messages.envoy_already_started, alreadyStarted);
        messages.setProperty(Messages.envoy_force_start, forceStart);
        messages.setProperty(Messages.envoy_not_started, notStarted);
        messages.setProperty(Messages.envoy_force_ended, forceEnded);

        messages.setProperty(Messages.envoy_warning, warning);
        messages.setProperty(Messages.envoy_started, started);
        messages.setProperty(Messages.envoys_remaining, left);
        messages.setProperty(Messages.envoy_ended, ended);

        messages.setProperty(Messages.not_enough_players, notEnoughPlayers);

        messages.setProperty(Messages.enter_editor_mode, enterEditorMode);
        messages.setProperty(Messages.exit_editor_mode, leaveEditorMode);
        messages.setProperty(Messages.envoy_clear_locations, editorClearLocations);
        messages.setProperty(Messages.envoy_clear_failure, editorClearFailure);

        messages.setProperty(Messages.envoy_kicked_from_editor_mode, kickedFromEditor);

        messages.setProperty(Messages.envoy_add_location, addLocation);
        messages.setProperty(Messages.envoy_remove_location, removeLocation);

        messages.setProperty(Messages.envoy_time_left, timeLeft);
        messages.setProperty(Messages.envoy_time_till_event, timeTillEvent);

        messages.setProperty(Messages.envoy_used_flare, usedFlare);
        messages.setProperty(Messages.envoy_cant_use_flare, cantUseFlares);
        messages.setProperty(Messages.envoy_give_flare, giveFlare);
        messages.setProperty(Messages.envoy_received_flare, givenFlare);

        messages.setProperty(Messages.envoy_new_center, newCenter);

        messages.setProperty(Messages.not_in_world_guard_region, notInWorldGuardRegion);

        messages.setProperty(Messages.start_ignoring_messages, startIgnoringMessages);
        messages.setProperty(Messages.stop_ignoring_messages, stopIgnoringMessages);

        messages.setProperty(Messages.cooldown_left, cooldownLeft);

        messages.setProperty(Messages.countdown_in_progress, countdownInProgress);

        messages.setProperty(Messages.drops_available, dropsAvailable);
        messages.setProperty(Messages.drops_possibilities, dropsPossible);
        messages.setProperty(Messages.drops_page, dropsPage);
        messages.setProperty(Messages.drops_format, dropsFormat);

        messages.setProperty(Messages.no_spawn_locations_found, noSpawnLocations);

        messages.setProperty(Messages.hologram_on_going, hologramOnGoing);
        messages.setProperty(Messages.hologram_not_running, hologramNotRunning);

        messages.setProperty(Messages.time_placeholder_day, timeDay);
        messages.setProperty(Messages.time_placeholder_hour, timeHour);
        messages.setProperty(Messages.time_placeholder_minute, timeMinute);
        messages.setProperty(Messages.time_placeholder_second, timeSecond);

        messages.setProperty(Messages.envoy_locations, crateLocations);
        messages.setProperty(Messages.location_format, locationFormat);
        messages.setProperty(Messages.command_not_found, commandNotFound);
        messages.setProperty(Messages.player_only, playerOnly);
        messages.setProperty(Messages.not_online, notOnline);
        messages.setProperty(Messages.not_a_number, notANumber);
        messages.setProperty(Messages.envoy_plugin_reloaded, reloaded);

        messages.setProperty(Messages.help, help);

        // Save the file.
        messages.save();

        // Delete the Messages.yml
        input.delete();
    }

    private String convert(String message) {
        return message
                .replaceAll("%prefix%", "")
                .replaceAll("%Prefix%", "")
                .replaceAll("{time}", "{time}")
                .replaceAll("%amount%", "{amount}")
                .replaceAll("%tier%", "{tier}")
                .replaceAll("%player%", "{player}")
                .replaceAll("%world%", "{world}")
                .replaceAll("%x%", "{x}")
                .replaceAll("%y%", "{y}")
                .replaceAll("%z%", "{z}")
                .replaceAll("%id%", "{id}")
                .replaceAll("%locations%", "{locations}");
    }
}