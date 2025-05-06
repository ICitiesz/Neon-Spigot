package com.islandstudio.neon.features.neonfeature.gui

import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.experimental.gui.GuiConstructor
import com.islandstudio.neon.experimental.gui.GuiPageNavigation
import com.islandstudio.neon.experimental.gui.GuiSortingOrder
import com.islandstudio.neon.experimental.gui.component.GuiButton
import com.islandstudio.neon.experimental.gui.state.GuiStateName
import com.islandstudio.neon.features.neonfeature.NeonFeatureManager
import com.islandstudio.neon.features.neonfeature.gui.button.node.FeatureGuiButtonNode
import com.islandstudio.neon.features.neonfeature.gui.button.node.FeatureOptionGuiButtonNode
import com.islandstudio.neon.shared.core.config.component.ConfigDataRange
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.utils.TextUtil
import com.islandstudio.neon.shared.utils.data.DataType
import com.islandstudio.neon.shared.utils.data.DataUtil
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.koin.core.component.inject
import java.util.*

class NeonFeatureGui(player: Player): GuiConstructor<NeonFeatureGuiComponent>(player), IComponentInjector {
    private val neonFeatureManager by inject<NeonFeatureManager>()

    init {
        withState(GuiStateName.NeonFeatureMainGUI, NeonFeatureGuiStateData(neonFeatureManager), 45)
        withState(GuiStateName.NeonFeatureOptionManagementGUI, null, 45)
    }

    override val guiComponent: NeonFeatureGuiComponent = NeonFeatureGuiComponent(this)

    override fun getGuiName(): String {
        return when(getCurrentGuiStateName()) {
            GuiStateName.NeonFeatureMainGUI -> "Neon Feature Manager"
            GuiStateName.NeonFeatureOptionManagementGUI -> "Neon Feature Manager (Option)"
            else -> ""
        }
    }
    override fun getGuiSlotCount(): Int = 54

