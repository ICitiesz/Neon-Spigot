package com.islandstudio.neon.stable.core.command.properties

import com.islandstudio.neon.stable.core.database.schema.neon_data.tables.pojos.AccessPermission
import com.islandstudio.neon.stable.player.nAccessPermission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

enum class CommandAlias(val command: Command) {
    NROLE(Command("role", Permission.AP_ACCESS_NROLE).apply {
        this.addCommandArgument(
            CommandArgument.CREATE to Command.ArgumentDetail(
                1,
                hashSetOf(Permission.AccessType.FULL)
            ),

            CommandArgument.REMOVE to Command.ArgumentDetail(
                1,
                hashSetOf(Permission.AccessType.FULL)
            ),

            CommandArgument.ASSIGN to Command.ArgumentDetail(
                1,
                hashSetOf(Permission.AccessType.FULL)
            ),

            CommandArgument.UNASSIGN to Command.ArgumentDetail(
                1,
                hashSetOf(Permission.AccessType.FULL)
            )
        )
    }),

    NACCESS_PERMISSION(Command("permission", Permission.AP_ACCESS_NACCESS_PERMISSION).apply {
        this.addCommandArgument(
            CommandArgument.GRANT to Command.ArgumentDetail(
                1,
                hashSetOf(Permission.AccessType.FULL)
            ),

            CommandArgument.REVOKE to Command.ArgumentDetail(
                1,
                hashSetOf(Permission.AccessType.FULL)
            ),

            CommandArgument.UPDATE_ACCESS_TYPE to Command.ArgumentDetail(
                1,
                hashSetOf(Permission.AccessType.FULL)
            )
        )
    }),

    NWAYPOINTS(Command("waypoints", null)),

    NRANK(Command("rank", null)),

    DEBUG(Command("debug", null)),

    GAMEMODE(Command("gm", null)),

    REGEN(Command("regen", null)),

    NSERVER_FEATURES(Command("serverfeatures", Permission.AP_ACCESS_NSERVER_FEATURE).apply {
        this.addCommandArgument()
    }),

    EFFECT(Command("effect", null)),

    MODERATOR(Command("mod", null)),

    NFIREWORKS(Command("fireworks", null)),

    NDURABLE(Command("durability", Permission.AP_ACCESS_NDURABLE_CONFIG).apply {
        this.addCommandArgument(
            CommandArgument.REMOVE_DAMAGE_PROPERTY to Command.ArgumentDetail(
                1,
                hashSetOf(Permission.AccessType.FULL)
            )
        )
    }),

    NPAINTING(Command("painting", null)),

    DATABASE(Command("db", null)),

    NSERVER_FEATURES_REMASTERED(Command("serverfeaturesRemastered", Permission.AP_ACCESS_NSERVER_FEATURE).apply {
        this.addCommandArgument(
            CommandArgument.RELOAD to Command.ArgumentDetail(1, hashSetOf(Permission.AccessType.FULL)),

            CommandArgument.GET to Command.ArgumentDetail(1, hashSetOf(Permission.AccessType.FULL)),

            CommandArgument.SET to Command.ArgumentDetail(1, hashSetOf(Permission.AccessType.FULL)),

            CommandArgument.TOGGLE to Command.ArgumentDetail(2, hashSetOf(Permission.AccessType.FULL)),

            CommandArgument.OPTION to Command.ArgumentDetail(2, hashSetOf(Permission.AccessType.FULL))
        )
    });

    companion object {
        /**
         * Get command alias
         *
         * @param commander
         * @param refArg
         * @param roleAccessPermission
         * @return
         */
        fun getCommandAlias(commander: CommandSender, refArg: String, roleAccessPermission: List<AccessPermission>? = null): MutableList<String> {
            return when (commander) {
                is ConsoleCommandSender -> {
                    CommandAlias.entries
                        .map { it.command.aliasName }
                        .sorted()
                        .filter { it.startsWith(refArg, true) }
                        .toMutableList()
                }

                is Player -> {
                    CommandAlias.entries
                        .asSequence()
                        .filter {
                            it.command.permission?.let { permission ->
                                return@filter permission.permissionCode in roleAccessPermission!!.map { rap -> rap.permissionCode }
                            }

                            return@filter true
                        }.map { it.command.aliasName }
                        .sorted()
                        .filter { it.startsWith(refArg, true) }
                        .toMutableList()
                }

                else -> { return mutableListOf() }
            }
        }
    }
}