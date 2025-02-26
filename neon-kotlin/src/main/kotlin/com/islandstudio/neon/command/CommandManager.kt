package com.islandstudio.neon.command

import com.islandstudio.neon.Neon
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.command.properties.AccessibleCommand
import com.islandstudio.neon.player.security.AccessControlManager
import com.islandstudio.neon.player.security.role.RoleManager
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.koin.core.component.inject
import java.util.*

class CommandManager: TabExecutor {
    companion object: IRunner, IComponentInjector {
        private val neon by inject<Neon>()
        private val commandSession: HashMap<UUID, ArrayList<AccessibleCommand>> = hashMapOf()
        private val systemAccessibleCommand = ArrayList<AccessibleCommand>()

        private const val COMMAND_PREFIX = "neon"

        override fun run() {
            neon.server.getPluginCommand(COMMAND_PREFIX)?.setExecutor(CommandManager())
        }

        private fun isValidCommander(commander: CommandSender): Boolean {
            return commander is Player || commander is ConsoleCommandSender
        }

        fun getCommanderName(commander: CommandSender): String? {
            return when(commander) {
                is Player -> commander.name
                is ConsoleCommandSender -> null

                else -> null
            }
        }

        fun registerPlayerAccessibleCommands(player: Player): ArrayList<AccessibleCommand> {
            commandSession[player.uniqueId]?.let { return it }

            return CommandAlias.getAccessibleCommands(player).apply {
                commandSession[player.uniqueId] = this
            }
        }

        fun registerSystemAccessibleCommands(commander: ConsoleCommandSender): ArrayList<AccessibleCommand> {
            if (systemAccessibleCommand.isEmpty()) {
                systemAccessibleCommand.addAll(CommandAlias.getAccessibleCommands(commander))
            }

            return systemAccessibleCommand
        }

        fun updatePlayerAccessibleCommands(player: Player) {
            if (!commandSession.keys.contains(player.uniqueId)) return

            commandSession.replace(player.uniqueId, CommandAlias.getAccessibleCommands(player))
        }

        fun unregisterPlayerAccessibleCommands(player: Player) {
            commandSession.remove(player.uniqueId)
        }
    }

    override fun onTabComplete(
        commander: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String>?
    ): List<String?>? {
        if (!isValidCommander(commander)) {
            CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Invalid commander!")
            return null
        }

        if (!cmd.name.equals(COMMAND_PREFIX, true)) return emptyList()

        val accessibleCommands = if (commander is Player) {
            registerPlayerAccessibleCommands(commander)
        } else {
            registerSystemAccessibleCommands(commander as ConsoleCommandSender)
        }

        args?.let { args ->
            if (accessibleCommands.isEmpty()) return@let

            if (args.size == 1) {
                return accessibleCommands
                    .map { it.command }
                    .sorted()
                    .filter { it.startsWith(args[0], true) }
                    .toList()
            }

            CommandAlias.getAllCommandAlias()
                .find { it.alias.equals(args[0], true) }
                ?.let {
                    return when(it) {
                        CommandAlias.RoleAlias -> {
                            RoleManager.getTabCompletion(commander, accessibleCommands, args)
                        }

                        CommandAlias.PermissionAlias -> {
                            AccessControlManager.getTabCompletion(commander, accessibleCommands, args)
                        }

                        CommandAlias.NWaypointsAlias -> {
                            arrayListOf()
                        }

                        CommandAlias.ServerFeaturesAlias -> {
                            arrayListOf()
                        }
                    }
                }
        }

        return emptyList()
    }

    override fun onCommand(
        commander: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        if (!isValidCommander(commander)) {
            CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.RED}Invalid commander!")
            return true
        }

        if (!cmd.name.equals(COMMAND_PREFIX, true)) return true

        val accessibleCommands = if (commander is Player) {
            registerPlayerAccessibleCommands(commander)
        } else {
            registerSystemAccessibleCommands(commander as ConsoleCommandSender)
        }

        args?.let { args ->
            if (accessibleCommands.isEmpty()) return true

            if (args.isEmpty()) {
                return@let
            }

            CommandAlias.getAllCommandAlias()
                .find { it.alias.equals(args[0], true) }
                ?.let {
                    when(it) {
                        CommandAlias.RoleAlias -> {
                            RoleManager.getCommandDispatcher(commander, accessibleCommands, args)
                        }

                        CommandAlias.PermissionAlias -> {
                            AccessControlManager.getCommandDispatcher(commander, accessibleCommands, args)
                        }

                        CommandAlias.NWaypointsAlias -> {

                        }

                        CommandAlias.ServerFeaturesAlias -> {

                        }
                    }
                } ?: CommandSyntaxHandler.alertInvalidCommand(commander, args[0])
        }

        return true
    }
}