    override fun renderGui() {
        when(getCurrentGuiStateName()) {
            GuiStateName.NeonFeatureMainGUI -> {
                val currentStateData = getCurrentGuiState().getStateData<NeonFeatureGuiStateData>()
                val features = with(currentStateData.features) {
                    return@with this.filter { tempFeatureList ->
                        val featureName = currentStateData.getFeatureName(tempFeatureList)

                        return@filter when(currentStateData.getCurrentSortType()) {
                            NeonFeatureSortType.Default -> {
                                true
                            }

                            NeonFeatureSortType.Stable ->  {
                                !currentStateData.getFeatureExperimentalStatus(featureName)
                            }

                            NeonFeatureSortType.Experimental -> {
                                currentStateData.getFeatureExperimentalStatus(featureName)
                            }
                        }
                    }.toMutableList().also { filteredFeatureList ->
                        if (getCurrentGuiState().currentSortOrder() == GuiSortingOrder.ACSENDING) {
                            filteredFeatureList.sortBy { currentStateData.getFeatureName(it) }
                            return@also
                        }

                        filteredFeatureList.sortByDescending { currentStateData.getFeatureName(it) }
                    }
                }

                getCurrentGuiState().updateMaxPage(features.size)
                guiComponent.configureGuiComponent()

                for (i in 0 until getCurrentGuiState().maxItemPerPage) {
                    getCurrentGuiState().updateItemIndex(i)

                    if (getCurrentGuiState().getItemIndex() >= features.size) break

                    /* Neon feature properties */
                    val featureName = currentStateData.getFeatureName(features[getCurrentGuiState().getItemIndex()])
                    val featureToggleStatus = neonFeatureManager.getFeatureToggle(featureName, currentStateData.featureConfig)
                    val featureCommand = currentStateData.getFeatureCommand(featureName) ?: "None"
                    val featureDescription = currentStateData.getFeatureDescription(featureName)?.let {
                        TextUtil.sliceText(it, 7)
                    } ?: listOf("No description provided!").toCollection(LinkedList())

                    /* Configure feature details for the button labels */
                    val featureDetails: LinkedList<String> = LinkedList<String>().apply {
                        val toggleStatusInfo = if (featureToggleStatus)
                            "${ChatColor.GREEN}${ChatColor.BOLD}Enabled!"
                        else
                            "${ChatColor.RED}${ChatColor.BOLD}Disabled!"

                        add("${ChatColor.GRAY}Status: $toggleStatusInfo")
                        add("")
                        add("${ChatColor.GRAY}Description:")

                        featureDescription.forEach { add("${ChatColor.YELLOW}${it}") }

                        add("")
                        add("${ChatColor.GRAY}Command:")
                        add("${ChatColor.YELLOW}$featureCommand")
                        add("")
                        add("${ChatColor.WHITE}[${ChatColor.GOLD}L.Click${ChatColor.WHITE}] " +
                                "${ChatColor.LIGHT_PURPLE}Toggle Server Feature")

                        if (currentStateData.getFeatureOptions(featureName).isNotEmpty()) {
                            add("${ChatColor.WHITE}[${ChatColor.GOLD}Shift L.Click${ChatColor.WHITE}] " +
                                    "${ChatColor.LIGHT_PURPLE}Manage Options")
                        }
                    }

                    guiComponent.featureBtn.clone(buttonDisplayName = "${ChatColor.GOLD}${ChatColor.BOLD}${featureName}")
                        .configureButton {
                            withButtonLabels(featureDetails)
                            withCustomData(FeatureGuiButtonNode(featureName, currentStateData.getFeatureExperimentalStatus(featureName)))

                            if (featureToggleStatus) withButtonGlintEffect()

                            createButton()

                            inventory.addItem(getButtonItem())
                        }
                }
            }

            GuiStateName.NeonFeatureOptionManagementGUI -> {
                val mainGuiState = getGuiState(GuiStateName.NeonFeatureMainGUI) ?: return
                val mainGuiStateData = mainGuiState.getStateData<NeonFeatureGuiStateData>()
                val featureOptions = mainGuiStateData.getFeatureOptions(mainGuiStateData.getSelectedFeature()).run {
                    this.map { it.copy() }
                }

                getCurrentGuiState().updateMaxPage(featureOptions.size)
                guiComponent.configureGuiComponent()

                for (i in 0 until getCurrentGuiState().maxItemPerPage) {
                    getCurrentGuiState().updateItemIndex(i)

                    if (getCurrentGuiState().getItemIndex() >= featureOptions.size) break

                    /* Prepare feature option data */
                    val featureOption = featureOptions[getCurrentGuiState().getItemIndex()]
                    val featureOptionCurrentValue = featureOption.value()
                    val featureOptionDescription = mainGuiStateData.getFeatureOptionDescription(featureOption.key())?.let {
                        TextUtil.sliceText(it, 7)
                    } ?: listOf("No description provided!").toCollection(LinkedList())
                    val featureOptionDataType = featureOption.dataType()
                    val featureOptionDefaultValue = mainGuiStateData.getFeatureOptionDefaultValue(featureOption)
                    val featureOptionDataRange = mainGuiStateData.getFeatureOptionDataRange(featureOption)
                    var minScaleFactor: Any? = null
                    var maxScaleFactor: Any? = null

                    /* Configure feature option details for button labels */
                    val featureOptionDetails: LinkedList<String> = LinkedList<String>().apply {
                        add("${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$featureOptionCurrentValue")
                        add("")
                        add("${ChatColor.GRAY}Description:")

                        featureOptionDescription.forEach { add("${ChatColor.YELLOW}${it}") }

                        add("")
                        add("${ChatColor.GRAY}Data Type: ${net.md_5.bungee.api.ChatColor.of("#42c6ff")}${featureOptionDataType.typeName}")

                        if (featureOptionDataType == DataType.Boolean) {
                            if ((featureOptionDefaultValue as Boolean)) {
                                add("${ChatColor.GRAY}Default Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}${featureOptionDefaultValue}")
                            } else {
                                add("${ChatColor.GRAY}Default Value: ${net.md_5.bungee.api.ChatColor.of("#ff0022")}${featureOptionDefaultValue}")
                            }
                        } else {
                            add("${ChatColor.GRAY}Default Value: ${net.md_5.bungee.api.ChatColor.of("#42c6ff")}${featureOptionDefaultValue}")
                        }

                        featureOptionDataRange?.let {
                            if (it is ConfigDataRange.DataRangeBoolean) {
                                add("${ChatColor.GRAY}Value Range: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}${it.minValue}" +
                                        "${ChatColor.WHITE} | ${net.md_5.bungee.api.ChatColor.of("#ff0022")}${it.maxValue}")
                                return@let
                            }

                            add("${ChatColor.GRAY}Value Range: ${net.md_5.bungee.api.ChatColor.of("#42c6ff")}${it.minValue}" +
                                    "${ChatColor.WHITE} ̴ ${net.md_5.bungee.api.ChatColor.of("#42c6ff")}${it.maxValue}")
                        } ?: add("${ChatColor.GRAY}Value Range: ${net.md_5.bungee.api.ChatColor.of("#42c6ff")}Unknown")

                        add("")

                        /* Control/scale factor tooltips */
                        when(featureOptionDataType) {
                            DataType.Double -> {
                                minScaleFactor = DataUtil.getScaleFactorOfDouble(featureOptionDataRange?.minValue as Double)
                                maxScaleFactor = DataUtil.getScaleFactorOfDouble(featureOptionDataRange.minValue as Double, true)

                                add("${ChatColor.WHITE}[${ChatColor.GOLD}M.Click${ChatColor.WHITE}] ${ChatColor.LIGHT_PURPLE}Reset to Default")
                                add("${ChatColor.WHITE}[${ChatColor.GOLD}L.Click${ChatColor.WHITE}/${ChatColor.GOLD}R.Click${ChatColor.WHITE}] " +
                                        "${ChatColor.LIGHT_PURPLE}-${minScaleFactor}${ChatColor.WHITE}/${ChatColor.LIGHT_PURPLE}+${minScaleFactor}")

                                if (maxScaleFactor != 1.0) {
                                    add("${ChatColor.WHITE}[${ChatColor.GOLD}Shift L.Click${ChatColor.WHITE}/${ChatColor.GOLD}Shift R.Click${ChatColor.WHITE}] " +
                                            "${ChatColor.LIGHT_PURPLE}-${maxScaleFactor}${ChatColor.WHITE}/${ChatColor.LIGHT_PURPLE}+${maxScaleFactor}")
                                }
                            }

                            DataType.Long -> {
                                minScaleFactor = 1L
                                maxScaleFactor = 10L

                                this.add("${ChatColor.WHITE}[${ChatColor.GOLD}M.Click${ChatColor.WHITE}] ${ChatColor.LIGHT_PURPLE}Reset to Default")
                                this.add("${ChatColor.WHITE}[${ChatColor.GOLD}L.Click${ChatColor.WHITE}/${ChatColor.GOLD}R.Click${ChatColor.WHITE}] " +
                                        "${ChatColor.LIGHT_PURPLE}-${minScaleFactor}${ChatColor.WHITE}/${ChatColor.LIGHT_PURPLE}+${minScaleFactor}")
                                this.add("${ChatColor.WHITE}[${ChatColor.GOLD}Shift L.Click${ChatColor.WHITE}/${ChatColor.GOLD}Shift R.Click${ChatColor.WHITE}] " +
                                        "${ChatColor.LIGHT_PURPLE}-${maxScaleFactor}${ChatColor.WHITE}/${ChatColor.LIGHT_PURPLE}+${maxScaleFactor}")
                            }

                            DataType.Boolean -> {
                                this.add("${ChatColor.WHITE}[${ChatColor.GOLD}M.Click${ChatColor.WHITE}] ${ChatColor.LIGHT_PURPLE}Reset to Default")
                                this.add("${ChatColor.WHITE}[${ChatColor.GOLD}L.Click${ChatColor.WHITE}] ${ChatColor.LIGHT_PURPLE}true${ChatColor.WHITE}/${ChatColor.LIGHT_PURPLE}false")
                            }

                            else -> return@apply
                        }
                    }

                    guiComponent.featureOptionBtn.clone(buttonDisplayName ="${ChatColor.GOLD}${ChatColor.BOLD}${featureOption.key()}" )
                        .configureButton {
                            withButtonLabels(featureOptionDetails)
                            withCustomData(FeatureOptionGuiButtonNode(
                                featureOption.key(),
                                featureOptionCurrentValue,
                                minScaleFactor,
                                maxScaleFactor
                            ))
                            createButton()

                            inventory.addItem(getButtonItem())
                        }
                }
            }

            else -> return
        }
    }

    override fun setGuiClickHandler(e: InventoryClickEvent) {
        val mainGuiState = getGuiState(GuiStateName.NeonFeatureMainGUI) ?: return
        val mainGuiStateData = mainGuiState.getStateData<NeonFeatureGuiStateData>()
        val currentGuiState = getCurrentGuiState()
        val clickType = e.click
        val clickedGuiButton = e.currentItem ?: return
        val clickedGuiButtonMetaData = clickedGuiButton.itemMeta ?: return

        when(clickedGuiButton.type) {
            /* Feature Button */
            Material.BIRCH_SIGN -> {
                if (!matchesGuiState(GuiStateName.NeonFeatureMainGUI)) return

                if (!GuiButton.Companion.matchesButtonRefId(guiComponent.featureBtn, clickedGuiButtonMetaData)) return

                val featureGuiButtonNode = GuiButton.getCustomData<FeatureGuiButtonNode>(clickedGuiButtonMetaData) ?: return

                when(clickType) {
                    ClickType.LEFT -> {
                        val featureToggleStatus = neonFeatureManager.getFeatureToggle(featureGuiButtonNode.featureName, mainGuiStateData.featureConfig)
                        val featureDetails = clickedGuiButtonMetaData.lore?.toCollection(LinkedList()) ?: return

                        GuiButton.updateButtonGlintEffect(clickedGuiButtonMetaData, !featureToggleStatus)

                        if (!featureToggleStatus) {
                            featureDetails[0] = "${ChatColor.GRAY}Status: ${ChatColor.GREEN}${ChatColor.BOLD}Enabled!"
                        } else {
                            featureDetails[0] = "${ChatColor.GRAY}Status: ${ChatColor.RED}${ChatColor.BOLD}Disabled!"
                        }

                        neonFeatureManager.setFeatureToggle(mainGuiStateData.featureConfig, featureGuiButtonNode.featureName, !featureToggleStatus)

                        clickedGuiButtonMetaData.lore = featureDetails
                    }

                    ClickType.SHIFT_LEFT -> {
                        if (!currentGuiState.getStateData<NeonFeatureGuiStateData>().selectFeature(featureGuiButtonNode.featureName)) return

                        super.openGui(GuiStateName.NeonFeatureOptionManagementGUI, true)
                    }

                    else -> return
                }

                clickedGuiButton.itemMeta = clickedGuiButtonMetaData
            }

            /* Feature Option Button */
            Material.PAPER -> {
                if (!matchesGuiState(GuiStateName.NeonFeatureOptionManagementGUI)) return

                if (!GuiButton.matchesButtonRefId(guiComponent.featureOptionBtn, clickedGuiButtonMetaData)) return

                GuiButton.getCustomData<FeatureOptionGuiButtonNode>(clickedGuiButtonMetaData)?.let {
                    val featureOption = mainGuiStateData.getFeatureOptions(mainGuiStateData.getSelectedFeature())
                        .find { x -> x.key() == it.featureOptionName } ?: return
                    val featureOptionDetails = clickedGuiButtonMetaData.lore?.toCollection(LinkedList()) ?: return
                    val featureOptionDataRange = mainGuiStateData.getFeatureOptionDataRange(featureOption) ?: return

                    when(e.click) {
                        ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                            when(featureOption.dataType()) {
                                DataType.Boolean -> {
                                    val boolValue = !(it.featureOptionCurrentValue as Boolean)

                                    featureOption.updateConfigNodeValue(boolValue)
                                    it.featureOptionCurrentValue = boolValue
                                    featureOptionDetails[0] = "${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$boolValue"
                                }

                                DataType.Double -> {
                                    val minValue = featureOptionDataRange.minValue as Double
                                    val currentValue = it.featureOptionCurrentValue as Double
                                    val minScaleFactor = it.featureOptionMinScaleFactor as Double
                                    val maxScaleFactor = it.featureOptionMaxScaleFactor as Double
                                    val valueScaleFactor = if (e.click == ClickType.LEFT) minScaleFactor
                                    else {
                                        if (maxScaleFactor == 1.0) {
                                            return@let minScaleFactor
                                        }

                                        maxScaleFactor
                                    }

                                    with((currentValue - valueScaleFactor)) {
                                        val decremetedValue = DataUtil.roundOffDouble(
                                            this,
                                            DataUtil.getFloatingPointCount(featureOptionDataRange.minValue as Double)
                                        )

                                        if (decremetedValue <= minValue) return@with minValue

                                        decremetedValue
                                    }.also { x ->
                                        featureOption.updateConfigNodeValue(x)
                                        it.featureOptionCurrentValue = x
                                        featureOptionDetails[0] = "${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$x"
                                    }
                                }

                                DataType.Long -> {
                                    val minValue = featureOptionDataRange.minValue as Long
                                    val currentValue = it.featureOptionCurrentValue as Long
                                    val minScaleFactor = it.featureOptionMinScaleFactor as Long
                                    val maxScaleFactor = it.featureOptionMaxScaleFactor as Long
                                    val valueScaleFactor = if (e.click == ClickType.LEFT) minScaleFactor else maxScaleFactor

                                    with(currentValue - valueScaleFactor) {
                                        if (this <= minValue) return@with minValue

                                        this
                                    }.also { x ->
                                        featureOption.updateConfigNodeValue(x)
                                        it.featureOptionCurrentValue = x
                                        featureOptionDetails[0] = "${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$x"
                                    }
                                }

                                else -> return
                            }
                        }

                        ClickType.RIGHT, ClickType.SHIFT_RIGHT -> {
                            when(featureOption.dataType()) {
                                DataType.Double -> {
                                    val minValue = featureOptionDataRange.minValue as Double
                                    val maxValue = featureOptionDataRange.maxValue as Double
                                    val currentValue = it.featureOptionCurrentValue as Double
                                    val minScaleFactor = it.featureOptionMinScaleFactor as Double
                                    val maxScaleFactor = it.featureOptionMaxScaleFactor as Double
                                    val valueScaleFactor = if (e.click == ClickType.RIGHT) minScaleFactor
                                    else {
                                        if (maxScaleFactor == 1.0) {
                                            return@let minScaleFactor
                                        }

                                        maxScaleFactor
                                    }

                                    with(currentValue + valueScaleFactor) {
                                        val incrementedValue = DataUtil.roundOffDouble(
                                            this,
                                            DataUtil.getFloatingPointCount(minValue)
                                        )

                                        if (incrementedValue >= maxValue) return@with maxValue

                                        incrementedValue
                                    }.also { x ->
                                        featureOption.updateConfigNodeValue(x)
                                        it.featureOptionCurrentValue = x
                                        featureOptionDetails[0] = "${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$x"
                                    }
                                }

                                DataType.Long -> {
                                    val maxValue = featureOptionDataRange.maxValue as Long
                                    val currentValue = it.featureOptionCurrentValue as Long
                                    val minScaleFactor = it.featureOptionMinScaleFactor as Long
                                    val maxScaleFactor = it.featureOptionMaxScaleFactor as Long
                                    val valueScaleFactor = if (e.click == ClickType.RIGHT) minScaleFactor else maxScaleFactor

                                    with(currentValue + valueScaleFactor) {
                                        if (this >= maxValue) return@with maxValue

                                        this
                                    }.also { x ->
                                        featureOption.updateConfigNodeValue(x)
                                        it.featureOptionCurrentValue = x
                                        featureOptionDetails[0] = "${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$x"
                                    }
                                }

                                else -> return
                            }
                        }

                        ClickType.MIDDLE -> {
                            val featureOptionDefaultValue = mainGuiStateData.getFeatureOptionDefaultValue(featureOption)

                            featureOption.updateConfigNodeValue(featureOptionDefaultValue)
                            it.featureOptionCurrentValue = featureOptionDefaultValue
                            featureOptionDetails[0] = "${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$featureOptionDefaultValue"
                        }

                        else -> return
                    }

                    GuiButton.updateCustomData(clickedGuiButtonMetaData, it)
                    clickedGuiButtonMetaData.lore = featureOptionDetails
                    clickedGuiButton.itemMeta = clickedGuiButtonMetaData
                }
            }

            Material.SNOWBALL -> {
                when {
                    /* Back Button */
                    GuiButton.matchesButtonRefId(guiComponent.backBtn, clickedGuiButtonMetaData) -> {
                        if (!matchesGuiState(GuiStateName.NeonFeatureOptionManagementGUI)) return

                        mainGuiStateData.selectFeature(null)
                        super.openGui(GuiStateName.NeonFeatureMainGUI, false)
                    }

                    /* Save Button */
                    GuiButton.matchesButtonRefId(guiComponent.saveBtn, clickedGuiButtonMetaData) -> {
                        if (!matchesGuiState(GuiStateName.NeonFeatureOptionManagementGUI)) return

                        mainGuiStateData.getFeatureOptions(mainGuiStateData.getSelectedFeature())
                            .forEach { it.buildConfigNode(true) }

                        mainGuiStateData.selectFeature(null)
                        super<GuiConstructor>.openGui(GuiStateName.NeonFeatureMainGUI, false)
                    }

                    /* Apply Button */
                    GuiButton.matchesButtonRefId(guiComponent.applyBtn, clickedGuiButtonMetaData) -> {
                        if (!matchesGuiState(GuiStateName.NeonFeatureMainGUI)) return

                        GuiButton.onConfirmation(clickedGuiButtonMetaData) {
                            if (!it) return@onConfirmation

                            neonFeatureManager.saveFeatureChanges(mainGuiStateData)
                            player.closeInventory()
                            CommandSyntaxHandler.sendCommandSyntax(
                                player,
                                "${ChatColor.YELLOW}Neon Feature has been updated! Kindly reload/restart the server to reflect the changes!"
                            )
                        }

                        clickedGuiButton.itemMeta = clickedGuiButtonMetaData
                    }

                    /* Sort Type Button */
                    GuiButton.matchesButtonRefId(guiComponent.sortTypeBtn, clickedGuiButtonMetaData) -> {
                        if (!matchesGuiState(GuiStateName.NeonFeatureMainGUI)) return

                        val sortTypeIndex = mainGuiStateData.getCurrentSortType().ordinal

                        with(sortTypeIndex + 1) {
                            if (this >= 3) {
                                mainGuiStateData.setSortType(NeonFeatureSortType.Default)
                            } else {
                                mainGuiStateData.setSortType(NeonFeatureSortType.entries[this])
                            }

                            currentGuiState.resetCurrentPageIndex()
                            inventory.clear()
                            renderGui()
                        }
                    }

                    /* Sort Order Button */
                    GuiButton.matchesButtonRefId(guiComponent.sortOrderBtn, clickedGuiButtonMetaData) -> {
                        if (!matchesGuiState(GuiStateName.NeonFeatureMainGUI)) return

                        val sortOrderIndex = currentGuiState.currentSortOrder().ordinal

                        with(sortOrderIndex + 1) {
                            if (this >= 2) {
                                currentGuiState.setSortOrder(GuiSortingOrder.ACSENDING)
                            } else {
                                currentGuiState.setSortOrder(GuiSortingOrder.entries[this])
                            }

                            currentGuiState.resetCurrentPageIndex()
                            inventory.clear()
                            renderGui()
                        }
                    }

                    else -> return
                }
            }

            Material.SPECTRAL_ARROW -> {
                when {
                    GuiButton.matchesButtonRefId(guiComponent.previousPageBtn, clickedGuiButtonMetaData) -> {
                        super<GuiConstructor>.openGui(GuiPageNavigation.PreviousPage)
                    }

                    GuiButton.matchesButtonRefId(guiComponent.nextPageBtn, clickedGuiButtonMetaData) -> {
                        super<GuiConstructor>.openGui(GuiPageNavigation.NextPage)
                    }
                }
            }

            /* Close Button */
            Material.BARRIER -> {
                if (!GuiButton.Companion.matchesButtonRefId(guiComponent.closeBtn, clickedGuiButtonMetaData)) {
                    return
                }

                player.closeInventory()
            }

            else -> return
        }
    }

    override fun getInventory(): Inventory {
        return super.getInventory()
    }
}