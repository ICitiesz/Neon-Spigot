package com.islandstudio.neon.stable.primary.nServerFeatures

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.application.di.IComponentInjector
import com.islandstudio.neon.stable.core.io.nFile.FolderList
import com.islandstudio.neon.stable.core.io.nFile.NeonDataFolder
import com.islandstudio.neon.stable.primary.nCommand.CommandHandler
import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.utils.DataSessionState
import com.islandstudio.neon.stable.utils.ObjectSerializer
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.nGUI.NGUIConstructor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.koin.core.component.inject
import org.simpleyaml.configuration.ConfigurationSection
import org.simpleyaml.configuration.file.YamlFile
import org.simpleyaml.configuration.implementation.SimpleYamlImplementation
import org.simpleyaml.configuration.implementation.api.QuoteStyle
import org.simpleyaml.configuration.implementation.api.QuoteValue
import org.simpleyaml.utils.SupplierIO
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.util.*

object NServerFeatures: IComponentInjector {
    private val nServerFeatureGUISession: HashMap<UUID, HashMap<String, ServerFeature.SerializableFeature>> = HashMap()
    private val neon by inject<Neon>()

    val experimentalTag = "${ChatColor.WHITE}${ChatColor.BOLD}[${ChatColor.YELLOW}${ChatColor.BOLD}" +
            "Exp${ChatColor.WHITE}${ChatColor.BOLD}] "
    var isNavigating = false

    fun getServerFeatureNames(sortingType: GUIBuilder.SortingType, sortingOrder: GUIBuilder.SortingOrder): List<String> {
        val serverFeatureNames: ArrayList<String> = Handler.getLoadedEditableServerFeatures().keys.toMutableList() as ArrayList<String>

        when (sortingType) {
            GUIBuilder.SortingType.STABLE -> {
                val tempServerFeatureContainer: ArrayList<String> = ArrayList()
                val internalServerFeatures = Handler.getLoadedInternalServerFeatures()

                serverFeatureNames.forEach {
                    val nServerFeature = internalServerFeatures[it]

                    if (!nServerFeature!!.isExperimental!!) tempServerFeatureContainer.add(it)
                }

                if (sortingOrder == GUIBuilder.SortingOrder.DESCENDING) return tempServerFeatureContainer.sortedDescending()

                return tempServerFeatureContainer.sorted()
            }

            GUIBuilder.SortingType.EXPERIMENTAL -> {
                val tempServerFeatureContainer: ArrayList<String> = ArrayList()
                val internalServerFeatures = Handler.getLoadedInternalServerFeatures()

                serverFeatureNames.forEach {
                    val nServerFeature = internalServerFeatures[it]

                    if (nServerFeature!!.isExperimental!!) tempServerFeatureContainer.add(it)
                }

                if (sortingOrder == GUIBuilder.SortingOrder.DESCENDING) return tempServerFeatureContainer.sortedDescending()

                return tempServerFeatureContainer.sorted()
            }

            else -> {
                if (sortingOrder == GUIBuilder.SortingOrder.DESCENDING) return serverFeatureNames.sortedDescending()

                return serverFeatureNames.sorted()
            }
        }
    }

    fun getServerFeatureOptions(serverFeatureName: String): List<String> {
        return Handler.getLoadedEditableServerFeatures()[serverFeatureName]!!.options?.keys?.sorted()?.toList() ?: listOf()
    }

    /**
     * Add player who use the nServerFeature GUI into the GUI session.
     *
     * @param playerUUID The player unique id.
     */
    fun addGUISession(playerUUID: UUID) {
        if (nServerFeatureGUISession.containsKey(playerUUID)) return

        nServerFeatureGUISession[playerUUID] = Handler.getLoadedEditableServerFeatures()
    }

    /**
     * Discard player from the GUI session upon the nServerFeature GUI closes.
     *
     * @param playerUUID The player unique id.
     */
    fun discardGUISession(playerUUID: UUID) {
        if (!nServerFeatureGUISession.containsKey(playerUUID)) return

        nServerFeatureGUISession.remove(playerUUID)
    }

    /**
     * Get 'editableServerFeature' data from the GUI session.
     *
     * @param playerUUID The player unique id.
     * @return The 'editableServerFeature' data
     */
    fun getGUISession(playerUUID: UUID): HashMap<String, ServerFeature.SerializableFeature> {
        return nServerFeatureGUISession[playerUUID]!!
    }

