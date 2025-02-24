package com.islandstudio.neon.api.service.security

import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.security.permission.*
import com.islandstudio.neon.api.dto.response.security.RolePermissionListResponseDTO
import com.islandstudio.neon.api.entity.security.RolePermissionEntity

interface IRolePermissionService {
    fun addRolePermission(invoker: String?, request: GrantRolePermissionRequestDTO): IActionResult<RolePermissionEntity?>

    fun addRolePermission(invoker: String?, request: BatchGrantRolePermissionRequestDTO): IActionResult<RolePermissionListResponseDTO>

    fun getRolePermissionById(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionEntity?>

    fun getRolePermissionListByRoleId(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionListResponseDTO>

    fun getRolePermissionListByPermissionId(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionListResponseDTO>

    fun getChildRolePermissionList(request: GetRolePermissionRequestDTO): IActionResult<RolePermissionListResponseDTO>

    fun removeRolePermissionById(request: RevokeRolePermissionRequestDTO): IActionResult<Int>

    fun removeRolePermissionById(request: BatchRevokeRolePermissionRequestDTO): IActionResult<Int>

    fun removeRolePermissionByRoleId(request: RevokeRolePermissionRequestDTO): IActionResult<Int>

    fun removkeRolePermissionByRoleId(request: BatchRevokeRolePermissionRequestDTO): IActionResult<Int>
}