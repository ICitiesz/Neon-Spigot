package com.islandstudio.neon.stable.item

import com.islandstudio.neon.experimental.utils.CraftBukkitConverter
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.core.application.SupportedVersions
import com.islandstudio.neon.stable.core.application.identity.NeonKey
import com.islandstudio.neon.stable.core.application.identity.NeonKeyGeneral
import com.islandstudio.neon.stable.core.application.reflection.CraftBukkitReflector
import com.islandstudio.neon.stable.core.application.reflection.NmsProcessor
import com.islandstudio.neon.stable.core.application.reflection.mapping.NmsMap
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderOwner
import net.minecraft.core.Registry
import net.minecraft.world.item.enchantment.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.koin.core.component.inject
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.util.*
import javax.annotation.Nullable

object NItemGlinter {
    /* NMS Registery Properties */
    private const val NMS_REGISTERY_HOLDER_CLASS_PATH = "core.Holder"
    private const val NMS_REGISTRIES_CLASS_PATH = "core.registries.BuiltInRegistries"
    private const val NMS_REGISTRIES_BASE_CLASS_PATH = "core.IRegistry"
    private const val NMS_REGISTERY_HOLDER_OWNER_PATH = "core.HolderOwner"
    private const val NMS_REGISTRIES_GENERIC_TYPE_T = "T"

    private val nmsProcessor = NmsProcessor()
    private val registriesBaseClass = nmsProcessor.getMcClass(NMS_REGISTRIES_BASE_CLASS_PATH)!!

    /* Used to get enchantment registry  */
    private val enchantmentRegistry = run {
        /* Get registries class based on the given class path */
        val registriesClass = when {
            nmsProcessor.hasMcClass(NMS_REGISTRIES_CLASS_PATH) -> {
                nmsProcessor.getMcClass(NMS_REGISTRIES_CLASS_PATH)
            }

            else -> {
                registriesBaseClass
            }
        }!!

        /* Get the enchantment registry */
        registriesClass.getField(NmsMap.EnchantmentRegistry.remapped).get(null).also {
            initOrGetUnregisterHolderContainer(it as Registry<Enchantment>)
        }
    }

    enum class ItemGlinterType(val register: Unit, val glint: org.bukkit.enchantments.Enchantment) {
        NGUI_BUTTON_GLINT(
            Handler.registerItemGlinter(
                NeonKeyGeneral.NGUI_HIGHTLIGHT_BUTTON,
                object : Enchantment(Rarity.RARE, null, null) {}),
            NeonKeyGeneral.NGUI_HIGHTLIGHT_BUTTON.run {
                return@run getItemGlinter(this)
                    ?: throw NullPointerException("Item glinter type '${NeonKey.getNeonKeyNameWithNamespace(this.key)}' not registered yet!")
            }
        );


        /* Temporary Unused */
//        NGUI_BUTTON_GLINT(
//            Handler.registerItemGlinter(NeonKeyGeneral.NGUI_HIGHTLIGHT_BUTTON, object : Enchantment(Rarity.RARE, null, null) {} ),
//            NeonKeyGeneral.NGUI_HIGHTLIGHT_BUTTON.run {
//                return@run getItemGlinter(this)
//                    ?: throw NullPointerException("Item glinter type '${NeonKey.getNeonKeyNameWithNamespace(this.key)}' not registered yet!")
//            }
//        );
    }

    object Handler: IComponentInjector {
        private val appContext by inject<AppContext>()

        fun run() {
            /* Toggle add registry entry */
            run enableAddRegistryEntry@ {
                if (appContext.serverMajorVersion == SupportedVersions.V1_17.majorVersion) {
                    return@enableAddRegistryEntry
                }

                if (appContext.serverMajorVersion == SupportedVersions.V1_18.majorVersion) {
                    if (appContext.serverVersion != "1.18.2") {
                        return@enableAddRegistryEntry
                    }
                }

                enchantmentRegistry.javaClass.getDeclaredField(NmsMap.RegistryEntryToggleState.remapped).also {
                    it.isAccessible = true
                    it.set(enchantmentRegistry, false)
                }
            }

            ItemGlinterType.entries.forEach { it.register }

            /* Revoke add registry entry */
            run disableAddRegistryEntry@ {
                if (appContext.serverMajorVersion == SupportedVersions.V1_17.majorVersion) {
                    return@disableAddRegistryEntry
                }

                if (appContext.serverMajorVersion == SupportedVersions.V1_18.majorVersion) {
                    if (appContext.serverVersion != "1.18.2") {
                        return@disableAddRegistryEntry
                    }
                }

                initOrGetUnregisterHolderContainer(enchantmentRegistry as Registry<Enchantment>)
                enchantmentRegistry.javaClass.getMethod(NmsMap.RevokeRegistryEntry.remapped).invoke(
                    enchantmentRegistry
                )
            }
        }

