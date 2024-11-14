package com.islandstudio.neon.stable.features.nServerFeatures

import com.islandstudio.neon.stable.core.command.CommandInterfaceProcessor
import com.islandstudio.neon.stable.core.gui.GUISession
import com.islandstudio.neon.stable.core.gui.state.GUIStateType
import com.islandstudio.neon.stable.core.gui.structure.GUIButton
import com.islandstudio.neon.stable.core.gui.structure.GUIPageNavType
import com.islandstudio.neon.stable.core.gui.structure.SortingOrder
import com.islandstudio.neon.stable.core.io.DataSourceType
import com.islandstudio.neon.stable.features.nServerFeatures.properties.ServerFeature
import com.islandstudio.neon.stable.utils.processing.TextProcessor
import com.islandstudio.neon.stable.utils.processing.properties.DataTypes
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.*

open class GUIHandler(guiSession: GUISession): GUIBuilder(guiSession) {
    private val player: Player = guiSession.guiHolder
    private val serverFeatureSession = NServerFeaturesRemastered.serverFeatureSession

    @Suppress("UNCHECKED_CAST")
    override val guiState = NServerFeaturesGUIState(guiSession).apply {
        val serverFeatures = (serverFeatureSession.getServerFeatureList(DataSourceType.EXTERNAL_SOURCE).clone() as ArrayList<ServerFeature>)
            .apply {
                this.replaceAll {
                    val serverFeatureClone = it.copy()
                    val refServerFeature = this.find { ref -> ref.featureName == serverFeatureClone.featureName }!!

                    serverFeatureClone.isEnabled = refServerFeature.isEnabled
                    serverFeatureClone.options.forEach { serverOptionClone ->
                        val refServerOptionValue = refServerFeature.options.find { ref ->
                            ref.optionName == serverOptionClone.optionName
                        }!!.optionValue!!

                        serverFeatureClone.setServerOptionValue(serverOptionClone.optionName, refServerOptionValue)
                    }

                    serverFeatureClone
                }
        }

        this.initStateData(GUIStateType.NSERVERFEATURES, serverFeatures)
    }

    override fun getGUIName(): String {
        return when(guiState.currentStateType()) {
            GUIStateType.NSERVERFEATURES -> {
                "nServerFeaturesRemastered"
            }

            GUIStateType.NSERVERFEATURES_MANAGE_OPTIONS -> {
                "Manage Options"
            }

            else -> { "" }
        }
    }

    override fun getGUISlots(): Int = 54

