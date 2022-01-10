package com.islandstudio.neon.stable.primary.nCommand

import com.islandstudio.neon.Main
import com.islandstudio.neon.experimental.nEffect.NEffect
import com.islandstudio.neon.stable.primary.nExperimental.NExperimental
import com.islandstudio.neon.stable.primary.nServerConfiguration.NServerConfiguration
import com.islandstudio.neon.stable.secondary.nRank.NRank
import com.islandstudio.neon.stable.secondary.nWaypoints.NWaypoints
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin

class NCommand: Listener, TabExecutor {

    companion object {
        var isModerating: Boolean = false

        private const val COMMAND_PREFIX: String = "neon"
        private val pluginName: String = ChatColor.WHITE.toString() + "[" + ChatColor.AQUA + "Neon" + ChatColor.WHITE + "] "
        private val plugin: Plugin = getPlugin(Main::class.java)

        fun run() {
            (plugin.server.getPluginCommand(COMMAND_PREFIX))?.setExecutor(NCommand())
        }

        fun getPluginName(): String {
            return pluginName
        }
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(pluginName + ChatColor.RED + "This command doesn't support console execution!")
            plugin.server.consoleSender.sendMessage()
            return true
        }

        val commander: Player = sender

        if (!cmd.name.equals(COMMAND_PREFIX, true)) return true

        if (args.isEmpty()) {
            commander.sendMessage(pluginName + ChatColor.YELLOW + "Please type '/neon help <pageNumber>' to show all the available commands!")
            return true
        }

        when (args[0].lowercase()) {
            Commands.RANK.name.lowercase() -> {
                NRank.setCommandHandler(commander, args, pluginName)
                return true
            }

            Commands.WAYPOINTS.name.lowercase() -> {
                NWaypoints.Handler.setCommandHandler(commander, args, pluginName)
                return true
            }

            Commands.DEBUG.name.lowercase() -> {
                if (!commander.isOp) {
                    commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
                    return true
                }
                commander.sendMessage(CommandSyntax.createSyntaxMessage("There is nothing here :D"))
                return true
            }

            Commands.SERVERCONFIG.name.lowercase() -> {
                NServerConfiguration.Handler.setCommandHandler(commander, args)
            }

            Commands.REGEN.name.lowercase() -> {
                if (!commander.isOp) {
                    commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
                    return true
                }

                if (commander.foodLevel < 20 || commander.health < 20)  {
                    commander.health = 20.0
                    commander.foodLevel = 20
                    commander.saturation = 20F

                    commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.GREEN}Your health and hunger has been restored!"))
                    return true
                }

                commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Regen available only when you are hungry or your health is low!"))
                return true
            }

            Commands.GM.name.lowercase() -> {
                if (!commander.isOp) {
                    commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
                    return true
                }

                if (args.size != 2) {
                    commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    return true
                }


                when (args[1].lowercase()) {
                    "0" -> {
                        commander.gameMode = GameMode.SURVIVAL
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.GREEN}Your gamemode has been changed to ${ChatColor.YELLOW}SURVIVAL ${ChatColor.GREEN}!"))
                    }

                    "1" -> {
                        commander.gameMode = GameMode.CREATIVE
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.GREEN}Your gamemode has been changed to ${ChatColor.YELLOW}CREATIVE ${ChatColor.GREEN}!"))
                    }

                    "2" -> {
                        commander.gameMode = GameMode.ADVENTURE
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.GREEN}Your gamemode has been changed to ${ChatColor.YELLOW}ADVENTURE ${ChatColor.GREEN}!"))
                    }

                    "3" -> {
                        commander.gameMode = GameMode.SPECTATOR
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.GREEN}Your gamemode has been changed to ${ChatColor.YELLOW}SPECTATOR ${ChatColor.GREEN}!"))
                    }

                    else -> {
                        commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    }
                }
            }

            Commands.MOD.name.lowercase() -> {
                if (!commander.isOp) {
                    commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
                    return true
                }

                if (args.size != 2) {
                    commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    return true
                }

                when (args[1].lowercase()) {
                    "on" -> {
                        if (!commander.isInvulnerable) {
                            commander.isInvulnerable = true
                            commander.allowFlight = true

                            if (!commander.isCollidable) {
                                commander.isCollidable = true
                            }

                            if (!isModerating) {
                                isModerating = true
                            }

                            commander.sendMessage("${ChatColor.GREEN}Moderation enabled!")
                        }

                        return true
                    }

                    "off" -> {
                        if (commander.isInvulnerable) {
                            commander.isInvulnerable = false
                            commander.allowFlight = false

                            if (commander.isCollidable) {
                                commander.isCollidable = false
                            }

                            if (isModerating) {
                                isModerating = false
                            }

                            commander.sendMessage("${ChatColor.GREEN}Moderation disabled!")
                        }

                        return true
                    }

                    else -> {
                        commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    }
                }

            }

            Commands.EFS.name.lowercase() -> {
                NEffect.setCommandHandler(commander)
            }

            Commands.EXPERIMENTAL.name.lowercase() -> {
                NExperimental.Handler.setCommandHandler(commander, args)
            }

            else -> {
                commander.sendMessage("$pluginName${ChatColor.YELLOW}Sorry, there are no command as " +
                        "${ChatColor.WHITE}'${ChatColor.GRAY}${args[0]}${ChatColor.WHITE}'${ChatColor.YELLOW}!")
                return true
            }
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): MutableList<String>? {
        if (sender !is Player) {
            plugin.server.consoleSender.sendMessage(ChatColor.RED.toString() + "This command doesn't support console execution!")
            return null
        }

        if (!cmd.name.equals(COMMAND_PREFIX, true)) return null

        val commander: Player = sender

        if (args.size == 1) return Commands.values().sorted().map { it.commandAlias }.toMutableList()

        if (args.isEmpty()) return null

        when (args[0].lowercase()) {
            Commands.RANK.name.lowercase() -> {
                return NRank.tabCompletion(commander, args)
            }

            Commands.WAYPOINTS.name.lowercase() -> {
                return NWaypoints.Handler.tabCompletion(commander, args)
            }

            Commands.SERVERCONFIG.name.lowercase() -> {
                return NServerConfiguration.Handler.tabCompletion(commander, args)
            }
        }

        return null
    }

}