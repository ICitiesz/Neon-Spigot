package com.islandstudio.neon.stable.secondary.nRank

import com.islandstudio.neon.Main
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nProfile.NProfile
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundChatPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerPlayerConnection
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import java.util.*

object NRank {
    private val plugin: Plugin = getPlugin(Main::class.java)
    private val scoreboard: Scoreboard = plugin.server.scoreboardManager!!.newScoreboard

    /* Initialization */
    fun run() {
        for (ranks in RankList.values()) {
            val team: Team = scoreboard.registerNewTeam(ranks.name)
            team.prefix = ranks.tagPrefix
        }
    }

    /* Update tag */
    fun updateTag() {
        plugin.server.onlinePlayers.forEach { target ->
            val playerProfile = NProfile(target)
            val playerRank: String = playerProfile.playerRank

            if (RankList.values().none { ranks: RankList -> playerRank.equals(ranks.name.lowercase(), true) }) return

            scoreboard.getTeam(playerRank.uppercase())!!.addEntry(target.name)
            target.scoreboard = scoreboard
        }
    }

    /* Send message with rank prefix in front of player name */
    fun sendMessage(player: Player, message: String) {
        val rank: String = NProfile.Handler.getProfileData(player)["Rank"] as String

        if (RankList.values().none { rank.equals(it.name, true) }) return

        plugin.server.onlinePlayers.parallelStream().forEach { onlinePlayers ->
            processMessage(RankList.valueOf(rank.uppercase()).tagPrefix, player.name, message, onlinePlayers)
        }
    }

    /* Set command handler to handle the command execution */
    fun setCommandHandler(commander: Player, args: Array<out String>, pluginName: String) {
        if (!commander.isOp) {
            commander.sendMessage(pluginName + ChatColor.RED.toString() + "You don't have permission to execute this command!")
            return
        }

        val onlinePlayer: Collection<Player> = plugin.server.onlinePlayers

        when (args.size) {
            3 -> {
                val playerName: String = args[2]

                /* Check if the option is 'remove' */
                if (!args[1].equals("remove", true)) {
                    commander.sendMessage(pluginName + ChatColor.RED + "Invalid or missing argument!")
                }

                /* Check if the given player name is exist */
                if (onlinePlayer.parallelStream().noneMatch { player: Player -> player.name.equals(playerName, true) }) {
                    commander.sendMessage(pluginName + ChatColor.RED + "No such player as '" + ChatColor.WHITE + playerName + ChatColor.RED + "'!")
                }

                removePlayerRank(commander, playerName, pluginName)
            }

            4 -> {
                val playerName: String = args[2]
                val rankName: String = args[3]

                /* Check if the option is 'set' */
                if (!args[1].equals("set", true)) {
                    commander.sendMessage(pluginName + ChatColor.RED + "Invalid or missing argument!")
                }

                /* Check if the given player name is exist */
                if (onlinePlayer.parallelStream().noneMatch { player: Player -> player.name.equals(playerName, true) }) {
                    commander.sendMessage(pluginName + ChatColor.RED + "No such player as '" + ChatColor.WHITE + playerName + ChatColor.RED + "'!")
                }

                /* Check if the given rank name is existed */
                if (RankList.values().none { ranks: RankList -> rankName.equals(ranks.name, true) }) {
                    commander.sendMessage(pluginName + ChatColor.RED + "No such rank as '" + ChatColor.WHITE + rankName + ChatColor.RED + "'!")
                }

                setPlayerRank(commander, rankName, playerName, pluginName)
            }

            else -> {
                commander.sendMessage(pluginName + ChatColor.RED + "Invalid or missing argument!")
            }
        }
    }

    fun tabCompletion(player: Player, args: Array<out String>): MutableList<String>? {
        if (!player.isOp) return null

        when (args.size) {
            2 -> {
                return listOf("set", "remove").toMutableList()
            }

            3 -> {
                if (args[1].equals("set", true) || args[1].equals("remove", true)) {
                    return plugin.server.onlinePlayers.parallelStream().map { target: Player -> target.name }.toList().toMutableList()
                }
            }

            4 -> {
                if (args[1].equals("set", true) && args[2].isNotEmpty()) {
                    return RankList.values().map { rank: RankList -> rank.name }.toList().toMutableList()
                }
            }
        }

        return null
    }

