package com.badbones69.crazyenvoys.api.objects;

import com.badbones69.crazyenvoys.CrazyEnvoys;
import com.badbones69.crazyenvoys.util.MsgUtils;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.ryderbelserion.fusion.core.api.support.ModSupport;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.PlayerBuilder;
import com.ryderbelserion.fusion.paper.utils.ColorUtils;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.th0rgal.oraxen.api.OraxenItems;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final CrazyEnvoys plugin = JavaPlugin.getPlugin(CrazyEnvoys.class);

    private final FusionPaper fusion = this.plugin.getFusion();

    private final Server server = this.plugin.getServer();

    // Item Data
    private Material material;
    private TrimMaterial trimMaterial;
    private TrimPattern trimPattern;
    private int damage;
    private String itemName;
    private final List<String> itemLore;
    private int itemAmount;
    private String customMaterial;

    // Player

    // Skulls
    private boolean isHead = false;
    private boolean isCustom = false;

    /**
     * Holds the {@link UUID} of the skull of which this {@link ItemStack} belongs to.
     */
    private @Nullable UUID uuid = null;

    private String customHead = "";

    /**
     * Holds the url for the skull.
     */
    private String url = "";

    // Enchantments/Flags
    private boolean unbreakable;
    private boolean hideItemFlags;
    private boolean glowing;

    // Entities
    private final boolean isMobEgg;
    private EntityType entityType;

    // Potions
    private PotionType potionType;
    private Color potionColor;
    private boolean isPotion;

    // Armor
    private Color armorColor;
    private boolean isLeatherArmor;

    // Enchantments
    private Map<Enchantment, Integer> enchantments;

    // Shields
    private boolean isShield;

    // Banners
    private boolean isBanner;
    private List<Pattern> patterns;

    // Maps
    private boolean isMap;
    private Color mapColor;

    // Placeholders
    private Map<String, String> namePlaceholders;
    private Map<String, String> lorePlaceholders;

    // Misc
    private ItemStack referenceItem;
    private List<ItemFlag> itemFlags;

    // Custom Data
    private int customModelData;
    private boolean useCustomModelData;

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    /**
     * Create a blank item builder.
     */
    public ItemBuilder() {
        this.itemStack = null;
        this.itemMeta = null;
        this.material = Material.STONE;
        this.trimMaterial = null;
        this.trimPattern = null;
        this.damage = 0;
        this.itemName = "";
        this.itemLore = new ArrayList<>();
        this.itemAmount = 1;

        this.isHead = false;

        this.unbreakable = false;
        this.hideItemFlags = false;
        this.glowing = false;

        this.isMobEgg = false;
        this.entityType = EntityType.BAT;

        this.potionType = null;
        this.potionColor = null;
        this.isPotion = false;

        this.armorColor = null;
        this.isLeatherArmor = false;

        this.enchantments = new HashMap<>();

        this.isShield = false;

        this.isBanner = false;
        this.patterns = new ArrayList<>();

        this.isMap = false;
        this.mapColor = Color.RED;

        this.namePlaceholders = new HashMap<>();
        this.lorePlaceholders = new HashMap<>();

        this.itemFlags = new ArrayList<>();
    }

    /**
     * Deduplicate an item builder.
     *
     * @param itemBuilder The item builder to deduplicate.
     */
    public ItemBuilder(ItemBuilder itemBuilder) {
        this.itemStack = itemBuilder.itemStack;
        this.itemMeta = itemBuilder.itemMeta;
        this.material = itemBuilder.material;
        this.trimMaterial = itemBuilder.trimMaterial;
        this.trimPattern = itemBuilder.trimPattern;
        this.damage = itemBuilder.damage;
        this.itemName = itemBuilder.itemName;
        this.itemLore = new ArrayList<>(itemBuilder.itemLore);
        this.itemAmount = itemBuilder.itemAmount;

        this.customHead = itemBuilder.customHead;

        this.uuid = itemBuilder.uuid;
        this.url = itemBuilder.url;

        this.referenceItem = itemBuilder.referenceItem;
        this.customModelData = itemBuilder.customModelData;
        this.useCustomModelData = itemBuilder.useCustomModelData;

        this.enchantments = new HashMap<>(itemBuilder.enchantments);

        this.isHead = itemBuilder.isHead;
        this.isCustom = itemBuilder.isCustom;

        this.unbreakable = itemBuilder.unbreakable;
        this.hideItemFlags = itemBuilder.hideItemFlags;
        this.glowing = itemBuilder.glowing;

        this.isMobEgg = itemBuilder.isMobEgg;
        this.entityType = itemBuilder.entityType;

        this.potionType = itemBuilder.potionType;
        this.potionColor = itemBuilder.potionColor;
        this.isPotion = itemBuilder.isPotion;

        this.armorColor = itemBuilder.armorColor;
        this.isLeatherArmor = itemBuilder.isLeatherArmor;

        this.isShield = itemBuilder.isShield;

        this.isBanner = itemBuilder.isBanner;
        this.patterns = new ArrayList<>(itemBuilder.patterns);

        this.isMap = itemBuilder.isMap;
        this.mapColor = itemBuilder.mapColor;

        this.namePlaceholders = new HashMap<>(itemBuilder.namePlaceholders);
        this.lorePlaceholders = new HashMap<>(itemBuilder.lorePlaceholders);
        this.itemFlags = new ArrayList<>(itemBuilder.itemFlags);

        this.customMaterial = itemBuilder.customMaterial;
    }

    /**
     * Updates the item meta
     *
     * @param itemMeta the new item meta
     */
    public void setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
    }

    /**
     * @return the item meta
     */
    public ItemMeta getItemMeta() {
        return this.itemMeta;
    }

    /**
     * @return the material.
     */
    public Material getMaterial() {
        return this.material;
    }

    /**
     * @return trim material
     */
    public TrimMaterial getTrimMaterial() {
        return this.trimMaterial;
    }

    /**
     * @return if the item is a banner.
     */
    public boolean isBanner() {
        return this.isBanner;
    }

    /**
     * @return if an item is a shield.
     */
    public boolean isShield() {
        return this.isShield;
    }

    /**
     * @return if the item is a spawn mob egg.
     */
    public boolean isMobEgg() {
        return this.isMobEgg;
    }

    /**
     * @return the entity type of the spawn mob egg.
     */
    public EntityType getEntityType() {
        return this.entityType;
    }

    /**
     * @return the name of the item.
     */
    public String getName() {
        return this.itemName;
    }

    /**
     * @return the lore on the item.
     */
    public List<String> getLore() {
        return this.itemLore;
    }

    /**
     * @return the enchantments on the item.
     */
    public Map<Enchantment, Integer> getEnchantments() {
        return this.enchantments;
    }

    /**
     * @return a list of ItemFlags.
     */
    public List<ItemFlag> getItemFlags() {
        return this.itemFlags;
    }

    /**
     * @return checks if flags are hidden.
     */
    public boolean isItemFlagsHidden() {
        return this.hideItemFlags;
    }

    /**
     * @return check if item is Leather Armor
     */
    public boolean isLeatherArmor() {
        return this.isLeatherArmor;
    }

    /**
     * @return checks if item is glowing.
     */
    public boolean isGlowing() {
        return this.glowing;
    }

    /**
     * @return checks if the item is unbreakable.
     */
    public boolean isUnbreakable() {
        return this.unbreakable;
    }

    /**
     * @return the amount of the item stack.
     */
    public Integer getAmount() {
        return this.itemAmount;
    }

    /**
     * Get the patterns on the banners.
     */
    public List<Pattern> getPatterns() {
        return this.patterns;
    }

    /**
     * Get the item's name with all the placeholders added to it.
     *
     * @return The name with all the placeholders in it.
     */
    public String getUpdatedName() {
        String newName = this.itemName;

        for (String placeholder : this.namePlaceholders.keySet()) {
            newName = newName.replace(placeholder, this.namePlaceholders.get(placeholder)).replace(placeholder.toLowerCase(), this.namePlaceholders.get(placeholder));
        }

        return newName;
    }

    private boolean isArmor() {
        String name = this.material.name();

        return name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS") || name.equals(Material.TURTLE_HELMET.name());
    }

    /**
     * Builder the item from all the information that was given to the builder.
     *
     * @return the result of all the info that was given to the builder as an ItemStack.
     */
    public ItemStack build() {
        ItemStack item = this.itemStack;

        final Optional<HeadDatabaseAPI> optional = this.plugin.getApi();

        if (optional.isPresent() && !this.customHead.isEmpty()) {
            final HeadDatabaseAPI api = optional.get();

            item = api.isHead(this.customHead) ? api.getItemHead(this.customHead) : item.withType(Material.PLAYER_HEAD);

            this.itemMeta = item.getItemMeta();
            this.material = Material.PLAYER_HEAD;

            this.isCustom = true;
        }

        if (this.fusion.isModReady(ModSupport.oraxen)) {
            io.th0rgal.oraxen.items.ItemBuilder oraxenItem = OraxenItems.getItemById(this.customMaterial);

            if (oraxenItem != null && OraxenItems.exists(this.customMaterial)) {
                item = oraxenItem.build();

                this.itemMeta = item.getItemMeta();
            }
        }

        if (this.fusion.isModReady(ModSupport.nexo)) {

        }

        if (this.fusion.isModReady(ModSupport.items_adder)) {

        }

        if (item.getType() == Material.AIR) {
            this.fusion.log("error", "Item cannot be air!");

            return null;
        }

        item.setAmount(this.itemAmount);

        if (this.itemMeta == null) this.itemMeta = item.getItemMeta();

        this.itemMeta.setDisplayName(getUpdatedName());
        this.itemMeta.setLore(getUpdatedLore());

        if (this.isHead && !this.isCustom) {
            if (this.itemMeta instanceof final SkullMeta skull) {
                if (this.uuid != null && !skull.hasOwner()) {
                    skull.setOwningPlayer(this.server.getOfflinePlayer(this.uuid));
                } else {
                    final PlayerProfile profile = this.server.createProfile(null, "");

                    profile.setProperty(new ProfileProperty("", ""));

                    PlayerTextures textures = profile.getTextures();

                    try {
                        textures.setSkin(URI.create(this.url).toURL(), PlayerTextures.SkinModel.CLASSIC);
                    } catch (final MalformedURLException exception) {
                        this.fusion.log("error", "Failed to set the texture url.", exception);
                    }

                    profile.setTextures(textures);
                    skull.setPlayerProfile(profile);
                }
            }
        }

        if (isArmor()) {
            if (this.trimPattern != null && this.trimMaterial != null) {
                ((ArmorMeta) this.itemMeta).setTrim(new ArmorTrim(this.trimMaterial, this.trimPattern));
            }
        }

        if (this.isMap) {
            MapMeta mapMeta = (MapMeta) this.itemMeta;

            if (this.mapColor != null) mapMeta.setColor(this.mapColor);
        }

        if (this.itemMeta instanceof Damageable damageable) {
            if (this.damage >= 1) {
                if (this.damage >= item.getType().getMaxDurability()) {
                    damageable.setDamage(item.getType().getMaxDurability());
                } else {
                    damageable.setDamage(this.damage);
                }
            }
        }

        if (this.isPotion && (this.potionType != null || this.potionColor != null)) {
            PotionMeta potionMeta = (PotionMeta) this.itemMeta;

            if (this.potionType != null) potionMeta.setBasePotionType(this.potionType);

            if (this.potionColor != null) potionMeta.setColor(this.potionColor);
        }

        if (this.material == Material.TIPPED_ARROW && this.potionType != null) {
            PotionMeta potionMeta = (PotionMeta) this.itemMeta;
            potionMeta.setBasePotionType(this.potionType);

            if (this.potionColor != null) potionMeta.setColor(this.potionColor);
        }

        if (this.isLeatherArmor && this.armorColor != null) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) this.itemMeta;
            leatherMeta.setColor(this.armorColor);
        }

        if (this.isBanner && !this.patterns.isEmpty()) {
            BannerMeta bannerMeta = (BannerMeta) this.itemMeta;
            bannerMeta.setPatterns(this.patterns);
        }

        if (this.isShield && !this.patterns.isEmpty()) {
            BlockStateMeta shieldMeta = (BlockStateMeta) this.itemMeta;
            Banner banner = (Banner) shieldMeta.getBlockState();
            banner.setPatterns(this.patterns);
            banner.update();
            shieldMeta.setBlockState(banner);
        }

        if (this.useCustomModelData) this.itemMeta.setCustomModelData(this.customModelData);

        this.itemFlags.forEach(this.itemMeta::addItemFlags);
        item.setItemMeta(this.itemMeta);
        hideItemFlags();
        item.addUnsafeEnchantments(this.enchantments);
        addGlow();

        return item;
    }

    /**
     * Set the type of item the builder is set to.
     *
     * @param material the material you wish to set.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder setMaterial(final Material material) {
        this.material = material;

        this.itemStack = new ItemStack(this.material);
        this.itemMeta = this.itemStack.getItemMeta();

        this.isHead = material == Material.PLAYER_HEAD;

        return this;
    }

    /**
     * Sets a custom skull
     *
     * @param skull the id of the skull
     * @return {@link ItemBuilder}
     */
    public @NotNull final ItemBuilder setSkull(@NotNull final String skull) {
        if (skull.isEmpty() || this.plugin.getApi().isEmpty()) return this;

        this.customHead = skull;
        this.isCustom = true;

        return this;
    }

    /**
     * Set trim material
     *
     * @param trimMaterial pattern to set.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder setTrimMaterial(final TrimMaterial trimMaterial) {
        this.trimMaterial = trimMaterial;

        return this;
    }

    /**
     * Set trim pattern
     *
     * @param trimPattern pattern to set.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder setTrimPattern(final TrimPattern trimPattern) {
        this.trimPattern = trimPattern;

        return this;
    }

    /**
     * Set the type of item and its metadata in the builder.
     *
     * @param material the string must be in this form: %Material% or %Material%:%MetaData%
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder setMaterial(String material) {
        String metaData;
        //Store material inside iaNamespace (e.g. ia:myblock)
        this.customMaterial = material;

        if (material.contains(":")) { // Sets the durability or another value option.
            String[] b = material.split(":");
            material = b[0];
            metaData = b[1];

            if (metaData.contains("#")) { // <ID>:<Durability>#<CustomModelData>
                String modelData = metaData.split("#")[1];
                if (isInt(modelData)) { // Value is a number.
                    this.useCustomModelData = true;
                    this.customModelData = Integer.parseInt(modelData);
                }
            }

            metaData = metaData.replace("#" + this.customModelData, "");

            if (isInt(metaData)) { // Value is durability.
                this.damage = Integer.parseInt(metaData);
            } else { // Value is something else.
                try {
                    this.potionType = getPotionType(PotionEffectType.getByName(metaData));
                } catch (Exception ignored) {}

                this.potionColor = ColorUtils.getColor(metaData);
                this.armorColor = ColorUtils.getColor(metaData);
                this.mapColor = ColorUtils.getColor(metaData);
            }
        } else if (material.contains("#")) {
            String[] b = material.split("#");
            material = b[0];

            if (isInt(b[1])) { // Value is a number.
                this.useCustomModelData = true;
                this.customModelData = Integer.parseInt(b[1]);
            }
        }

        Material matchedMaterial = Material.matchMaterial(material);

        if (matchedMaterial != null) this.material = matchedMaterial;

        // If it's item, create itemstack.
        if (this.material.isItem()) {
            this.itemStack = new ItemStack(this.material);
            this.itemMeta = this.itemStack.getItemMeta();
        }

        switch (this.material.name()) {
            case "PLAYER_HEAD" -> this.isHead = true;
            case "POTION", "SPLASH_POTION" -> this.isPotion = true;
            case "LEATHER_HELMET", "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS", "LEATHER_HORSE_ARMOR" -> this.isLeatherArmor = true;
            case "BANNER" -> this.isBanner = true;
            case "SHIELD" -> this.isShield = true;
            case "FILLED_MAP" -> this.isMap = true;
        }

        if (this.material.name().contains("BANNER")) this.isBanner = true;

        return this;
    }

    /**
     * @param damage the damage value of the item.
     * @return {@link ItemBuilder}
     */
    public ItemBuilder setDamage(final int damage) {
        this.damage = damage;

        return this;
    }

    /**
     * Get the damage to the item.
     *
     * @return the damage to the item as an int.
     */
    public int getDamage() {
        return this.damage;
    }

    /**
     * @param itemName the name of the item.
     * @return the ItemBuilder with an updated name.
     */
    public ItemBuilder setName(final String itemName) {
        if (itemName != null) this.itemName = MsgUtils.color(itemName);

        return this;
    }

    /**
     * @param placeholders the placeholders that will be used.
     * @return the ItemBuilder with updated placeholders.
     */
    public ItemBuilder setNamePlaceholders(final Map<String, String> placeholders) {
        this.namePlaceholders = placeholders;

        return this;
    }

    /**
     * Add a placeholder to the name of the item.
     *
     * @param placeholder the placeholder that will be replaced.
     * @param argument the argument you wish to replace the placeholder with.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder addNamePlaceholder(final String placeholder, final String argument) {
        this.namePlaceholders.put(placeholder, argument);

        return this;
    }

    /**
     * Remove a placeholder from the list.
     *
     * @param placeholder the placeholder you wish to remove.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder removeNamePlaceholder(final String placeholder) {
        this.namePlaceholders.remove(placeholder);

        return this;
    }

    /**
     * Set the lore of the item in the builder. This will auto force color in all the lores that contains color code.
     *
     * @param lore the lore of the item in the builder.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder setLore(final List<String> lore) {
        if (lore != null) {
            this.itemLore.clear();

            for (String line : lore) {
                this.itemLore.add(MsgUtils.color(line));
            }
        }

        return this;
    }

    /**
     * Add a line to the current lore of the item. This will auto force color in the lore that contains color code.
     *
     * @param lore the new line you wish to add.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder addLore(final String lore) {
        if (lore != null) this.itemLore.add(MsgUtils.color(lore));

        return this;
    }

    /**
     * Set the placeholders that are in the lore of the item.
     *
     * @param placeholders the placeholders that you wish to use.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder setLorePlaceholders(final Map<String, String> placeholders) {
        this.lorePlaceholders = placeholders;

        return this;
    }

    /**
     * Add a placeholder to the lore of the item.
     *
     * @param placeholder the placeholder you wish to replace.
     * @param argument the argument that will replace the placeholder.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder addLorePlaceholder(final String placeholder, final String argument) {
        this.lorePlaceholders.put(placeholder, argument);

        return this;
    }

    /**
     * Get the lore with all the placeholders added to it.
     *
     * @return the lore with all placeholders in it.
     */
    public List<String> getUpdatedLore() {
        List<String> newLore = new ArrayList<>();

        for (String item : this.itemLore) {
            for (String placeholder : this.lorePlaceholders.keySet()) {
                item = item.replace(placeholder, this.lorePlaceholders.get(placeholder)).replace(placeholder.toLowerCase(), this.lorePlaceholders.get(placeholder));
            }

            newLore.add(item);
        }

        return newLore;
    }

    /**
     * Remove a placeholder from the lore.
     *
     * @param placeholder the placeholder you wish to remove.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder removeLorePlaceholder(final String placeholder) {
        this.lorePlaceholders.remove(placeholder);

        return this;
    }

    /**
     * @param entityType the entity type the mob spawn egg will be.
     * @return the ItemBuilder with an updated mob spawn egg.
     */
    public ItemBuilder setEntityType(final EntityType entityType) {
        this.entityType = entityType;

        return this;
    }

    /**
     * Add patterns to the item.
     *
     * @param stringPattern the pattern you wish to add.
     */
    private void addPatterns(final String stringPattern) {
        try {
            String[] split = stringPattern.split(":");

            for (PatternType pattern : PatternType.values()) {
                if (split[0].equalsIgnoreCase(pattern.name()) || split[0].equalsIgnoreCase(pattern.getIdentifier())) {
                    DyeColor color = ColorUtils.getDyeColor(split[1]);

                    addPattern(new Pattern(color, pattern));

                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    /**
     * @param patterns the list of Patterns to add.
     * @return the ItemBuilder with updated patterns.
     */
    public ItemBuilder addPatterns(final List<String> patterns) {
        patterns.forEach(this::addPatterns);

        return this;
    }

    /**
     * @param pattern a pattern to add.
     * @return the ItemBuilder with an updated pattern.
     */
    public ItemBuilder addPattern(final Pattern pattern) {
        this.patterns.add(pattern);

        return this;
    }

    /**
     * @param patterns set a list of Patterns.
     * @return the ItemBuilder with an updated list of patterns.
     */
    public ItemBuilder setPattern(final List<Pattern> patterns) {
        this.patterns = patterns;

        return this;
    }

    /**
     * @param amount the amount of the item stack.
     * @return the ItemBuilder with an updated item count.
     */
    public ItemBuilder setAmount(final int amount) {
        this.itemAmount = amount;

        return this;
    }

    /**
     * Set the player that will be displayed on the head.
     *
     * @param player the player being displayed on the head.
     * @return the ItemBuilder with an updated Player Name.
     */
    public ItemBuilder setPlayerName(final String player) {
        if (player.isEmpty() || player.isBlank()) return this;

        // This is temporary until HDB is updated, The dev of the plugin has a house now
        // and his plugin doesn't work on 1.21
        if (player.length() > 16) {
            this.url = "https://textures.minecraft.net/texture/" + player.replace("https://textures.minecraft.net/texture/", "");

            return this;
        }

        @NotNull final PlayerBuilder builder = new PlayerBuilder(this.plugin, player);
        // More extensive but we only call methods once, and we avoid NPE.
        @Nullable final Player target = builder.getPlayer();

        if (target != null) {
            this.uuid = target.getUniqueId();
        } else {
            @Nullable final OfflinePlayer offlineTarget = builder.getOfflinePlayer();

            if (offlineTarget != null) this.uuid = offlineTarget.getUniqueId();
        }

        return this;
    }

    /**
     * It will override any enchantments used in ItemBuilder.addEnchantment() below.
     *
     * @param enchantment a list of enchantments to add to the item.
     * @return the ItemBuilder with a list of updated enchantments.
     */
    public ItemBuilder setEnchantments(HashMap<Enchantment, Integer> enchantment) {
        if (enchantment != null) this.enchantments = enchantment;

        return this;
    }

    /**
     * Adds an enchantment to the item.
     *
     * @param enchantment the enchantment you wish to add.
     * @param level the level of the enchantment ( Unsafe levels included )
     * @return the ItemBuilder with updated enchantments.
     */
    public ItemBuilder addEnchantments(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    /**
     * Remove an enchantment from the item.
     *
     * @param enchantment the enchantment you wish to remove.
     * @return the ItemBuilder with updated enchantments.
     */
    public ItemBuilder removeEnchantments(Enchantment enchantment) {
        this.enchantments.remove(enchantment);
        return this;
    }

    /**
     * Set the flags that will be on the item in the builder.
     *
     * @param flagStrings the flag names as string you wish to add to the item in the builder.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder setFlagsFromStrings(List<String> flagStrings) {
        this.itemFlags.clear();

        for (String flagString : flagStrings) {
            ItemFlag flag = getFlag(flagString);

            if (flag != null) this.itemFlags.add(flag);
        }

        return this;
    }

    /**
     * Adds a list of item flags to an item.
     *
     * @param flagStrings list of items to add.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder addItemFlags(List<String> flagStrings) {
        for (String flagString : flagStrings) {
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(flagString.toUpperCase());

                addItemFlag(itemFlag);
            } catch (Exception ignored) {}
        }

        return this;
    }

    /**
     * Add a flag to the item in the builder.
     *
     * @param flagString the name of the flag you wish to add.
     * @return the ItemBuilder with updated info.
     */
    public ItemBuilder addFlags(String flagString) {
        ItemFlag flag = getFlag(flagString);

        if (flag != null) this.itemFlags.add(flag);
        return this;
    }

    /**
     * Adds an ItemFlag to a map which is added to an item.
     *
     * @param itemFlag the flag to add.
     * @return the ItemBuilder with an updated ItemFlag.
     */
    public ItemBuilder addItemFlag(ItemFlag itemFlag) {
        if (itemFlag != null) this.itemFlags.add(itemFlag);

        return this;
    }

    /**
     * Adds multiple ItemFlags in a list to a map which get added to an item.
     *
     * @param itemFlags the list of flags to add.
     * @return the ItemBuilder with a list of ItemFlags.
     */
    public ItemBuilder setItemFlags(List<ItemFlag> itemFlags) {
        if (itemFlags.isEmpty()) return this;

        this.itemFlags = itemFlags;
        return this;
    }

    /**
     * @param hideItemFlags hide item flags based on a boolean.
     * @return the ItemBuilder with an updated Boolean.
     */
    public ItemBuilder hideItemFlags(boolean hideItemFlags) {
        this.hideItemFlags = hideItemFlags;
        return this;
    }

    /**
     * Hides item flags
     */
    public void hideItemFlags() {
        if (this.hideItemFlags) {
            this.itemMeta.addItemFlags(ItemFlag.values());
            this.itemStack.setItemMeta(this.itemMeta);
        }
    }

    /**
     * Sets the converted item as a reference to try and save NBT tags and stuff.
     *
     * @param referenceItem the item that is being referenced.
     * @return the ItemBuilder with updated info.
     */
    private ItemBuilder setReferenceItem(ItemStack referenceItem) {
        this.itemStack = referenceItem;
        this.itemMeta = this.itemStack.getItemMeta();
        return this;
    }

    /**
     * @param unbreakable sets the item to be unbreakable.
     * @return the ItemBuilder with an updated Boolean.
     */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    /**
     * @param glow sets whether to make an item to glow or not.
     * @return the ItemBuilder with an updated Boolean.
     */
    public ItemBuilder setGlow(boolean glow) {
        this.glowing = glow;
        return this;
    }

    /**
     * Converts a String to an ItemBuilder.
     *
     * @param itemString the string you wish to convert.
     * @return the string as an ItemBuilder.
     */
    public static ItemBuilder convertString(String itemString) {
        return convertString(itemString, null);
    }

    /**
     * Converts a string to an ItemBuilder with a placeholder for errors.
     *
     * @param itemString the string you wish to convert.
     * @param placeHolder the placeholder to use if there is an error.
     * @return the string as an ItemBuilder.
     */
    public static ItemBuilder convertString(String itemString, String placeHolder) {
        ItemBuilder itemBuilder = new ItemBuilder();

        try {
            for (String optionString : itemString.split(", ")) {
                String option = optionString.split(":")[0];
                String value = optionString.replace(option + ":", "").replace(option, "");

                switch (option.toLowerCase()) {
                    case "item" -> itemBuilder.setMaterial(value);
                    case "name" -> itemBuilder.setName(value);
                    case "amount" -> {
                        try {
                            itemBuilder.setAmount(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            itemBuilder.setAmount(1);
                        }
                    }
                    case "damage" -> {
                        try {
                            itemBuilder.setDamage(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            itemBuilder.setDamage(0);
                        }
                    }
                    case "lore" -> itemBuilder.setLore(Arrays.asList(value.split(",")));
                    case "player" -> itemBuilder.setPlayerName(value);
                    case "skull" -> itemBuilder.setSkull(value);
                    case "unbreakable-item" -> {
                        if (value.isEmpty() || value.equalsIgnoreCase("true")) itemBuilder.setUnbreakable(true);
                    }
                    case "trim-pattern" -> {
                        if (!value.isEmpty()) itemBuilder.setTrimPattern(RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).get(NamespacedKey.minecraft(value.toLowerCase())));
                    }
                    case "trim-material" -> {
                        if (!value.isEmpty()) itemBuilder.setTrimMaterial(RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).get(NamespacedKey.minecraft(value.toLowerCase())));
                    }
                    default -> {
                        Enchantment enchantment = getEnchantment(option);

                        if (enchantment != null) {
                            try {
                                itemBuilder.addEnchantments(enchantment, Integer.parseInt(value));
                            } catch (NumberFormatException e) {
                                itemBuilder.addEnchantments(enchantment, 1);
                            }

                            break;
                        }

                        for (ItemFlag itemFlag : ItemFlag.values()) {
                            if (itemFlag.name().equalsIgnoreCase(option)) {
                                itemBuilder.addItemFlag(itemFlag);
                                break;
                            }
                        }

                        try {
                            for (PatternType pattern : PatternType.values()) {
                                if (option.equalsIgnoreCase(pattern.name()) || value.equalsIgnoreCase(pattern.getIdentifier())) {
                                    DyeColor color = ColorUtils.getDyeColor(value);

                                    itemBuilder.addPattern(new Pattern(color, pattern));

                                    break;
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception exception) {
            itemBuilder.setMaterial(Material.RED_TERRACOTTA).setName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor : &c" + (placeHolder != null ? placeHolder : "")));

            CrazyEnvoys plugin = CrazyEnvoys.get();
            plugin.getLogger().log(Level.WARNING, "An error has occurred with the item builder: ", exception);
        }

        return itemBuilder;
    }

    /**
     * Converts a list of Strings to a list of ItemBuilders.
     *
     * @param itemStrings the list of Strings.
     * @return the list of ItemBuilders.
     */
    public static List<ItemBuilder> convertStringList(List<String> itemStrings) {
        return convertStringList(itemStrings, null);
    }

    /**
     * Converts a list of Strings to a list of ItemBuilders with a placeholder for errors.
     *
     * @param itemStrings the list of strings.
     * @param placeholder the placeholder for errors.
     * @return the list of ItemBuilders.
     */
    public static List<ItemBuilder> convertStringList(List<String> itemStrings, String placeholder) {
        return itemStrings.stream().map(itemString -> convertString(itemString, placeholder)).collect(Collectors.toList());
    }

    /**
     * Add glow to an item.
     *
     */
    private void addGlow() {
        if (this.glowing) {
            try {
                this.itemMeta.setEnchantmentGlintOverride(true);
                this.itemStack.setItemMeta(this.itemMeta);
            } catch (NoClassDefFoundError ignored) {}
        }
    }

    /**
     * Get the PotionEffect from a PotionEffectType.
     *
     * @param type The type of the potion effect.
     * @return The potion type.
     */
    private PotionType getPotionType(PotionEffectType type) {
        if (type != null) {
            if (type.equals(PotionEffectType.FIRE_RESISTANCE)) {
                return PotionType.FIRE_RESISTANCE;
            } else if (type.equals(PotionEffectType.INSTANT_DAMAGE)) {
                return PotionType.STRONG_HARMING;
            } else if (type.equals(PotionEffectType.INSTANT_HEALTH)) {
                return PotionType.HEALING;
            } else if (type.equals(PotionEffectType.INVISIBILITY)) {
                return PotionType.INVISIBILITY;
            } else if (type.equals(PotionEffectType.JUMP_BOOST)) {
                return PotionType.LEAPING;
            } else if (type.equals(PotionEffectType.LUCK)) {
                return PotionType.LUCK;
            } else if (type.equals(PotionEffectType.NIGHT_VISION)) {
                return PotionType.NIGHT_VISION;
            } else if (type.equals(PotionEffectType.POISON)) {
                return PotionType.POISON;
            } else if (type.equals(PotionEffectType.REGENERATION)) {
                return PotionType.REGENERATION;
            } else if (type.equals(PotionEffectType.SLOWNESS)) {
                return PotionType.SLOWNESS;
            } else if (type.equals(PotionEffectType.SPEED)) {
                return PotionType.SWIFTNESS;
            } else if (type.equals(PotionEffectType.STRENGTH)) {
                return PotionType.STRENGTH;
            } else if (type.equals(PotionEffectType.WATER_BREATHING)) {
                return PotionType.WATER_BREATHING;
            } else if (type.equals(PotionEffectType.WEAKNESS)) {
                return PotionType.WEAKNESS;
            }
        }

        return null;
    }

    /**
     * Get the enchantment from a string.
     *
     * @param enchantmentName the string of the enchantment.
     * @return the enchantment from the string.
     */
    private static Enchantment getEnchantment(String enchantmentName) {
        enchantmentName = stripEnchantmentName(enchantmentName);
        for (Enchantment enchantment : Enchantment.values()) {
            try {
                if (stripEnchantmentName(enchantment.getKey().getKey()).equalsIgnoreCase(enchantmentName)) return enchantment;

                HashMap<String, String> enchantments = getEnchantmentList();

                if (stripEnchantmentName(enchantment.getName()).equalsIgnoreCase(enchantmentName) || (enchantments.get(enchantment.getName()) != null &&
                        stripEnchantmentName(enchantments.get(enchantment.getName())).equalsIgnoreCase(enchantmentName))) return enchantment;
            } catch (Exception ignore) {}
        }

        return null;
    }

    /**
     * Strip extra characters from an enchantment name.
     *
     * @param enchantmentName the enchantment name.
     * @return the stripped enchantment name.
     */
    private static String stripEnchantmentName(String enchantmentName) {
        return enchantmentName != null ? enchantmentName.replace("-", "").replace("_", "").replace(" ", "") : null;
    }

    /**
     * Get the list of enchantments and their in-Game names.
     *
     * @return the hashmap of enchantments and their in-game names.
     */
    private static HashMap<String, String> getEnchantmentList() {
        HashMap<String, String> enchantments = new HashMap<>();
        enchantments.put("ARROW_DAMAGE", "Power");
        enchantments.put("ARROW_FIRE", "Flame");
        enchantments.put("ARROW_INFINITE", "Infinity");
        enchantments.put("ARROW_KNOCKBACK", "Punch");
        enchantments.put("DAMAGE_ALL", "Sharpness");
        enchantments.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
        enchantments.put("DAMAGE_UNDEAD", "Smite");
        enchantments.put("DEPTH_STRIDER", "Depth_Strider");
        enchantments.put("DIG_SPEED", "Efficiency");
        enchantments.put("DURABILITY", "Unbreaking");
        enchantments.put("FIRE_ASPECT", "Fire_Aspect");
        enchantments.put("KNOCKBACK", "KnockBack");
        enchantments.put("LOOT_BONUS_BLOCKS", "Fortune");
        enchantments.put("LOOT_BONUS_MOBS", "Looting");
        enchantments.put("LUCK", "Luck_Of_The_Sea");
        enchantments.put("LURE", "Lure");
        enchantments.put("OXYGEN", "Respiration");
        enchantments.put("PROTECTION_ENVIRONMENTAL", "Protection");
        enchantments.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
        enchantments.put("PROTECTION_FALL", "Feather_Falling");
        enchantments.put("PROTECTION_FIRE", "Fire_Protection");
        enchantments.put("PROTECTION_PROJECTILE", "Projectile_Protection");
        enchantments.put("SILK_TOUCH", "Silk_Touch");
        enchantments.put("THORNS", "Thorns");
        enchantments.put("WATER_WORKER", "Aqua_Affinity");
        enchantments.put("BINDING_CURSE", "Curse_Of_Binding");
        enchantments.put("MENDING", "Mending");
        enchantments.put("FROST_WALKER", "Frost_Walker");
        enchantments.put("VANISHING_CURSE", "Curse_Of_Vanishing");
        enchantments.put("SWEEPING_EDGE", "Sweeping_Edge");
        enchantments.put("RIPTIDE", "Riptide");
        enchantments.put("CHANNELING", "Channeling");
        enchantments.put("IMPALING", "Impaling");
        enchantments.put("LOYALTY", "Loyalty");

        return enchantments;
    }

    /**
     * Check if the parameter is an integer.
     *
     * @param value string to check.
     * @return true if integer or false if not.
     */
    private boolean isInt(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    /**
     * Get an item flag from string.
     *
     * @param flagString string to check.
     * @return item flag or null.
     */
    private ItemFlag getFlag(String flagString) {
        for (ItemFlag flag : ItemFlag.values()) {
            if (flag.name().equalsIgnoreCase(flagString)) return flag;
        }

        return null;
    }
}