package com.islandstudio.neon.stable.player.nRole

import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.command.CommandDispatcher
import com.islandstudio.neon.stable.core.command.CommandInterfaceProcessor
import com.islandstudio.neon.stable.core.command.properties.CommandAlias
import com.islandstudio.neon.stable.core.command.properties.CommandArgument
import com.islandstudio.neon.stable.core.command.properties.CommandSyntax
import com.islandstudio.neon.stable.core.common.action.ActionState
import com.islandstudio.neon.stable.core.common.action.ActionStatus
import com.islandstudio.neon.stable.core.database.model.RoleWithPermissionModel
import com.islandstudio.neon.stable.core.database.repository.RoleRepository
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.Role
import com.islandstudio.neon.stable.core.gui.NGUI
import com.islandstudio.neon.stable.player.NPlayerProfile
import com.islandstudio.neon.stable.player.nRoleAccess.NRoleAccess
import com.islandstudio.neon.stable.utils.processing.GeneralInputProcessor
import com.islandstudio.neon.stable.utils.processing.TextProcessor
import com.islandstudio.neon.stable.utils.processing.properties.DataTypes
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.koin.core.component.inject
import java.util.*

object NRole: ModuleInjector {
    private const val MIN_INPUT_LENGTH = 3
    private const val MAX_INPUT_LENGTH = 64

    private val roleRepository by inject<RoleRepository>()

    object Handler: CommandDispatcher {


        fun run() {
            //NConstructor.registerEventProcessor(EventProcessor())
        }

        override fun getCommandDispatcher(commander: CommandSender, args: Array<out String>) {
            val command = CommandAlias.NROLE.command
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

                return NGUI.initGUISession(commander, GUIHandler::class.java).getGUIHandler().openGUI()
            }

            /* Process execution for each of the commandArg */
            command.getCommandArgument(args[1])?.let { commandArg ->
                if (!command.isArgumentAccessible(commander, commandArg, roleAccess)) {
                    return CommandInterfaceProcessor.notifyInvalidCommand(commander, args[0])
                }

                when(commandArg) {
                    /* Create new role */
                    CommandArgument.CREATE -> {
                        if (argLength != 5) {
                            return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                        }

                        val roleDisplayName = with(args[2]) {
                            /* Role display name format validation */
                            validateRoleDisplayName(this).also {
                                if (it.isSuccess()) return@also

                                return CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                            }

                            /* Let player choose whether replace spaces for role display name */
                            val replaceSpaces = with(args[3].lowercase()) isReplaceSpaces@ {
                                if (!GeneralInputProcessor.validateDataType(this, DataTypes.BOOLEAN)) {
                                    return CommandInterfaceProcessor.sendCommandSyntax(
                                        commander,
                                        "Invalid data type!",
                                    )
                                }

                                return@isReplaceSpaces this.lowercase().toBoolean()
                            }

                            /* Replace '_' within role display name with space if player define true */
                            if (replaceSpaces) return@with this.replace('_', ' ')

                            return@with this
                        }

                        val roleCode = with(args[4].uppercase()) {
                            /* Role code format validation */
                            validateRoleCode(this).also {
                                if (!it.isSuccess()) {
                                    return CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                                }
                            }

                            return@with this
                        }

                        createRole(roleDisplayName, roleCode).also {
                            if (!it.isSuccess()) {
                                return CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                            }

                            CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.GREEN}New role '${ChatColor.RESET}${TextProcessor.processColorText(roleDisplayName)}" +
                                        "${ChatColor.GREEN}' has been created!")
                        }
                    }

                    /* Remove role */
                    CommandArgument.REMOVE -> {
                        if (!(argLength == 3 || argLength == 4)) {
                            return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                        }

                        val roleCode = with(args[2].uppercase()) {
                            /* Role code format validation */
                            validateRoleCode(this).also {
                                if (!it.isSuccess()) {
                                    return CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                                }
                            }

                            return@with this
                        }

                        if (argLength == 3) {
                            CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.YELLOW}Are you sure to remove role by role code '${ChatColor.WHITE}${roleCode}${ChatColor.YELLOW}'? " +
                                        "Players who already assigned by this role will be unassigned, as well as the associated permissions will be revoked."
                            )

