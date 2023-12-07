package com.badbones69.crazyenvoys.api.objects.misc;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.FileManager;
import com.badbones69.crazyenvoys.api.FileManager.CustomFile;
import us.crazycrew.crazyenvoys.other.MsgUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Tier {

    private final String name;
    private final CustomFile file;

    private boolean claimPermissionToggle;

    private String claimPermission;
    
    private int spawnChance;
    private boolean useChance;
    private Material placedBlockMaterial;
    private Short placedBlockMetaData;
    private boolean bulkToggle;
    private boolean bulkRandom;
    private int bulkMax;
    private boolean holoToggle;
    private Double holoHeight;
    private final List<String> holoMessage;
    private boolean fireworkToggle;
    private List<Color> fireworkColors;
    private boolean signalFlareToggle;
    private String signalFlareTimer;
    private List<Color> signalFlareColors;
    private List<Prize> prizes;
    private List<String> prizeMessage;

    // Placeholders,
    private final HashMap<String, String> lorePlaceholders;

    private final @NotNull CrazyEnvoys plugin = CrazyEnvoys.get();

    private final @NotNull Methods methods = this.plugin.getMethods();
    
    /**
     * Create a new tier.
     *
     * @param name The name of the tier.
     */
    public Tier(String name) {
        this.name = name;
        FileManager fileManager = this.plugin.getFileManager();
        this.file = fileManager.getFile(name);
        this.claimPermission = "";
        this.claimPermissionToggle = false;
        this.spawnChance = 100;
        this.useChance = true;
        this.placedBlockMaterial = Material.CHEST;
        this.placedBlockMetaData = 0;
        this.bulkToggle = false;
        this.bulkRandom = true;
        this.bulkMax = 3;
        this.holoToggle = true;
        this.holoHeight = 1.5;
        this.holoMessage = new ArrayList<>();
        this.lorePlaceholders = new HashMap<>();
        this.fireworkToggle = true;
        this.fireworkColors = new ArrayList<>();
        this.signalFlareToggle = true;
        this.signalFlareTimer = "15s";
        this.signalFlareColors = new ArrayList<>();
        this.prizes = new ArrayList<>();
        this.prizeMessage = Collections.emptyList();
        this.holoMessage.addAll(Collections.singletonList("&7&l(&6&l!&7&l) Envoy Crate"));
    }

    // Check if the envoy is allowed to require the claim permission.
    public boolean isClaimPermissionToggleEnabled() {
        return this.claimPermissionToggle;
    }

    // Set the boolean toggle.
    public Tier setClaimPermissionToggle(boolean claimPermissionToggle) {
        this.claimPermissionToggle = claimPermissionToggle;
        return this;
    }

    // Fetch the permission required to claim the envoy.
    public String getClaimPermission() {
        return this.claimPermission;
    }

    // Set the claim permission.
    public Tier setClaimPermission(String claimPermission) {
        this.claimPermission = claimPermission;
        return this;
    }
    public List<String> getPrizeMessage() {
        return this.prizeMessage;
    }

    public void setPrizeMessage(List<String> prizeMessage) {
        this.prizeMessage = prizeMessage;
    }

    /**
     * Get the name of the tier.
     *
     * @return The name of the tier.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Get the file for the tier.
     */
    public CustomFile getFile() {
        return this.file;
    }
    
    /**
     * Get the chance of the crate spawning.
     */
    public int getSpawnChance() {
        return this.spawnChance;
    }
    
    /**
     * Set the chance that the crate will spawn in the event.
     *
     * @param spawnChance The new chance the crate will spawn.
     */
    public Tier setSpawnChance(int spawnChance) {
        this.spawnChance = spawnChance;
        return this;
    }
    
    /**
     * Check to see if this tier uses a chance system for the prizes.
     */
    public boolean getUseChance() {
        return this.useChance;
    }
    
    /**
     * Set if the tier uses a chance system for the prizes.
     *
     * @param useChance True if it uses a chance system and false if not.
     */
    public Tier setUseChance(boolean useChance) {
        this.useChance = useChance;
        return this;
    }
    
    /**
     * Get the material of the block that acts as the crate.
     */
    public Material getPlacedBlockMaterial() {
        return this.placedBlockMaterial;
    }
    
    /**
     * Set the material of the block that acts as the crate.
     *
     * @param placedBlockMaterial Material of the block.
     */
    public Tier setPlacedBlockMaterial(Material placedBlockMaterial) {
        this.placedBlockMaterial = placedBlockMaterial;
        return this;
    }
    
    /**
     * Get the metadata of the block that is acts as the crate.
     */
    public Short getPlacedBlockMetaData() {
        return this.placedBlockMetaData;
    }
    
    /**
     * Set the metadata of the block that acts as the crate.
     *
     * @param placedBlockMetaData The metadata as a Short.
     */
    public Tier setPlacedBlockMetaData(short placedBlockMetaData) {
        this.placedBlockMetaData = placedBlockMetaData;
        return this;
    }
    
    /**
     * Set the metadata of the block that acts as the crate.
     *
     * @param placedBlockMetaData The metadata as a Short.
     */
    public Tier setPlacedBlockMetaData(int placedBlockMetaData) {
        this.placedBlockMetaData = (short) placedBlockMetaData;
        return this;
    }
    
    /**
     * Check to see if the bulk prizes option is on.
     */
    public boolean getBulkToggle() {
        return this.bulkToggle;
    }
    
    /**
     * Set if the bulk prize option is on.
     *
     * @param bulkToggle True if it can give multiple prizes and false if not.
     */
    public Tier setBulkToggle(boolean bulkToggle) {
        this.bulkToggle = bulkToggle;
        return this;
    }
    
    /**
     * Check if it picks a random amount of prizes.
     * True if it picks from 1-max. False if it picks the max amount of prizes.
     */
    public boolean getBulkRandom() {
        return this.bulkRandom;
    }
    
    /**
     * Set if it picks a random amount of prizes from 1-max.
     *
     * @param bulkRandom True if it picks from 1-max and false if it picks the max amount of prizes.
     */
    public Tier setBulkRandom(boolean bulkRandom) {
        this.bulkRandom = bulkRandom;
        return this;
    }
    
    /**
     * Get the max amount of prizes a bulk can have.
     */
    public int getBulkMax() {
        return this.bulkMax;
    }
    
    /**
     * Set the max amount of prizes a bulk can have.
     *
     * @param bulkMax The max amount of prizes.
     */
    public Tier setBulkMax(int bulkMax) {
        this.bulkMax = bulkMax;
        return this;
    }
    
    /**
     * Check to see if holograms are on for the tier crate.
     */
    public boolean isHoloEnabled() {
        return this.holoToggle;
    }
    
    /**
     * Set if the tier uses holograms.
     *
     * @param holoToggle True if it does and false if not.
     */
    public Tier setHoloToggle(boolean holoToggle) {
        this.holoToggle = holoToggle;
        return this;
    }
    
    /**
     * Get the height of the hologram.
     */
    public Double getHoloHeight() {
        return this.holoHeight;
    }
    
    /**
     * Set the height of the hologram.
     *
     * @param holoHeight The height as a Double.
     */
    public Tier setHoloHeight(Double holoHeight) {
        this.holoHeight = holoHeight;
        return this;
    }


    /**
     * Add a placeholder to the lore of the item.
     *
     * @param placeholder The placeholder you wish to replace.
     * @param argument    The argument that will replace the placeholder.
     */
    public void addLorePlaceholder(String placeholder, String argument) {
        this.lorePlaceholders.put(placeholder, argument);
    }

    /**
     * @return All lore placeholders.
     */
    public HashMap<String, String> getLorePlaceholders() {
        return this.lorePlaceholders;
    }

    /**
     * Get the hologram message with all the placeholders added to it.
     *
     * @return The hologram with all placeholders in it.
     */
    public List<String> getHoloMessage() {
        return this.methods.getPlaceholders(this.holoMessage, this.lorePlaceholders);
    }

    /**
     * Set the message that the hologram displays.
     *
     * @param holoMessage The message that is displayed. This auto color codes the message.
     */
    public Tier setHoloMessage(List<String> holoMessage) {
        this.holoMessage.clear();

        for (String message : holoMessage) {
            this.holoMessage.add(MsgUtils.color(message));
        }

        return this;
    }
    
    /**
     * Check if the tier crate shoots a firework when claimed.
     */
    public boolean getFireworkToggle() {
        return this.fireworkToggle;
    }
    
    /**
     * Set if the tier crate shoots a firework when claimed.
     *
     * @param fireworkToggle True if it does and false if not.
     */
    public Tier setFireworkToggle(boolean fireworkToggle) {
        this.fireworkToggle = fireworkToggle;
        return this;
    }
    
    /**
     * List of all the colors that the firework displays.
     */
    public List<Color> getFireworkColors() {
        return this.fireworkColors;
    }
    
    /**
     * Set the colors of the firework.
     *
     * @param fireworkColors List of Colors of the firework.
     */
    public Tier setFireworkColors(List<Color> fireworkColors) {
        this.fireworkColors = fireworkColors;
        return this;
    }
    
    /**
     * Add a color to the firework effects.
     *
     * @param fireworkColor A color to add to the firework effect.
     */
    public Tier addFireworkColor(Color fireworkColor) {
        this.fireworkColors.add(fireworkColor);
        return this;
    }
    
    /**
     * Check to see if the tier crate shoots signal fireworks for players to see where it is.
     */
    public boolean getSignalFlareToggle() {
        return this.signalFlareToggle;
    }
    
    /**
     * Set if the tier crates shoot fireworks in the air.
     *
     * @param signalFlareToggle True if it does and false if not.
     */
    public Tier setSignalFlareToggle(boolean signalFlareToggle) {
        this.signalFlareToggle = signalFlareToggle;
        return this;
    }
    
    /**
     * Get the amount of time when a flare is shot off.
     */
    public String getSignalFlareTimer() {
        return this.signalFlareTimer;
    }
    
    /**
     * Set the time for a flare to be shot off.
     *
     * @param signalFlareTimer The time until a flare is shot off. Examples: "15s", "1m, 10s", "15m"
     */
    public Tier setSignalFlareTimer(String signalFlareTimer) {
        this.signalFlareTimer = signalFlareTimer;
        return this;
    }
    
    /**
     * Get a list of Colors that the flare displays.
     */
    public List<Color> getSignalFlareColors() {
        return this.signalFlareColors;
    }
    
    /**
     * Set the colors the flare will display.
     *
     * @param signalFlareColors List of colors the firework will be.
     */
    public Tier setSignalFlareColors(List<Color> signalFlareColors) {
        this.signalFlareColors = signalFlareColors;
        return this;
    }
    
    /**
     * Add a color to the signal flare firework effect.
     *
     * @param signalFlareColors The color added to the firework effect.
     */
    public Tier addSignalFlareColor(Color signalFlareColors) {
        this.signalFlareColors.add(signalFlareColors);
        return this;
    }
    
    /**
     * Get the prizes that can be found in the tier.
     */
    public List<Prize> getPrizes() {
        return this.prizes;
    }
    
    /**
     * Set the list of prizes the tier has.
     *
     * @param prizes List of prizes.
     */
    public Tier setPrizes(List<Prize> prizes) {
        this.prizes = prizes;
        return this;
    }
    
    /**
     * Add a prize to the tier.
     *
     * @param prize A new prize that is added to the list of prizes.
     */
    public Tier addPrize(Prize prize) {
        this.prizes.add(prize);
        return this;
    }
}