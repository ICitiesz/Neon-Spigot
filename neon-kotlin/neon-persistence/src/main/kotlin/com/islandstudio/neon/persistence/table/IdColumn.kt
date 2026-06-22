package com.islandstudio.neon.persistence.table

import org.jetbrains.exposed.v1.core.Column

interface IdColumn {
    val id: Column<Long>
}