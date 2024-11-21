package com.islandstudio.neon.stable.player.nRole

import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.database.model.RoleWithPermissionModel
import com.islandstudio.neon.stable.core.gui.GUISession
import com.islandstudio.neon.stable.core.gui.state.GUIState
import com.islandstudio.neon.stable.core.gui.state.GUIStateType
import org.koin.core.component.inject

data class NRoleGUIState(private val guiSession: GUISession): GUIState(), ModuleInjector {
    private val nRole by inject<NRole>()

    override var currentGUIStateType: GUIStateType = GUIStateType.NROLE

    private var currentSelectedRole: RoleWithPermissionModel? = null

    init {
        this.initStateOption(GUIStateType.NROLE, 45)
        this.initStateOption(GUIStateType.NROLE_MANAGE_PERMISSION, 45)
    }

    @Suppress("UNCHECKED_CAST")
    fun selectRole(roleCode: String): Boolean {
        (this.getStateData(GUIStateType.NROLE) as List<RoleWithPermissionModel>)
            .find { it.roleCode == roleCode }
            ?.let {
                currentSelectedRole = it.clone()
                return true
        }

        return false
    }

    fun deselectRole(isApply: Boolean = false) {
        this.currentSelectedRole?.let { roleWithPerm ->
            @Suppress("UNCHECKED_CAST")
            if (isApply) {
                (getStateData(GUIStateType.NROLE) as MutableList<RoleWithPermissionModel>).apply {
                    this.find { it.roleId == roleWithPerm.roleId }?.let { oldRecord ->
                        NRole.getRoleWithPermission(roleWithPerm.roleId!!)?.let { newRecord ->
                            this[this.indexOf(oldRecord)] = newRecord
                        }
                    }
                }
            }

            this.currentSelectedRole = null
        }
    }

    fun getSelectedRole(): RoleWithPermissionModel? = currentSelectedRole

}
