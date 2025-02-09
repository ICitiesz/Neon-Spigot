package com.islandstudio.neon.stable.core.application.datakey

import org.bukkit.persistence.PersistentDataType

sealed class DataContainerType<T>(keyName: String): AbstractDataKey(keyName), DataContainerType.IDataContainerWrapper<T> {
    private interface IDataContainerWrapper<T> {
        val neonGeneralDataKey: NeonGeneralDataKey
        val persistentDataType: PersistentDataType<*, T>
    }

    data object PlayerSessionContainer: DataContainerType<ByteArray>("neon.player.player_session") {
        override val neonGeneralDataKey: NeonGeneralDataKey = NeonGeneralDataKey.NeonPlayerSession()
        override val persistentDataType: PersistentDataType<*, ByteArray> = PersistentDataType.BYTE_ARRAY
    }
}
