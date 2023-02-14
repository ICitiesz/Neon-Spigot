package com.islandstudio.neon.stable.secondary.iWaypoints;

import com.islandstudio.neon.stable.utils.INamespaceKeys;
import com.islandstudio.neon.stable.utils.iGUI.ButtonHighlighter;
import com.islandstudio.neon.stable.utils.iGUI.IGUI;
import com.islandstudio.neon.stable.utils.iGUI.IGUIConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class GUIBuilderRemoval extends IGUIConstructor {
    protected int maxItemPerPage = 45;
    protected int maxPage = 1;
    protected int pageIndex = 0;
    protected int itemIndex = 0;

    /* Button display names */
    protected static final String previousButtonDisplayName = ChatColor.GOLD + "" + ChatColor.BOLD + "<<Previous";
    protected static final String nextButtonDisplayName = ChatColor.GOLD + "" + ChatColor.BOLD + "Next>>";
    protected static final String closeButtonDisplayName = ChatColor.RED + "" + ChatColor.BOLD + "Close";
    protected static final String clearSelectionButtonDisplayName = ChatColor.YELLOW + "" + ChatColor.BOLD + "Clear selection";
    protected static final String removeButtonDisplayName = ChatColor.RED + "" + ChatColor.BOLD + "Remove";

    /* Button lore */
    protected static List<String> removeButtonLore = Collections.singletonList(ChatColor.YELLOW + "Please select waypoint(s) to remove!");
    protected static List<String> clearSelectionButtonLore = Collections.singletonList(ChatColor.GRAY + "Total selected: " + ChatColor.GREEN + 0);

    /* Button identifier key */
    protected static final NamespacedKey BUTTON_ID_KEY = INamespaceKeys.NEON_BUTTON.getKey();

    /* Button highlighter */
    protected static final ButtonHighlighter BUTTON_HIGHLIGHTER = new ButtonHighlighter(INamespaceKeys.NEON_BUTTON_HIGHLIGHTER.getKey());

    public GUIBuilderRemoval(IGUI IGUI) {
        super(IGUI);
    }

    public void addGUIButtons() {
        /* Button lore */
        List<String> navigationButtonLore = Arrays.asList(
                ChatColor.GRAY + "Current Page:", ChatColor.GREEN + "" + (pageIndex + 1)
                        + ChatColor.WHITE + " of " + ChatColor.GREEN + maxPage);

        /* Button item */
        ItemStack previousButton = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack nextButton = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemStack clearSelectionButton = new ItemStack(Material.NAME_TAG);
        ItemStack removeButton = new ItemStack(Material.BLAZE_POWDER);

        /* Button item meta */
        ItemMeta previousButtonMeta = previousButton.getItemMeta();
        ItemMeta nextButtonMeta = nextButton.getItemMeta();
        ItemMeta closeButtonMeta = closeButton.getItemMeta();
        ItemMeta clearSelectionButtonMeta = clearSelectionButton.getItemMeta();
        ItemMeta removeButtonMeta = removeButton.getItemMeta();

        if (previousButtonMeta == null || nextButtonMeta == null || closeButtonMeta == null || clearSelectionButtonMeta == null || removeButtonMeta == null) return;

        previousButtonMeta.setDisplayName(previousButtonDisplayName);
        previousButtonMeta.setLore(navigationButtonLore);
        previousButtonMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

        nextButtonMeta.setDisplayName(nextButtonDisplayName);
        nextButtonMeta.setLore(navigationButtonLore);
        nextButtonMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

        closeButtonMeta.setDisplayName(closeButtonDisplayName);
        closeButtonMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

        clearSelectionButtonMeta.setDisplayName(clearSelectionButtonDisplayName);
        clearSelectionButtonMeta.setLore(clearSelectionButtonLore);
        clearSelectionButtonMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

        removeButtonMeta.setDisplayName(removeButtonDisplayName);
        removeButtonMeta.setLore(removeButtonLore);
        removeButtonMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

        previousButton.setItemMeta(previousButtonMeta);
        nextButton.setItemMeta(nextButtonMeta);
        closeButton.setItemMeta(closeButtonMeta);
        clearSelectionButton.setItemMeta(clearSelectionButtonMeta);
        removeButton.setItemMeta(removeButtonMeta);

        gui.setItem(49, closeButton);
        gui.setItem(53, removeButton);
        gui.setItem(52, clearSelectionButton);

        if (pageIndex > 0) gui.setItem(48, previousButton);

        if ((pageIndex + 1) != maxPage) gui.setItem(50, nextButton);
    }
}
