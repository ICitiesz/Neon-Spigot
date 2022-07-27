package com.islandstudio.neon.stable.primary.nCommand

import com.islandstudio.neon.experimental.nEffect.NEffect
import com.islandstudio.neon.experimental.nServerConfigurationNew.NServerConfigurationNew
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nExperimental.NExperimental
import com.islandstudio.neon.stable.primary.nServerConfiguration.NServerConfiguration
import com.islandstudio.neon.stable.secondary.nHarvest.NHarvest
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

class NCommand: Listener, TabExecutor {

    companion object {
        var isModerating: Boolean = false

        private const val COMMAND_PREFIX: String = "neon"
        private val pluginName: String = "${ChatColor.WHITE}[${ChatColor.AQUA}Neon${ChatColor.WHITE}]"
        private val plugin: Plugin = NConstructor.plugin

        fun run() {
            (plugin.server.getPluginCommand(COMMAND_PREFIX))?.setExecutor(NCommand())
        }

        /**
         * Return the plugin name that used in the plugin message.
         *
         * @return The plugin name. (String)
         */
        fun getPluginName(): String {
            return pluginName
        }
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            plugin.server.consoleSender.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.RED}This command doesn't support console execution!"))
            return true
        }

        val commander: Player = sender

        if (!cmd.name.equals(COMMAND_PREFIX, true)) return true

        if (args.isEmpty()) {
            commander.sendMessage(CommandSyntax.createSyntaxMessage(
                "${ChatColor.YELLOW}Please type '/neon help <pageNumber>' to show all the available commands!"))
            return true
        }

        when (args[0].lowercase()) {
            Commands.RANK.commandAlias -> {
                NRank.setCommandHandler(commander, args, pluginName)
                return true
            }

            Commands.WAYPOINTS.commandAlias -> {
                NWaypoints.Handler.setCommandHandler(commander, args)
                return true
            }

            Commands.DEBUG.commandAlias -> {
                if (!commander.isOp) {
                    commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
                    return true
                }

//                NDisguise.test(comm
//                ander)
//                NDisguise.testPacket(commander)

                //commander.sendMessage(CommandSyntax.createSyntaxMessage("There is nothing here :D"))


                return true
            }

            Commands.SERVERCONFIG.commandAlias -> {
                if (!commander.isOp) {
                    commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
                    return true
                }

                commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.RED}The old nServerConfiguration is disabled for renovation!"))
                commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Please use the list of commands below to test the new nServerConfiguration:"))
                commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.WHITE}'${ChatColor.GREEN}/neon serverconfignew${ChatColor.WHITE}' " +
                        "${ChatColor.YELLOW}to open the nServerConfiguration GUI."))
                commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.WHITE}'${ChatColor.GREEN}/neon serverconfignew <feature/config name>${ChatColor.WHITE}' " +
                        "${ChatColor.YELLOW}to view toggle status for the feature/config."))
                commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.WHITE}'${ChatColor.GREEN}/neon serverconfignew <feature/config name> <option name>${ChatColor.WHITE}' " +
                        "${ChatColor.YELLOW}to view option value for the feature/config."))
                commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.WHITE}'${ChatColor.GREEN}/neon serverconfignew <feature/config name> <option name> <option value>${ChatColor.WHITE}' " +
                        "${ChatColor.YELLOW}to tweak options for the feature/config."))

                //NServerConfiguration.Handler.setCommandHandler(commander, args)
            }

            Commands.SERVERCONFIGNEW.commandAlias -> {
                NServerConfigurationNew.Handler.setCommandHandler(commander, args)
            }

            Commands.REGEN.commandAlias -> {
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

            Commands.GM.commandAlias -> {
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

            Commands.MOD.commandAlias -> {
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

            Commands.EFS.commandAlias -> {
                NEffect.setCommandHandler(commander)
            }

            Commands.EXPERIMENTAL.commandAlias -> {
                NExperimental.Handler.setCommandHandler(commander, args)
            }

            else -> {
                commander.sendMessage(CommandSyntax.createSyntaxMessage(
                    "${ChatColor.YELLOW}Sorry, there are no command as ${ChatColor.WHITE}" +
                                "'${ChatColor.GRAY}${args[0]}${ChatColor.WHITE}'${ChatColor.YELLOW}!"))
                return true
            }
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): MutableList<String>? {
        if (sender !is Player) {
            plugin.server.consoleSender.sendMessage("${ChatColor.RED}This command doesn't support console execution!")
            return null
        }

        if (!cmd.name.equals(COMMAND_PREFIX, true)) return null

        val commander: Player = sender

        if (args.size == 1) return Commands.values().sorted().map { it.commandAlias }.filter { it.startsWith(args[0], true) }.toMutableList()

        if (args.isEmpty()) return null

        when (args[0].lowercase()) {
            Commands.RANK.commandAlias -> {
                return NRank.tabCompletion(commander, args)
            }

            Commands.WAYPOINTS.commandAlias -> {
                return NWaypoints.Handler.tabCompletion(commander, args)
            }

            Commands.SERVERCONFIG.commandAlias -> {
                return NServerConfiguration.Handler.tabCompletion(commander, args)
            }

            Commands.SERVERCONFIGNEW.commandAlias -> {
                return NServerConfigurationNew.Handler.tabCompletion(commander, args)
            }
        }

        return null
    }

}