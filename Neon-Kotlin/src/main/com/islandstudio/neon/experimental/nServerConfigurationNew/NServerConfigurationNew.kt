package com.islandstudio.neon.experimental.nServerConfigurationNew

import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nFolder.FolderList
import com.islandstudio.neon.stable.primary.nFolder.NFolder
import com.islandstudio.neon.stable.utils.NItemHighlight
import com.islandstudio.neon.stable.utils.NNamespaceKeys
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.nGUI.NGUIConstructor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.MemorySection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.simpleyaml.configuration.comments.CommentType
import org.simpleyaml.configuration.comments.format.YamlCommentFormat
import org.simpleyaml.configuration.file.YamlFile
import org.simpleyaml.utils.SupplierIO
import org.yaml.snakeyaml.Yaml
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.Reader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil

object NServerConfigurationNew {
    /**
     * Set toggle for the specified config.
     *
     * @param parentKey The parent key (feature name) of the config.
     * @param value The boolean value to set.
     */
    fun setToggle(parentKey: String, value: Boolean, commander: Player? = null) {
        Handler.getClientConfigSection(parentKey, commander)?.set(ServerConfigGeneralProperties.IS_ENABLED.property, value)
    }

    /**
     * Set option value for the specific config.
     *
     * @param parentKey The parent key (feature name) of the config.
     * @param optionKey The option key (option name) of the config.
     * @param value The value need to be set for the option.
     */
    fun setOptionValue(parentKey: String, optionKey: String, value: Any) {
        Handler.getClientConfigSection("${parentKey}.option")?.set(optionKey, value)
        save()
    }

    /**
     * Get toggle value from client config.
     *
     * @param parentKey The parent key (feature name) of the config.
     * @return The status of the client config (enabled or disabled).
     */
    fun getToggle(parentKey: String): Boolean {
        return getClientValue(parentKey, ServerConfigGeneralProperties.IS_ENABLED) as Boolean
    }

    fun getOptionValue(parentKey: String, optionKey: String): Any? {
        return getClientValue(parentKey, ServerConfigGeneralProperties.OPTION, optionKey)
    }

    /**
     * Save the client config to the file.
     *
     */
    fun save(commander: Player? = null) {
        val nServerConfigFile = Handler.nServerConfigFile

        nServerConfigFile.copyTo(Handler.nServerConfigFileBackup, true)
        Handler.getClientFileConfiguration(commander).save(nServerConfigFile)
        Handler.loadConfig()
    }

    /**
     * Get all the parent keys from source config. (config names).
     *
     * @return List of parent keys from source config.
     */
    private fun getSourceParentKeys(): ArrayList<String> {
        return Handler.getSourceFileConfiguration().getKeys(false).toCollection(ArrayList())
    }

    /**
     * Get all the parent keys from client config. (config names).
     *
     * @return List of parent keys from client config.
     */
    fun getClientParentKeys(commander: Player? = null): ArrayList<String> {
        return Handler.getClientFileConfiguration(commander).getKeys(false).toCollection(ArrayList())
    }

    /**
     * Get source config details
     *
     * @param isOptionKey Whether the key is option key or not.
     * @return The config details.
     */
    fun getSourceConfigDetails(isOptionKey: Boolean): TreeMap<String, ArrayList<String>> {
        val sourceConfigDetails: TreeMap<String, ArrayList<String>> = TreeMap()

        if (isOptionKey) {
            getSourceParentKeys().forEach {parentKey ->
                sourceConfigDetails[parentKey] = Handler.getSourceConfigSection("${parentKey}.option")?.getKeys(false)
                    ?.toCollection(ArrayList()) ?: ArrayList()
            }

            return sourceConfigDetails
        }

        getSourceParentKeys().forEach { parentKey ->
            sourceConfigDetails[parentKey] = Handler.getSourceConfigSection(parentKey)?.getKeys(false)
                ?.toCollection(ArrayList()) ?: ArrayList()
        }

        return sourceConfigDetails
    }

    /**
     * Get client config details.
     *
     * @param isOptionKey Whether the config is option key or not.
     * @return The config details.
     */
    fun getClientConfigDetails(isOptionKey: Boolean, commander: Player? = null): TreeMap<String, ArrayList<String>> {
        val clientConfigDetails: TreeMap<String, ArrayList<String>> = TreeMap()

        if (isOptionKey) {
            getClientParentKeys().forEach {parentKey ->
                clientConfigDetails[parentKey] = Handler.getClientConfigSection("${parentKey}.option")?.getKeys(false)
                    ?.toCollection(ArrayList()) ?: ArrayList()
            }

            return clientConfigDetails
        }

        getClientParentKeys().forEach { parentKey ->
            clientConfigDetails[parentKey] = Handler.getClientConfigSection(parentKey)?.getKeys(false)
                ?.toCollection(ArrayList()) ?: ArrayList()
        }

        return clientConfigDetails
    }