        /**
         * Register custom item glinter as effectless enchantment or effective enchantment
         *
         * @param T The item glinter property that subclass of Enchantment.
         * @param neonKeyGeneral The Neon Key used as identifier
         * @param enchantment The enchantment properties
         */
        fun <T: Enchantment> registerItemGlinter(neonKeyGeneral: NeonKeyGeneral, enchantment: T) {
            /* Stage 1: Check the existance of target item glinter/enchantment */
            if (hasItemGlinterRegistered(neonKeyGeneral)) return

            /* Stage 2:: Get unregister holder container  */
            initOrGetUnregisterHolderContainer(enchantmentRegistry as Registry<Enchantment>, false)?.let {
                if (it.isNotEmpty()) return@let

                /* Add enchantment to the unregister holder container if it doesn't exist in the container */
                Holder.Reference::class.java.methods
                    .filter { method -> Modifier.isPublic(method.modifiers) }
                    .filter { method -> method.returnType == Holder.Reference::class.java }
                    .filter { method -> method.parameterCount == 2 }
                    .filter { method ->
                        if (nmsProcessor.hasMcClass(NMS_REGISTERY_HOLDER_OWNER_PATH)) {
                            method.parameterTypes.contains(HolderOwner::class.java) && method.parameterTypes.contains(
                                Any::class.java
                            )
                        } else {
                            method.parameterTypes.contains(Registry::class.java) && method.parameterTypes.contains(Any::class.java)
                        }

                    }.apply {
                        if (this.isEmpty()) return@apply

                        val unregistryHolderReference = this.first().run {
                            if (nmsProcessor.hasMcClass(NMS_REGISTERY_HOLDER_OWNER_PATH)) {
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

            /* Stage 3: Register item glinter to server within NMS */
            val registeredItemGlinterType = Registry::class.java.getMethod(
                NmsMap.RegisterEnchantment.remapped, Registry::class.java, String::class.java, Any::class.java)
                .invoke(null, enchantmentRegistry, NeonKey.getNeonKeyNameWithNamespace(neonKeyGeneral.key), enchantment) as Enchantment

            /* Stage 3.1: Register item glinter to server within NMS if it is legacy */
            /* Legacy code for registering enchantment within Bukkit */
            val bukkitAcceptingNew = org.bukkit.enchantments.Enchantment::class.java.declaredFields.find { acceptingNewField -> acceptingNewField.name == "acceptingNew" }
            val bukkitRegisterEnchanment = org.bukkit.enchantments.Enchantment::class.java.methods.find { method -> method.name == "registerEnchantment" }

            if (bukkitAcceptingNew == null || bukkitRegisterEnchanment == null) return

            val craftBukkitEnchant = CraftBukkitReflector.getCraftBukkitClass("enchantments.CraftEnchantment")
                .getConstructor(Enchantment::class.java).newInstance(registeredItemGlinterType)

            bukkitAcceptingNew.apply {
                this.isAccessible = true
                this.set(null, true)
            }

            bukkitRegisterEnchanment.invoke(null, craftBukkitEnchant)

            bukkitAcceptingNew.set(null, false)
        }
    }

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

    /**
     * Get the item glinter by NeonKey.
     *
     * @return The item glinter as enchantment
     */
    private fun getItemGlinter(neonKeyGeneral: NeonKeyGeneral): org.bukkit.enchantments.Enchantment? {
        return org.bukkit.Registry.ENCHANTMENT.get(neonKeyGeneral.key)
    }

    /**
     * Check wheather the item glinter has registered
     *
     * @param neonKeyGeneral The Neon Key as identifier.
     * @return True if registered, else false
     */
    fun hasItemGlinterRegistered(neonKeyGeneral: NeonKeyGeneral): Boolean {
        return getItemGlinter(neonKeyGeneral)?.let { true } ?: false
    }

    /**
     * Apply item glinter to the target item.
     *
     * @param itemStack The target item.
     * @param itemGlinterType The item glinter type needed for apply.
     * @return Updated item meta
     */
    /* [Temporary unused] */
    fun applyItemGlinter(itemStack: ItemStack, itemGlinterType: ItemGlinterType): ItemMeta {
        itemStack.itemMeta.run {
            if (hasItemGlinter(this!!, itemGlinterType)) return this
        }

        /* ItemStack convertion (Bukkit => NMS) */
        val nmsItemStack = CraftBukkitConverter.bukkitItemStackToNMSItemStack(itemStack)
        val craftItemStack = CraftBukkitReflector.getCraftItemStackClass()

        /* Apply enchantment (Item Glinter) */
        nmsItemStack.javaClass.getMethod(
            NmsMap.ApplyEnchantment.remapped,
            Enchantment::class.java,
            Int::class.java
        ).invoke(
            nmsItemStack,
            itemGlinterType.glint,
            0)

        /* Get the item meta from the NMS ItemStack */
        val itemMeta = craftItemStack.getMethod("getItemMeta", net.minecraft.world.item.ItemStack::class.java)
            .invoke(null, nmsItemStack) as ItemMeta

        return itemMeta
    }

    /**
     * Used to remove item glinter from the target item.
     *
     * @param itemMeta The item meta of the target item.
     * @param itemGlinterType The item glinter type
     * @return Updated item meta.
     */
    /* Temporary unused */
    fun removeItemGlinter(itemMeta: ItemMeta, itemGlinterType: ItemGlinterType): ItemMeta {
        /* Check if the item has the target item glinter */
        if (!hasItemGlinter(itemMeta, itemGlinterType)) return itemMeta

        /* Get the target item glinter as Bukkit enchantment */
        val targetItemGlinter = itemMeta.enchants.keys.find {
            it.javaClass.getMethod("getHandle").invoke(it) == itemGlinterType.glint
        }!!

        itemMeta.removeEnchant(targetItemGlinter)

        return itemMeta
    }

    /**
     * Check if the target item meta has item glinter
     *
     * @param itemMeta The target item meta.
     * @param itemGlinterType The item glinter type
     * @return True if existed, else false.
     */
    private fun hasItemGlinter(itemMeta: ItemMeta, itemGlinterType: ItemGlinterType): Boolean {
        itemMeta.enchants.keys.find {
            it.javaClass.getMethod("getHandle").invoke(it) == itemGlinterType.glint
        } ?: return false

        return true
    }

    /**
     * Initialize unregister holder container
     *
     * @param enchantmentRegistry The enchantment registery
     */
    fun initOrGetUnregisterHolderContainer(enchantmentRegistry: Registry<Enchantment>, isInit: Boolean = true): IdentityHashMap<Any, Any>? {
        /* Filter and get the unregisterHolderContainer */
        enchantmentRegistry.javaClass.declaredFields
            .filter { declaredField -> Modifier.isPrivate(declaredField.modifiers) && !Modifier.isFinal(declaredField.modifiers) }
            .filter { declaredField -> declaredField.isAnnotationPresent(Nullable::class.java) }
            .filter { declaredField -> declaredField.type == Map::class.java }
            .filter { declaredField ->
                val actualTypeArgs = (declaredField.genericType as ParameterizedType).actualTypeArguments

                actualTypeArgs.first().typeName.equals(NMS_REGISTRIES_GENERIC_TYPE_T) && actualTypeArgs.last().typeName.contains(
                    NMS_REGISTERY_HOLDER_CLASS_PATH
                )
            }.apply {
                if (this.isEmpty()) return@apply

                this.first().apply {
                    this.isAccessible = true

                    if (isInit) this.set(enchantmentRegistry, IdentityHashMap<Any, Any>())

                    return this.get(enchantmentRegistry) as IdentityHashMap<Any, Any>
                }
            }

        return null
    }
}