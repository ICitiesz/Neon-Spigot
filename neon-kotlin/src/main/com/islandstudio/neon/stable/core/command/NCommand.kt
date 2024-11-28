package com.islandstudio.neon.stable.core.command

import com.islandstudio.neon.Neon
import com.islandstudio.neon.experimental.nEffect.NEffect
import com.islandstudio.neon.experimental.nFireworks.NFireworks
import com.islandstudio.neon.experimental.nPainting.NPainting
import com.islandstudio.neon.stable.core.application.di.ModuleInjector
import com.islandstudio.neon.stable.core.application.init.AppInitializer
import com.islandstudio.neon.stable.core.command.commandlist.NCommandList
import com.islandstudio.neon.stable.core.command.properties.CommandAlias
import com.islandstudio.neon.stable.core.command.properties.CommandSyntax
import com.islandstudio.neon.stable.features.nDurable.NDurable
import com.islandstudio.neon.stable.features.nRank.NRank
import com.islandstudio.neon.stable.features.nServerFeatures.NServerFeaturesRemastered
import com.islandstudio.neon.stable.features.nWaypoints.NWaypoints
import com.islandstudio.neon.stable.player.nAccessPermission.NAccessPermission
import com.islandstudio.neon.stable.player.nRole.NRole
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.ServerCommandEvent
import org.koin.core.component.inject

class NCommand: Listener, TabExecutor {
    companion object: ModuleInjector {
        var isModerating: Boolean = false

        /* nCommand general properties */
        val COMMAND_SYNTAX_PREFIX: String = "${ChatColor.WHITE}[${ChatColor.AQUA}Neon${ChatColor.WHITE}] "
        private const val COMMAND_PREFIX: String = "neon"
        private val neon by inject<Neon>()
        private val disabledServerReloadCommand: ArrayList<String> = arrayListOf("rl", "reload", "bukkit:reload", "bukkit:rl")

        fun run() {
            AppInitializer.registerEventProcessor(EventProcessor())

            (neon.server.getPluginCommand(COMMAND_PREFIX))?.setExecutor(NCommand()).also {
                NCommandList.Handler.run()
            }
        }

        /*
        * CommandInterfaceProcessor.sendCommandSyntax(
                commander,
                "${ChatColor.YELLOW}Please use ${ChatColor.GREEN}`/neon reload`${ChatColor.YELLOW} to reload configuration."
            )
        *  */

    }

