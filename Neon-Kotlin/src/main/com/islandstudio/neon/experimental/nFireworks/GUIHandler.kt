package com.islandstudio.neon.experimental.nFireworks

import com.islandstudio.neon.experimental.utils.NItemGlinter
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import org.bukkit.ChatColor
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*
import kotlin.math.ceil

class GUIHandler(nGUI: NGUI): GUIBuilder(nGUI) {
    private val player: Player = nGUI.getGUIOwner()
    //private var selectedImageName = ""
    private val imageFileNames = NFireworks.Handler.getImageFileNames()
//    private val imageFileNames = listOf(
//    "test1.jpg", "test2.jpg", "test3.jpg", "test4.jpg", "test5.jpg", "test6.jpg", "test7.jpg",
//    "test8.jpg", "test9.jpg", "test10.jpg", "test11.jpg", "test12.jpg", "test13.jpg", "test14.jpg",
//    "test15.jpg", "test16.jpg", "test17.jpg", "test18.jpg", "test19.jpg", "test20.jpg", "test21.jpg",
//    "test22.jpg", "test23.jpg", "test24.jpg", "test25.jpg", "test26.jpg", "test27.jpg", "test28.jpg",
//    "test29.jpg", "test30.jpg","test31.jpg","test32.jpg","test33.jpg","test34.jpg", "test35.jpg",
//    "test36.jpg", "test37.jpg","test38.jpg","test39.jpg","test40.jpg","test41.jpg","test42.jpg","test43.jpg",
//    "test44.jpg", "test45.jpg","test46.jpg","test47.jpg","test48.jpg","test49.jpg","test50.jpg","test51.jpg",
//    "test52.jpg", "test53.jpg","test54.jpg","test55.jpg","test56.jpg","test57.jpg","test58.jpg","test59.jpg")

    override fun getGUIName(): String = super.guiState.stateName

    override fun getGUISlots(): Int {
        if (guiState != GUIState.FIREWORK_CONFIGURATION) return 54

        return 18
    }

