package com.islandstudio.neon.experimental.gui.state

import com.islandstudio.neon.experimental.gui.GuiPagination

data class GuiState<T: GuiStateData>(
    private val stateData: T?,
    val maxItemPerPage: Int
): GuiPagination(maxItemPerPage) {
    private var stateActive: Boolean = false

    fun isStateActive(): Boolean = stateActive

    fun keepStateActive(value: Boolean) {
        stateActive = value
    }

    fun <T: GuiStateData> getStateData(): T {
        return stateData as T
    }
}