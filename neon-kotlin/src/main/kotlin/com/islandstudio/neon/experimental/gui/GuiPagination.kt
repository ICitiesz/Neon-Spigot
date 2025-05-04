package com.islandstudio.neon.experimental.gui

import kotlin.math.ceil

abstract class GuiPagination(private val maxItemPerPage: Int) {
    private var guiItemSortOrder: GuiSortingOrder = GuiSortingOrder.ACSENDING
    private var guiMaxPage: Int = 1
    private var guiPageIndex: Int = 0
    private var guiItemIndex: Int = 0
    // TODO: guiConfirmStatus

    fun updateMaxPage(stateDataSize: Int) {
        guiMaxPage = ceil(
            stateDataSize.toDouble() / maxItemPerPage.toDouble()
        ).toInt()
    }

    fun updateCurrentPageIndex(newCurrentPageIndex: Int) {
        guiPageIndex = newCurrentPageIndex
    }

    fun updateItemIndex(itemIndex: Int) {
        guiItemIndex = maxItemPerPage * guiPageIndex + itemIndex
    }

    fun getCurrentPageIndex(): Int = guiPageIndex

    fun getMaxPage(): Int = guiMaxPage

    fun getItemIndex(): Int = guiItemIndex

    fun currentPageIndexIncreament(): Int = guiPageIndex++

    fun currentPageIndexDecreament(): Int = guiPageIndex--

    fun currentSortOrder(): GuiSortingOrder = guiItemSortOrder

    fun resetCurrentPageIndex() { guiPageIndex = 0 }

    fun setSortOrder(guiSortOrder: GuiSortingOrder) {
        guiItemSortOrder = guiSortOrder
    }
}
