package com.islandstudio.neon.features.neonfeature

import com.islandstudio.neon.command.CommandAlias
import com.islandstudio.neon.command.ICommandDispatcher
import com.islandstudio.neon.command.option.ServerFeaturesCommandOption
import com.islandstudio.neon.command.processing.CommandSyntax
import com.islandstudio.neon.command.processing.CommandSyntaxHandler
import com.islandstudio.neon.command.properties.AccessibleCommand
import com.islandstudio.neon.command.properties.CommandFilter
import com.islandstudio.neon.experimental.gui.GuiManager
import com.islandstudio.neon.features.neonfeature.gui.NeonFeatureGui
import com.islandstudio.neon.features.neonfeature.gui.NeonFeatureGuiStateData
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.config.AppConfig
import com.islandstudio.neon.shared.core.config.component.ConfigDataRange
import com.islandstudio.neon.shared.core.config.component.ConfigNodeProperty
import com.islandstudio.neon.shared.core.config.obj.NeonServerFeaturesConfigObject
import com.islandstudio.neon.shared.core.config.property.NeonServerFeaturesConfigProperty
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.io.resource.NeonExternalResource
import com.islandstudio.neon.shared.utils.data.DataType
import com.islandstudio.neon.shared.utils.data.DataUtil
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.annotation.Single
import org.koin.core.component.inject

@Single
class NeonFeatureManager {
    private val serverFeaturesAppConfig by lazy {
        AppConfig(
            NeonExternalResource.NeonServerFeaturesFile,
            NeonServerFeaturesConfigObject(),
            NeonServerFeaturesConfigProperty::class
        )
    }

