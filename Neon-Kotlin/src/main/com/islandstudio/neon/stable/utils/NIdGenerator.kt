package com.islandstudio.neon.stable.utils

import java.util.*

object NIdGenerator {
    enum class NIdType {
        COMMAND_LIST_SESSION
    }

    fun generateNId(nIdType: NIdType): String {
        val uuid = UUID.randomUUID()

        when (nIdType) {
            NIdType.COMMAND_LIST_SESSION -> {
                return "cmdList_session.$uuid"
            }
        }
    }

    /* Unused */
    fun generateNIdWithValidation() {

    }

    fun generatePlayerSessionId(playerUUID: UUID): String {
        return "${UUID.randomUUID()}.${playerUUID}"
    }
}