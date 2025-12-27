package com.islandstudio.neon.apinew.service.player.impl

import com.islandstudio.neon.apinew.connection.UserContext
import com.islandstudio.neon.apinew.dto.ResultProvider
import com.islandstudio.neon.apinew.dto.action.player.profile.CreatePlayerProfileActionDTO
import com.islandstudio.neon.apinew.entity.player.PlayerProfile
import com.islandstudio.neon.apinew.entity.player.PlayerRole
import com.islandstudio.neon.apinew.repository.player.IPlayerProfileRepository
import com.islandstudio.neon.apinew.service.player.IPlayerProfileService
import com.islandstudio.neon.apinew.status.ActionResultStatus
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import org.koin.core.annotation.Single
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Single
class PlayerProfileService : IPlayerProfileService, IComponentProvider {
    private val playerProfileRepository = getComponent<IPlayerProfileRepository>()

    override suspend fun createPlayerProfile(context: UserContext, action: CreatePlayerProfileActionDTO): ResultProvider<PlayerProfile> {
        return when (val result = playerProfileRepository.addPlayerProfile(context,
            PlayerProfile(
                null,
                action.uuid,
                action.name,
                action.roleId,
                LocalDateTime.now(ZoneOffset.UTC),
            )
        )) {
            null -> ResultProvider(ActionResultStatus.FailedToCreatePlayerProfile(), null)
            else -> ResultProvider(ActionResultStatus.Success(), result)
        }
    }

    override suspend fun getPlayerProfile(uuid: UUID): ResultProvider<PlayerProfile> {
        return when (val result = playerProfileRepository.getPlayerProfile(uuid.toString())) {
            null -> ResultProvider(ActionResultStatus.PlayerProfileNotExist(), null)
            else -> ResultProvider(ActionResultStatus.Success(), result)
        }
    }

    override suspend fun assignPlayerRole(context: UserContext, playerProfile: PlayerProfile, playerRole: PlayerRole): ResultProvider<PlayerProfile> {
        if (playerProfile.roleId == playerRole.id) return ResultProvider(ActionResultStatus.PlayerRoleAlreadyAssigned())

        val result = playerProfileRepository.updatePlayerProfile(context, playerProfile.copy(roleId = playerRole.id))

        return ResultProvider(ActionResultStatus.Success(), result)
    }

    override suspend fun unassignPlayerRole(
        context: UserContext,
        uuid: UUID,
    ): ResultProvider<PlayerProfile> {
        TODO("Not yet implemented")
    }
}