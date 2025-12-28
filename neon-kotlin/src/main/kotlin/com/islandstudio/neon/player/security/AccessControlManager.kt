package com.islandstudio.neon.player.security

import com.islandstudio.neon.Neon
import com.islandstudio.neon.api.adapter.security.PermissionAdaptor
import com.islandstudio.neon.api.adapter.security.RolePermissionAdapter
import com.islandstudio.neon.api.dto.request.security.permission.*
import com.islandstudio.neon.api.dto.response.security.RolePermissionPermissionCodeDTO
import com.islandstudio.neon.api.entity.security.PermissionEntity
import com.islandstudio.neon.command.CommandManager
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.player.permission.NeonPermission
import com.islandstudio.neon.player.security.role.RoleManager
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.initialization.IRunner
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class AccessControlManager: IComponentInjector {
    private val neon by inject<Neon>()
    private val rolePermissionAdapter by inject<RolePermissionAdapter>()

    companion object: IRunner, IComponentInjector {
        private val accessControlManager by inject<AccessControlManager>()
        private val roleManager by inject<RoleManager>()
        private val permissionAdaptor by inject<PermissionAdaptor>()

        override fun run() {
            val serverPermissions = accessControlManager.getPermissionFromDB()
            val clientAllNeonPermissions = NeonPermission.getAllPermission()
            val clientMainNeonPermissions = NeonPermission.getAllMainPermission()

            val serverPermissionCodes = serverPermissions.map { x -> x.permissionCode }
            val addPermissionsList: ArrayList<PermissionDTO> = arrayListOf()

            clientMainNeonPermissions.forEach {
                when {
                    /* For empty server permission and non-existing main permission:
                    * - It Will add to the server along with its sub-permission
                    *  */
                    serverPermissions.isEmpty() || it.permissionCode !in serverPermissionCodes -> {
                        val subPermissions = NeonPermission.getSubPermissions(it)
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

                        val subPermissions = NeonPermission.getSubPermissions(it)
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
                    .filter { it.permissionCode !in clientAllNeonPermissions.map { x -> x.permissionCode } }
                    .map {
                        RemovePermissionRequestDTO(it.permissionId)
                    }

                permissionAdaptor.removePermission(BatchRemovePermissionRequestDTO(removalPermissionList)).onSuccess {

                }.onFailure {
                    throw it.neonException!!
                }
            }
        }
    }

    fun grantPermission(commander: CommandSender, roleCode: String, permissionCodes: ArrayList<String>) {
        val role = roleManager.getRole(commander, roleCode = roleCode) ?: return

        val clientMainNeonPermissions = NeonPermission.getAllMainPermission()
        val clientSubNeonPermissions = NeonPermission.getAllSubPermission()
        val serverPermissions = getPermissionFromDB().also {
            val clientAllNeonPermissionCodes = NeonPermission.getAllPermission().map { x -> x.permissionCode }

            permissionCodes
                .filter { x -> x !in it.map { y -> y.permissionCode } || x !in clientAllNeonPermissionCodes }
                .also { filteredPermissionCodes ->
                    if (filteredPermissionCodes.isEmpty()) return@also

                    return CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Invalid permissions: " +
                        "${ChatColor.WHITE}${it.joinToString(", ")}")
                }
        }

        val grantedRolePermissions = getGrantedRolePermission(role.roleId!!).also {
            it.filter { x -> x.permissionCode in permissionCodes }.also { filteredGrantedRolePermissions ->
                if (filteredGrantedRolePermissions.isEmpty()) return@also

                return CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.YELLOW}Permission already assigned to this role: " +
                        "${ChatColor.WHITE}${it.joinToString(", ") { x -> x.permissionCode!! }}")
            }
        }

        val pendingRolePermissions: ArrayList<GrantRolePermissionRequestDTO> = arrayListOf()

        /* Prepare main permission */
        serverPermissions
            .filter {
                x -> x.permissionCode in permissionCodes && x.permissionCode in clientMainNeonPermissions.map { x -> x.permissionCode }
            }
            .forEach {
                pendingRolePermissions.add(GrantRolePermissionRequestDTO(roleId = role.roleId, permissionId = it.permissionId!!))
            }

        /* Prepare sub permission */
        serverPermissions
            .filter {
                x -> x.permissionCode in permissionCodes && x.permissionCode in clientSubNeonPermissions.map { x -> x.permissionCode }
            }
            .forEach { serverPermission ->
                val subPermission = clientSubNeonPermissions.find { x -> x.permissionCode == serverPermission.permissionCode }!!

                /* Attempt 1: Search the main role permission from the granted role permission */
                grantedRolePermissions
                    .find { x -> x.permissionCode == subPermission.mainPermission!!.permissionCode }
                    ?.let {
                        pendingRolePermissions.add(GrantRolePermissionRequestDTO(
                            roleId = role.roleId,
                            permissionId = serverPermission.permissionId!!,
                            parentRolePermissionId = it.rolePermissionId
                        ))

                        return@forEach
                    }

                /* Attempt 2: Search the main role permission from the pending role permission  */
                val mainPermission = serverPermissions.single { x -> x.permissionCode == subPermission.mainPermission!!.permissionCode }
                val pendingMainRolePermission = pendingRolePermissions.find { x -> x.permissionId == mainPermission.permissionId }

                pendingMainRolePermission?.let {
                    it.subRolePermissions.add(GrantRolePermissionRequestDTO(
                        roleId = role.roleId, permissionId =
                            serverPermission.permissionId!!
                    ))

                    return@forEach
                }

                /* Attempt 3: Prepare both main and sub role permission */
                val mainRolePermission = GrantRolePermissionRequestDTO(
                    roleId = role.roleId,
                    permissionId = mainPermission.permissionId!!,
                    subRolePermissions = arrayListOf(GrantRolePermissionRequestDTO(
                        roleId = role.roleId,
                        permissionId = serverPermission.permissionId!!,
                    ))
                )

                pendingRolePermissions.add(mainRolePermission)
            }

        rolePermissionAdapter.grantRolePermission(
            CommandManager.getCommanderName(commander),
            BatchGrantRolePermissionRequestDTO(pendingRolePermissions)
        ).onSuccess {
            //CommandManager.updatePlayerAccessibleCommandsByRole(role.roleId!!)

            CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.GREEN}Selected permission(s) has been granted to the role " +
                    "'${ChatColor.WHITE}${role.roleCode}${ChatColor.GREEN}'!")
        }.onFailure {
            CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Error while trying to grant permission to role!" +
                    " Please try again later!")

            throw it.neonException!!
        }
    }

    fun revokePermission(commander: CommandSender, roleCode: String, permissionCodes: ArrayList<String>, revokeAll: Boolean = false) {
        val role = roleManager.getRole(commander, roleCode = roleCode) ?: return

        val clientMainNeonPermissions = NeonPermission.getAllMainPermission()
        val clientSubNeonPermissions = NeonPermission.getAllSubPermission()

        val grantedRolePermissions = getGrantedRolePermission(role.roleId!!).also { grantedRolePermissionList ->
            if (revokeAll) {
                if (grantedRolePermissionList.isEmpty()) {
                    return CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}The target role does not have any permissions granted!")
                }

                return@also
            }

            getPermissionFromDB().also { permissionFromDB ->
                val clientAllNeonPermissionCodes = NeonPermission.getAllPermission().map { x -> x.permissionCode }

                permissionCodes.filter { x -> x !in permissionFromDB.map { y -> y.permissionCode } || x !in clientAllNeonPermissionCodes }.also {
                    if (it.isEmpty()) return@also

                    return CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Invalid permissions: " +
                            "${ChatColor.WHITE}${it.joinToString(", ")}")
                }
            }

            permissionCodes.filter { x -> x !in grantedRolePermissionList.map { y -> y.permissionCode } }.also {
                if (it.isEmpty()) return@also

                return CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Invalid permission or already revoked from this role: " +
                        "${ChatColor.WHITE}${it.joinToString(", ")}")
            }
        }

        val pendingRolePermissions = grantedRolePermissions
            .filter { x ->
                if (revokeAll) return@filter true

                x.permissionCode in permissionCodes && (x.permissionCode in clientMainNeonPermissions.map { y -> y.permissionCode }
                    || x.permissionCode in clientSubNeonPermissions.map { y -> y.permissionCode } )
            }
            .map {
                RevokeRolePermissionRequestDTO(rolePermissionId = it.rolePermissionId)
            }

        rolePermissionAdapter.revokeRolepermission(BatchRevokeRolePermissionRequestDTO(pendingRolePermissions))
            .onSuccess {
                //CommandManager.updatePlayerAccessibleCommandsByRole(role.roleId!!)

                if (revokeAll) {
                    return@onSuccess CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.GREEN}All permissions has been revoked from the role " +
                            "'${ChatColor.WHITE}${role.roleCode}${ChatColor.GREEN}'!")
                }

                CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.GREEN}Selected permission(s) has been revoked from the role " +
                        "'${ChatColor.WHITE}${role.roleCode}${ChatColor.GREEN}'!")
            }
            .onFailure {
                CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Error while trying to revoke permission from the role!" +
                        " Please try again later!")
                throw it.neonException!!
            }
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
}