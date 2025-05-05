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

    /**
     * Total gui slot count
     *
     * @return
     */
    abstract fun getGuiSlotCount(): Int

    /**
     * Used to render gui with configured settings and components
     *
     */
    abstract fun renderGui()

    /**
     * Set the gui click handler
     *
     * @param e
     */
    abstract fun setGuiClickHandler(e: InventoryClickEvent)

    /**
     * Open GUI with default configuration.
     *
     */
    fun openGui() {
        inventory = neon.server.createInventory(this, getGuiSlotCount(), getGuiName())
        renderGui()
        player.openInventory(inventory)
    }

    /**
     * Open GUI with given GuiStateName. This is used to open other GUI withtin the same GUI handler.
     *
     * @param guiStateName Target GUI state name.
     * @param resetPageIndex Reset the page index.
     */
    fun openGui(guiStateName: GuiStateName, resetPageIndex: Boolean) {
        setCurrentGuiState(guiStateName)

        if (resetPageIndex) {
            getCurrentGuiState().resetCurrentPageIndex()
        }

        openGui()
    }

    /**
     * Open GUI with given GuiPageNavigation. Usually used to navigate page by page within the same GUI.
     *
     * @param guiPageNavigation
     */
    fun openGui(guiPageNavigation: GuiPageNavigation) {
        when(guiPageNavigation) {
            GuiPageNavigation.PreviousPage -> {
                if (getCurrentGuiState().getCurrentPageIndex() == 0) return

                getCurrentGuiState().currentPageIndexDecreament()
                getCurrentGuiState().keepStateActive(true)

                openGui()
            }

            GuiPageNavigation.NextPage -> {
                if (getCurrentGuiState().getCurrentPageIndex() >= getCurrentGuiState().getMaxPage()) return

                getCurrentGuiState().currentPageIndexIncreament()
                getCurrentGuiState().keepStateActive(true)

                openGui()
            }
        }
    }

    override fun getInventory(): Inventory = inventory
}