package com.islandstudio.neon.stable.primary.nCommand

import com.islandstudio.neon.experimental.nEffect.NEffect
import com.islandstudio.neon.experimental.nFireworks.NFireworks
import com.islandstudio.neon.stable.primary.nCommand.nCommandList.NCommandList
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nServerFeatures.NServerFeatures
import com.islandstudio.neon.stable.secondary.nDurable.NDurable
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

class NCommand: Commands(), Listener, TabExecutor {
    companion object {
        var isModerating: Boolean = false

        private const val COMMAND_PREFIX: String = "neon"
        private val pluginName: String = "${ChatColor.WHITE}[${ChatColor.AQUA}Neon${ChatColor.WHITE}]"
        private val plugin: Plugin = NConstructor.plugin

        fun run() {
            (plugin.server.getPluginCommand(COMMAND_PREFIX))?.setExecutor(NCommand())

            NCommandList.Handler.run()
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
            if (commander.isSleeping) {
                commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Unable to display command list while sleeping!"))
                return true
            }

            NCommandList.displayCommandUI(commander, NCommandList.getCommandListBook(commander), NCommandList.CommandUITypes.COMMAND_LIST, null)
            return true
        }

        when (args[0].lowercase()) {
            CommandAlias.RANK.aliasName -> {
                NRank.setCommandHandler(commander, args, pluginName)
                return true
            }

            CommandAlias.WAYPOINTS.aliasName -> {
                NWaypoints.Handler.setCommandHandler(commander, args)
                return true
            }

            CommandAlias.NFIREWORKS.aliasName -> {
                NFireworks.Handler.setCommandHandler(commander, args)
                return true
            }

            CommandAlias.DEBUG.aliasName -> {
                if (!commander.isOp) {
                    commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
                    return true
                }

                commander.sendMessage(CommandSyntax.createSyntaxMessage("There is nothing here :D"))
                return true
            }

            CommandAlias.SERVERFEATURES.aliasName -> {
                NServerFeatures.Handler.setCommandHandler(commander, args)
            }

            CommandAlias.REGEN.aliasName -> {
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

            CommandAlias.GM.aliasName -> {
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

            CommandAlias.MOD.aliasName -> {
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

            CommandAlias.EFFECT.aliasName -> {
                NEffect.setCommandHandler(commander)
            }

            CommandAlias.DURABILITY.aliasName -> {
                NDurable.Handler.setCommandHandler(commander, args)
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

        if (args.size == 1) return CommandAlias.values().sorted().map { it.aliasName }.filter { it.startsWith(args[0], true) }.toMutableList()

        when (args[0].lowercase()) {
            CommandAlias.RANK.aliasName -> {
                return NRank.tabCompletion(commander, args)
            }

            CommandAlias.WAYPOINTS.aliasName -> {
                return NWaypoints.Handler.tabCompletion(commander, args)
            }

            CommandAlias.SERVERFEATURES.aliasName -> {
                return NServerFeatures.Handler.tabCompletion(commander, args)
            }

            CommandAlias.NFIREWORKS.aliasName -> {
                return mutableListOf()
                //return NFireworks.Handler.tabCompletion(commander, args)
            }

            CommandAlias.DURABILITY.aliasName -> {
                return NDurable.Handler.tabCompletion(commander, args)
            }
        }

        return mutableListOf()
    }

}