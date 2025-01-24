package com.islandstudio.neon.stable.core.application.server

import com.islandstudio.neon.stable.core.application.reflection.NmsProcessor
import com.islandstudio.neon.stable.core.application.reflection.mapping.NmsMap
import com.islandstudio.neon.stable.core.command.commandlist.NCommandList
import com.islandstudio.neon.stable.features.nBundle.NBundle
import com.islandstudio.neon.stable.features.nDurable.NDurable
import io.netty.channel.Channel
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import org.bukkit.World
import org.bukkit.entity.Player

object ServerGamePacketManager {
    private val nmsProcessor = NmsProcessor()

    /**
     * Get Minecraft-based player
     *
     * @param player The target Bukkit player
     * @return Minecraft-based player
     */
    fun getMcPlayer(player: Player): ServerPlayer {
        return player.javaClass.getMethod("getHandle").invoke(player) as ServerPlayer
    }

    /**
     * Get Minecraft-based world
     *
     * @param world The target Bukkit world
     * @return Minecraft-based world
     */
    fun getMcWorld(world: World): ServerLevel {
        return world.javaClass.getMethod("getHandle").invoke(world) as ServerLevel
    }

    /**
     * Send server game packet to player
     *
     * @param player The target player to send.
     * @param serverGamePacket The game packet
     */
    fun sendServerGamePacket(player: Player, serverGamePacket: Any) {
        val mcPlayer = getMcPlayer(player)

        val playerConnection = mcPlayer.javaClass.getField(NmsMap.PlayerConnection.remapped).get(mcPlayer)
        val mcPacketClass = nmsProcessor.getMcClass("network.protocol.${NmsMap.Packet.remapped}")

        nmsProcessor.getMcClass("server.network.ServerCommonPacketListenerImpl")?.let {
            playerConnection.javaClass.superclass.getMethod(NmsMap.SendPacket.remapped, mcPacketClass).invoke(playerConnection, serverGamePacket)
        } ?: playerConnection.javaClass.getMethod(NmsMap.SendPacket.remapped, mcPacketClass).invoke(playerConnection, serverGamePacket)
    }

    /**
     * Register server game packet listener to player when they join the server
     *
     * @param player The target player who join the server.
     */
    fun registerServerGamePacketListener(player: Player) {
        val serverGamePacketChannel = getServerGamePacketChannel(player)

        serverGamePacketChannel.pipeline().addBefore(
            "packet_handler",
            player.name,
            getServerGamePacketChannelHandler(getMcPlayer(player))
        )
    }

    /**
     * Unregister server game packet listener from the player when they leave the server
     *
     * @param player The target player who leaves the server
     */
    fun unregisterServerGamePacketListener(player: Player) {
        val serverGamePacketChannel = getServerGamePacketChannel(player)

        serverGamePacketChannel.eventLoop().submit {
            serverGamePacketChannel.pipeline().remove(player.name)
        }
    }

    /**
     * Reload server game packet listener after server reloaded
     *
     * @param player The target player
     */
    fun reloadServerGamePacketListener(player: Player) {
        val serverGamePacketChannel = getServerGamePacketChannel(player)

        serverGamePacketChannel.eventLoop().submit {
            serverGamePacketChannel.pipeline().replace(
                player.name,
                player.name,
                getServerGamePacketChannelHandler(getMcPlayer(player))
            )
        }
    }

    /**
     * Get server game packet channel to listen incoming/outgoing packets.
     *
     * @param player
     * @return
     */
    private fun getServerGamePacketChannel(player: Player): Channel {
        val mcPlayer = getMcPlayer(player)
        val playerConnection = mcPlayer.javaClass.getField(NmsMap.PlayerConnection.remapped).get(mcPlayer)

        val networkManagerField = nmsProcessor.getMcClass("server.network.ServerCommonPacketListenerImpl")?.let {
            playerConnection.javaClass.superclass.getDeclaredField(NmsMap.NetworkManager.remapped)
        } ?: playerConnection.javaClass.getDeclaredField(NmsMap.NetworkManager.remapped)

        networkManagerField.isAccessible = true

        val networkManager = networkManagerField.get(playerConnection)

        return networkManager.javaClass.getField(NmsMap.Channel.remapped).get(networkManager) as Channel
    }

    /**
     * Get server game packet channel handler that used to handle listening incoming/outgoing packets.
     *
     * @param mcPlayer The target Minecraft-based player
     * @return Server game packet channel handler
     */
    private fun getServerGamePacketChannelHandler(mcPlayer: ServerPlayer): ChannelDuplexHandler {
        return object : ChannelDuplexHandler() {
            /* Packet Send: Client -> Server */
            override fun channelRead(handlerContext: ChannelHandlerContext?, serverGamePacket: Any?) {
                when (serverGamePacket) {
                    is ServerboundContainerButtonClickPacket -> {
                        val nmsButtonID = serverGamePacket.javaClass.getDeclaredField(NmsMap.ButtonId.remapped).run {
                            this.isAccessible = true
                            this.getInt(serverGamePacket)
                        }

                        NCommandList.navigateCommandUI(nmsButtonID, mcPlayer)
                    }
                }

                super.channelRead(handlerContext, serverGamePacket)
            }

            /* Packet Send: Server -> Client */
            override fun write(handlerContext: ChannelHandlerContext?, serverGamePacket: Any?, promise: ChannelPromise?) {
                when (serverGamePacket) {
                    is ClientboundContainerSetSlotPacket -> {
                        val bukkitPlayer = mcPlayer.javaClass.getMethod(NmsMap.GetBukkitEntity.remapped)
                            .invoke(mcPlayer) as Player

                        val mcItemStack = serverGamePacket.javaClass.getMethod(NmsMap.GetSetSlotItemStack.remapped)
                            .invoke(serverGamePacket)

                        NDurable.Handler.applyDamagePropertyOnGive(mcItemStack as ItemStack)
                        NBundle.discoverBundleRecipe(bukkitPlayer, mcItemStack)
                    }
                }

                super.write(handlerContext, serverGamePacket, promise)
            }
        }
    }
}