    fun setToggle(serverFeature: ServerFeature.SerializableFeature, newToggleValue: Boolean) {
        serverFeature.isEnabled = newToggleValue
    }

    fun getToggle(serverFeatureName: ServerFeature.FeatureNames): Boolean {
        return Handler.getLoadedExternalServerFeatures()[serverFeatureName.featureName]!!.isEnabled
            ?: Handler.getLoadedInternalServerFeatures()[serverFeatureName.featureName]!!.isEnabled!!
    }

    fun getEditableToggle(serverFeatureName: String): Boolean {
        return Handler.getLoadedEditableServerFeatures()[serverFeatureName]!!.isEnabled
            ?: Handler.getLoadedInternalServerFeatures()[serverFeatureName]!!.isEnabled!!
    }

    fun isExperimental(serverFeatureName: String): Boolean {
        return Handler.getLoadedInternalServerFeatures()[serverFeatureName]!!.isExperimental!!
    }

    fun setOptionValue(serverFeatureName: String, optionName: String, newOptionValue: Any) {
        val editableServerFeature = Handler.getLoadedEditableServerFeatures()

        editableServerFeature[serverFeatureName]!!.options!![optionName]!!.optionValue = newOptionValue
        saveEditableServerFeature(editableServerFeature)
    }

    fun getOptionValue(serverFeatureName: String, optionName: String): Any {
        return Handler.getLoadedEditableServerFeatures()[serverFeatureName]!!.options!![optionName]!!.optionValue
    }

    fun getOptionDataType(serverFeatureName: String, optionName: String): String {
        return Handler.getLoadedEditableServerFeatures()[serverFeatureName]!!.options!![optionName]!!.optionDataType
    }

    fun getOptionDataRange(serverFeatureName: String, optionName: String): Array<String> {
        return Handler.getLoadedEditableServerFeatures()[serverFeatureName]!!.options!![optionName]!!.optionDataRange
    }

    fun saveEditableServerFeature(serverFeature: HashMap<String, ServerFeature.SerializableFeature>) {
        Handler.setLoadedEditableServerFeatures(serverFeature)
        Handler.updateServerFeatures(Handler.getLoadedEditableServerFeatures(), Handler.SavingState.SAVE)
    }

    object Handler: CommandHandler/*, CommandDispatcher*/ {
        private val nServerFeaturesFolder: File = FolderList.NSERVERFEATURES_FOLDER.folder
        private val nServerFeaturesFile = File(nServerFeaturesFolder, "nServerFeatures.yml")

        private val loadedServerFeatures: EnumMap<DataSessionState, String> = EnumMap(DataSessionState::class.java)
        private lateinit var optionProperties: YamlFile

        enum class SavingState {
            SAVE,
            UPDATE
        }

        /**
         * Initialization for nServerFeatures
         *
         */
        fun run() {
            var isFirstGenerated = false

            /* Stage 1: File creation and initialization. */
            NeonDataFolder.createNewFile(nServerFeaturesFolder, nServerFeaturesFile.name)

            /* Getting the file content size */
            val externalBufferedReader: BufferedReader = nServerFeaturesFile.bufferedReader()
            val externalLinesCount: Long = externalBufferedReader.lines().count()

            externalBufferedReader.close()

            /* Check if the file content lines is equal to 0 */
            if (externalLinesCount == 0L) {
                saveToFile(getInternalServerFeaturesAsYamlFile())

                isFirstGenerated = true
            }

            /* Stage 2: Load server features data into memory. */
            loadServerFeatures()

            if (!isFirstGenerated) {
                /* Stage 2.1: Update server feature data if the file is not first generated */
                updateServerFeatures(getLoadedExternalServerFeatures(), SavingState.UPDATE)
            }

            /* Stage 3: Register event for nServerFeatures GUI */
//            NConstructor.registerEventProcessor(EventProcessor())
        }

