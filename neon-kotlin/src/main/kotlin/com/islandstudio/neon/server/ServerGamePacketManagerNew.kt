package com.islandstudio.neon.server

import com.islandstudio.neon.core.nmsmapping.NmsManagerNew
import com.islandstudio.neon.core.nmsmapping.type.NmsClass
import com.islandstudio.neon.core.nmsmapping.type.NmsField
import com.islandstudio.neon.core.nmsmapping.type.NmsMethod
import com.islandstudio.neon.shared.utils.data.DataUtil
import io.netty.channel.Channel
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.network.Connection
import org.bukkit.entity.Player

object ServerGamePacketManagerNew: NmsManagerNew.INmsMapper {
    private const val COMMON_PLAYER_CONNECTION_CLASS_NAME = "server.network.ServerCommonPacketListenerImpl"

    fun registerServerGamePacketListener(player: Player) {
        val serverGamePacketChannel = getServerGamePacketChannel(player)

        serverGamePacketChannel.eventLoop().submit {
            serverGamePacketChannel.pipeline().addBefore(
                "packet_handler",
                player.name,
                ServerChannelDuplexHandler(player)
            )
        }
    }

    fun unregisterServerGamePacketListener(player: Player) {
        val serverGamePacketChannel = getServerGamePacketChannel(player)

        serverGamePacketChannel.eventLoop().submit {
            serverGamePacketChannel.pipeline().remove(player.name)
        }
    }

    fun reloadServerGamePacketListener(player: Player) {
        val serverGamePacketChannel = getServerGamePacketChannel(player)

        serverGamePacketChannel.eventLoop().submit {
            serverGamePacketChannel.pipeline().replace(
                player.name,
                player.name,
                ServerChannelDuplexHandler(player)
            )
        }
    }

    fun sendServerGamePacket(player: Player, serverGamePacket: Any) {
        val nmsPlayer = NmsManagerNew.toNmsPlayer(player)
        val packetClass = NmsManagerNew.getNmsClass("network.protocol.${mapClass(NmsClass.Packet)}")
        val playerConnection = nmsPlayer.javaClass.getField(mapField(NmsField.PlayerConnection)).get(nmsPlayer)

        getPlayerConnectionClass(player).getMethod(mapMethod(NmsMethod.SendPacket), packetClass)
            .invoke(playerConnection, serverGamePacket)
    }

    private fun getPlayerConnectionClass(player: Player): Class<*> {
        val nmsPlayer = NmsManagerNew.toNmsPlayer(player)
        val playerConnection = nmsPlayer.javaClass.getField(mapField(NmsField.PlayerConnection)).get(nmsPlayer)

        return NmsManagerNew.getNmsClass(COMMON_PLAYER_CONNECTION_CLASS_NAME)?.let {
            playerConnection.javaClass.superclass
        } ?: playerConnection.javaClass
    }

    private fun getServerGamePacketChannel(player: Player): Channel {
        return getNetworkManager(player).run {
            DataUtil.asType(this.javaClass.getField(mapField(NmsField.Channel)).get(this))
        }
    }

    private fun getNetworkManager(player: Player): Connection {
        val nmsPlayer = NmsManagerNew.toNmsPlayer(player)
        val playerConnection = nmsPlayer.javaClass.getField(mapField(NmsField.PlayerConnection)).get(nmsPlayer)

        return DataUtil.asType(getPlayerConnectionClass(player).getDeclaredField(mapField(NmsField.NetworkManager)).run {
            this.isAccessible = true
            this.get(playerConnection)
        })
    }

    class ServerChannelDuplexHandler(private val player: Player): ChannelDuplexHandler() {
        /* Packet Send: Client -> Server */
        /**
         * Channel read
         *
         * @param ctx The channel handler context
         * @param msg The server game packet
         */
        override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {

            super.channelRead(ctx, msg)
        }

        /* Packet Send: Server -> Client */
        /**
         * Write
         *
         * @param ctx The channel handler context
         * @param msg The server game packet
         * @param promise
         */
        override fun write(ctx: ChannelHandlerContext?, msg: Any?, promise: ChannelPromise?) {
            super.write(ctx, msg, promise)
        }
    }
}