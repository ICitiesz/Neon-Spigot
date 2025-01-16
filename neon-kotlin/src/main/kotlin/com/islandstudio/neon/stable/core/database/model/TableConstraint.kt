package com.islandstudio.neon.stable.core.database.model

import java.io.Serializable

data class TableConstraint(
    var tableCatalog: String?,
    var tableSchema: String?,
    var tableName: String?,
    var constraintName: String?,
    var constraintType: String?
): Serializable
