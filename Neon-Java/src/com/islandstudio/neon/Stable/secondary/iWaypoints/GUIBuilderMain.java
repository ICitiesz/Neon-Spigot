package com.islandstudio.neon.stable.secondary.iWaypoints;

import com.islandstudio.neon.stable.utils.INamespaceKeys;
import com.islandstudio.neon.stable.utils.iGUI.IGUI;
import com.islandstudio.neon.stable.utils.iGUI.IGUIConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public abstract class GUIBuilderMain extends IGUIConstructor {
    protected int maxItemPerPage = 45;
    protected int maxPage = 1;
    protected int pageIndex = 0;
    protected int itemIndex = 0;

    /* Button display names */
    protected static final String previousButtonDisplayName = ChatColor.GOLD + "" + ChatColor.BOLD + "<<Previous";
    protected static final String nextButtonDisplayName = ChatColor.GOLD + "" + ChatColor.BOLD + "Next>>";
    protected static final String closeButtonDisplayName = ChatColor.RED + "" + ChatColor.BOLD + "Close";

    /* Button identifier key */
    protected static final NamespacedKey BUTTON_ID_KEY = INamespaceKeys.NEON_BUTTON.getKey();

    public GUIBuilderMain(IGUI iGUI) {
        super(iGUI);
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

        /* Button meta */
        ItemMeta previousButtonMeta = previousButton.getItemMeta();
        ItemMeta nextButtonMeta = nextButton.getItemMeta();
        ItemMeta closeButtonMeta = closeButton.getItemMeta();

        if (previousButtonMeta == null || nextButtonMeta == null || closeButtonMeta == null) return;

        previousButtonMeta.setDisplayName(previousButtonDisplayName);
        previousButtonMeta.setLore(navigationButtonLore);
        previousButtonMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

        nextButtonMeta.setDisplayName(nextButtonDisplayName);
        nextButtonMeta.setLore(navigationButtonLore);
        nextButtonMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

        closeButtonMeta.setDisplayName(closeButtonDisplayName);
        closeButtonMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

        previousButton.setItemMeta(previousButtonMeta);
        nextButton.setItemMeta(nextButtonMeta);
        closeButton.setItemMeta(closeButtonMeta);

        gui.setItem(49, closeButton);

        if (pageIndex > 0) gui.setItem(48, previousButton);

        if ((pageIndex + 1) != maxPage) gui.setItem(50, nextButton);
    }
}
