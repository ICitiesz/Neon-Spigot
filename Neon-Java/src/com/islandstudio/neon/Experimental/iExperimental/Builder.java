package com.islandstudio.neon.Experimental.iExperimental;

import com.islandstudio.neon.Stable.New.features.GUI.Initialization.GUIConstructor;
import com.islandstudio.neon.Stable.New.features.GUI.Initialization.GUIUtility;
import com.islandstudio.neon.Stable.New.Utilities.INamespaceKeys;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public abstract class Builder extends GUIConstructor {
    protected int pageIndex = 0;
    protected int max = 45;
    protected int itemIndex = 0;

    /* Constructor for Builder class */
    public Builder(GUIUtility guiUtility) {
        super(guiUtility);
    }

    public void addButtons() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Current Page: " + ChatColor.GREEN + (pageIndex + 1));

        ItemStack nextButton = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack previousButton = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemStack applyButton = new ItemStack(Material.LEVER);

        ItemMeta nextButtonMeta = nextButton.getItemMeta();
        ItemMeta previousButtonMeta = previousButton.getItemMeta();
        ItemMeta closeButtonMeta = closeButton.getItemMeta();
        ItemMeta applyButtonMeta = applyButton.getItemMeta();

        if (nextButtonMeta == null || previousButtonMeta == null || closeButtonMeta == null || applyButtonMeta == null) return;

        nextButtonMeta.setDisplayName(ChatColor.GOLD + "Next");
        nextButtonMeta.setLore(lore);
        nextButtonMeta.getPersistentDataContainer().set(INamespaceKeys.NEON_BUTTON.getKey(), PersistentDataType.STRING, INamespaceKeys.NEON_BUTTON.getKey().toString());

        previousButtonMeta.setDisplayName(ChatColor.GOLD + "Previous");
        previousButtonMeta.setLore(lore);
        previousButtonMeta.getPersistentDataContainer().set(INamespaceKeys.NEON_BUTTON.getKey(), PersistentDataType.STRING, INamespaceKeys.NEON_BUTTON.getKey().toString());

        closeButtonMeta.setDisplayName(ChatColor.RED + "Close");
        closeButtonMeta.getPersistentDataContainer().set(INamespaceKeys.NEON_BUTTON.getKey(), PersistentDataType.STRING, INamespaceKeys.NEON_BUTTON.getKey().toString());

        applyButtonMeta.setDisplayName(ChatColor.RED + "Apply");
        applyButtonMeta.getPersistentDataContainer().set(INamespaceKeys.NEON_BUTTON.getKey(), PersistentDataType.STRING, INamespaceKeys.NEON_BUTTON.getKey().toString());

        nextButton.setItemMeta(nextButtonMeta);
        previousButton.setItemMeta(previousButtonMeta);
        closeButton.setItemMeta(closeButtonMeta);
        applyButton.setItemMeta(applyButtonMeta);

        inventory.setItem(53, applyButton);
        inventory.setItem(50, nextButton);
        inventory.setItem(49, closeButton);
        inventory.setItem(48, previousButton);
    }
}
