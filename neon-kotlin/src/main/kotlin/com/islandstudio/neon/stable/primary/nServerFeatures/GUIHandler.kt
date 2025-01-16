package com.islandstudio.neon.stable.primary.nServerFeatures

import com.islandstudio.neon.Neon
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.item.NItemGlinter
import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.inject
import java.util.*
import kotlin.math.ceil

open class GUIHandler (nGUI: NGUI): GUIBuilder(nGUI), IComponentInjector {
    private val neon by inject<Neon>()

    private val player: Player = nGUI.getGUIOwner()
    private val internalServerFeatures = NServerFeatures.Handler.getLoadedInternalServerFeatures()
    private var serverFeatureNames = NServerFeatures.getServerFeatureNames(sortingType, sortingOrder)

    override fun getGUIName(): String {
        return "${ChatColor.YELLOW}${ChatColor.MAGIC}-----${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}nServerFeatures" +
                "${ChatColor.YELLOW}${ChatColor.MAGIC}-----"
    }

    override fun getGUISlots(): Int {
        return 54
    }

    override fun setGUIButtons() {
        maxPage = ceil(serverFeatureNames.size.toDouble() / maxItemPerPage.toDouble()).toInt()

        addNavigationButtons()

        for (i in 0 until super.maxItemPerPage) {
            itemIndex = super.maxItemPerPage * pageIndex + i

            if (itemIndex >= serverFeatureNames.size) break

            val serverFeatureItem = ItemStack(Material.BIRCH_SIGN)
            val serverFeatureItemMeta = serverFeatureItem.itemMeta!!

            val serverFeatureDetailsContainer: LinkedList<String> = LinkedList()
            val serverFeatureName = serverFeatureNames[itemIndex]

            val internalServerFeature = internalServerFeatures[serverFeatureName]!!

            val editableServerFeature = NServerFeatures.getGUISession(player.uniqueId)[serverFeatureName]!!
            val editableToggleStatus = editableServerFeature.isEnabled ?: internalServerFeature.isEnabled!!

            if (editableToggleStatus) {
                serverFeatureDetailsContainer.add("${ChatColor.GRAY}Status: ${ChatColor.GREEN}Enabled!")
                serverFeatureItemMeta.addEnchant(NItemGlinter.ItemGlinterType.NGUI_BUTTON_GLINT.glint, 0, true)
            } else {
                serverFeatureDetailsContainer.add("${ChatColor.GRAY}Status: ${ChatColor.RED}Disabled!")
            }

            serverFeatureDetailsContainer.add("")

            when(isOptionVisible) {
                true -> {
                    serverFeatureDetailsContainer.add("${ChatColor.GRAY}${ChatColor.UNDERLINE}Option(s):")

                    val editableServerFeatureOption = editableServerFeature.options ?: internalServerFeature.options!!

                    editableServerFeatureOption.isEmpty().let {
                        if (it) {
                            serverFeatureDetailsContainer.add("${ChatColor.YELLOW}No option available!")
                            return@let
                        }

                        editableServerFeatureOption.forEach { option ->
                            val optionName = option.value.optionName
                            val optionValue = option.value.optionValue

                            if (option.value.optionDataType.equals("Boolean", true)) {
                                if (optionValue as Boolean) {
                                    serverFeatureDetailsContainer.add("${ChatColor.GOLD}${optionName}: ${ChatColor.GREEN}${optionValue}")
                                    return@let
                                }

                                serverFeatureDetailsContainer.add("${ChatColor.GOLD}${optionName}: ${ChatColor.RED}${optionValue}")
                                return@let
                            }

                            serverFeatureDetailsContainer.add("${ChatColor.GOLD}${optionName}: ${ChatColor.GREEN}${optionValue}")
                        }
                    }
                }

                false -> {
                    val editableServerFeatureDescription: ArrayList<String> = editableServerFeature.description?.split(" ")
                        ?.toCollection(ArrayList()) ?: internalServerFeature.description!!.split(" ").toCollection(ArrayList())

                    val modifiedServerFeatureDescription: LinkedList<Collection<String>> = LinkedList()
                    var splicedWords: LinkedList<String> = LinkedList()

                    /* Spit up the description into 7 words per line */
                    for (word in editableServerFeatureDescription) {
                        if (splicedWords.size == 7) {
                            modifiedServerFeatureDescription.add(splicedWords)
                            splicedWords = LinkedList()
                        }

                        splicedWords.add(word)

                        if ((editableServerFeatureDescription.size - editableServerFeatureDescription.indexOf(word)) == 1) {
                            modifiedServerFeatureDescription.add(splicedWords)
                        }
                    }

                    serverFeatureDetailsContainer.add("${ChatColor.GRAY}Description: ")

                    /* Setting up the server feature description to the button */
                    modifiedServerFeatureDescription.forEach { word ->
                        serverFeatureDetailsContainer.add("${ChatColor.YELLOW}${word.joinToString(" ")}")
                    }

                    serverFeatureDetailsContainer.add("")
                    serverFeatureDetailsContainer.add("${ChatColor.GRAY}Command:")

                    val editableServerFeatureCommand = editableServerFeature.command ?: internalServerFeature.command!!

                    editableServerFeatureCommand.isEmpty().let {
                        if (it) {
                            serverFeatureDetailsContainer.add("${ChatColor.YELLOW}No command available!")
                            return@let
                        }

                        serverFeatureDetailsContainer.add("${ChatColor.YELLOW}${editableServerFeatureCommand}")
                    }

                }
            }

            /* Setting up the server feature name to the button */
            val editableFeatureState = editableServerFeature.isExperimental ?: internalServerFeature.isExperimental!!

            val itemDisplayName = if (editableFeatureState) "${NServerFeatures.experimentalTag}${ChatColor.GOLD}${serverFeatureName}"
            else "${ChatColor.GOLD}${serverFeatureName}"

            serverFeatureItemMeta.setDisplayName(itemDisplayName)

            serverFeatureItemMeta.lore = serverFeatureDetailsContainer

            serverFeatureItemMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

            serverFeatureItem.itemMeta = serverFeatureItemMeta

            inventory.addItem(serverFeatureItem)
        }
    }

