package me.badbones69.crazyenvoy.api.objects;

import me.badbones69.crazyenvoy.Methods;
import me.badbones69.crazyenvoy.api.FileManager;
import me.badbones69.crazyenvoy.api.FileManager.CustomFile;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tier {
    
    private String name;
    private CustomFile file;
    private int spawnChance;
    private boolean useChance;
    private Material placedBlockMaterial;
    private Short placedBlockMetaData;
    private boolean bulkToggle;
    private boolean bulkRandom;
    private int bulkMax;
    private boolean holoToggle;
    private Double holoHight;
    private List<String> holoMessage;
    private boolean fireworkToggle;
    private List<Color> fireworkColors;
    private boolean signalFlareToggle;
    private String signalFlareTimer;
    private List<Color> signalFlareColors;
    private List<Prize> prizes;
    
    /**
     * Create a new tier.
     * @param name The name of the tier.
     */
    public Tier(String name) {
        this.name = name;
        file = FileManager.getInstance().getFile(name);
        spawnChance = 100;
        useChance = true;
        placedBlockMaterial = Material.CHEST;
        placedBlockMetaData = 0;
        bulkToggle = false;
        bulkRandom = true;
        bulkMax = 3;
        holoToggle = true;
        holoHight = 1.5;
        holoMessage = new ArrayList<>();
        fireworkToggle = true;
        fireworkColors = new ArrayList<>();
        signalFlareToggle = true;
        signalFlareTimer = "15s";
        signalFlareColors = new ArrayList<>();
        prizes = new ArrayList<>();
        holoMessage.addAll(Arrays.asList("&7&l(&6&l!&7&l) Envoy Crate"));
    }
    
    /**
     * Get the name of the tier.
     * @return The name of the tier.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the file for the tier.
     */
    public CustomFile getFile() {
        return file;
    }
    
    /**
     * Get the chance of the crate spawning.
     */
    public int getSpawnChance() {
        return spawnChance;
    }
    
    /**
     * Set the chance that the crate will spawn in the event.
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
        return useChance;
    }
    
    /**
     * Set if the tier uses a chance system for the prizes.
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
        return placedBlockMaterial;
    }
    
    /**
     * Set the material of the block that acts as the crate.
     * @param placedBlockMaterial Material of the block.
     */
    public Tier setPlacedBlockMaterial(Material placedBlockMaterial) {
        this.placedBlockMaterial = placedBlockMaterial;
        return this;
    }
    
    /**
     * Get the meta data of the block that is acts as the crate.
     */
    public Short getPlacedBlockMetaData() {
        return placedBlockMetaData;
    }
    
    /**
     * Set the meta data of the block that acts as the crate.
     * @param placedBlockMetaData The meta data as a Short.
     */
    public Tier setPlacedBlockMetaData(short placedBlockMetaData) {
        this.placedBlockMetaData = placedBlockMetaData;
        return this;
    }
    
    /**
     * Set the meta data of the block that acts as the crate.
     * @param placedBlockMetaData The meta data as a Short.
     */
    public Tier setPlacedBlockMetaData(int placedBlockMetaData) {
        this.placedBlockMetaData = (short) placedBlockMetaData;
        return this;
    }
    
    /**
     * Check to see if the bulk prizes option is on.
     */
    public boolean getBulkToggle() {
        return bulkToggle;
    }
    
    /**
     * Set if the bulk prize option is on.
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
        return bulkRandom;
    }
    
    /**
     * Set if it picks a random amount of prizes from 1-max.
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
        return bulkMax;
    }
    
    /**
     * Set the max amount of prizes a bulk can have.
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
        return holoToggle;
    }
    
    /**
     * Set if the tier uses holograms.
     * @param holoToggle True if it does and false if not.
     */
    public Tier setHoloToggle(boolean holoToggle) {
        this.holoToggle = holoToggle;
        return this;
    }
    
    /**
     * Get the hieght of the hologram.
     */
    public Double getHoloHight() {
        return holoHight;
    }
    
    /**
     * Set the hight of the hologram.
     * @param holoHight The hight as a Double.
     */
    public Tier setHoloHight(Double holoHight) {
        this.holoHight = holoHight;
        return this;
    }
    
    /**
     * Get the hologram message above the tier crates.
     */
    public List<String> getHoloMessage() {
        return holoMessage;
    }
    
    /**
     * Set the message that the hologram displays.
     * @param holoMessage The message that is displayed. This auto color codes the message.
     */
    public Tier setHoloMessage(List<String> holoMessage) {
        this.holoMessage.clear();
        for (String message : holoMessage) {
            this.holoMessage.add(Methods.color(message));
        }
        return this;
    }
    
    /**
     * Check if the tier crate shoots a firework when claimed.
     */
    public boolean getFireworkToggle() {
        return fireworkToggle;
    }
    
    /**
     * Set if the tier crate shoots a firework when claimed.
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
        return fireworkColors;
    }
    
    /**
     * Set the colors of the firework.
     * @param fireworkColors List of Colors of the firework.
     */
    public Tier setFireworkColors(List<Color> fireworkColors) {
        this.fireworkColors = fireworkColors;
        return this;
    }
    
    /**
     * Add a color to the firework effects.
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
        return signalFlareToggle;
    }
    
    /**
     * Set if the tier crates shoot fireworks in the air.
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
        return signalFlareTimer;
    }
    
    /**
     * Set the time for a flare to be shot off.
     * @param signalFlareTimer The time untill a flare is shot off. Examples: "15s", "1m, 10s", "15m"
     */
    public Tier setSignalFlareTimer(String signalFlareTimer) {
        this.signalFlareTimer = signalFlareTimer;
        return this;
    }
    
    /**
     * Get a list of Colors that the flare displays.
     */
    public List<Color> getSignalFlareColors() {
        return signalFlareColors;
    }
    
    /**
     * Set the colors the flare will display.
     * @param signalFlareColors List of colors the firework will be.
     */
    public Tier setSignalFlareColors(List<Color> signalFlareColors) {
        this.signalFlareColors = signalFlareColors;
        return this;
    }
    
    /**
     * Add a color to the signal flare firework effect.
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
        return prizes;
    }
    
    /**
     * Set the list of prizes the tier has.
     * @param prizes List of prizes.
     */
    public Tier setPrizes(List<Prize> prizes) {
        this.prizes = prizes;
        return this;
    }
    
    /**
     * Add a prize to the tier.
     * @param prize A new prize that is added to the list of prizes.
     */
    public Tier addPrize(Prize prize) {
        prizes.add(prize);
        return this;
    }
    
}