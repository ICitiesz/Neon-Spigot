package com.islandstudio.neon.stable.player.security

import com.islandstudio.neon.Neon
import com.islandstudio.neon.api.adapter.security.PermissionAdaptor
import com.islandstudio.neon.api.adapter.security.RolePermissionAdapter
import com.islandstudio.neon.api.dto.request.security.permission.*
import com.islandstudio.neon.api.dto.response.security.RolePermissionPermissionCodeDTO
import com.islandstudio.neon.api.entity.security.PermissionEntity
import com.islandstudio.neon.command.CommandManager
import com.islandstudio.neon.command.ICommandDispatcher
import com.islandstudio.neon.command.option.PermissionCommandOption
import com.islandstudio.neon.command.processing.CommandSyntax
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.command.properties.AccessibleCommand
import com.islandstudio.neon.command.properties.CommandAlias
import com.islandstudio.neon.command.properties.CommandFilter
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.player.security.permission.Permission
import com.islandstudio.neon.stable.player.security.role.RoleManager
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class AccessControlManager: IComponentInjector {
    private val neon by inject<Neon>()
    private val rolePermissionAdapter by inject<RolePermissionAdapter>()

    companion object: IRunner, ICommandDispatcher, IComponentInjector {
        private val permissionCommandAlias = CommandAlias.PermissionAlias

        private val accessControlManager by inject<AccessControlManager>()
        private val roleManager by inject<RoleManager>()
        private val permissionAdaptor by inject<PermissionAdaptor>()

        override fun run() {
            val serverPermissions = accessControlManager.getPermissionFromDB()
            val clientAllPermissions = Permission.getAllPermission()
            val clientMainPermissions = Permission.getAllMainPermission()

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
            val argLength = args.size.apply {
                if (this != 1) return@apply

                // TODO: GUI implementation
            }

            val accessibleCommandOptions = CommandAlias.getAccessibleCommandOptions(
                commander, accessibleCommands, permissionCommandAlias
            )

            permissionCommandAlias.onMatchOption(args[1]) {
                it?.let {
                    if (it.option !in accessibleCommandOptions) {
                        return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander, CommandSyntax.INVALID_PERMISSION)
                    }
                }

                when(it) {
                    PermissionCommandOption.Grant -> {
                        val argIndex = argLength - 1

                        if (argLength < 4) {
                            return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argIndex)
                        }

                        val roleCode = args[2].uppercase()
                        val permissionCodes = args.toMutableList().subList(3, argLength).distinctBy { x -> x.uppercase() }

                        accessControlManager.grantPermission(commander, roleCode, permissionCodes.toCollection(ArrayList()))
                    }

                    PermissionCommandOption.Revoke -> {
                        val argIndex = argLength - 1

                        if (argLength < 4) {
                            return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argIndex)
                        }

                        val roleCode = args[2].uppercase()
                        val permissionCodes = args.toMutableList().subList(3, argLength).run {
                            val tempList =  this.filter { x -> !x.equals("confirm", true) }

                            if (tempList.isEmpty()) {
                                return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Please provide permission(s) to revoke!")
                            }

                            if (!this.last().equals("confirm", true)) {
                                return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Are you sure to revoke these permission(s) from the role " +
                                        "'${ChatColor.WHITE}${roleCode}${ChatColor.RED}'? Upon revocation, player will not able to access certain feature. " +
                                        "To continue, please add '${ChatColor.WHITE}confirm${ChatColor.RED}' at the end of the command.")
                            }

                            return@run tempList.distinctBy { x -> x.uppercase() }
                        }

                        accessControlManager.revokePermission(commander, roleCode, permissionCodes.toCollection(
                            ArrayList()))
                    }

                    PermissionCommandOption.RevokeAll -> {
                        val argIndex = argLength - 1

                        if (!(argLength == 3 || argLength == 4)) {
                            return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argIndex)
                        }

                        val roleCode = args[2].uppercase()

                        if (!args.last().equals("confirm", true)) {
                            return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Are you sure to revoke all permissions from the role " +
                                    "'${ChatColor.WHITE}${roleCode}${ChatColor.RED}'? Upon revocation, player will not able to access certain feature. " +
                                    "To continue, please add '${ChatColor.WHITE}confirm${ChatColor.RED}' at the end of the command.")
                        }

                        accessControlManager.revokePermission(commander, roleCode, arrayListOf(), true)
                    }

                    else -> CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argLength - 1)
                }
            }
        }

        override fun getTabCompletion(
            commander: CommandSender,
            accessibleCommand: ArrayList<AccessibleCommand>,
            args: Array<out String>
        ): MutableList<String> {
            val argLength = args.size

            return when {
                argLength == 2 -> {
                    val argIndex = argLength - 1

                    CommandAlias.getAccessibleCommandOptions(
                        commander,
                        accessibleCommand,
                        permissionCommandAlias,
                        CommandFilter(argIndex, args[argIndex])
                    )
                }

                argLength == 3 -> {
                    val argIndex = argLength - 1

                    return permissionCommandAlias.onMatchOption(args[1]) {
                        when(it) {
                            PermissionCommandOption.Grant,
                            PermissionCommandOption.Revoke,
                            PermissionCommandOption.RevokeAll -> {
                                roleManager.getAllRole()
                                    .map { it.roleCode!! }
                                    .filter { it.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            else -> super.getTabCompletion(commander, accessibleCommand, args)
                        }
                    }
                }

                argLength >= 4 -> {
                    val argIndex = argLength - 1

                    return permissionCommandAlias.onMatchOption(args[1]) {
                        when(it) {
                            PermissionCommandOption.Grant,
                            PermissionCommandOption.Revoke -> {
                                accessControlManager.getPermissionFromDB()
                                    .map { it.permissionCode!! }
                                    .filter { it.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            else -> super.getTabCompletion(commander, accessibleCommand, args)
                        }
                    }
                }

                else -> super.getTabCompletion(commander, accessibleCommand, args)
            }
        }
    }

    fun grantPermission(commander: CommandSender, roleCode: String, permissionCodes: ArrayList<String>) {
        val role = roleManager.getRole(commander, roleCode = roleCode) ?: return

        val clientMainPermissions = Permission.getAllMainPermission()
        val clientSubPermissions = Permission.getAllSubPermission()
        val serverPermissions = getPermissionFromDB().also {
            val clientAllPermissionCodes = Permission.getAllPermission().map { x -> x.permissionCode }

            permissionCodes.filter { x -> x !in it.map { y -> y.permissionCode } || x !in clientAllPermissionCodes }.also {
                if (it.isEmpty()) return@also

                return CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Invalid permissions: " +
                        "${ChatColor.WHITE}${it.joinToString(", ")}")
            }
        }

        val grantedRolePermissions = getGrantedRolePermission(role.roleId!!).also {
            it.filter { x -> x.permissionCode in permissionCodes }.also {
                if (it.isEmpty()) return@also

                return CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.YELLOW}Permission already assigned to this role: " +
                        "${ChatColor.WHITE}${it.joinToString(", ") { x -> x.permissionCode!! }}")
            }
        }

        val pendingRolePermissions: ArrayList<GrantRolePermissionRequestDTO> = arrayListOf()

        /* Prepare main permission */
        serverPermissions
            .filter {
                x -> x.permissionCode in permissionCodes && x.permissionCode in clientMainPermissions.map { x -> x.permissionCode }
            }
            .forEach {
                pendingRolePermissions.add(GrantRolePermissionRequestDTO(roleId = role.roleId, permissionId = it.permissionId!!))
            }

        /* Prepare sub permission */
        serverPermissions
            .filter {
                x -> x.permissionCode in permissionCodes && x.permissionCode in clientSubPermissions.map { x -> x.permissionCode }
            }
            .forEach { serverPermission ->
                val subPermission = clientSubPermissions.find { x -> x.permissionCode == serverPermission.permissionCode }!!

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

        val clientMainPermissions = Permission.getAllMainPermission()
        val clientSubPermissions = Permission.getAllSubPermission()

        val grantedRolePermissions = getGrantedRolePermission(role.roleId!!).also {
            if (revokeAll) {
                if (it.isEmpty()) {
                    return CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}The target role does not have any permissions granted!")
                }

                return@also
            }

            getPermissionFromDB().also {
                val clientAllPermissionCodes = Permission.getAllPermission().map { x -> x.permissionCode }

                permissionCodes.filter { x -> x !in it.map { y -> y.permissionCode } || x !in clientAllPermissionCodes }.also {
                    if (it.isEmpty()) return@also

                    return CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Invalid permissions: " +
                            "${ChatColor.WHITE}${it.joinToString(", ")}")
                }
            }

            permissionCodes.filter { x -> x !in it.map { y -> y.permissionCode } }.also {
                if (it.isEmpty()) return@also

                return CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Invalid permission or already revoked from this role: " +
                        "${ChatColor.WHITE}${it.joinToString(", ")}")
            }
        }

        val pendingRolePermissions = grantedRolePermissions
            .filter { x ->
                if (revokeAll) return@filter true

                x.permissionCode in permissionCodes && (x.permissionCode in clientMainPermissions.map { y -> y.permissionCode }
                    || x.permissionCode in clientSubPermissions.map { y -> y.permissionCode } )
            }
            .map {
                RevokeRolePermissionRequestDTO(rolePermissionId = it.rolePermissionId)
            }

        rolePermissionAdapter.revokeRolepermission(BatchRevokeRolePermissionRequestDTO(pendingRolePermissions))
            .onSuccess {
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

    fun getGrantedParentPermission(grantedRolePermissions: ArrayList<RolePermissionPermissionCodeDTO>): ArrayList<RolePermissionPermissionCodeDTO> {
        return grantedRolePermissions.filter { permission -> permission.parentRolePermissionId == null }.toCollection(ArrayList())
    }

    fun getGrantedChildPermission(grantedRolePermissions: ArrayList<RolePermissionPermissionCodeDTO>): ArrayList<RolePermissionPermissionCodeDTO> {
        return grantedRolePermissions.filter { permission -> permission.parentRolePermissionId != null }.toCollection(ArrayList())
    }
}