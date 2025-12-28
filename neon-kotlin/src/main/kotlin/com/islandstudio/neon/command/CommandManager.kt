package com.islandstudio.neon.command

import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.command.properties.AccessibleCommand
import com.islandstudio.neon.features.neonfeature.NeonFeatureManager
import com.islandstudio.neon.player.permission.PermissionManager
import com.islandstudio.neon.player.role.RoleManagerNew
import com.islandstudio.neon.player.session.PlayerSessionManagerNew
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.core.di.injectComponent
import com.islandstudio.neon.shared.core.initialization.IPluginContext
import com.islandstudio.neon.shared.core.initialization.IRunnerNew
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class CommandManager: TabExecutor, IRunnerNew, IComponentProvider {
    private val commandPrefix = "neon"
    private val commandSession: HashMap<Player, ArrayList<AccessibleCommand>> = hashMapOf()

    private val pluginContext = getComponent<IPluginContext>()

    private val playerSessionManager by injectComponent<PlayerSessionManagerNew>()
    private val permissionManager by injectComponent<PermissionManager>()
    private val roleManager by injectComponent<RoleManagerNew>()

    init {
        getKoin().declare(this)
    }

    override fun run() {
        pluginContext.getServer().getPluginCommand(commandPrefix)?.setExecutor(CommandManager())
    }

    private fun isValidCommander(commander: CommandSender): Boolean {
        return commander is Player || commander is ConsoleCommandSender
    }

    fun registerPlayerAccessibleCommands(player: Player): ArrayList<AccessibleCommand> {
        commandSession[player]?.let { return it }

        return CommandAlias.getAccessibleCommands(player).apply {
            commandSession[player] = this
        }
    }

    fun unregisterPlayerAccessibleCommands(player: Player) {
        commandSession.remove(player)
    }

    fun updatePlayerAccessibleCommands(player: Player) {
        if (!commandSession.keys.contains(player)) return

        commandSession.replace(player, CommandAlias.getAccessibleCommands(player))
    }

    fun updatePlayerAccessibleCommandsByRole(roleId: Long) {
        commandSession.keys
            .filter { x->
                val playerSession = playerSessionManager.getPlayerSession(x) ?: return@filter false

                playerSession.roleId?.let { y -> y == roleId } ?: return@filter false
            }.forEach {
                updatePlayerAccessibleCommands(it)
            }
    }

    companion object: IComponentProvider {

        fun getCommanderName(commander: CommandSender): String? {
            return when(commander) {
                is Player -> commander.name
                is ConsoleCommandSender -> null

                else -> null
            }
        }
    }

    override fun onTabComplete(
        commander: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String>?
    ): List<String?>? {
        if (!isValidCommander(commander)) return emptyList()

        if (!cmd.name.equals(commandPrefix, true)) return emptyList()

        val playerAccessibleCommands = if (commander is Player) {
            registerPlayerAccessibleCommands(commander)
        } else {
            arrayListOf()
        }

        args?.let { args ->
            if (args.size == 1) {
                if (commander is Player) {
                    return playerAccessibleCommands
                        .map { it.command }
                        .sorted()
                        .filter { it.startsWith(args[0], true) }
                        .toList()
                }

                return CommandAlias.getAllCommandAlias()
                    .map { it.alias }
                    .sorted()
                    .filter { it.startsWith(args[0], true) }
                    .toList()
            }

            CommandAlias.getAllCommandAlias()
                .find { it.alias.equals(args[0], true) }
                ?.let {
//                    val playerAccessibleCommand = with(playerAccessibleCommands) {
//                        if (commander !is Player) return@with null
//
//                        return@with playerAccessibleCommands
//                            .find { x -> x.command.equals(it.alias, true) }
//                            ?: return@let
//                    }

                    val playerAccessibleCommand = with(playerAccessibleCommands) {
                        return@with playerAccessibleCommands
                            .find { x -> x.command.equals(it.alias, true) }
                            ?: if (commander !is Player) return@with null else return@let
                    }

                    return when(it) {
                        CommandAlias.RoleAlias -> {
                            roleManager.commandDispatcher.getTabCompletion(commander, playerAccessibleCommand, args)
                        }

                        CommandAlias.PermissionAlias -> {
                            permissionManager.commandDispatcher.getTabCompletion(commander, playerAccessibleCommand, args)
                        }

//                        CommandAlias.NWaypointsAlias -> {
//                            arrayListOf()
//                        }

                        CommandAlias.NeonFeatureAlias -> {
                            NeonFeatureManager.getTabCompletion(commander, playerAccessibleCommand, args)
                        }

                        else -> arrayListOf()
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

        if (!cmd.name.equals(commandPrefix, true)) return true

        val playerAccessibleCommands = if (commander is Player) {
            registerPlayerAccessibleCommands(commander)
        } else {
            arrayListOf()
        }

        args?.let { args ->
            if (args.isEmpty()) return@let

            if (commander is Player && playerAccessibleCommands.isEmpty()) {
                CommandSyntaxHandler.alertInvalidCommand(commander, args[0])
                return true
            }

            CommandAlias.getAllCommandAlias()
                .find { it.alias.equals(args[0], true) }
                ?.let {
                    val playerAccessibleCommand = with(playerAccessibleCommands) {
                        if (commander !is Player) return@with null

                        return@with playerAccessibleCommands
                            .find { x -> x.command.equals(it.alias, true) }
                            ?: return@let CommandSyntaxHandler.alertInvalidCommand(commander, args[0])
                    }

                    when(it) {
                        CommandAlias.RoleAlias -> {
                            roleManager.commandDispatcher.processCommand(commander, playerAccessibleCommand, args)
                        }

                        CommandAlias.PermissionAlias -> {
                            permissionManager.commandDispatcher.processCommand(commander, playerAccessibleCommand, args)
                        }

                        CommandAlias.NeonFeatureAlias -> {
                            NeonFeatureManager.getCommandDispatcher(commander, playerAccessibleCommand, args)
                        }

                        else -> CommandSyntaxHandler.alertInvalidCommand(commander, args[0])
                    }
                } ?: CommandSyntaxHandler.alertInvalidCommand(commander, args[0])
        }

        return true
    }
}