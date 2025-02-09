/*
 * This file is generated by jOOQ.
 */
package com.islandstudio.neon.api.schema.neon_data.keys


import com.islandstudio.neon.api.schema.neon_data.tables.TPermission
import com.islandstudio.neon.api.schema.neon_data.tables.TPlayerProfile
import com.islandstudio.neon.api.schema.neon_data.tables.TRole
import com.islandstudio.neon.api.schema.neon_data.tables.TRolePermission
import com.islandstudio.neon.api.schema.neon_data.tables.records.TPermissionRecord
import com.islandstudio.neon.api.schema.neon_data.tables.records.TPlayerProfileRecord
import com.islandstudio.neon.api.schema.neon_data.tables.records.TRolePermissionRecord
import com.islandstudio.neon.api.schema.neon_data.tables.records.TRoleRecord

import org.jooq.ForeignKey
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal



// -------------------------------------------------------------------------
// UNIQUE and PRIMARY KEY definitions
// -------------------------------------------------------------------------

val PK_T_PERMISSION: UniqueKey<TPermissionRecord> = Internal.createUniqueKey(TPermission.T_PERMISSION, DSL.name("PK_T_PERMISSION"), arrayOf(TPermission.T_PERMISSION.PERMISSION_ID), true)
val UQ_T_PERMISSION_PERMISSION_CODE: UniqueKey<TPermissionRecord> = Internal.createUniqueKey(TPermission.T_PERMISSION, DSL.name("UQ_T_PERMISSION_PERMISSION_CODE"), arrayOf(TPermission.T_PERMISSION.PERMISSION_CODE), true)
val PK_T_PLAYER_PROFILE: UniqueKey<TPlayerProfileRecord> = Internal.createUniqueKey(TPlayerProfile.T_PLAYER_PROFILE, DSL.name("PK_T_PLAYER_PROFILE"), arrayOf(TPlayerProfile.T_PLAYER_PROFILE.ID), true)
val UQ_T_PLAYER_PROFILE_P_UUID_P_NAME: UniqueKey<TPlayerProfileRecord> = Internal.createUniqueKey(TPlayerProfile.T_PLAYER_PROFILE, DSL.name("UQ_T_PLAYER_PROFILE_P_UUID_P_NAME"), arrayOf(TPlayerProfile.T_PLAYER_PROFILE.PLAYER_UUID, TPlayerProfile.T_PLAYER_PROFILE.PLAYER_NAME), true)
val PK_T_ROLE: UniqueKey<TRoleRecord> = Internal.createUniqueKey(TRole.T_ROLE, DSL.name("PK_T_ROLE"), arrayOf(TRole.T_ROLE.ROLE_ID), true)
val UQ_T_ROLE_ROLE_CODE: UniqueKey<TRoleRecord> = Internal.createUniqueKey(TRole.T_ROLE, DSL.name("UQ_T_ROLE_ROLE_CODE"), arrayOf(TRole.T_ROLE.ROLE_CODE), true)
val PK_T_ROLE_PERMISSION: UniqueKey<TRolePermissionRecord> = Internal.createUniqueKey(TRolePermission.T_ROLE_PERMISSION, DSL.name("PK_T_ROLE_PERMISSION"), arrayOf(TRolePermission.T_ROLE_PERMISSION.ROLE_PERMISSION_ID), true)
val UQ_T_ROLE_PERMISSION_ROLE_ID_PERMISSION_ID: UniqueKey<TRolePermissionRecord> = Internal.createUniqueKey(TRolePermission.T_ROLE_PERMISSION, DSL.name("UQ_T_ROLE_PERMISSION_ROLE_ID_PERMISSION_ID"), arrayOf(TRolePermission.T_ROLE_PERMISSION.ROLE_ID, TRolePermission.T_ROLE_PERMISSION.PERMISSION_ID), true)

// -------------------------------------------------------------------------
// FOREIGN KEY definitions
// -------------------------------------------------------------------------

val FK_T_PERMISSION_PARENT_PERMISSION_ID: ForeignKey<TPermissionRecord, TPermissionRecord> = Internal.createForeignKey(TPermission.T_PERMISSION, DSL.name("FK_T_PERMISSION_PARENT_PERMISSION_ID"), arrayOf(TPermission.T_PERMISSION.PARENT_PERMISSION_ID), com.islandstudio.neon.api.schema.neon_data.keys.PK_T_PERMISSION, arrayOf(TPermission.T_PERMISSION.PERMISSION_ID), true)
val FK_T_PLAYER_PROFILE_T_ROLE_ROLE_ID: ForeignKey<TPlayerProfileRecord, TRoleRecord> = Internal.createForeignKey(TPlayerProfile.T_PLAYER_PROFILE, DSL.name("FK_T_PLAYER_PROFILE_T_ROLE_ROLE_ID"), arrayOf(TPlayerProfile.T_PLAYER_PROFILE.ROLE_ID), com.islandstudio.neon.api.schema.neon_data.keys.PK_T_ROLE, arrayOf(TRole.T_ROLE.ROLE_ID), true)
val FK_T_ROLE_PERMISSION_PARENT_ROLE_PERMISSION_ID: ForeignKey<TRolePermissionRecord, TRolePermissionRecord> = Internal.createForeignKey(TRolePermission.T_ROLE_PERMISSION, DSL.name("FK_T_ROLE_PERMISSION_PARENT_ROLE_PERMISSION_ID"), arrayOf(TRolePermission.T_ROLE_PERMISSION.PARENT_ROLE_PERMISSION_ID), com.islandstudio.neon.api.schema.neon_data.keys.PK_T_ROLE_PERMISSION, arrayOf(TRolePermission.T_ROLE_PERMISSION.ROLE_PERMISSION_ID), true)
val FK_T_ROLE_PERMISSION_PERMISSION_ID: ForeignKey<TRolePermissionRecord, TPermissionRecord> = Internal.createForeignKey(TRolePermission.T_ROLE_PERMISSION, DSL.name("FK_T_ROLE_PERMISSION_PERMISSION_ID"), arrayOf(TRolePermission.T_ROLE_PERMISSION.PERMISSION_ID), com.islandstudio.neon.api.schema.neon_data.keys.PK_T_PERMISSION, arrayOf(TPermission.T_PERMISSION.PERMISSION_ID), true)
val FK_T_ROLE_PERMISSION_ROLE_ID: ForeignKey<TRolePermissionRecord, TRoleRecord> = Internal.createForeignKey(TRolePermission.T_ROLE_PERMISSION, DSL.name("FK_T_ROLE_PERMISSION_ROLE_ID"), arrayOf(TRolePermission.T_ROLE_PERMISSION.ROLE_ID), com.islandstudio.neon.api.schema.neon_data.keys.PK_T_ROLE, arrayOf(TRole.T_ROLE.ROLE_ID), true)
