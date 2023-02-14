package com.islandstudio.neon.stable.secondary.iEffect;

import com.islandstudio.neon.stable.utils.INamespaceKeys;
import com.islandstudio.neon.stable.utils.iGUI.ButtonHighlighter;
import com.islandstudio.neon.stable.utils.iGUI.IGUI;
import com.islandstudio.neon.stable.utils.iGUI.IGUIConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.List;

public abstract class GUIBuilder extends IGUIConstructor {
    public GUIBuilder(IGUI iGUI) {
        super(iGUI);
    }

    /* GUI button display names */
    protected static final String HASTE_1 = ChatColor.GOLD + "Haste I";
    protected static final String HASTE_2 = ChatColor.GOLD + "Haste II";
    protected static final String HASTE_3 = ChatColor.GOLD + "Haste III";
    protected static final String REMOVE_EFFECT = ChatColor.RED + "Remove Effect";

    /* Button identifier key */
    protected static final NamespacedKey BUTTON_ID_KEY = INamespaceKeys.NEON_BUTTON.getKey();

    /* Button highlighter */
    private static final ButtonHighlighter BUTTON_HIGHLIGHTER = new ButtonHighlighter(INamespaceKeys.NEON_BUTTON_HIGHLIGHTER.getKey());

    public void addGUIButtons() {
        /* Button display icon */
        final ItemStack HASTE_1_BUTTON = new ItemStack(Material.GOLDEN_PICKAXE);
        final ItemStack HASTE_2_BUTTON = new ItemStack(Material.GOLDEN_PICKAXE);
        final ItemStack HASTE_3_BUTTON = new ItemStack(Material.GOLDEN_PICKAXE);
        final ItemStack REMOVE_EFFECT_BUTTON = new ItemStack(Material.MILK_BUCKET);

        /* Button item meta */
        final ItemMeta HASTE_1_META = HASTE_1_BUTTON.getItemMeta();
        final ItemMeta HASTE_2_META = HASTE_2_BUTTON.getItemMeta();
        final ItemMeta HASTE_3_META = HASTE_3_BUTTON.getItemMeta();
        final ItemMeta REMOVE_EFFECT_META = REMOVE_EFFECT_BUTTON.getItemMeta();

        /* Button lore / description */
        final List<String> HASTE_1_LORE = Collections.singletonList(ChatColor.GRAY + "Amplifier" + ChatColor.WHITE + ": " + ChatColor.GREEN + "150");
        final List<String> HASTE_2_LORE = Collections.singletonList(ChatColor.GRAY + "Amplifier" + ChatColor.WHITE + ": " + ChatColor.GREEN + "300");
        final List<String> HASTE_3_LORE = Collections.singletonList(ChatColor.GRAY + "Amplifier" + ChatColor.WHITE + ": " + ChatColor.GREEN + "600");

        assert HASTE_1_META != null;
        assert HASTE_2_META != null;
        assert HASTE_3_META != null;
        assert REMOVE_EFFECT_META != null;

        HASTE_1_META.setDisplayName(HASTE_1);
        HASTE_2_META.setDisplayName(HASTE_2);
        HASTE_3_META.setDisplayName(HASTE_3);
        REMOVE_EFFECT_META.setDisplayName(REMOVE_EFFECT);

        HASTE_1_META.setLore(HASTE_1_LORE);
        HASTE_2_META.setLore(HASTE_2_LORE);
        HASTE_3_META.setLore(HASTE_3_LORE);

        HASTE_1_META.addEnchant(BUTTON_HIGHLIGHTER, 0, true);
        HASTE_2_META.addEnchant(BUTTON_HIGHLIGHTER, 0, true);
        HASTE_3_META.addEnchant(BUTTON_HIGHLIGHTER, 0, true);

        HASTE_1_META.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        HASTE_2_META.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        HASTE_3_META.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        HASTE_1_META.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.getKey());
        HASTE_2_META.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.getKey());
        HASTE_3_META.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.getKey());
        REMOVE_EFFECT_META.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.getKey());

        HASTE_1_BUTTON.setItemMeta(HASTE_1_META);
        HASTE_2_BUTTON.setItemMeta(HASTE_2_META);
        HASTE_3_BUTTON.setItemMeta(HASTE_3_META);
        REMOVE_EFFECT_BUTTON.setItemMeta(REMOVE_EFFECT_META);

        gui.setItem(0, HASTE_1_BUTTON);
        gui.setItem(1, HASTE_2_BUTTON);
        gui.setItem(2, HASTE_3_BUTTON);
        gui.setItem(8, REMOVE_EFFECT_BUTTON);
    }
}
