package com.islandstudio.neon.stable.primary.nProfile

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nFolder.FolderList
import com.islandstudio.neon.stable.primary.nFolder.NFolder
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.*
import java.util.*

object NProfile {
    private val playerSession: HashMap<UUID, PlayerProfile> = HashMap()

    fun addPlayerSession(player: Player) {
        //playerSession[player.uniqueId] =
    }

    fun discardPlayerSession(player: Player) {
        playerSession.remove(player.uniqueId)
    }

    fun getPlayerSession(player: Player): PlayerProfile {
        return playerSession[player.uniqueId]!!
    }

    object Handler {
        private val classLoader: ClassLoader = NProfile::class.java.classLoader

        private val jsonParser: JSONParser = JSONParser()
        private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

        /**
         * Initialization for nProfile
         *
         */
        fun run() {
            NConstructor.registerEventProcessor(EventProcessor())
        }

        /**
         * Create player profile when player join the server.
         *
         * @param player The player who join the server.
         */
        fun createPlayerProfile(player: Player) {
            val playerProfileFolder: File = getPlayerProfileFolder(player)
            val playerProfileFile: File = getPlayerProfileFile(player)

            NFolder.createNewFile(playerProfileFolder, playerProfileFile)

            val externalBufferedReader = playerProfileFile.bufferedReader()
            val externalLinesCount: Long = externalBufferedReader.lines().count()

            externalBufferedReader.close()

            /* Check if the file content lines is equal to 0 */
            if (externalLinesCount == 0L) {
                val externalBufferedWriter = playerProfileFile.bufferedWriter()

                val profileElement: JSONObject = getInternalProfileElement()

                profileElement.replace("Name", player.name)
                profileElement.replace("UUID", player.uniqueId.toString())

                if (player.isOp) profileElement.replace("Rank", "OWNER")

                externalBufferedWriter.write(gson.toJson(profileElement))
                externalBufferedWriter.close()

                return
            }

            updateProfileElement(player)
        }

        /* Set value for specify field in the player profile */
        fun setValue(player: Player, fieldName: String, value: String) {
            val playerProfileData: JSONObject = getProfileData(player)
            val fileOutputStream = FileOutputStream(getPlayerProfileFile(player))
            val bufferedWriter = BufferedWriter(OutputStreamWriter(fileOutputStream))

            if (playerProfileData[fieldName]!! == value) return

            playerProfileData.replace(fieldName, value)

            bufferedWriter.write(gson.toJson(playerProfileData))
            bufferedWriter.close()
        }

        /* Get profile data */
        fun getProfileData(player: Player): JSONObject {
            val fileReader = FileReader(getPlayerProfileFile(player))
            val playerProfileData: JSONObject = jsonParser.parse(fileReader) as JSONObject
            fileReader.close()

            return playerProfileData
        }

        /**
         * Update profile element if there is a profile element newly added or missing.
         *
         * @param player The given player.
         */
        private fun updateProfileElement(player: Player) {
            val playerProfileFile = getPlayerProfileFile(player)

            val internalProfileElement: JSONObject = getInternalProfileElement()
            val externalProfileElement: JSONObject = jsonParser.parse(playerProfileFile.reader()) as JSONObject

            internalProfileElement.keys.forEach { key ->
                if (!externalProfileElement.containsKey(key)) externalProfileElement.putIfAbsent(key, internalProfileElement[key])
            }

            externalProfileElement.keys.removeIf { key -> !internalProfileElement.containsKey(key) }

            val externalBufferedWriter = playerProfileFile.bufferedWriter()

            externalBufferedWriter.write(gson.toJson(externalProfileElement))
            externalBufferedWriter.close()
        }

        /**
         * Get player profile file.
         *
         * @param player The given player.
         * @return The given player profile file.
         */
        private fun getPlayerProfileFile(player: Player): File {
            return File(getPlayerProfileFolder(player), "profile_${player.uniqueId}.json")
        }

        /**
         * Get profile element from internal source.
         *
         * @return Internal profile elements as Json Object.
         */
        private fun getInternalProfileElement(): JSONObject {
            val stringBuilder: StringBuilder = StringBuilder()
            val inputStream: InputStream = classLoader.getResourceAsStream("resources/player_profile.json")!!
            val internalBufferedReader = BufferedReader(InputStreamReader(inputStream))

            val internalProfileElement: Array<Any> = internalBufferedReader.lines()!!.toArray()

            internalProfileElement.forEach { stringBuilder.append(it) }

            return jsonParser.parse(stringBuilder.toString()) as JSONObject
        }

        /**
         * Get player profile folder
         *
         * @param player The given player.
         * @return The given player profile folder.
         */
        private fun getPlayerProfileFolder(player: Player): File {
            return File(FolderList.NPROFILE.folder, "player_${player.uniqueId}")
        }
    }

    private class EventProcessor: Listener {
        @EventHandler
        private fun onPlayerJoin(e: PlayerJoinEvent) {
            val player = e.player

            Handler.createPlayerProfile(player)

            //addPlayerSession(player)
        }

        @EventHandler
        private fun onPlayerQuit(e: PlayerQuitEvent) {
            //discardPlayerSession(e.player)
        }
    }
}