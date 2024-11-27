package com.islandstudio.neon.stable.core.database.repository

import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.AccessPermission
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.records.DtAccessPermissionRecord
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.references.DT_ACCESS_PERMISSION
import org.jooq.DSLContext
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class AccessPermissionRepository: ModuleInjector {
    private val dbContext by inject<DSLContext>()

    fun addAccessPermission(accessPermission: AccessPermission) {
        val record = DtAccessPermissionRecord(accessPermission)

        dbContext.executeInsert(record)
    }

    fun getAccessPermissionById(permissionId: Long): AccessPermission? {
        val sqlQuery = dbContext
            .select()
            .from(DT_ACCESS_PERMISSION)
            .where(DT_ACCESS_PERMISSION.PERMISSION_ID.eq(permissionId))

        return dbContext.fetchOne(sqlQuery)?.into(AccessPermission::class.java)
    }

    fun getAccessPermissionByPermissionCode(permissionCode: String): AccessPermission? {
        val sqlQuery = dbContext
            .select()
            .from(DT_ACCESS_PERMISSION)
            .where(DT_ACCESS_PERMISSION.PERMISSION_CODE.eq(permissionCode))


        return dbContext.fetchOne(sqlQuery)?.into(AccessPermission::class.java)
    }

    fun getAllAccessPermission(): List<AccessPermission> {
        val sqlQuery = dbContext
            .select()
            .from(DT_ACCESS_PERMISSION)

        return dbContext.fetchAsync(sqlQuery).toCompletableFuture().join().into(AccessPermission::class.java)
    }

    fun getAccessPermissionListByIdList(permissionIds: List<Long>): List<AccessPermission> {
        val sqlQuery = dbContext
            .select()
            .from(DT_ACCESS_PERMISSION)
            .where(DT_ACCESS_PERMISSION.PERMISSION_ID.`in`(permissionIds))

        return dbContext.fetchAsync(sqlQuery).toCompletableFuture().join().into(AccessPermission::class.java)
    }

    fun isExistByPermissionCode(permissionCode: String): Boolean {
        val sqlQuery = dbContext
            .select()
            .from(DT_ACCESS_PERMISSION)
            .where(DT_ACCESS_PERMISSION.PERMISSION_CODE.eq(permissionCode))

        return dbContext.fetchExists(sqlQuery)
    }
}