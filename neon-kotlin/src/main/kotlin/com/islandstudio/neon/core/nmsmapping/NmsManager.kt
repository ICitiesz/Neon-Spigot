package com.islandstudio.neon.core.nmsmapping

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.islandstudio.neon.Neon
import com.islandstudio.neon.core.initialization.NeonPluginLoader
import com.islandstudio.neon.core.nmsmapping.type.NmsClass
import com.islandstudio.neon.core.nmsmapping.type.NmsConstructor
import com.islandstudio.neon.core.nmsmapping.type.NmsField
import com.islandstudio.neon.core.nmsmapping.type.NmsMethod
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.io.resource.NeonInternalResource
import com.islandstudio.neon.shared.core.io.resource.ResourceManager
import com.islandstudio.neon.shared.utils.data.DataUtil
import net.minecraft.core.BlockPos
import net.minecraft.world.item.Item
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.koin.core.component.inject


object NmsManager: NmsMapping(), IComponentInjector, IRunner {
    private val neon by inject<Neon>()

    override fun run() {
        NeonPluginLoader.displayInitMessage("neon.nms_manager.init.load_nms_mapping", 500)

        val appContext by inject<AppContext>()
        val nmsMappingDataStream = ResourceManager.getNeonResourceAsStream(NeonInternalResource.NeonNmsMapping2)

        csvReader().open(nmsMappingDataStream) {
            val nmsCsvData = readAllWithHeaderAsSequence().filter { data ->
                data["Versions"]?.let {
                    val versions = it.split(";").map { version -> version.trim() }

                    appContext.serverVersion in versions
                } ?: false
            }

            nmsCsvData.forEach { registerMapping(it) }
        }
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
        val nmsItemStack = toNmsItemStack(bukkitItemStack)

        // TODO: Need add to nms mapping
        return DataUtil.asType(nmsItemStack.javaClass.getMethod("d").invoke(nmsItemStack))
    }

    fun toBukkitItemStack(nmsItemStack: net.minecraft.world.item.ItemStack): ItemStack {
        return DataUtil.asType(getCraftBukkitClass("inventory.CraftItemStack")
            .getMethod("asCraftMirror", net.minecraft.world.item.ItemStack::class.java)
            .invoke(null, nmsItemStack)
        )
    }

    fun toNmsBlockLocation(bukkitLocation: Location): BlockPos {
        return DataUtil.asType(getCraftBukkitClass("util.CraftLocation")
            .getMethod("toBlockPosition", Location::class.java)
            .invoke(null, bukkitLocation))
    }

    interface INmsMapper {
        fun mapField(nmsField: NmsField): String = nmsFields[nmsField] ?: ""

        fun mapMethod(nmsMethod: NmsMethod): String = nmsMethods[nmsMethod] ?: ""

        fun mapConstructor(nmsConstructor: NmsConstructor): String = nmsConstructors[nmsConstructor] ?: ""

        fun mapClass(nmsClass: NmsClass): String = nmsClasses[nmsClass] ?: ""
    }
}