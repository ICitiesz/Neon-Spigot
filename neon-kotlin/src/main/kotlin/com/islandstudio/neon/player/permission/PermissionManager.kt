package com.islandstudio.neon.player.permission

import com.islandstudio.neon.apinew.dto.action.player.permission.AddPermissionListActionDTO
import com.islandstudio.neon.apinew.dto.action.player.permission.AddRolePermisionActionDTO
import com.islandstudio.neon.apinew.dto.result.player.permission.RolePermissionDetailResultDTO
import com.islandstudio.neon.apinew.entity.player.Permission
import com.islandstudio.neon.apinew.facade.player.IPermissionFacade
import com.islandstudio.neon.apinew.facade.player.IRolePermissionFacade
import com.islandstudio.neon.apinew.status.ActionResultStatus
import com.islandstudio.neon.command.CommandManager
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.core.di.injectComponent
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import com.islandstudio.neon.shared.core.initialization.IRunnerAsync
import org.bukkit.command.CommandSender
import java.util.Locale.getDefault

class PermissionManager: IRunnerAsync, IComponentProvider {
    private val pluginContext = getComponent<IPluginContext>()
    private val commandManager by injectComponent<CommandManager>()

    val commandDispatcher = PermissionCommandDispatcher(this)

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

    suspend fun getAllPermissionFromDatabase(): ArrayList<Permission> {
        val permissionFacade = getComponent<IPermissionFacade>()

        return permissionFacade.getAllPermissions().getResult().get()
    }

    suspend fun grantPermission(commander: CommandSender, roleCode: String, permissionCodes: ArrayList<String>) {
        val rolePermissionFacade = getComponent<IRolePermissionFacade>() // TODO: Need handle user context

        rolePermissionFacade.isRolePermissionExistByPermissionCodes(roleCode, permissionCodes).getResult { status, _ ->
            when (status) {
                is ActionResultStatus.RolePermissionAlreadyExist -> {
                    CommandSyntaxHandler.sendCommandSyntax(commander, status.message)
                    return
                }

                else -> return
            }
        }

        val clientMainNeonPermissions = NeonPermission.getAllMainPermission()
        val clientSubNeonPermissions = NeonPermission.getAllSubPermission()

        val newRolePermissionList: ArrayList<AddRolePermisionActionDTO.RolePermissionActionDTO> = arrayListOf()

        /* Prepare main role permissions */
        newRolePermissionList.addAll(clientMainNeonPermissions
            .filter { clientMainNeonPermission -> clientMainNeonPermission.permissionCode in permissionCodes }
            .map { AddRolePermisionActionDTO.RolePermissionActionDTO(it.permissionCode) }
        )

        /* Prepare sub role permissions */
        clientSubNeonPermissions
            .filter { clientSubNeonPermission -> clientSubNeonPermission.permissionCode in permissionCodes }
            .forEach { permission ->
                permission.mainPermission?.let { mainPermission ->
                    /* Add child to the parent if parent exist in the parent list */
                    if (mainPermission.permissionCode in newRolePermissionList.map { it.permissionCode }) {
                        val mainRolePermission = newRolePermissionList.first { it.permissionCode == mainPermission.permissionCode }

                        mainRolePermission.subRolePermissions.add(AddRolePermisionActionDTO.RolePermissionActionDTO(permission.permissionCode))
                        return@let
                    }

                    /* Add parent permission if not exist in the parent list */
                    newRolePermissionList.add(
                        AddRolePermisionActionDTO.RolePermissionActionDTO(
                            mainPermission.permissionCode,
                            null,
                            arrayListOf(AddRolePermisionActionDTO.RolePermissionActionDTO(permission.permissionCode))
                        )
                    )
                }
            }

        rolePermissionFacade.addRolePermissionList(AddRolePermisionActionDTO(roleCode, newRolePermissionList)).getResult().get().also {
            commandManager.updatePlayerAccessibleCommandsByRole(it)
            CommandSyntaxHandler.sendCommandSyntax(commander, "Permision has been granted!")
        }
    }

    suspend fun revokePermission(commander: CommandSender, roleCode: String, permissionCodes: ArrayList<String>) {
    }

    suspend fun getGrantedRolePermissions(roleId: Long): ArrayList<RolePermissionDetailResultDTO> {
        val rolePermissionFacade = getComponent<IRolePermissionFacade>()

        return rolePermissionFacade.getRolePermissionDetailList(roleId).getResult().get()
    }
}