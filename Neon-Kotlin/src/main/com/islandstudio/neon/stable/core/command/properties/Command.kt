package com.islandstudio.neon.stable.core.command.properties

import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.RoleAccess
import com.islandstudio.neon.stable.player.nAccessPermission.NAccessPermission
import com.islandstudio.neon.stable.player.nAccessPermission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.util.*

data class Command(
    val aliasName: String,
    val permission: Permission?
) {
    private val enforcePermission: Boolean = permission != null
    private val arguments: EnumMap<CommandArgument, ArgumentDetail> = EnumMap(CommandArgument::class.java)

    data class ArgumentDetail(
        val argIndex: Int,
        val accessTypes: HashSet<Permission.AccessType>?
    )

    fun addCommandArgument(vararg commandArg: Pair<CommandArgument, ArgumentDetail>) {
        arguments.putAll(commandArg)
    }

    /**
     * Get command argument based command sender type as well as the given role access.
     * This function only used in command tab completion functionality.
     *
     * @param commander
     * @param argIndex
     * @param refArg
     * @param ignorecase
     * @param roleAccess
     * @return
     */
    fun getCommandArgument(commander: CommandSender, argIndex: Int, refArg: String, ignorecase: Boolean = true, roleAccess: RoleAccess? = null): MutableList<String> {
        return arguments.entries
            .filter { argEntry ->
                val isSameArgIndex =  argEntry.value.argIndex == argIndex

                /* Split section to process get command argument */
                when (commander) {
                    is Player -> {
                        val argumentAccessType = argEntry.value.accessTypes!!
                            .map { at -> at.toString() }

                        /* Get role access, if null return empty list */
                        roleAccess?.let {
                            /* Check if the command argument require access type */
                            return@filter (isSameArgIndex && it.accessType in argumentAccessType )
                        } ?: return mutableListOf()
                    }

                    is ConsoleCommandSender -> { return@filter isSameArgIndex }

                    else -> { return mutableListOf() }
                }
            }.map { it.key.argName }
            .filter { it.startsWith(refArg, ignorecase) }
            .toMutableList()
    }

    /**
     * Get command argument based on the reference argument value.
     *
     * @param refArg
     * @return
     */
    fun getCommandArgument(refArg: String): CommandArgument? {
        return arguments.keys.find { it.argName.equals(refArg, true) }
    }

    fun getAllCommandArgument(): LinkedList<CommandArgument> {
        return arguments.keys.toCollection(LinkedList())
    }

    fun isArgumentAccessible(commander: CommandSender, commandArg: CommandArgument, roleAccess: RoleAccess?): Boolean {
        arguments[commandArg]?.let {
            when(commander) {
                is Player -> {
                    if (!enforcePermission) return true

                    roleAccess?.let { access ->
                        return access.accessType!! in it.accessTypes!!.map { at -> at.toString() }
                    }

                    return false
                }

                is ConsoleCommandSender -> { return true }
                else -> { return false }
            }
        } ?: return false
    }

    /**
     * Check if the commander (player) have the access to the command.
     *
     * @param commander
     * @param roleAccess
     * @return
     */
    fun isCommandAccessible(commander: CommandSender, roleAccess: RoleAccess?): Boolean {
        when(commander) {
            is ConsoleCommandSender -> {
                return true
            }

            is Player -> {
                if (!enforcePermission) return true

                roleAccess?.let {
                    NAccessPermission.getAccessPermission(it.permissionId!!)?.let { accessPermission ->
                        return accessPermission.permissionCode == permission!!.permissionCode
                    } ?: return false
                } ?: return false
            }
        }

        return false
    }
}
