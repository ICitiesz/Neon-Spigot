package com.islandstudio.neon.stable.player.nRole

import com.islandstudio.neon.stable.core.gui.GUISession
import com.islandstudio.neon.stable.core.gui.state.GUIStateType
import com.islandstudio.neon.stable.core.gui.structure.GUIBuilderProvider
import com.islandstudio.neon.stable.core.gui.structure.GUIButton
import com.islandstudio.neon.stable.core.gui.structure.GUIConstructor
import com.islandstudio.neon.stable.player.nAccessPermission.Permission
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class GUIBuilder(guiSession: GUISession): GUIConstructor(guiSession), GUIBuilderProvider {
    /* GUI Buttons */
    protected val ROLE_BTN = GUIButton(
        "ROLE_BTN",
        ItemStack(Material.NAME_TAG),
        null
    )

    protected val PERMISSION_BTN = GUIButton(
        "PERMISSION_BTN",
        ItemStack(Material.BIRCH_SIGN),
        null
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

        when (guiState.currentStateType()) {
            GUIStateType.NROLE_MANAGE_PERMISSION -> {
                BACK_BTN
                    .initButtonMeta()
                    .createButton().run {
                        inventory.setItem(45, this)
                    }

                APPLY_BTN
                    .initButtonMeta()
                    .initConfirmationStatus(guiStateOption.getConfirmationStatus())
                    .createButton().run {
                        inventory.setItem(53, this)
                    }
            }

            else -> { return }
        }
    }

    fun getColoredAccessType(accessType: Permission.AccessType): String {
        return if (accessType == Permission.AccessType.FULL) {
            "${ChatColor.GREEN}${Permission.AccessType.FULL}"
        } else {
            "${ChatColor.YELLOW}${Permission.AccessType.LIMITED}"
        }
    }

    fun getColoredAccessStatus(isGranted: Boolean): String {
        return if (isGranted) {
            "${ChatColor.GREEN}${ChatColor.BOLD}Granted!"
        } else {
            "${ChatColor.RED}${ChatColor.BOLD}Denied!"
        }
    }
}
