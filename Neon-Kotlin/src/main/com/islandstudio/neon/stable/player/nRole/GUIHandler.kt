package com.islandstudio.neon.stable.player.nRole

import com.islandstudio.neon.stable.core.command.CommandInterfaceProcessor
import com.islandstudio.neon.stable.core.database.model.RoleWithPermissionModel
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.AccessPermission
import com.islandstudio.neon.stable.core.gui.GUISession
import com.islandstudio.neon.stable.core.gui.state.GUIStateType
import com.islandstudio.neon.stable.core.gui.structure.GUIButton
import com.islandstudio.neon.stable.core.gui.structure.GUIPageNavType
import com.islandstudio.neon.stable.player.nAccessPermission.NAccessPermission
import com.islandstudio.neon.stable.player.nAccessPermission.Permission
import com.islandstudio.neon.stable.player.nRoleAccess.NRoleAccess
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

open class GUIHandler(guiSession: GUISession): GUIBuilder(guiSession) {
    private val player: Player = guiSession.guiHolder

    override val guiState = NRoleGUIState(guiSession).apply {
        this.initStateData(GUIStateType.NROLE, NRole.getAllRoleWithPermission())
        this.initStateData(GUIStateType.NROLE_MANAGE_PERMISSION, NAccessPermission.getAllPermission())
    }

    override fun getGUIName(): String {
        return when (guiState.currentStateType()) {
            GUIStateType.NROLE -> {
                "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}${ChatColor.MAGIC}--------" +
                        "${ChatColor.BLUE}${ChatColor.BOLD}[${ChatColor.GOLD}${ChatColor.BOLD} nRole ${ChatColor.BLUE}${ChatColor.BOLD}]" +
                        "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}${ChatColor.MAGIC}--------"
            }

            GUIStateType.NROLE_MANAGE_PERMISSION -> {
                "${ChatColor.GOLD}${ChatColor.BOLD}nRole ${ChatColor.DARK_GRAY}${ChatColor.BOLD}|${ChatColor.RESET} ${guiState.getSelectedRole()!!.roleDisplayName}"
            }

            else -> {
                ""
            }
        }
    }

    override fun getGUISlots(): Int = 54

