package com.islandstudio.neon.api.repository.security

import com.islandstudio.neon.api.dto.security.PermissionDto

interface PermissionRepository {
    suspend fun addPermissionList(): List<PermissionDto>
    suspend fun removeListById(ids: List<Long>): Boolean
    suspend fun removeListByCode(codes: List<String>): Boolean
}