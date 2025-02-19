package com.islandstudio.neon.stable.player.security

import com.islandstudio.neon.Neon
import com.islandstudio.neon.api.adapter.security.RolePermissionAdapter
import com.islandstudio.neon.api.dto.request.security.permission.GetRolePermissionRequestDTO
import com.islandstudio.neon.api.dto.response.security.RolePermissionPermissionCodeDTO
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.command.ICommandDispatcher
import com.islandstudio.neon.stable.command.properties.AccessibleCommand
import com.islandstudio.neon.stable.command.properties.CommandAlias
import org.bukkit.command.CommandSender
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class AccessControlManager: IComponentInjector {
    private val neon by inject<Neon>()
    private val rolePermissionAdapter by inject<RolePermissionAdapter>()
    /*
    * RolePermission -> Store permission for role
    * Permission -> Store as reference
    *
    * Database Table:
    * T_ROLE_PERMISSION
    * T_PERMISSION
    *  */

    companion object: IRunner, ICommandDispatcher {
        private val permissionCommand = CommandAlias.PermissionAlias

        override fun run() {
            TODO("Not yet implemented")
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