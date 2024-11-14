package com.islandstudio.neon.stable.core.command.properties

enum class CommandArgument(val argName: String) {
    CREATE("create"),
    REMOVE("remove"),
    ALL("all"),
    PAINTING_REMOVAL_STICK("removalStick"),
    CONFIRM("CONFIRM"),
    RELOAD("reload"),
    GET("get"),
    SET("set"),
    DEFAULT("default"),

    /* nRole Command Arguments */
    ASSIGN("assign"),
    UNASSIGN("unassign"),

    /* nPermision Command Arguments */
    GRANT("grant"),
    REVOKE("revoke"),
    UPDATE_ACCESS_TYPE("updateAccessType"),

    /* nDurable Command Arguments */
    REMOVE_DAMAGE_PROPERTY("removeDamageProperty"),

    /* nServerFeatures Command Arguments */
    TOGGLE("toggle"),
    OPTION("option")
    ;

    companion object {
        fun valueOfCommandArgument(value: String): CommandArgument? {
            return CommandArgument.entries.find { it.argName.equals(value, true) }
        }
    }
}