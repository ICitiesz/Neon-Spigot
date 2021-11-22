package com.islandstudio.neon.Stable.New.features.GUI.Initialization;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class GUIConstructor implements InventoryHolder {
    protected Inventory inventory;
    protected GUIUtility guiUtility;

    public GUIConstructor(GUIUtility guiUtility) {
        this.guiUtility = guiUtility;
    }

    public abstract String getName();
    public abstract int getSlots();
    public abstract void setItems();
    public abstract void clickHandler(InventoryClickEvent e);

    public void open() {
        inventory = Bukkit.getServer().createInventory(this, getSlots(), getName());
        this.setItems();

        guiUtility.getOwner().openInventory(inventory);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
