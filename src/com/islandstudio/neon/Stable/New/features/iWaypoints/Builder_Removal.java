package com.islandstudio.neon.Stable.New.features.iWaypoints;

import com.islandstudio.neon.Stable.New.features.GUI.Initialization.GUIConstructor;
import com.islandstudio.neon.Stable.New.features.GUI.Initialization.GUIUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public abstract class Builder_Removal extends GUIConstructor {
    protected int pageIndex = 0;
    protected int max = 45;
    protected int itemIndex = 0;

    public Builder_Removal(GUIUtility guiUtility) {
        super(guiUtility);
    }

    public void addButtons() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Current Page: " + ChatColor.GREEN + (pageIndex + 1));

        ItemStack nextButton = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack previousButton = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemStack removeButton = new ItemStack(Material.BLAZE_POWDER);

        ItemMeta nextButtonMeta = nextButton.getItemMeta();
        ItemMeta previousButtonMeta = previousButton.getItemMeta();
        ItemMeta closeButtonMeta = closeButton.getItemMeta();
        ItemMeta removeButtonMeta = removeButton.getItemMeta();

        if (nextButtonMeta != null) {
            nextButtonMeta.setDisplayName(ChatColor.GOLD + "Next");
            nextButtonMeta.setLore(lore);
        }

        if (previousButtonMeta != null) {
            previousButtonMeta.setDisplayName(ChatColor.GOLD + "Previous");
            previousButtonMeta.setLore(lore);
        }

        if (closeButtonMeta != null) {
            closeButtonMeta.setDisplayName(ChatColor.RED + "Close");
        }

        if (removeButtonMeta != null) {
            removeButtonMeta.setDisplayName(ChatColor.RED + "Remove");
        }

        nextButton.setItemMeta(nextButtonMeta);
        previousButton.setItemMeta(previousButtonMeta);
        closeButton.setItemMeta(closeButtonMeta);
        removeButton.setItemMeta(removeButtonMeta);

        inventory.setItem(53, removeButton);
        inventory.setItem(50, nextButton);
        inventory.setItem(49, closeButton);
        inventory.setItem(48, previousButton);
    }
}
