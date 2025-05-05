package com.islandstudio.neon.stable.core.application.datakey

import org.bukkit.persistence.PersistentDataType

sealed class DataContainerType<T>(keyName: String): AbstractDataKey(keyName) {
    abstract val persistentDataType: PersistentDataType<*, T>

    data object PlayerSessionContainer: DataContainerType<ByteArray>("neon.player.player_session") {
        override val persistentDataType: PersistentDataType<*, ByteArray> = PersistentDataType.BYTE_ARRAY
    }

    data object NeonGuiButtonRefIdContainer: DataContainerType<String>("neon.gui.button.reference_id") {
        override val persistentDataType: PersistentDataType<*, String> = PersistentDataType.STRING
    }

    data object NeonGuiButtonCustomDataContainer: DataContainerType<String>("neon.gui.button.custom_data_container") {
        override val persistentDataType: PersistentDataType<*, String> = PersistentDataType.STRING
    }

    data object NeonGuiButtonConfirmationStatusContainer: DataContainerType<Boolean>("neon.gui.button.confirmation_status") {
        override val persistentDataType: PersistentDataType<*, Boolean> = PersistentDataType.BOOLEAN
    }
}
