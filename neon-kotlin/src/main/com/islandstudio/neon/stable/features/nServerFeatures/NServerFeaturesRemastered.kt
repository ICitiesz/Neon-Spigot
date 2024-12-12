package com.islandstudio.neon.stable.features.nServerFeatures

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.common.action.ActionState
import com.islandstudio.neon.stable.common.action.ActionStatus
import com.islandstudio.neon.stable.core.application.di.IComponentInjector
import com.islandstudio.neon.stable.core.command.CommandDispatcher
import com.islandstudio.neon.stable.core.command.CommandInterfaceProcessor
import com.islandstudio.neon.stable.core.command.properties.CommandAlias
import com.islandstudio.neon.stable.core.command.properties.CommandArgument
import com.islandstudio.neon.stable.core.command.properties.CommandSyntax
import com.islandstudio.neon.stable.core.gui.NGUI
import com.islandstudio.neon.stable.core.io.ConfigurationProperty
import com.islandstudio.neon.stable.core.io.DataSourceType
import com.islandstudio.neon.stable.core.io.nFile.NeonDataFolder
import com.islandstudio.neon.stable.core.io.resource.NeonInternalResource
import com.islandstudio.neon.stable.core.io.resource.ResourceManager
import com.islandstudio.neon.stable.features.nServerFeatures.properties.ServerFeature
import com.islandstudio.neon.stable.features.nServerFeatures.properties.ServerFeatureDetail
import com.islandstudio.neon.stable.features.nServerFeatures.properties.ServerFeatureOption
import com.islandstudio.neon.stable.player.nRoleAccess.NRoleAccess
import com.islandstudio.neon.stable.utils.processing.GeneralInputProcessor
import com.islandstudio.neon.stable.utils.processing.properties.DataTypes
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject
import org.simpleyaml.configuration.ConfigurationSection
import org.simpleyaml.configuration.file.YamlFile
import org.simpleyaml.configuration.implementation.SimpleYamlImplementation
import org.simpleyaml.configuration.implementation.api.QuoteStyle
import org.simpleyaml.configuration.implementation.api.QuoteValue
import org.simpleyaml.configuration.implementation.snakeyaml.SnakeYamlImplementation
import org.simpleyaml.exceptions.InvalidConfigurationException
import org.simpleyaml.utils.SupplierIO
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object NServerFeaturesRemastered: IComponentInjector {
    private val resourceManager = ResourceManager()
    private val neon by inject<Neon>()

    val serverFeatureSession: ServerFeatureSession = ServerFeatureSession()


    private val nServerFeaturesSourceFile: YamlFile  = run {
        val readerSupplierIO = SupplierIO.Reader {
            resourceManager.getNeonResourceAsStream(NeonInternalResource.NeonServerFeatures)!!.reader()
        }

        YamlFile.loadConfiguration(readerSupplierIO, true).apply {
            /* Adjust the length of each line for the output of YAML format */
            (this.implementation as SnakeYamlImplementation).dumperOptions.width = 1170
        }
    }

    private val optionProperties: YamlFile  = run {
        val readerSupplierIO = SupplierIO.Reader {
            resourceManager.getNeonResourceAsStream(NeonInternalResource.NeonServerOptionProperties)!!.reader()
        }

        YamlFile.loadConfiguration(readerSupplierIO).apply {
            /* Adjust the length of each line for the output of YAML format */
            (this.implementation as SnakeYamlImplementation).dumperOptions.width = 1170
        }
    }

    private val nServerFeaturesActiveFile: File = NeonDataFolder.createNewFile(NeonDataFolder.NServerFeaturesFolder, "nServerFeatures-reduced.yml")

    object Handler: CommandDispatcher {
        fun run() {
            val isFirstGenerate = with(nServerFeaturesActiveFile.length()) {
                /* Check if the file is blank */
                if (this == 0L) {
                    saveToFile(nServerFeaturesSourceFile)
                    return@with true
                }

                return@with false
            }

            /* Stage 1: Load server features data */
            loadServerFeatures().run {
                /* If it is not first generate, the server feature will go through update process */
                if (!isFirstGenerate) {
                    updateServerFeatureFile()
                }
            }
        }

        override fun getCommandDispatcher(commander: CommandSender, args: Array<out String>) {
            val command = CommandAlias.NSERVER_FEATURES_REMASTERED.command
            val roleAccess = NRoleAccess.getCommandSenderRoleAccess(commander, command.permission).also {
                if (!command.isCommandAccessible(commander, it)) {
                    return CommandInterfaceProcessor.notifyInvalidCommand(commander, args[0])
                }
            }

            val argsLength = args.size

            if (argsLength == 1) {
                if (commander !is Player) {
                    return CommandInterfaceProcessor.sendCommandSyntax(commander, CommandSyntax.UNSUPPORTED_GUI_ACCESS)
                }

                return NGUI.initGUISession(commander, GUIHandler::class.java).getGUIHandler().openGUI()
            }

            command.getCommandArgument(args[1])?.let { commandArg ->
                if (!command.isArgumentAccessible(commander, commandArg, roleAccess)) {
                    return CommandInterfaceProcessor.notifyInvalidCommand(commander, args[0])
                }

                when(commandArg) {
                    /* Reload server feature config and apply changes the server */
                    CommandArgument.RELOAD -> {
                        if (argsLength != 2) {
                            return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                        }

                        reloadServerFeatures().also { actionState ->
                            if (actionState.actionStatus != ActionStatus.SUCCESS) {
                                return CommandInterfaceProcessor.sendCommandSyntax(
                                    commander,
                                    actionState.actionStatusMessage
                                )
                            }

                            return CommandInterfaceProcessor.sendCommandSyntax(
                                commander,
                                "${ChatColor.GREEN}Server feature has been reloaded!"
                            )
                        }
                    }

                    /* Get server feature toggle status / Get server feature option value */
                    CommandArgument.GET -> {
                        if (!(argsLength == 4 || argsLength == 5)) {
                            return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                        }

                        command.getCommandArgument(args[2])?.let { commandArg2 ->
                            when(commandArg2) {
                                CommandArgument.TOGGLE -> {
                                    if (argsLength != 4) {
                                        return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                                    }

                                    val featureName = with(args[3]) {
                                        serverFeatureSession.getServerFeature(this)?.featureName
                                            ?: return CommandInterfaceProcessor.sendCommandSyntax(
                                            commander,
                                            "${ChatColor.RED}No such server feature as '${ChatColor.GOLD}${this}${ChatColor.RED}'!"
                                        )
                                    }

                                    val toggleStatus = with(serverFeatureSession.getActiveServerFeatureToggle(featureName)!!) {
                                        if (this) return@with "${ChatColor.GREEN}enabled"

                                        return@with "${ChatColor.RED}disabled"
                                    }

                                    return CommandInterfaceProcessor.sendCommandSyntax(
                                        commander,
                                        "${ChatColor.GOLD}${featureName} ${ChatColor.YELLOW}is currently ${toggleStatus}${ChatColor.YELLOW}!"
                                    )
                                }

                                CommandArgument.OPTION -> {
                                    if (argsLength != 5) {
                                        return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                                    }

                                    val featureName = with(args[3]) {
                                        serverFeatureSession.getServerFeature(this)?.let {
                                            it.featureName
                                        } ?: return CommandInterfaceProcessor.sendCommandSyntax(
                                            commander,
                                            "${ChatColor.RED}No such server feature as '${ChatColor.GOLD}${this}${ChatColor.RED}'!"
                                        )
                                    }

                                    val featureOption = with(args[4]) {
                                        serverFeatureSession.getServerFeatureOption(featureName, this)
                                            ?: return CommandInterfaceProcessor.sendCommandSyntax(
                                                commander,
                                                "${ChatColor.RED}No such server feature option as '${ChatColor.GOLD}${this}${ChatColor.RED}'!"
                                            )
                                    }

                                    val featureOptionValue = with(serverFeatureSession.getActiveServerFeatureOptionValue(featureName, featureOption.optionName)!!) {
                                        if (this is Boolean) {
                                            if (this) return@with "${ChatColor.GREEN}$this"

                                           return@with "${ChatColor.RED}$this"
                                        }

                                        return@with "${ChatColor.WHITE}$this"
                                    }

                                    CommandInterfaceProcessor.sendCommandSyntax(
                                        commander,
                                        "${ChatColor.GOLD}${featureName}:${featureOption.optionName} ${ChatColor.YELLOW}is currently set to: $featureOptionValue"
                                    )
                                }

                                else -> {
                                    return CommandInterfaceProcessor.notifyInvalidArgument(commander, args, 2)
                                }
                            }
                        } ?: return CommandInterfaceProcessor.notifyInvalidArgument(commander, args, 2)
                    }

                    /* Set server feature toggle status / Set server feature option value */
                    CommandArgument.SET -> {
                        if (!(argsLength == 5 || argsLength == 6)) {
                            return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                        }

                        command.getCommandArgument(args[2])?.let { commandArg2 ->
                            when(commandArg2) {
                                CommandArgument.TOGGLE -> {
                                    if (argsLength != 5) {
                                        return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                                    }

                                    val sourceServerFeature = with(args[3]) {
                                        serverFeatureSession.getServerFeature(this)?.let {
                                            return@with it
                                        } ?: return CommandInterfaceProcessor.sendCommandSyntax(
                                            commander,
                                            "${ChatColor.RED}No such server feature as '${ChatColor.GOLD}${this}${ChatColor.RED}'!"
                                        )
                                    }

                                    val newToggleValue = with(args[4]) {
                                        if (CommandInterfaceProcessor.hasCommandArgument(this, CommandArgument.DEFAULT)) {
                                            return@with sourceServerFeature.isEnabled!!
                                        }

                                        if (!GeneralInputProcessor.validateDataType(this, DataTypes.BOOLEAN)) {
                                            return CommandInterfaceProcessor.sendCommandSyntax(
                                                commander,
                                                "Invalid data type!"
                                            )
                                        }

                                        return@with GeneralInputProcessor.convertDataType(this, DataTypes.BOOLEAN) as Boolean
                                    }.run {
                                        serverFeatureSession.setServerFeatureToggle(sourceServerFeature.featureName, this)

                                        saveToFile(toYAML(serverFeatureSession.getServerFeatureList(DataSourceType.EXTERNAL_SOURCE)))

                                        if (this) return@run "${ChatColor.GREEN}enabled"

                                        "${ChatColor.RED}disabled"
                                    }

                                    return CommandInterfaceProcessor.sendCommandSyntax(
                                        commander,
                                        "${ChatColor.GOLD}${sourceServerFeature.featureName} ${ChatColor.YELLOW}has been ${newToggleValue}${ChatColor.YELLOW}! " +
                                                "Please do `/neon serverfeaturesRemastered reload` to apply the changes!"
                                    )
                                }

                                CommandArgument.OPTION -> {
                                    if (argsLength != 6) {
                                        return CommandInterfaceProcessor.notifyInvalidArgument(commander, args)
                                    }

                                    val featureName = with(args[3]) {
                                        serverFeatureSession.getServerFeature(this)?.let {
                                            return@with it.featureName
                                        }

                                        return CommandInterfaceProcessor.sendCommandSyntax(
                                            commander,
                                            "${ChatColor.RED}No such server feature as '${ChatColor.GOLD}${this}${ChatColor.RED}'!"
                                        )
                                    }

                                    val featureOption = with(args[4]) {
                                        serverFeatureSession.getServerFeatureOption(featureName, this)?.let {
                                            return@with it
                                        }

                                        return CommandInterfaceProcessor.sendCommandSyntax(
                                            commander,
                                            "${ChatColor.RED}No such server feature option as '${ChatColor.GOLD}${this}${ChatColor.RED}'!"
                                        )
                                    }

                                    val optionDataType = featureOption.optionDataType.uppercase()

                                    val newOptionValue = with(args[5]) {
                                        if (CommandInterfaceProcessor.hasCommandArgument(this, CommandArgument.DEFAULT)) {
                                            return@with featureOption.optionDefaultValue
                                        }

                                        if (!GeneralInputProcessor.validateDataType(this, optionDataType)) {
                                            return CommandInterfaceProcessor.sendCommandSyntax(
                                                commander,
                                                "Invalid data type!"
                                            )
                                        }

                                        val optionMinValue = featureOption.optionMinValue
                                        val optionMaxValue = featureOption.optionMaxValue

                                        if (optionDataType != DataTypes.BOOLEAN.dataType) {
                                            if (!GeneralInputProcessor.validateDataRange(this, optionDataType, optionMinValue, optionMaxValue)) {
                                                return CommandInterfaceProcessor.sendCommandSyntax(
                                                    commander,
                                                    "Invalid data range!"
                                                )
                                            }
                                        }

                                        return@with GeneralInputProcessor.convertDataType(this, optionDataType)!!
                                    }.run {
                                        serverFeatureSession.setServerFeatureOptionValue(
                                            featureName,
                                            featureOption.optionName,
                                            this
                                        )

                                        saveToFile(toYAML(serverFeatureSession.getServerFeatureList(DataSourceType.EXTERNAL_SOURCE)))

                                        if (this !is Boolean) {
                                            return@run "${ChatColor.WHITE}$this"
                                        }

                                        if (this) return@run "${ChatColor.GREEN}$this"

                                        return@run "${ChatColor.RED}$this"
                                    }

                                    return CommandInterfaceProcessor.sendCommandSyntax(
                                        commander,
                                        "${ChatColor.GOLD}${featureName}:${featureOption.optionName} ${ChatColor.YELLOW}has been set to: $newOptionValue " +
                                                "${ChatColor.YELLOW}Please do `/neon serverfeaturesRemastered reload` to apply the changes!"
                                    )
                                }

                                else -> {
                                    return CommandInterfaceProcessor.notifyInvalidArgument(commander, args, 2)
                                }
                            }
                        } ?: return CommandInterfaceProcessor.notifyInvalidArgument(commander, args, 2)
                    }

                    else -> {
                        return CommandInterfaceProcessor.notifyInvalidArgument(commander, args, 1)
                    }
                }
            }
        }

        override fun getTabCompletion(commander: CommandSender, args: Array<out String>): MutableList<String> {
            val command = CommandAlias.NSERVER_FEATURES_REMASTERED.command
            val roleAccess = NRoleAccess.getCommandSenderRoleAccess(commander, command.permission)
            val serverFeatures by lazy {
                serverFeatureSession.getServerFeatureList(DataSourceType.INTERNAL_SOURCE)
            }

            when(val argLength = args.size) {
                2 -> {
                    return command.getCommandArgument(commander, argLength - 1, args[1], roleAccess = roleAccess)
                }

                3 -> {
                    command.getAllCommandArgument().find { it.argName.equals(args[1], true) }?.let {
                        if (!command.isArgumentAccessible(commander, it, roleAccess)) {
                            return super.getTabCompletion(commander, args)
                        }

                        return when(it) {
                            CommandArgument.GET, CommandArgument.SET -> {
                                command.getCommandArgument(commander, argLength - 1, args[2], roleAccess = roleAccess)
                            }

                            else -> {
                                super.getTabCompletion(commander, args)
                            }
                        }
                    }
                }

                4 -> {
                    val argIndex = argLength - 1

                    command.getAllCommandArgument().find { it.argName.equals(args[2], true) }?.let {
                        if (!command.isArgumentAccessible(commander, it, roleAccess)) {
                            return super.getTabCompletion(commander, args)
                        }

                        return when(it) {
                            CommandArgument.TOGGLE, CommandArgument.OPTION -> {
                                serverFeatures
                                    .map { feature -> feature.featureName }
                                    .filter { featureName -> featureName.startsWith(args[argIndex]) }
                                    .toMutableList()
                            }

                            else -> {
                                super.getTabCompletion(commander, args)
                            }
                        }
                    }
                }

                5 -> {
                    val argIndex = argLength - 1

                    when {
                        ((CommandInterfaceProcessor.hasCommandArgument(args[1], CommandArgument.GET)
                                && CommandInterfaceProcessor.hasCommandArgument(args[2], CommandArgument.OPTION))
                            || (CommandInterfaceProcessor.hasCommandArgument(args[1], CommandArgument.SET)
                                && CommandInterfaceProcessor.hasCommandArgument(args[2], CommandArgument.OPTION))) -> {

                                if (!command.isArgumentAccessible(commander, CommandArgument.GET, roleAccess)
                                    || !command.isArgumentAccessible(commander, CommandArgument.OPTION, roleAccess)
                                    || !command.isArgumentAccessible(commander, CommandArgument.SET, roleAccess)) {

                                    return super.getTabCompletion(commander, args)
                                }

                                return serverFeatures.find {
                                    it.featureName.equals(args[3], true)
                                }?.let {
                                    val serverFeatureOptions = it.options

                                    if (serverFeatureOptions.isEmpty()) super.getTabCompletion(commander, args)

                                    serverFeatureOptions
                                        .map { option -> option.optionName }
                                        .filter { option -> option.startsWith(args[argIndex]) }
                                        .toMutableList()
                                } ?: super.getTabCompletion(commander, args)
                            }

                        (CommandInterfaceProcessor.hasCommandArgument(args[1], CommandArgument.SET)
                            && CommandInterfaceProcessor.hasCommandArgument(args[2], CommandArgument.TOGGLE)) -> {
                                if (!command.isArgumentAccessible(commander, CommandArgument.SET, roleAccess)
                                    || !command.isArgumentAccessible(commander, CommandArgument.TOGGLE, roleAccess)) {
                                    return super.getTabCompletion(commander, args)
                                }

                                if (serverFeatures.find { it.featureName.equals(args[3], true) } != null) {
                                    return listOf("default", "true", "false")
                                        .filter { toggleValue -> toggleValue.startsWith(args[argIndex], true) }
                                        .toMutableList()
                                }
                        }
                    }
                }

                6 -> {
                    val argIndex = argLength - 1

                    if (!command.isArgumentAccessible(commander, CommandArgument.SET, roleAccess)
                        || !command.isArgumentAccessible(commander, CommandArgument.OPTION, roleAccess)) {
                        return super.getTabCompletion(commander, args)
                    }

                    if (CommandInterfaceProcessor.hasCommandArgument(args[1], CommandArgument.SET)
                        && CommandInterfaceProcessor.hasCommandArgument(args[2], CommandArgument.OPTION)) {
                        return listOf("default")
                            .filter { optionValue -> optionValue.startsWith(args[argIndex], true) }
                            .toMutableList()
                    }
                }
            }

            return super.getTabCompletion(commander, args)
        }
    }

    /**
     * Reload server features
     *
     * @return
     */
    private fun reloadServerFeatures(): ActionState {
        runCatching {
            loadServerFeatures()
            updateServerFeatureFile()
            neon.getAppInitializer().reInit()
        }.onFailure {
            it.printStackTrace()
            return ActionState(
                ActionStatus.SERVER_FEATURE_FAILED_TO_RELOAD,
                "${ChatColor.RED}Failed to reload server features! More info in the console!"
            )
        }

        return ActionState.success()
    }

    /**
     * Update server feature file to add/remove server feature.
     *
     */
    fun updateServerFeatureFile() {
        val sourceServerFeatures = serverFeatureSession.getServerFeatureList(DataSourceType.INTERNAL_SOURCE)
        val activeServerFeatures = serverFeatureSession.getServerFeatureList(DataSourceType.EXTERNAL_SOURCE)

        sourceServerFeatures.forEach {
            /* Stage 1: Check if the server feature in `sourceServerFeatures` exist in `activeServerFeatures`
            * If not exist, default value will remain */
            val sourceServerFeatureName = it.featureName
            val activeServerFeature = activeServerFeatures.find { active ->
                active.featureName == sourceServerFeatureName
            } ?: return@forEach

            /* Stage 2: Check if the isEnabled is 'Boolean' type */
            activeServerFeature.isEnabled.also { isEnabled ->
                if (isEnabled is Boolean) {
                    it.isEnabled = isEnabled
                }
            }

            /* Stage 3: Check if the 'option' property is null or empty
            * If it is, the default value will remain */
            if (activeServerFeature.options.isEmpty()) return@forEach

            /* Stage 4: Check if the options in `sourceServerFeatures` exist in `activeServerFeatures`
            * If not exist, the default value will remain */
            it.options.forEach sourceServerFeatureOption@ { sourceServerFeatureOption ->
                val sourceServerFeatureOptionName = sourceServerFeatureOption.optionName
                val activeServerFeatureOption = activeServerFeature.options.find { active ->
                    active.optionName == sourceServerFeatureOptionName
                } ?: return@sourceServerFeatureOption

                /* Stage 5: Check if the data type of option value equal to default data type
                * If not equal, the default value will remain */
                val actionOptionValue = activeServerFeatureOption.optionValue ?: return@sourceServerFeatureOption
                val activeOptionDataType = actionOptionValue.javaClass.simpleName

                if (activeOptionDataType != serverFeatureSession.getOptionDataType(sourceServerFeatureName, sourceServerFeatureOptionName)) {
                    return@sourceServerFeatureOption
                }

                /* Stage 5.1: Check if the data type of option value equal to 'Boolean'
                * If equal, the option value will skip the data range check and copy to 'sourceServerFeatures' */
                if (activeOptionDataType == DataTypes.BOOLEAN.dataType) {
                    sourceServerFeatureOption.optionValue = activeServerFeatureOption.optionValue
                    return@sourceServerFeatureOption
                }

                /* Stage 6: Check if the data range of option value valid as default
                 * If not valid, the default value will remain */
                if (!GeneralInputProcessor.validateDataRange(
                        actionOptionValue,
                        DataTypes.valueOf(serverFeatureSession.getOptionDataType(sourceServerFeatureName, sourceServerFeatureOptionName)!!.uppercase()),
                        serverFeatureSession.getOptionMinValue(sourceServerFeatureName, sourceServerFeatureOptionName),
                        serverFeatureSession.getOptionMaxValue(sourceServerFeatureName, sourceServerFeatureOptionName)
                    )) {

                    sourceServerFeatureOption.optionValue = serverFeatureSession.getOptionDefaultValue(sourceServerFeatureName, sourceServerFeatureOptionName)
                    return@sourceServerFeatureOption
                }

                sourceServerFeatureOption.optionValue = actionOptionValue
            }
        }

        saveToFile(toYAML(sourceServerFeatures))
        loadServerFeatures()
    }

    /**
     * Convert data class based of `ServerFeature` into YAML structure
     *
     * @param serverFeatures
     * @return
     */
    fun toYAML(serverFeatures: ArrayList<ServerFeature>): YamlFile {
        val sourceServerFeatureFile = YamlFile.loadConfigurationFromString(nServerFeaturesSourceFile.saveToString(), true) .apply {
            (this.implementation as SimpleYamlImplementation).dumperOptions.width = 1170
        }
        val sourceFeatureNames = sourceServerFeatureFile.getKeys(false)

        serverFeatures.filter { it.featureName in sourceFeatureNames }.forEach {
            val sourceServerFeatureSection = sourceServerFeatureFile.getConfigurationSection(it.featureName)
            val sourceConfigProperties = sourceServerFeatureSection.getKeys(false)

            /* Copy toggle value to Yaml file if available */
            if (sourceConfigProperties.contains(ConfigurationProperty.IS_ENABLED)) {
                sourceServerFeatureSection.set(ConfigurationProperty.IS_ENABLED, it.isEnabled)
            }

            @Suppress("UNCHECKED_CAST")
            /* Copy options value to Yaml file if available */
            if (sourceConfigProperties.contains(ConfigurationProperty.OPTIONS)) {
                val sourceOptionList = sourceServerFeatureSection.getList(ConfigurationProperty.OPTIONS)

                if (sourceOptionList.isNullOrEmpty()) return@forEach

                it.options.filter { option ->
                    val sourceOptions = sourceOptionList.map { sourceOption ->
                        (sourceOption as LinkedHashMap<*, *>).entries.first()
                    }

                    option.optionName in sourceOptions.map { sourceOption -> sourceOption.key }
                }.forEach { serverOption ->
                    val sourceOptions = sourceOptionList.map { sourceOption ->
                        (sourceOption as LinkedHashMap<String, Any>)
                    }

                    sourceOptions.find { option ->
                        option.entries.first().key == serverOption.optionName
                    }!![serverOption.optionName] = serverOption.optionValue as Any
                }
            }
        }

        return sourceServerFeatureFile
    }

    /**
     * Prepare server feature configuration from the internal source and external source and load it into memory.
     *
     */
    private fun loadServerFeatures() {
        /* Source Server Feature */
        with(buildServerFeature(nServerFeaturesSourceFile)) {
            serverFeatureSession.initServerFeature(this, DataSourceType.INTERNAL_SOURCE)

            val sourceServerFeatureComment = this.map {
                it.featureName to nServerFeaturesSourceFile.getComment(it.featureName)
            }

            val sourceServerFeatureCommands = sourceServerFeatureComment.map {
                val splitComment: Array<String> = if (!it.second.contains("\n")) arrayOf(it.second) else it.second.split("\n").toTypedArray()
                val serverFeatureDescription = splitComment.first()
                val serverFeatureCommand: String? = if (splitComment.size == 2) splitComment.last() else null

                ServerFeatureDetail(it.first, serverFeatureDescription, serverFeatureCommand)
            }.toCollection(ArrayList())

            serverFeatureSession.initServerFeatureDetail(sourceServerFeatureCommands)
        }

        /* Source Server Feature Option */
        serverFeatureSession.initServerFeatureOption(
            optionProperties.getValues(false).entries
                .map { ServerFeatureOption(it) }
                .toCollection(ArrayList())
        )

        /* Active Server Feature */
        runCatching {
            serverFeatureSession.initServerFeature(buildServerFeature(
                YamlFile.loadConfiguration(nServerFeaturesActiveFile, true).apply {
                    /* Adjust the length of each line for the output of YAML format */
                    (this.implementation as SnakeYamlImplementation).dumperOptions.width = 1170
                }
            ), DataSourceType.EXTERNAL_SOURCE)
        }.onFailure {
            /* Handling when there is a YAML parsing error */
            if (it is InvalidConfigurationException) {
                val backupNServerFeatureFile =  with(nServerFeaturesActiveFile) {
                    val currentDate = SimpleDateFormat("yyyy_MM_dd").format(Date())
                    var fileIndex = 1
                    var backupDestinationFile = File("${this.absolutePath}-${currentDate}-${fileIndex}.backup")

                    /* Loop to ensure the file index is identical */
                    while (backupDestinationFile.exists()) {
                        fileIndex++
                        backupDestinationFile = File("${this.absolutePath}-${currentDate}-${fileIndex}.backup")
                    }

                    this.copyTo(backupDestinationFile, false)
                }

                /* Try load with the source server feature config file */
                serverFeatureSession.initServerFeature(
                    buildServerFeature(
                    nServerFeaturesSourceFile
                ), DataSourceType.EXTERNAL_SOURCE)

                neon.logger.severe("Error while loading server features! Original source server features will be loaded instead!")
                neon.logger.warning("A backup of the nServerFeature config file will be saved at: ${backupNServerFeatureFile.absolutePath}")
            }
        }
    }

    /**
     * Build server feature from the YAML to editable structure-like data class.
     *
     * @param yamlFile
     * @return
     */
    private fun buildServerFeature(yamlFile: YamlFile): ArrayList<ServerFeature> {
        return yamlFile.getValues(false).entries.map { ServerFeature(it) }.toCollection(ArrayList())
    }

    /**
     * Save YAML content into the file.
     *
     * @param yamlFile
     */
    fun saveToFile(yamlFile: YamlFile) {
        /* Add single quote to all String value */
        yamlFile.getValues(true)
            .forEach {
                /* Check if the value is the configuration (or branch) */
                if (it.value is ConfigurationSection) {
                    if (!it.key.contains(ConfigurationProperty.OPTIONS)) return@forEach

                    val optionSection = it.value as ConfigurationSection

                    optionSection.getValues(false)
                        .filter { option -> option.value is String }
                        .forEach { option ->
                            optionSection.set(option.key, QuoteValue(option.value, QuoteStyle.SINGLE))
                        }
                }

                if (it.value is String && !it.key.contains(".${ConfigurationProperty.OPTIONS}")) {
                    yamlFile.set(it.key, QuoteValue(it.value, QuoteStyle.SINGLE))
                }
            }

        /* Write to the external nServerFeatures file */
        nServerFeaturesActiveFile.outputStream().bufferedWriter().use {
            it.write(yamlFile.saveToString().trim())
        }
    }
}