        override fun getCommandHandler(commander: Player, args: Array<out String>) {
            if (!commander.isOp) {
                commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
                return
            }

            val serverFeatureNames = getServerFeatureNames(GUIBuilder.SortingType.DEFAULT, GUIBuilder.SortingOrder.ASCENDING)
            val internalServerFeature = getLoadedInternalServerFeatures()

            when (args.size) {
                /* Size 1: Add player to nServerFeatures gui session and open the gui. */
                1 -> {
                    addGUISession(commander.uniqueId)
                    GUIHandler(NGUI.Handler.getNGUI(commander)).openGUI()
                }

                /* Size 2: Get toggle status for the specific feature. */
                2 -> {
                    val featureNameField = args[1]

                    /* Check and get feature name, if null will be sending a reminder message that the given feature name is not exists. */
                    val featureName: String = serverFeatureNames.firstOrNull { it.equals(featureNameField, true) }
                        ?: return commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, " +
                        "there are no such server feature as ${ChatColor.WHITE}'${ChatColor.GRAY}${featureNameField}${ChatColor.WHITE}'${ChatColor.YELLOW}!"))

                    val isEnabled: String = if (getEditableToggle(featureName)) "${ChatColor.GREEN}enabled" else "${ChatColor.RED}disabled"

                    val valueMessage: String = if (isExperimental(featureName)) CommandSyntax.createSyntaxMessage("$experimentalTag${ChatColor.GOLD}${featureName} ${ChatColor.YELLOW}" +
                            "is currently ${isEnabled}${ChatColor.YELLOW}!") else CommandSyntax.createSyntaxMessage("${ChatColor.GOLD}${featureName} ${ChatColor.YELLOW}" +
                            "is currently ${isEnabled}${ChatColor.YELLOW}!")

                    commander.sendMessage(valueMessage)
                }

                /* Size 3: Get current option value for the specific feature if available */
                3 -> {
                    val featureNameField = args[1]
                    val featureOptionField = args[2]

                    /* Check and get feature name, if null will be sending a reminder message that the given feature name is not exists. */
                    val featureName: String = serverFeatureNames.firstOrNull { it.equals(featureNameField, true) }
                        ?: return commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, " +
                                "there are no such server feature as ${ChatColor.WHITE}'${ChatColor.GRAY}${featureNameField}${ChatColor.WHITE}'${ChatColor.YELLOW}!"))

                    val optionNames: List<String> = getServerFeatureOptions(featureName)

