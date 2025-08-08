package com.badbones69.crazyenvoys.api.objects.misc;

import com.badbones69.crazyenvoys.Methods;
import com.badbones69.crazyenvoys.api.objects.ItemBuilder;
import com.badbones69.crazyenvoys.util.MsgUtils;
import com.ryderbelserion.fusion.paper.utils.ColorUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.*;

public class Tier {

    private final String name;

    private boolean claimPermissionToggle;

    private String claimPermission;
    
    private int spawnChance;
    private boolean useChance;
    private Material placedBlockMaterial;
    private short placedBlockMetaData;
    private boolean bulkToggle;
    private boolean bulkRandom;
    private int bulkMax;
    private boolean holoToggle;
    private double holoHeight;
    private final List<String> holoMessage;
    private boolean fireworkToggle;
    private List<Color> fireworkColors = new ArrayList<>();
    private boolean signalFlareToggle;
    private String signalFlareTimer;
    private List<Color> signalFlareColors = new ArrayList<>();
    private List<Prize> prizes = new ArrayList<>();
    private List<String> prizeMessage = new ArrayList<>();

    public Tier(
            final boolean claimPermissionToggle,
            @NotNull final String claimPermission,
            final boolean useChance,
            final int spawnChance,
            final boolean bulkToggle,
            final boolean bulkRandom,
            final int maxBulk,
            final boolean holoToggle,
            final int holoRange,
            final double holoHeight,
            @NotNull final List<String> holoMessage,
            @NotNull final YamlConfiguration configuration,
            @NotNull final Path path
    ) {
        this.claimPermissionToggle = claimPermissionToggle;
        this.claimPermission = claimPermission;
        this.useChance = useChance;
        this.spawnChance = spawnChance;
        this.bulkToggle = bulkToggle;
        this.bulkRandom = bulkRandom;
        this.bulkMax = maxBulk;
        this.holoToggle = holoToggle;
        this.holoRange = holoRange;
        this.holoHeight = holoHeight;
        this.holoMessage = holoMessage;

        final ItemBuilder placedBlock = new ItemBuilder().setMaterial(configuration.getString("Settings.Placed-Block", "CHEST"));

        setPlacedBlockMaterial(placedBlock.getMaterial());
        setPlacedBlockMetaData(placedBlock.getDamage());

        this.fireworkToggle = configuration.getBoolean("Settings.Firework-Toggle");

        final List<String> colors = configuration.getStringList("Settings.Firework-Colors");

        if (colors.isEmpty()) {
            setFireworkColors(Arrays.asList(Color.GRAY, Color.BLACK, Color.ORANGE));
        } else {
            colors.forEach(color -> addFireworkColor(ColorUtils.getColor(color)));
        }

        if (configuration.contains("Settings.Prize-Message") && !configuration.getStringList("Settings.Prize-Message").isEmpty()) {
            List<String> array = new ArrayList<>();

            configuration.getStringList("Settings.Prize-Message").forEach(line -> array.add(line.replaceAll("%reward%", "{reward}").replaceAll("%tier%", "{tier}")));

            setPrizeMessage(array);
        }

        setSignalFlareToggle(configuration.getBoolean("Settings.Signal-Flare.Toggle"));
        setSignalFlareTimer(configuration.getString("Settings.Signal-Flare.Time"));

        if (configuration.getStringList("Settings.Signal-Flare.Colors").isEmpty()) {
            setSignalFlareColors(Arrays.asList(Color.GRAY, Color.BLACK, Color.ORANGE));
        } else {
            configuration.getStringList("Settings.Signal-Flare.Colors").forEach(color -> addSignalFlareColor(ColorUtils.getColor(color)));
        }

        for (String prizeID : configuration.getConfigurationSection("Prizes").getKeys(false)) {
            String cpath = "Prizes." + prizeID + ".";
            int chance = configuration.getInt(cpath + "Chance");
            String displayName = configuration.contains(cpath + "DisplayName") ? configuration.getString(cpath + "DisplayName") : "";

            List<String> commands = new ArrayList<>();

            configuration.getStringList(cpath + "Commands").forEach(line -> commands.add(line.replaceAll("%reward%", "{reward}")
                    .replaceAll("%player%", "{player}")
                    .replaceAll("%Player%", "{player}")
                    .replaceAll("%tier%", "{tier}")));

            List<String> messages = new ArrayList<>();

            configuration.getStringList(cpath + "Messages").forEach(line -> messages.add(line.replaceAll("%reward%", "{reward}")
                    .replaceAll("%player%", "{player}")
                    .replaceAll("%Player%", "{player}")
                    .replaceAll("%tier%", "{tier}")));

            boolean dropItems = configuration.getBoolean(cpath + "Drop-Items");
            List<ItemBuilder> items = ItemBuilder.convertStringList(configuration.getStringList(cpath + "Items"));
            addPrize(new Prize(prizeID).setDisplayName(displayName).setChance(chance).setDropItems(dropItems).setItemBuilders(items)
                    .setCommands(commands).setMessages(messages));
        }

        this.name = path.getFileName().toString().replace(".yml", "");
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
    public short getPlacedBlockMetaData() {
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
     *
     * @return true if it picks from 1-max. false if it picks the max amount of prizes.
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
    public double getHoloHeight() {
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

    private int holoRange;

    /**
     * Set the range at which a hologram can be seen.
     *
     * @param holoRange the range
     * @return the object with updated information.
     */
    public Tier setHoloRange(int holoRange) {
        this.holoRange = holoRange;

        return this;
    }

    /**
     * Gets the range at which a hologram can be seen.
     *
     * @return the range
     */
    public int getHoloRange() {
        return this.holoRange;
    }

    /**
     * Get the hologram message with all the placeholders added to it.
     *
     * @return The hologram with all placeholders in it.
     */
    public List<String> getHoloMessage() {
        return Methods.getPlaceholders(this.holoMessage);
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