package com.islandstudio.neon.stable.core.application.datakey

import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

object DataContainerManager {

    /**
     * Attach data to object that contains persistent data container.
     *
     * @param T The object
     * @param neonGeneralDataKey Neon general data key
     * @param data The data
     * @param persistentDataType The persistent data type.
     * @param target same as T
     */
    fun <T: PersistentDataHolder> attachData(neonGeneralDataKey: NeonGeneralDataKey, data: Any, persistentDataType: PersistentDataType<*, Any>, target: T) {
        if (hasDataAttached(target, neonGeneralDataKey, persistentDataType)) return

        target.persistentDataContainer[neonGeneralDataKey.dataKey, persistentDataType] = data
    }

    /**
     * Detach data
     *
     * @param T The object
     * @param neonGeneralDataKey Neon general data key
     * @param persistentDataType The persistent data type.
     * @param target same as T
     */
    fun <T: PersistentDataHolder> detachData(neonGeneralDataKey: NeonGeneralDataKey, persistentDataType: PersistentDataType<*, *>, target: T) {
        if (!hasDataAttached(target, neonGeneralDataKey, persistentDataType)) return

        target.persistentDataContainer.remove(neonGeneralDataKey.dataKey)
    }

    fun <T: PersistentDataHolder> updateAttachedData(neonGeneralDataKey: NeonGeneralDataKey, data: Any, persistentDataType: PersistentDataType<*, Any>, target: T) {
        if (!hasDataAttached(target, neonGeneralDataKey, persistentDataType)) return

        target.persistentDataContainer[neonGeneralDataKey.dataKey, persistentDataType] = data
    }

    fun <T: PersistentDataHolder> hasDataAttached(target: T, neonGeneralDataKey: NeonGeneralDataKey, persistentDataType: PersistentDataType<*, *>): Boolean {
        return target.persistentDataContainer.has(neonGeneralDataKey.dataKey)
    }
}