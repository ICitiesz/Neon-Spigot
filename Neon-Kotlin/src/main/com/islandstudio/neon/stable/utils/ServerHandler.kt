package com.islandstudio.neon.stable.utils

import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerPlayerConnection
import net.minecraft.world.item.crafting.Recipe
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object ServerHandler {
    // Future Improvement: TODO: Add a prefix '[Neon]' to the front of join and quit messages

    fun broadcastJoinMessage(e: PlayerJoinEvent) {
        val player: Player = e.player
        val server: Server = player.server

        e.joinMessage = ""

        server.broadcastMessage(ChatColor.GOLD.toString() + "Welcome back, " + ChatColor.GREEN + player.name + ChatColor.GOLD + "!")
        server.broadcastMessage(ChatColor.GREEN.toString() + "" + server.onlinePlayers.size + ChatColor.GOLD + " of " + ChatColor.RED + Bukkit.getMaxPlayers() + ChatColor.GOLD + " player(s) Online!")
    }

    fun broadcastQuitMessage(e: PlayerQuitEvent) {
        val player: Player = e.player
        val server: Server = player.server

        e.quitMessage = ""

        server.broadcastMessage(ChatColor.GREEN.toString() + player.name + ChatColor.GOLD + " left," + ChatColor.GREEN + " " + (server.onlinePlayers.size - 1) + ChatColor.GOLD + " other(s) here!")
    }

    @Suppress("UNCHECKED_CAST")
    fun updateRecipe(player: Player) {
        val handle: Any = player.javaClass.getMethod("getHandle").invoke(player)!!
        val serverRecipes: Map<Any, Map<Any, Any>>

        when (NConstructor.getVersion()) {
            "1.17" -> {
                serverRecipes = ((handle as ServerPlayer).server!!).recipeManager!!.recipes!! as Map<Any, Map<Any, Any>>

                (handle.connection!! as ServerPlayerConnection).send(ClientboundUpdateRecipesPacket(
                    serverRecipes.values.parallelStream().flatMap { map -> map.values.parallelStream() }.toList()!! as MutableCollection<Recipe<*>>
                ))
            }

            "1.18" -> {
                val craftingManager: Any = ((handle as ServerPlayer).server!!).javaClass.getMethod("aC").invoke(handle.server!!)!!

                serverRecipes = craftingManager.javaClass.getField("c").get(craftingManager)!! as Map<Any, Map<Any, Any>>

                handle.connection!!.send(ClientboundUpdateRecipesPacket(
                    serverRecipes.values.parallelStream().flatMap { map -> map.values.parallelStream() }.toList()!! as MutableCollection<Recipe<*>>
                ))
            }
        }
    }
}