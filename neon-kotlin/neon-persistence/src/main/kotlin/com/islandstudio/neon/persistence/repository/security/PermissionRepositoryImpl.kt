package com.islandstudio.neon.persistence.repository.security

import com.islandstudio.neon.api.dto.security.PermissionDto
import com.islandstudio.neon.api.repository.security.PermissionRepository
import com.islandstudio.neon.persistence.DatabaseContext
import com.islandstudio.neon.persistence.entity.security.Permission
import com.islandstudio.neon.persistence.entity.security.PermissionTable
import com.islandstudio.neon.persistence.repository.BaseRepositoryImpl
import org.koin.core.annotation.Single

@Single
class PermissionRepositoryImpl(dbContext: DatabaseContext):
    BaseRepositoryImpl<Permission, PermissionTable>(dbContext, PermissionTable), PermissionRepository {
    override suspend fun addPermissionList(): List<PermissionDto> {
        TODO("Not yet implemented")
    }

    override suspend fun removeListById(ids: List<Long>): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun removeListByCode(codes: List<String>): Boolean {
        TODO("Not yet implemented")
    }

}