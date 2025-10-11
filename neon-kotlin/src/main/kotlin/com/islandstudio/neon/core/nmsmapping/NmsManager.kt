package com.islandstudio.neon.core.nmsmapping

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.islandstudio.neon.Neon
import com.islandstudio.neon.core.initialization.NeonPluginLoader
import com.islandstudio.neon.core.nmsmapping.type.NmsClass
import com.islandstudio.neon.core.nmsmapping.type.NmsConstructor
import com.islandstudio.neon.core.nmsmapping.type.NmsField
import com.islandstudio.neon.core.nmsmapping.type.NmsMethod
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.initialization.IRunner
import com.islandstudio.neon.shared.core.io.resource.NeonInternalResource
import com.islandstudio.neon.shared.core.io.resource.ResourceManager
import com.islandstudio.neon.shared.utils.data.DataUtil
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
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

    fun toNmsPlayer(bukkitPlayer: Player): ServerPlayer {
        return DataUtil.asType(bukkitPlayer.javaClass.getMethod("getHandle").invoke(bukkitPlayer))
    }

    fun toBukkitPlayer(nmsPlayer: ServerPlayer): Player {
        return DataUtil.asType(nmsPlayer.javaClass.getMethod(getNmsMethod(NmsMethod.GetBukkitEntity)).invoke(nmsPlayer))
    }

    inline fun <reified T: Entity>toBukkitEntity(nmsEntity: net.minecraft.world.entity.Entity): T {
        return DataUtil.asType(nmsEntity.javaClass.getMethod("getBukkitEntity").invoke(nmsEntity))
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

    fun toNmsAttributeModifier(bukkitAttributeModifier: AttributeModifier): net.minecraft.world.entity.ai.attributes.AttributeModifier {
        return DataUtil.asType(getCraftBukkitClass("attribute.CraftAttributeInstance")
            .getMethod("convert", AttributeModifier::class.java)
            .invoke(null, bukkitAttributeModifier)
        )
    }

    fun toBukkitAttributeModifier(nmsAttributeModifier: net.minecraft.world.entity.ai.attributes.AttributeModifier): AttributeModifier {
        return DataUtil.asType(getCraftBukkitClass("attribute.CraftAttributeInstance")
            .getMethod("convert", net.minecraft.world.entity.ai.attributes.AttributeModifier::class.java)
            .invoke(null, nmsAttributeModifier)
        )
    }

    fun toNmsAttribute(bukktiAttribute: Attribute): net.minecraft.world.entity.ai.attributes.Attribute {
        return DataUtil.asType(getCraftBukkitClass("attribute.CraftAttribute")
            .getMethod("bukkitToMinecraft", Attribute::class.java)
            .invoke(null, bukktiAttribute)
        )
    }

    fun toBukkitAttribute(nmsAttribute: net.minecraft.world.entity.ai.attributes.Attribute): Attribute {
        return DataUtil.asType(getCraftBukkitClass("attribute.CraftAttribute")
            .getMethod("minecraftToBukkit", net.minecraft.world.entity.ai.attributes.Attribute::class.java)
            .invoke(null, nmsAttribute)
        )
    }

    interface INmsMapper {
        fun mapField(nmsField: NmsField): String = getNmsField(nmsField)

        fun mapMethod(nmsMethod: NmsMethod): String = getNmsMethod(nmsMethod)

        fun mapConstructor(nmsConstructor: NmsConstructor): String = getNmsConstructor(nmsConstructor)

        fun mapClass(nmsClass: NmsClass): String = getNmsClass(nmsClass)
    }
}