package com.islandstudio.neon.stable.utils.iGUI;

import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class IGUIConstructor implements InventoryHolder {
    protected Inventory gui;
    protected IGUI iGUI;

    public IGUIConstructor(IGUI iGUI) {
        this.iGUI = iGUI;
    }

    /**
     * Get the GUI name.
     *
     * @return The GUI name.
     */
    public abstract String getGUIName();

    /**
     * Get the GUI slots.
     *
     * @return The GUI slots.
     */
    public abstract int getGUISlots();

    /**
     * Set the GUI buttons.
     *
     */
    public abstract void setGUIButtons();

    /**
     * Set the GUI click handler.
     *
     * @param e The InventoryCLickEvent.
     */
    public abstract void setGUIClickHandler(InventoryClickEvent e);

    public void openGUI() {
        gui = IConstructor.getPlugin().getServer().createInventory(this, getGUISlots(), getGUIName());
        this.setGUIButtons();
        iGUI.getGUIOwner().openInventory(gui);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Inventory getInventory() {
        return gui;
    }
}
