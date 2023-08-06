package com.islandstudio.neon.experimental.nFireworks

import com.islandstudio.neon.stable.utils.NItemHighlight
import com.islandstudio.neon.stable.utils.NeonKey
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.nGUI.NGUIConstructor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

abstract class GUIBuilder(nGUI: NGUI): NGUIConstructor(nGUI) {
    protected val maxItemPerPage = 45
    protected var maxPage = 1
    protected var pageIndex = 0
    protected var itemIndex = 0

    /* Button display names */
    protected val previousButtonDisplayName = "${ChatColor.GOLD}Previous"
    protected val nextButtonDisplayName = "${ChatColor.GOLD}Next"
    protected val closeButtonDisplayName = "${ChatColor.RED}Close"
    //protected val applyButtonDisplayName = "${ChatColor.RED}Apply Changes"
    //protected val toggleOptionValueDisplayName = "${ChatColor.GOLD}Toggle Option Value"

    /* Button identifier key */
    protected val buttonIDKey: NamespacedKey = NeonKey.NamespaceKeys.NEON_BUTTON.key

    /* Button highlight effect */
    protected val nItemHighlight: NItemHighlight = NItemHighlight(NeonKey.NamespaceKeys.NEON_BUTTON_HIGHLIGHT.key)

    fun addGUIButtons() {
        /* Button item */
        val nextButton = ItemStack(Material.SPECTRAL_ARROW)
        val previousButton = ItemStack(Material.SPECTRAL_ARROW)
        val closeButton = ItemStack(Material.BARRIER)
        //val applyButton = ItemStack(Material.LEVER)
        //val toggleOptionButton = ItemStack(Material.NAME_TAG)


    }
}