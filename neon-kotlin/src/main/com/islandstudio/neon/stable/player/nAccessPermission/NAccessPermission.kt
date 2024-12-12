package com.islandstudio.neon.stable.player.nAccessPermission

import com.islandstudio.neon.stable.core.application.di.IComponentInjector
import com.islandstudio.neon.stable.core.command.CommandDispatcher
import com.islandstudio.neon.stable.core.command.CommandInterfaceProcessor
import com.islandstudio.neon.stable.core.command.properties.CommandAlias
import com.islandstudio.neon.stable.core.command.properties.CommandArgument
import com.islandstudio.neon.stable.core.command.properties.CommandSyntax
import com.islandstudio.neon.stable.core.database.repository.AccessPermissionRepository
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.AccessPermission
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.RoleAccess
import com.islandstudio.neon.stable.player.nRole.NRole
import com.islandstudio.neon.stable.player.nRoleAccess.NRoleAccess
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

object NAccessPermission: IComponentInjector {
    private val accessPermissionRepository by inject<AccessPermissionRepository>()

    object Handler: CommandDispatcher {
        fun run() {
            initAccessPermission()
        }

        override fun getCommandDispatcher(commander: CommandSender, args: Array<out String>) {
            val command = CommandAlias.NACCESS_PERMISSION.command
            val roleAccess = NRoleAccess.getCommandSenderRoleAccess(commander, command.permission).also {
                if (!command.isCommandAccessible(commander, it)) {
                    return CommandInterfaceProcessor.notifyInvalidCommand(commander, args[0])
                }
            }
            val argLength = args.size

            if (argLength == 1) {
                if (commander !is Player) {
                    return CommandInterfaceProcessor.sendCommandSyntax(commander, CommandSyntax.UNSUPPORTED_GUI_ACCESS)
                }

                // TODO: nAccessPermission GUI
                return
            }

            command.getCommandArgument(args[1])?.let { commandArg ->
                if (!command.isArgumentAccessible(commander, commandArg, roleAccess)) {
                    return CommandInterfaceProcessor.sendCommandSyntax(commander, CommandSyntax.INVALID_PERMISSION)
                }

                when(commandArg) {
                    /* Grant permission to role */
                    CommandArgument.GRANT -> {
                        if (argLength != 5) {
                            return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                        }

                        val permission = with(args[2].uppercase()) {
                            getAccessPermission(this)?.let {
                                return@with it
                            }

                            return CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.RED}No such permission as ${ChatColor.WHITE}" +
                                        "'${ChatColor.GOLD}${this}${ChatColor.WHITE}'${ChatColor.RED}!"
                            )
                        }

                        val accessType = with(args[3]) {
                            Permission.AccessType.valueOfAccessType(this)?.let {
                                return@with it
                            }

                            return CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.RED}No such access type as ${ChatColor.WHITE}" +
                                        "'${ChatColor.GOLD}${this}${ChatColor.WHITE}'${ChatColor.RED}!"
                            )
                        }

