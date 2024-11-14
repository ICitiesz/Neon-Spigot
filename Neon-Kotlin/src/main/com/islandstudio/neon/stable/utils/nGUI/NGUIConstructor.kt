package com.islandstudio.neon.stable.utils.nGUI

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.koin.core.component.inject

abstract class NGUIConstructor(protected val nGUI: NGUI): InventoryHolder, ModuleInjector {
    private val neon by inject<Neon>()
    private lateinit var inventory: Inventory

    abstract fun getGUIName(): String
    abstract fun getGUISlots(): Int
    abstract fun setGUIButtons()
    abstract fun setGUIClickHandler(e: InventoryClickEvent)

    fun openGUI() {
        inventory = neon.server.createInventory(this, getGUISlots(), getGUIName())
        this.setGUIButtons()
        nGUI.getGUIOwner().openInventory(inventory)
    }

    override fun getInventory(): Inventory {
        return inventory
    }
}