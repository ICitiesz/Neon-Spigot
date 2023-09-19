package com.islandstudio.neon.stable.core.network

import com.islandstudio.neon.stable.primary.nCommand.nCommandList.NCommandList
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.secondary.nBundle.NBundle
import com.islandstudio.neon.stable.secondary.nDurable.NDurable
import com.islandstudio.neon.stable.utils.reflection.NMSRemapped
import io.netty.channel.Channel
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import org.bukkit.World
import org.bukkit.entity.Player
import java.lang.reflect.Field

object NPacketProcessor {
    private val plugin = NConstructor.plugin

    /**
     * Get Minecraft-based player
     *
     * @param player The target player (Bukkit)
     * @return Minecraft-based player
     */
    fun getNPlayer(player: Player): ServerPlayer = player::class.java.getMethod("getHandle").invoke(player)!! as ServerPlayer

    /**
     * Get Minecraft-based world
     *
     * @param world The target world (Bukkit)
     * @return Minecraft-based world
     */
    fun getNWorld(world: World): ServerLevel = world::class.java.getMethod("getHandle").invoke(world) as ServerLevel

    /**
     * Send game packet to player
     *
     * @param player The target player to send.
     * @param gamePacket The game packet
     */
    fun sendGamePacket(player: Player, gamePacket: Packet<*>) {
        val nPlayer = getNPlayer(player)

        val playerConnection = nPlayer.javaClass.getField(NMSRemapped.Mapping.NMS_PLAYER_CONNECTION.remapped).get(nPlayer)

        playerConnection.javaClass.getMethod(NMSRemapped.Mapping.NMS_SEND_PACKET.remapped, Packet::class.java).invoke(playerConnection, gamePacket)
    }

    /**
     * Get channel handler that used to handle listening incoming/outgoing packets.
     *
     * @param nPlayer The target Minecraft-based player
     * @return Channel handler
     */
    private fun getChannelHandler(nPlayer: ServerPlayer): ChannelDuplexHandler {
        val channelHandler = object : ChannelDuplexHandler() {
            override fun channelRead(ctx: ChannelHandlerContext?, gamePacket: Any?) {
                when (gamePacket) {
                    is ServerboundContainerButtonClickPacket -> {
                        NCommandList.navigateCommandUI(gamePacket.buttonId, nPlayer)
                    }
                }

                super.channelRead(ctx, gamePacket)
            }

            override fun write(ctx: ChannelHandlerContext?, gamePacket: Any?, cPromise: ChannelPromise?) {
                when (gamePacket) {
                    is ClientboundContainerSetSlotPacket -> {
                        val bukkitPlayer = nPlayer.javaClass.getMethod(NMSRemapped.Mapping.NMS_GET_BUKKIT_ENTITY.remapped)
                            .invoke(nPlayer) as Player

                        NDurable.Handler.applyDamagePropertyOnGive(nPlayer, gamePacket.slot)
                        NBundle.discoverBundleRecipe(bukkitPlayer, gamePacket.slot)
                    }
                }

                super.write(ctx, gamePacket, cPromise)
            }
        }

        return channelHandler
    }

    /**
     * Add game packet listener when the player joins the server.
     *
     * @param player The target player who joins the server.
     */
    fun addGamePacketListener(player: Player) {
        val channel = getChannel(player)

        channel.pipeline().addBefore("packet_handler", player.name, getChannelHandler(getNPlayer(player)))
    }

    /**
     * Remove game packet listener when the player leave the server.
     *
     * @param player The target player who leaves the server
     */
    fun removeGamePacketListener(player: Player) {
        val channel = getChannel(player)

        channel.eventLoop().submit {
            channel.pipeline().remove(player.name)
        }
    }

    /**
     * Reload game packet listener after server reload.
     *
     * @param player The target player
     */
    fun reloadGamePacketListener(player: Player) {
        val channel = getChannel(player)

        channel.eventLoop().submit {
            channel.pipeline().replace(player.name, player.name, getChannelHandler(getNPlayer(player)))
        }
    }

    /**
     * Get channel listening incoming/outgoing packets.
     *
     * @param player
     * @return
     */
    private fun getChannel(player: Player): Channel {
        val nPlayer = getNPlayer(player)

        val playerConnection = nPlayer.javaClass.getField(NMSRemapped.Mapping.NMS_PLAYER_CONNECTION.remapped).get(nPlayer)

        val networkManagerField: Field = playerConnection.javaClass.getDeclaredField(NMSRemapped.Mapping.NMS_NETWORK_MANAGER.remapped)
        networkManagerField.isAccessible = true

        val networkManager = networkManagerField.get(playerConnection)

        return networkManager.javaClass.getField(NMSRemapped.Mapping.NMS_CHANNEL.remapped).get(networkManager) as Channel
    }
}