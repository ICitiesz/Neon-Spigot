package com.islandstudio.neon.stable.utils

import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

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

    fun hasNeonKey(neonKey: NamespaceKeys, keyDataType: PersistentDataType<*, *>, itemMeta: ItemMeta): Boolean {
        return itemMeta.persistentDataContainer.has(neonKey.key, keyDataType)
    }

    fun <Z: Any> addNeonKey(value: Z, namespaceKey: NamespaceKeys, keyDataType: PersistentDataType<*, Z>, itemMeta: ItemMeta, doUpdate: Boolean = false) {
        if ((hasNeonKey(namespaceKey, keyDataType, itemMeta) && !doUpdate)) return

        itemMeta.persistentDataContainer[namespaceKey.key, keyDataType] = value
    }

    fun <Z: Any> updateNeonKey(value: Z, namespaceKey: NamespaceKeys, keyDataType: PersistentDataType<*, Z>, itemMeta: ItemMeta) {
        addNeonKey(value, namespaceKey, keyDataType, itemMeta, true)
    }

    fun removeNeonKey(neonKey: NamespaceKeys, keyDataType: PersistentDataType<*, *>, itemStack: ItemStack) {
        val itemMeta = itemStack.itemMeta ?: return

        if (!hasNeonKey(neonKey, keyDataType, itemMeta)) return

        itemMeta.persistentDataContainer.remove(neonKey.key)

        itemStack.itemMeta = itemMeta
    }

    /**
     * Get value from the given Neon key.
     *
     * @param neonKey The Neon key
     * @param persistentDataType The Neon key's dataType
     * @param itemMeta
     * @return
     */
    fun getNeonKeyValue(neonKey: NamespaceKeys, persistentDataType: PersistentDataType<*, *>, itemMeta: ItemMeta): Any? {
        if (!hasNeonKey(neonKey, persistentDataType, itemMeta)) return null

        return itemMeta.persistentDataContainer.get(neonKey.key, persistentDataType)
    }

    fun getNeonKeyNameWithNamespace(neonKey: NamespaceKeys): String {
        return "${neonKey.key.namespace}:${neonKey.key.key}"
    }
}