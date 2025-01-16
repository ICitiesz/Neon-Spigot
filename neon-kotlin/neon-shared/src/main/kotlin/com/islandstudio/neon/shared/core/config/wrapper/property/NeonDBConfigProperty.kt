package com.islandstudio.neon.shared.core.config.wrapper.property

import com.islandstudio.neon.shared.core.config.component.ConfigDataRange
import com.islandstudio.neon.shared.core.config.component.ConfigProperty
import com.islandstudio.neon.shared.utils.data.DataType

sealed class NeonDBConfigProperty<T>(
    parentConfigKey: String,
    keyName: String,
    description: String,
    dataType: DataType,
    defaultValue: T,
    dataRange: ConfigDataRange<*>
): ConfigProperty<T>(
    parentConfigKey,
    keyName,
    description,
    dataType,
    defaultValue,
    dataRange
) {
    data object IsUniversal: NeonDBConfigProperty<Boolean>(
        parentConfigKey = "NeonDatabase",
        keyName = "isUniversal",
        description = "Define whether the database is share across MC version.",
        dataType = DataType.Boolean,
        defaultValue = true,
        dataRange = ConfigDataRange.DataRangeBoolean
    )

    data object BackUpIsEnabled: NeonDBConfigProperty<Boolean>(
        parentConfigKey = "NeonDatabase.backup",
        keyName = "isEnabled",
        description = "Backup not available yet",
        dataType = DataType.Boolean,
        defaultValue = true,
        dataRange = ConfigDataRange.DataRangeBoolean
    )

    data object RunOnceAfterServerInitialized: NeonDBConfigProperty<Boolean>(
        parentConfigKey = "NeonDatabase.backup",
        keyName = "runOnceAfterServerInitialized",
        description = "Backup once after server initialized (Startup/Reload)",
        dataType = DataType.Boolean,
        defaultValue = true,
        dataRange = ConfigDataRange.DataRangeBoolean
    )

    data object BackupInterval: NeonDBConfigProperty<Long>(
        parentConfigKey = "NeonDatabase.backup",
        keyName = "backupInterval",
        description = "Backup interval in minutes",
        dataType = DataType.Long,
        defaultValue = 15,
        dataRange = ConfigDataRange<Long>(15, 60)
    )

    data object MaxBackups: NeonDBConfigProperty<Long>(
        parentConfigKey = "NeonDatabase.backup",
        keyName = "maxBackups",
        description = "Maximum backup entry will be created. Overwrite the older if reaches maximum.",
        dataType = DataType.Long,
        defaultValue = 5,
        dataRange = ConfigDataRange<Long>(1, 10)
    )

    data object TransferDataUponDatabaseChange: NeonDBConfigProperty<Boolean>(
        parentConfigKey = "NeonDatabase.backup",
        keyName = "transferDataUponDatabaseChange",
        description = "[Experimental] Transfer and merge data over another database after database switching.",
        dataType = DataType.Boolean,
        defaultValue = false,
        dataRange = ConfigDataRange.DataRangeBoolean
    )
}