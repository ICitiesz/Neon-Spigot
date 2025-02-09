package com.islandstudio.neon.stable.player.authorization

import com.islandstudio.neon.api.adapter.RoleAdapter
import com.islandstudio.neon.api.dto.action.ActionStatus
import com.islandstudio.neon.api.dto.request.security.CreateRoleRequestDTO
import com.islandstudio.neon.api.dto.request.security.role.GetRoleRequestDTO
import com.islandstudio.neon.api.entity.security.RoleEntity
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.command.Command
import com.islandstudio.neon.stable.command.ICommandDispatcher
import com.islandstudio.neon.stable.command.processing.CommandSyntaxHandler
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class RoleManager: IComponentInjector {
    private val roleAdapter by inject<RoleAdapter>()

    companion object: ICommandDispatcher {
        private val roleCommandAlias = Command.RoleCommand

        override fun getCommandDispatcher(commander: CommandSender, args: Array<out String>) {
            run {
                if (Command.isConsoleCommandSender(commander)) return@run

                Command.isCommandAccessible(commander)
            }


        }

        override fun getTabCompletion(commander: CommandSender, args: Array<out String>): MutableList<String> {
            return super.getTabCompletion(commander, args)
        }
    }

    fun createRole(commander: CommandSender?, roleCode: String, roleDisplayName: String, underscoreAsSpace: Boolean = false) {
        val request = CreateRoleRequestDTO(roleDisplayName, roleCode)

        roleAdapter.createRole(commander?.let { if (it is ConsoleCommandSender) null else it.name }, request)
            .onSuccess {
                println("Yaa create role success!")
            }
            .onFailure {
                commander?.let {
                    CommandSyntaxHandler.sendCommandSyntax(it, "Error!")
                }

                throw it.neonException!!
            }
            .onOtherStatus {
                when(it.status){
                    ActionStatus.DUPLICATE_RECORD -> {
                        commander?.let { commandSender ->
                            CommandSyntaxHandler.sendCommandSyntax(commandSender, it.displayMessage!!)
                        }
                    }

                    else -> return@onOtherStatus
                }
            }
    }

    fun getRole(commander: CommandSender?, roleId: Long?, roleCode: String?): RoleEntity? {
        val request = GetRoleRequestDTO(roleId, roleCode)

        roleAdapter.getRole(request).apply {
            return when(this.status) {
                ActionStatus.SUCCESS -> this.result
                ActionStatus.NULL_OR_EMPTY_FIELD,
                ActionStatus.ROLE_NOT_EXIST -> {
                    commander?.let {
                        CommandSyntaxHandler.sendCommandSyntax(it, this.displayMessage!!)
                    }

                    null
                }
                ActionStatus.FAILURE -> {
                    commander?.let {
                        CommandSyntaxHandler.sendCommandSyntax(it, "Error!")
                    }

                    throw this.neonException!!
                }

                else -> null
            }
        }
    }
}