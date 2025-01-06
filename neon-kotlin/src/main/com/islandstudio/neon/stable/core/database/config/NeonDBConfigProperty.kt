package com.islandstudio.neon.stable.core.database.config

import com.islandstudio.neon.stable.core.config.component.ConfigDataRange
import com.islandstudio.neon.stable.core.config.component.ConfigProperty
import com.islandstudio.neon.stable.utils.processing.properties.DataTypes


sealed class NeonDBConfigProperty(
    parentConfigKey: String,
    keyName: String,
    description: String,
    dataType: DataTypes,
    defaultValue: Any,
    dataRange: ConfigDataRange<*>
): ConfigProperty(
    parentConfigKey,
    keyName,
    description,
    dataType,
    defaultValue,
    dataRange
) {
    data object IsUniversal: NeonDBConfigProperty(
        parentConfigKey = "NeonDatabase",
        keyName = "isUniversal",
        description = "Define whether the database is share across MC version.",
        dataType = DataTypes.BOOLEAN,
        defaultValue = true,
        dataRange = ConfigDataRange.DataRangeBoolean
    )

    data object BackUpIsEnabled: NeonDBConfigProperty(
        parentConfigKey = "NeonDatabase.backup",
        keyName = "isEnabled",
        description = "Backup not available yet",
        dataType = DataTypes.BOOLEAN,
        defaultValue = true,
        dataRange = ConfigDataRange.DataRangeBoolean
    )

    data object RunOnceAfterServerInitialized: NeonDBConfigProperty(
        parentConfigKey = "NeonDatabase.backup",
        keyName = "runOnceAfterServerInitialized",
        description = "Backup once after server initialized (Startup/Reload)",
        dataType = DataTypes.BOOLEAN,
        defaultValue = true,
        dataRange = ConfigDataRange.DataRangeBoolean
    )

    data object BackupInterval: NeonDBConfigProperty(
        parentConfigKey = "NeonDatabase.backup",
        keyName = "backupInterval",
        description = "Backup interval in minutes",
        dataType = DataTypes.LONG,
        defaultValue = 15,
        dataRange = ConfigDataRange<Long>(15, 60)
    )

    data object MaxBackups: NeonDBConfigProperty(
        parentConfigKey = "NeonDatabase.backup",
        keyName = "maxBackups",
        description = "Maximum backup entry will be created. Overwrite the older if reaches maximum.",
        dataType = DataTypes.LONG,
        defaultValue = 5,
        dataRange = ConfigDataRange<Long>(1, 10)
    )

    data object TransferDataUponDatabaseChange: NeonDBConfigProperty(
        parentConfigKey = "NeonDatabase.backup",
        keyName = "transferDataUponDatabaseChange",
        description = "[Experimental] Transfer and merge data over another database after database switching.",
        dataType = DataTypes.BOOLEAN,
        defaultValue = false,
        dataRange = ConfigDataRange.DataRangeBoolean
    )
}