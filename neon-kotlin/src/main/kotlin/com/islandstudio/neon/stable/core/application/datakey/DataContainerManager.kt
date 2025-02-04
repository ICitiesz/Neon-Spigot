package com.islandstudio.neon.stable.core.application.datakey

import org.bukkit.persistence.PersistentDataHolder

object DataContainerManager {

    /**
     * Attach data to object that contains persistent data container.
     *
     * @param T The object
     * @param U The data type
     * @param data The data
     * @param dataContainerType The data container type.
     * @param target Same as T
     */
    fun <T: PersistentDataHolder, U: Any> attachData(target: T, data: U, dataContainerType: DataContainerType<U>) {
        if (hasDataContainerAttached(target, dataContainerType)) return

        target.persistentDataContainer[dataContainerType.dataKey, dataContainerType.persistentDataType] = data
    }

    /**
     * Detach data
     *
     * @param T The target
     * @param U The data type
     * @param dataContainerType The data container type.
     * @param target same as T
     */
    fun <T: PersistentDataHolder, U> detachData(target: T, dataContainerType: DataContainerType<U>) {
        if (!hasDataContainerAttached(target, dataContainerType)) return

        target.persistentDataContainer.remove(dataContainerType.dataKey)
    }

    fun <T: PersistentDataHolder, U: Any> updateAttachedData(target: T, data: U, dataContainerType: DataContainerType<U>) {
        if (!hasDataContainerAttached(target, dataContainerType)) return

        target.persistentDataContainer[dataContainerType.dataKey, dataContainerType.persistentDataType] = data
    }

    fun <T: PersistentDataHolder, U> getAttachedData(target: T, dataContainerType: DataContainerType<U>): U? {
        if (!hasDataContainerAttached(target, dataContainerType)) return null

        return target.persistentDataContainer.get(dataContainerType.dataKey, dataContainerType.persistentDataType)
    }

    fun <T: PersistentDataHolder, U> hasDataContainerAttached(target: T, dataContainerType: DataContainerType<U>): Boolean {
        return target.persistentDataContainer.has(dataContainerType.dataKey)
    }
}