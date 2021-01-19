package me.badbones69.crazyenvoy.api.objects;

import me.badbones69.crazyenvoy.api.FileManager.Files;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class EnvoySettings {
    
    private static EnvoySettings instance = new EnvoySettings();
    
    private boolean isFallingBlocksEnabled;
    private Material fallingBlockMaterial;
    private short fallingBlockDurability;
    private int fallingHeight;
    private boolean isMaxCrateEnabled;
    private boolean randomAmount;
    private int minCrates;
    private int maxCrates;
    private boolean useRandomLocations;
    private int maxRadius;
    private int minRadius;
    private boolean isEnvoyRunTimerEnabled;
    private String envoyRunTimer;
    private boolean isEnvoyCooldownEnabled;
    private String envoyCooldown;
    private String envoyClockTime;
    private boolean isMinPlayersEnabled;
    private boolean isMinFlareEnabled;
    private int minPlayers;
    private boolean isPickupBroadcastEnabled;
    private boolean isCrateCooldownEnabled;
    private String crateCooldownTimer;
    private boolean isWorldMessagesEnabled;
    private List<String> worldMessagesWorlds;
    private List<String> envoyWarnings;
    private boolean isFlaresRegionEnabled;
    private List<String> flaresRegions;
    
    public static EnvoySettings getInstance() {
        return instance;
    }
    
    public void loadSettings() {
        FileConfiguration config = Files.CONFIG.getFile();
        String path = "Settings.";
        this.isFallingBlocksEnabled = config.getBoolean(path + "Falling-Block-Toggle");
        this.fallingBlockDurability = 15;
        String fallingBlock = config.getString(path + "Falling-Block");
        if (fallingBlock.contains(":")) {
            String[] split = fallingBlock.split(":");
            fallingBlock = split[0];
            fallingHeight = Integer.parseInt(split[1]);
        }
        this.fallingBlockMaterial = Material.matchMaterial(fallingBlock);
        if (fallingBlockMaterial == null) {
            fallingBlockMaterial = Material.BEACON;
        }
        this.fallingHeight = config.getInt(path + "Fall-Height");
        this.isMaxCrateEnabled = config.getBoolean(path + "Max-Crate-Toggle");
        this.randomAmount = config.getBoolean(path + "Random-Amount", false);
        this.minCrates = config.getInt(path + "Min-Crates", 1);
        this.maxCrates = config.getInt(path + "Max-Crates");
        this.useRandomLocations = config.getBoolean(path + "Random-Locations");
        this.maxRadius = config.getInt(path + "Max-Radius");
        this.minRadius = config.getInt(path + "Min-Radius");
        this.isEnvoyRunTimerEnabled = config.getBoolean(path + "Envoy-Timer-Toggle");
        this.envoyRunTimer = config.getString(path + "Envoy-Run-Time").toLowerCase();
        this.isEnvoyCooldownEnabled = config.getBoolean(path + "Envoy-Cooldown-Toggle");
        this.envoyCooldown = config.getString(path + "Envoy-Cooldown").toLowerCase();
        this.envoyClockTime = config.getString(path + "Envoy-Time").toLowerCase();
        this.isMinPlayersEnabled = config.getBoolean(path + "Minimum-Players-Toggle");
        this.isMinFlareEnabled = config.getBoolean(path + "Minimum-Flare-Toggle");
        this.minPlayers = config.getInt(path + "Minimum-Players");
        this.isPickupBroadcastEnabled = config.getBoolean(path + "Broadcast-Crate-Pick-Up");
        this.isCrateCooldownEnabled = config.getBoolean(path + "Crate-Collect-Cooldown.Toggle");
        this.crateCooldownTimer = config.getString(path + "Crate-Collect-Cooldown.Time").toLowerCase();
        this.isWorldMessagesEnabled = config.getBoolean(path + "World-Messages.Toggle");
        this.worldMessagesWorlds = config.getStringList(path + "World-Messages.Worlds");
        this.envoyWarnings = config.getStringList(path + "Envoy-Warnings");
        this.isFlaresRegionEnabled = config.getBoolean(path + "Flares.World-Guard.Toggle");
        this.flaresRegions = config.getStringList(path + "Flares.World-Guard.Regions");
    }
    
    public boolean isFallingBlocksEnabled() {
        return isFallingBlocksEnabled;
    }
    
    public EnvoySettings setFallingBlocksEnabled(boolean fallingBlocksEnabled) {
        isFallingBlocksEnabled = fallingBlocksEnabled;
        return this;
    }
    
    public Material getFallingBlockMaterial() {
        return fallingBlockMaterial;
    }
    
    public EnvoySettings setFallingBlockMaterial(Material fallingBlockMaterial) {
        this.fallingBlockMaterial = fallingBlockMaterial;
        return this;
    }
    
    public short getFallingBlockDurability() {
        return fallingBlockDurability;
    }
    
    public EnvoySettings setFallingBlockDurability(short fallingBlockDurability) {
        this.fallingBlockDurability = fallingBlockDurability;
        return this;
    }
    
    public int getFallingHeight() {
        return fallingHeight;
    }
    
    public EnvoySettings setFallingHeight(int fallingHeight) {
        this.fallingHeight = fallingHeight;
        return this;
    }
    
    public boolean isMaxCrateEnabled() {
        return isMaxCrateEnabled;
    }
    
    public EnvoySettings setMaxCrateEnabled(boolean maxCrateEnabled) {
        isMaxCrateEnabled = maxCrateEnabled;
        return this;
    }

    public boolean isRandomAmount() {
        return this.randomAmount;
    }

    public int getMinCrates() {
        return this.minCrates;
    }

    public int getMaxCrates() {
        return maxCrates;
    }
    
    public EnvoySettings setMaxCrates(int maxCrates) {
        this.maxCrates = maxCrates;
        return this;
    }
    
    public boolean isRandomLocationsEnabled() {
        return useRandomLocations;
    }
    
    public EnvoySettings setUseRandomLocations(boolean useRandomLocations) {
        this.useRandomLocations = useRandomLocations;
        return this;
    }
    
    public int getMaxRadius() {
        return maxRadius;
    }
    
    public EnvoySettings setMaxRadius(int maxRadius) {
        this.maxRadius = maxRadius;
        return this;
    }
    
    public int getMinRadius() {
        return minRadius;
    }
    
    public EnvoySettings setMinRadius(int minRadius) {
        this.minRadius = minRadius;
        return this;
    }
    
    public boolean isEnvoyRunTimerEnabled() {
        return isEnvoyRunTimerEnabled;
    }
    
    public EnvoySettings setEnvoyRunTimerEnabled(boolean envoyRunTimerEnabled) {
        isEnvoyRunTimerEnabled = envoyRunTimerEnabled;
        return this;
    }
    
    public String getEnvoyRunTimer() {
        return envoyRunTimer;
    }
    
    public EnvoySettings setEnvoyRunTimer(String envoyRunTimer) {
        this.envoyRunTimer = envoyRunTimer;
        return this;
    }
    
    public boolean isEnvoyCooldownEnabled() {
        return isEnvoyCooldownEnabled;
    }
    
    public EnvoySettings setEnvoyCooldownEnabled(boolean envoyCooldownEnabled) {
        isEnvoyCooldownEnabled = envoyCooldownEnabled;
        return this;
    }
    
    public String getEnvoyCooldown() {
        return envoyCooldown;
    }
    
    public EnvoySettings setEnvoyCooldown(String envoyCooldown) {
        this.envoyCooldown = envoyCooldown;
        return this;
    }
    
    public String getEnvoyClockTime() {
        return envoyClockTime;
    }
    
    public EnvoySettings setEnvoyClockTime(String envoyClockTime) {
        this.envoyClockTime = envoyClockTime;
        return this;
    }
    
    public boolean isMinPlayersEnabled() {
        return isMinPlayersEnabled;
    }
    
    public EnvoySettings setMinPlayersEnabled(boolean minPlayersEnabled) {
        isMinPlayersEnabled = minPlayersEnabled;
        return this;
    }
    
    public boolean isMinFlareEnabled() {
        return isMinFlareEnabled;
    }
    
    public EnvoySettings setMinFlareEnabled(boolean minFlareEnabled) {
        isMinFlareEnabled = minFlareEnabled;
        return this;
    }
    
    public int getMinPlayers() {
        return minPlayers;
    }
    
    public EnvoySettings setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        return this;
    }
    
    public boolean isPickupBroadcastEnabled() {
        return isPickupBroadcastEnabled;
    }
    
    public EnvoySettings setPickupBroadcastEnabled(boolean pickupBroadcastEnabled) {
        isPickupBroadcastEnabled = pickupBroadcastEnabled;
        return this;
    }
    
    public boolean isCrateCooldownEnabled() {
        return isCrateCooldownEnabled;
    }
    
    public EnvoySettings setCrateCooldownEnabled(boolean crateCooldownEnabled) {
        isCrateCooldownEnabled = crateCooldownEnabled;
        return this;
    }
    
    public String getCrateCooldownTimer() {
        return crateCooldownTimer;
    }
    
    public EnvoySettings setCrateCooldownTimer(String crateCooldownTimer) {
        this.crateCooldownTimer = crateCooldownTimer;
        return this;
    }
    
    public boolean isWorldMessagesEnabled() {
        return isWorldMessagesEnabled;
    }
    
    public EnvoySettings setWorldMessagesEnabled(boolean worldMessagesEnabled) {
        isWorldMessagesEnabled = worldMessagesEnabled;
        return this;
    }
    
    public List<String> getWorldMessagesWorlds() {
        return worldMessagesWorlds;
    }
    
    public EnvoySettings setWorldMessagesWorlds(List<String> worldMessagesWorlds) {
        this.worldMessagesWorlds = worldMessagesWorlds;
        return this;
    }
    
    public List<String> getEnvoyWarnings() {
        return envoyWarnings;
    }
    
    public EnvoySettings setEnvoyWarnings(List<String> envoyWarnings) {
        this.envoyWarnings = envoyWarnings;
        return this;
    }
    
    public boolean isFlaresRegionEnabled() {
        return isFlaresRegionEnabled;
    }
    
    public EnvoySettings setFlaresRegionEnabled(boolean flaresRegionEnabled) {
        isFlaresRegionEnabled = flaresRegionEnabled;
        return this;
    }
    
    public List<String> getFlaresRegions() {
        return flaresRegions;
    }
    
    public EnvoySettings setFlaresRegions(List<String> flaresRegions) {
        this.flaresRegions = flaresRegions;
        return this;
    }
    
}