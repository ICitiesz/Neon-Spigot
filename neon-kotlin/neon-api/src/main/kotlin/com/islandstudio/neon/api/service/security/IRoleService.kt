package com.islandstudio.neon.api.service.security

import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.security.CreateRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.role.GetRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.role.RemoveRoleRequestDTO
import com.islandstudio.neon.api.entity.security.RoleEntity

interface IRoleService {
    fun createRole(invoker: String?, request: CreateRoleRequestDTO): IActionResult<RoleEntity?>

    fun getRoleById(request: GetRoleRequestDTO): IActionResult<RoleEntity?>

    fun getRoleByRoleCode(request: GetRoleRequestDTO): IActionResult<RoleEntity?>

    fun removeRoleByRoleCode(request: RemoveRoleRequestDTO): IActionResult<Int>
}