    override fun setGUIButtons() {
        if (guiState != GUIState.FIREWORK_CONFIGURATION) {
            maxPage = ceil(imageFileNames.size.toDouble() / maxItemPerPage.toDouble()).toInt()

            addNavigationButtons()

            for (i in 0 until super.maxItemPerPage) {
                itemIndex = super.maxItemPerPage * pageIndex + i

                if (itemIndex >= imageFileNames.size) break

                val imageFileItem = ItemStack(Material.PAINTING)
                val imageFileItemMeta = imageFileItem.itemMeta!!

                val detailsContainer: LinkedList<String> = LinkedList()
                val imageFileName = "${ChatColor.GOLD}${imageFileNames[itemIndex]}"

                if (fireworkEffects.imageName.isNotEmpty() && imageFileNames[itemIndex] == fireworkEffects.imageName) {
                    imageFileItemMeta.addEnchant(NItemGlinter.ItemGlinterType.NGUI_BUTTON_GLINT.glint, 0, true)
                }

                imageFileItemMeta.setDisplayName(imageFileName)
                imageFileItemMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

                detailsContainer.add("${ChatColor.GRAY}Test Detail")
                imageFileItemMeta.lore = detailsContainer

                imageFileItem.itemMeta = imageFileItemMeta

                inventory.addItem(imageFileItem)
            }

            return
        }

        addNavigationButtons()

        /* Firework property button */
        val propertyBtn = ItemStack(Material.FIREWORK_STAR)

        propertyBtnNames.keys.sorted().forEach {
            val propertyBtnMeta = propertyBtn.itemMeta!!

            propertyBtnMeta.setDisplayName(propertyBtnNames[it])
            propertyBtnMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, it)
            propertyBtnMeta.addEnchant(NItemGlinter.ItemGlinterType.NGUI_BUTTON_GLINT.glint, 0, true)

            val minValueText = "${ChatColor.GRAY}[${ChatColor.GOLD}Min${ChatColor.GRAY}: ${ChatColor.GREEN}"
            val maxValueText = "${ChatColor.GRAY}, ${ChatColor.GOLD}Max${ChatColor.GRAY}: ${ChatColor.GREEN}"

            when (it) {
                "amountBtnName" -> {
                    val amountBtnLore: List<String> = listOf(
                        "${ChatColor.YELLOW}The amount of the firework. ${minValueText}1${maxValueText}64${ChatColor.GRAY}]",
                        "${ChatColor.LIGHT_PURPLE}Shift-L.Click: +10 | Shift-R.Click: -10",
                        "${ChatColor.GRAY}Current: ${ChatColor.GREEN}${fireworkEffects.fireworkAmount}")

                    propertyBtnMeta.lore = amountBtnLore
                }

                "colorBtnName" -> {
                    val colorBtnLore: List<String> = listOf(
                        "${ChatColor.YELLOW}Primary color for the firework when explode.",
                        "${ChatColor.GRAY}Current: ${fireworkEffects.fireworkColor.coloredName}")

                    propertyBtnMeta.lore = colorBtnLore
                }

                "explosionTypeBtnName" -> {
                    val explosionTypeBtnLore: List<String> = listOf(
                        "${ChatColor.YELLOW}Explosion type for the firework when explode.",
                        "${ChatColor.GRAY}Current: ${fireworkEffects.getExplosionTypeColoredName()}")

                    propertyBtnMeta.lore = explosionTypeBtnLore
                }

                "patternFacing" -> {
                    val patternFacingBtnLore: List<String> = listOf(
                        "${ChatColor.YELLOW}Firework pattern facing when explode.",
                        "${ChatColor.GRAY}Current: ${fireworkEffects.getFireworkPatternFacingName(player)}"
                    )

                    propertyBtnMeta.lore = patternFacingBtnLore
                }

                "powerBtnName" -> {
                    val powerBtnLore: List<String> = listOf(
                        "${ChatColor.YELLOW}The power(height) of the firework. ${minValueText}0${maxValueText}127${ChatColor.GRAY}]",
                        "${ChatColor.LIGHT_PURPLE}Shift-L.Click: +10 | Shift-R.Click: -10",
                        "${ChatColor.GRAY}Current: ${ChatColor.GREEN}${fireworkEffects.fireworkPower}",)

                    propertyBtnMeta.lore = powerBtnLore
                }

                "withFadeBtnName" -> {
                    val withFadeBtnLore: List<String> = listOf(
                        "${ChatColor.YELLOW}Add fade effect to the firework.",
                        "${ChatColor.LIGHT_PURPLE}Shift-L.Click: Next Color | Shift-R.Click: Previous Color",
                        "${ChatColor.GRAY}Current: ${fireworkEffects.getToggleColoredName(fireworkEffects.fireworkWithFade)} ${ChatColor.GRAY}| ${fireworkEffects.fireworkWithFadeColor.coloredName}")

                    propertyBtnMeta.lore = withFadeBtnLore
                }

                "withFlickerBtnName" -> {
                    val withFlickerBtnLore: List<String> = listOf(
                        "${ChatColor.YELLOW}Add flicker effect to the firework.",
                        "${ChatColor.GRAY}Current: ${fireworkEffects.getToggleColoredName(fireworkEffects.fireworkWithFlicker)}")

                    propertyBtnMeta.lore = withFlickerBtnLore
                }

                "withTrailBtnName" -> {
                    val withTrailBtnLore: List<String> = listOf(
                        "${ChatColor.YELLOW}Add trail effect to the firework.",
                        "${ChatColor.GRAY}Current: ${fireworkEffects.getToggleColoredName(fireworkEffects.fireworkWithTrail)}")

                    propertyBtnMeta.lore = withTrailBtnLore
                }
            }

            propertyBtn.itemMeta = propertyBtnMeta

            inventory.addItem(propertyBtn)
        }
    }

    override fun setGUIClickHandler(e: InventoryClickEvent) {
        val clickedItem = e.currentItem!!
        val clickedItemMeta = clickedItem.itemMeta!!
        val persistentDataContainer = clickedItemMeta.persistentDataContainer

        when (clickedItem.type) {
            /* Image file button */
            Material.PAINTING -> {
                if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                if (guiState != GUIState.FIREWORK_PATTERN_SELECTION) return

                val clickedItemDisplayName  = clickedItemMeta.displayName.replace("${ChatColor.GOLD}", "")

                if (!imageFileNames.contains(clickedItemDisplayName)) return

                if (clickedItemMeta.hasEnchant(NItemGlinter.ItemGlinterType.NGUI_BUTTON_GLINT.glint)) return

                fireworkEffects.imageName = clickedItemDisplayName

                inventory.clear()
                setGUIButtons()
            }

            /* Firework propery button */
            Material.FIREWORK_STAR -> {
                if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                if (guiState != GUIState.FIREWORK_CONFIGURATION) return

                val fireworkPropertyName = persistentDataContainer.get(buttonIDKey, PersistentDataType.STRING)!!
                val clickedButtonLore = clickedItemMeta.lore

                val currentValue = clickedButtonLore?.find {
                    it.startsWith("${ChatColor.GRAY}Current:")
                } ?: return

                val applyBtn = inventory.contents.filterNotNull().filter { it.itemMeta!!
                    .persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING) }
                    .find { it.itemMeta!!.displayName == applyBtnName } ?: return

                val applyBtnMeta = applyBtn.itemMeta!!
                val applyBtnLore = applyBtnMeta.lore!!

                val clickType = e.click
                lateinit var fireworkPropertyValue: String

                when (fireworkPropertyName) {
                    /* Firework amount button */
                    "amountBtnName" -> {
                        when (clickType) {
                            ClickType.LEFT -> {
                                if (fireworkEffects.fireworkAmount.toInt() == 64) return

                                fireworkEffects.fireworkAmount++
                            }

                            ClickType.RIGHT -> {
                                if (fireworkEffects.fireworkAmount.toInt() == 1) return

                                fireworkEffects.fireworkAmount--
                            }

                            ClickType.SHIFT_LEFT -> {
                                if (fireworkEffects.fireworkAmount.toInt() == 64) return

                                val newValue = fireworkEffects.fireworkAmount + 10

                                if (newValue >= 64) {
                                    fireworkEffects.fireworkAmount = 64
                                } else {
                                    fireworkEffects.fireworkAmount = newValue.toByte()
                                }
                            }

                            ClickType.SHIFT_RIGHT -> {
                                if (fireworkEffects.fireworkAmount.toInt() == 1) return

                                val newValue = fireworkEffects.fireworkAmount - 10

                                if (newValue <= 1) {
                                    fireworkEffects.fireworkAmount = 1
                                } else {
                                    fireworkEffects.fireworkAmount = newValue.toByte()
                                }
                            }

                            else -> { return }
                        }

                        fireworkPropertyValue = "${ChatColor.GREEN}${fireworkEffects.fireworkAmount}"

                        applyBtnLore[1] = "${ChatColor.GRAY}Amount: ${ChatColor.GREEN}${fireworkEffects.fireworkAmount}"
                    }

                    /* Firework color button */
                    "colorBtnName" -> {
                        val sortedFireworkColors = FireworkProperty.FireworkColors.entries.toTypedArray().sorted()
                        var currentFireworkColorIndex = sortedFireworkColors.indexOf(fireworkEffects.fireworkColor)

                        when (clickType) {
                            ClickType.LEFT -> {
                                currentFireworkColorIndex++

                                if (currentFireworkColorIndex >= sortedFireworkColors.size) {
                                    currentFireworkColorIndex = 0
                                }
                            }

                            ClickType.RIGHT -> {
                                currentFireworkColorIndex--

                                if (currentFireworkColorIndex < 0) {
                                    currentFireworkColorIndex = sortedFireworkColors.size - 1
                                }
                            }

                            else -> { return }
                        }

                        fireworkEffects.fireworkColor = sortedFireworkColors[currentFireworkColorIndex]

                        fireworkPropertyValue = fireworkEffects.fireworkColor.coloredName

                        applyBtnLore[2] = "${ChatColor.GRAY}Color: ${fireworkEffects.fireworkColor.coloredName}"
                    }

                    /* Firework explosion type */
                    "explosionTypeBtnName" -> {
                        val sortedFireworkExplosionTypes = FireworkEffect.Type.entries.toTypedArray().sorted()
                        var currentFireowkrExplosionTypeIndex = sortedFireworkExplosionTypes.indexOf(fireworkEffects.fireworkExplosionType)

                        when (clickType) {
                            ClickType.LEFT -> {
                                currentFireowkrExplosionTypeIndex++

                                if (currentFireowkrExplosionTypeIndex >= sortedFireworkExplosionTypes.size) {
                                    currentFireowkrExplosionTypeIndex = 0
                                }
                            }

                            ClickType.RIGHT -> {
                                currentFireowkrExplosionTypeIndex--

                                if (currentFireowkrExplosionTypeIndex < 0) {
                                    currentFireowkrExplosionTypeIndex = sortedFireworkExplosionTypes.size - 1
                                }
                            }

                            else -> { return }
                        }

                        fireworkEffects.fireworkExplosionType = sortedFireworkExplosionTypes[currentFireowkrExplosionTypeIndex]
                        fireworkPropertyValue = fireworkEffects.getExplosionTypeColoredName()

                        applyBtnLore[3] = "${ChatColor.GRAY}Explosion Type: ${fireworkEffects.getExplosionTypeColoredName()}"
                    }

                    /* Firework pattern facing */
                    "patternFacing" -> {
                        val sortedFireworkPatternFacingOptions = FireworkProperty.FireworkPatternFacingOptions.entries.toTypedArray().sorted()
                        var currentFireowkrPatternFacingIndex = sortedFireworkPatternFacingOptions.indexOf(fireworkEffects.fireworkPatternFacingOptions)

                        when (clickType) {
                            ClickType.LEFT -> {
                                currentFireowkrPatternFacingIndex++

                                if (currentFireowkrPatternFacingIndex >= sortedFireworkPatternFacingOptions.size) {
                                    currentFireowkrPatternFacingIndex = 0
                                }
                            }

                            ClickType.RIGHT -> {
                                currentFireowkrPatternFacingIndex--

                                if (currentFireowkrPatternFacingIndex < 0) {
                                    currentFireowkrPatternFacingIndex = sortedFireworkPatternFacingOptions.size - 1
                                }
                            }

                            else -> { return }
                        }

                        fireworkEffects.updateFireworkPatternFacingOptions(sortedFireworkPatternFacingOptions[currentFireowkrPatternFacingIndex])

                        fireworkPropertyValue = fireworkEffects.getFireworkPatternFacingName(player)

                        applyBtnLore[4] = "${ChatColor.GRAY}Pattern Facing: ${fireworkEffects.getFireworkPatternFacingName(player)}"
                    }

                    /* Firework launch power button */
                    "powerBtnName" -> {
                        when (clickType) {
                            ClickType.LEFT -> {
                                if (fireworkEffects.fireworkPower.toInt() == 127) return

                                fireworkEffects.fireworkPower++
                            }

                            ClickType.RIGHT -> {
                                if (fireworkEffects.fireworkPower.toInt() == 0) return

                                fireworkEffects.fireworkPower--
                            }

                            ClickType.SHIFT_LEFT -> {
                                if (fireworkEffects.fireworkPower.toInt() == 127) return

                                val newValue = fireworkEffects.fireworkPower + 10

                                if (newValue >= 127) {
                                    fireworkEffects.fireworkPower = 127
                                } else {
                                    fireworkEffects.fireworkPower = newValue.toByte()
                                }
                            }

                            ClickType.SHIFT_RIGHT -> {
                                if (fireworkEffects.fireworkPower.toInt() == 0) return

                                val newValue = fireworkEffects.fireworkPower - 10

                                if (newValue <= 0) {
                                    fireworkEffects.fireworkPower = 0
                                } else {
                                    fireworkEffects.fireworkPower = newValue.toByte()
                                }
                            }

                            else -> { return }
                        }

                        fireworkPropertyValue = "${ChatColor.GREEN}${fireworkEffects.fireworkPower}"

                        applyBtnLore[5] = "${ChatColor.GRAY}Power: ${ChatColor.GREEN}${fireworkEffects.fireworkPower}"
                    }

                    /* Firework fade effect button */
                    "withFadeBtnName" -> {
                        val sortedFireworkFadeColors = FireworkProperty.FireworkColors.entries.toTypedArray().sorted()
                        var currentFireworkFadeColorIndex = sortedFireworkFadeColors.indexOf(fireworkEffects.fireworkWithFadeColor)

                        when (clickType) {
                            ClickType.LEFT, ClickType.RIGHT -> {
                                fireworkEffects.fireworkWithFade = !fireworkEffects.fireworkWithFade
                            }

                            ClickType.SHIFT_LEFT -> {
                                currentFireworkFadeColorIndex++

                                if (currentFireworkFadeColorIndex >= sortedFireworkFadeColors.size) {
                                    currentFireworkFadeColorIndex = 0
                                }
                            }

                            ClickType.SHIFT_RIGHT -> {
                                currentFireworkFadeColorIndex--

                                if (currentFireworkFadeColorIndex < 0) {
                                    currentFireworkFadeColorIndex = sortedFireworkFadeColors.size - 1
                                }
                            }

                            else -> { return }
                        }

                        fireworkEffects.fireworkWithFadeColor = sortedFireworkFadeColors[currentFireworkFadeColorIndex]
                        fireworkPropertyValue = fireworkEffects.getWithFadeColoredName()

                        applyBtnLore[6] = "${ChatColor.GRAY}With Fade: ${fireworkEffects.getWithFadeColoredName()}"
                    }

                    /* Firework flicker effect button */
                    "withFlickerBtnName" -> {
                        fireworkEffects.fireworkWithFlicker = !fireworkEffects.fireworkWithFlicker
                        fireworkPropertyValue = fireworkEffects.getToggleColoredName(fireworkEffects.fireworkWithFlicker)

                        applyBtnLore[7] = "${ChatColor.GRAY}With Flicker: ${fireworkEffects.getToggleColoredName(fireworkEffects.fireworkWithFlicker)}"
                    }

                    /* Firework trail effect button */
                    "withTrailBtnName" -> {
                        fireworkEffects.fireworkWithTrail = !fireworkEffects.fireworkWithTrail
                        fireworkPropertyValue = fireworkEffects.getToggleColoredName(fireworkEffects.fireworkWithTrail)

                        applyBtnLore[8] = "${ChatColor.GRAY}With Fade: ${fireworkEffects.getToggleColoredName(fireworkEffects.fireworkWithTrail)}"
                    }
                }

                clickedButtonLore[clickedButtonLore.indexOf(currentValue)] = "${ChatColor.GRAY}Current: $fireworkPropertyValue"
                clickedItemMeta.lore = clickedButtonLore
                clickedItem.itemMeta = clickedItemMeta

                applyBtnMeta.lore = applyBtnLore
                applyBtn.itemMeta = applyBtnMeta
            }

            /* Clear & restore default selection button */
            Material.NAME_TAG -> {
                if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                when (guiState) {
                    GUIState.FIREWORK_PATTERN_SELECTION -> {
                        /* Clear selection button */
                        if (clickedItemMeta.displayName != clearSelectionBtnName) return

                        if (fireworkEffects.imageName.isEmpty()) return

                        fireworkEffects.imageName = ""
                    }

                    GUIState.FIREWORK_CONFIGURATION -> {
                        /* Restore default button */
                        if (clickedItemMeta.displayName != restoreDefaultBtnName) return

                        fireworkEffects.setDefault()
                    }
                }

                inventory.clear()
                setGUIButtons()
            }

            /* Navigation button */
            Material.SPECTRAL_ARROW -> {
                if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                if (guiState != GUIState.FIREWORK_PATTERN_SELECTION) return

                when (clickedItemMeta.displayName) {
                    previousBtnName -> {
                        if (pageIndex == 0) return

                        pageIndex--
                        super.openGUI()
                    }

                    nextBtnName -> {
                        if ((itemIndex + 1) >= imageFileNames.size) return

                        pageIndex++
                        super.openGUI()
                    }
                }
            }

            /* Close button */
            Material.BARRIER -> {
                if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                if (clickedItemMeta.displayName != closeBtnName) return

                player.closeInventory()
            }

            /* Proceed & apply button */
            Material.LEVER -> {
                when (guiState) {
                    GUIState.FIREWORK_PATTERN_SELECTION -> {
                        if (clickedItemMeta.displayName != proceedBtnName) return

                        if (fireworkEffects.imageName.isEmpty()) return

                        guiState = GUIState.FIREWORK_CONFIGURATION
                        super.openGUI()
                    }

                    GUIState.FIREWORK_CONFIGURATION -> {
                        when (clickedItemMeta.displayName) {
                            backBtnName -> {
                                guiState = GUIState.FIREWORK_PATTERN_SELECTION
                                super.openGUI()
                            }

                            applyBtnName -> {
                                player.inventory.addItem(NFireworks.createFirework(fireworkEffects, player))
                                NFireworks.Handler.createPatternFrameData(fireworkEffects.imageName)
                                player.closeInventory()
                            }
                        }
                    }
                }
            }

            else -> { return }
        }
     }

}