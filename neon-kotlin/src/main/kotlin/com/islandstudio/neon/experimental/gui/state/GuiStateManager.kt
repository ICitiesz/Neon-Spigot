package com.islandstudio.neon.experimental.gui.state

open class GuiStateManager {
    private lateinit var currentGuiState: Pair<GuiStateName, GuiState<*>>
    private val guiStates: HashMap<GuiStateName, GuiState<*>> = HashMap()

    fun <T: GuiStateData>withState(guiStateName: GuiStateName, guiStateData: T?, maxItemPerPage: Int) {
        guiStates.putIfAbsent(guiStateName, GuiState(guiStateData, maxItemPerPage))

        if (guiStates.size == 1) {
            currentGuiState = guiStates.entries.first().toPair()
        }
    }

    fun getGuiStateManager(): GuiStateManager {
        return this
    }

    fun getCurrentGuiState(): GuiState<*> {
        return currentGuiState.second
    }

    fun getCurrentGuiStateName(): GuiStateName {
        return currentGuiState.first
    }

    fun getGuiState(guiStateName: GuiStateName): GuiState<*>? {
        return guiStates[guiStateName]
    }

    fun setCurrentGuiState(guiStateName: GuiStateName) {
        guiStates.entries
            .find { x -> x.key == guiStateName }
            ?.let {
                currentGuiState = it.toPair()
            }
    }

    fun matchesGuiState(guiStateName: GuiStateName): Boolean {
        return getCurrentGuiStateName() == guiStateName
    }
}