    @Suppress("UNCHECKED_CAST")
    override fun setGUIButtons() {
        val guiStateOption = guiState.currentStateOption()

        when (guiState.currentStateType()) {
            GUIStateType.NROLE -> {
                val roles = guiState.currentStateData()

                guiStateOption.updateMaxPage(roles.size)
                addNavigationButtons()

                for (i in 0 until guiStateOption.maxItemPerPage) {
                    guiStateOption.updateItemIndex(i)

                    if (guiStateOption.getItemIndex() >= roles.size) break

                    /* Role properties */
                    val role = roles[guiStateOption.getItemIndex()] as RoleWithPermissionModel
                    val roleData = hashMapOf("roleCode" to role.roleCode!!)

                    val roleDetails: LinkedList<String> = LinkedList<String>().apply {
                        this.add("${ChatColor.GRAY}Role Code: ${ChatColor.GREEN}${role.roleCode}")
                        this.add("${ChatColor.GRAY}Assigned Players: ${ChatColor.GREEN}${role.assignedPlayerCount}")
                        this.add("")
                        this.add("${ChatColor.YELLOW}Click to Manage Permisssion")
                    }

                    ROLE_BTN.clone(buttonName = role.roleDisplayName!!)
                        .initButtonMeta(roleDetails)
                        .initDataContainer(roleData as HashMap<String, Any>)
                        .createButton().run {
                            inventory.addItem(this)
                        }
                }
            }

            GUIStateType.NROLE_MANAGE_PERMISSION -> {
                val accessPermissions = guiState.currentStateData() as List<AccessPermission>
                val selectedRole = guiState.getSelectedRole()!!

                guiStateOption.updateMaxPage(accessPermissions.size)

                addNavigationButtons()

                for (i in 0 until guiStateOption.maxItemPerPage) {
                    guiStateOption.updateItemIndex(i)

                    if (guiStateOption.getItemIndex() >= accessPermissions.size) break

                    /* Permission properties */
                    val permission = accessPermissions[guiStateOption.getItemIndex()]

                    with(selectedRole) {
                        var permissionAccessType = Permission.AccessType.LIMITED
                        var isGranted = false

                        this.grantedPermissions.find {
                            it.permissionId ==  permission.permissionId
                        }?.let {
                            if (it.accessType == Permission.AccessType.FULL.toString()) {
                                permissionAccessType = Permission.AccessType.FULL
                            }

                            isGranted = true
                        }

                        val permissionDetails: LinkedList<String> = LinkedList<String>().apply {
                            this.add("${ChatColor.YELLOW}${permission.permissionDesc}")
                            this.add("")
                            this.add("${ChatColor.GRAY}Perm. Code: ${ChatColor.GREEN}${permission.permissionCode}")
                            this.add("${ChatColor.GRAY}Access Type: ${getColoredAccessType(permissionAccessType)}")
                            this.add("")
                            this.add(getColoredAccessStatus(isGranted))
                            this.add("")
                            this.add("${ChatColor.LIGHT_PURPLE}Shift L.Click: Change Access Type")
                        }

                        val permissionData = hashMapOf<String, Any>(
                            "permissionCode" to permission.permissionCode!!,
                            "accessType" to permissionAccessType.toString(),
                            "isGranted" to isGranted
                        )

                        PERMISSION_BTN.clone(buttonName = "${ChatColor.GOLD}${permission.permissionName}")
                            .initButtonMeta(permissionDetails, isGranted)
                            .initDataContainer(permissionData)
                            .createButton().run {
                                inventory.addItem(this)
                            }
                    }
                }
            }

            else -> { return }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun setGUIClickHandler(e: InventoryClickEvent) {
        val clickedItem: ItemStack = e.currentItem!!
        val clickedItemMeta: ItemMeta = clickedItem.itemMeta!!

        when (clickedItem.type) {
            /* Role Button */
            Material.NAME_TAG -> {
                if (!guiState.matchesCurrentState(GUIStateType.NROLE)) return

                if (!ROLE_BTN.matchesButtonType(clickedItemMeta)) return

                GUIButton.Handler.getDataContainer(clickedItemMeta)?.let {
                    val roleCode: String = it["roleCode"] as String

                    if (!guiState.selectRole(roleCode)) return
                } ?: return

                super.openGUI(GUIStateType.NROLE_MANAGE_PERMISSION, true)
            }

            /* Navigation Button */
            Material.SPECTRAL_ARROW -> {
                if (!GUIButton.Handler.hasButtonType(clickedItemMeta)) return

                if (guiState.matchesCurrentState(GUIStateType.NROLE_MANAGE_PERMISSION)) {
                    if (guiState.currentStateOption().resetConfirmation()) {
                        setGUIButtons()
                    }
                }

                when (GUIButton.Handler.getClickedButtonType(clickedItemMeta)) {
                    PREVIOUS_BTN.buttonType -> {
                        super.openGUI(GUIPageNavType.PREVIOUS_PAGE)
                    }

                    NEXT_BTN.buttonType -> {
                        super.openGUI(GUIPageNavType.NEXT_PAGE)
                    }
                }
            }

            /* Permission Button */
            Material.BIRCH_SIGN -> {
                if (!guiState.matchesCurrentState(GUIStateType.NROLE_MANAGE_PERMISSION)) return

                if (!PERMISSION_BTN.matchesButtonType(clickedItemMeta)) return

                if (guiState.currentStateOption().resetConfirmation()) {
                    inventory.clear()
                    setGUIButtons()
                }

                val accessPermissions = guiState.currentStateData() as List<AccessPermission>
                val selectedRole = guiState.getSelectedRole()!!
                val selectedPermissionDetail = clickedItemMeta.lore!!

                val selectedPermissionData = GUIButton.Handler.getDataContainer(clickedItemMeta) ?: return
                val selectedPermission = accessPermissions.find {
                    selectedPermissionData["permissionCode"]?.let { permissionCode ->
                        return@let it.permissionCode == (permissionCode as String)
                    } ?: return
                } ?: return

                val selectedPermissionStatus = selectedPermissionData["isGranted"]?.let { return@let it as Boolean } ?: return
                val selectedPermissionAccessType = selectedPermissionData["accessType"]?.let { return@let it as String } ?: return

                when (e.click) {
                    ClickType.LEFT -> {
                        when(selectedPermissionStatus) {
                            /* Granted -> Denied */
                            true -> {
                                GUIButton.Handler.updateIsGlint(clickedItemMeta, false)

                                selectedPermissionData["isGranted"] = false
                                selectedPermissionDetail[5] = getColoredAccessStatus(false)
                                selectedRole.updatePermissionToRevoked(selectedPermission.permissionId!!)
                            }

                            /* Denied -> Granted */
                            false -> {
                                GUIButton.Handler.updateIsGlint(clickedItemMeta, true)

                                selectedPermissionData["isGranted"] = true
                                selectedPermissionDetail[5] = getColoredAccessStatus(true)
                                selectedRole.updatePermissionToGranted(
                                    selectedPermission.permissionId!!,
                                    Permission.AccessType.valueOf(selectedPermissionAccessType)
                                )
                            }
                        }
                    }

                    ClickType.SHIFT_LEFT -> {
                        when(selectedPermissionAccessType) {
                            /* LIMITED -> FULL */
                            Permission.AccessType.LIMITED.toString() -> {
                                selectedPermissionData["accessType"] = Permission.AccessType.FULL.toString()
                                selectedPermissionDetail[3] = "${ChatColor.GRAY}Access Type: ${getColoredAccessType(
                                    Permission.AccessType.FULL)}"
                                selectedRole.updateAccessType(selectedPermission.permissionId!!, Permission.AccessType.FULL)
                            }

                            /* FULL -> LIMITED */
                            Permission.AccessType.FULL.toString() -> {
                                selectedPermissionData["accessType"] = Permission.AccessType.LIMITED.toString()
                                selectedPermissionDetail[3] = "${ChatColor.GRAY}Access Type: ${getColoredAccessType(
                                    Permission.AccessType.LIMITED)}"
                                selectedRole.updateAccessType(selectedPermission.permissionId!!, Permission.AccessType.LIMITED)
                            }
                        }
                    }

                    else -> { return }
                }

                GUIButton.Handler.updateDataContainer(clickedItemMeta, selectedPermissionData)
                clickedItemMeta.lore = selectedPermissionDetail
                clickedItem.itemMeta = clickedItemMeta
            }

