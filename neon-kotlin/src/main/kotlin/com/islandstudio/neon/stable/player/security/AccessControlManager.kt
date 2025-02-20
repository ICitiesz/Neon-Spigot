package com.islandstudio.neon.stable.player.security

import com.islandstudio.neon.Neon
import com.islandstudio.neon.api.adapter.security.PermissionAdaptor
import com.islandstudio.neon.api.adapter.security.RolePermissionAdapter
import com.islandstudio.neon.api.dto.request.security.permission.*
import com.islandstudio.neon.api.dto.response.security.RolePermissionPermissionCodeDTO
import com.islandstudio.neon.api.entity.security.PermissionEntity
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.command.ICommandDispatcher
import com.islandstudio.neon.stable.command.properties.AccessibleCommand
import com.islandstudio.neon.stable.command.properties.CommandAlias
import com.islandstudio.neon.stable.player.security.permission.Permission
import org.bukkit.command.CommandSender
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class AccessControlManager: IComponentInjector {
    private val neon by inject<Neon>()
    private val rolePermissionAdapter by inject<RolePermissionAdapter>()

    companion object: IRunner, ICommandDispatcher, IComponentInjector {
        private val accessControlManager by inject<AccessControlManager>()
        private val permissionAlias = CommandAlias.PermissionAlias
        private val permissionAdaptor by inject<PermissionAdaptor>()

        override fun run() {
            val serverPermissions = accessControlManager.getPermissionFromDB()
            val clientAllPermissions = Permission.getAllPermission()
            val clientMainPermissions = Permission.getMainPermissions()

            val serverPermissionCodes = serverPermissions.map { x -> x.permissionCode }
            val addPermissionsList: ArrayList<PermissionDTO> = arrayListOf()

            clientMainPermissions.forEach {
                when {
                    /* For empty server permission and non-existing main permission:
                    * - It Will add to the server along with its sub-permission
                    *  */
                    serverPermissions.isEmpty() || it.permissionCode !in serverPermissionCodes -> {
                        val subPermissions = Permission.getSubPermissions(it)
                            .map { subPermission ->
                                PermissionDTO(
                                    subPermission.permissionCode,
                                    subPermission.description
                                )
                            }

                        addPermissionsList.add(
                            PermissionDTO(
                                it.permissionCode,
                                it.description,
                                subPermissions
                            )
                        )
                    }

                    /* For existing server main permission:
                   * - It will add the sub-permission if not exists
                   *  */
                    it.permissionCode in serverPermissionCodes -> {
                        val permissionEntity = serverPermissions.find { x -> x.permissionCode == it.permissionCode }

                        val subPermissions = Permission.getSubPermissions(it)
                            .filter { x -> x.permissionCode !in serverPermissionCodes }
                            .map { subPermission ->
                                PermissionDTO(
                                    subPermission.permissionCode,
                                    subPermission.description,
                                    parentPermissionId = permissionEntity?.permissionId
                                )
                            }

                        addPermissionsList.addAll(subPermissions)
                    }
                }
            }

            val batchAddRequest = BatchAddPermissionRequestDTO(addPermissionsList)

            permissionAdaptor.addPermission(batchAddRequest).onSuccess {

            }.onFailure {
                throw it.neonException!!
            }

            if (serverPermissions.isNotEmpty()) {
                val removalPermissionList = serverPermissions
                    .filter { it.permissionCode !in clientAllPermissions.map { x -> x.permissionCode } }
                    .map {
                        RemovePermissionRequestDTO(it.permissionId)
                    }

                permissionAdaptor.removePermission(BatchRemovePermissionRequestDTO(removalPermissionList)).onSuccess {

                }.onFailure {
                    throw it.neonException!!
                }
            }
        }

        override fun getCommandDispatcher(
            commander: CommandSender,
            accessibleCommands: ArrayList<AccessibleCommand>,
            args: Array<out String>
        ) {
            TODO("Not yet implemented")
        }

        override fun getTabCompletion(
            commander: CommandSender,
            accessibleCommand: ArrayList<AccessibleCommand>,
            args: Array<out String>
        ): MutableList<String> {
            return super.getTabCompletion(commander, accessibleCommand, args)
        }
    }

    fun grantPermission() {

    }

    fun revokePermission() {

    }

    fun getPermissionFromDB(): ArrayList<PermissionEntity> {
        val permissionList: ArrayList<PermissionEntity> = arrayListOf()

        permissionAdaptor.getAllPermission()
            .onSuccess {
                permissionList.addAll(it.result!!.permissionList)
            }.onFailure {
                neon.server.logger.severe("Error while trying to get permission from database! Please try again later!")
                throw it.neonException!!
            }

        return permissionList
    }

    fun getGrantedRolePermission(roleId: Long): ArrayList<RolePermissionPermissionCodeDTO> {
        val request = GetRolePermissionRequestDTO(roleId = roleId, includePermissionCode = true)
        val grantedRolePermissionList = ArrayList<RolePermissionPermissionCodeDTO>()

        rolePermissionAdapter.getRolePermissionList(request)
            .onSuccess {
                grantedRolePermissionList.addAll(it.result!!.rolePermissionPermissionCodeList)
            }
            .onFailure {
                neon.server.logger.severe("Error while trying to get granted role permission! Please try again later!")
                throw it.neonException!!
            }

        return grantedRolePermissionList
    }

    fun getGrantedParentPermission(grantedRolePermissions: ArrayList<RolePermissionPermissionCodeDTO>): ArrayList<RolePermissionPermissionCodeDTO> {
        return grantedRolePermissions.filter { permission -> permission.parentRolePermissionId == null }.toCollection(ArrayList())
    }

    fun getGrantedChildPermission(grantedRolePermissions: ArrayList<RolePermissionPermissionCodeDTO>): ArrayList<RolePermissionPermissionCodeDTO> {
        return grantedRolePermissions.filter { permission -> permission.parentRolePermissionId != null }.toCollection(ArrayList())
    }
}