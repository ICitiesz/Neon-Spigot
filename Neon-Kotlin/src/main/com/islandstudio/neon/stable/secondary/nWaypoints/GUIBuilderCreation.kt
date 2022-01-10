package com.islandstudio.neon.stable.secondary.nWaypoints

import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.nGUI.NGUIConstructor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

abstract class GUIBuilderCreation(nGUI: NGUI) : NGUIConstructor(nGUI) {
    protected val maxItemPerPage = 45
    protected var pageIndex = 0
    protected var itemIndex = 0

    fun addGUIButtons() {
        val buttonLore: ArrayList<String> = ArrayList()
        buttonLore.add(ChatColor.GRAY.toString() + "Current page: " + ChatColor.GREEN + "${pageIndex + 1}")

        val nextButton = ItemStack(Material.SPECTRAL_ARROW)
        val previousButton = ItemStack(Material.SPECTRAL_ARROW)
        val closeButton = ItemStack(Material.BARRIER)

        val nextButtonMeta = nextButton.itemMeta
        val previousButtonMeta = previousButton.itemMeta
        val closeButtonMeta = closeButton.itemMeta

        if (nextButtonMeta != null) {
            nextButtonMeta.setDisplayName(ChatColor.GOLD.toString() + "Next")
            nextButtonMeta.lore = buttonLore
        }

        if (previousButtonMeta != null) {
            previousButtonMeta.setDisplayName(ChatColor.GOLD.toString() + "Previous")
            previousButtonMeta.lore = buttonLore
        }

        closeButtonMeta?.setDisplayName(ChatColor.RED.toString() + "Close")

        nextButton.itemMeta = nextButtonMeta
        previousButton.itemMeta = previousButtonMeta
        closeButton.itemMeta = closeButtonMeta

        inventory.setItem(50, nextButton)
        inventory.setItem(49, closeButton)
        inventory.setItem(48, previousButton)
    }
}