package com.islandstudio.neon.shared.core.config.wrapper.obj

import com.akuleshov7.ktoml.annotations.TomlComments
import com.islandstudio.neon.shared.core.config.component.type.IConfigObject
import com.islandstudio.neon.shared.core.config.wrapper.property.NeonDBConfigProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NeonDBConfigObject(
    @Serializable
    @SerialName("NeonDatabase")
    @TomlComments(lines = [
        "Database folder:",
        "- NeonDB-Online <= This is universal",
        "- NeonDB-Online-1.20 <= This is MC version-based",
        "",
        "Database folder name format:",
        "- Universal",
        "   -> NeonDB-{onlineMode [Online/Offline]}",
        "",
        "- Mc version-based",
        "   -> NeonDB-{onlineMode [Online/Offline]}-{mcVersion}"
    ])
    var neonDBConfig: NeonDBConfig = NeonDBConfig()
): IConfigObject {
    @Serializable
    data class NeonDBConfig(
        @TomlComments(
            lines = [
                "Define whether the database is share across MC version.",
                "This means no matter what MC version within supported range, the same database will be used.",
                "Default: true"
            ]
        )
        var isUniversal: Boolean = NeonDBConfigProperty.IsUniversal.defaultValue,

        var neonDBBackup: NeonDBBackup = NeonDBBackup()
    ): IConfigObject {
        @Serializable
        data class NeonDBBackup(
            @TomlComments("Backup not available yet")
            var isEnabled: Boolean = NeonDBConfigProperty.BackUpIsEnabled.defaultValue,

            @TomlComments("Backup once after server initialized (Startup/Reload)")
            var runOnceAfterServerInitialized: Boolean = NeonDBConfigProperty.RunOnceAfterServerInitialized.defaultValue,

            @TomlComments(lines = [
                "Backup interval in minutes",
                "Default: 30 minutes [Min = 15 minutes | Max = 60 minutes]"
            ])
            var backupInterval: Long = NeonDBConfigProperty.BackupInterval.defaultValue,

            @TomlComments(lines = [
                "Maximum backup entry will be created. Overwrite the older if reaches maximum.",
                "Default = 5 [Min = 1 | Max = 10]"
            ])
            var maxBackups: Long = NeonDBConfigProperty.MaxBackups.defaultValue,

            @TomlComments(lines = [
                "[Experimental] Transfer and merge data over another database after database switching.",
                "This option only applicable for Mc version-based database"
            ])
            var transferDataUponDatabaseChange: Boolean = NeonDBConfigProperty.TransferDataUponDatabaseChange.defaultValue
        ): IConfigObject
    }
}
