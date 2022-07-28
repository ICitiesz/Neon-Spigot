package com.islandstudio.neon.stable.utils

import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import net.minecraft.network.protocol.Packet
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerPlayerConnection
import org.bukkit.entity.Player

object NPacketProcessor {
    fun getNPlayer(player: Player): ServerPlayer {
        return player::class.java.getMethod("getHandle").invoke(player)!! as ServerPlayer
    }

    fun sendGamePacket(player: Player, gamePacket: Packet<*>) {
        val nPlayer = getNPlayer(player)

        when (NConstructor.getVersion()) {
            "1.17" -> {
                (nPlayer.connection!! as ServerPlayerConnection).send(gamePacket)
            }

            "1.18" -> {
                nPlayer.connection!!.send(gamePacket)
            }
        }
    }
}