                        val role = with(args[4].uppercase()) {
                            /* Validate format */
                            NRole.Handler.validateRoleCode(this).also {
                                if (it.isSuccess()) return@also

                                return CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                            }

                            NRole.getRole(this)?.let {
                                return@with it
                            }

                            return CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.RED}No such role code as ${ChatColor.WHITE}" +
                                        "'${ChatColor.GOLD}${this}${ChatColor.WHITE}'${ChatColor.RED}!"
                            )
                        }

                        val newRoleAccess = RoleAccess(
                            roleId = role.roleId,
                            permissionId = permission.permissionId,
                            accessType = accessType.toString()
                        )

                        NRoleAccess.addRoleAccess(newRoleAccess).also {
                            if (it.isSuccess()) {
                                return CommandInterfaceProcessor.sendCommandSyntax(
                                    commander,
                                    "${ChatColor.GREEN}Permission has been updated for ${ChatColor.RESET}${role.roleDisplayName}${ChatColor.GREEN}!"
                                )
                            }

                            return CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                        }
                    }

                    /* Role permission revocation */
                    CommandArgument.REVOKE -> {
                        /* Arg length validation */
                        if (!(argLength == 4 || argLength == 5)) {
                            return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                        }

                        val permission = with(args[2].uppercase()) {
                            Permission.valueOfPermissionCode(this)?.let {
                                return@with it
                            }

                            return CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.RED}No such permission as ${ChatColor.WHITE}" +
                                        "'${ChatColor.GOLD}${this}${ChatColor.WHITE}'${ChatColor.RED}!"
                            )
                        }

                        val role = with(args[3].uppercase()) {
                            /* Validate format */
                            NRole.Handler.validateRoleCode(this).also {
                                if (it.isSuccess()) return@also

                                return CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                            }


                            NRole.getRole(this)?.let {
                                return@with it
                            }

                            return CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.RED}No such role code as ${ChatColor.WHITE}" +
                                        "'${ChatColor.GOLD}${this}${ChatColor.WHITE}'${ChatColor.RED}!"
                            )
                        }

                        if (argLength == 4) {
                            CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.YELLOW}Are you sure to revoke permission '${ChatColor.WHITE}${permission.permissionCode}" +
                                        "${ChatColor.YELLOW}' from the role '${ChatColor.WHITE}${role.roleCode}${ChatColor.YELLOW}'? The role will not able to access to certain features " +
                                        "based on the revoked permission."
                            )

                            CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.YELLOW}Please retype the command with '${ChatColor.WHITE}CONFIRM${ChatColor.YELLOW}' " +
                                        "at the end to proceed."
                            )

                            return
                        }

                        /* Validation confirmation */
                        if (!CommandInterfaceProcessor.hasConfirmation(args[4])) {
                            return CommandInterfaceProcessor.sendCommandSyntax(commander, CommandSyntax.INVALID_CONFIRMATION)
                        }

                        NRoleAccess.getRoleAccess(role.roleId!!, permission)?.let { roleAccess ->
                            NRoleAccess.removeRoleAccess(roleAccess.roleAccessId!!).also {
                                if (it.isSuccess()) {
                                    return CommandInterfaceProcessor.sendCommandSyntax(
                                        commander,
                                        "${ChatColor.GREEN}Permission ${ChatColor.GOLD}${permission.permissionCode} " +
                                                "${ChatColor.GREEN}has been revoked from role ${ChatColor.GOLD}${role.roleCode}${ChatColor.GREEN}!"
                                    )
                                }

                                CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                            }
                        }
                    }

                    /* Update access type of role permission */
                    CommandArgument.UPDATE_ACCESS_TYPE -> {
                        if (argLength != 5) {
                            return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                        }

                        val accessType = with(args[2]) {
                            Permission.AccessType.entries.find { this.uppercase() == it.toString() }?.let {
                                return@with it
                            }

                            return CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.RED}No such access type as ${ChatColor.WHITE}" +
                                        "'${ChatColor.GOLD}${this}${ChatColor.WHITE}'${ChatColor.RED}!"
                            )
                        }

                        val permission = with(args[3].uppercase()) {
                            Permission.valueOfPermissionCode(this)?.let {
                                return@with it
                            }

                            return CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.RED}No such permission as ${ChatColor.WHITE}" +
                                        "'${ChatColor.GOLD}${this}${ChatColor.WHITE}'${ChatColor.RED}!"
                            )
                        }

                        val role = with(args[4].uppercase()) {
                            /* Validate format */
                            NRole.Handler.validateRoleCode(this).also {
                                if (it.isSuccess()) return@also

                                return CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                            }

                            NRole.getRole(this)?.let {
                                return@with it
                            }

                            return CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.RED}No such role code as ${ChatColor.WHITE}" +
                                        "'${ChatColor.GOLD}${this}${ChatColor.WHITE}'${ChatColor.RED}!"
                            )
                        }

                        NRoleAccess.getRoleAccess(role.roleId!!, permission)?.let { roleAccess ->
                            roleAccess.accessType = accessType.toString()

                            NRoleAccess.updateRoleAccess(roleAccess).also {
                                if (it.isSuccess()) {
                                    return CommandInterfaceProcessor.sendCommandSyntax(
                                        commander,
                                        "${ChatColor.GREEN}Access type for permission, ${ChatColor.GOLD}${permission.permissionCode} " +
                                                "${ChatColor.GREEN}has been updated!"
                                    )
                                }

                                return CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                            }
                        }

                        return CommandInterfaceProcessor.sendCommandSyntax(commander, "${ChatColor.RED}No such permission assigned to this role!")
                    }

                    else -> {
                        return CommandInterfaceProcessor.notifyInvalidArgument(commander, args, 1)
                    }
                }
            } ?: return CommandInterfaceProcessor.notifyInvalidArgument(commander, args, 1)
        }

        override fun getTabCompletion(commander: CommandSender, args: Array<out String>): MutableList<String> {
            val command = CommandAlias.NACCESS_PERMISSION.command
            val roleAccess = NRoleAccess.getCommandSenderRoleAccess(commander, command.permission)

            val roles = NRole.getAllRole()
            val accessPermissionList = getAllPermission()

            when (val argLength = args.size) {
                2 -> {
                    return command.getCommandArgument(commander, argLength - 1, args[1], roleAccess = roleAccess)
                }

                3 -> {
                    val argIndex = argLength - 1

                    command.getAllCommandArgument().find { it.argName.equals(args[1], true) }?.let {
                        if (!command.isArgumentAccessible(commander, it, roleAccess)) {
                            return super.getTabCompletion(commander, args)
                        }

                        return when (it) {
                            CommandArgument.GRANT, CommandArgument.REVOKE -> {
                                accessPermissionList
                                    .map { permission -> permission.permissionCode!! }
                                    .filter { permissionCode -> permissionCode.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            CommandArgument.UPDATE_ACCESS_TYPE -> {
                                Permission.AccessType.entries
                                    .map { at -> at.toString() }
                                    .filter { at -> at.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            else -> {
                                super.getTabCompletion(commander, args)
                            }
                        }
                    }
                }

                4 -> {
                    val argIndex = argLength - 1

                    command.getAllCommandArgument().find { it.argName.equals(args[1], true) }?.let {
                        if (!command.isArgumentAccessible(commander, it, roleAccess)) {
                            return super.getTabCompletion(commander, args)
                        }

                        return when(it) {
                            CommandArgument.GRANT -> {
                                Permission.AccessType.entries
                                    .map { at -> at.toString() }
                                    .filter { at -> at.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            CommandArgument.REVOKE -> {
                                roles
                                    .map { role -> role.roleCode!! }
                                    .filter { roleCode -> roleCode.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            CommandArgument.UPDATE_ACCESS_TYPE -> {
                                accessPermissionList
                                    .map { permission -> permission.permissionCode!! }
                                    .filter { permissionCode -> permissionCode.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            else -> {
                                super.getTabCompletion(commander, args)
                            }
                        }
                    }
                }

                5 -> {
                    val argIndex = argLength - 1

                    command.getAllCommandArgument().find { it.argName.equals(args[1], true) }?.let {
                        if (!command.isArgumentAccessible(commander, it, roleAccess)) {
                            return super.getTabCompletion(commander, args)
                        }

                        return when(it) {
                            CommandArgument.GRANT -> {
                                roles
                                    .map { role -> role.roleCode!! }
                                    .filter { roleCode -> roleCode.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            CommandArgument.UPDATE_ACCESS_TYPE -> {
                                roles
                                    .map { role -> role.roleCode!! }
                                    .filter { roleCode -> roleCode.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            else -> {
                                super.getTabCompletion(commander, args)
                            }
                        }
                    }
                }
            }

            return super.getTabCompletion(commander, args)
        }
    }

    private fun initAccessPermission() {
        Permission.entries.forEach {
            if (accessPermissionRepository.isExistByPermissionCode(it.permissionCode)) return@forEach

            val accessPermission = AccessPermission(
                permissionName = it.permissionName,
                permissionCode = it.permissionCode,
                permissionDesc = it.permissionDesc
            )

            accessPermissionRepository.addAccessPermission(accessPermission)
        }
    }

    fun getAccessPermission(permissionId: Long): AccessPermission? {
        return accessPermissionRepository.getAccessPermissionById(permissionId)
    }

    fun getAccessPermission(permissionCode: String): AccessPermission? {
        return accessPermissionRepository.getAccessPermissionByPermissionCode(permissionCode)
    }

    fun getAllPermission(): List<AccessPermission> {
        return accessPermissionRepository.getAllAccessPermission()
    }

    fun getAssignedRoleAllPermission(player: Player): List<AccessPermission> {
        with(NRoleAccess.getAssignedRoleAccess(player)) {
            val permissionIds = this.map { it.permissionId!! }

            return accessPermissionRepository.getAccessPermissionListByIdList(permissionIds)
        }
    }

    fun hasPermission(permissionCode: String): Boolean {
        return getAccessPermission(permissionCode) != null
    }
}