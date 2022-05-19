package com.islandstudio.neon.experimental.nDisguise

import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.core.Registry
import net.minecraft.network.protocol.game.ClientboundAddMobPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.lang.reflect.Field
import java.lang.reflect.Modifier

object NDisguise {
//    private fun processMessage(rank: String, playerName: String, message: String, onlinePlayers: Player) {
//        val messagePacket = ClientboundChatPacket(
//            Component.Serializer.fromJson("{\"text\":\"$rank${ChatColor.WHITE}$playerName${ChatColor.GRAY} > ${ChatColor.WHITE}${messageFilter(message)}\"}"),
//            ChatType.CHAT,
//            onlinePlayers.uniqueId
//        )
//
//        val handle: Any = onlinePlayers.javaClass.getMethod("getHandle").invoke(onlinePlayers)!!
//
//        when (NConstructor.getVersion()) {
//            "1.17" -> {
//                ((handle as ServerPlayer).connection!! as ServerPlayerConnection).send(messagePacket)
//            }
//
//            "1.18" -> {
//                (handle as ServerPlayer).connection!!.send(messagePacket)
//            }
//        }
//    }

    fun test(player: Player) {
        val handle: Any = player.javaClass.getMethod("getHandle").invoke(player)!!

        val serverPlayer: ServerPlayer = handle as ServerPlayer
        val entity: LivingEntity = serverPlayer

        //println(serverPlayer.connection.craftPlayer.name)
        //println(serverPlayer.connection.craftPlayer)

    }

    fun testPacket(player: Player) {
        val handle: Any = player.javaClass.getMethod("getHandle").invoke(player)!!

        val serverPlayer: ServerPlayer = handle as ServerPlayer

        val addLivingEntityPacket = ClientboundAddMobPacket(serverPlayer)
        val removeEntitiesPacket = ClientboundRemoveEntitiesPacket()
        val intField: Field = removeEntitiesPacket.javaClass.getDeclaredField("a")
        intField.isAccessible = true

        val idField: Field = (serverPlayer as net.minecraft.world.entity.Entity).javaClass.getField("at")
        idField.isAccessible = true
        val id = idField.get(serverPlayer) as Int

        //intField.set(removeEntitiesPacket, intArrayOf(id))
        removeEntitiesPacket.entityIds.add(id)

        //removeEntitiesPacket.entityIds.add((serverPlayer as LivingEntity))

        val field: Field = addLivingEntityPacket.javaClass.getDeclaredField("c")
        field.isAccessible = true
        field.set(addLivingEntityPacket, 107)

        handle.connection!!.send(removeEntitiesPacket)
        handle.connection!!.send(addLivingEntityPacket)

        println("test packet sent!")
    }
}