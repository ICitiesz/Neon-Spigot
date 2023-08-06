package com.islandstudio.neon.stable.secondary.nWaypoints

import com.islandstudio.neon.stable.utils.NItemHighlight
import com.islandstudio.neon.stable.utils.NeonKey
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.nGUI.NGUIConstructor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class GUIBuilderRemoval(nGUI: NGUI) : NGUIConstructor(nGUI) {
    protected val maxItemPerPage = 45
    protected var maxPage = 1
    protected var pageIndex = 0
    protected var itemIndex = 0

    /* Button display names */
    protected val previousButtonDisplayName = "${ChatColor.GOLD}Previous"
    protected val nextButtonDisplayName = "${ChatColor.GOLD}Next"
    protected val closeButtonDisplayName = "${ChatColor.RED}Close"
    protected val removeButtonDisplayName = "${ChatColor.RED}Remove"

    /* Button identifier key */
    protected val buttonIDKey: NamespacedKey = NeonKey.NamespaceKeys.NEON_BUTTON.key

    /* Button highlight effect */
    protected val nItemHighlight: NItemHighlight = NItemHighlight(NeonKey.NamespaceKeys.NEON_BUTTON_HIGHLIGHT.key)

    fun addGUIButtons() {
        /* Button lore */
        val buttonLore: List<String> = listOf("${ChatColor.GRAY}Current:", "${ChatColor.WHITE}Page ${ChatColor.GREEN}${pageIndex + 1} ${ChatColor.WHITE}of ${ChatColor.GREEN}$maxPage")

        /* Button item */
        val nextButton = ItemStack(Material.SPECTRAL_ARROW)
        val previousButton = ItemStack(Material.SPECTRAL_ARROW)
        val closeButton = ItemStack(Material.BARRIER)
        val removeButton = ItemStack(Material.BLAZE_POWDER)

        /* Button item meta */
        val nextButtonMeta = nextButton.itemMeta
        val previousButtonMeta = previousButton.itemMeta
        val closeButtonMeta = closeButton.itemMeta
        val removeButtonMeta = removeButton.itemMeta

        nextButtonMeta!!.setDisplayName(nextButtonDisplayName)
        nextButtonMeta.lore = buttonLore
        nextButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        previousButtonMeta!!.setDisplayName(previousButtonDisplayName)
        previousButtonMeta.lore = buttonLore
        previousButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        closeButtonMeta!!.setDisplayName(closeButtonDisplayName)
        closeButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        removeButtonMeta!!.setDisplayName(removeButtonDisplayName)
        removeButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        nextButton.itemMeta = nextButtonMeta
        previousButton.itemMeta = previousButtonMeta
        closeButton.itemMeta = closeButtonMeta
        removeButton.itemMeta = removeButtonMeta

        inventory.setItem(53, removeButton)
        inventory.setItem(49, closeButton)

        if (pageIndex > 0) inventory.setItem(48, previousButton)

        if ((pageIndex + 1) != maxPage) inventory.setItem(50, nextButton)
    }
}