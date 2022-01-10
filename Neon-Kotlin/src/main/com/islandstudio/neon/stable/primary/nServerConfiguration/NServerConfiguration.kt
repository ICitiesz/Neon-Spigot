package com.islandstudio.neon.stable.primary.nServerConfiguration

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.primary.nFolder.FolderList
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.*

class NServerConfiguration(private val serverConfig: Map.Entry<String, Any>) {
    /* Future usage for GUI */
    val serverConfigName = serverConfig.key
    val serverConfigValue = serverConfig.value

    object Handler {
        private val classLoader: ClassLoader = NServerConfiguration::class.java.classLoader

        private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        private val jsonParser: JSONParser = JSONParser()

        /* Initialization */
        fun run() {
            createNewFiles()

            val fileReader = FileReader(getServerConfigFile())
            val clientBufferedReader = BufferedReader(fileReader)
            val clientNServerConfigurationFileSize: Long = clientBufferedReader.lines().count()

            fileReader.close()
            clientBufferedReader.close()

            if (clientNServerConfigurationFileSize == 0L) {
                val clientFileOutputStream = FileOutputStream(getServerConfigFile())
                val clientBufferedWriter = BufferedWriter(OutputStreamWriter(clientFileOutputStream))

                clientBufferedWriter.write(gson.toJson(getSourceElement()))
                clientBufferedWriter.close()
                clientFileOutputStream.close()

                return
            }

            updateServerConfigElement()
        }

        @Suppress("UNCHECKED_CAST")
        /* Set command handler */
        fun setCommandHandler(commander: Player, args: Array<out String>) {
            if (!commander.isOp) {
                commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
                return
            }

            when (args.size) {
                2 -> {
                    if (!getServerConfig().containsKey(args[1])) {
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, " +
                                "there are no such server config as ${ChatColor.WHITE}'${ChatColor.GRAY}${args[1]}${ChatColor.WHITE}'${ChatColor.YELLOW}!"))
                        return
                    }

                    val serverConfigStatus: String = if (getServerConfig()[args[1]] == true) {
                        "${ChatColor.GREEN}enabled"
                    } else if (getServerConfig()[args[1]] == false) {
                        "${ChatColor.RED}disabled"
                    } else {
                        ""
                    }

                    commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.GOLD}${args[1]} ${ChatColor.GREEN}is currently $serverConfigStatus${ChatColor.GREEN}!"))
                    return
                }

                3 -> {
                    if (!getServerConfig().containsKey(args[1])) {
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, " +
                                "there are no such server config as ${ChatColor.WHITE}'${ChatColor.GRAY}${args[1]}${ChatColor.WHITE}'${ChatColor.YELLOW}!"))
                        return
                    }

                    val modifiedValue: String = if (args[2].equals("true", true)) {
                        "${ChatColor.GREEN}enabled"
                    } else if (args[2].equals("false", true)) {
                        "${ChatColor.RED}disabled"
                    } else {
                        commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                        return
                    }

                    setConfig(args[1], args[2])

                    commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.GOLD}" +
                            "${args[1].replace('_', ' ', true)} ${ChatColor.GREEN}has been ${modifiedValue}${ChatColor.GREEN}!"))
                    return
                }

                else -> {
                    commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    return
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        /* Tab completion for the command */
        fun tabCompletion(commander: Player, args: Array<out String>): MutableList<String>? {
            if (!commander.isOp) return null

            when (args.size) {
                2 -> {
                    return getServerConfig().keys.toList().toMutableList() as MutableList<String>
                }

                3 -> {
                    return listOf("true", "false").toMutableList()
                }
            }

            return null
        }

        /* Modify server configuration */
        private fun setConfig(config: String, value: String) {
            val serverConfig: JSONObject = getServerConfig()

            val fileOutputStream = FileOutputStream(getServerConfigFile())
            val bufferedWriter = BufferedWriter(OutputStreamWriter(fileOutputStream))

            if (value.equals("true", true) || value.equals("false", true)) {
                serverConfig[config] = value.toBoolean()
            } else {
                serverConfig[config] = value
            }

            bufferedWriter.write(gson.toJson(serverConfig))
            bufferedWriter.close()
            fileOutputStream.close()
        }

        /* Get server config */
        fun getServerConfig(): JSONObject {
            val fileReader = FileReader(getServerConfigFile())
            return jsonParser.parse(fileReader) as JSONObject
        }

        /* Update server config element */
        private fun updateServerConfigElement() {
            val fileReader = FileReader(getServerConfigFile())
            val sourceElement: JSONObject = getSourceElement()
            val clientElement: JSONObject = jsonParser.parse(fileReader) as JSONObject

            sourceElement.keys.forEach { key ->
                if (!clientElement.containsKey(key)) clientElement.putIfAbsent(key, sourceElement[key])
            }

            clientElement.keys.removeIf { key -> !sourceElement.containsKey(key) }

            val clientFileOutputStream = FileOutputStream(getServerConfigFile())
            val clientBufferedWriter = BufferedWriter(OutputStreamWriter(clientFileOutputStream))

            clientBufferedWriter.write(gson.toJson(clientElement))
            clientBufferedWriter.close()
            clientFileOutputStream.close()

            fileReader.close()
        }

        /* Get source element */
        private fun getSourceElement(): JSONObject {
            val stringBuilder: StringBuilder = StringBuilder()
            val inputStream: InputStream = classLoader.getResourceAsStream("resources/server_configuration.json")!!
            val sourceBufferReader = BufferedReader(InputStreamReader(inputStream))

            sourceBufferReader.lines()!!.toArray().forEach { content: Any ->
                stringBuilder.append(content as String)
            }

            return jsonParser.parse(stringBuilder.toString()) as JSONObject
        }

        /* Create required folders and files */
        private fun createNewFiles() {
            val serverConfigFile = getServerConfigFile()

            if (!FolderList.FOLDER_A.folder.exists()) {
                FolderList.FOLDER_A.folder.mkdirs()
            }

            if (!serverConfigFile.exists()) {
                serverConfigFile.createNewFile()
            }
        }

        /* Get server config file */
        private fun getServerConfigFile(): File {
            return File(FolderList.FOLDER_A.folder, "server_configuration.json")
        }
    }
}