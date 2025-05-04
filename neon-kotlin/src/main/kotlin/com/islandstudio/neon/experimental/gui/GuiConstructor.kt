package com.islandstudio.neon.experimental.gui

import com.islandstudio.neon.Neon
import com.islandstudio.neon.experimental.gui.component.IGuiComponent
import com.islandstudio.neon.experimental.gui.state.GuiStateManager
import com.islandstudio.neon.experimental.gui.state.GuiStateName
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.koin.core.component.inject

abstract class GuiConstructor<T: IGuiComponent>(val player: Player): GuiStateManager(), IComponentInjector, InventoryHolder {
    private val neon by inject<Neon>()
    private lateinit var inventory: Inventory

    abstract val guiComponent: T

    abstract fun getGuiName(): String
    abstract fun getGuiSlotCount(): Int

    abstract fun renderGui()
    abstract fun setGuiClickHandler(e: InventoryClickEvent)

    fun openGui() {
        inventory = neon.server.createInventory(this, getGuiSlotCount(), getGuiName())
        renderGui()
        player.openInventory(inventory)
    }

    fun openGui(guiStateName: GuiStateName, resetPageIndex: Boolean) {
        //guiState.currentStateOption().resetConfirmation()
        setCurrentGuiState(guiStateName)

        if (resetPageIndex) {
            getCurrentGuiState().resetCurrentPageIndex()
        }

        openGui()
    }

    override fun getInventory(): Inventory = inventory
}