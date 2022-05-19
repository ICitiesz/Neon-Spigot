package com.islandstudio.neon.stable.utils.nGUI

import com.islandstudio.neon.Neon
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin

abstract class NGUIConstructor(protected val nGUI: NGUI): InventoryHolder {
    private val plugin: Plugin = getPlugin(Neon::class.java)

    private lateinit var inventory: Inventory

    abstract fun getGUIName(): String
    abstract fun getGUISlots(): Int
    abstract fun setItems()
    abstract fun guiClickHandler(e: InventoryClickEvent)

    fun openGUI() {
        inventory = plugin.server.createInventory(this, getGUISlots(), getGUIName())
        this.setItems()
        nGUI.getGUIOwner().openInventory(inventory)
    }

    override fun getInventory(): Inventory {
        return inventory
    }
}