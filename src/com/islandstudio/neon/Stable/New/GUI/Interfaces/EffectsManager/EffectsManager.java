package com.islandstudio.neon.Stable.New.GUI.Interfaces.EffectsManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EffectsManager implements Listener {
    //Inventory Name//
    public final String inventoryName = ChatColor.GREEN + "Effects Manager";

    //Button Names//
    public final String EFFECT_1 = ChatColor.GREEN + "Haste I";
    public final String EFFECT_2 = ChatColor.GREEN + "Haste II";
    public final String EFFECT_3 = ChatColor.GREEN + "Haste III";
    public final String REMOVE_BUTTON = ChatColor.RED + "Remove Effect";

    public final void openEffectManager(Player player) {
        Inventory effectInventory = Bukkit.getServer().createInventory(null, 9, inventoryName);

        ItemFlag[] itemFlags= {ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE};

        //Item Stacks//
        ItemStack HASTE_1 = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemStack HASTE_2 = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemStack HASTE_3 = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemStack REMOVE_EFFECT = new ItemStack(Material.BARRIER);

        //Item Metas//
        ItemMeta HASTE_1_META = HASTE_1.getItemMeta();
        ItemMeta HASTE_2_META = HASTE_2.getItemMeta();
        ItemMeta HASTE_3_META = HASTE_3.getItemMeta();
        ItemMeta REMOVE_EFFECT_META = REMOVE_EFFECT.getItemMeta();

        if (HASTE_1_META != null && HASTE_2_META != null && HASTE_3_META != null && REMOVE_EFFECT_META != null) {
            //Display Names//
            HASTE_1_META.setDisplayName(EFFECT_1);
            HASTE_2_META.setDisplayName(EFFECT_2);
            HASTE_3_META.setDisplayName(EFFECT_3);
            REMOVE_EFFECT_META.setDisplayName(REMOVE_BUTTON);

            //Enchantments//
            HASTE_1_META.addEnchant(Enchantment.DIG_SPEED, 3, false);
            HASTE_2_META.addEnchant(Enchantment.DIG_SPEED, 3, false);
            HASTE_3_META.addEnchant(Enchantment.DIG_SPEED, 3, false);
            REMOVE_EFFECT_META.addEnchant(Enchantment.DIG_SPEED, 3, false);

            //Unbreakable//
            HASTE_1_META.setUnbreakable(true);
            HASTE_2_META.setUnbreakable(true);
            HASTE_3_META.setUnbreakable(true);
            REMOVE_EFFECT_META.setUnbreakable(true);

            //Item Flags//
            HASTE_1_META.addItemFlags(itemFlags);
            HASTE_2_META.addItemFlags(itemFlags);
            HASTE_3_META.addItemFlags(itemFlags);
            REMOVE_EFFECT_META.addItemFlags(itemFlags);
        }

        //Set Item Meta//
        HASTE_1.setItemMeta(HASTE_1_META);
        HASTE_2.setItemMeta(HASTE_2_META);
        HASTE_3.setItemMeta(HASTE_3_META);
        REMOVE_EFFECT.setItemMeta(REMOVE_EFFECT_META);

        //Set Item Into Effect Inventory//
        effectInventory.setItem(0, HASTE_1);
        effectInventory.setItem(1, HASTE_2);
        effectInventory.setItem(2, HASTE_3);
        effectInventory.setItem(8, REMOVE_EFFECT);

        player.openInventory(effectInventory);
    }


}
