package com.islandstudio.neon.player.role

import com.islandstudio.neon.command.ICommandDispatcherNew
import com.islandstudio.neon.command.properties.AccessibleCommand
import org.bukkit.command.CommandSender

class RoleManagerCommandDispatcher(roleManagerNew: RoleManagerNew): ICommandDispatcherNew {
    override fun processCommand(
        commander: CommandSender,
        playerAccessibleCommand: AccessibleCommand?,
        args: Array<out String>,
    ) {
        TODO("Not yet implemented")
    }

}