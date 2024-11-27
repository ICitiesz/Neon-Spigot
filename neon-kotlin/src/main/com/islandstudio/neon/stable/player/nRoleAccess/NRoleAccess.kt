package com.islandstudio.neon.stable.player.nRoleAccess

import com.islandstudio.neon.stable.common.action.ActionState
import com.islandstudio.neon.stable.common.action.ActionStatus
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.database.repository.RoleAccessRepository
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.AccessPermission
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.RoleAccess
import com.islandstudio.neon.stable.player.NPlayerProfile
import com.islandstudio.neon.stable.player.nAccessPermission.NAccessPermission
import com.islandstudio.neon.stable.player.nAccessPermission.Permission
import com.islandstudio.neon.stable.player.nRole.NRole
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject
import java.util.logging.Logger

object NRoleAccess: ModuleInjector {
    private val neonLogger by inject<Logger>()
    private val roleAccessReposittory by inject<RoleAccessRepository>()

    /**
     * Add role access permission.
     *
     * @param roleAccess
     * @return
     */
    fun addRoleAccess(roleAccess: RoleAccess): ActionState {
        return addRoleAccess(listOf(roleAccess))
    }

    /**
     * Add role access permission in batch.
     *
     * @param roleAccessList
     * @return
     */
    fun addRoleAccess(roleAccessList: List<RoleAccess>): ActionState {
        if (roleAccessList.isEmpty()) return ActionState(ActionStatus.EMPTY_COLLECTION)

        /* Validate duplication */
        roleAccessList.find { roleAccess ->
            val permission = Permission.valueOfPermissionCode(
                NAccessPermission.getAccessPermission(roleAccess.permissionId!!)?.permissionCode!!
            )!!

            hasRoleAccess(roleAccess.roleId!!, permission)
        }?.let {
            val permission = Permission.valueOfPermissionCode(
                NAccessPermission.getAccessPermission(it.permissionId!!)?.permissionCode!!
            )!!

            return ActionState(
                ActionStatus.DUPLICATE_RECORD,
                "${ChatColor.RED}Permission ${ChatColor.WHITE}'${ChatColor.GOLD}" +
                        "${permission.permissionCode}${ChatColor.WHITE}' ${ChatColor.RED}already exist for role code " +
                        "${ChatColor.WHITE}'${ChatColor.GOLD}${NRole.getRole(it.roleId!!)!!.roleCode}${ChatColor.WHITE}'${ChatColor.RED}!",
                permission
            )
        } ?: roleAccessList.forEach {
            roleAccessReposittory.addRoleAccess(it)
        }

        return ActionState.success()
    }

    /**
     * Get role access permission by roleId and permission.
     *
     * @param roleId
     * @param permission
     * @return
     */
    fun getRoleAccess(roleId: Long, permission: Permission): RoleAccess? {
        val accessPermission: AccessPermission = NAccessPermission.getAccessPermission(permission.permissionCode) ?: return null

        return roleAccessReposittory.getByPermission(roleId, accessPermission.permissionId!!, null)
    }

    /**
     * Get assigned role access
     *
     * @param player
     * @param permission
     * @return
     */
    fun getAssignedRoleAccess(player: Player, permission: Permission): RoleAccess? {
        NPlayerProfile.getPlayerAssignedRole(player)?.let {
            return getRoleAccess(it.roleId!!, permission)
        } ?: return null
    }

    fun getCommandSenderRoleAccess(commander: CommandSender, permission: Permission?): RoleAccess? {
        when(commander) {
            is ConsoleCommandSender -> {
                return null
            }

            is Player -> {
                permission?.let {
                    return getAssignedRoleAccess(commander, it)
                }

                return null
            }
        }

        return null
    }

    fun getAssignedRoleAccess(player: Player): List<RoleAccess> {
        NPlayerProfile.getPlayerAssignedRole(player)?.let {
            return getRoleAccess(it.roleId!!)
        } ?: return listOf()
    }

    /**
     * Update role access
     *
     * @param roleAccess
     * @return
     */
    fun updateRoleAccess(roleAccess: RoleAccess): ActionState {
        return updateRoleAccess(listOf(roleAccess))
    }

    /**
     * Update Role Access in batch.
     *
     * @param roleAccessList
     */
    fun updateRoleAccess(roleAccessList: List<RoleAccess>): ActionState {
        if (roleAccessList.isEmpty()) return ActionState(ActionStatus.EMPTY_COLLECTION)

        roleAccessList.forEach { roleAccess ->
            roleAccessReposittory.updateRoleAccess(roleAccess)
        }

        return ActionState.success()
    }

    /**
     * Remove role access
     *
     * @param roleAccessId
     * @param roleId
     * @return
     */
    fun removeRoleAccess(roleAccessId: Long): ActionState {
        return removeRoleAccess(listOf(roleAccessId))
    }

    /**
     * Remove role access in batch
     *
     * @param roleAccessIds
     * @param roleIds
     * @return
     */
    fun removeRoleAccess(roleAccessIds: List<Long>): ActionState {
        if (roleAccessIds.isEmpty()) return ActionState(ActionStatus.EMPTY_COLLECTION)

        roleAccessIds.forEach { roleAccessId ->
            roleAccessReposittory.removeByRoleAccessId(roleAccessId)
        }

        return ActionState.success()
    }

    fun hasRoleAccess(roleId: Long, permission: Permission, accessType: Permission.AccessType? = null): Boolean {
        val accessPermission = NAccessPermission.getAccessPermission(permission.permissionCode) ?: return false

        roleAccessReposittory.getByPermission(roleId, accessPermission.permissionId!!, accessType)?.let {
            return true
        }

        return false
    }

    /* Get all role access by given roleId */
    fun getRoleAccess(roleId: Long): List<RoleAccess> {
        return roleAccessReposittory.getAllByRoleId(roleId)
    }

    fun getRoleAccessList(): List<RoleAccess> {
        return roleAccessReposittory.getAll()
    }
}