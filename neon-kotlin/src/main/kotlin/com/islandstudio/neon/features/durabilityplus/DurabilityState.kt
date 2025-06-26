package com.islandstudio.neon.features.durabilityplus

import org.bukkit.inventory.ItemStack

data class DurabilityState(
    val itemStack: ItemStack,
    val isBroken: Boolean
)
