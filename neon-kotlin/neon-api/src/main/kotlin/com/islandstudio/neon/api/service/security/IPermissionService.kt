package com.islandstudio.neon.api.service.security

import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.security.permission.AddPermissionRequestDTO
import com.islandstudio.neon.api.dto.request.security.permission.BatchAddPermissionRequestDTO
import com.islandstudio.neon.api.dto.request.security.permission.BatchRemovePermissionRequestDTO
import com.islandstudio.neon.api.dto.request.security.permission.RemovePermissionRequestDTO
import com.islandstudio.neon.api.dto.response.security.PermissionListResponseDTO
import com.islandstudio.neon.api.entity.security.PermissionEntity

interface IPermissionService {
    fun addPermission(request: AddPermissionRequestDTO): IActionResult<PermissionEntity?>

    fun addPermission(request: BatchAddPermissionRequestDTO): IActionResult<PermissionListResponseDTO>

    fun removePermissionById(request: RemovePermissionRequestDTO): IActionResult<Int>

    fun removePermissionById(request: BatchRemovePermissionRequestDTO): IActionResult<Int>

    fun removePermissionByPermissionCode(request: RemovePermissionRequestDTO): IActionResult<Int>

    fun removePermissionByPermissionCode(request: BatchRemovePermissionRequestDTO): IActionResult<Int>

    fun getAllPermission(): IActionResult<PermissionListResponseDTO>
}