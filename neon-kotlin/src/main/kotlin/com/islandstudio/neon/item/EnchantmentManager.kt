package com.islandstudio.neon.item

import com.islandstudio.neon.core.initialization.CompatibleVersions
import com.islandstudio.neon.core.nmsmapping.NmsMap
import com.islandstudio.neon.core.nmsmapping.NmsProcessor
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.utils.data.DataUtil
import com.islandstudio.neon.stable.core.application.reflection.CraftBukkitReflector
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderOwner
import net.minecraft.core.Registry
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentCategory
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.util.*
import javax.annotation.Nullable

@Single
class EnchantmentManager {
    /* NMS Registery Properties */
    private val NMS_REGISTERY_HOLDER_CLASS_PATH = "core.Holder"
    private val NMS_REGISTRIES_CLASS_PATH = "core.registries.BuiltInRegistries"
    private val NMS_REGISTRIES_BASE_CLASS_PATH = "core.IRegistry"
    private val NMS_REGISTERY_HOLDER_OWNER_PATH = "core.HolderOwner"
    private val NMS_REGISTRIES_GENERIC_TYPE_T = "T"

    private val appContext by inject<AppContext>()
    private val nmsProcessor = NmsProcessor()
    private val registriesBaseClass = nmsProcessor.getMcClass(NMS_REGISTRIES_BASE_CLASS_PATH)!!

    private val enchantmentRegistry = run {
        /* Get registries class based on the given class path */
        val registriesClass = when {
            nmsProcessor.hasMcClass(NMS_REGISTRIES_CLASS_PATH) -> {
                nmsProcessor.getMcClass(NMS_REGISTRIES_CLASS_PATH)
            }

            else -> registriesBaseClass
        }!!

        /* Get the enchantment registry */
        registriesClass.getField(NmsMap.EnchantmentRegistry.remapped).get(null).also {
            initOrGetUnregisterHolderContainer(DataUtil.asType(it))
        }
    }

    companion object: IRunner, IComponentInjector {
        private val enchantmentManager by inject<EnchantmentManager>()

        override fun run() {
            enchantmentManager.initialize()
        }
    }

    fun initialize() {
        /* Enable the ability to add registry entry */
        run enableAddRegistryEntry@ {
            if (appContext.serverMajorVersion.equals(CompatibleVersions.V1_17.getMajorVersion(), true)) {
                return@enableAddRegistryEntry
            }

            if (appContext.serverMajorVersion.equals(CompatibleVersions.V1_18.getMajorVersion(), true)
                && appContext.serverVersion != CompatibleVersions.V1_18.versions[2]) // V1_18.versions[2] = 1.18.2
            {
                return@enableAddRegistryEntry
            }

            enchantmentRegistry.javaClass.getDeclaredField(NmsMap.RegistryEntryToggleState.remapped).also {
                it.isAccessible = true
                it.set(enchantmentRegistry, false)
            }
        }

        NeonEnchantment.getAllNeonEnchantment().forEach { it.register() }

        /* Disable the ability to add registry entry */
        run disableAddRegistryEntry@ {
            if (appContext.serverMajorVersion.equals(CompatibleVersions.V1_17.getMajorVersion(), true)) {
                return@disableAddRegistryEntry
            }

            if (appContext.serverMajorVersion.equals(CompatibleVersions.V1_18.getMajorVersion(), true)
                && appContext.serverVersion != CompatibleVersions.V1_18.versions[2]) // V1_18.versions[2] = 1.18.2
            {
                return@disableAddRegistryEntry
            }

            initOrGetUnregisterHolderContainer(DataUtil.asType(enchantmentRegistry))
            enchantmentRegistry.javaClass.getMethod(NmsMap.RevokeRegistryEntry.remapped).invoke(enchantmentRegistry)
        }
    }

