package com.islandstudio.neon.stable.features.nServerFeatures

import com.islandstudio.neon.stable.core.gui.GUISession
import com.islandstudio.neon.stable.core.gui.state.GUIState
import com.islandstudio.neon.stable.core.gui.state.GUIStateType
import com.islandstudio.neon.stable.features.nServerFeatures.properties.ServerFeature
import org.bukkit.ChatColor

data class NServerFeaturesGUIState(private val guiSession: GUISession): GUIState() {
    enum class FeatureSortingType(val type: String) {
        DEFAULT("${ChatColor.WHITE}Default"),
        STABLE("${ChatColor.WHITE}Stable"),
        EXPERIMENTAL("${ChatColor.WHITE}Experimental")
    }

    override var currentGUIStateType: GUIStateType = GUIStateType.NSERVERFEATURES

    private var currentSelectedFeature: ServerFeature? = null
    private var currentFeatureSortingType: FeatureSortingType = FeatureSortingType.DEFAULT

    init {
        this.initStateOption(GUIStateType.NSERVERFEATURES, 45)
        this.initStateOption(GUIStateType.NSERVERFEATURES_MANAGE_OPTIONS, 45)
    }

    @Suppress("UNCHECKED_CAST")
    fun selectServerFeature(featureName: String): Boolean {
        (getStateData(GUIStateType.NSERVERFEATURES) as List<ServerFeature>)
            .find { it.featureName == featureName }
            ?.let {
                if (it.options.isEmpty()) return false

                currentSelectedFeature = it.copy().apply {
                    this.options.forEach { serverOption ->
                        val refOptionValue = it.options.find { refOption ->
                            refOption.optionName == serverOption.optionName
                        }!!.optionValue!!

                        this.setServerOptionValue(serverOption.optionName, refOptionValue)
                    }
                }

                return true
            }

        return false
    }

    fun deselectServerFeature() {
        this.currentSelectedFeature?.let { this.currentSelectedFeature = null }
    }

    fun getSelectedServerFeature(): ServerFeature? = currentSelectedFeature

    fun currentFeatureSortType(): FeatureSortingType = currentFeatureSortingType

    fun selectSortingType(featureSortingType: FeatureSortingType) {
        currentFeatureSortingType = featureSortingType
    }
}
