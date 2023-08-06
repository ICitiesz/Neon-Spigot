package com.islandstudio.neon.stable.secondary.nRank

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.primary.nProfile.NProfile
import com.islandstudio.neon.stable.primary.nProfile.PlayerProfile
import com.islandstudio.neon.stable.utils.NPacketProcessor
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundChatPacket
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import java.util.*

object NRank {
    private val plugin: Plugin = getPlugin(Neon::class.java)
    private val scoreboard: Scoreboard = plugin.server.scoreboardManager!!.newScoreboard

    /**
     * Initializes the nRank.
     */
    fun run() {
        for (ranks in RankList.values()) {
            val team: Team = scoreboard.registerNewTeam(ranks.name)
            team.prefix = ranks.tagPrefix
        }
    }

    /**
     * Updates the rank tag of the player.
     */
    fun updateTag() {
        plugin.server.onlinePlayers.forEach { target ->
            val playerProfile = PlayerProfile(target)
            val playerRank: String = playerProfile.playerRank

            if (RankList.values().none { ranks: RankList -> playerRank.equals(ranks.name.lowercase(), true) }) return

            scoreboard.getTeam(playerRank.uppercase())!!.addEntry(target.name)
            target.scoreboard = scoreboard
        }
    }

    /**
     * Add rank prefix in front of their name in the chat.
     *
     * @param player The player who sent the message. (Player)
     * @param message The message was sent. (String)
     *
     * @return The message with the rank prefix in front of the player's name. (String)
     */
    fun sendMessage(player: Player, message: String) {
        val rank: String = NProfile.Handler.getProfileData(player)["Rank"] as String

        if (RankList.values().none { rank.equals(it.name, true) }) return

        plugin.server.onlinePlayers.parallelStream().forEach { onlinePlayers ->
            processMessage(RankList.valueOf(rank.uppercase()).tagPrefix, player.name, message, onlinePlayers)
        }
    }

    /**
     * Sets the command handler to handle the command execution.
     *
     * @param commander The player who execute the command. (Player)
     * @param args The arguments in the command. (String)
     * @param pluginName The plugin name that used in the plugin message. (String)
     */
    fun setCommandHandler(commander: Player, args: Array<out String>, pluginName: String) {
        if (!commander.isOp) return commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)

        val onlinePlayer: Collection<Player> = plugin.server.onlinePlayers

        when (args.size) {
            3 -> {
                val playerName: String = args[2]

                /* Check if the option is 'remove' */
                if (!args[1].equals("remove", true)) return commander
                    .sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)


                /* Check if the given player name is exist */
                if (onlinePlayer.parallelStream().noneMatch { player: Player -> player.name.equals(playerName, true) }) return commander
                    .sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.RED}No such player as '${ChatColor.WHITE}$playerName${ChatColor.RED}'!"))