                            CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.YELLOW}Please retype the command with '${ChatColor.WHITE}CONFIRM${ChatColor.YELLOW}' " +
                                        "at the end to proceed."
                            )

                            return
                        }

                        /* Validation confirmation */
                        if (!CommandInterfaceProcessor.hasCommandArgument(args[3], CommandArgument.CONFIRM, false)) {
                            return CommandInterfaceProcessor.sendCommandSyntax(commander, CommandSyntax.INVALID_CONFIRMATION)
                        }

                        removeRole(roleCode).also {
                            return CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                        }
                    }

                    /* Assign role to player */
                    CommandArgument.ASSIGN -> {
                        if (argLength != 4) {
                            return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                        }

                        val targetPlayerProfile = with(args[2]) {
                            return@with NPlayerProfile.getPlayerProfile(this)
                                ?: return CommandInterfaceProcessor.sendCommandSyntax(commander, "No profile!")
                        }

                        val roleCode = with(args[3].uppercase()) {
                            validateRoleCode(this).also {
                                if (!it.isSuccess()) {
                                    return CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                                }
                            }

                            if (!hasRole(this)) {
                                return CommandInterfaceProcessor.sendCommandSyntax(
                                    commander,
                                    "${ChatColor.RED}No such role code as ${ChatColor.WHITE}" +
                                            "'${ChatColor.GOLD}${this}${ChatColor.WHITE}'${ChatColor.RED}!"
                                )
                            }

                            return@with this
                        }

                        NPlayerProfile.assignRole(targetPlayerProfile, roleCode).also {
                            if (it.isSuccess()) {
                                return CommandInterfaceProcessor.sendCommandSyntax(
                                    commander,
                                    "${ChatColor.WHITE}${targetPlayerProfile.playerName} " +
                                            "${ChatColor.GREEN}has been assigned to ${ChatColor.RESET}${getRole(roleCode)!!.roleDisplayName} ${ChatColor.GREEN}role!"
                                )
                            }

                            return CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                        }
                    }

                    /* Uassign role from player */
                    CommandArgument.UNASSIGN -> {
                        if (!(argLength == 3 || argLength == 4)) {
                            return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                        }

                        val targetPlayerProfile = with(args[2]) {
                            return@with NPlayerProfile.getPlayerProfile(this)
                                ?: return CommandInterfaceProcessor.sendCommandSyntax(commander, "No profile!")
                        }

                        val role = targetPlayerProfile.roleId?.let role@ {
                            return@role getRole(it)
                        } ?: return CommandInterfaceProcessor.sendCommandSyntax(
                            commander,
                            "${ChatColor.WHITE}${targetPlayerProfile.playerName} ${ChatColor.RED}don't have any role assigned!"
                        )

                        if (argLength == 3) {
                            CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.YELLOW}Are you sure to unassign role '${ChatColor.WHITE}${role.roleCode}${ChatColor.YELLOW}' from " +
                                        "${ChatColor.WHITE}${targetPlayerProfile.playerName}${ChatColor.YELLOW}? Players with the role will be restricted to " +
                                        "access certain features based on the granted permission to the role."
                            )

                            CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.YELLOW}Please retype the command with '${ChatColor.WHITE}CONFIRM${ChatColor.YELLOW}' " +
                                        "at the end to proceed."
                            )

                            return
                        }

                        /* Validate confirmation */
                        if (!CommandInterfaceProcessor.hasCommandArgument(args[3], CommandArgument.CONFIRM, false)) {
                            return CommandInterfaceProcessor.sendCommandSyntax(commander, CommandSyntax.INVALID_CONFIRMATION)
                        }

                        NPlayerProfile.unassignRole(targetPlayerProfile).also {
                            if (it.isSuccess()) {
                                return CommandInterfaceProcessor.sendCommandSyntax(
                                    commander,
                                    "${role.roleDisplayName} ${ChatColor.GREEN}role has been unassigned " +
                                            "from ${ChatColor.WHITE}${targetPlayerProfile.playerName}${ChatColor.GREEN}!"
                                )
                            }

                            return CommandInterfaceProcessor.sendCommandSyntax(commander, it.actionStatusMessage)
                        }
                    }

                    else -> {
                        return CommandInterfaceProcessor.notifyInvalidArgument(commander, args, 1)
                    }
                }
            } ?: return CommandInterfaceProcessor.notifyInvalidArgument(commander, args, 1)
        }

        override fun getTabCompletion(commander: CommandSender, args: Array<out String>): MutableList<String> {
            val command = CommandAlias.NROLE.command
            val roleAccess = NRoleAccess.getCommandSenderRoleAccess(commander, command.permission)

            val roles = getAllRole()
            val playerProfiles by lazy { NPlayerProfile.getAllPlayerProfile() }

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
                            CommandArgument.REMOVE -> {
                                roles
                                    .map { role -> role.roleCode!! }
                                    .filter { roleCode -> roleCode.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            CommandArgument.ASSIGN, CommandArgument.UNASSIGN -> {
                                playerProfiles
                                    .map { profile -> profile.playerName!! }
                                    .filter { profile -> profile.startsWith(args[argIndex]) }
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
                            CommandArgument.CREATE -> {
                                listOf("true", "false")
                                    .filter { boolValue -> boolValue.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            CommandArgument.ASSIGN -> {
                                roles
                                    .map { role -> role.roleCode!! }
                                    .filter { roleCode -> roleCode.startsWith(args[argIndex], true) }
                                    .toMutableList()
                            }

                            CommandArgument.REMOVE, CommandArgument.UNASSIGN -> {
                                super.getTabCompletion(commander, args)
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

        /**
         * Perform validation on role display name.
         *
         * @param roleDisplayName Given display name.
         * @return Action state which contains action status based on what condition met.
         */
        private fun validateRoleDisplayName(roleDisplayName: String): ActionState {
            if (roleDisplayName.isEmpty() || roleDisplayName.length < MIN_INPUT_LENGTH || roleDisplayName.length > MAX_INPUT_LENGTH) {
                return ActionState(
                    ActionStatus.INVALID_STRING_LENGTH,
                    "${ChatColor.RED}Invalid role display name! " +
                            "Role display name must have minimum 3 characters and maximum 64 characters!"
                )
            }

            return ActionState.success()
        }

        /**
         * Perform validation on role code.
         *
         * @param roleCode Given role code.
         * @return Action state which contains action status based on what condition met.
         */
        fun validateRoleCode(roleCode: String): ActionState {
            val roleCodePattern = "[A-Z0-9_]+".toRegex()

            when {
                (roleCode.isEmpty() || roleCode.length < MIN_INPUT_LENGTH || roleCode.length > MAX_INPUT_LENGTH) -> {
                    return ActionState(
                        ActionStatus.INVALID_STRING_LENGTH,
                        "${ChatColor.RED}Role code must have minimum 3 characters and maximum 64 characters!",
                        roleCode)
                }


                !roleCode.matches(roleCodePattern) -> {
                    return ActionState(
                        ActionStatus.INVALID_STRING_PATTERN,
                        "${ChatColor.RED}Role code can only be a combination of uppercase letters, numbers, and underscores."
                    )
                }
            }

            return ActionState.success()
        }
    }

    /**
     * Create new role if not exist by given role display name and role code as secondary identifier.
     *
     * @param roleDisplayName The role display name which will display beside player display as well as the chat.
     * @param roleCode The role code which use as secondary identifier.
     * @return [ActionState]
     */
    fun createRole(roleDisplayName: String, roleCode: String): ActionState {
        val coloredRoleDisplayName = TextProcessor.processColorText(roleDisplayName)

        if (hasRole(roleCode)) {
            return ActionState(
                ActionStatus.DUPLICATE_RECORD,
                "${ChatColor.RED}Failed to create new role " +
                        "'${ChatColor.RESET}${TextProcessor.processColorText(roleDisplayName)}${ChatColor.RED}${ChatColor.RED}'" +
                        " due to the role already exists!",
                roleCode
            )
        }

        with(Role(roleDisplayName = coloredRoleDisplayName, roleCode = roleCode)) {
            roleRepository.addRole(this)
        }

        return ActionState.success()
    }

    /**
     * Remove role by given role code.
     *
     * @param roleCode Role secondary identifier
     * @return
     */
    fun removeRole(roleCode: String): ActionState {
        if (!hasRole(roleCode)) {
            return ActionState(
                ActionStatus.RECORD_NOT_EXIST,
                "${ChatColor.RED}Failed to remove role by given role code " +
                        "'${ChatColor.GOLD}${roleCode}${ChatColor.RED}' due to not exist!"
            )
        }

        roleRepository.removeByRoleCode(roleCode)

        return ActionState.success(
            "${ChatColor.GREEN}Role by role code '${ChatColor.GOLD}${roleCode}${ChatColor.GREEN}' has been removed!"
        )
    }

    fun hasRole(roleId: Long): Boolean {
        return roleRepository.isExistByRoleId(roleId)
    }

    fun hasRole(roleCode: String): Boolean {
        return roleRepository.isExistByRoleCode(roleCode)
    }

    fun increamentAssignedPlayers(role: Role) {
        if (!hasRole(role.roleCode!!)) return

        role.assignedPlayerCount = role.assignedPlayerCount!! + 1

        roleRepository.updateRole(role)
    }

    fun decreamentAssignedPlayers(role: Role) {
        if (!hasRole(role.roleCode!!)) return

        role.assignedPlayerCount?.let {
            if (it <= 0) return

            role.assignedPlayerCount = it - 1

            roleRepository.updateRole(role)
        }
    }

    fun getAllRole(): List<Role> {
       return roleRepository.getAll()
    }

    fun getAllRoleWithPermission(): List<RoleWithPermissionModel> {
        val rolesWithPermissionsList: LinkedList<RoleWithPermissionModel> = LinkedList()
        val roles = roleRepository.getAll()
        val roleAccess = NRoleAccess.getRoleAccessList()

        roles.forEach { role ->
            val roleWithPermission = RoleWithPermissionModel(
                role.roleId,
                role.roleDisplayName,
                role.roleCode,
                role.assignedPlayerCount!!,
                roleAccess.filter { it.roleId == role.roleId }.toCollection(ArrayList())
            )

            rolesWithPermissionsList.add(roleWithPermission)
        }

        return rolesWithPermissionsList
    }

    fun getRoleWithPermission(roleId: Long): RoleWithPermissionModel? {
        val role = roleRepository.getByRoleId(roleId) ?: return null
        val roleAccess = NRoleAccess.getRoleAccess(roleId)

        val roleWithPermission = RoleWithPermissionModel(
            role.roleId,
            role.roleDisplayName,
            role.roleCode,
            role.assignedPlayerCount!!,
            roleAccess.toCollection(ArrayList())
        )

        return roleWithPermission
    }

    /**
     * Get role by roleId.
     *
     * @param roleId
     * @return
     */
    fun getRole(roleId: Long): Role?  {
        return roleRepository.getByRoleId(roleId)
    }

    /**
     * Get role by roleCode.
     *
     * @param roleCode
     * @return
     */
    fun getRole(roleCode: String): Role? {
        return roleRepository.getByRoleCode(roleCode)
    }

    fun getRoleByPlayerUUID(playerUUID: UUID): Role? {
        return roleRepository.getPlayerRoleByPlayerUUID(playerUUID)
    }

    private class EventProcessor: Listener {
    }
}