package com.islandstudio.neon.stable.utils.nGUI

import com.islandstudio.neon.stable.core.init.NConstructor
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

abstract class NGUIConstructor(protected val nGUI: NGUI): InventoryHolder {
    private lateinit var inventory: Inventory

    abstract fun getGUIName(): String
    abstract fun getGUISlots(): Int
    abstract fun setGUIButtons()
    abstract fun setGUIClickHandler(e: InventoryClickEvent)

    fun openGUI() {
        inventory = NConstructor.plugin.server.createInventory(this, getGUISlots(), getGUIName())
        this.setGUIButtons()
        nGUI.getGUIOwner().openInventory(inventory)
    }

    override fun getInventory(): Inventory {
        return inventory
    }
}