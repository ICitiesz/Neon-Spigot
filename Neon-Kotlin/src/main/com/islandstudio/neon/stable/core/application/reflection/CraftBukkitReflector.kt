package com.islandstudio.neon.stable.core.application.reflection

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject

object CraftBukkitReflector: ModuleInjector {
    private val neon by inject<Neon>()

    /**
     * Get craft bukkit class
     *
     * @param className The class name. [E.g.: inventory.CraftInventory]
     * @return The CraftBukkit class
     */
    fun getCraftBukkitClass(className: String): Class<*> {
        val craftBukkitVersion = neon.server.javaClass.name.split(".")[3]

        return Class.forName("org.bukkit.craftbukkit.${craftBukkitVersion}.$className")
    }

    fun getCraftItemStackClass(): Class<*> = getCraftBukkitClass("inventory.CraftItemStack")

    fun getCraftItemStackClassByItemStack(itemStack: ItemStack): Class<*> = itemStack.javaClass
}