                    /* Check and get option name, if null will be sending a reminder message that the given option name is not exists. */
                    val optionName: String = optionNames.firstOrNull { it.equals(featureOptionField, true) } ?: return if (isExperimental(featureName))
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, there are no option as ${ChatColor.WHITE}'" +
                                "${ChatColor.GRAY}${featureOptionField}${ChatColor.WHITE}' ${ChatColor.YELLOW}for $experimentalTag${ChatColor.GOLD}${featureName}${ChatColor.YELLOW}!"))
                    else
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, there are no option as ${ChatColor.WHITE}'" +
                                "${ChatColor.GRAY}${featureOptionField}${ChatColor.WHITE}' ${ChatColor.YELLOW}for ${ChatColor.GOLD}${featureName}${ChatColor.YELLOW}!"))

                    val valueMessage: String = if (isExperimental(featureName)) CommandSyntax.createSyntaxMessage("$experimentalTag${ChatColor.GOLD}${featureName}${ChatColor.GRAY}: " +
                            "${ChatColor.GOLD}${optionName} ${ChatColor.YELLOW}is currently set to${ChatColor.WHITE}: ")
                    else CommandSyntax.createSyntaxMessage("${ChatColor.GOLD}${featureName}${ChatColor.GRAY}: " +
                            "${ChatColor.GOLD}${optionName} ${ChatColor.YELLOW}is currently set to${ChatColor.WHITE}: ")

                    val optionValue = getOptionValue(featureName, optionName)

                    if (getOptionDataType(featureName, optionName).equals("Boolean", true)) {
                        if (optionValue as Boolean) {
                            commander.sendMessage("${valueMessage}${ChatColor.GREEN}$optionValue")
                        } else {
                            commander.sendMessage("${valueMessage}${ChatColor.RED}$optionValue")
                        }

                        return
                    }

                    commander.sendMessage("${valueMessage}${ChatColor.GREEN}${optionValue}")
                }

                /* Size 4: Set new option value to the specific option. */
                4 -> {
                    val featureNameField = args[1]
                    val featureOptionField = args[2]
                    val featureOptionValueField = args[3]

                    /* Check and get feature name, if null will be sending a reminder message that the given feature name is not exists. */
                    val featureName: String = serverFeatureNames.firstOrNull { it.equals(featureNameField, true) } ?: return commander
                        .sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, there are no such server feature as ${ChatColor.WHITE}'" +
                                "${ChatColor.GRAY}${featureNameField}${ChatColor.WHITE}'${ChatColor.YELLOW}!"))

                    val optionNames: List<String> = getServerFeatureOptions(featureName)

                    /* Check and get option name, if null will be sending a reminder message that the given option name is not exists. */
                    val optionName: String = optionNames.firstOrNull { it.equals(featureOptionField, true) } ?: return if (isExperimental(featureName))
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, there are no option as ${ChatColor.WHITE}'" +
                            "${ChatColor.GRAY}${featureOptionField}${ChatColor.WHITE}' ${ChatColor.YELLOW}for $experimentalTag${ChatColor.GOLD}${featureName}${ChatColor.YELLOW}!"))
                    else
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Sorry, there are no option as ${ChatColor.WHITE}'" +
                                "${ChatColor.GRAY}${featureOptionField}${ChatColor.WHITE}' ${ChatColor.YELLOW}for ${ChatColor.GOLD}${featureName}${ChatColor.YELLOW}!"))

                    /* Preset messages */
                    val errorMessage: String = if (isExperimental(featureName)) CommandSyntax.createSyntaxMessage("${ChatColor.RED}Invalid data type or data range! $experimentalTag${ChatColor.GOLD}${featureName}${ChatColor.GRAY}: " +
                            "${ChatColor.GOLD}${optionName} ${ChatColor.YELLOW}is required${ChatColor.WHITE}: ") else CommandSyntax.createSyntaxMessage("${ChatColor.RED}Invalid data type or data range! ${ChatColor.GOLD}${featureName}${ChatColor.GRAY}: " +
                            "${ChatColor.GOLD}${optionName} ${ChatColor.YELLOW}is required${ChatColor.WHITE}: ")

                    val reloadMessage: String = CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Please reload the server to apply the effects!")

                    val modifiedMessage: String = if (isExperimental(featureName)) CommandSyntax.createSyntaxMessage("$experimentalTag${ChatColor.GOLD}${featureName}${ChatColor.GRAY}: " +
                            "${ChatColor.GOLD}${optionName} ${ChatColor.YELLOW}has been set to${ChatColor.WHITE}: ") else CommandSyntax.createSyntaxMessage("${ChatColor.GOLD}${featureName}${ChatColor.GRAY}: " +
                            "${ChatColor.GOLD}${optionName} ${ChatColor.YELLOW}has been set to${ChatColor.WHITE}: ")

                    val optionDataType = getOptionDataType(featureName, optionName)

                    /* Data type verification */
                    val newOptionValue: Any = if (featureOptionValueField.equals("default", true)) {
                            internalServerFeature[featureName]!!.options!![optionName]!!.optionValue
                        } else {
                        OptionValueValidation.isDataTypeValid(optionDataType, featureOptionValueField)
                            ?: return commander.sendMessage("${errorMessage}${ChatColor.GREEN}${optionDataType} value")
                    }

                    if (optionDataType.equals(OptionValueValidation.DataTypes.BOOLEAN.dataType, true)) {
                        if (newOptionValue as Boolean) {
                            commander.sendMessage("${modifiedMessage}${ChatColor.GREEN}$newOptionValue")
                        } else {
                            commander.sendMessage("${modifiedMessage}${ChatColor.RED}$newOptionValue")
                        }

                        setOptionValue(featureName, optionName, newOptionValue)

                        commander.sendMessage(reloadMessage)

                        neon.server.onlinePlayers.forEach { onlinePlayer ->
                            if (!onlinePlayer.isOp) return@forEach

                            if (onlinePlayer == commander) return@forEach

                            onlinePlayer.sendMessage("${ChatColor.GOLD}${commander.name}${ChatColor.YELLOW} has made changes to the nServerFeatures.")
                        }

                        return
                    }

                    val optionDataRange = getOptionDataRange(featureName, optionName)

                    val minValue: Any = optionDataRange.first()
                    val maxValue: Any = optionDataRange.last()

                    val minMaxErrorMessage: String = "${errorMessage}${ChatColor.GRAY}[${ChatColor.GOLD}Min${ChatColor.GRAY}:" +
                            " ${ChatColor.GREEN}${minValue}${ChatColor.GRAY}, ${ChatColor.GOLD}Max${ChatColor.GRAY}: ${ChatColor.GREEN}${maxValue}${ChatColor.GRAY}]"

                    /* Data range verification */
                    if (!OptionValueValidation.isDataRangeValid(newOptionValue, optionDataType, optionDataRange)) {
                        return commander.sendMessage(minMaxErrorMessage)
                    }

                    setOptionValue(featureName, optionName, newOptionValue)

                    commander.sendMessage("${modifiedMessage}${ChatColor.GREEN}$newOptionValue")
                    commander.sendMessage(reloadMessage)

                    neon.server.onlinePlayers.forEach { onlinePlayer ->
                        if (!onlinePlayer.isOp) return@forEach

                        if (onlinePlayer == commander) return@forEach

                        onlinePlayer.sendMessage("${ChatColor.GOLD}${commander.name}${ChatColor.YELLOW} has made changes to the nServerFeatures.")
                    }
                }

                else -> {
                    commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    return
                }
            }
        }

        override fun getTabCompletion(commander: Player, args: Array<out String>): MutableList<String> {
            if (!commander.isOp) return super.getTabCompletion(commander, args)

            val serverFeatureNames = getServerFeatureNames(GUIBuilder.SortingType.DEFAULT, GUIBuilder.SortingOrder.ASCENDING)

            when (args.size) {
                /* Size 2: Display all server feature names */
                2 -> {
                    return serverFeatureNames.filter { it.startsWith(args[1], true) }.toMutableList()
                }

                /* Size 3: Display all server feature options */
                3 -> {
                    val featureName = serverFeatureNames.firstOrNull { it.equals(args[1], true) } ?: return super.getTabCompletion(commander, args)

                    return getServerFeatureOptions(featureName).filter { it.startsWith(args[2], true) }
                        .toMutableList()
                }

                /* Size 4: Display pre-define value for specific server feature option */
                4 -> {
                    val featureName = serverFeatureNames.firstOrNull { it.equals(args[1], true) }
                        ?: return super.getTabCompletion(commander, args)

                    val optionName: String = getServerFeatureOptions(featureName).firstOrNull { it.equals(args[2], true) }
                        ?: return super.getTabCompletion(commander, args)

                    if (getOptionDataType(featureName, optionName).equals("Boolean",true)) {
                        return mutableListOf("default", "true", "false").filter { it.startsWith(args[3], true) }.toMutableList()
                    }

                    return mutableListOf("default").filter { it.startsWith(args[3], true) }.toMutableList()
                }
            }

            return super.getTabCompletion(commander, args)
        }

        /**
         * Load server features data & option properties into memory.
         *
         */
        private fun loadServerFeatures() {
            val internalServerSerializableFeature: HashMap<String, ServerFeature.SerializableFeature> = HashMap()
            val externalServerSerializableFeature: HashMap<String, ServerFeature.SerializableFeature> = HashMap()

            /* Load option properties */
            optionProperties = YamlFile.loadConfiguration(SupplierIO.Reader
            { NServerFeatures::class.java.classLoader.getResourceAsStream("resources/nServerFeatures/nServerFeaturesOptionProperties.yml")!!.reader() })

            /* Load internal server feature */
            getInternalServerFeaturesAsYamlFile().getValues(false)
                .forEach {
                    internalServerSerializableFeature[it.key] = ServerFeature.SerializableFeature(it)
                }

            loadedServerFeatures[DataSessionState.INTERNAL] = ObjectSerializer.serializeObjectEncoded(internalServerSerializableFeature)

            /* Load external & editable server feature */
            YamlFile.loadConfiguration(SupplierIO.Reader { nServerFeaturesFile.reader() }).getValues(false)
                .forEach {
                    externalServerSerializableFeature[it.key] = ServerFeature.SerializableFeature(it)
                }

            loadedServerFeatures[DataSessionState.EXTERNAL] = ObjectSerializer.serializeObjectEncoded(externalServerSerializableFeature)
            loadedServerFeatures[DataSessionState.EDITABLE] = ObjectSerializer.serializeObjectEncoded(externalServerSerializableFeature)
        }

        @Suppress("UNCHECKED_CAST")
        fun getLoadedInternalServerFeatures(): HashMap<String, ServerFeature.SerializableFeature> {
            return ObjectSerializer.deserializeObjectEncoded(loadedServerFeatures[DataSessionState.INTERNAL]!!) as HashMap<String, ServerFeature.SerializableFeature>
        }

        @Suppress("UNCHECKED_CAST")
        fun getLoadedExternalServerFeatures(): HashMap<String, ServerFeature.SerializableFeature> {
            return ObjectSerializer.deserializeObjectEncoded(loadedServerFeatures[DataSessionState.EXTERNAL]!!) as HashMap<String, ServerFeature.SerializableFeature>
        }

        @Suppress("UNCHECKED_CAST")
        fun getLoadedEditableServerFeatures(): HashMap<String, ServerFeature.SerializableFeature> {
            return ObjectSerializer.deserializeObjectEncoded(loadedServerFeatures[DataSessionState.EDITABLE]!!) as HashMap<String, ServerFeature.SerializableFeature>
        }

        fun setLoadedEditableServerFeatures(editableServerFeature: HashMap<String, ServerFeature.SerializableFeature>) {
            loadedServerFeatures[DataSessionState.EDITABLE] = ObjectSerializer.serializeObjectEncoded(editableServerFeature)
        }

        fun getOptionProperties(): YamlFile {
            (optionProperties.implementation as SimpleYamlImplementation).dumperOptions.width = 1170

            return optionProperties
        }

        /**
         * Update external server features from reference [internal / editable].
         *
         */
        fun updateServerFeatures(referenceServerFeature: HashMap<String, ServerFeature.SerializableFeature>, savingState: SavingState) {
            val updatedServerFeatures = getInternalServerFeaturesAsYamlFile()

            /* 'updatedServerFeatures' is a copy of internal server features', where everything is default. */
            updatedServerFeatures.getValues(false).forEach { data ->
                val updatedServerFeature = ServerFeature.Feature(data)
                val updatedServerFeatureName = updatedServerFeature.name!!

                /* Stage 1: Check if the server feature in 'updatedServerFeatures' exists in 'externalServerFeatures'
                 * If not exist, the default value will remain */
                if (!referenceServerFeature.keys.contains(updatedServerFeatureName)) return@forEach

                val externalServerFeature = referenceServerFeature[updatedServerFeatureName]

                /* Stage 2: Check if the 'isEnabled' is Boolean type
                 * If it is, copy the value to 'updatedServerFeatures'*/
                val externalServerFeatureToggle = externalServerFeature!!.isEnabled

                if (externalServerFeatureToggle is Boolean) {
                    updatedServerFeatures.set("${updatedServerFeatureName}.${ServerFeature.ServerFeatureProperty.IS_ENABLED.property}", externalServerFeatureToggle)
                }

                /* Stage 3: Check if the 'option' property is null
                 * If it is, the default value will remain */
                if (externalServerFeature.options == null) return@forEach

                /* Stage 4: Check if the options in 'updatedServerFeatures' exist in 'externalServerFeatures'
                 * If not exist, the default value will remain */
                updatedServerFeature.option!!.getValues(false).forEach updatedServerFeatureOptions@{ options ->
                    val externalServerFeatureOption = externalServerFeature.options!!

                    if (!externalServerFeatureOption.keys.contains(options.key)) return@updatedServerFeatureOptions

                    val optionProperty = getLoadedInternalServerFeatures()[updatedServerFeatureName]!!.options!![options.key]!!

                    val externalFeatureOptionValue = externalServerFeatureOption[options.key]!!.optionValue

                    /* Stage 5: Check if the data type of option value equal to default data type
                     * If not equal, the default value will remain */
                    if (externalFeatureOptionValue::class.java.simpleName != optionProperty.optionDataType) return@updatedServerFeatureOptions

                    /* Stage 5.1: Check if the data type of option value equal to Boolean
                     * If equal, the option value will skip the data range check and copy to 'updatedServerFeatures'  */
                    if (externalFeatureOptionValue::class.java.simpleName == OptionValueValidation.DataTypes.BOOLEAN.dataType) {
                        updatedServerFeatures.set("${updatedServerFeatureName}.${ServerFeature.ServerFeatureProperty.OPTIONS.property}.${options.key}", externalFeatureOptionValue)
                        return@updatedServerFeatureOptions
                    }

                    /* Stage 6: Check if the data range of option value valid as default
                     * If not valid, the default value will remain */
                    if (!OptionValueValidation.isDataRangeValid(
                            externalFeatureOptionValue,
                            optionProperty.optionDataType,
                            optionProperty.optionDataRange
                        )
                    ) {
                        return@updatedServerFeatureOptions
                    }

                    updatedServerFeatures.set("${updatedServerFeatureName}.${ServerFeature.ServerFeatureProperty.OPTIONS.property}.${options.key}", externalFeatureOptionValue)
                }
            }

            saveToFile(updatedServerFeatures)

            if (savingState == SavingState.SAVE) return

            loadServerFeatures()
        }

        private fun saveToFile(serverFeature: YamlFile) {
            /* Add single quote to all String data value */
            serverFeature.getValues(true).forEach {
                if (it.value is ConfigurationSection) {
                    if (!it.key.contains(".${ServerFeature.ServerFeatureProperty.OPTIONS.property}")) return@forEach

                    val optionSection = it.value as ConfigurationSection

                    optionSection.getValues(false).forEach fE2@{ option ->
                        if (option.value !is String) return@fE2

                        optionSection.set(option.key, QuoteValue(option.value, QuoteStyle.SINGLE))
                    }
                }

                if (it.value is String && !it.key.contains(".${ServerFeature.ServerFeatureProperty.OPTIONS.property}")) {
                    serverFeature.set(it.key, QuoteValue(it.value, QuoteStyle.SINGLE))
                }
            }

            val externalBufferedWriter: BufferedWriter = nServerFeaturesFile.outputStream().bufferedWriter()
            externalBufferedWriter.write(serverFeature.saveToString().trim())
            externalBufferedWriter.close()
        }

        /**
         * Get internal server features as YamlFile.
         *
         * @return Internal server features in YamlFile.
         */
        private fun getInternalServerFeaturesAsYamlFile(): YamlFile {
            val internalServerFeatures = YamlFile.loadConfiguration(SupplierIO.Reader { NServerFeatures::class.java.classLoader
                .getResourceAsStream("resources/nServerFeatures/nServerFeatures.yml")!!.reader() }, true)

            (internalServerFeatures.implementation as SimpleYamlImplementation).dumperOptions.width = 1170

            return internalServerFeatures
        }

