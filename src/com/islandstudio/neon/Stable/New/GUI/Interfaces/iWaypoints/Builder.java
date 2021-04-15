package com.islandstudio.neon.Stable.New.GUI.Interfaces.iWaypoints;

import com.islandstudio.neon.Stable.New.GUI.Initialization.GUIConstructor;
import com.islandstudio.neon.Stable.New.GUI.Initialization.GUIUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public abstract class Builder extends GUIConstructor {
    protected int pageIndex = 0;
    protected int max = 45;
    protected int itemIndex = 0;

    public Builder(GUIUtility guiUtility) {
        super(guiUtility);
    }

    public void addButtons() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Current Page: " + ChatColor.GREEN + (pageIndex + 1));

        ItemStack nextButton = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack previousButton = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack closeButton = new ItemStack(Material.BARRIER);

        ItemMeta nextButtonMeta = nextButton.getItemMeta();
        ItemMeta previousButtonMeta = previousButton.getItemMeta();
        ItemMeta closeButtonMeta = closeButton.getItemMeta();

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

        nextButton.setItemMeta(nextButtonMeta);
        previousButton.setItemMeta(previousButtonMeta);
        closeButton.setItemMeta(closeButtonMeta);

        inventory.setItem(50, nextButton);
        inventory.setItem(49, closeButton);
        inventory.setItem(48, previousButton);
    }
}
