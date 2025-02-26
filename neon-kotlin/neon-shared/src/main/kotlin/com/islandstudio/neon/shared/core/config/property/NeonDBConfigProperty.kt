package com.islandstudio.neon.shared.core.config.property

import com.islandstudio.neon.shared.core.config.component.AbstractConfigProperty
import com.islandstudio.neon.shared.core.config.component.ConfigDataRange
import com.islandstudio.neon.shared.utils.data.DataType

sealed class NeonDBConfigProperty<T>: AbstractConfigProperty<T>() {
    data object IsUniversal: NeonDBConfigProperty<Boolean>() {
        override val parentConfigKey: String = "NeonDatabase"
        override val keyName: String = "isUniversal"
        override val description: String = "Define whether the database is share across MC version."
        override val dataType: DataType = DataType.Boolean
        override val defaultValue: Boolean = true
        override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
    }

    data object BackUpIsEnabled: NeonDBConfigProperty<Boolean>() {
        override val parentConfigKey: String = "NeonDatabase.backup"
        override val keyName: String = "isEnabled"
        override val description: String = "Backup not available yet"
        override val dataType: DataType = DataType.Boolean
        override val defaultValue: Boolean = true
        override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
    }

    data object RunOnceAfterServerInitialized: NeonDBConfigProperty<Boolean>() {
        override val parentConfigKey: String = "NeonDatabase.backup"
        override val keyName: String = "runOnceAfterServerInitialized"
        override val description: String = "Backup once after server initialized (Startup/Reload)"
        override val dataType: DataType = DataType.Boolean
        override val defaultValue: Boolean = true
        override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
    }

    data object BackupInterval: NeonDBConfigProperty<Long>() {
        override val parentConfigKey: String = "NeonDatabase.backup"
        override val keyName: String = "backupInterval"
        override val description: String = "Backup interval in minutes"
        override val dataType: DataType = DataType.Long
        override val defaultValue: Long = 15
        override val dataRange: ConfigDataRange<Long> = ConfigDataRange<Long>(15, 60)
    }

    data object MaxBackups: NeonDBConfigProperty<Long>() {
        override val parentConfigKey: String = "NeonDatabase.backup"
        override val keyName: String = "maxBackups"
        override val description: String = "Maximum backup entry will be created. Overwrite the older if reaches maximum."
        override val dataType: DataType = DataType.Long
        override val defaultValue: Long = 5
        override val dataRange: ConfigDataRange<Long> = ConfigDataRange<Long>(1, 10)
    }

    data object TransferDataUponDatabaseChange: NeonDBConfigProperty<Boolean>() {
        override val parentConfigKey: String = "NeonDatabase.backup"
        override val keyName: String = "transferDataUponDatabaseChange"
        override val description: String = "[Experimental] Transfer and merge data over another database after database switching."
        override val dataType: DataType = DataType.Boolean
        override val defaultValue: Boolean = false
        override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.DataRangeBoolean
    }
}