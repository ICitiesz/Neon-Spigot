package com.islandstudio.neon.experimental.nServerConfigurationNew

import com.islandstudio.neon.stable.utils.NItemHighlight
import com.islandstudio.neon.stable.utils.NNamespaceKeys
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.nGUI.NGUIConstructor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class GUIBuilder(ngui: NGUI): NGUIConstructor(ngui) {
    protected val maxItemPerPage = 45
    protected var maxPage = 1
    protected var pageIndex = 0
    protected var itemIndex = 0

    protected var isOptionVisible = false

    /* Button display names */
    protected val previousButtonDisplayName = "${ChatColor.GOLD}Previous"
    protected val nextButtonDisplayName = "${ChatColor.GOLD}Next"
    protected val closeButtonDisplayName = "${ChatColor.RED}Close"
    protected val applyButtonDisplayName = "${ChatColor.RED}Apply Changes"
    protected val toggleOptionValueDisplayName = "${ChatColor.GOLD}Toggle Option Value"

    /* Button identifier key */
    protected val buttonIDKey: NamespacedKey = NNamespaceKeys.NEON_BUTTON.key

    /* Button highlight effect */
    protected val nItemHighlight: NItemHighlight = NItemHighlight(NNamespaceKeys.NEON_BUTTON_HIGHLIGHT.key)

    fun addGUIButtons() {
        /* Option visibility status */
        val optionVisibility: String = if (isOptionVisible) "${ChatColor.GREEN}Visible!" else "${ChatColor.RED}Hidden!"

        /* Button lore */
        val buttonLore1: List<String> = listOf("${ChatColor.GRAY}Current:", "${ChatColor.WHITE}Page ${ChatColor.GREEN}${pageIndex + 1} ${ChatColor.WHITE}of ${ChatColor.GREEN}$maxPage")
        val buttonLore2: List<String> = listOf("${ChatColor.YELLOW}Server reload are required!")
        val buttonLore3: List<String> = listOf("${ChatColor.GRAY}Status: $optionVisibility")

        /* Button item */
        val nextButton = ItemStack(Material.SPECTRAL_ARROW)
        val previousButton = ItemStack(Material.SPECTRAL_ARROW)
        val closeButton = ItemStack(Material.BARRIER)
        val applyButton = ItemStack(Material.LEVER)
        val toggleOptionButton = ItemStack(Material.NAME_TAG)

        /* Button item meta */
        val nextButtonMeta = nextButton.itemMeta
        val previousButtonMeta = previousButton.itemMeta
        val closeButtonMeta = closeButton.itemMeta
        val applyButtonMeta = applyButton.itemMeta
        val toggleOptionButtonMeta = toggleOptionButton.itemMeta

        nextButtonMeta!!.setDisplayName(nextButtonDisplayName)
        nextButtonMeta.lore = buttonLore1
        nextButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        previousButtonMeta!!.setDisplayName(previousButtonDisplayName)
        previousButtonMeta.lore = buttonLore1
        previousButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        closeButtonMeta!!.setDisplayName(closeButtonDisplayName)
        closeButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        applyButtonMeta!!.setDisplayName(applyButtonDisplayName)
        applyButtonMeta.lore = buttonLore2
        applyButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        toggleOptionButtonMeta!!.setDisplayName(toggleOptionValueDisplayName)
        toggleOptionButtonMeta.lore = buttonLore3
        toggleOptionButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        when (isOptionVisible) {
            true -> {
                toggleOptionButtonMeta.addEnchant(nItemHighlight, 0 , true)
            }

            false -> {
                toggleOptionButtonMeta.removeEnchant(nItemHighlight)
            }
        }

        nextButton.itemMeta = nextButtonMeta
        previousButton.itemMeta = previousButtonMeta
        closeButton.itemMeta = closeButtonMeta
        applyButton.itemMeta = applyButtonMeta
        toggleOptionButton.itemMeta = toggleOptionButtonMeta

        inventory.setItem(53, applyButton)
        inventory.setItem(52, toggleOptionButton)
        inventory.setItem(49, closeButton)

        if (pageIndex > 0) inventory.setItem(48, previousButton)

        if ((pageIndex + 1) != maxPage) inventory.setItem(50, nextButton)
    }
}