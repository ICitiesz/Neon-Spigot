package com.islandstudio.neon.stable.core.database.model

import java.io.Serializable

data class InactiveTable(
    var tableCatalog: String?,
    var tableSchema: String?,
    var tableName: String?
): Serializable