    @Suppress("UNCHECKED_CAST")
    override fun setGUIButtons() {
        val guiStateOption = guiState.currentStateOption()

        when(guiState.currentStateType()) {
            GUIStateType.NSERVERFEATURES -> {
                @Suppress("UNCHECKED_CAST")
                val serverFeatures = with(guiState.currentStateData() as List<ServerFeature>) {
                    when(guiState.currentFeatureSortType()) {
                        NServerFeaturesGUIState.FeatureSortingType.DEFAULT -> {
                            if (guiState.currentStateSortOrder() == SortingOrder.DESCENDING) {
                                return@with this.sortedByDescending { it.featureName }
                            }

                            return@with this.sortedBy { it.featureName }
                        }

                        NServerFeaturesGUIState.FeatureSortingType.STABLE -> {
                            this.filter {
                                !serverFeatureSession.isExperimental(it.featureName)
                            }.apply {
                                if (guiState.currentStateSortOrder() == SortingOrder.DESCENDING) {
                                    return@with this.sortedByDescending { it.featureName }
                                }

                                return@with this.sortedBy { it.featureName }
                            }
                        }

                        NServerFeaturesGUIState.FeatureSortingType.EXPERIMENTAL -> {
                            this.filter {
                                serverFeatureSession.isExperimental(it.featureName)
                            }.apply {
                                if (guiState.currentStateSortOrder() == SortingOrder.DESCENDING) {
                                    return@with this.sortedByDescending { it.featureName }
                                }

                                return@with this.sortedBy { it.featureName }
                            }
                        }
                    }
                }

                guiStateOption.updateMaxPage(serverFeatures.size)
                addNavigationButtons()

                for (i in 0 until guiStateOption.maxItemPerPage) {
                    guiStateOption.updateItemIndex(i)

                    if (guiStateOption.getItemIndex() >= serverFeatures.size) break

                    /* Server feature properties */
                    val feature = serverFeatures[guiStateOption.getItemIndex()]
                    val featureData: HashMap<String, Any> = hashMapOf("featureName" to feature.featureName)
                    val featureToggleStatus = feature.isEnabled!!.also {
                        featureData["featureToggleStatus"] = it
                    }

                    val featureDetails: LinkedList<String> = LinkedList<String>().apply {
                        val toggleStatus = with(featureToggleStatus) {
                            if (this) return@with "${ChatColor.GREEN}${ChatColor.BOLD}Enabled!"

                            return@with "${ChatColor.RED}${ChatColor.BOLD}Disabled!"
                        }
                        val featureDescription = with(serverFeatureSession
                            .getServerFeatureDescription(feature.featureName, false)) {
                            if (this == null) return@with listOf("None").toCollection(LinkedList())

                            TextProcessor.sliceText(this, 7)
                        }
                        val featureCommand = serverFeatureSession
                            .getServerFeatureCommand(feature.featureName, false) ?: "None"

                        this.add("${ChatColor.GRAY}Status: $toggleStatus")
                        this.add("")
                        this.add("${ChatColor.GRAY}Description:")

                        featureDescription.forEach {
                            this.add("${ChatColor.YELLOW}${it}")
                        }

                        this.add("")
                        this.add("${ChatColor.GRAY}Command:")
                        this.add("${ChatColor.YELLOW}$featureCommand")
                        this.add("")
                        this.add("${ChatColor.WHITE}[${ChatColor.GOLD}L.Click${ChatColor.WHITE}] " +
                                "${ChatColor.LIGHT_PURPLE}Toggle Server Feature")

                        if (feature.options.isNotEmpty()) {
                            this.add("${ChatColor.WHITE}[${ChatColor.GOLD}Shift L.Click${ChatColor.WHITE}] " +
                                    "${ChatColor.LIGHT_PURPLE}Manage Options")
                        }
                    }

                    FEATURE_BTN.clone(buttonName = "${ChatColor.GOLD}${ChatColor.BOLD}${feature.featureName}")
                        .initButtonMeta(featureDetails, featureToggleStatus)
                        .initDataContainer(featureData)
                        .createButton().run {
                            inventory.addItem(this)
                        }
                }
            }

            GUIStateType.NSERVERFEATURES_MANAGE_OPTIONS -> {
                val selectedServerFeature = guiState.getSelectedServerFeature()!!
                val serverFeatureOption = selectedServerFeature.options

                guiStateOption.updateMaxPage(serverFeatureOption.size)
                addNavigationButtons()

                for (i in 0 until guiStateOption.maxItemPerPage) {
                    guiStateOption.updateItemIndex(i)

                    if (guiStateOption.getItemIndex() >= serverFeatureOption.size) break

                    val featureOption = serverFeatureOption[guiStateOption.getItemIndex()]

                    val featureOptionData = hashMapOf(
                        "optionName" to featureOption.optionName,
                        "optionCurrentValue" to featureOption.optionValue
                    )

                    val featureOptionDetail: LinkedList<String> = LinkedList<String>().apply {
                        val featureOptionDescription = serverFeatureSession.getOptionDescription(
                            selectedServerFeature.featureName,
                            featureOption.optionName
                        )!!.run featureOptionDesc@ {
                            return@featureOptionDesc TextProcessor.sliceText(this, 7)
                        }

                        val optionDataType = serverFeatureSession.getOptionDataType(selectedServerFeature.featureName, featureOption.optionName)!!
                        val optionMinValue = serverFeatureSession.getOptionMinValue(selectedServerFeature.featureName, featureOption.optionName)
                        val optionMaxValue = serverFeatureSession.getOptionMaxValue(selectedServerFeature.featureName, featureOption.optionName)
                        val optionDefaultValue = serverFeatureSession.getOptionDefaultValue(selectedServerFeature.featureName, featureOption.optionName)
                        val optionCurrentValue = featureOption.optionValue

                        this.add("${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$optionCurrentValue")
                        this.add("")
                        this.add("${ChatColor.GRAY}Description:")

                        featureOptionDescription.forEach {
                            this.add("${ChatColor.YELLOW}${it}")
                        }

                        this.add("")
                        this.add("${ChatColor.GRAY}Data Type: ${net.md_5.bungee.api.ChatColor.of("#42c6ff")}${optionDataType}")
                        this.add("${ChatColor.GRAY}Default Value: ${net.md_5.bungee.api.ChatColor.of("#42c6ff")}$optionDefaultValue")

                        if (optionMinValue != null && optionMaxValue != null) {
                            this.add("${ChatColor.GRAY}Value Range: ${net.md_5.bungee.api.ChatColor.of("#42c6ff")}${optionMinValue}" +
                                    "${ChatColor.WHITE} ̴ ${net.md_5.bungee.api.ChatColor.of("#42c6ff")}${optionMaxValue}")
                        }

                        this.add("")

                        when(DataTypes.valueOf(optionDataType.uppercase())) {
                            DataTypes.DOUBLE -> {
                                val doubleModifier = TextProcessor.getDoubleModifier(optionMinValue as Double).also { incDouble ->
                                    featureOptionData["doubleModifier"] = incDouble
                                }
                                val doubleModifierCeil = TextProcessor.getDoubleModifier(optionMinValue, true).also { incDoubleCeil ->
                                    featureOptionData["doubleModifierCeil"] = incDoubleCeil
                                }

                                this.add("${ChatColor.WHITE}[${ChatColor.GOLD}M.Click${ChatColor.WHITE}] ${ChatColor.LIGHT_PURPLE}Reset to Default")
                                this.add("${ChatColor.WHITE}[${ChatColor.GOLD}L.Click${ChatColor.WHITE}/${ChatColor.GOLD}R.Click${ChatColor.WHITE}] " +
                                        "${ChatColor.LIGHT_PURPLE}-${doubleModifier}${ChatColor.WHITE}/${ChatColor.LIGHT_PURPLE}+${doubleModifier}")

                                if (doubleModifierCeil != 1.0) {
                                    this.add("${ChatColor.WHITE}[${ChatColor.GOLD}Shift L.Click${ChatColor.WHITE}/${ChatColor.GOLD}Shift R.Click${ChatColor.WHITE}] " +
                                            "${ChatColor.LIGHT_PURPLE}-${doubleModifierCeil}${ChatColor.WHITE}/${ChatColor.LIGHT_PURPLE}+${doubleModifierCeil}")
                                }
                            }

                            DataTypes.INTEGER -> {
                                this.add("${ChatColor.WHITE}[${ChatColor.GOLD}M.Click${ChatColor.WHITE}] ${ChatColor.LIGHT_PURPLE}Reset to Default")
                                this.add("${ChatColor.WHITE}[${ChatColor.GOLD}L.Click${ChatColor.WHITE}/${ChatColor.GOLD}R.Click${ChatColor.WHITE}] " +
                                        "${ChatColor.LIGHT_PURPLE}-1${ChatColor.WHITE}/${ChatColor.LIGHT_PURPLE}+1")

                                this.add("${ChatColor.WHITE}[${ChatColor.GOLD}Shift L.Click${ChatColor.WHITE}/${ChatColor.GOLD}Shift R.Click${ChatColor.WHITE}] " +
                                        "${ChatColor.LIGHT_PURPLE}-10${ChatColor.WHITE}/${ChatColor.LIGHT_PURPLE}+10")
                            }

                            DataTypes.BOOLEAN -> {
                                this.add("${ChatColor.WHITE}[${ChatColor.GOLD}M.Click${ChatColor.WHITE}] ${ChatColor.LIGHT_PURPLE}Reset to Default")
                                this.add("${ChatColor.WHITE}[${ChatColor.GOLD}L.Click${ChatColor.WHITE}] ${ChatColor.LIGHT_PURPLE}true${ChatColor.WHITE}/${ChatColor.LIGHT_PURPLE}false")
                            }

                            else -> {}
                        }

                    }

                    FEATURE_OPTION_BTN.clone(buttonName = "${ChatColor.GOLD}${ChatColor.BOLD}${featureOption.optionName}")
                        .initButtonMeta(featureOptionDetail)
                        .initDataContainer(featureOptionData as HashMap<String, Any>)
                        .createButton().run {
                            inventory.addItem(this)
                        }
                }
            }

            else -> {
                return
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun setGUIClickHandler(e: InventoryClickEvent) {
        val clickedItem = e.currentItem ?: return
        val clickedItemMeta = clickedItem.itemMeta ?: return
        val currentStateOption = guiState.currentStateOption()

        when(clickedItem.type) {
            /* Feature Button */
            Material.BIRCH_SIGN -> {
                if (!guiState.matchesCurrentState(GUIStateType.NSERVERFEATURES)) return

                if (!FEATURE_BTN.matchesButtonType(clickedItemMeta)) return

                GUIButton.Handler.getDataContainer(clickedItemMeta)?.let {
                    val featureName = it["featureName"] as String
                    val featureToggleStatus = it["featureToggleStatus"] as Boolean

                    when(e.click) {
                        ClickType.LEFT -> {
                            val serverFeatures = guiState.currentStateData() as List<ServerFeature>
                            val featureDetail: LinkedList<String> = clickedItemMeta.lore?.toCollection(LinkedList()) ?: return

                            with(featureToggleStatus) {
                                return@with !this
                            }.apply {
                                val serverFeature = serverFeatures.find { feature -> feature.featureName == featureName }!!

                                it.replace("featureToggleStatus", this)
                                GUIButton.Handler.updateIsGlint(clickedItemMeta, this)

                                if (this) {
                                    featureDetail[0] = "${ChatColor.GRAY}Status: ${ChatColor.GREEN}${ChatColor.BOLD}Enabled!"
                                } else {
                                    featureDetail[0] = "${ChatColor.GRAY}Status: ${ChatColor.RED}${ChatColor.BOLD}Disabled!"
                                }

                                serverFeature.isEnabled = this
                            }

                            GUIButton.Handler.updateDataContainer(clickedItemMeta, it)
                            clickedItemMeta.lore = featureDetail
                            clickedItem.itemMeta = clickedItemMeta
                        }

                        ClickType.SHIFT_LEFT -> {
                            if (!guiState.selectServerFeature(featureName)) return

                            super.openGUI(GUIStateType.NSERVERFEATURES_MANAGE_OPTIONS, true)
                        }

                        else -> { return }
                    }
                } ?: return

                if (guiState.currentStateOption().resetConfirmation()) {
                    inventory.clear()
                    setGUIButtons()
                }
            }

            /* Feature Option Button */
            Material.PAPER -> {
                if (!guiState.matchesCurrentState(GUIStateType.NSERVERFEATURES_MANAGE_OPTIONS)) return

                if (!FEATURE_OPTION_BTN.matchesButtonType(clickedItemMeta)) return

                GUIButton.Handler.getDataContainer(clickedItemMeta)?.let {
                    val selectedServerFeature = guiState.getSelectedServerFeature()!!
                    val featureOptionDetail: LinkedList<String> = clickedItemMeta.lore?.toCollection(LinkedList()) ?: return

                    val optionName = it["optionName"] as String
                    val optionCurrentValue = it["optionCurrentValue"]
                    val doubleModifier = it["doubleModifier"]?.let incDouble@ { incDouble -> incDouble as Double } ?: 0.0
                    val doubleModifierCeil = it["doubleModifierCeil"]?.let incDoubleCeil@ { incDoubleCeil -> incDoubleCeil as Double } ?: 0.0
                    val optionDataType = DataTypes.valueOf(serverFeatureSession.getOptionDataType(selectedServerFeature.featureName, optionName)!!.uppercase())
                    val optionDefaultValue = serverFeatureSession.getOptionDefaultValue(selectedServerFeature.featureName, optionName)
                    val optionMinValue = serverFeatureSession.getOptionMinValue(selectedServerFeature.featureName, optionName)
                    val optionMaxValue = serverFeatureSession.getOptionMaxValue(selectedServerFeature.featureName, optionName)

                    when(e.click) {
                        ClickType.LEFT, ClickType.SHIFT_LEFT -> {
                            when (optionDataType) {
                                DataTypes.BOOLEAN -> {
                                    with(optionCurrentValue as Boolean) {
                                        return@with !this
                                    }.apply {
                                        selectedServerFeature.setServerOptionValue(optionName, this)
                                        it.replace("optionCurrentValue", this)
                                        featureOptionDetail[0] = "${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$this"
                                    }
                                }

                                DataTypes.DOUBLE -> {
                                    val valueModifier = if (e.click == ClickType.LEFT) doubleModifier
                                    else {
                                        doubleModifierCeil.run {
                                            if (this == 1.0) return@run doubleModifier

                                            doubleModifierCeil
                                        }
                                    }

                                    with( (optionCurrentValue as Double) - valueModifier) {
                                        val decrementedValue = TextProcessor.roundOfDouble(this, TextProcessor.getFloatingPointCount(optionMinValue as Double))

                                        if (decrementedValue <= optionMinValue) return@with optionMinValue

                                        decrementedValue
                                    }.apply {
                                        selectedServerFeature.setServerOptionValue(optionName, this)
                                        it.replace("optionCurrentValue", this)
                                        featureOptionDetail[0] = "${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$this"
                                    }
                                }

                                DataTypes.INTEGER -> {
                                    val valueModifier = if (e.click == ClickType.LEFT) 1 else 10

                                    with((optionCurrentValue as Int) - valueModifier) {
                                        if (this <= optionMinValue as Int) return@with optionMinValue

                                        this
                                    }.apply {
                                        selectedServerFeature.setServerOptionValue(optionName, this)
                                        it.replace("optionCurrentValue", this)
                                        featureOptionDetail[0] = "${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$this"
                                    }
                                }

                                else -> { return }
                            }
                        }

                        ClickType.RIGHT, ClickType.SHIFT_RIGHT -> {
                            when(optionDataType) {
                                DataTypes.DOUBLE -> {
                                    val valueModifier = if (e.click == ClickType.RIGHT) doubleModifier
                                    else {
                                        doubleModifierCeil.run {
                                            if (this == 1.0) return@run doubleModifier

                                            doubleModifierCeil
                                        }
                                    }

                                    with((optionCurrentValue as Double) + valueModifier) {
                                        val incrementedValue = TextProcessor.roundOfDouble(this, TextProcessor.getFloatingPointCount(optionMinValue as Double))

                                        if (incrementedValue >= optionMaxValue as Double) return@with optionMaxValue

                                        incrementedValue
                                    }.apply {
                                        selectedServerFeature.setServerOptionValue(optionName, this)
                                        it.replace("optionCurrentValue", this)
                                        featureOptionDetail[0] = "${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$this"
                                    }
                                }

                                DataTypes.INTEGER -> {
                                    val valueModifier = if (e.click == ClickType.RIGHT) 1 else 10

                                    with((optionCurrentValue as Int) + valueModifier) {
                                        if (this >= optionMaxValue as Int) {
                                            return@with optionMaxValue
                                        }

                                        this
                                    }.apply {
                                        selectedServerFeature.setServerOptionValue(optionName, this)
                                        it.replace("optionCurrentValue", this)
                                        featureOptionDetail[0] = "${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$this"
                                    }
                                }

                                else -> { return }
                            }
                        }

                        ClickType.MIDDLE -> {
                            selectedServerFeature.setServerOptionValue(optionName, optionDefaultValue!!)
                            it.replace("optionCurrentValue", optionDefaultValue)
                            featureOptionDetail[0] = "${ChatColor.GRAY}Current Value: ${net.md_5.bungee.api.ChatColor.of("#3dff6a")}$optionDefaultValue"
                        }

                        else -> { return }
                    }

                    GUIButton.Handler.updateDataContainer(clickedItemMeta, it)
                    clickedItemMeta.lore = featureOptionDetail
                    clickedItem.itemMeta = clickedItemMeta
                } ?: return
            }

            /* Navigation Button */
            Material.SPECTRAL_ARROW -> {
                if (!GUIButton.Handler.hasButtonType(clickedItemMeta)) return

                if (guiState.matchesCurrentState(GUIStateType.NSERVERFEATURES)) {
                    if (guiState.currentStateOption().resetConfirmation()) {
                        setGUIButtons()
                    }
                }

                when (GUIButton.Handler.getClickedButtonType(clickedItemMeta)) {
                    PREVIOUS_BTN.buttonType -> {
                        super.openGUI(GUIPageNavType.PREVIOUS_PAGE)
                    }

                    NEXT_BTN.buttonType -> {
                        super.openGUI(GUIPageNavType.NEXT_PAGE)
                    }
                }
            }

            Material.SNOWBALL -> {
                if (!GUIButton.Handler.hasButtonType(clickedItemMeta)) return

                when(GUIButton.Handler.getClickedButtonType(clickedItemMeta)) {
                    SORT_TYPE_BTN.buttonType -> {
                        if (!guiState.matchesCurrentState(GUIStateType.NSERVERFEATURES)) return

                        val typeIndex = guiState.currentFeatureSortType().ordinal

                        with(typeIndex + 1) {
                            if (this >= 3) {
                                guiState.selectSortingType(NServerFeaturesGUIState.FeatureSortingType.DEFAULT)
                            } else {
                                guiState.selectSortingType(NServerFeaturesGUIState.FeatureSortingType.entries[this])
                            }

                            currentStateOption.resetCurrentPageIndex()
                            guiState.currentStateOption().resetConfirmation()
                            inventory.clear()
                            setGUIButtons()
                        }
                    }

                    SORT_ORDER_BTN.buttonType -> {
                        if (!guiState.matchesCurrentState(GUIStateType.NSERVERFEATURES)) return

                        val orderIndex = guiState.currentStateSortOrder().ordinal

                        with(orderIndex + 1) {
                            if (this >= 2) {
                                guiState.selectSortingOrder(SortingOrder.ACSENDING)
                            } else {
                                guiState.selectSortingOrder(SortingOrder.entries[this])
                            }

                            currentStateOption.resetCurrentPageIndex()
                            guiState.currentStateOption().resetConfirmation()
                            inventory.clear()
                            setGUIButtons()
                        }
                    }

                    BACK_BTN.buttonType -> {
                        if (!guiState.matchesCurrentState(GUIStateType.NSERVERFEATURES_MANAGE_OPTIONS)) return

                        guiState.deselectServerFeature()
                        super.openGUI(GUIStateType.NSERVERFEATURES, false)
                    }

                    SAVE_BTN.buttonType -> {
                        if (!guiState.matchesCurrentState(GUIStateType.NSERVERFEATURES_MANAGE_OPTIONS)) return

                        val selectedServerFeature = guiState.getSelectedServerFeature()!!

                        (guiState.getStateData(GUIStateType.NSERVERFEATURES) as ArrayList<ServerFeature>).find {
                            it.featureName == selectedServerFeature.featureName
                        }!!.apply {
                            selectedServerFeature.options.forEach {
                                this.setServerOptionValue(it.optionName, it.optionValue!!)
                            }
                        }

                        guiState.deselectServerFeature()
                        super.openGUI(GUIStateType.NSERVERFEATURES, false)
                    }

                    APPLY_BTN.buttonType -> {
                        if (!guiState.matchesCurrentState(GUIStateType.NSERVERFEATURES)) return

                        guiState.currentStateOption().validationConfirmation().run {
                            if (this) return@run

                            GUIButton.Handler.onConfirm(clickedItemMeta)
                            inventory.clear()
                            setGUIButtons()
                            return
                        }

                        with(guiState.currentStateData() as ArrayList<ServerFeature>) {
                            serverFeatureSession.updateServerFeatures(this)
                            NServerFeaturesRemastered.saveToFile(NServerFeaturesRemastered.toYAML(this))

                            CommandInterfaceProcessor.sendCommandSyntax(
                                player as CommandSender,
                                "${ChatColor.YELLOW}Server features has been updated! Kindly do '/neon serverfeaturesRemastered reload' to reload the changes!"
                            )

                            player.closeInventory()
                        }
                    }
                }
            }

            Material.BARRIER -> {
                if (!CLOSE_BTN.matchesButtonType(clickedItemMeta)) return

                player.closeInventory()
            }

            else -> {
                return
            }
        }
    }
}