    /* Set player rank */
    private fun setPlayerRank(commander: Player, rankName: String, playerName:String, pluginName: String) {
        val target: Player = plugin.server.getPlayer(playerName)!!

        /* Check if the given rankName matches the 'Rank' field in the player profile.  */
        if (!rankName.equals(NProfile.Handler.getProfileData(target)["Rank"] as String, true)) {

            /* Check if the given rankName is 'OWNER', and check if the target player is server operator */
            if (rankName.equals(RankList.OWNER.name, true) && !target.isOp) {
                commander.sendMessage(pluginName + ChatColor.YELLOW + "The '" + ChatColor.GRAY + rankName.uppercase() + ChatColor.YELLOW + "' rank is unavailable for this player.")
                return
            }

            NProfile.Handler.setValue(target, "Rank", rankName.uppercase())
            updateTag()
            commander.sendMessage(pluginName + ChatColor.GREEN + "Rank successfully set!")
            return
        }

        commander.sendMessage(
            pluginName + ChatColor.RED + "The player " + ChatColor.WHITE + playerName + ChatColor.RED
                    + " already has the '" + ChatColor.GRAY + rankName.uppercase() + ChatColor.RED + "' rank!"
        )
    }

    /* Remove player rank */
    private fun removePlayerRank(commander: Player, playerName: String, pluginName: String) {
        val target: Player = plugin.server.getPlayer(playerName)!!
        val playerRank: String = NProfile.Handler.getProfileData(target)["Rank"] as String

        if (playerRank.equals(RankList.MEMBER.name, true)) {
            commander.sendMessage(pluginName + ChatColor.YELLOW + "Minimum rank must be a Member!")
            return
        }

        NProfile.Handler.setValue(target, "Rank", RankList.MEMBER.name)
        updateTag()

        commander.sendMessage(pluginName + ChatColor.RED+ "The '" + ChatColor.GRAY + playerRank.uppercase() + ChatColor.RED + "' rank has been removed from '" + ChatColor.GRAY + target.name + ChatColor.RED + "'!")
        target.sendMessage(pluginName + ChatColor.RED + "Your '" + ChatColor.GRAY + playerRank.uppercase() + ChatColor.RED + "' rank has been removed!")
    }

    /* Process message with rank prefix in front of player name */
    private fun processMessage(rank: String, playerName: String, message: String, onlinePlayers: Player) {
        val messagePacket = ClientboundChatPacket(
            Component.Serializer.fromJson("{\"text\":\"$rank${ChatColor.WHITE}$playerName${ChatColor.GRAY} > ${ChatColor.WHITE}${messageFilter(message)}\"}"),
            ChatType.CHAT,
            onlinePlayers.uniqueId
        )

        val handle: Any = onlinePlayers.javaClass.getMethod("getHandle").invoke(onlinePlayers)!!

        when (NConstructor.getVersion()) {
            "1.17" -> {
                ((handle as ServerPlayer).connection!! as ServerPlayerConnection).send(messagePacket)
            }

            "1.18" -> {
                (handle as ServerPlayer).connection!!.send(messagePacket)
            }
        }
    }

    /* Filter out some characters in the message to avoid parsing error */
    private fun messageFilter(message: String): String {
        val subStr: MutableMap<Int, String> = TreeMap()
        val stringBuilder: StringBuilder = StringBuilder()
        var tempMessage: String = message

        val escStr = "\\"

        for (i in message.indices) {
            subStr[i] = message.substring(i, i + 1)
        }

        for (key in subStr.keys) {
            if (subStr[key] == "\"") {
                subStr[key] = escStr + subStr[key]
            }

            if (subStr[key] == "\\") {
                subStr[key] = escStr + subStr[key]
            }

            stringBuilder.append(subStr[key])
            tempMessage = stringBuilder.toString()
        }

        return tempMessage
    }
}