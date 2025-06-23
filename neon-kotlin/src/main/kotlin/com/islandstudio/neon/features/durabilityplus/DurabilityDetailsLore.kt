package com.islandstudio.neon.features.durabilityplus

import com.islandstudio.neon.item.DetailLoreBuilder
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.util.ColorUtil
import org.bukkit.ChatColor
import org.bukkit.inventory.meta.ItemMeta

data class DurabilityDetailsLore(
    val isEnabled: Boolean,
    val showItemDurability: Boolean,
    val itemDamageCount: Int,
    val itemMaxDurability: Int,
): DetailLoreBuilder(), IComponentInjector {
    private val damageTag = "${ColorUtil.fromHex("#ab0000")}BROKEN"

    override fun setDetailLore(itemMeta: ItemMeta, isBroken: Boolean) {
        val detailLoreText = buildString {
            if (isEnabled && showItemDurability) {
                val currentDurabilityPercentage = with(String.format(
                    "%.2f", ((itemMaxDurability - itemDamageCount) / itemMaxDurability.toDouble()) * 100
                )) {
                    return@with when(this.toDouble()) {
                        100.0 -> "${ColorUtil.fromHex("#00ba19")}${this}%"

                        in 75.0 .. 99.99 -> "${ColorUtil.fromHex("#5bff14")}${this}%"

                        in 50.0 .. 74.99 ->  "${ColorUtil.fromHex("#ddff00")}${this}%"

                        in 25.0 .. 49.99 ->  "${ColorUtil.fromHex("#ffbb00")}${this}%"

                        in 0.01 ..  24.99 ->  "${ColorUtil.fromHex("#ff4538")}${this}%"

                        0.0 ->  damageTag

                        else -> return
                    }
                }

                append("${ChatColor.GRAY}Durability: $currentDurabilityPercentage")
            } else if (isEnabled && isBroken) {
                append(damageTag)
            }
        }

        if (detailLoreText.isEmpty()) {
            itemMeta.lore = null
            return
        }

        buildDetailLore(itemMeta) {
            add("")
            add(detailLoreText)
        }
    }
}