    /**
     * Register custom enchantment either effectless or effective
     *
     * @param T
     * @param neonEnchantment The Neon Enchantment
     * @param enchantment The Mc enchantment
     */
    fun <T: Enchantment> registerEnchantment(neonEnchantment: NeonEnchantment, enchantment: T) {
        /* Stage 1: Check the existance of target item glinter/enchantment */
        if (neonEnchantment.isRegistered()) return

        /* Stage 2:: Get unregister holder container */
        enchantmentManager.initOrGetUnregisterHolderContainer(DataUtil.asType(enchantmentManager.enchantmentRegistry), false)?.let {
            if (it.isNotEmpty()) return@let

            /* Add enchantment to the unregister holder container if it doesn't exist in the container */
            Holder.Reference::class.java.methods
                .filter { method -> Modifier.isPublic(method.modifiers) }
                .filter { method -> method.returnType == Holder.Reference::class.java }
                .filter { method -> method.parameterCount == 2 }
                .filter { method ->
                    if (enchantmentManager.nmsProcessor.hasMcClass(enchantmentManager.NMS_REGISTERY_HOLDER_OWNER_PATH)) {
                        method.parameterTypes.contains(HolderOwner::class.java) && method.parameterTypes.contains(
                            Any::class.java
                        )
                    } else {
                        method.parameterTypes.contains(Registry::class.java) && method.parameterTypes.contains(Any::class.java)
                    }

                }.apply {
                    if (this.isEmpty()) return@apply

                    val unregistryHolderReference = this.first().run {
                        if (enchantmentManager.nmsProcessor.hasMcClass(enchantmentManager.NMS_REGISTERY_HOLDER_OWNER_PATH)) {
                            val unregistryHolderOwner = enchantmentRegistry.javaClass.declaredFields
                                .filter { field -> Modifier.isPrivate(field.modifiers) && Modifier.isFinal(field.modifiers) }
                                .filter { field -> field.type == HolderLookup::class.java }
                                .apply unregistryHolderOwner@ {
                                    this@unregistryHolderOwner.first().isAccessible = true
                                    this@unregistryHolderOwner.first().get(enchantmentRegistry)
                                }
                            this.invoke(null, unregistryHolderOwner, enchantment)
                        } else {
                            this.invoke(null, enchantmentRegistry, enchantment)
                        }
                    }

                    it[enchantment] = unregistryHolderReference
                }

            /*.ifNotEmpty {
                    val unregistryHolderReference = this.first().run {
                        if (NReflector.hasNamespaceClass(NMS_REGISTERY_HOLDER_OWNER_PATH)) {
                            val unregistryHolderOwner = enchantmentRegistry.javaClass.declaredFields
                                .filter { field -> Modifier.isPrivate(field.modifiers) && Modifier.isFinal(field.modifiers) }
                                .filter { field -> field.type == HolderLookup::class.java }
                                .ifNotEmpty unregistryHolderOwner@ {
                                    this@unregistryHolderOwner.first().isAccessible = true
                                    this@unregistryHolderOwner.first().get(enchantmentRegistry)
                                }

                            this.invoke(null, unregistryHolderOwner, enchantment)
                        } else {
                            this.invoke(null, enchantmentRegistry, enchantment)
                        }
                    }

                    it[enchantment] = unregistryHolderReference
                }*/
        }

        /* Stage 3: Register item glint to server within NMS */
        val registeredItemGlintType = Registry::class.java.getMethod(
            NmsMap.RegisterEnchantment.remapped, Registry::class.java, String::class.java, Any::class.java)
            .invoke(null, enchantmentRegistry, neonEnchantment.dataKey.toString(), enchantment) as Enchantment

        /* Stage 3.1: Register item glinter to server within NMS if it is legacy */
        /* Legacy code for registering enchantment within Bukkit */
        val bukkitAcceptingNew = org.bukkit.enchantments.Enchantment::class.java.declaredFields.find { acceptingNewField -> acceptingNewField.name == "acceptingNew" }
        val bukkitRegisterEnchanment = org.bukkit.enchantments.Enchantment::class.java.methods.find { method -> method.name == "registerEnchantment" }

        if (bukkitAcceptingNew == null || bukkitRegisterEnchanment == null) return

        val craftBukkitEnchant = CraftBukkitReflector.getCraftBukkitClass("enchantments.CraftEnchantment")
            .getConstructor(Enchantment::class.java).newInstance(registeredItemGlintType)

        bukkitAcceptingNew.apply {
            this.isAccessible = true
            this.set(null, true)
        }

        bukkitRegisterEnchanment.invoke(null, craftBukkitEnchant)
        bukkitAcceptingNew.set(null, false)
    }

