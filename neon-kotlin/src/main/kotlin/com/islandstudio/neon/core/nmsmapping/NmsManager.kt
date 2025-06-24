package com.islandstudio.neon.core.nmsmapping

import com.islandstudio.neon.Neon
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.utils.data.DataUtil
import net.minecraft.core.BlockPos
import net.minecraft.world.item.Item
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject


object NmsManager: IComponentInjector, IRunner {
    private val neon by inject<Neon>()

    override fun run() {

    }

    fun getCraftBukkitClass(clazzName: String): Class<*> {
        val craftBukkitVersion = neon.server.javaClass.name.split(".")[3]

        return Class.forName("org.bukkit.craftbukkit.${craftBukkitVersion}.$clazzName")
    }

    fun toNmsItemStack(bukkitItemStack: ItemStack): net.minecraft.world.item.ItemStack {
        return DataUtil.asType(getCraftBukkitClass("inventory.CraftItemStack")
            .getMethod("asNMSCopy", ItemStack::class.java)
            .invoke(null, bukkitItemStack)
        )
    }

    fun toNmsItem(bukkitItemStack: ItemStack): Item {
        val nmsItemStack = toNmsItemStack(DataUtil.asType(bukkitItemStack))

        // TODO: Need add to nms mapping
        return DataUtil.asType(nmsItemStack.javaClass.getMethod("d").invoke(nmsItemStack))
    }

    fun toBukkitItemStack(nmsItemStack: net.minecraft.world.item.ItemStack): ItemStack {
        return DataUtil.asType(getCraftBukkitClass("inventory.CraftItemStack")
            .getMethod("copyNMSStack", ItemStack::class.java)
            .invoke(null, nmsItemStack)
        )
    }

    fun toNmsBlockLocation(bukkitLocation: Location): BlockPos {
        return DataUtil.asType(getCraftBukkitClass("util.CraftLocation")
            .getMethod("toBlockPosition", Location::class.java)
            .invoke(null, bukkitLocation))
    }
}