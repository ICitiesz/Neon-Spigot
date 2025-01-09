package com.islandstudio.neon.stable.player

import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.common.action.ActionState
import com.islandstudio.neon.stable.common.action.ActionStatus
import com.islandstudio.neon.stable.core.application.init.AppLoader
import com.islandstudio.neon.stable.core.database.repository.PlayerProfileRepository
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.PlayerProfile
import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.Role
import com.islandstudio.neon.stable.player.nRole.NRole
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.util.*

@Single
class NPlayerProfile: IComponentInjector {
    private val playerProfileRepository by inject<PlayerProfileRepository>()

    object Handler {
        fun run() = AppLoader.registerEventProcessor(EventProcessor())
    }

    private fun createPlayerProfile(player: Player) {
        if (playerProfileRepository.getByPlayerUUID(player.uniqueId) != null) return

        val playerProfile = PlayerProfile(
            player.uniqueId,
            player.name,
            LocalDateTime.now(),
            null
        )

        playerProfileRepository.addPlayerProfile(playerProfile)
    }

    fun getPlayerProfile(playerName: String): PlayerProfile? {
        return playerProfileRepository.getByPlayerName(playerName)
    }

    fun getPlayerProfile(playerUUID: UUID): PlayerProfile? {
        return playerProfileRepository.getByPlayerUUID(playerUUID)
    }

    fun getAllPlayerProfile(): List<PlayerProfile> {
        return playerProfileRepository.getAll()
    }

    fun hasPlayerProfile(playerUUID: UUID): Boolean {
        getPlayerProfile(playerUUID)?.let { return true } ?: return false
    }

    fun hasPlayerProfile(playerName: String): Boolean {
        getPlayerProfile(playerName)?.let { return true } ?: return false
    }

    fun updatePlayerProfile(playerProfile: PlayerProfile): Boolean {
        if (!hasPlayerProfile(playerProfile.playerUuid!!)) return false

        playerProfileRepository.updatePlayerProfile(playerProfile)
        return true
    }

    /**
     * Assign role to player.
     *
     * @param playerProfile The target player profile.
     * @param roleCode The role code for the specific role.
     * @return
     */
    fun assignRole(playerProfile: PlayerProfile, roleCode: String): ActionState {
        val role = NRole.getRole(roleCode)!!

        playerProfile.roleId?.let {
            if (it != role.roleId) return@let

            return ActionState(
                ActionStatus.PLAYER_ROLE_IDENTICAL,
                "${ChatColor.WHITE}${playerProfile.playerName} ${ChatColor.RED}already assigned with the target role.",
                roleCode
            )
        }

        playerProfile.roleId = role.roleId
        playerProfileRepository.updatePlayerProfile(playerProfile)
        NRole.increamentAssignedPlayers(role)

        return ActionState(ActionStatus.SUCCESS)
    }

    /**
     * Unassign role from the player.
     *
     * @param playerProfile The target player profile.
     * @return
     */
    fun unassignRole(playerProfile: PlayerProfile): ActionState {
        val role = NRole.getRole(playerProfile.roleId!!)

        playerProfile.roleId?.let {
            playerProfile.roleId = null
        } ?: return ActionState(
            ActionStatus.PLAYER_ROLE_NOT_ASSIGN,
            "${ChatColor.WHITE}${playerProfile.playerName} ${ChatColor.RED}don't have any role assigned!"
        )

        updatePlayerProfile(playerProfile)
        NRole.decreamentAssignedPlayers(role!!)

        return ActionState(ActionStatus.SUCCESS)
    }

    fun getAssignedRoleId(player: Player): Long? {
        return playerProfileRepository.getAssignedRoleId(player.uniqueId)
    }

    fun getPlayerAssignedRole(player: Player): Role? {
        return getPlayerAssignedRole(player.uniqueId)
    }

    fun getPlayerAssignedRole(playerUUID: UUID): Role? {
        return NRole.getRoleByPlayerUUID(playerUUID)
    }

    private class EventProcessor: Listener, IComponentInjector {
        private val nPlayerProfile by inject<NPlayerProfile>()

        @EventHandler
        private fun onPlayerJoin(e: PlayerJoinEvent) {
            nPlayerProfile.createPlayerProfile(e.player)
        }
    }
}