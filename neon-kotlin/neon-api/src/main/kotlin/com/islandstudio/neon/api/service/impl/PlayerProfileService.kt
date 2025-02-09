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
import com.islandstudio.neon.api.repository.security.IRoleRepository
import com.islandstudio.neon.api.service.IPlayerProfileService
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.exception.NeonAPIException
import com.islandstudio.neon.shared.core.exception.NeonException
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.ZoneOffset

@Single
class PlayerProfileService: IPlayerProfileService, IComponentInjector {
    private val appContext by inject<AppContext>()

    private val playerProfileRepository by inject<IPlayerProfileRepository>()
    private val roleRepository by inject<IRoleRepository>()

    override fun createPlayerProfile(request: CreatePlayerProfileRequestDTO): IActionResult<PlayerProfileEntity?> {
        val actionResult = ActionResult<PlayerProfileEntity?>()

        runCatching {
            /* Check if the player exists */
            if (playerProfileRepository.existsByUUID(request.playerUuid)) {
                return actionResult
                    .withStatus(ActionStatus.PLAYER_PROFILE_EXIST)
            }

            val playerProfile = PlayerProfileEntity(
                playerUuid = request.playerUuid,
                playerName = request.playerName,
                joinAt = LocalDateTime.now(ZoneOffset.UTC)
            ).updateCreatedModified(request.playerName)

            playerProfileRepository.addPlayerProfile(playerProfile).run {
                return actionResult
                    .withSuccessStatus()
                    .withResult(this)
            }
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun updatePlayerProfile(invoker: String?, request: UpdatePlayerProfileRequestDTO): IActionResult<PlayerProfileEntity?> {
        val actionResult = ActionResult<PlayerProfileEntity?>()

        runCatching {
            val playerProfile = playerProfileRepository.getByUUID(request.playerUUID)?.apply {
                this.playerName = request.playerName
            } ?: return actionResult
                .withStatus(ActionStatus.PLAYER_PROFILE_NOT_EXIST)

            playerProfileRepository.updatePlayerProfile(playerProfile).run {
                return actionResult
                    .withSuccessStatus()
                    .withResult(this)
            }
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun getPlayerProfileByUuid(request: GetPlayerProfileRequestDTO): IActionResult<PlayerProfileEntity?> {
        val actionResult = ActionResult<PlayerProfileEntity?>()

        runCatching {
            playerProfileRepository.getByUUID(request.playerUuid!!)?.let {
                return actionResult
                    .withSuccessStatus()
                    .withResult(it)
            }

            return actionResult
                .withStatus(ActionStatus.PLAYER_PROFILE_NOT_EXIST)
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun getPlayerProfileByName(request: GetPlayerProfileRequestDTO): IActionResult<PlayerProfileEntity?> {
        val actionResult = ActionResult<PlayerProfileEntity?>()

        runCatching {
            playerProfileRepository.getByPlayerName(request.playerName!!)?.let {
                return actionResult
                    .withSuccessStatus()
                    .withResult(it)
            }

            return actionResult
                .withStatus(ActionStatus.PLAYER_PROFILE_NOT_EXIST)
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun getAllPlayerProfile(): IActionResult<List<PlayerProfileEntity>> {
        val actionResult= ActionResult<List<PlayerProfileEntity>>()

        runCatching {
            playerProfileRepository.getAll().run {
                return actionResult
                    .withSuccessStatus()
                    .withResult(this)
            }
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
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

    override fun assignRole(invoker: String?, request: AssignRoleRequestDTO): IActionResult<Long?> {
        val actionResult = ActionResult<Long?>()

        runCatching {
            val playerProfile = playerProfileRepository.getByUUID(request.playerUUID)
                ?: return actionResult
                    .withStatus(ActionStatus.PLAYER_PROFILE_NOT_EXIST)

            val role = roleRepository.getByRoleCode(request.roleCode)
                ?: return actionResult
                    .withStatus(ActionStatus.ROLE_NOT_EXIST)

            playerProfile.roleId?.let {
                if (it != role.roleId!!) {
                    playerProfile.roleId = role.roleId
                    return@let
                }

                return actionResult
                    .withStatus(ActionStatus.PLAYER_ROLE_ALREADY_ASSIGN)
            }

            playerProfileRepository.updatePlayerProfile(playerProfile.updateModified(invoker)).run {
                return actionResult
                    .withSuccessStatus()
                    .withResult(this?.roleId)
            }
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }

    override fun unassignRole(invoker: String?, request: UnassignRoleRequestDTO): IActionResult<Unit> {
        val actionResult = ActionResult<Unit>()

        runCatching {
            val playerProfile = playerProfileRepository.getByUUID(request.playerUUID)
                ?: return actionResult
                    .withStatus(ActionStatus.PLAYER_PROFILE_NOT_EXIST)

            playerProfile.roleId?.let {
                playerProfile.roleId = null
            } ?: return actionResult
                .withStatus(ActionStatus.PLAYER_ROLE_NOT_ASSIGN)

            playerProfileRepository.updatePlayerProfile(playerProfile.updateModified(invoker)).run {
                return actionResult
                    .withSuccessStatus()
            }
        }.getOrElse {
            return actionResult
                .withFailureStatus()
                .withNeonException(NeonAPIException(it.message, it))
        }
    }
}