                removePlayerRank(commander, playerName, pluginName)
            }

            4 -> {
                val playerName: String = args[2]
                val rankName: String = args[3]

                /* Check if the option is 'set' */
                if (!args[1].equals("set", true)) return commander
                    .sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)

                /* Check if the given player name is exist */
                if (onlinePlayer.parallelStream().noneMatch { player: Player -> player.name.equals(playerName, true) }) return commander
                    .sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.RED}No such player as '${ChatColor.WHITE}$playerName${ChatColor.RED}'!"))

                /* Check if the given rank name is existed */
                if (RankList.values().none { ranks: RankList -> rankName.equals(ranks.name, true) }) return commander
                    .sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.RED}No such rank as '${ChatColor.WHITE}$rankName${ChatColor.RED}'!"))

                setPlayerRank(commander, rankName, playerName, pluginName)
            }

            else -> {
                commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
            }
        }
    }

    /**
     * Handle the tab completion for the command.
     *
     * @param player The player who execute the command. (Player)
     * @param args The arguments in the command. (String)
     *
     * @return The list of tab completion. (List<String>)
     */
    fun tabCompletion(player: Player, args: Array<out String>): MutableList<String> {
        if (!player.isOp) return mutableListOf()

        when (args.size) {
            2 -> {
                return listOf("set", "remove").filter { it.startsWith(args[1]) }.toMutableList()
            }

            3 -> {
                if (args[1].equals("set", true) || args[1].equals("remove", true)) {
                    return plugin.server.onlinePlayers.parallelStream().map { target: Player -> target.name }.toList().filter { it.startsWith(args[2]) }.toMutableList()
                }
            }

            4 -> {
                if (!args[1].equals("set", true)) return mutableListOf()

                return RankList.values().map { rank: RankList -> rank.name }.toList().filter { it.startsWith(args[3]) }.toMutableList()
            }
        }

        return mutableListOf()
    }

    /**
     * Set the player rank.
     *
     * @param commander The player who execute the command. (Player)
     * @param rankName The rank name. (String)
     * @param playerName The player name to set. (String)
     * @param pluginName The plugin name that used in the plugin message. (String)
     */
    private fun setPlayerRank(commander: Player, rankName: String, playerName:String, pluginName: String) {
        val target: Player = plugin.server.getPlayerExact(playerName)!!

        /* Check if the given rankName matches the 'Rank' field in the player profile.  */
        if (!rankName.equals(NProfile.Handler.getProfileData(target)["Rank"] as String, true)) {

            /* Check if the given rankName is 'OWNER', and check if the target player is server operator */
            if (rankName.equals(RankList.OWNER.name, true) && !target.isOp) {
                commander.sendMessage(
                    CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}The '${ChatColor.GRAY}${rankName.uppercase()}${ChatColor.YELLOW}' " +
                    "rank is unavailable for this player."))
                return
            }

            NProfile.Handler.setValue(target, "Rank", rankName.uppercase())
            updateTag()
            commander.sendMessage(
                CommandSyntax.createSyntaxMessage("${ChatColor.GREEN}Rank successfully set!"))
            return
        }

        commander.sendMessage(
            CommandSyntax.createSyntaxMessage("${ChatColor.RED}The player ${ChatColor.WHITE}${target.name}${ChatColor.RED} already has the " +
            "'${ChatColor.GRAY}${rankName.uppercase()}${ChatColor.RED}' rank!"))
    }

    /**
     * Remove the player rank.
     *
     * @param commander The player who execute the command. (Player)
     * @param playerName The player name to remove the rank. (String)
     * @param pluginName The plugin name that used in the plugin message. (String)
     */
    private fun removePlayerRank(commander: Player, playerName: String, pluginName: String) {
        val target: Player = plugin.server.getPlayer(playerName)!!
        val playerRank: String = NProfile.Handler.getProfileData(target)["Rank"] as String

        if (playerRank.equals(RankList.MEMBER.name, true)) {
            commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Minimum rank must be a Member!"))
            return
        }

        NProfile.Handler.setValue(target, "Rank", RankList.MEMBER.name)
        updateTag()

        commander.sendMessage(
            CommandSyntax.createSyntaxMessage("${ChatColor.RED}The '${ChatColor.GRAY}${playerRank.uppercase()}${ChatColor.RED}' " +
            "rank has been removed from '${ChatColor.GRAY}${target.name}${ChatColor.RED}'!"))
        target.sendMessage(
            CommandSyntax.createSyntaxMessage("${ChatColor.RED}Your '${ChatColor.GRAY}${playerRank.uppercase()}${ChatColor.RED}' " +
            "rank has been removed!"))
    }

    /**
     * Process message with rank prefix in front of player name.
     *
     * @param rank The rank name. (String)
     * @param message The message to process. (String)
     * @param onlinePlayers The online players. (Player)
     */
    private fun processMessage(rank: String, playerName: String, message: String, onlinePlayers: Player) {
        val messagePacket = ClientboundChatPacket(
            Component.Serializer.fromJson("{\"text\":\"$rank${ChatColor.WHITE}$playerName${ChatColor.GRAY} > ${ChatColor.WHITE}${messageFilter(message)}\"}"),
            ChatType.CHAT,
            onlinePlayers.uniqueId
        )

        NPacketProcessor.sendGamePacket(onlinePlayers, messagePacket)
    }

    /**
     * Filter out some characters in the message to avoid parsing error.
     *
     * @param message The message to filter. (String)
     *
     * @return The filtered message. (String)
     */
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