package com.islandstudio.neon.stable.core.gui.state

import com.islandstudio.neon.stable.core.gui.structure.SortingOrder
import java.util.*

abstract class GUIState {
    protected open val stateData: EnumMap<GUIStateType, List<Any>> = EnumMap(GUIStateType::class.java)
    protected open val stateOptions: EnumMap<GUIStateType, GUIStateOption> = EnumMap(GUIStateType::class.java)
    protected open var stateActive: Boolean = false
    protected open var stateSortOrder: SortingOrder = SortingOrder.ACSENDING
    protected abstract var currentGUIStateType: GUIStateType

    /**
     * Get if the GUI state is still active
     *
     * @return
     */
    fun isStateActive(): Boolean = stateActive

    /**
     * Get current GUI state name
     *
     * @return
     */
    fun currentStateType(): GUIStateType = currentGUIStateType

    /**
     * Get current GUI state data
     *
     * @return
     */
    fun currentStateData(): List<Any> {
        return getStateData(currentGUIStateType)
    }

    /**
     * Get GUI state data by given GUI state name
     *
     * @param guiStateType
     * @return
     */
    fun getStateData(guiStateType: GUIStateType): List<Any> {
        return stateData[guiStateType]!!
    }

    /**
     * Get current GUI state option
     *
     * @return
     */
    fun currentStateOption(): GUIStateOption {
        return getStateOption(currentGUIStateType)
    }

    /**
     * Get GUI state option by given GUI state type
     *
     * @param guiStateType
     * @return
     */
    fun getStateOption(guiStateType: GUIStateType): GUIStateOption {
        return stateOptions[guiStateType]!!
    }

    /**
     * Initialize GUI state option by given GUI state name.
     *
     * @param guiStateType
     * @param maxItemPerPage
     */
    fun initStateOption(guiStateType: GUIStateType, maxItemPerPage: Int) {
        stateOptions.putIfAbsent(guiStateType, GUIStateOption(guiStateType, maxItemPerPage))
    }

    /**
     * Initialize GUI state data by given GUI state name and the data.
     *
     * @param guiStateName
     * @param guiStateData
     */
    fun initStateData(guiStateName: GUIStateType, guiStateData: List<Any>) {
        stateData.putIfAbsent(guiStateName, guiStateData)
    }

    /**
     * Keep the GUI state active or not acive
     *
     * @param keepStateActive
     */
    fun keepStateActive(keepStateActive: Boolean) {
        stateActive = keepStateActive
    }

    fun currentStateSortOrder(): SortingOrder = stateSortOrder

    /**
     * Select GUI state by given GUI state name
     *
     * @param guiStateType
     */
    fun selectState(guiStateType: GUIStateType) {
        currentGUIStateType = guiStateType
    }

    fun selectSortingOrder(sortingOrder: SortingOrder) {
        stateSortOrder = sortingOrder
    }

    fun matchesCurrentState(guiStateType: GUIStateType): Boolean = currentGUIStateType == guiStateType
}