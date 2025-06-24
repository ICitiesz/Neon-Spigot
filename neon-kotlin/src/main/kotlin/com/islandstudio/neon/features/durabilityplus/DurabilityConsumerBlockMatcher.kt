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

    private val nonStrippedWoodBlocks = NeonItemMaterial.buildItemMaterialList {
        addAll(Material.entries
            .filter { x -> Tag.LOGS.isTagged(x) }
            .filterNot { x -> x.name.startsWith("stripped_", true) }
        )
    }

    private val exposedCopperBlocks = NeonItemMaterial.buildItemMaterialList {
        addAll(Material.entries.filter { x ->
            x.name.startsWith("exposed_", true) && x.name.contains("copper", true)
        })
    }

    private val weatheredCopperBlocks = NeonItemMaterial.buildItemMaterialList {
        addAll(Material.entries.filter { x ->
            x.name.startsWith("weathered_", true) && x.name.contains("copper", true)
        })
    }

    private val oxidizedCopperBlocks = NeonItemMaterial.buildItemMaterialList {
        addAll(Material.entries.filter { x ->
            x.name.startsWith("oxidized_", true) && x.name.contains("copper", true)
        })
    }

    private val waxedCopperBlocks = NeonItemMaterial.buildItemMaterialList {
        addAll(Material.entries.filter { x ->
            x.name.startsWith("waxed_", true) && x.name.contains("copper", true)
        })
    }

    private val pathBlocks = NeonItemMaterial.buildItemMaterialList {
        add(Material.GRASS_BLOCK)
        add(Material.DIRT)
        add(Material.COARSE_DIRT)
        add(Material.ROOTED_DIRT)
    }

    fun matchConsumerBlockItem(blockItem: ItemStack): Boolean {
        return blockItem.type in interactiveBlocks
    }

    fun matchesNonStrippedWoodBlockItems(blockItem: ItemStack): Boolean {
        return blockItem.type in nonStrippedWoodBlocks
    }

    fun matchesExposedCopperBlockItems(blockItem: ItemStack): Boolean {
        return blockItem.type in exposedCopperBlocks
    }

    fun matchesWeatheredCopperBlockItems(blockItem: ItemStack): Boolean {
        return blockItem.type in weatheredCopperBlocks
    }

    fun matchesOxidizedCopperBlockItems(blockItem: ItemStack): Boolean {
        return blockItem.type in oxidizedCopperBlocks
    }

    fun matchesWaxedCopperBlockItems(blockItem: ItemStack): Boolean {
        return blockItem.type in waxedCopperBlocks
    }

    fun matchesPathBlockITems(blockItem: ItemStack): Boolean {
        return blockItem.type in pathBlocks
    }

    fun matchesCreativeOnlyBlockItems(blockItem: ItemStack): Boolean {
        return blockItem.type in creativeOnlyBlocks
    }
}