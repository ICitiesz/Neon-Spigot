package com.islandstudio.neon.features.neonfeature.gui

import com.islandstudio.neon.experimental.gui.GuiConstructor
import com.islandstudio.neon.experimental.gui.GuiSortingOrder
import com.islandstudio.neon.experimental.gui.component.GuiButton
import com.islandstudio.neon.experimental.gui.component.IGuiComponent
import com.islandstudio.neon.experimental.gui.state.GuiStateName
import org.bukkit.ChatColor
import org.bukkit.Material
import java.util.*

open class NeonFeatureGuiComponent(private val guiConstructor: GuiConstructor<*>): IGuiComponent {
    private val guiStateManager = guiConstructor

    val featureBtn = GuiButton(
        "NEON_FEATURE_GUI__FEATURE_BTN",
        Material.BIRCH_SIGN,
        null
    )

    val featureOptionBtn = GuiButton(
        "NEON_FEATURE_GUI__FEATURE_OPTION_BTN",
        Material.PAPER,
        null
    )

    val sortTypeBtn = GuiButton(
        "NEON_FEATURE_GUI___SORT_TYPE_BTN",
        Material.SNOWBALL,
        "${ChatColor.GOLD}Sort By${ChatColor.GRAY}:"
    )

    val sortOrderBtn = GuiButton(
        "NEON_FEATURE_GUI__SORT_ORDER_BTN",
        Material.SNOWBALL,
        "${ChatColor.GOLD}Sort Order${ChatColor.GRAY}:"
    )

    val previousPageBtn = GuiButton(
        "NEON_FEATURE_GUI__PREVIOUS_PAGE_BTN",
        Material.SPECTRAL_ARROW,
        "${ChatColor.GOLD}${ChatColor.BOLD}<< Previous"
    )

    val nextPageBtn = GuiButton(
        "NEON_FEATURE_GUI__NEXT_PAGE_BTN",
        Material.SPECTRAL_ARROW,
        "${ChatColor.GOLD}${ChatColor.BOLD}Next >>"
    )

    val closeBtn = GuiButton(
        "NEON_FEATURE_GUI__CLOSE_BTN",
        Material.BARRIER,
        "${ChatColor.RED}${ChatColor.BOLD}Close"
    )

    val backBtn = GuiButton(
        "NEON_FEATURE_GUI__BACK_BTN",
        Material.SNOWBALL,
        "${ChatColor.GOLD}${ChatColor.BOLD}<< Back"
    )

    val applyBtn = GuiButton(
        "NEON_FEATURE_GUI__APPLY_BTN",
        Material.SNOWBALL,
        "${ChatColor.GOLD}${ChatColor.BOLD}Apply Changes"
    )

    val saveBtn = GuiButton(
        "NEON_FEATURE_GUI__SAVE_BTN",
        Material.SNOWBALL,
        "${ChatColor.GOLD}${ChatColor.BOLD}Save Options"
    )

    override fun configureGuiComponent() {
        val currentGuiState = guiStateManager.getCurrentGuiState()

        val guiPageMetaData = LinkedList<String>().apply {
            add("${ChatColor.GRAY}Current:")
            add("${ChatColor.WHITE}Page ${ChatColor.GREEN}${currentGuiState.getCurrentPageIndex() + 1}" +
                    " ${ChatColor.WHITE}of ${ChatColor.GREEN}${currentGuiState.getMaxPage()}")
        }

        /* Button general meta data configuration */
        previousPageBtn.configureButton {
            withButtonLabels(guiPageMetaData)
            createButton()

            if (currentGuiState.getCurrentPageIndex() > 0) {
                guiConstructor.inventory.setItem(48, getButtonItem())
            }
        }

        nextPageBtn.configureButton {
            withButtonLabels(guiPageMetaData)
            createButton()

            if ((currentGuiState.getCurrentPageIndex() + 1) != currentGuiState.getMaxPage()) {
                guiConstructor.inventory.setItem(50, getButtonItem())
            }
        }

        closeBtn.configureButton {
            createButton()

            guiConstructor.inventory.setItem(49, getButtonItem())
        }

        when(guiStateManager.getCurrentGuiStateName()) {
            GuiStateName.NeonFeatureMainGUI -> {
                val currentStateData = guiStateManager.getCurrentGuiState().getStateData<NeonFeatureGuiStateData>()

                sortTypeBtn.configureButton {
                    withButtonLabels {
                        add(currentStateData.getCurrentSortType().typeName)
                    }

                    createButton()

                    guiConstructor.inventory.setItem(45, getButtonItem())
                }

                sortOrderBtn.configureButton {
                    withButtonLabels {
                        when(guiStateManager.getCurrentGuiState().currentSortOrder()) {
                            GuiSortingOrder.ACSENDING -> {
                                add("${ChatColor.WHITE}Ascending")
                            }

                            GuiSortingOrder.DESCENDING ->  {
                                add("${ChatColor.WHITE}Descending")
                            }
                        }
                    }

                    createButton()

                    guiConstructor.inventory.setItem(46, getButtonItem())
                }

                applyBtn.configureButton {
                    withConfirmationStatus()
                    createButton()

                    guiConstructor.inventory.setItem(53, getButtonItem())
                }
            }

            GuiStateName.NeonFeatureOptionManagementGUI -> {
                backBtn.configureButton {
                    createButton()

                    guiConstructor.inventory.setItem(45, getButtonItem())
                }

                saveBtn.configureButton {
                    createButton()

                    guiConstructor.inventory.setItem(53, getButtonItem())
                }
            }

            else -> return
        }
    }
}