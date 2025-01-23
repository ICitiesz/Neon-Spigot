package com.islandstudio.neon.api.service.impl

import com.islandstudio.neon.api.dto.action.ActionResult
import com.islandstudio.neon.api.dto.action.ActionStatus
import com.islandstudio.neon.api.dto.action.IActionResult
import com.islandstudio.neon.api.dto.request.player.CreatePlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.player.GetPlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.player.UpdatePlayerProfileRequestDTO
import com.islandstudio.neon.api.dto.request.security.AssignRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.UnassignRoleRequestDTO
import com.islandstudio.neon.api.entity.player.PlayerProfileEntity
import com.islandstudio.neon.api.repository.player.IPlayerProfileRepository
import com.islandstudio.neon.api.service.IPlayerProfileService
import com.islandstudio.neon.api.service.IRoleService
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.exception.NeonAPIException
import com.islandstudio.neon.shared.core.exception.NeonException
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class PlayerProfileService: IPlayerProfileService, IComponentInjector {
    private val appContext by inject<AppContext>()

    private val playerProfileRepository by inject<IPlayerProfileRepository>()
    private val roleService by inject<IRoleService>()

    override fun createPlayerProfile(request: CreatePlayerProfileRequestDTO): IActionResult<Unit> {
        if (playerProfileRepository.existsByUUID(request.playerUuid)) {
            return ActionResult(status = ActionStatus.Failure)
        }

        runCatching {
            val newPlayerProfile = PlayerProfileEntity(
                playerUuid = request.playerUuid,
                playerName = request.playerName,
            )

            playerProfileRepository.addPlayerProfile(newPlayerProfile)
        }.onFailure {
            return ActionResult(
                status = ActionStatus.Failure,
                logMessage = appContext.getFormattedCodeMessage(
                    "neon.exception.api.create_record",
                    "Player Profile",
                    "Unhandled API exception!"
                ),
                neonException = NeonAPIException(it.message, it)
            )
        }

        return ActionResult(status = ActionStatus.Success)
    }

    override fun updatePlayerProfile(request: UpdatePlayerProfileRequestDTO): IActionResult<Unit> {
        val playerProfile = playerProfileRepository.getByUUID(request.playerUUID)?.apply {
            this.playerName = request.playerName
        } ?: return ActionResult(
            status = ActionStatus.Failure,
            displayMessage = "No such player profile found!",
            logMessage = "No such player profile found!"
        )

        runCatching {
            playerProfileRepository.updatePlayerProfile(playerProfile)
        }.onFailure {
            return ActionResult(
                status = ActionStatus.Failure,
                logMessage = "Error while trying to update player profile!",
                neonException = NeonAPIException(it.message, it)
            )
        }

        return ActionResult(status = ActionStatus.Success)
    }

    override fun getPlayerProfileByUuid(request: GetPlayerProfileRequestDTO): IActionResult<PlayerProfileEntity?> {
        val playerProfile = playerProfileRepository.getByUUID(request.playerUuid!!)

        playerProfile?.let {
            return ActionResult(status = ActionStatus.Success, result = playerProfile)
        }

        return ActionResult(status = ActionStatus.Failure, displayMessage = "No such player profile found!",)
    }

    override fun getPlayerProfileByName(request: GetPlayerProfileRequestDTO): IActionResult<PlayerProfileEntity?> {
        val playerProfile = playerProfileRepository.getByPlayerName(request.playerName!!)

        playerProfile?.let {
            return ActionResult(status = ActionStatus.Success, result = playerProfile)
        }

        return ActionResult(status = ActionStatus.Failure, displayMessage = "No such player profile found!")
    }

    override fun getAllPlayerProfile(): IActionResult<List<PlayerProfileEntity>> {
        return runCatching {
            val playerProfileList = playerProfileRepository.getAll()

            ActionResult(
                result = playerProfileList,
                status = ActionStatus.Success
            )
        }.getOrElse {
            return ActionResult(
                result = listOf(),
                ActionStatus.Failure,
                logMessage = "Error while trying to get all player profile!",
                neonException = NeonAPIException(it.message, it)
            )
        }
    }

    override fun hasPlayerProfile(request: GetPlayerProfileRequestDTO): Boolean {
       return when {
            request.playerUuid != null -> {
                playerProfileRepository.existsByUUID(request.playerUuid)
            }

            !request.playerName.isNullOrEmpty() -> {
                playerProfileRepository.existsByPlayerName(request.playerName)
            }

            else -> {
                throw NeonException("Invalid request: Empty player UUID/name")
            }
        }
    }

    override fun assignRole(request: AssignRoleRequestDTO): IActionResult<Unit> {
        val playerProfile = playerProfileRepository.getByUUID(request.playerUUID)
            ?: return ActionResult(
                status = ActionStatus.Failure,
                displayMessage = "No such player profile found!"
            )

        val role = roleService.getRoleByRoleCode(request.roleCode)
            ?: return ActionResult(
                status = ActionStatus.Failure,
                displayMessage = "No such role by role code!"
            )

        playerProfile.roleId?.let {
            if (it != role.roleId!!) return@let

            return ActionResult(
                status = ActionStatus.Failure,
                displayMessage = "Player already assigned with the given role!"
            )
        }

        playerProfile.roleId = role.roleId

        runCatching {
            playerProfileRepository.updatePlayerProfile(playerProfile)
        }.onFailure {
            return ActionResult(
                status = ActionStatus.Failure,
                logMessage = "Error while trying to update player profile!",
                neonException = NeonAPIException(it.message, it)
            )
        }

        return ActionResult(status = ActionStatus.Success)
    }

    override fun unassignRole(request: UnassignRoleRequestDTO): IActionResult<Unit> {
        val playerProfile = playerProfileRepository.getByUUID(request.playerUUID)
            ?: return ActionResult(
                status = ActionStatus.Failure,
                displayMessage = "No such player profile!"
            )

        if (playerProfile.roleId == null) {
            return ActionResult(
                status = ActionStatus.Failure,
                displayMessage = "The player don't have role assigned!"
            )
        }

        playerProfile.roleId = null

        runCatching {
            playerProfileRepository.updatePlayerProfile(playerProfile)
        }.onFailure {
            return ActionResult(
                status = ActionStatus.Failure,
                logMessage = "Error while trying to update player profile!",
                neonException = NeonAPIException(it.message, it)
            )
        }

        return ActionResult(status = ActionStatus.Success)
    }
}