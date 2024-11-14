package com.islandstudio.neon.stable.core.gui.structure

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.gui.GUISession
import com.islandstudio.neon.stable.core.gui.state.GUIState
import com.islandstudio.neon.stable.core.gui.state.GUIStateType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.koin.core.component.inject

abstract class GUIConstructor(private val guiSession: GUISession): InventoryHolder, ModuleInjector {
    private val neon by inject<Neon>()
    private lateinit var inventory: Inventory
    abstract val guiState: GUIState

    abstract fun getGUIName(): String
    abstract fun getGUISlots(): Int
    abstract fun setGUIButtons()
    abstract fun setGUIClickHandler(e: InventoryClickEvent)

    /**
     * Open GUI with default configuration.
     *
     */
    fun openGUI() {
        inventory = neon.server.createInventory(this, getGUISlots(), getGUIName())
        setGUIButtons()
        guiSession.guiHolder.openInventory(inventory)
    }

    /**
     * Open GUI with given GUIStateName. This is used to open other GUI withtin the same GUI handler.
     *
     * @param guiStateName Target GUI state name.
     * @param resetPageIndex Reset the page index.
     */
    fun openGUI(guiStateName: GUIStateType, resetPageIndex: Boolean) {
        guiState.currentStateOption().resetConfirmation()
        guiState.selectState(guiStateName)
        if (resetPageIndex) guiState.currentStateOption().resetCurrentPageIndex()
        openGUI()
    }

    /**
     * Open GUI with given GUIPageNavType. This is used to navigate page by page within the same GUI.
     *
     * @param guiPageNavType
     */
    fun openGUI(guiPageNavType: GUIPageNavType) {
        when (guiPageNavType) {
            GUIPageNavType.PREVIOUS_PAGE -> {
                if (guiState.currentStateOption().getPageIndex() == 0) return

                guiState.currentStateOption().currentPageIndexDecreament()
                guiState.keepStateActive(true)

                openGUI()
            }

            GUIPageNavType.NEXT_PAGE -> {
                if ((guiState.currentStateOption().getItemIndex() + 1) >= guiState.currentStateData().size) return

                guiState.currentStateOption().currentPageIndexIncreament()
                guiState.keepStateActive(true)

                openGUI()
            }
        }
    }

    override fun getInventory(): Inventory = inventory
}