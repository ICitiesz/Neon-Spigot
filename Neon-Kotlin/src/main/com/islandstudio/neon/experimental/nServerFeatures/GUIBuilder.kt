package com.islandstudio.neon.experimental.nServerFeatures

import com.islandstudio.neon.stable.utils.NItemHighlight
import com.islandstudio.neon.stable.utils.NeonKey
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
    protected var sortingType = SortingType.DEFAULT
    protected var sortingOrder = SortingOrder.ASCENDING

    /* Button display names */
    protected val previousButtonDisplayName = "${ChatColor.GOLD}${ChatColor.BOLD}<<Previous"
    protected val nextButtonDisplayName = "${ChatColor.GOLD}${ChatColor.BOLD}Next>>"
    protected val closeButtonDisplayName = "${ChatColor.RED}${ChatColor.BOLD}Close"
    protected val applyButtonDisplayName = "${ChatColor.RED}${ChatColor.BOLD}Apply Changes"
    protected val toggleOptionValueDisplayName = "${ChatColor.GOLD}${ChatColor.BOLD}Toggle Option Value"
    protected val sortTypeButtonDisplayName = "${ChatColor.GOLD}${ChatColor.BOLD}Sort By"
    protected val sortOrderButtonDisplayName = "${ChatColor.GOLD}${ChatColor.BOLD}Sort Order"

    /* Button identifier key */
    protected val buttonIDKey: NamespacedKey = NeonKey.NamespaceKeys.NEON_BUTTON.key

    /* Button highlight effect */
    protected val nItemHighlight: NItemHighlight = NItemHighlight(NeonKey.NamespaceKeys.NEON_BUTTON_HIGHLIGHT.key)

    /* Soring types whether show all by default, either by stable or by experimental */
    enum class SortingType(val type: String) {
        DEFAULT("${ChatColor.WHITE}Default"),
        STABLE("${ChatColor.WHITE}Stable"),
        EXPERIMENTAL("${ChatColor.WHITE}Experimental")
    }

    /* Sorting order by Ascending or by Descending */
    enum class SortingOrder(val order: String) {
        ASCENDING("${ChatColor.WHITE}Ascending"),
        DESCENDING("${ChatColor.WHITE}Descending")
    }

    fun addNavigationButtons() {
        /* Option visibility status */
        val optionVisibility: String = if (isOptionVisible) "${ChatColor.GREEN}Visible!" else "${ChatColor.RED}Hidden!"

        /* Button lore */
        val nextButtonLore: List<String> = listOf("${ChatColor.GRAY}Current:", "${ChatColor.WHITE}Page ${ChatColor.GREEN}${pageIndex + 1} ${ChatColor.WHITE}of ${ChatColor.GREEN}$maxPage")
        val applyButtonLore: List<String> = listOf("${ChatColor.YELLOW}Server reload are required!")
        val toggleOptionButtonLore: List<String> = listOf("${ChatColor.GRAY}Status: $optionVisibility")
        val sortTypeButtonLore: List<String> = when (sortingType) {
            SortingType.STABLE -> {
                listOf(SortingType.STABLE.type)
            }

            SortingType.EXPERIMENTAL -> {
                listOf(SortingType.EXPERIMENTAL.type)
            }

            else -> {
                listOf(SortingType.DEFAULT.type)
            }
        }

        val sortOrderButtonLore: List<String> = when (sortingOrder) {
            SortingOrder.DESCENDING -> {
                listOf(SortingOrder.DESCENDING.order)
            }

            else -> {
                listOf(SortingOrder.ASCENDING.order)
            }
        }

        /* Button item */
        val nextButton = ItemStack(Material.SPECTRAL_ARROW)
        val previousButton = ItemStack(Material.SPECTRAL_ARROW)
        val closeButton = ItemStack(Material.BARRIER)
        val applyButton = ItemStack(Material.LEVER)
        val toggleOptionButton = ItemStack(Material.NAME_TAG)
        val sortTypeButton = ItemStack(Material.NAME_TAG)
        val sortOrderButton = ItemStack(Material.NAME_TAG)

        /* Button item meta */
        val nextButtonMeta = nextButton.itemMeta
        val previousButtonMeta = previousButton.itemMeta
        val closeButtonMeta = closeButton.itemMeta
        val applyButtonMeta = applyButton.itemMeta
        val toggleOptionButtonMeta = toggleOptionButton.itemMeta
        val sortTypeButtonMeta = sortTypeButton.itemMeta
        val sortOrderButtonMeta = sortOrderButton.itemMeta

        nextButtonMeta!!.setDisplayName(nextButtonDisplayName)
        nextButtonMeta.lore = nextButtonLore
        nextButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        previousButtonMeta!!.setDisplayName(previousButtonDisplayName)
        previousButtonMeta.lore = nextButtonLore
        previousButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        closeButtonMeta!!.setDisplayName(closeButtonDisplayName)
        closeButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        applyButtonMeta!!.setDisplayName(applyButtonDisplayName)
        applyButtonMeta.lore = applyButtonLore
        applyButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        toggleOptionButtonMeta!!.setDisplayName(toggleOptionValueDisplayName)
        toggleOptionButtonMeta.lore = toggleOptionButtonLore
        toggleOptionButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        sortTypeButtonMeta!!.setDisplayName(sortTypeButtonDisplayName)
        sortTypeButtonMeta.lore = sortTypeButtonLore
        sortTypeButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        sortOrderButtonMeta!!.setDisplayName(sortOrderButtonDisplayName)
        sortOrderButtonMeta.lore = sortOrderButtonLore
        sortOrderButtonMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

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
        sortTypeButton.itemMeta = sortTypeButtonMeta
        sortOrderButton.itemMeta = sortOrderButtonMeta

        inventory.setItem(53, applyButton)
        inventory.setItem(52, toggleOptionButton)
        inventory.setItem(49, closeButton)
        inventory.setItem(45, sortTypeButton)
        inventory.setItem(46, sortOrderButton)

        if (pageIndex > 0) inventory.setItem(48, previousButton)

        if ((pageIndex + 1) != maxPage) inventory.setItem(50, nextButton)
    }
}