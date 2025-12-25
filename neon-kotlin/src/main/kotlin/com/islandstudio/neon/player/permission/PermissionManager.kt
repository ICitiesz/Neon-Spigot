package com.islandstudio.neon.player.permission

import com.islandstudio.neon.apinew.dto.action.player.permission.AddPermissionListActionDTO
import com.islandstudio.neon.apinew.entity.player.Permission
import com.islandstudio.neon.apinew.facade.player.IPermissionFacade
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import com.islandstudio.neon.shared.core.initialization.IRunnerAsync
import java.util.Locale.getDefault

class PermissionManager: IRunnerAsync, IComponentProvider {
    private val pluginContext = getComponent<IPluginContext>()

    init {
        getKoin().declare(this)
    }

    override suspend fun runSuspend() {
        pluginContext.getPluginLogger().info("Initializing permission manager...")

        val permissionFacade = getComponent<IPermissionFacade>()
        val serverPermissions = getAllPermissionFromDatabase()
        val clientAllNeonPermissions = NeonPermission.getAllPermission()
        val clientMainNeonPermissions = NeonPermission.getAllMainPermission()

        val serverPermissionCodes = serverPermissions.map { serverPermission -> serverPermission.code }
        val newPermissionList: ArrayList<AddPermissionListActionDTO.PermissionActionDTO> = arrayListOf()

        clientMainNeonPermissions.forEach { clientMainNeonPermission ->
            /* For empty server permission and non-existing main permission:
            * - It Will add to the server along with its sub-permission
            *  */
            when {
                serverPermissions.isEmpty() || clientMainNeonPermission.permissionCode !in serverPermissionCodes -> {
                    val subPermissions = NeonPermission.getSubPermissions(clientMainNeonPermission)
                        .map { subPermission ->
                            AddPermissionListActionDTO.PermissionActionDTO(
                                subPermission.permissionCode.split('_').joinToString(" ") {
                                    it.lowercase().replaceFirstChar { char ->
                                        if (char.isLowerCase()) char.titlecase(getDefault()) else char.toString()
                                    }
                                },
                                subPermission.permissionCode,
                                subPermission.description
                            )
                        }

                    newPermissionList.add(
                        AddPermissionListActionDTO.PermissionActionDTO(
                            clientMainNeonPermission.permissionCode.split('_').joinToString(" ") {
                                it.lowercase().replaceFirstChar { char ->
                                    if (char.isLowerCase()) char.titlecase(getDefault()) else char.toString()
                                }
                            },
                            clientMainNeonPermission.permissionCode,
                            clientMainNeonPermission.description,
                            null,
                            subPermissions
                        )
                    )
                }

                /* For existing server main permission:
                * - It will add the sub-permission if not exists
                *  */
                clientMainNeonPermission.permissionCode in serverPermissionCodes -> {
                    val serverMainPermission = serverPermissions.find { serverPermission -> serverPermission.code == clientMainNeonPermission.permissionCode }

                    val subPermissions = NeonPermission.getSubPermissions(clientMainNeonPermission)
                        .filter { subPermission -> subPermission.permissionCode !in serverPermissionCodes }
                        .map {subPermission ->
                            AddPermissionListActionDTO.PermissionActionDTO(
                                subPermission.permissionCode.split('_').joinToString(" ") {
                                    it.lowercase().replaceFirstChar { char ->
                                        if (char.isLowerCase()) char.titlecase(getDefault()) else char.toString()
                                    }
                                },
                                subPermission.permissionCode,
                                subPermission.description,
                                serverMainPermission?.id
                            )
                        }

                    newPermissionList.addAll(subPermissions)
                }
            }
        }

        if (newPermissionList.isNotEmpty()) {
            permissionFacade.addPermissionList(AddPermissionListActionDTO(newPermissionList))
        }

        if (serverPermissions.isNotEmpty()) {
           val removalPermissionList = serverPermissions
               .filter { serverPermission -> serverPermission.code !in clientAllNeonPermissions.map { it.permissionCode } }
               .map { serverPermission -> serverPermission.id!! }
               .toCollection(ArrayList())

            if (removalPermissionList.isNotEmpty()) {
                permissionFacade.removePermissionListByIds(removalPermissionList)
            }
        }
    }

    private suspend fun getAllPermissionFromDatabase(): ArrayList<Permission> {
        val permissionFacade = getComponent<IPermissionFacade>()

        return permissionFacade.getAllPermissions().getResult().get()
    }


}