    companion object: IRunner, ICommandDispatcher, IComponentInjector {
        private val serverFeaturesCommandAlias = CommandAlias.ServerFeaturesAlias
        private val serverFeaturesManager by inject<NeonFeatureManager>()

        override fun run() {
            serverFeaturesManager.initialize()
        }

        override fun getCommandDispatcher(
            commander: CommandSender,
            accessibleCommands: ArrayList<AccessibleCommand>,
            args: Array<out String>
        ) {
            val argLength = args.size.apply {
                if (this != 1) return@apply

                if (commander !is Player) {
                    return CommandSyntaxHandler.sendCommandSyntax(commander, CommandSyntax.UNSUPPORTED_GUI_ACCESS)
                }

                val guiManager by inject<GuiManager>()

                runCatching {
                    guiManager.initGuiSession(commander, NeonFeatureGui::class).getGui().openGui()
                }.onFailure {
                    CommandSyntaxHandler.sendCommandSyntax(commander, CommandSyntax.UNEXPECTED_GUI_ERROR)
                    it.printStackTrace()
                }

                return
            }

            val accessibleCommandOptions = CommandAlias.getAccessibleCommandOptions(
                commander, accessibleCommands, serverFeaturesCommandAlias
            )

            serverFeaturesCommandAlias.onMatchOption(args[1]) {
                it?.let {
                    if (!CommandAlias.checkCommandOptionAccess(it, accessibleCommandOptions)) {
                        return@let CommandSyntaxHandler.sendCommandSyntax(commander, CommandSyntax.INVALID_PERMISSION)
                    }

                    when(it) {
                        ServerFeaturesCommandOption.GetToggle -> {
                            if (argLength != 3) {
                                return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argLength - 1)
                            }

                            val featureName = with(args[2]) {
                                serverFeaturesManager.getServerFeatureNames().find { x -> x == this }
                                    ?: return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander,
                                    "${ChatColor.RED}No such server feature as '${ChatColor.WHITE}${this}${ChatColor.RED}'!"
                                )
                            }

                            val toggleStatus = if (serverFeaturesManager.getFeatureToggle(featureName)) {
                                "${ChatColor.GREEN}enabled"
                            } else {
                                "${ChatColor.RED}disabled"
                            }

                            CommandSyntaxHandler.sendCommandSyntax(commander, "${ChatColor.GOLD}${featureName} ${ChatColor.WHITE}is currently $toggleStatus")
                        }

                        ServerFeaturesCommandOption.SetToggle -> {
                            if (argLength != 4) {
                                return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argLength - 1)
                            }

                            val featureName = with(args[2]) {
                                serverFeaturesManager.getServerFeatureNames()
                                    .find { x -> x == this } ?: return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander,
                                    "${ChatColor.RED}No such server feature as '${ChatColor.WHITE}${this}${ChatColor.RED}'!"
                                )
                            }

                            val toggleStatus = with(args[3]) {
                                val isToggled = if (this.equals(ServerFeaturesCommandOption.ServerFeaturesCommandOptionArgument.SetToggleDefault.optionArg, true)) {
                                    serverFeaturesManager.serverFeaturesAppConfig.getAllConfigProperty()
                                        .find { it.parentConfigKey == featureName }!!.defaultValue as Boolean
                                } else {
                                    DataUtil.convertDataType(this, DataType.Boolean) as Boolean?
                                        ?: return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argLength - 1)
                                }

                                isToggled to if (isToggled) "${ChatColor.GREEN}enabled" else "${ChatColor.RED}disabled"
                            }

                            serverFeaturesManager.setFeatureToggle(featureName, toggleStatus.first).also {
                                return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander,
                                    "${ChatColor.GOLD}${featureName} ${ChatColor.WHITE}has been ${toggleStatus.second}"
                                )
                            }
                        }

                        ServerFeaturesCommandOption.GetOption -> {
                            if (argLength != 4) {
                                return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argLength - 1)
                            }

                            val featureName = with(args[2]) {
                                serverFeaturesManager.getServerFeatureNames().find { x -> x == this }
                                    ?: return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander,
                                    "${ChatColor.RED}No such server feature as '${ChatColor.WHITE}${this}${ChatColor.RED}'!"
                                )
                            }

                            val featureOptionName = with(args[3]) {
                                serverFeaturesManager.getServerFeatureOptionNames(featureName).find { x -> x == this }
                                    ?: return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander,
                                    "${ChatColor.RED}No such option '${ChatColor.WHITE}${this}${ChatColor.RED}' " +
                                            "for this server feature!"
                                )
                            }

                            serverFeaturesManager.getFeatureOptionValue(featureName, featureOptionName)?.let {
                                val optionValue = if (DataUtil.validateDataType(it, DataType.Boolean)) {
                                    if (it as Boolean) "${ChatColor.GREEN}true"

                                    "${ChatColor.RED}false"
                                } else {
                                    "${ChatColor.GREEN}${it}"
                                }

                                CommandSyntaxHandler.sendCommandSyntax(commander,
                                    "${ChatColor.GOLD}${featureName}:${featureOptionName}${ChatColor.WHITE} is currently set to: $optionValue"
                                )
                            }
                        }

                        ServerFeaturesCommandOption.SetOption -> {
                            if (argLength != 5) {
                                return@onMatchOption CommandSyntaxHandler.alertInvalidCommandArg(commander, args, argLength - 1)
                            }

                            val featureName = with(args[2]) {
                                serverFeaturesManager.getServerFeatureNames().find { x -> x == this }
                                    ?: return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander,
                                        "${ChatColor.RED}No such server feature as '${ChatColor.WHITE}${this}${ChatColor.RED}'!"
                                    )
                            }

                            val featureOptionName = with(args[3]) {
                                serverFeaturesManager.getServerFeatureOptionNames(featureName).find { x -> x == this }
                                    ?: return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander,
                                        "${ChatColor.RED}No such option '${ChatColor.WHITE}${this}${ChatColor.RED}' " +
                                                "for this server feature!"
                                    )
                            }

                            val configProperty = serverFeaturesManager.serverFeaturesAppConfig.getAllConfigProperty()
                                .find { x -> x.parentConfigKey == "${featureName}.options" && x.keyName == featureOptionName }!!

                            val featureOptionValue = with(args[4]) {
                                val optionValue = if (this.equals(ServerFeaturesCommandOption.ServerFeaturesCommandOptionArgument.SetOptionDefault.optionArg, true)) {
                                    configProperty.defaultValue
                                } else { this }

                                DataUtil.convertDataType(optionValue!!, DataType.Boolean)?.let { convertedData ->
                                    if (convertedData as Boolean) {
                                        return@with convertedData to "${ChatColor.GREEN}true"
                                    }

                                    convertedData to "${ChatColor.RED}false"
                                } ?: (optionValue to "${ChatColor.GREEN}${optionValue}")
                            }

                            /* Data type validation */
                            if (!DataUtil.validateDataType(featureOptionValue.first, configProperty.dataType)) {
                                return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander,
                                    "${ChatColor.RED}Invalid data type!"
                                )
                            }

                            /* Data range validation */
                            if (!DataUtil.validateDataRange(
                                    featureOptionValue.first,
                                    configProperty.dataType,
                                    configProperty.dataRange.minValue,
                                    configProperty.dataRange.maxValue
                            )) {
                                return@onMatchOption CommandSyntaxHandler.sendCommandSyntax(commander,
                                    "${ChatColor.RED}Invalid data range! ${ChatColor.WHITE}Min: " +
                                            "${ChatColor.YELLOW}${configProperty.dataRange.minValue} ${ChatColor.WHITE}| Max: " +
                                            "${ChatColor.YELLOW}${configProperty.dataRange.maxValue}"
                                )
                            }

                            serverFeaturesManager.setFeatureOptionValue(featureName, featureOptionName, featureOptionValue.first)

                            CommandSyntaxHandler.sendCommandSyntax(commander,
                                "${ChatColor.GOLD}${featureName}:${featureOptionName}${ChatColor.WHITE} has been set to: ${featureOptionValue.second}"
                            )
                        }
                    }
                }
            }
        }

        override fun getTabCompletion(
            commander: CommandSender,
            accessibleCommand: ArrayList<AccessibleCommand>,
            args: Array<out String>
        ): MutableList<String> {
            val accessibleCommandOptions = CommandAlias.getAccessibleCommandOptions(
                commander,
                accessibleCommand,
                serverFeaturesCommandAlias
            )

            val argLength = args.size
            val argIndex = argLength - 1

            val booleanValueList = arrayListOf(
                "default",
                ConfigDataRange.DataRangeBoolean.minValue.toString(),
                ConfigDataRange.DataRangeBoolean.maxValue.toString()
            )

            return when(argLength) {
                2 -> {
                    CommandAlias.getAccessibleCommandOptions(
                        commander,
                        accessibleCommand,
                        serverFeaturesCommandAlias,
                        CommandFilter(argIndex, args[argIndex])
                    )
                }

                3 -> {
                    serverFeaturesCommandAlias.onMatchOption(args[1]) {
                        it?.let {
                            if (!CommandAlias.checkCommandOptionAccess(it, accessibleCommandOptions)) return@let

                            when(it) {
                                ServerFeaturesCommandOption.GetOption,
                                ServerFeaturesCommandOption.SetOption -> {
                                    return@onMatchOption serverFeaturesManager.getServerFeatureNames()
                                        .filter { x -> serverFeaturesManager.getServerFeatureOptionNames(x).isNotEmpty() }
                                        .filter { x -> x.startsWith(args[argIndex], true) }
                                        .toMutableList()
                                }

                                ServerFeaturesCommandOption.GetToggle,
                                ServerFeaturesCommandOption.SetToggle -> {
                                    return@onMatchOption serverFeaturesManager.getServerFeatureNames()
                                        .filter { x -> x.startsWith(args[argIndex], true) }
                                        .toMutableList()
                                }
                            }
                        }

                        super.getTabCompletion(commander, accessibleCommand, args)
                    }
                }

                4 -> {
                    serverFeaturesCommandAlias.onMatchOption(args[1]) {
                        it?.let {
                            if (!CommandAlias.checkCommandOptionAccess(it, accessibleCommandOptions)) return@let

                            when(it) {
                                ServerFeaturesCommandOption.SetToggle -> {
                                    return@onMatchOption booleanValueList
                                }

                                ServerFeaturesCommandOption.GetOption,
                                ServerFeaturesCommandOption.SetOption -> {
                                    val featureName = args[2]

                                    return@onMatchOption serverFeaturesManager.getServerFeatureOptionNames(featureName)
                                }

                                else -> return@let
                            }
                        }

                        super.getTabCompletion(commander, accessibleCommand, args)
                    }
                }

                5 -> {
                    serverFeaturesCommandAlias.onMatchOption(args[1]) {
                        it?.let {
                            if (!CommandAlias.checkCommandOptionAccess(it, accessibleCommandOptions)) return@let

                            when(it) {
                                ServerFeaturesCommandOption.SetOption -> {
                                    val featureName = args[2]
                                    val featureOptionName = args[3]

                                    serverFeaturesManager.getServerFeatureOptions(featureName)
                                        .find { x -> x.keyName.equals(featureOptionName, true) }
                                        ?.let {
                                            if (it.dataType == DataType.Boolean) {
                                                return@onMatchOption booleanValueList
                                            }

                                            return@onMatchOption arrayListOf("default")
                                        }
                                }

                                else -> return@let
                            }
                        }

                        super.getTabCompletion(commander, accessibleCommand, args)
                    }
                }

                else -> super.getTabCompletion(commander, accessibleCommand, args)
            }
        }
    }

    fun initialize() {
    }

    fun getFeatureToggle(featureName: String, configNodeProperties: ArrayList<ConfigNodeProperty>? = null): Boolean {
        return configNodeProperties?.let {
            serverFeaturesAppConfig.getConfigNode(
                configNodeProperties,
                featureName,
                "isEnabled"
            )?.value()?.let { it as Boolean } ?: false
        }
            ?: serverFeaturesAppConfig.getConfigNode(featureName, "isEnabled")!!.value() as Boolean
    }

    fun setFeatureToggle(configNodeProperties: ArrayList<ConfigNodeProperty>, featureName: String, toggle: Boolean): Boolean {
        return serverFeaturesAppConfig.getConfigNode(
            configNodeProperties,
            featureName,
            "isEnabled"
        )?.updateConfigNodeValue(toggle) ?: return false
    }

    fun saveFeatureChanges(neonFeatureGuiStateData: NeonFeatureGuiStateData? = null) {
        neonFeatureGuiStateData?.let {
            serverFeaturesAppConfig.updateConfigNodeProperties(it.featureConfig)
        }

        serverFeaturesAppConfig.saveConfig()
    }

    fun setFeatureToggle(featureName: String, toggle: Boolean): Boolean {
        serverFeaturesAppConfig.getConfigNode(featureName, "isEnabled")
            ?.updateConfigNodeValue(toggle) ?: return false

        saveFeatureChanges()
        return true
    }

    fun getFeatureOptionValue(featureName: String, optionName: String): Any? {
        return serverFeaturesAppConfig.getConfigNode("${featureName}.options", optionName)
            ?.value()

    }

    fun setFeatureOptionValue(featureName: String, optionName: String, optionValue: Any): Boolean? {
          return serverFeaturesAppConfig.getConfigNode("${featureName}.options", optionName)
              ?.let {
                  if (it.updateConfigNodeValue(optionValue)) {
                      serverFeaturesAppConfig.saveConfig()
                      return@let true
                  }

                  false
              }
    }

    fun getFeatureConfigProperties(): ArrayList<NeonServerFeaturesConfigProperty<*>> {
        return serverFeaturesAppConfig.getAllConfigProperty()
    }

    fun getFeatureConfigNodeProperties(): ArrayList<ConfigNodeProperty> {
        return serverFeaturesAppConfig.cloneConfigNodeProperties() as ArrayList<ConfigNodeProperty>
    }

    fun getServerFeatureNames(): ArrayList<String> {
        return getFeatureConfigProperties()
            .filter { !it.parentConfigKey.endsWith(".options") }
            .map { it.parentConfigKey }
            .toCollection(ArrayList())
    }

    fun getServerFeatureOptionNames(serverFeatureName: String): ArrayList<String> {
        return getServerFeatureOptions(serverFeatureName)
            .map { it.keyName }
            .toCollection(ArrayList())
    }

    fun getServerFeatureOptions(serverFeatureName: String): ArrayList<NeonServerFeaturesConfigProperty<*>> {
        return getFeatureConfigProperties()
            .filter { it.parentConfigKey.equals("${serverFeatureName}.options", true) }
            .toCollection(ArrayList())
    }
}