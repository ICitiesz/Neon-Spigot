/*
 * This file is generated by jOOQ.
 */
package com.islandstudio.neon.stable.core.database.schema.neon_data


import com.islandstudio.neon.stable.core.database.schema.DefaultCatalog
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.DtAccessPermission
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.DtPlayerProfile
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.DtRole
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.DtRoleAccess
import org.jooq.Catalog
import org.jooq.Table
import org.jooq.impl.SchemaImpl
import kotlin.collections.List


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class NeonData : SchemaImpl("NEON_DATA", DefaultCatalog.DEFAULT_CATALOG) {
    public companion object {

        /**
         * The reference instance of <code>NEON_DATA</code>
         */
        val NEON_DATA: NeonData = NeonData()
    }

    /**
     * The table <code>NEON_DATA.DT_ACCESS_PERMISSION</code>.
     */
    val DT_ACCESS_PERMISSION: DtAccessPermission get() = DtAccessPermission.DT_ACCESS_PERMISSION

    /**
     * The table <code>NEON_DATA.DT_PLAYER_PROFILE</code>.
     */
    val DT_PLAYER_PROFILE: DtPlayerProfile get() = DtPlayerProfile.DT_PLAYER_PROFILE

    /**
     * The table <code>NEON_DATA.DT_ROLE</code>.
     */
    val DT_ROLE: DtRole get() = DtRole.DT_ROLE

    /**
     * The table <code>NEON_DATA.DT_ROLE_ACCESS</code>.
     */
    val DT_ROLE_ACCESS: DtRoleAccess get() = DtRoleAccess.DT_ROLE_ACCESS

    override fun getCatalog(): Catalog = DefaultCatalog.DEFAULT_CATALOG

    override fun getTables(): List<Table<*>> = listOf(
        DtAccessPermission.DT_ACCESS_PERMISSION,
        DtPlayerProfile.DT_PLAYER_PROFILE,
        DtRole.DT_ROLE,
        DtRoleAccess.DT_ROLE_ACCESS
    )
}
