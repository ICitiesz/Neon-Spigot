package com.islandstudio.neon.stable.primary.iExperimental;

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

public abstract class GUIBuilder extends IGUIConstructor {
    protected int maxItemPerPage = 45;
    protected int maxPage = 1;
    protected int pageIndex = 0;
    protected int itemIndex = 0;

    /* Button display names */
    protected static final String PREVIOUS_BUTTON_DISPLAY_NAME = ChatColor.GOLD + "" + ChatColor.BOLD + "<<Previous";
    protected static final String NEXT_BUTTON_DISPLAY_NAME = ChatColor.GOLD + "" + ChatColor.BOLD + "Next>>";
    protected static final String CLOSE_BUTTON_DISPLAY_NAME = ChatColor.RED + "" + ChatColor.BOLD + "Close";
    protected static final String APPLY_BUTTON_DISPLAY_NAME = ChatColor.RED + "" + ChatColor.BOLD + "Apply Changes";

    /* Button identifier key */
    protected static final NamespacedKey BUTTON_ID_KEY = INamespaceKeys.NEON_BUTTON.getKey();

    /* Button highlighter */
    protected static final ButtonHighlighter BUTTON_HIGHLIGHTER = new ButtonHighlighter(INamespaceKeys.NEON_BUTTON_HIGHLIGHTER.getKey());

    public GUIBuilder(IGUI IGUI) {
        super(IGUI);
    }

    public void addGUIButtons() {
        /* Button lore */
        List<String> navigationButtonLore = Arrays.asList(
                ChatColor.GRAY + "Current Page:", ChatColor.GREEN + "" + (pageIndex + 1)
                        + ChatColor.WHITE + " of " + ChatColor.GREEN + maxPage);

        List<String> applyButtonLore = Collections.singletonList(ChatColor.YELLOW + "Server reload are required!");

        /* Button item */
        ItemStack nextButton = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack previousButton = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemStack applyButton = new ItemStack(Material.LEVER);

        /* Button meta */
        ItemMeta nextButtonMeta = nextButton.getItemMeta();
        ItemMeta previousButtonMeta = previousButton.getItemMeta();
        ItemMeta closeButtonMeta = closeButton.getItemMeta();
        ItemMeta applyButtonMeta = applyButton.getItemMeta();

        if (nextButtonMeta == null || previousButtonMeta == null || closeButtonMeta == null || applyButtonMeta == null) return;

        nextButtonMeta.setDisplayName(NEXT_BUTTON_DISPLAY_NAME);
        nextButtonMeta.setLore(navigationButtonLore);
        nextButtonMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

        previousButtonMeta.setDisplayName(PREVIOUS_BUTTON_DISPLAY_NAME);
        previousButtonMeta.setLore(navigationButtonLore);
        previousButtonMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

        closeButtonMeta.setDisplayName(CLOSE_BUTTON_DISPLAY_NAME);
        closeButtonMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

        applyButtonMeta.setDisplayName(APPLY_BUTTON_DISPLAY_NAME);
        applyButtonMeta.setLore(applyButtonLore);
        applyButtonMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

        nextButton.setItemMeta(nextButtonMeta);
        previousButton.setItemMeta(previousButtonMeta);
        closeButton.setItemMeta(closeButtonMeta);
        applyButton.setItemMeta(applyButtonMeta);

        gui.setItem(53, applyButton);
        gui.setItem(49, closeButton);

        if (pageIndex > 0) gui.setItem(48, previousButton);

        if ((pageIndex + 1) != maxPage) gui.setItem(50, nextButton);
    }
}
