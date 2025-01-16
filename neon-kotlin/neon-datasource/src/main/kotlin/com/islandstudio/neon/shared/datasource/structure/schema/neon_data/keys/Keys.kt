/*
 * This file is generated by jOOQ.
 */
package com.islandstudio.neon.shared.datasource.structure.schema.neon_data.keys


import com.islandstudio.neon.shared.datasource.structure.schema.neon_data.tables.DtAccessPermission
import com.islandstudio.neon.shared.datasource.structure.schema.neon_data.tables.DtPlayerProfile
import com.islandstudio.neon.shared.datasource.structure.schema.neon_data.tables.DtRole
import com.islandstudio.neon.shared.datasource.structure.schema.neon_data.tables.DtRoleAccess
import com.islandstudio.neon.shared.datasource.structure.schema.neon_data.tables.records.DtAccessPermissionRecord
import com.islandstudio.neon.shared.datasource.structure.schema.neon_data.tables.records.DtPlayerProfileRecord
import com.islandstudio.neon.shared.datasource.structure.schema.neon_data.tables.records.DtRoleAccessRecord
import com.islandstudio.neon.shared.datasource.structure.schema.neon_data.tables.records.DtRoleRecord

import org.jooq.ForeignKey
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal



// -------------------------------------------------------------------------
// UNIQUE and PRIMARY KEY definitions
// -------------------------------------------------------------------------

val PK_DT_ACCESS_PERMISSION: UniqueKey<DtAccessPermissionRecord> = Internal.createUniqueKey(DtAccessPermission.DT_ACCESS_PERMISSION, DSL.name("PK_DT_ACCESS_PERMISSION"), arrayOf(DtAccessPermission.DT_ACCESS_PERMISSION.PERMISSION_ID), true)
val UQ_DT_ACCESS_PERMISSION_PERMISSION_CODE: UniqueKey<DtAccessPermissionRecord> = Internal.createUniqueKey(DtAccessPermission.DT_ACCESS_PERMISSION, DSL.name("UQ_DT_ACCESS_PERMISSION_PERMISSION_CODE"), arrayOf(DtAccessPermission.DT_ACCESS_PERMISSION.PERMISSION_CODE), true)
val UQ_DT_ACCESS_PERMISSION_PERMISSION_NAME: UniqueKey<DtAccessPermissionRecord> = Internal.createUniqueKey(DtAccessPermission.DT_ACCESS_PERMISSION, DSL.name("UQ_DT_ACCESS_PERMISSION_PERMISSION_NAME"), arrayOf(DtAccessPermission.DT_ACCESS_PERMISSION.PERMISSION_NAME), true)
val PK_DT_PLAYER_PROFILE: UniqueKey<DtPlayerProfileRecord> = Internal.createUniqueKey(DtPlayerProfile.DT_PLAYER_PROFILE, DSL.name("PK_DT_PLAYER_PROFILE"), arrayOf(DtPlayerProfile.DT_PLAYER_PROFILE.PLAYER_UUID), true)
val UQ_DT_PLAYER_PROFILE_PLAYER_NAME: UniqueKey<DtPlayerProfileRecord> = Internal.createUniqueKey(DtPlayerProfile.DT_PLAYER_PROFILE, DSL.name("UQ_DT_PLAYER_PROFILE_PLAYER_NAME"), arrayOf(DtPlayerProfile.DT_PLAYER_PROFILE.PLAYER_NAME), true)
val PK_DT_ROLE: UniqueKey<DtRoleRecord> = Internal.createUniqueKey(DtRole.DT_ROLE, DSL.name("PK_DT_ROLE"), arrayOf(DtRole.DT_ROLE.ROLE_ID), true)
val UQ_DT_ROLE_ROLE_CODE: UniqueKey<DtRoleRecord> = Internal.createUniqueKey(DtRole.DT_ROLE, DSL.name("UQ_DT_ROLE_ROLE_CODE"), arrayOf(DtRole.DT_ROLE.ROLE_CODE), true)
val PK_ROLE_ACCESS: UniqueKey<DtRoleAccessRecord> = Internal.createUniqueKey(DtRoleAccess.DT_ROLE_ACCESS, DSL.name("PK_ROLE_ACCESS"), arrayOf(DtRoleAccess.DT_ROLE_ACCESS.ROLE_ACCESS_ID), true)
val UQ_DT_ROLE_ACCESS_BATCH_1: UniqueKey<DtRoleAccessRecord> = Internal.createUniqueKey(DtRoleAccess.DT_ROLE_ACCESS, DSL.name("UQ_DT_ROLE_ACCESS_BATCH_1"), arrayOf(DtRoleAccess.DT_ROLE_ACCESS.ROLE_ID, DtRoleAccess.DT_ROLE_ACCESS.PERMISSION_ID), true)

// -------------------------------------------------------------------------
// FOREIGN KEY definitions
// -------------------------------------------------------------------------

val FK_DT_PLAYER_PROFILE_DT_ROLE_ROLE_ID: ForeignKey<DtPlayerProfileRecord, DtRoleRecord> = Internal.createForeignKey(DtPlayerProfile.DT_PLAYER_PROFILE, DSL.name("FK_DT_PLAYER_PROFILE_DT_ROLE_ROLE_ID"), arrayOf(DtPlayerProfile.DT_PLAYER_PROFILE.ROLE_ID), com.islandstudio.neon.shared.datasource.structure.schema.neon_data.keys.PK_DT_ROLE, arrayOf(DtRole.DT_ROLE.ROLE_ID), true)
val FK_DT_ROLE_ACCESS__DT_ROLE_ROLE_ID: ForeignKey<DtRoleAccessRecord, DtRoleRecord> = Internal.createForeignKey(DtRoleAccess.DT_ROLE_ACCESS, DSL.name("FK_DT_ROLE_ACCESS__DT_ROLE_ROLE_ID"), arrayOf(DtRoleAccess.DT_ROLE_ACCESS.ROLE_ID), com.islandstudio.neon.shared.datasource.structure.schema.neon_data.keys.PK_DT_ROLE, arrayOf(DtRole.DT_ROLE.ROLE_ID), true)
val FK_DT_ROLE_ACCESS_PERMISSION__DT_ACCESS_PERMISSION_PERMISSION_ID: ForeignKey<DtRoleAccessRecord, DtAccessPermissionRecord> = Internal.createForeignKey(DtRoleAccess.DT_ROLE_ACCESS, DSL.name("FK_DT_ROLE_ACCESS_PERMISSION__DT_ACCESS_PERMISSION_PERMISSION_ID"), arrayOf(DtRoleAccess.DT_ROLE_ACCESS.PERMISSION_ID), com.islandstudio.neon.shared.datasource.structure.schema.neon_data.keys.PK_DT_ACCESS_PERMISSION, arrayOf(DtAccessPermission.DT_ACCESS_PERMISSION.PERMISSION_ID), true)
