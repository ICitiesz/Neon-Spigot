package com.islandstudio.neon.api.service.security

import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.security.permission.GetRolePermissionRequestDTO
import com.islandstudio.neon.api.dto.request.security.permission.GrantRolePermissionRequestDTO
import com.islandstudio.neon.api.dto.request.security.permission.RevokeRolePermissionRequestDTO
import com.islandstudio.neon.api.dto.response.security.RolePermissionListResponseDTO
import com.islandstudio.neon.api.entity.security.RolePermissionEntity

interface IRolePermissionService {
    fun addRolePermission(invoker: String?, request: GrantRolePermissionRequestDTO): IActionResult<RolePermissionEntity?>

    fun getRolePermissionById(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionEntity?>

    fun getRolePermissionListByRoleId(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionListResponseDTO>

    fun getRolePermissionListByPermissionId(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionListResponseDTO>

    fun getChildRolePermissionList(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionListResponseDTO>

    fun removeRolePermissionById(request: RevokeRolePermissionRequestDTO): IActionResult<Int>

    fun removeRolePermissionByRoleId(request: RevokeRolePermissionRequestDTO): IActionResult<Int>
}