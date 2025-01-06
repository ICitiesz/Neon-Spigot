package com.islandstudio.neon.stable.core.database.config

import com.akuleshov7.ktoml.annotations.TomlComments
import com.islandstudio.neon.experimental.config.component.type.AbstractConfigWrapper
import com.islandstudio.neon.experimental.config.component.type.IConfigObject
import com.islandstudio.neon.stable.core.database.config.NeonDBConfigWrapper.NeonDBConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class NeonDBConfigWrapper : AbstractConfigWrapper<NeonDBConfig, NeonDBConfigProperty>(
    NeonDBConfig(),
    NeonDBConfigProperty::class
) {
    @Serializable
    data class NeonDBConfig(
        @TomlComments(lines = [
            "Define whether the database is share across MC version.",
            "This means no matter what MC version within supported range, the same database will be used.",
            "Default: true"
        ])
        var isUniversal: Boolean = NeonDBConfigProperty.IsUniversal.defaultValue as Boolean,

        @SerialName("backup")
        var neonDBBackup: NeonDBBackup = NeonDBBackup()
    ): IConfigObject {
        companion object {
            @Serializable
            @SerialName("NeonDatabase")
            private val neonDBConfig = NeonDBConfig()
        }
    }

    @Serializable
    data class NeonDBBackup(
        @TomlComments("Backup not available yet")
        var isEnabled: Boolean = NeonDBConfigProperty.BackUpIsEnabled.defaultValue as Boolean,

        @TomlComments("Backup once after server initialized (Startup/Reload)")
        var runOnceAfterServerInitialized: Boolean = NeonDBConfigProperty.RunOnceAfterServerInitialized.defaultValue as Boolean,

        @TomlComments(lines = [
            "Backup interval in minutes",
            "Default: 30 minutes [Min = 15 minutes | Max = 60 minutes]"
        ])
        var backupInterval: Int = NeonDBConfigProperty.BackupInterval.defaultValue as Int,

        @TomlComments(lines = [
            "Maximum backup entry will be created. Overwrite the older if reaches maximum.",
            "Default = 5 [Min = 1 | Max = 10]"
        ])
        var maxBackups: Int = NeonDBConfigProperty.MaxBackups.defaultValue as Int,

        @TomlComments(lines = [
            "[Experimental] Transfer and merge data over another database after database switching.",
            "This option only applicable for Mc version-based database"
        ])
        var transferDataUponDatabaseChange: Boolean = NeonDBConfigProperty.TransferDataUponDatabaseChange.defaultValue as Boolean
    ): IConfigObject
}