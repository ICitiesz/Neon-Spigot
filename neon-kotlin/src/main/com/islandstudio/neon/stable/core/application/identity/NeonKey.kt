package com.islandstudio.neon.stable.core.application.identity

import com.islandstudio.neon.Neon
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.io.resource.NeonInternalResource
import com.islandstudio.neon.shared.core.io.resource.ResourceManager
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.inject
import java.util.*

object NeonKey: IComponentInjector {
    enum class NeonKeyType {
        GENERAL,
        RECIPE
    }

    val neonKeyGeneralProperties: Properties = Properties()
    val neonKeyRecipeProperties: Properties = Properties()

    object Handler {
        fun run() {
            neonKeyGeneralProperties.load(ResourceManager.getNeonResourceAsStream(NeonInternalResource.NeonKeyGeneralProperties))
            neonKeyRecipeProperties.load(ResourceManager.getNeonResourceAsStream(NeonInternalResource.NeonKeyRecipeProperties))
        }
    }

    fun hasNeonKey(neonKey: NamespacedKey, keyDataType: PersistentDataType<*, *>, target: Any): Boolean {
        return when (target) {
            is ItemMeta -> {
                target.persistentDataContainer.has(neonKey, keyDataType)
            }

            is Entity -> {
                target.persistentDataContainer.has(neonKey, keyDataType)
            }

            else -> {
                false
            }
        }
    }

    /**
     * Add neon key to specific target (item meta/entity)
     *
     * @param Z The datatype based on value
     * @param value The value
     * @param neonKey The neonkey to identify the value.
     * @param keyDataType  The datatype for the value.
     * @param target The target to add: item meta /entity.
     * @param doUpdate Is it the operation is update the key value.
     */
    fun <Z: Any> addNeonKey(value: Z, neonKey: NamespacedKey, keyDataType: PersistentDataType<*, Z>, target: Any, doUpdate: Boolean = false) {
        when (target) {
            is ItemMeta -> {
                if ((hasNeonKey(neonKey, keyDataType, target) && !doUpdate)) return

                target.persistentDataContainer[neonKey, keyDataType] = value
                return
            }

            is Entity -> {
                if ((hasNeonKey(neonKey, keyDataType, target) && !doUpdate)) return

                target.persistentDataContainer[neonKey, keyDataType] = value
                return
            }

            else -> {
                return
            }
        }
    }

    fun <Z: Any> updateNeonKey(value: Z, neonKey: NamespacedKey, keyDataType: PersistentDataType<*, Z>, target: Any) {
        addNeonKey(value, neonKey, keyDataType, target, true)
    }

    /**
     * Remove neon key from the specific target (ItemStack/Entity)
     *
     * @param neonKey The neonkey used to identify value
     * @param keyDataType The value datatype
     * @param target The target (ItemStack/Entity)
     * @return True if the removal success, else false
     */
    fun removeNeonKey(neonKey: NamespacedKey, keyDataType: PersistentDataType<*, *>, target: Any): Boolean {
        when (target) {
            is ItemStack -> {
                val itemMeta = target.itemMeta ?: return false

                if (!hasNeonKey(neonKey, keyDataType, itemMeta)) return false

                itemMeta.persistentDataContainer.remove(neonKey)
                target.itemMeta = itemMeta
                return true
            }

            is Entity -> {
                if (!hasNeonKey(neonKey, keyDataType, target)) return false

                target.persistentDataContainer.remove(neonKey)

                return true
            }

            else -> {
                return false
            }
        }
    }

    /**
     * Remove Neon Key by namespace and containsKeyword if available.
     *
     * @param itemStack The target item.
     * @param containsKeyword The containsKeyword if available.
     * @return
     */
    fun removeNeonKeyByNamespace(itemStack: ItemStack, containsKeyword: String? = null): Boolean {
        val itemMeta = itemStack.itemMeta ?: return false
        var dataContainerKeys = itemMeta.persistentDataContainer.keys

        dataContainerKeys = dataContainerKeys.filter { it.namespace.equals("Neon", true) }.toMutableSet()

        if (containsKeyword != null) {
            dataContainerKeys = dataContainerKeys.filter { it.key.contains(containsKeyword, true) }.toMutableSet()
        }

        if (dataContainerKeys.isEmpty()) return false

        itemMeta.persistentDataContainer.keys.forEach {
            if (!dataContainerKeys.contains(it)) return@forEach

            itemMeta.persistentDataContainer.remove(it)
        }

        itemStack.itemMeta = itemMeta
        return true
    }

    /**
     * Get value from the given Neon key.
     *
     * @param neonKey The Neon key
     * @param persistentDataType The Neon key's dataType
     * @param target The item meta from the target item.
     * @return
     */
    fun getNeonKeyValue(neonKey: NamespacedKey, persistentDataType: PersistentDataType<*, *>, target: Any): Any? {
        when (target) {
            is ItemMeta -> {
                if (!hasNeonKey(neonKey, persistentDataType, target)) return null

                return target.persistentDataContainer.get(neonKey, persistentDataType)
            }

            is Entity -> {
                if (!hasNeonKey(neonKey, persistentDataType, target)) return null

                return target.persistentDataContainer.get(neonKey, persistentDataType)
            }

            else -> {
                return null
            }
        }
    }

    fun getNeonKeyNameWithNamespace(neonKey: NamespacedKey): String {
        return "${neonKey.namespace}:${neonKey.key}"
    }

    /**
     * Get neon key from the properties file based on Neon key type.
     *
     * @param keyName The key name
     * @param neonKeyType The Neon key type
     * @return A namespace key
     */
    fun fromProperty(keyName: String, neonKeyType: NeonKeyType): NamespacedKey {
        val neon by inject<Neon>()

        return when (neonKeyType) {
            NeonKeyType.GENERAL -> {
                NamespacedKey(neon, neonKeyGeneralProperties.getProperty(keyName))
            }

            NeonKeyType.RECIPE -> {
                NamespacedKey(neon, neonKeyRecipeProperties.getProperty(keyName))
            }
        }
    }
}