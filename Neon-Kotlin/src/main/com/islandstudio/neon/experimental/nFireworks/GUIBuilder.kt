package com.islandstudio.neon.experimental.nFireworks

import com.islandstudio.neon.stable.utils.NItemHighlight
import com.islandstudio.neon.stable.utils.identifier.NeonKeyGeneral
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.nGUI.NGUIConstructor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class GUIBuilder(nGUI: NGUI): NGUIConstructor(nGUI) {
    /* nFireworks properties */
    // TODO: FIREWORK_PATTERN_SELECTION = Firework Pattern Selection GUI | FIREWORK_CONFIGURATION = Vanila Firework Properties
    protected var guiState: GUIState = GUIState.FIREWORK_PATTERN_SELECTION // Default state
    protected var fireworkEffects: FireworkProperty.FireworkEffects = FireworkProperty.FireworkEffects(player =  nGUI.getGUIOwner())

    /* Pagination properties */
    protected val maxItemPerPage = 45
    protected var maxPage = 1
    protected var pageIndex = 0
    protected var itemIndex = 0

    /* Button names */
    protected val previousBtnName = "${ChatColor.GOLD}${ChatColor.BOLD}Previous"
    protected val nextBtnName = "${ChatColor.GOLD}${ChatColor.BOLD}Next"
    protected val closeBtnName = "${ChatColor.RED}${ChatColor.BOLD}Close"
    protected val applyBtnName = "${ChatColor.GREEN}${ChatColor.BOLD}Create firework!"
    protected val proceedBtnName = "${ChatColor.GREEN}${ChatColor.BOLD}Proceed"
    protected val backBtnName = "${ChatColor.GOLD}${ChatColor.BOLD}Go Back"
    protected val clearSelectionBtnName = "${ChatColor.YELLOW}${ChatColor.BOLD}Clear Selection"
    protected val restoreDefaultBtnName = "${ChatColor.YELLOW}${ChatColor.BOLD}Restore Default"

    protected val propertyBtnNames: HashMap<String, String> = hashMapOf(
        "amountBtnName" to "${ChatColor.GOLD}${ChatColor.BOLD}Amount",
        "colorBtnName" to "${ChatColor.GOLD}${ChatColor.BOLD}Color",
        "explosionTypeBtnName" to "${ChatColor.GOLD}${ChatColor.BOLD}Explosion Type",
        "patternFacing" to "${ChatColor.GOLD}${ChatColor.BOLD}Pattern Facing",
        "powerBtnName" to "${ChatColor.GOLD}${ChatColor.BOLD}Power",
        "withFadeBtnName" to "${ChatColor.GOLD}${ChatColor.BOLD}With Fade",
        "withFlickerBtnName" to "${ChatColor.GOLD}${ChatColor.BOLD}With Flicker",
        "withTrailBtnName" to "${ChatColor.GOLD}${ChatColor.BOLD}With Trail",
    )

    /* General button item */
    private val previousBtn = ItemStack(Material.SPECTRAL_ARROW)
    private val nextBtn = ItemStack(Material.SPECTRAL_ARROW)
    private val closeBtn = ItemStack(Material.BARRIER)
    private val applyBtn = ItemStack(Material.LEVER)
    private val proceedBtn = ItemStack(Material.LEVER)
    private val backBtn = ItemStack(Material.LEVER)
    private val clearSelectionBtn = ItemStack(Material.NAME_TAG)
    private val restoreDefaultBtn = ItemStack(Material.NAME_TAG)

    /* Button identifier key */
    protected val buttonIDKey: NamespacedKey = NeonKeyGeneral.NGUI_BUTTON.key

    /* Button highlight effect */
    protected val nItemHighlight: NItemHighlight = NItemHighlight(NeonKeyGeneral.NGUI_HIGHTLIGHT_BUTTON.key)

    enum class GUIState(val stateName: String) {
        FIREWORK_PATTERN_SELECTION("nFireworks: Select Pattern"),
        FIREWORK_CONFIGURATION("nFireworks: Vanila Properties")
    }

    fun addNavigationButtons() {
        /* Button item meta */
        val previousBtnMeta = previousBtn.itemMeta!!
        val nextBtnMeta = nextBtn.itemMeta!!
        val closeBtnMeta = closeBtn.itemMeta!!
        val applyBtnMeta = applyBtn.itemMeta!!
        val proceedBtnMeta = proceedBtn.itemMeta!!
        val backBtnMeta = backBtn.itemMeta!!
        val clearSelectionBtnMeta = clearSelectionBtn.itemMeta!!
        val restoreDefaultBtnMeta = restoreDefaultBtn.itemMeta!!

        /* Button lores */
        val prevNextBtnLore: List<String> = listOf("${ChatColor.GRAY}Current:", "${ChatColor.WHITE}Page ${ChatColor.GREEN}${pageIndex + 1} ${ChatColor.WHITE}of ${ChatColor.GREEN}$maxPage")
        val proceedBtnLore: List<String> = listOf("${ChatColor.YELLOW}Please select a pattern to proceed!")
        val clearSelectionBtnLore: List<String> = if (fireworkEffects.imageName.isEmpty()) listOf("${ChatColor.GRAY}Selected: ${ChatColor.DARK_GRAY}none")
            else listOf("${ChatColor.GRAY}Selected: ${ChatColor.GOLD}${fireworkEffects.imageName}")
        val applyBtnLore: List<String> = listOf(
            "${ChatColor.GRAY}Selected Image: ${ChatColor.GOLD}${fireworkEffects.imageName}",
            "${ChatColor.GRAY}Amount: ${ChatColor.GREEN}${fireworkEffects.fireworkAmount}",
            "${ChatColor.GRAY}Color: ${fireworkEffects.fireworkColor.coloredName}",
            "${ChatColor.GRAY}Explosion Type: ${fireworkEffects.getExplosionTypeColoredName()}",
            "${ChatColor.GRAY}Pattern Facing: ${fireworkEffects.getFireworkPatternFacingName(nGUI.getGUIOwner())}",
            "${ChatColor.GRAY}Power: ${ChatColor.GREEN}${fireworkEffects.fireworkPower}",
            "${ChatColor.GRAY}With Fade: ${fireworkEffects.getWithFadeColoredName()}",
            "${ChatColor.GRAY}With Flicker: ${fireworkEffects.getToggleColoredName(fireworkEffects.fireworkWithFlicker)}",
            "${ChatColor.GRAY}With Trail: ${fireworkEffects.getToggleColoredName(fireworkEffects.fireworkWithTrail)}"
        )

        /* Button meta data configuration */
        previousBtnMeta.setDisplayName(previousBtnName)
        previousBtnMeta.lore = prevNextBtnLore
        previousBtnMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        nextBtnMeta.setDisplayName(nextBtnName)
        nextBtnMeta.lore = prevNextBtnLore
        nextBtnMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        closeBtnMeta.setDisplayName(closeBtnName)
        closeBtnMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        applyBtnMeta.setDisplayName(applyBtnName)
        applyBtnMeta.lore = applyBtnLore
        applyBtnMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        proceedBtnMeta.setDisplayName(proceedBtnName)
        if (fireworkEffects.imageName.isEmpty()) {
            proceedBtnMeta.lore = proceedBtnLore
        } else {
            proceedBtnMeta.lore = listOf()
        }
        proceedBtnMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        restoreDefaultBtnMeta.setDisplayName(restoreDefaultBtnName)
        restoreDefaultBtnMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        clearSelectionBtnMeta.setDisplayName(clearSelectionBtnName)
        clearSelectionBtnMeta.lore = clearSelectionBtnLore
        clearSelectionBtnMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        backBtnMeta.setDisplayName(backBtnName)
        backBtnMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

        nextBtn.itemMeta = nextBtnMeta
        previousBtn.itemMeta = previousBtnMeta
        closeBtn.itemMeta = closeBtnMeta
        applyBtn.itemMeta = applyBtnMeta
        clearSelectionBtn.itemMeta = clearSelectionBtnMeta
        backBtn.itemMeta = backBtnMeta
        proceedBtn.itemMeta = proceedBtnMeta
        restoreDefaultBtn.itemMeta = restoreDefaultBtnMeta

        /* Button allocation */
        when (guiState) {
            GUIState.FIREWORK_PATTERN_SELECTION -> {
                inventory.setItem(53, proceedBtn)
                inventory.setItem(49, closeBtn)
                inventory.setItem(52, clearSelectionBtn)

                if (pageIndex > 0) inventory.setItem(48, previousBtn)

                if ((pageIndex + 1) != maxPage) inventory.setItem(50, nextBtn)
            }

            GUIState.FIREWORK_CONFIGURATION -> {
                inventory.setItem(17, applyBtn)
                inventory.setItem(13, closeBtn)
                inventory.setItem(9, backBtn)
                inventory.setItem(16, restoreDefaultBtn)
            }
        }
    }
}