package com.islandstudio.neon.stable.command

import com.islandstudio.neon.Neon
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.command.processing.CommandSyntaxHandler
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.koin.core.component.inject

class CommandManager: TabExecutor {
    companion object: IRunner, IComponentInjector {
        private val neon by inject<Neon>()

        private const val COMMAND_PREFIX = "neon"

        override fun run() {
            neon.server.getPluginCommand(COMMAND_PREFIX)?.setExecutor(CommandManager())
        }

        fun isValidCommander(commander: CommandSender): Boolean {
            return commander is Player || commander is ConsoleCommandSender
        }
    }

    override fun onTabComplete(
        commander: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String?>?
    ): List<String?>? {
        if (!isValidCommander(commander)) {
            CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Invalid commander!")
            return null
        }

        return null
    }

    override fun onCommand(
        commander: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String?>?
    ): Boolean {
        if (!isValidCommander(commander)) {
            CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Invalid commander!")
            return true
        }



        TODO("Not yet implemented")
    }
}