    /**
     * Initialize unregister holder container
     *
     * @param enchantmentRegistry The enchantment registery
     */
    fun initOrGetUnregisterHolderContainer(enchantmentRegistry: Registry<Enchantment>, isInit: Boolean = true): IdentityHashMap<Any, Any>? {
        /* Filter and get the unregisterHolderContainer */
        return enchantmentRegistry.javaClass.declaredFields
            .filter { declaredField -> Modifier.isPrivate(declaredField.modifiers) && !Modifier.isFinal(declaredField.modifiers) }
            .filter { declaredField -> declaredField.isAnnotationPresent(Nullable::class.java) }
            .filter { declaredField -> declaredField.type == Map::class.java }
            .filter { declaredField ->
                val actualTypeArgs = (declaredField.genericType as ParameterizedType).actualTypeArguments

                actualTypeArgs.first().typeName.equals(NMS_REGISTRIES_GENERIC_TYPE_T) && actualTypeArgs.last().typeName.contains(
                    NMS_REGISTERY_HOLDER_CLASS_PATH
                )
            }.run {
                if (this.isEmpty()) return@run null

                this.first().run {
                    this.isAccessible = true

                    if (isInit) this.set(enchantmentRegistry, IdentityHashMap<Any, Any>())

                    return@run DataUtil.asType(this.get(enchantmentRegistry))
                }
            }
    }

    fun buildMcEnchantment(rarity: Enchantment.Rarity, enchanmentCategory: EnchantmentCategory?, equipmentSlot: Array<EquipmentSlot>?): Enchantment {
        return object: net.minecraft.world.item.enchantment.Enchantment(rarity, enchanmentCategory, equipmentSlot) {}
    }

    /* Legacy unused code (May useful in the future, may remove once reach v1.11 full release) */
    /* Temporary unused */
//    /**
//     * Get the item glinter by NeonKey.
//     *
//     * @return The item glinter as enchantment
//     */
//    fun getItemGlinter(neonKeyGeneral: NeonKeyGeneral): Enchantment? {
//        val itemGlinter: Enchantment? = registriesBaseClass.getMethod(NMSRemapped.Mapping.NMS_GET_ENCHANTMENT_BY_NAMESPACEDKEY.remapped, ResourceLocation::class.java)
//            .invoke(enchantmentRegistry, ResourceLocation::class.java.getConstructor(String::class.java)
//                .newInstance(NeonKey.getNeonKeyNameWithNamespace(neonKeyGeneral.key)))?.let {
//                it as Enchantment
//            }
//
//        return itemGlinter
//    }

//    /**
//     * Apply item glinter to the target item.
//     *
//     * @param itemStack The target item.
//     * @param itemGlinterType The item glinter type needed for apply.
//     * @return Updated item meta
//     */
//    /* [Temporary unused] */
//    fun applyItemGlinter(itemStack: ItemStack, itemGlinterType: ItemGlinterType): ItemMeta {
//        itemStack.itemMeta.run {
//            if (hasItemGlinter(this!!, itemGlinterType)) return this
//        }
//
//        /* ItemStack convertion (Bukkit => NMS) */
//        val nmsItemStack = CraftBukkitConverter.bukkitItemStackToNMSItemStack(itemStack)
//        val craftItemStack = CraftBukkitReflector.getCraftItemStackClass()
//
//        /* Apply enchantment (Item Glinter) */
//        nmsItemStack.javaClass.getMethod(
//            NmsMap.ApplyEnchantment.remapped,
//            Enchantment::class.java,
//            Int::class.java
//        ).invoke(
//            nmsItemStack,
//            itemGlinterType.glint,
//            0)
//
//        /* Get the item meta from the NMS ItemStack */
//        val itemMeta = craftItemStack.getMethod("getItemMeta", net.minecraft.world.item.ItemStack::class.java)
//            .invoke(null, nmsItemStack) as ItemMeta
//
//        return itemMeta
//    }
//
//    /**
//     * Used to remove item glinter from the target item.
//     *
//     * @param itemMeta The item meta of the target item.
//     * @param itemGlinterType The item glinter type
//     * @return Updated item meta.
//     */
//    /* Temporary unused */
//    fun removeItemGlinter(itemMeta: ItemMeta, itemGlinterType: ItemGlinterType): ItemMeta {
//        /* Check if the item has the target item glinter */
//        if (!hasItemGlinter(itemMeta, itemGlinterType)) return itemMeta
//
//        /* Get the target item glinter as Bukkit enchantment */
//        val targetItemGlinter = itemMeta.enchants.keys.find {
//            it.javaClass.getMethod("getHandle").invoke(it) == itemGlinterType.glint
//        }!!
//
//        itemMeta.removeEnchant(targetItemGlinter)
//
//        return itemMeta
//    }
}