    override fun onCommand(commander: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (!cmd.name.equals(COMMAND_PREFIX, true)) return true

        /* Display command list book if no arguments after the command prefix */
        with(args) {
            if (args.isEmpty()) {
                if (commander !is Player) {
                    return@with CommandInterfaceProcessor.sendCommandSyntax(commander, "${ChatColor.RED}Display command list currently not support in console!")
                }

                if (commander.isSleeping) {
                    return@with CommandInterfaceProcessor.sendCommandSyntax(commander, "${ChatColor.RED}Unable to display command list while sleeping!")
                }

                return@with NCommandList.displayCommandUI(commander, NCommandList.getCommandListBook(commander), NCommandList.CommandUITypes.COMMAND_LIST, null)
            }

            /* Main command component which will target each feature's command dispatcher based on command alias */
            CommandAlias.entries.find { it.command.aliasName.equals(args[0], true) }?.let {
                when(it) {
                    CommandAlias.NROLE -> {
                        NRole.Handler.getCommandDispatcher(commander, args)
                    }

                    CommandAlias.NACCESS_PERMISSION -> {
                        NAccessPermission.Handler.getCommandDispatcher(commander, args)
                    }

                    CommandAlias.NWAYPOINTS -> {
                        NWaypoints.Handler.setCommandHandler(commander as Player, args)
                    }

                    CommandAlias.NRANK -> {
                        NRank.setCommandHandler(commander as Player, args, "${ChatColor.WHITE}[${ChatColor.AQUA}Neon${ChatColor.WHITE}]")
                    }

                    CommandAlias.DEBUG -> {
                        CommandInterfaceProcessor.sendCommandSyntax(commander, "This is debug command!")


                        // TODO: Multiplayer testing needed
//                        val nPlayer = NPacketProcessor.getNPlayer(commander as Player)
//
//                        nPlayer.javaClass.getField("displayName").apply {
//                            this.set(nPlayer, "IsNameAvailable")
//                        }
//
//                        nPlayer.javaClass.superclass.getDeclaredField("cq").apply {
//                            this.isAccessible = true
//
//                            val gameProfile = this.get(nPlayer)
//
//                            gameProfile.javaClass.getDeclaredField("name").apply {
//                                this.isAccessible = true
//
//                                this.set(gameProfile, "${ChatColor.GREEN}CanChangeName")
//                            }
//                        }
//
//                        neon.server.onlinePlayers.forEach { player ->
//                            val updateProfilePacket = ClientboundPlayerInfoUpdatePacket(Action.UPDATE_DISPLAY_NAME, nPlayer)
//
//                            NPacketProcessor.sendGamePacket(player, updateProfilePacket)
//                        }
//
//                        val nPlayer2 = NPacketProcessor.getNPlayer(commander)
//
//                        nPlayer2.javaClass.getField("displayName").apply {
//                            println(this.get(nPlayer2))
//                        }
//
//                        println(commander.name)
//                        println(commander.displayName)
                    }

                    CommandAlias.GAMEMODE -> {
                        if (!commander.isOp) {
                            return@let CommandInterfaceProcessor.sendCommandSyntax(commander, CommandSyntax.INVALID_PERMISSION)
                        }

                        if (args.size != 2 ) {
                            return@let CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                        }

                        when (args[1].lowercase()) {
                            "0" -> {
                                (commander as Player).gameMode = GameMode.SURVIVAL
                                CommandInterfaceProcessor.sendCommandSyntax(
                                    commander,
                                    "${ChatColor.GREEN}Your gamemode has been changed to ${ChatColor.YELLOW}SURVIVAL ${ChatColor.GREEN}!")
                            }

                            "1" -> {
                                (commander as Player).gameMode = GameMode.CREATIVE
                                CommandInterfaceProcessor.sendCommandSyntax(
                                    commander,
                                    "${ChatColor.GREEN}Your gamemode has been changed to ${ChatColor.YELLOW}CREATIVE ${ChatColor.GREEN}!"
                                )
                            }

                            "2" -> {
                                (commander as Player).gameMode = GameMode.ADVENTURE
                                CommandInterfaceProcessor.sendCommandSyntax(
                                    commander,
                                    "${ChatColor.GREEN}Your gamemode has been changed to ${ChatColor.YELLOW}ADVENTURE ${ChatColor.GREEN}!"
                                )
                            }

                            "3" -> {
                                (commander as Player).gameMode = GameMode.SPECTATOR
                                CommandInterfaceProcessor.sendCommandSyntax(
                                    commander,
                                    "${ChatColor.GREEN}Your gamemode has been changed to ${ChatColor.YELLOW}SPECTATOR ${ChatColor.GREEN}!"
                                )
                            }

                            else -> {
                                CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                            }
                        }
                    }

                    CommandAlias.REGEN -> {
                        if (!commander.isOp) {
                            return@let CommandInterfaceProcessor.sendCommandSyntax(commander, CommandSyntax.INVALID_PERMISSION)
                        }

                        if ((commander as Player).foodLevel < 20 || commander.health < 20)  {
                            commander.health = 20.0
                            commander.foodLevel = 20
                            commander.saturation = 20F

                            return@let CommandInterfaceProcessor.sendCommandSyntax(commander, "${ChatColor.GREEN}Your health and hunger has been restored!")
                        }

                        return@let CommandInterfaceProcessor.sendCommandSyntax(commander, "${ChatColor.YELLOW}Regen available only when you are hungry or your health is low!")
                    }

                    CommandAlias.NSERVER_FEATURES -> {
                        //NServerFeatures.Handler.getCommandHandler(commander as Player, args)
                    }

                    CommandAlias.EFFECT -> {
                        NEffect.setCommandHandler(commander as Player)
                    }

                    CommandAlias.MODERATOR -> {
                        if (true) return@let CommandInterfaceProcessor.sendCommandSyntax(commander, "${ChatColor.YELLOW}This command currently disabled for update!")

                        if (!commander.isOp) {
                            return@let CommandInterfaceProcessor.sendCommandSyntax(commander, CommandSyntax.INVALID_PERMISSION)
                        }

                        if (args.size != 2) {
                            return@let commander.sendMessage(com.islandstudio.neon.stable.primary.nCommand.CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                        }

                        when (args[1].lowercase()) {
                            "on" -> {
                                if (!(commander as Player).isInvulnerable) {
                                    commander.isInvulnerable = true
                                    commander.allowFlight = true

                                    if (!commander.isCollidable) {
                                        commander.isCollidable = true
                                    }

                                    if (!com.islandstudio.neon.stable.primary.nCommand.NCommand.isModerating) {
                                        com.islandstudio.neon.stable.primary.nCommand.NCommand.isModerating = true
                                    }

                                    commander.sendMessage("${ChatColor.GREEN}Moderation enabled!")
                                }

                                return true
                            }

                            "off" -> {
                                if ((commander as Player).isInvulnerable) {
                                    commander.isInvulnerable = false
                                    commander.allowFlight = false

                                    if (commander.isCollidable) {
                                        commander.isCollidable = false
                                    }

                                    if (com.islandstudio.neon.stable.primary.nCommand.NCommand.isModerating) {
                                        com.islandstudio.neon.stable.primary.nCommand.NCommand.isModerating = false
                                    }

                                    commander.sendMessage("${ChatColor.GREEN}Moderation disabled!")
                                }

                                return true
                            }

                            else -> {
                                CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                            }
                        }
                    }

                    CommandAlias.NFIREWORKS -> {
                        NFireworks.Handler.getCommandHandler(commander as Player, args)
                    }

                    CommandAlias.NDURABLE -> {
                        NDurable.Handler.getCommandDispatcher(commander, args)
                    }

                    CommandAlias.NPAINTING -> {
                        NPainting.Handler.getCommandHandler(commander as Player, args)
                    }

                    CommandAlias.DATABASE -> {
//                        if (args.size == 2) {
//                            if (args[1].equals("off", true)) {
//
//                                //databaseController.stopDatabase()
//                            } else {
//                                return@let
//                            }
//                        }
                    }

                    CommandAlias.NSERVER_FEATURES_REMASTERED -> {
                        NServerFeaturesRemastered.Handler.getCommandDispatcher(commander, args)
                    }
                }
            } ?: CommandInterfaceProcessor.notifyInvalidCommand(commander, args[0])
        }

        return true
    }

    override fun onTabComplete(commander: CommandSender, cmd: Command, label: String, args: Array<out String>): MutableList<String>? {
        if (!cmd.name.equals(COMMAND_PREFIX, true)) return null

        /* Get the role access permission from commander if it is player */
        val roleAccessPermission = with(commander) {
            if (this !is Player) return@with null

            return@with NAccessPermission.getAssignedRoleAllPermission(this)
        }

        if (args.size == 1) {
            return CommandAlias.getCommandAlias(commander, args[0], roleAccessPermission)
        }

        CommandAlias.entries.find {
            it.command.aliasName.equals(args[0], true)
        }?.let {
            when (it) {
                CommandAlias.NROLE -> {
                    return NRole.Handler.getTabCompletion(commander, args)
                }

                CommandAlias.NACCESS_PERMISSION -> {
                    return NAccessPermission.Handler.getTabCompletion(commander, args)
                }

                CommandAlias.NWAYPOINTS -> {
                    return mutableListOf()
                }

                CommandAlias.NRANK -> {
                    return mutableListOf()
                }

                CommandAlias.DEBUG -> {
                    return mutableListOf()
                }

                CommandAlias.GAMEMODE -> {
                    return mutableListOf()
                }

                CommandAlias.REGEN -> {
                    return mutableListOf()
                }

                CommandAlias.NSERVER_FEATURES ->  {
                    return mutableListOf()
                }

                CommandAlias.EFFECT -> {
                    return mutableListOf()
                }

                CommandAlias.MODERATOR -> {
                    return mutableListOf()
                }

                CommandAlias.NFIREWORKS -> {
                    return mutableListOf()
                }

                CommandAlias.NDURABLE -> {
                    return NDurable.Handler.getTabCompletion(commander, args)
                }

                CommandAlias.NPAINTING -> {
                    return mutableListOf()
                }

                CommandAlias.DATABASE -> {
                    return mutableListOf()
                }

                CommandAlias.NSERVER_FEATURES_REMASTERED -> {
                    return NServerFeaturesRemastered.Handler.getTabCompletion(commander, args)
                }
            }
        }

        return mutableListOf()
    }

    private class EventProcessor: Listener {
        @EventHandler
        private fun onConsoleCommand(e: ServerCommandEvent) {
            //revokeServerReloadCommand(e)
        }

        @EventHandler
        private fun onPlayerCommand(e: PlayerCommandPreprocessEvent) {
            //revokeServerReloadCommand(e)
        }
    }
}