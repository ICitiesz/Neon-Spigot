package com.islandstudio.neon.stable.utils

import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.kotlin.konan.properties.Properties

object NeonKey {
    enum class NamespaceKeys(val key: NamespacedKey) {
        NEON_BUTTON(NamespacedKey(NConstructor.plugin, "neon_button")),
        NEON_BUTTON_HIGHLIGHT(NamespacedKey(NConstructor.plugin, "neon_button_highlight")),
        NEON_BUNDLE(NamespacedKey(NConstructor.plugin, "neon_bundle")),
        NEON_FIREWORK(NamespacedKey(NConstructor.plugin, "neon_firework")),
        NEON_ID_FIELD(NamespacedKey(NConstructor.plugin, "neon_id")),

        /* nDurable Properties */
        NEON_N_DURABLE(NamespacedKey(NConstructor.plugin, "neon_n_durable")),
        NEON_N_DURABLE_DAMAGE(NamespacedKey(NConstructor.plugin, "neon_n_durable_damage")),
    }

    private val neonKeyProperties: Properties = Properties()

    object Handler {
        fun run() {
            neonKeyProperties.load(this.javaClass.classLoader.getResourceAsStream("resources/NeonKeys.properties"))
        }
    }

    fun hasNeonKey(neonKey: NamespacedKey, keyDataType: PersistentDataType<*, *>, itemMeta: ItemMeta): Boolean {
        return itemMeta.persistentDataContainer.has(neonKey, keyDataType)
    }

    fun <Z: Any> addNeonKey(value: Z, namespaceKey: NamespacedKey, keyDataType: PersistentDataType<*, Z>, itemMeta: ItemMeta, doUpdate: Boolean = false) {
        if ((hasNeonKey(namespaceKey, keyDataType, itemMeta) && !doUpdate)) return

        itemMeta.persistentDataContainer[namespaceKey, keyDataType] = value
    }

    fun <Z: Any> updateNeonKey(value: Z, namespaceKey: NamespacedKey, keyDataType: PersistentDataType<*, Z>, itemMeta: ItemMeta) {
        addNeonKey(value, namespaceKey, keyDataType, itemMeta, true)
    }

    fun removeNeonKey(neonKey: NamespacedKey, keyDataType: PersistentDataType<*, *>, itemStack: ItemStack): Boolean {
        val itemMeta = itemStack.itemMeta ?: return false

        if (!hasNeonKey(neonKey, keyDataType, itemMeta)) return false

        itemMeta.persistentDataContainer.remove(neonKey)
        itemStack.itemMeta = itemMeta
        return true
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
     * @param itemMeta The item meta from the target item.
     * @return
     */
    fun getNeonKeyValue(neonKey: NamespacedKey, persistentDataType: PersistentDataType<*, *>, itemMeta: ItemMeta): Any? {
        if (!hasNeonKey(neonKey, persistentDataType, itemMeta)) return null

        return itemMeta.persistentDataContainer.get(neonKey, persistentDataType)
    }

    fun getNeonKeyNameWithNamespace(neonKey: NamespacedKey): String {
        return "${neonKey.namespace}:${neonKey.key}"
    }

    /**
     * Get neon key from the NeonKeys.properties file.
     *
     * @param keyName
     * @return
     */
    fun fromProperty(keyName: String): NamespacedKey {
        return NamespacedKey(NConstructor.plugin, neonKeyProperties.getProperty(keyName))
    }
}