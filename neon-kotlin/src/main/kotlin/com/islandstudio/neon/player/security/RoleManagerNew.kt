package com.islandstudio.neon.player.security

import com.islandstudio.neon.apinew.entity.player.PlayerRole
import com.islandstudio.neon.apinew.facade.player.IPlayerRoleFacade
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.core.exception.NeonException
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import com.islandstudio.neon.shared.core.initialization.IRunnerNew
import org.bukkit.event.Listener
import org.bukkit.scoreboard.Scoreboard

class RoleManagerNew: IRunnerNew, IComponentProvider {
    private val pluginContext = getComponent<IPluginContext>()
    private var roleScoreboard: Scoreboard? = null

    init {
        getKoin().declare(this)
    }

    override fun run() {
        roleScoreboard = pluginContext.getServer().scoreboardManager?.newScoreboard
            ?: throw NeonException("Could not initialize role scoreboard due to world not loaded!")

        // get all role and register to the scoreboard

        registerEvent(RoleManagerEvent(this))
    }

    suspend fun getAllRole(): ArrayList<PlayerRole> {
        val playerRoleFacade = getComponent<IPlayerRoleFacade>()

        return playerRoleFacade.getAllPlayerRoles().getResult().get()
    }

    private class RoleManagerEvent(roleManagerNew: RoleManagerNew): Listener {

    }
}