package com.islandstudio.neon.features.durabilityplus

import com.islandstudio.neon.item.NeonItemMaterial
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack


object DurabilityConsumerBlockMatcher {
    private val interactiveBlocks = NeonItemMaterial.buildItemMaterialList {
        add(Material.COMMAND_BLOCK)
        addAll(Material.entries.filter { x -> Tag.FENCES.isTagged(x) })
        add(Material.PUMPKIN)
    }

    private val creativeOnlyBlocks = NeonItemMaterial.buildItemMaterialList {
        add(Material.COMMAND_BLOCK)
        add(Material.CHAIN_COMMAND_BLOCK)
        add(Material.REPEATING_COMMAND_BLOCK)
        add(Material.JIGSAW)
        add(Material.STRUCTURE_BLOCK)
    }

    private val axeInteractiveBlocks = NeonItemMaterial.buildItemMaterialList {
        /* Non-stripped wood blocks */
        addAll(Material.entries
            .filter { x -> Tag.LOGS.isTagged(x) }
            .filterNot { x -> x.name.startsWith("stripped_", true) }
        )

        /* Exposed copper blocks */
        addAll(Material.entries.filter { x ->
            x.name.startsWith("exposed_", true) && x.name.contains("copper", true)
        })

        /* Weather copper block */
        addAll(Material.entries.filter { x ->
            x.name.startsWith("weathered_", true) && x.name.contains("copper", true)
        })

        /* Oxidized copper block */
        addAll(Material.entries.filter { x ->
            x.name.startsWith("oxidized_", true) && x.name.contains("copper", true)
        })

        /* Waxed copper blocks */
        addAll(Material.entries.filter { x ->
            x.name.startsWith("waxed_", true) && x.name.contains("copper", true)
        })
    }

    private val shearsInteractiveBlocks = NeonItemMaterial.buildItemMaterialList {
        add(Material.PUMPKIN)
    }

    private val brushableBlocks = NeonItemMaterial.buildItemMaterialList {
        add(NeonItemMaterial.SUSPICIOUS_SAND)
        add(NeonItemMaterial.SUSPICIOUS_GRAVEL)
    }

    private val pathBlocks = NeonItemMaterial.buildItemMaterialList {
        add(Material.GRASS_BLOCK)
        add(Material.DIRT)
        add(Material.COARSE_DIRT)
        add(Material.ROOTED_DIRT)
    }

    fun matchConsumerBlock(blockItem: ItemStack): Boolean {
        return blockItem.type in interactiveBlocks
    }

    fun matchesAxeInteractiveBlocks(blockType: Material): Boolean {
        return blockType in axeInteractiveBlocks
    }

    fun matchesPathBlocks(blockMaterial: Material): Boolean {
        return blockMaterial in pathBlocks
    }

    fun matchesCreativeOnlyBlocks(blockMaterial: Material): Boolean {
        return blockMaterial in creativeOnlyBlocks
    }

    fun matchesShearsInteractiveBlocks(blockMaterial: Material): Boolean {
        return blockMaterial in shearsInteractiveBlocks
    }

    fun matchesBrushableBlocks(blockMaterial: Material): Boolean {
        return blockMaterial in brushableBlocks
    }
}