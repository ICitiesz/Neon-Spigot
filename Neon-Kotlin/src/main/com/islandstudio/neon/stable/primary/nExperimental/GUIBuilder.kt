package com.islandstudio.neon.stable.primary.nExperimental

import com.islandstudio.neon.stable.utils.NNamespaceKeys
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.nGUI.NGUIConstructor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class GUIBuilder(nGUI: NGUI) : NGUIConstructor(nGUI) {
    protected val maxItemPerPage = 45
    protected var pageIndex = 0
    protected var itemIndex = 0

    fun addGUIButtons() {
        val buttonLore: ArrayList<String> = ArrayList()
        buttonLore.add("${ChatColor.GRAY}Current page: ${ChatColor.GREEN}${pageIndex + 1}")

        val nextButton = ItemStack(Material.SPECTRAL_ARROW)
        val previousButton = ItemStack(Material.SPECTRAL_ARROW)
        val closeButton = ItemStack(Material.BARRIER)
        val applyButton = ItemStack(Material.LEVER)

        val nextButtonMeta = nextButton.itemMeta
        val previousButtonMeta = previousButton.itemMeta
        val closeButtonMeta = closeButton.itemMeta
        val applyButtonMeta = applyButton.itemMeta

        if (nextButtonMeta != null) {
            nextButtonMeta.setDisplayName("${ChatColor.GOLD}Next")
            nextButtonMeta.lore = buttonLore
            nextButtonMeta.persistentDataContainer.set(NNamespaceKeys.NEON_BUTTON.key, PersistentDataType.STRING, NNamespaceKeys.NEON_BUTTON.key.toString())
        }

        if (previousButtonMeta != null) {
            previousButtonMeta.setDisplayName("${ChatColor.GOLD}Previous")
            previousButtonMeta.lore = buttonLore
            previousButtonMeta.persistentDataContainer.set(NNamespaceKeys.NEON_BUTTON.key, PersistentDataType.STRING, NNamespaceKeys.NEON_BUTTON.key.toString())
        }

        closeButtonMeta!!.setDisplayName("${ChatColor.RED}Close")
        closeButtonMeta.persistentDataContainer.set(NNamespaceKeys.NEON_BUTTON.key, PersistentDataType.STRING, NNamespaceKeys.NEON_BUTTON.key.toString())
        applyButtonMeta!!.setDisplayName("${ChatColor.RED}Apply")
        applyButtonMeta.persistentDataContainer.set(NNamespaceKeys.NEON_BUTTON.key, PersistentDataType.STRING, NNamespaceKeys.NEON_BUTTON.key.toString())

        nextButton.itemMeta = nextButtonMeta
        previousButton.itemMeta = previousButtonMeta
        closeButton.itemMeta = closeButtonMeta
        applyButton.itemMeta = applyButtonMeta

        inventory.setItem(53, applyButton)
        inventory.setItem(50, nextButton)
        inventory.setItem(49, closeButton)
        inventory.setItem(48, previousButton)
    }
}