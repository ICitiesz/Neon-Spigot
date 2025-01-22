/*
 * This file is generated by jOOQ.
 */
package com.islandstudio.neon.api.schema.neon_data.keys


import com.islandstudio.neon.api.schema.neon_data.tables.TPlayerProfile
import com.islandstudio.neon.api.schema.neon_data.tables.TRole
import com.islandstudio.neon.api.schema.neon_data.tables.records.TPlayerProfileRecord
import com.islandstudio.neon.api.schema.neon_data.tables.records.TRoleRecord

import org.jooq.ForeignKey
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal



// -------------------------------------------------------------------------
// UNIQUE and PRIMARY KEY definitions
// -------------------------------------------------------------------------

val PK_T_PLAYER_PROFILE: UniqueKey<TPlayerProfileRecord> = Internal.createUniqueKey(TPlayerProfile.T_PLAYER_PROFILE, DSL.name("PK_T_PLAYER_PROFILE"), arrayOf(TPlayerProfile.T_PLAYER_PROFILE.PLAYER_UUID), true)
val UQ_T_PLAYER_PROFILE_PLAYER_NAME: UniqueKey<TPlayerProfileRecord> = Internal.createUniqueKey(TPlayerProfile.T_PLAYER_PROFILE, DSL.name("UQ_T_PLAYER_PROFILE_PLAYER_NAME"), arrayOf(TPlayerProfile.T_PLAYER_PROFILE.PLAYER_NAME), true)
val PK_T_ROLE: UniqueKey<TRoleRecord> = Internal.createUniqueKey(TRole.T_ROLE, DSL.name("PK_T_ROLE"), arrayOf(TRole.T_ROLE.ROLE_ID), true)
val UQ_T_ROLE_ROLE_CODE: UniqueKey<TRoleRecord> = Internal.createUniqueKey(TRole.T_ROLE, DSL.name("UQ_T_ROLE_ROLE_CODE"), arrayOf(TRole.T_ROLE.ROLE_CODE), true)

// -------------------------------------------------------------------------
// FOREIGN KEY definitions
// -------------------------------------------------------------------------

val FK_T_PLAYER_PROFILE_T_ROLE_ROLE_ID: ForeignKey<TPlayerProfileRecord, TRoleRecord> = Internal.createForeignKey(TPlayerProfile.T_PLAYER_PROFILE, DSL.name("FK_T_PLAYER_PROFILE_T_ROLE_ROLE_ID"), arrayOf(TPlayerProfile.T_PLAYER_PROFILE.ROLE_ID), com.islandstudio.neon.api.schema.neon_data.keys.PK_T_ROLE, arrayOf(TRole.T_ROLE.ROLE_ID), true)