    fun getSourceOptionComments(): TreeMap<String, String?> {
        val sourceYamlFile = YamlFile.loadConfiguration(SupplierIO.InputStream { NServerConfigurationNew::class.java.
        classLoader.getResourceAsStream("resources/server_configuration-new.yml") }, true)

        val sourceOptionKeys: TreeMap<String, ArrayList<String>> = getSourceConfigDetails(true)
        val sourceOptionComments: TreeMap<String, String?> = TreeMap()

        sourceOptionKeys.forEach { (featureName, keys) ->
            keys.forEach { key ->
                sourceOptionComments["${featureName}.option.${key}"] = sourceYamlFile.getComment("${featureName}.option.${key}", CommentType.BLOCK)
            }
        }

        return sourceOptionComments
    }

    fun getClientOptionComments(): TreeMap<String, String?> {
        val clientYamlFile = YamlFile.loadConfiguration(Handler.nServerConfigFile, true)

        val clientOptionKeys: TreeMap<String, ArrayList<String>> = getClientConfigDetails(true)
        val clientOptionComments: TreeMap<String, String?> = TreeMap()

        clientOptionKeys.forEach { (featureName, keys) ->
            keys.forEach { key ->
                clientOptionComments["${featureName}.option.${key}"] = clientYamlFile.getComment("${featureName}.option.${key}", CommentType.BLOCK) ?: null
            }
        }

        return clientOptionComments
    }

    /**
     * Get value from source config based on ServerConfigProperties.
     *
     * @param parentKey Parent key (config name) of the config.
     * @param key Child key (config detail) of the config.
     * @param serverConfigGeneralProperties ServerConfigProperties
     * @return Value of the given child key (config detail).
     */
    private fun getSourceValue(parentKey: String, serverConfigGeneralProperties: ServerConfigGeneralProperties, key: String? = null): Any? {
        if (serverConfigGeneralProperties != ServerConfigGeneralProperties.OPTION) return Handler.getSourceConfigSection(parentKey)?.get(serverConfigGeneralProperties.property)

        return key?.let { Handler.getSourceConfigSection("${parentKey}.option")?.get(it) }
    }

    /**
     * Get value from client config based on ServerConfigProperties.
     *
     * @param parentKey Parent key (config name) of the config.
     * @param key Child key (config detail) of the config.
     * @param serverConfigGeneralProperties ServerConfigProperties
     * @return Value of the given child key (config detail).
     */
    private fun getClientValue(parentKey: String, serverConfigGeneralProperties: ServerConfigGeneralProperties, key: String? = null, commander: Player? = null): Any? {
        if (serverConfigGeneralProperties != ServerConfigGeneralProperties.OPTION) return Handler.getClientConfigSection(parentKey, commander)?.get(serverConfigGeneralProperties.property)

        return key?.let { Handler.getClientConfigSection("${parentKey}.option", commander)?.get(it) }
    }

    object Handler {
        private val nServerConfigFolder: File = FolderList.SERVER_CONFIGURATION_FOLDER_NEW.folder

        val nServerConfigFile = File(nServerConfigFolder, "nServerConfig.yml")
        val nServerConfigFileBackup = File(nServerConfigFolder, "nServerConfig_backup.yml")

        val guiSessions: TreeMap<UUID, FileConfiguration> = TreeMap()
        var isNavigating = false

        private lateinit var clientFileConfiguration: FileConfiguration
        private lateinit var sourceFileConfiguration: FileConfiguration

        fun run() {
            NFolder.createNewFile(nServerConfigFile, nServerConfigFolder)

            val clientBufferedReader: BufferedReader = nServerConfigFile.bufferedReader()
            val clientFileLines: Long = clientBufferedReader.lines().count()

            clientBufferedReader.close()

            if (clientFileLines == 0L) {
                val clientFileOutputStream: FileOutputStream = nServerConfigFile.outputStream()
                val clientBufferedWriter: BufferedWriter = clientFileOutputStream.bufferedWriter()

                getSourceContent().forEachIndexed { index, line ->
                    clientBufferedWriter.write(line)

                    if (index != getSourceContent().lastIndex) {
                        clientBufferedWriter.newLine()
                    }
                }

                clientBufferedWriter.close()
            }

            loadConfig()

            NConstructor.registerEvent(EventController())
        }

        /**
         * Set command handler
         *
         * @param commander The player who perform the command
         * @param args The command arguments
         */
        fun setCommandHandler(commander: Player, args: Array<out String>) {
            if (!commander.isOp) {
                commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
                return
            }

            when (args.size) {
                /* Size 1: Open nServerConfiguration GUI */
                1 -> {
                    loadConfig()
                    guiSessions[commander.uniqueId] = getClientFileConfiguration()
                    GUIHandler(NGUI.Handler.getNGUI(commander)).openGUI()
                    return
                }

                /* Size 2: Get toggle status for the specific config/feature */
                2 -> {
                    val featureNameField = args[1]
                    val sourceFeatureNames: List<String> = getSourceParentKeys()

                    /* Check and get feature name, if null will be sending a reminder message that the given feature name is not exists. */
                    val featureName: String = sourceFeatureNames.firstOrNull { it.equals(featureNameField, true) } ?: return commander
                        .sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, there are no server config as ${ChatColor.WHITE}'" +
                                "${ChatColor.GRAY}${featureNameField}${ChatColor.WHITE}'${ChatColor.YELLOW}!"))

                    val isEnabled: String = if (getToggle(featureName)) "${ChatColor.GREEN}enabled" else "${ChatColor.RED}disabled"

                    val valueMessage: String = CommandSyntax.createSyntaxMessage("${ChatColor.GOLD}${featureName} ${ChatColor.YELLOW}" +
                            "is currently ${isEnabled}${ChatColor.YELLOW}!")

                    commander.sendMessage(valueMessage)
                }