//        override fun getCommandDispatcher(commander: CommandSender, args: Array<out String>) {
//            val command = CommandAlias.NSERVER_FEATURES.command
//            val roleAccess = NRoleAccess.getCommandSenderRoleAccess(commander, command.permission).also {
//                if (!command.isCommandAccessible(commander, it)) {
//                    return CommandInterfaceProcessor.notifyInvalidCommand(commander, args[0])
//                }
//            }
//            val argsLength = args.size
//
//            if (argsLength == 1) {
//                if (commander !is Player) {
//                    return CommandInterfaceProcessor.sendCommandSyntax(commander, com.islandstudio.neon.stable.core.command.properties.CommandSyntax.UNSUPPORTED_GUI_ACCESS)
//                }
//
//                addGUISession(commander.uniqueId)
//                GUIHandler(NGUI.Handler.getNGUI(commander)).openGUI()
//                return
//            }
//
//            // TODO: Need to change the command execution to fully integrate permission for each of the command argument.
//
//        }
    }

    class EventProcessor: Listener {
        @EventHandler
        private fun onInventoryClose(e: InventoryCloseEvent) {
            val player: Player = e.player as Player

            if (e.view.title != GUIHandler(NGUI.Handler.getNGUI(player)).getGUIName()) return

            val inventoryHolder: InventoryHolder = e.inventory.holder ?: return

            if (inventoryHolder !is NGUIConstructor) return

            if (isNavigating) {
                isNavigating = false
                return
            }

            discardGUISession(player.uniqueId)
        }

        @EventHandler
        private fun onInventoryClick(e: InventoryClickEvent) {
            val player: Player = e.whoClicked as Player

            if (e.view.title != GUIHandler(NGUI.Handler.getNGUI(player)).getGUIName()) return

            val clickedInventory: Inventory = e.clickedInventory ?: return

            val inventoryHolder: InventoryHolder = clickedInventory.holder ?: return

            if (clickedInventory == player.inventory) e.isCancelled = true

            if (inventoryHolder !is NGUIConstructor) return

            e.isCancelled = true

            if (e.currentItem == null) return

            inventoryHolder.setGUIClickHandler(e)
        }
    }
}