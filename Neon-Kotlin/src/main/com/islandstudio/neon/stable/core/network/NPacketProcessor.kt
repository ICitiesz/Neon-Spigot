package com.islandstudio.neon.stable.core.network

import com.islandstudio.neon.stable.core.init.NConstructor
import com.islandstudio.neon.stable.primary.nCommand.nCommandList.NCommandList
import com.islandstudio.neon.stable.secondary.nBundle.NBundle
import com.islandstudio.neon.stable.secondary.nDurable.NDurable
import com.islandstudio.neon.stable.utils.reflection.NMSRemapped
import com.islandstudio.neon.stable.utils.reflection.NReflector
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
    fun sendGamePacket(player: Player, gamePacket: Any) {
        val nPlayer = getNPlayer(player)

        val playerConnection = nPlayer.javaClass.getField(NMSRemapped.Mapping.NMS_PLAYER_CONNECTION.remapped).get(nPlayer)

        val nmsPacketClass = NReflector.getNamespaceClass("network.protocol.${NMSRemapped.Mapping.NMS_PACKET.remapped}")

        NReflector.getNamespaceClass("server.network.ServerCommonPacketListenerImpl")?.let {
            playerConnection.javaClass.superclass.getMethod(NMSRemapped.Mapping.NMS_SEND_PACKET.remapped, nmsPacketClass).invoke(playerConnection, gamePacket)
        } ?: playerConnection.javaClass.getMethod(NMSRemapped.Mapping.NMS_SEND_PACKET.remapped, nmsPacketClass).invoke(playerConnection, gamePacket)
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
                        val nmsButtonID = gamePacket::class.java.getDeclaredField(NMSRemapped.Mapping.NMS_BUTTON_ID.remapped).also {
                            it.isAccessible = true
                        }

                        NCommandList.navigateCommandUI(nmsButtonID.getInt(gamePacket), nPlayer)
                    }
                }

                super.channelRead(ctx, gamePacket)
            }

            override fun write(ctx: ChannelHandlerContext?, gamePacket: Any?, cPromise: ChannelPromise?) {
                when (gamePacket) {
                    is ClientboundContainerSetSlotPacket -> {
                        val bukkitPlayer = nPlayer.javaClass.getMethod(NMSRemapped.Mapping.NMS_GET_BUKKIT_ENTITY.remapped)
                            .invoke(nPlayer) as Player

                        val baseItemStack = gamePacket.javaClass.getMethod(NMSRemapped.Mapping.NMS_GET_SET_SLOT_ITEM_STACK.remapped)
                            .invoke(gamePacket)

                        NDurable.Handler.applyDamagePropertyOnGive(baseItemStack as ItemStack)
                        NBundle.discoverBundleRecipe(bukkitPlayer, baseItemStack)
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

        val networkManagerField = NReflector.getNamespaceClass("server.network.ServerCommonPacketListenerImpl")?.let {
            playerConnection.javaClass.superclass.getDeclaredField(NMSRemapped.Mapping.NMS_NETWORK_MANAGER.remapped)
        } ?: playerConnection.javaClass.getDeclaredField(NMSRemapped.Mapping.NMS_NETWORK_MANAGER.remapped)

        networkManagerField.isAccessible = true

        val networkManager = networkManagerField.get(playerConnection)

        return networkManager.javaClass.getField(NMSRemapped.Mapping.NMS_CHANNEL.remapped).get(networkManager) as Channel
    }
}