    override fun setGUIClickHandler(e: InventoryClickEvent) {
        val clickedItem: ItemStack = e.currentItem!!
        val clickedItemMeta: ItemMeta = clickedItem.itemMeta!!
        val persistentDataContainer: PersistentDataContainer = clickedItemMeta.persistentDataContainer
        val editableServerFeature = NServerFeatures.getGUISession(player.uniqueId)

        when (clickedItem.type) {
            /* Server feature button */
            Material.BIRCH_SIGN -> {
                if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                val clickedItemDisplayName: String = if (clickedItemMeta.displayName.contains(NServerFeatures.experimentalTag))
                    clickedItemMeta.displayName.substring(20) else clickedItemMeta.displayName.substring(2)

                if (!serverFeatureNames.contains(clickedItemDisplayName)) return

                val clickedItemLore: ArrayList<String> = clickedItemMeta.lore as ArrayList<String>

                val statusEnabled = "${ChatColor.GRAY}Status: ${ChatColor.GREEN}Enabled!"
                val statusDisabled = "${ChatColor.GRAY}Status: ${ChatColor.RED}Disabled!"

                when {
                    clickedItemLore.contains(statusEnabled) -> {
                        NServerFeatures.setToggle(editableServerFeature[clickedItemDisplayName]!!, false)

                        clickedItemLore[clickedItemLore.indexOf(statusEnabled)] = statusDisabled
                        clickedItemMeta.removeEnchant(NItemGlinter.ItemGlinterType.NGUI_BUTTON_GLINT.glint)
                    }

                    clickedItemLore.contains(statusDisabled) -> {
                        NServerFeatures.setToggle(editableServerFeature[clickedItemDisplayName]!!, true)

                        clickedItemLore[clickedItemLore.indexOf(statusDisabled)] = statusEnabled
                        clickedItemMeta.addEnchant(NItemGlinter.ItemGlinterType.NGUI_BUTTON_GLINT.glint, 0, true)
                    }
                }

                clickedItemMeta.lore = clickedItemLore
                clickedItem.itemMeta = clickedItemMeta
            }

            /* Apply button */
            Material.LEVER -> {
                if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                if (clickedItemMeta.displayName != applyButtonDisplayName) return

                NServerFeatures.saveEditableServerFeature(editableServerFeature)

                player.closeInventory()

                neon.server.onlinePlayers.forEach { onlinePlayer ->
                    if (!onlinePlayer.isOp) return@forEach

                    if (onlinePlayer == player) return@forEach

                    onlinePlayer.sendMessage("${ChatColor.GOLD}${player.name}${ChatColor.YELLOW} has made changes to the nServerFeatures.")
                }

                player.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Please reload the server to apply the effects!"))
            }

            /* Navigation button */
            Material.SPECTRAL_ARROW -> {
                if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                when (clickedItemMeta.displayName) {
                    previousButtonDisplayName -> {
                        if (pageIndex == 0) return

                        NServerFeatures.isNavigating = true
                        pageIndex--
                        super.openGUI()
                    }

                    nextButtonDisplayName -> {
                        if ((itemIndex + 1) >= serverFeatureNames.size) return

                        NServerFeatures.isNavigating = true
                        pageIndex++
                        super.openGUI()
                    }
                }
            }

            /* Close button */
            Material.BARRIER -> {
                if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                if (clickedItemMeta.displayName != closeButtonDisplayName) return

                player.closeInventory()
            }

            Material.NAME_TAG -> {
                if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                when (clickedItemMeta.displayName) {
                    /* Toggle option value button */
                    toggleOptionValueDisplayName -> {
                        val clickedItemLore: ArrayList<String> = clickedItemMeta.lore as ArrayList<String>

                        val statusVisible = "${ChatColor.GRAY}Status: ${ChatColor.GREEN}Visible!"
                        val statusHidden = "${ChatColor.GRAY}Status: ${ChatColor.RED}Hidden!"

                        when (isOptionVisible) {
                            true -> {
                                isOptionVisible = false
                                clickedItemLore[clickedItemLore.indexOf(statusVisible)] = statusHidden

                                clickedItemMeta.removeEnchant(NItemGlinter.ItemGlinterType.NGUI_BUTTON_GLINT.glint)
                            }

                            false -> {
                                isOptionVisible = true
                                clickedItemLore[clickedItemLore.indexOf(statusHidden)] = statusVisible

                                clickedItemMeta.addEnchant(NItemGlinter.ItemGlinterType.NGUI_BUTTON_GLINT.glint, 0, true)
                            }
                        }

                        clickedItemMeta.lore = clickedItemLore
                        clickedItem.itemMeta = clickedItemMeta

                        inventory.clear()
                        setGUIButtons()
                    }

                    /* Sort type button */
                    sortTypeButtonDisplayName -> {
                        val clickedItemLore: ArrayList<String> = clickedItemMeta.lore as ArrayList<String>

                        val typeIndex = SortingType.entries.indexOf(SortingType.entries.single { it.type == clickedItemLore.first() })

                        when (typeIndex + 1) {
                            1 -> {
                                clickedItemLore[0] = SortingType.STABLE.type
                                sortingType = SortingType.STABLE
                            }

                            2 -> {
                                clickedItemLore[0] = SortingType.EXPERIMENTAL.type
                                sortingType = SortingType.EXPERIMENTAL
                            }

                            else -> {
                                clickedItemLore[0] = SortingType.DEFAULT.type
                                sortingType = SortingType.DEFAULT
                            }
                        }

                        clickedItemMeta.lore = clickedItemLore
                        clickedItem.itemMeta = clickedItemMeta

                        inventory.clear()

                        serverFeatureNames = NServerFeatures.getServerFeatureNames(sortingType, sortingOrder)
                        pageIndex = 0
                        setGUIButtons()
                    }

                    /* Sort order button */
                    sortOrderButtonDisplayName -> {
                        val clickedItemLore: ArrayList<String> = clickedItemMeta.lore as ArrayList<String>

                        val orderIndex = SortingOrder.entries.indexOf(SortingOrder.entries.single { it.order == clickedItemLore.first() })

                        when (orderIndex + 1) {
                            1 -> {
                                clickedItemLore[0] = SortingOrder.DESCENDING.order
                                sortingOrder = SortingOrder.DESCENDING
                            }

                            else -> {
                                clickedItemLore[0] = SortingOrder.ASCENDING.order
                                sortingOrder = SortingOrder.ASCENDING
                            }
                        }

                        clickedItemMeta.lore = clickedItemLore
                        clickedItem.itemMeta = clickedItemMeta

                        inventory.clear()

                        serverFeatureNames = NServerFeatures.getServerFeatureNames(sortingType, sortingOrder)
                        pageIndex = 0
                        setGUIButtons()
                    }
                }
            }

            else -> {
                return
            }
        }
    }
}