                /* Size 3: Get current value for the specific option */
                3 -> {
                    val featureNameField = args[1]
                    val featureOptionField = args[2]

                    val sourceFeatureNames: List<String> = getSourceParentKeys()

                    /* Check and get feature name, if null will be sending a reminder message that the given feature name is not exists. */
                    val featureName: String = sourceFeatureNames.firstOrNull { it.equals(featureNameField, true) } ?: return commander
                        .sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, there are no server config as ${ChatColor.WHITE}'" +
                                "${ChatColor.GRAY}${featureNameField}${ChatColor.WHITE}'${ChatColor.YELLOW}!"))

                    val sourceOptionNames: List<String> = getSourceConfigDetails(true)[featureName] ?: ArrayList()

                    /* Check and get option name, if null will be sending a reminder message that the given option name is not exists. */
                    val optionName: String = sourceOptionNames.firstOrNull { it.equals(featureOptionField, true) } ?: return commander
                        .sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, there are no option as ${ChatColor.WHITE}'" +
                                "${ChatColor.GRAY}${featureOptionField}${ChatColor.WHITE}' ${ChatColor.YELLOW}for ${ChatColor.GOLD}${featureName}${ChatColor.YELLOW}!"))

                    loadConfig()

                    /* Loop through OptionValueProperties to return the current option value to commander. */
                    ServerConfigOptionProperties.values().forEach {
                        if (!it.option.split(".")[2].equals(optionName, true)) return@forEach

                        val valueMessage: String = CommandSyntax.createSyntaxMessage("${ChatColor.GOLD}${featureName}${ChatColor.GRAY}: " +
                                "${ChatColor.GOLD}${optionName} ${ChatColor.YELLOW}is currently set to${ChatColor.WHITE}: ")

                        val clientValue: Any? = getClientValue(featureName, ServerConfigGeneralProperties.OPTION, optionName)

                        when (it.dataType) {
                            OptionDataValidation.DataTypes.BOOLEAN -> {
                                val value: Boolean = clientValue as Boolean

                                if (value)  {
                                    commander.sendMessage("${valueMessage}${ChatColor.GREEN}$value")
                                } else {
                                    commander.sendMessage("${valueMessage}${ChatColor.RED}$value")
                                }
                                return
                            }

                            OptionDataValidation.DataTypes.FLOAT, OptionDataValidation.DataTypes.DOUBLE,
                            OptionDataValidation.DataTypes.BYTE, OptionDataValidation.DataTypes.SHORT,
                            OptionDataValidation.DataTypes.INTEGER, OptionDataValidation.DataTypes.LONG -> {
                                val value: Any = when (it.dataType) {
                                    OptionDataValidation.DataTypes.FLOAT -> clientValue as Float
                                    OptionDataValidation.DataTypes.DOUBLE -> clientValue as Double
                                    OptionDataValidation.DataTypes.BYTE -> clientValue as Byte
                                    OptionDataValidation.DataTypes.SHORT -> clientValue as Short
                                    OptionDataValidation.DataTypes.INTEGER -> clientValue as Int
                                    else -> clientValue as Long
                                }

                                commander.sendMessage("${valueMessage}${ChatColor.GREEN}$value")
                                return
                            }

                            else -> {
                                return
                            }
                        }
                    }

                    return
                }

                /* Size 4: Set new value to the specific option */
                4 -> {
                    val featureNameField = args[1]
                    val featureOptionField = args[2]
                    val featureOptionValueField = args[3]

                    val sourceFeatureNames: List<String> = getSourceParentKeys()

                    /* Check and get feature name, if null will be sending a reminder message that the given feature name is not exists. */
                    val featureName: String = sourceFeatureNames.firstOrNull { it.equals(featureNameField, true) } ?: return commander
                        .sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, there are no server config as ${ChatColor.WHITE}'" +
                            "${ChatColor.GRAY}${featureNameField}${ChatColor.WHITE}'${ChatColor.YELLOW}!"))

                    val sourceOptionNames: List<String> = getSourceConfigDetails(true)[featureName] ?: ArrayList()

                    /* Check and get option name, if null will be sending a reminder message that the given option name is not exists. */
                    val optionName: String = sourceOptionNames.firstOrNull { it.equals(featureOptionField, true) } ?: return commander
                        .sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, there are no option as ${ChatColor.WHITE}'" +
                            "${ChatColor.GRAY}${featureOptionField}${ChatColor.WHITE}' ${ChatColor.YELLOW}for ${ChatColor.GOLD}${featureName}${ChatColor.YELLOW}!"))

                    /* Loop through OptionValueProperties to perform input value verification. */
                    ServerConfigOptionProperties.values().forEach {
                        if (!it.option.split(".")[2].equals(optionName, true)) return@forEach

                        /* Preset messages */
                        val errorMessage: String = CommandSyntax.createSyntaxMessage("${ChatColor.RED}Invalid! ${ChatColor.GOLD}${featureName}${ChatColor.GRAY}: " +
                                "${ChatColor.GOLD}${optionName} ${ChatColor.YELLOW}is required${ChatColor.WHITE}: ")

                        val reloadMessage: String = CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Please reload the server to apply the effects!")

                        val modifiedMessage: String = CommandSyntax.createSyntaxMessage("${ChatColor.GOLD}${featureName}${ChatColor.GRAY}: " +
                                "${ChatColor.GOLD}${optionName} ${ChatColor.YELLOW}has been set to${ChatColor.WHITE}: ")

                        /* Data type verification */
                        val value: Any = OptionDataValidation.isDataTypeValid(it.dataType, featureOptionValueField) ?: return commander
                            .sendMessage("${errorMessage}${ChatColor.GREEN}${it.dataType.name.lowercase().replaceFirstChar {char -> char.titlecase(Locale.getDefault())}} value")

                        when (it.dataType) {
                            OptionDataValidation.DataTypes.BOOLEAN -> {
                                setOptionValue(featureName, optionName, value)

                                if (value as Boolean) {
                                    commander.sendMessage("${modifiedMessage}${ChatColor.GREEN}$value")
                                } else {
                                    commander.sendMessage("${modifiedMessage}${ChatColor.RED}$value")
                                }
                            }

                            OptionDataValidation.DataTypes.FLOAT, OptionDataValidation.DataTypes.DOUBLE,
                            OptionDataValidation.DataTypes.BYTE, OptionDataValidation.DataTypes.SHORT,
                            OptionDataValidation.DataTypes.INTEGER, OptionDataValidation.DataTypes.LONG -> {
                                /* Minimum value for specific option. */
                                val minValue: Any = it.dataRange[0]

                                /* Maximum value for specific option. */
                                val maxValue: Any = it.dataRange[1]

                                val minMaxErrorMessage: String = "${errorMessage}${ChatColor.GRAY}[${ChatColor.GOLD}Min${ChatColor.GRAY}:" +
                                        " ${ChatColor.GREEN}${minValue}${ChatColor.GRAY}, ${ChatColor.GOLD}Max${ChatColor.GRAY}: ${ChatColor.GREEN}${maxValue}${ChatColor.GRAY}]"

                                /* Data range verification */
                                if (!OptionDataValidation.isDataRangeValid(it.dataType, it.dataRange, value)) return commander
                                    .sendMessage(minMaxErrorMessage)

                                setOptionValue(featureName, optionName, value)

                                commander.sendMessage("${modifiedMessage}${ChatColor.GREEN}$value")
                            }

                            else -> {}
                        }

                        commander.sendMessage(reloadMessage)
                    }
                }

                else -> {
                    commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    return
                }
            }
        }

        /**
         * Tab completion for the command.
         *
         * @param commander The player who perform the command.
         * @param args The command arguments.
         *
         */
        fun tabCompletion(commander: Player, args: Array<out String>): MutableList<String> {
            if (!commander.isOp) return mutableListOf()

            loadConfig()

            val sourceFeatureNames: List<String> = getSourceParentKeys()

            when (args.size) {
                2 -> {
                    return sourceFeatureNames.filter { it.startsWith(args[1], true) }.toMutableList()
                }

                3 -> {
                    val featureName = sourceFeatureNames.firstOrNull { it.equals(args[1], true) } ?: return mutableListOf()

                    return getSourceConfigDetails(true)[featureName]?.filter { it.startsWith(args[2], true) }
                        ?.toMutableList() ?: return mutableListOf()
                }

                4 -> {
                    val featureName = sourceFeatureNames.firstOrNull { it.equals(args[1], true) } ?: return mutableListOf()

                    val sourceOptionNames: List<String> = getSourceConfigDetails(true)[featureName] ?: return mutableListOf()

                    val optionName: String = sourceOptionNames.firstOrNull { it.equals(args[2], true) } ?: return mutableListOf()

                    ServerConfigOptionProperties.values().forEach { optionDataTypes ->
                        if (!optionDataTypes.option.split(".")[2].equals(optionName, true)) return@forEach

                        if (optionDataTypes.dataType != OptionDataValidation.DataTypes.BOOLEAN) return@forEach

                        return mutableListOf("true", "false").filter { it.startsWith(args[3], true) }.toMutableList()
                    }
                }
            }

            return mutableListOf()
        }

        /**
         * Load config both source and client.
         *
         */
        fun loadConfig() {
            // TODO: Error messages handling.
            /* This section is used to loading the source server config. */
            val nServerConfigSource: Reader = NServerConfigurationNew::class.java.
            classLoader.getResourceAsStream("resources/server_configuration-new.yml")?.reader() as Reader

            sourceFileConfiguration = YamlConfiguration.loadConfiguration(nServerConfigSource)
            nServerConfigSource.close()

            /* This section is used to loading the client server config.
            * Several processes will be run accordingly if the client server config can't be loaded.
            * */
            try {
                val yamlConfig = YamlConfiguration()
                yamlConfig.load(nServerConfigFile)

                clientFileConfiguration = yamlConfig

                if (!nServerConfigFileBackup.exists()) {
                    nServerConfigFile.copyTo(nServerConfigFileBackup, true)
                }
            } catch (err: Exception) {
                val nServerConfigFileError = File(nServerConfigFolder, "nServerConfig_corrupted.yml")

                /* Create a copy of the corrupted server config file named 'nServerConfig_corrupted.yml' */
                println("There is an error while loading the 'nServerConfig.yml' file!")
                nServerConfigFile.copyTo(nServerConfigFileError, true)

                println("Corrupted server config has been copied to the 'nServerConfig_corrupted.yml' file.")
                Thread.sleep(200)

                println("Rolling back previous server config from the 'nServerConfig_backup.yml' file......")
                Thread.sleep(200)

                /* Roll back the server config from the 'nServerConfig_backup.yml'. */
                if (nServerConfigFileBackup.exists()) {
                    try {
                        /* Load the 'nServerConfig_backup.yml' */
                        val backupYamlConfig = YamlConfiguration()
                        backupYamlConfig.load(nServerConfigFileBackup)

                        backupYamlConfig.saveToString().let {
                            nServerConfigFile.writeText(it)
                        }

                        run()
                        println("Server config has been rolled back!")
                    } catch (err: InvalidConfigurationException) {
                        resetServerConfig()
                    }
                    return
                }

                resetServerConfig()
            }

            update()
        }

        /**
         * Get source file configuration
         *
         * @return Source file configuration
         */
        fun getSourceFileConfiguration(): FileConfiguration {
            return sourceFileConfiguration
        }

        /**
         * Get client file configuration
         *
         * @return Client file configuration
         */
        fun getClientFileConfiguration(commander: Player? = null): FileConfiguration {
            if (commander == null) return clientFileConfiguration

            return guiSessions[commander.uniqueId] ?: clientFileConfiguration
        }

        /**
         * Reset server config to default. This method only used within the loadConfig() method.
         *
         */
        private fun resetServerConfig() {
            if (nServerConfigFileBackup.exists()) {
                /* Reset server config to default if the 'nServerConfig_backup.yml' can't be loaded. */
                println("There is an error while loading the 'nServerConfig_backup.yml' file!")
                Thread.sleep(200)

                println("Removing the corrupted 'nServerConfig_backup.yml' file......")
                Thread.sleep(200)

                println("Resetting the server config to default......")
                Thread.sleep(200)

                nServerConfigFileBackup.delete()
                nServerConfigFile.delete()
                run()

                println("Server config has been reset to default!")
                return
            }

            /* Reset server config to default if the 'nServerConfig_backup.yml' does not exist. */
            println("'nServerConfig_backup.yml' file not found!")
            Thread.sleep(200)

            println("Resetting the server config to default......")
            Thread.sleep(200)

            nServerConfigFile.delete()
            println("Server config has been reset to default!")
            run()
        }

        /**
         * Update server config from source to client.
         *
         */
        private fun update() {
            val sourceConfig = sourceFileConfiguration

            /* Checking 1: Check and update the parent keys. */
            if (!getClientParentKeys().containsAll(getSourceParentKeys())) {
                getSourceParentKeys().forEach { parentKey ->
                    sourceConfig.set(parentKey, getSourceConfigSection(parentKey))

                    if (!getClientParentKeys().contains(parentKey)) return@forEach

                    val clientSection: MemorySection = getClientConfigSection(parentKey) as MemorySection

                    sourceConfig.set("${parentKey}.is_enabled", clientSection.get("is_enabled") as Boolean)
                    sourceConfig.set("${parentKey}.option", clientSection.get("option") as MemorySection)
                }

                val clientFileOutputStream: FileOutputStream = nServerConfigFile.outputStream()
                val clientBufferedWriter: BufferedWriter = clientFileOutputStream.bufferedWriter()

                clientBufferedWriter.write(sourceConfig.saveToString().trim())
                clientBufferedWriter.close()

                loadConfig()
            }

            /* Checking 2: Check and update the child keys. */
            if (getClientConfigDetails(false) != getSourceConfigDetails(false)) {
                val sourceConfigChild = getSourceConfigDetails(false)
                val clientConfigChild = getClientConfigDetails(false)

                sourceConfigChild.forEach fE1@ { (parentKey, childKeys) ->
                    val sourceSection: ConfigurationSection = getSourceConfigSection(parentKey) as ConfigurationSection

                    sourceConfig.set(parentKey, sourceSection)

                    childKeys.forEach fE2@ {childKey ->
                        if (!clientConfigChild[parentKey]!!.contains(childKey)) return@fE2

                        val clientSection: MemorySection = getClientConfigSection(parentKey) as MemorySection

                        if (childKey.equals(ServerConfigGeneralProperties.DESCRIPTION.property, true)) return@fE2

                        if (childKey.equals(ServerConfigGeneralProperties.OPTION.property, true)) {
                            if (clientSection[childKey] !is ConfigurationSection) return@fE2
                        }

                        sourceConfig.set("${parentKey}.${childKey}", clientSection.get(childKey))
                    }
                }

                val clientFileOutputStream: FileOutputStream = nServerConfigFile.outputStream()
                val clientBufferedWriter: BufferedWriter = clientFileOutputStream.bufferedWriter()

                clientBufferedWriter.write(sourceConfig.saveToString().trim())
                clientBufferedWriter.close()

                loadConfig()
            }

            /* Checking 3: Check and update the option keys. */
            if (getClientConfigDetails(true) != getSourceConfigDetails(true)) {
                val sourceOptionKeys: TreeMap<String, ArrayList<String>> = getSourceConfigDetails(true)
                val clientOptionKeys: TreeMap<String, ArrayList<String>> = getClientConfigDetails(true)

                sourceOptionKeys.forEach fE1@ { (parentKey, optionKeys) ->
                    val sourceSection: ConfigurationSection = getSourceConfigSection("${parentKey}.option")
                        ?: sourceConfig.createSection("${parentKey}.option")

                    sourceConfig.set("${parentKey}.is_enabled", getClientConfigSection(parentKey)?.get("is_enabled") as Boolean)
                    sourceConfig.set("${parentKey}.option", sourceSection)

                    optionKeys.forEach fE2@ {
                        if (!clientOptionKeys[parentKey]!!.contains(it)) return@fE2

                        val clientSection: MemorySection = getClientConfigSection("${parentKey}.option") as MemorySection

                        sourceConfig.set("${parentKey}.option.${it}", clientSection.get(it))
                    }
                }

                val clientFileOutputStream: FileOutputStream = nServerConfigFile.outputStream()
                val clientBufferedWriter: BufferedWriter = clientFileOutputStream.bufferedWriter()

                clientBufferedWriter.write(sourceConfig.saveToString().trim())
                clientBufferedWriter.close()

                loadConfig()
            }

            /* Checking 4: Validate toggle, description, and command, it will reset to default if invalid  */
            getSourceConfigDetails(false).forEach { (parentKey, childKeys) ->
                childKeys.forEach childKey@ {
                    when {
                        it.equals(ServerConfigGeneralProperties.DESCRIPTION.property, true) -> {
                            val sourceDescription: String = getSourceValue(parentKey, ServerConfigGeneralProperties.DESCRIPTION) as String
                            val clientDescription: String = getClientValue(parentKey, ServerConfigGeneralProperties.DESCRIPTION) as String

                            if (clientDescription != sourceDescription) {
                                getClientConfigSection(parentKey)?.set(ServerConfigGeneralProperties.DESCRIPTION.property, sourceDescription)
                                save()
                            }

                            return@childKey
                        }

                        it.equals(ServerConfigGeneralProperties.IS_ENABLED.property, true) -> {
                            val sourceToggle: Boolean = getSourceValue(parentKey, ServerConfigGeneralProperties.IS_ENABLED) as Boolean
                            val clientToggle: Boolean? = (getClientValue(parentKey, ServerConfigGeneralProperties.IS_ENABLED).toString()).toBooleanStrictOrNull()

                            if (clientToggle == null) {
                                setToggle(parentKey, sourceToggle)
                                save()
                            }

                            return@childKey
                        }

                        it.equals(ServerConfigGeneralProperties.COMMAND.property, true) -> {
                            val sourceCommand: String = getSourceValue(parentKey, ServerConfigGeneralProperties.COMMAND) as String
                            val clientCommand: String = getClientValue(parentKey, ServerConfigGeneralProperties.COMMAND) as String

                            if (clientCommand != sourceCommand) {
                                getClientConfigSection(parentKey)?.set(ServerConfigGeneralProperties.COMMAND.property, sourceCommand)
                                save()
                            }

                            return@childKey
                        }

                        else -> {
                            return@childKey
                        }
                    }
                }
            }

            /* Checking 5: Validate option value and reset to default if invalid. */
            ServerConfigOptionProperties.values().forEach {
                val featureName: String = it.option.split(".")[0]
                val optionName: String = it.option.split(".")[2]
                val sourceOptionValue: Any = getSourceValue(featureName, ServerConfigGeneralProperties.OPTION, optionName)!!
                val clientOptionValue: Any? = getClientValue(featureName, ServerConfigGeneralProperties.OPTION, optionName)

                when (it.dataType) {
                    OptionDataValidation.DataTypes.BOOLEAN -> {
                        if (OptionDataValidation.isDataTypeValid(it.dataType, clientOptionValue.toString()) == null) {
                            setOptionValue(featureName, optionName, sourceOptionValue)
                            return@forEach
                        }
                    }

                    OptionDataValidation.DataTypes.FLOAT, OptionDataValidation.DataTypes.DOUBLE,
                    OptionDataValidation.DataTypes.BYTE, OptionDataValidation.DataTypes.SHORT,
                    OptionDataValidation.DataTypes.INTEGER, OptionDataValidation.DataTypes.LONG -> {
                        if (OptionDataValidation.isDataTypeValid(it.dataType, clientOptionValue.toString()) == null) {
                            setOptionValue(featureName, optionName, sourceOptionValue)
                            return@forEach
                        }

                        if (!OptionDataValidation.isDataRangeValid(it.dataType, it.dataRange, clientOptionValue)) {
                            setOptionValue(featureName, optionName, sourceOptionValue)
                            return@forEach
                        }
                    }

                    else -> {}
                }
            }

            /* Checking 6: Check and update the comments for the option keys */
            if (NConstructor.getVersion().equals("1.17", true)) return

            if (getClientOptionComments() != getSourceOptionComments()) {
                val sourceOptionComments: TreeMap<String, String?> = getSourceOptionComments()
                val clientOptionComments: TreeMap<String, String?> = getClientOptionComments()

                sourceOptionComments.forEach { (optionPathName, optionComments) ->
                    if (clientOptionComments[optionPathName] == optionComments) return@forEach

                    clientOptionComments[optionPathName] = optionComments
                }

                val clientYamlFile = YamlFile.loadConfiguration(nServerConfigFile, true)

                clientOptionComments.forEach { (optionPathName, optionComments) ->
                    clientYamlFile.setComment(optionPathName, optionComments, CommentType.BLOCK)
                }

                clientYamlFile.setCommentFormat(YamlCommentFormat.PRETTY)
                clientYamlFile.save()

                loadConfig()
            }
        }

        /**
         * Get client config section
         *
         * @param key Config key
         * @return Client config section
         */
        fun getClientConfigSection(key: String, commander: Player? = null): ConfigurationSection? {
            return getClientFileConfiguration(commander).getConfigurationSection(key)
        }

        /**
         * Get source config section
         *
         * @param key Config key
         * @return Source config section
         */
        fun getSourceConfigSection(key: String): ConfigurationSection? {
            return getSourceFileConfiguration().getConfigurationSection(key)
        }

        private fun getSourceContent(): List<String> {
            val inputStream: InputStream = NServerConfigurationNew::class.java.
                            classLoader.getResourceAsStream("resources/server_configuration-new.yml")!!

            val bufferedReader: BufferedReader = inputStream.bufferedReader()

            val sourceContent: MutableList<String> = ArrayList()

            bufferedReader.lines()!!.toArray().toList().forEach { content ->
                sourceContent.add(content as String)
            }

            return sourceContent
        }
    }

    class GUIHandler(nGUI: NGUI): GUIBuilder(nGUI) {
        private val player: Player = nGUI.getGUIOwner()

        override fun getGUIName(): String {
            return "nServerConfiguration"
        }

        override fun getGUISlots(): Int {
            return 54
        }

        override fun setItems() {
            maxPage = ceil(getSourceParentKeys().size.toDouble() / maxItemPerPage.toDouble()).toInt()

            addGUIButtons()

            for (i in 0 until super.maxItemPerPage) {
                itemIndex = super.maxItemPerPage * pageIndex + i

                if (itemIndex >= getSourceParentKeys().size) break

                val serverConfigDetails: ArrayList<String> = ArrayList()

                val serverConfig = ItemStack(Material.BIRCH_SIGN)
                val serverConfigMeta = serverConfig.itemMeta!!

                val serverConfigName = getSourceParentKeys()[itemIndex]

                /* Setting up the status of the config to the button */
                if (getClientValue(serverConfigName, ServerConfigGeneralProperties.IS_ENABLED, commander = player) as Boolean) {
                    serverConfigDetails.add("${ChatColor.GRAY}Status: ${ChatColor.GREEN}Enabled!")
                    serverConfigMeta.addEnchant(NItemHighlight(NNamespaceKeys.NEON_BUTTON_HIGHLIGHT.key), 0, true)
                } else {
                    serverConfigDetails.add("${ChatColor.GRAY}Status: ${ChatColor.RED}Disabled!")
                }

                serverConfigDetails.add("")

                when (isOptionVisible) {
                    true -> {
                        serverConfigDetails.add("${ChatColor.GRAY}${ChatColor.UNDERLINE}Option:")
                        val sourceOption: TreeMap<String, ArrayList<String>> = getSourceConfigDetails(true)

                        if (sourceOption[serverConfigName].isNullOrEmpty()) {
                            serverConfigDetails.add("${ChatColor.YELLOW}No option available!")
                        }

                        sourceOption[serverConfigName]?.forEach { optionName ->
                            val clientOptionValue: Any? = getClientValue(serverConfigName, ServerConfigGeneralProperties.OPTION, optionName, player)

                            serverConfigDetails.add("${ChatColor.WHITE}${optionName}: ${ChatColor.YELLOW}${clientOptionValue}")
                        }
                    }

                    false -> {
                        val serverConfigDescription: ArrayList<String> = (getSourceValue(serverConfigName,
                            ServerConfigGeneralProperties.DESCRIPTION) as String).split(" ").toCollection(ArrayList())

                        val modifiedServerConfigDescription: ArrayList<Collection<String>> = ArrayList()
                        var splicedWords: ArrayList<String> = ArrayList()

                        /* Spit up the description into 7 words for a sentence */
                        for (word in serverConfigDescription) {
                            if (splicedWords.size == 7) {
                                modifiedServerConfigDescription.add(splicedWords)
                                splicedWords = ArrayList()
                            }

                            splicedWords.add(word)

                            if ((serverConfigDescription.size - serverConfigDescription.indexOf(word)) == 1) {
                                modifiedServerConfigDescription.add(splicedWords)
                            }
                        }

                        serverConfigDetails.add("${ChatColor.GRAY}Description: ")

                        /* Setting up the config description to the button */
                        modifiedServerConfigDescription.forEach { word ->
                            serverConfigDetails.add("${ChatColor.YELLOW}${word.joinToString(" ")}")
                        }

                        serverConfigDetails.add("")
                        serverConfigDetails.add("${ChatColor.GRAY}Command:")

                        getSourceValue(serverConfigName, ServerConfigGeneralProperties.COMMAND)?.let { command ->
                            if ((command as String).isEmpty()) {
                                serverConfigDetails.add("${ChatColor.YELLOW}No command available!")
                                return@let
                            }
                            serverConfigDetails.add("${ChatColor.YELLOW}${command}")
                        }
                    }
                }

                /* Setting up the config name to the button */
                serverConfigMeta.setDisplayName("${ChatColor.GOLD}${serverConfigName}")
                serverConfigMeta.lore = serverConfigDetails

                serverConfigMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

                serverConfig.itemMeta = serverConfigMeta

                inventory.addItem(serverConfig)
            }

        }

        override fun guiClickHandler(e: InventoryClickEvent) {
            val clickedItem: ItemStack = e.currentItem!!
            val clickedItemMeta: ItemMeta = clickedItem.itemMeta!!
            val persistentDataContainer: PersistentDataContainer = clickedItemMeta.persistentDataContainer

            when (clickedItem.type) {
                /* Server config button */
                Material.BIRCH_SIGN -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    val clickedItemDisplayName: String = clickedItemMeta.displayName.substring(2)

                    getSourceParentKeys().forEach { configName ->
                        if (configName != clickedItemDisplayName) return@forEach

                        val lore: ArrayList<String> = clickedItemMeta.lore as ArrayList<String>

                        val statusEnabled = "${ChatColor.GRAY}Status: ${ChatColor.GREEN}Enabled!"
                        val statusDisabled = "${ChatColor.GRAY}Status: ${ChatColor.RED}Disabled!"

                        lore.forEach { status ->
                            when (status) {
                                statusEnabled -> {
                                    setToggle(configName, false, player)
                                    lore[lore.indexOf(status)] = statusDisabled
                                    clickedItemMeta.removeEnchant(nItemHighlight)
                                }

                                statusDisabled -> {
                                    setToggle(configName, true, player)
                                    lore[lore.indexOf(status)] = statusEnabled
                                    clickedItemMeta.addEnchant(nItemHighlight, 0, true)
                                }
                            }
                        }

                        clickedItemMeta.lore = lore
                        clickedItem.itemMeta = clickedItemMeta
                    }
                }

                /* Apply button */
                Material.LEVER -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    if (clickedItemMeta.displayName != applyButtonDisplayName) return

                    save(player)

                    player.closeInventory()
                    player.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Please reload the server to apply the effects!"))
                }

                /* Navigation button */
                Material.SPECTRAL_ARROW -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    when (clickedItemMeta.displayName) {
                        previousButtonDisplayName -> {
                            if (pageIndex == 0) return

                            Handler.isNavigating = true
                            pageIndex--
                            super.openGUI()
                        }

                        nextButtonDisplayName -> {
                            if ((itemIndex + 1) >= getSourceParentKeys().size) return

                            Handler.isNavigating = true
                            pageIndex++
                            super.openGUI()
                        }
                    }
                }

                /* Close button */
                Material.BARRIER -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    if (clickedItemMeta.displayName != closeButtonDisplayName) return

                    player.closeInventory()
                }

                /* Toggle option value button */
                Material.NAME_TAG -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    if (clickedItemMeta.displayName != toggleOptionValueDisplayName) return

                    val lore: ArrayList<String> = clickedItemMeta.lore as ArrayList<String>

                    val statusVisible = "${ChatColor.GRAY}Status: ${ChatColor.GREEN}Visible!"
                    val statusHidden = "${ChatColor.GRAY}Status: ${ChatColor.RED}Hidden!"

                    when (isOptionVisible) {
                        true -> {
                            isOptionVisible = false
                            lore[lore.indexOf(statusVisible)] = statusHidden
                            clickedItemMeta.removeEnchant(nItemHighlight)
                        }

                        false -> {
                            isOptionVisible = true
                            lore[lore.indexOf(statusHidden)] = statusVisible
                            clickedItemMeta.addEnchant(nItemHighlight, 0, true)
                        }
                    }

                    clickedItemMeta.lore = lore
                    clickedItem.itemMeta = clickedItemMeta

                    inventory.clear()
                    setItems()
                }

                else -> {
                    return
                }
            }
        }
    }

    private class EventController: Listener {
        @EventHandler
        private fun onClick(e: InventoryClickEvent) {
            val player: Player = e.whoClicked as Player

            if (e.view.title != GUIHandler(NGUI.Handler.getNGUI(player)).getGUIName()) return

            val clickedInventory: Inventory = e.clickedInventory ?: return

            val inventoryHolder: InventoryHolder = clickedInventory.holder ?: return

            if (clickedInventory == player.inventory) e.isCancelled = true

            if (inventoryHolder !is NGUIConstructor) return

            e.isCancelled = true

            if (e.currentItem == null) return

            inventoryHolder.guiClickHandler(e)
        }

        @EventHandler
        private fun onInventoryCLose(e: InventoryCloseEvent) {
            val player: Player = e.player as Player

            if (e.view.title != GUIHandler(NGUI.Handler.getNGUI(player)).getGUIName()) return

            val inventoryHolder: InventoryHolder = e.inventory.holder ?: return

            if (inventoryHolder !is NGUIConstructor) return

            if (Handler.isNavigating) {
                Handler.isNavigating = false
                return
            }

            Handler.guiSessions.remove(player.uniqueId)
        }
    }
}