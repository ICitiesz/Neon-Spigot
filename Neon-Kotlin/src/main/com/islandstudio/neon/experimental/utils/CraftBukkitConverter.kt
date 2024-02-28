package com.islandstudio.neon.experimental.utils

import com.islandstudio.neon.stable.utils.reflection.NReflector
import net.minecraft.world.item.ItemStack

object CraftBukkitConverter {
    fun bukkitItemStackToNMSItemStack(bukkitItemStack: org.bukkit.inventory.ItemStack): ItemStack {
        return NReflector.CraftBukkitReflector.getCraftItemStackClass().getMethod("asNMSCopy", org.bukkit.inventory.ItemStack::class.java)
                .invoke(null, bukkitItemStack) as ItemStack
    }

    fun nmsItemStackToBukkitItemStack(nmsItemStack: ItemStack): org.bukkit.inventory.ItemStack {
        return NReflector.CraftBukkitReflector.getCraftItemStackClass().getMethod("copyNMSStack", ItemStack::class.java)
            .invoke(null, nmsItemStack) as org.bukkit.inventory.ItemStack
    }

}