package com.islandstudio.neon.stable.primary.nProfile

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.islandstudio.neon.stable.primary.nFolder.NFolder
import com.islandstudio.neon.stable.primary.nFolder.FolderList
import org.bukkit.entity.Player
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.*
import java.util.stream.Collectors

data class NProfile(val player: Player) {
    private val playerProfile = Handler.getProfileData(player)

    val playerName: String = playerProfile["Name"] as String
    val playerUUID: String = playerProfile["UUID"] as String
    val playerRank: String = (playerProfile["Rank"] as String).lowercase()
    val playerIsMuted: Boolean = playerProfile["isMuted"] as Boolean

    object Handler {
        private val classLoader: ClassLoader = NProfile::class.java.classLoader

        private val jsonParser: JSONParser = JSONParser()
        private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

        fun createProfile(player: Player) {
            createNewFiles(player)

            val fileReader = FileReader(getPlayerProfile(player))
            val clientBufferedReader = BufferedReader(fileReader)
            val clientProfileElementSize: Long = clientBufferedReader.lines().count()

            fileReader.close()
            clientBufferedReader.close()

            if (clientProfileElementSize == 0L) {
                val clientFileOutputStream = FileOutputStream(getPlayerProfile(player))
                val clientBufferedWriter = BufferedWriter(OutputStreamWriter(clientFileOutputStream))

                val profileElement: JSONObject = getSourceProfileElement()

                profileElement.replace("Name", player.name)
                profileElement.replace("UUID", player.uniqueId.toString())

                if (player.isOp) {
                    profileElement.replace("Rank", "OWNER")
                }

                clientBufferedWriter.write(gson.toJson(profileElement))
                clientBufferedWriter.close()
                clientFileOutputStream.close()

                return
            }

            updateProfileElement(player)
        }

        /* Set value for specify field in the player profile */
        fun setValue(player: Player, fieldName: String, value: String) {
            val playerProfileData: JSONObject = getProfileData(player)
            val fileOutputStream = FileOutputStream(getPlayerProfile(player))
            val bufferedWriter = BufferedWriter(OutputStreamWriter(fileOutputStream))

            if (playerProfileData[fieldName]!! == value) return

            playerProfileData.replace(fieldName, value)

            bufferedWriter.write(gson.toJson(playerProfileData))
            bufferedWriter.close()
        }

        /* Get profile data */
        fun getProfileData(player: Player): JSONObject {
            val fileReader = FileReader(getPlayerProfile(player))
            val playerProfileData: JSONObject = jsonParser.parse(fileReader) as JSONObject
            fileReader.close()

            return playerProfileData
        }

        /* Update profile element */
        private fun updateProfileElement(player: Player) {
            val fileReader = FileReader(getPlayerProfile(player))
            val sourceProfileElement: JSONObject = getSourceProfileElement()
            val clientProfileElement: JSONObject = jsonParser.parse(fileReader) as JSONObject

            sourceProfileElement.keys.forEach { key ->
                if (!clientProfileElement.containsKey(key)) clientProfileElement.putIfAbsent(key, sourceProfileElement[key])
            }

            clientProfileElement.keys.removeIf { key -> !sourceProfileElement.containsKey(key) }

            val clientFileOutputStream = FileOutputStream(getPlayerProfile(player))
            val clientBufferedWriter = BufferedWriter(OutputStreamWriter(clientFileOutputStream))

            clientBufferedWriter.write(gson.toJson(clientProfileElement))

            clientBufferedWriter.close()
            clientFileOutputStream.close()

            fileReader.close()
        }

        /* Get player profile */
        private fun getPlayerProfile(player: Player): File {
            return File(getPlayerFolder(player), "profile_" + player.uniqueId + ".json")
        }

        /* Get profile element from source */
        private fun getSourceProfileElement(): JSONObject {
            val stringBuilder: StringBuilder = StringBuilder()
            val inputStream: InputStream = classLoader.getResourceAsStream("resources/profile_.json")!!
            val sourceBufferedReader = BufferedReader(InputStreamReader(inputStream))

            val sourceProfileElement: Array<Any> = sourceBufferedReader.lines()!!.toArray()

            sourceProfileElement.forEach { content ->
                stringBuilder.append(content)
            }

            return jsonParser.parse(stringBuilder.toString()) as JSONObject
        }

        /* Get player folder */
        private fun getPlayerFolder(player: Player): File {
            return File(NFolder.getDataFolder(),
                NFolder.getVersion() + "/" + NFolder.getMode()
                        + "/server_data/players/player_" + player.uniqueId)
        }

        /* Create required folders and files */
        private fun createNewFiles(player: Player) {
            val mainFolder: File = FolderList.FOLDER_B.folder
            val playerFolder: File = getPlayerFolder(player)
            val playerProfile: File = getPlayerProfile(player)

            if (!mainFolder.exists()) {
                mainFolder.mkdirs()
            }

            if (!playerFolder.exists()) {
                playerFolder.mkdirs()
            }

            if (!playerProfile.exists()) {
                playerProfile.createNewFile()
            }
        }
    }
}