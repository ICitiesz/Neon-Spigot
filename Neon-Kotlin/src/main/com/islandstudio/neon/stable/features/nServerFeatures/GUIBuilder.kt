package com.islandstudio.neon.stable.features.nServerFeatures

import com.islandstudio.neon.stable.core.gui.GUISession
import com.islandstudio.neon.stable.core.gui.state.GUIStateType
import com.islandstudio.neon.stable.core.gui.structure.GUIBuilderProvider
import com.islandstudio.neon.stable.core.gui.structure.GUIButton
import com.islandstudio.neon.stable.core.gui.structure.GUIConstructor
import com.islandstudio.neon.stable.core.gui.structure.SortingOrder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class GUIBuilder(guiSession: GUISession): GUIConstructor(guiSession), GUIBuilderProvider {


    /* GUI Buttons */
    protected val FEATURE_BTN = GUIButton(
        "FEATURE_BTN",
        ItemStack(Material.BIRCH_SIGN),
        null
    )

    protected val FEATURE_OPTION_BTN = GUIButton(
        "FEATURE_OPTION_BTN",
        ItemStack(Material.PAPER),
        null
    )

    protected val SORT_TYPE_BTN = GUIButton(
        "SORT_TYPE_BTN",
        ItemStack(Material.SNOWBALL),
        "${ChatColor.GOLD}Sort By${ChatColor.GRAY}:"
    )

    protected val SORT_ORDER_BTN = GUIButton(
        "SORT_ORDER_BY",
        ItemStack(Material.SNOWBALL),
        "${ChatColor.GOLD}Sort Order${ChatColor.GRAY}:"
    )

    protected val PREVIOUS_BTN = GUIButton(
        "PREVIOUS_BTN",
        ItemStack(Material.SPECTRAL_ARROW),
        "${ChatColor.GOLD}${ChatColor.BOLD}<< Previous"
    )

    protected val NEXT_BTN = GUIButton(
        "NEXT_BTN",
        ItemStack(Material.SPECTRAL_ARROW),
        "${ChatColor.GOLD}${ChatColor.BOLD}Next >>"
    )

    protected val CLOSE_BTN = GUIButton(
        "CLOSE_BTN",
        ItemStack(Material.BARRIER),
        "${ChatColor.RED}${ChatColor.BOLD}Close"
    )

    protected val BACK_BTN = GUIButton(
        "BACK_BTN",
        ItemStack(Material.SNOWBALL),
        "${ChatColor.GOLD}${ChatColor.BOLD}<< Back"
    )

    protected val APPLY_BTN = GUIButton(
        "APPLY_BTN",
        ItemStack(Material.SNOWBALL),
        "${ChatColor.GOLD}${ChatColor.BOLD}Apply Changes"
    )

    protected val SAVE_BTN = GUIButton(
        "SAVE_BTN",
        ItemStack(Material.SNOWBALL),
        "${ChatColor.GOLD}${ChatColor.BOLD}Save Options"
    )

    override fun addNavigationButtons() {
        val guiStateOption = guiState.currentStateOption()

        val pageDetail: LinkedList<String> = LinkedList<String>().apply {
            this.add("${ChatColor.GRAY}Current:")
            this.add("${ChatColor.WHITE}Page ${ChatColor.GREEN}${guiStateOption.getPageIndex() + 1}" +
                    " ${ChatColor.WHITE}of ${ChatColor.GREEN}${guiStateOption.getMaxPage()}")
        }

        /* Button meta configuration */
        PREVIOUS_BTN
            .initButtonMeta(pageDetail)
            .createButton().run {
                if (guiStateOption.getPageIndex() > 0) inventory.setItem(48, this)
            }

        NEXT_BTN
            .initButtonMeta(pageDetail)
            .createButton().run {
                if ((guiStateOption.getPageIndex() + 1) != guiStateOption.getMaxPage()) inventory.setItem(50, this)
            }

        CLOSE_BTN
            .initButtonMeta()
            .createButton().run {
                inventory.setItem(49, this)
            }

        when(guiState.currentStateType()) {
            GUIStateType.NSERVERFEATURES -> {
                val sortTypeDetail: LinkedList<String> = LinkedList<String>().apply {
                    val sortType = (guiState as NServerFeaturesGUIState).currentFeatureSortType()

                    this.add(sortType.type)
                }

                val sortOrderDetail: LinkedList<String> = LinkedList<String>().apply {
                    when(guiState.currentStateSortOrder()) {
                        SortingOrder.ACSENDING -> {
                            this.add("${ChatColor.WHITE}Ascending")
                        }

                        SortingOrder.DESCENDING -> {
                            this.add("${ChatColor.WHITE}Descending")
                        }
                    }
                }

                SORT_TYPE_BTN
                    .initButtonMeta(sortTypeDetail)
                    .initDataContainer(hashMapOf("sortType" to (guiState as NServerFeaturesGUIState).currentFeatureSortType()))
                    .createButton().run {
                        inventory.setItem(45, this)
                    }

                SORT_ORDER_BTN
                    .initButtonMeta(sortOrderDetail)
                    .initDataContainer(hashMapOf("sortOrder" to guiState.currentStateSortOrder()))
                    .createButton().run {
                        inventory.setItem(46, this)
                    }

                APPLY_BTN
                    .initButtonMeta()
                    .initConfirmationStatus(guiStateOption.getConfirmationStatus())
                    .createButton().run {
                        inventory.setItem(53, this)
                    }
            }

            GUIStateType.NSERVERFEATURES_MANAGE_OPTIONS -> {
                BACK_BTN
                    .initButtonMeta()
                    .createButton().run {
                        inventory.setItem(45, this)
                    }

                SAVE_BTN
                    .initButtonMeta()
                    .createButton().run {
                        inventory.setItem(53, this)
                    }
            }

            else -> { return }
        }

    }
}