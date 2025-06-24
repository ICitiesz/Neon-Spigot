package com.islandstudio.neon.item

import com.google.common.collect.ImmutableList
import org.bukkit.Material

/**
 * Used to declare used materials that from the latest version of Minecraft.
 * This is to ensure compatibility across versions where some of the material may
 * not available for older versions.
 *
 * @constructor Create empty Neon item material
 */
object NeonItemMaterial {
    val BRUSH = fromBukkitMaterial("brush")

    private fun fromBukkitMaterial(materialName: String): Material {
        return Material.matchMaterial(materialName) ?: Material.AIR
    }
    
    fun buildItemMaterialList(block: ArrayList<Material>.() -> Unit): ImmutableList<Material> {
        val itemMaterialList = ArrayList<Material>()
        
        block.invoke(itemMaterialList)
        
        return ImmutableList.copyOf(
            itemMaterialList
                .filter { x -> x != Material.AIR }
                .distinct()
        )
    }
}