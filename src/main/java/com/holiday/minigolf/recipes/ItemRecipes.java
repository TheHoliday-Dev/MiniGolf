package com.holiday.minigolf.recipes;

import com.raus.shortUtils.ShortUtils;
import com.holiday.minigolf.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.UUID;

public class ItemRecipes {

    private static final AttributeModifier noDamage = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", -10, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlot.HAND);
    private static final AttributeModifier fastSwing = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", 10, AttributeModifier.Operation.MULTIPLY_SCALAR_1, org.bukkit.inventory.EquipmentSlot.HAND);

    public static void registerRecipes() {
        Main plugin = Main.getInstance();

        Bukkit.addRecipe(createIronRecipe());
        Bukkit.addRecipe(createDriverRecipe());
        Bukkit.addRecipe(createWedgeRecipe());
        Bukkit.addRecipe(createPutterRecipe());
        Bukkit.addRecipe(createGolfBallRecipe());
        Bukkit.addRecipe(createWhistleRecipe());
    }

    public static ItemStack createIronItem() {
        ItemStack item = new ItemStack(Material.IRON_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Iron");
        meta.setLore(Arrays.asList(ChatColor.DARK_GRAY + "A well-rounded club", ChatColor.DARK_GRAY + "for longer distances."));
        meta.setCustomModelData(2);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, noDamage);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, fastSwing);
        ShortUtils.addKey(meta, Main.getInstance().ironKey);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createDriverItem() {
        ItemStack item = new ItemStack(Material.IRON_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(net.md_5.bungee.api.ChatColor.RESET + "Driver");
        meta.setLore(Arrays.asList(net.md_5.bungee.api.ChatColor.DARK_GRAY + "An upgraded version of the Iron club."));
        meta.setCustomModelData(2);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, noDamage);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, fastSwing);
        ShortUtils.addKey(meta, Main.getInstance().driverKey);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createWedgeItem() {
        ItemStack item = new ItemStack(Material.IRON_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(net.md_5.bungee.api.ChatColor.RESET + "Wedge");
        meta.setLore(Arrays.asList(net.md_5.bungee.api.ChatColor.DARK_GRAY + "A specialized club", net.md_5.bungee.api.ChatColor.DARK_GRAY + "for tall obstacles."));
        meta.setCustomModelData(3);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, noDamage);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, fastSwing);
        ShortUtils.addKey(meta, Main.getInstance().wedgeKey);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createPutterItem() {
        ItemStack item = new ItemStack(Material.IRON_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(net.md_5.bungee.api.ChatColor.RESET + "Putter");
        meta.setLore(Arrays.asList(net.md_5.bungee.api.ChatColor.DARK_GRAY + "A specialized club", net.md_5.bungee.api.ChatColor.DARK_GRAY + "for finishing holes."));
        meta.setCustomModelData(1);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, noDamage);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, fastSwing);
        ShortUtils.addKey(meta, Main.getInstance().putterKey);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createGolfBallItem() {
        ItemStack item = new ItemStack(Material.SNOWBALL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "Golf Ball");
        meta.setCustomModelData(1);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(Main.getInstance().parKey, PersistentDataType.INTEGER, 1);
        ShortUtils.addKey(meta, Main.getInstance().ballKey);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createWhistleItem() {
        ItemStack item = new ItemStack(Material.IRON_NUGGET);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(net.md_5.bungee.api.ChatColor.RESET + "Golf Whistle");
        meta.setLore(Arrays.asList(net.md_5.bungee.api.ChatColor.DARK_GRAY + "Returns your last", net.md_5.bungee.api.ChatColor.DARK_GRAY + "hit golf ball to its", net.md_5.bungee.api.ChatColor.DARK_GRAY + "previous position."));
        meta.setCustomModelData(1);
        ShortUtils.addKey(meta, Main.getInstance().whistleKey);
        item.setItemMeta(meta);
        return item;
    }

    public static ShapedRecipe createIronRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(Main.getInstance().ironKey, createIronItem());
        recipe.shape(" s", "is", "ii");
        recipe.setIngredient('s', Material.STICK);
        recipe.setIngredient('i', Material.IRON_INGOT);
        return recipe;
    }

    public static ShapedRecipe createDriverRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(Main.getInstance().driverKey, createDriverItem());
        recipe.shape(" s ", "is ", "iii");
        recipe.setIngredient('s', Material.STICK);
        recipe.setIngredient('i', Material.IRON_INGOT);
        return recipe;
    }

    public static ShapedRecipe createWedgeRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(Main.getInstance().wedgeKey, createWedgeItem());
        recipe.shape(" s", "is", " i");
        recipe.setIngredient('s', Material.STICK);
        recipe.setIngredient('i', Material.IRON_INGOT);

        return recipe;
    }

    public static ShapedRecipe createPutterRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(Main.getInstance().putterKey, createGolfBallItem());
        recipe.shape(" s", " s", "ii");
        recipe.setIngredient('s', Material.STICK);
        recipe.setIngredient('i', Material.IRON_INGOT);

        return recipe;
    }

    public static ShapedRecipe createGolfBallRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(Main.getInstance().ballKey, createGolfBallItem());
        recipe.shape(" p ", "pcp", " p ");
        recipe.setIngredient('p', Material.WHITE_DYE);
        recipe.setIngredient('c', Material.CLAY_BALL);
        return recipe;
    }

    public static ShapedRecipe createWhistleRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(Main.getInstance().whistleKey, createWhistleItem());
        recipe.shape(" ii", "i i", " i ");
        recipe.setIngredient('i', Material.IRON_NUGGET);
        return recipe;
    }
}
