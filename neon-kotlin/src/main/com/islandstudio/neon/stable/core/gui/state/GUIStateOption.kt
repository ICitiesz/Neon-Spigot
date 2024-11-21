package com.islandstudio.neon.stable.core.gui.state

import kotlin.math.ceil

data class GUIStateOption(val guiStateType: GUIStateType, val maxItemPerPage: Int) {
    private var guiMaxPage = 1
    private var guiPageIndex = 0
    private var guiItemIndex = 0
    private var guiConfirmStatus = 0

    fun getMaxPage(): Int = guiMaxPage

    fun getPageIndex(): Int = guiPageIndex

    fun getItemIndex(): Int = guiItemIndex

    fun updateMaxPage(stateDataSize: Int) {
        guiMaxPage = ceil(
            stateDataSize.toDouble() / maxItemPerPage.toDouble()
        ).toInt()
    }

    fun updateCurrentPageIndex(newCurrentPageIndex: Int) {
        guiPageIndex = newCurrentPageIndex
    }

    fun updateItemIndex(index: Int) {
        guiItemIndex = maxItemPerPage * guiPageIndex + index
    }

    fun currentPageIndexIncreament() = guiPageIndex++

    fun currentPageIndexDecreament() = guiPageIndex--

    fun resetCurrentPageIndex() { guiPageIndex = 0 }

    fun validationConfirmation(): Boolean {
        if (guiConfirmStatus == 1) {
            resetConfirmation()
            return true
        }

        guiConfirmStatus++
        return false
    }

    fun getConfirmationStatus(): Int = guiConfirmStatus

    fun resetConfirmation(): Boolean {
        if (guiConfirmStatus == 0) return false

        guiConfirmStatus = 0
        return true
    }
}