            /* Multi-function Button */
            Material.SNOWBALL -> {
                if (!GUIButton.Handler.hasButtonType(clickedItemMeta)) return

                when(GUIButton.Handler.getClickedButtonType(clickedItemMeta)) {
                    BACK_BTN.buttonType -> {
                        if (!guiState.matchesCurrentState(GUIStateType.NROLE_MANAGE_PERMISSION)) return

                        guiState.deselectRole()

                        super.openGUI(GUIStateType.NROLE, false)
                    }

                    APPLY_BTN.buttonType -> {
                        if (!guiState.matchesCurrentState(GUIStateType.NROLE_MANAGE_PERMISSION)) return

                        guiState.currentStateOption().validationConfirmation().run {
                            if (this) return@run

                            GUIButton.Handler.onConfirm(clickedItemMeta)
                            inventory.clear()
                            setGUIButtons()
                            return
                        }

                        val selectedRole = guiState.getSelectedRole()!!

                        val revokedRoleAccessIds = selectedRole.revokedPermissions.map { it.roleAccessId!! }
                        //val revokedRoleIds = selectedRole.revokedPermissions.map { it.roleId!! }

                        val grantedRoleAccess = selectedRole.grantedPermissions
                            .filter { it.roleAccessId != null }
                        val newGrantedRoleAccess = selectedRole.grantedPermissions
                            .filter { it.roleAccessId == null }

                        if (selectedRole.revokedPermissions.isNotEmpty()) {
                            NRoleAccess.removeRoleAccess(revokedRoleAccessIds)
                        }

                        if (grantedRoleAccess.isNotEmpty()) {
                            NRoleAccess.updateRoleAccess(grantedRoleAccess)
                        }

                        if (newGrantedRoleAccess.isNotEmpty()) {
                            NRoleAccess.addRoleAccess(newGrantedRoleAccess)
                        }

                        CommandInterfaceProcessor.sendCommandSyntax(
                            player,
                            "${ChatColor.GREEN}Permission has been updated for ${selectedRole.roleDisplayName}${ChatColor.GREEN}!"
                        )

                        guiState.deselectRole(true)

                        super.openGUI(GUIStateType.NROLE, false)
                    }
                }
            }

            /* Close Button */
            Material.BARRIER -> {
                if (!CLOSE_BTN.matchesButtonType(clickedItemMeta)) return

                player.closeInventory()
            }

            else -> {

            }
        }
    }
}