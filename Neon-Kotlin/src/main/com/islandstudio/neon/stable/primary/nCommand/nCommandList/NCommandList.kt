package com.islandstudio.neon.stable.primary.nCommand.nCommandList

import com.islandstudio.neon.stable.core.init.NConstructor
import com.islandstudio.neon.stable.core.network.NPacketProcessor
import com.islandstudio.neon.stable.primary.nCommand.Commands
import com.islandstudio.neon.stable.utils.NIdGenerator
import com.islandstudio.neon.stable.utils.identifier.NeonKeyGeneral
import com.islandstudio.neon.stable.utils.reflection.NMSRemapped
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.inventory.AbstractContainerMenu
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.filter.RegexFilter
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.persistence.PersistentDataType
import org.simpleyaml.configuration.file.YamlFile
import org.simpleyaml.utils.SupplierIO
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ceil

object NCommandList {
    private val plugin = NConstructor.plugin

    /* An ID for book identification to ensure the book that server is looking for is the correct one
     * Everytime server reload will regenerate a new one.
     */
    private val nCommandListSessionID = NIdGenerator.generateNId(NIdGenerator.NIdType.COMMAND_LIST_SESSION)

    /* Command book item */
    private val commandListBooks: EnumMap<Commands.CommandTargetUser, ItemStack> = EnumMap(Commands.CommandTargetUser::class.java)
    private val commandUsageBooks: EnumMap<Commands.CommandAlias, ItemStack> = EnumMap(Commands.CommandAlias::class.java)

    /* Command UI session
    * > Used to keep track the player last seen page, and the navigation */
    private val commandUISessions: ConcurrentHashMap<UUID, CommandUISession> = ConcurrentHashMap()

    enum class CommandUITypes(val typeName: String) {
        COMMAND_LIST("Command List"),
        COMMAND_USAGES("Command Usages")
    }

    private enum class CommandUINavigationCommand(val command: String) {
        SHOW_COMMAND_USAGES("/neon showCommandUsages"),
        BACK_TO_COMMAND_LIST("/neon backToCommandList")
    }

    private enum class ClickedButton {
        NONE,
        PREVIOUS_PAGE,
        NEXT_PAGE
    }

    object Handler {
        /**
         * Initialization for nCommandList
         *
         */
        fun run() {
            initCommandUIContent()
            initCommandFilter()

            NConstructor.registerEventProcessor(EventProcessor())
        }

        /**
         * Initialize command list and command usage, then store it as Book item in memory.
         *
         */
        private fun initCommandUIContent() {
            val playerCommandYamlFile = YamlFile.loadConfiguration(SupplierIO.Reader {
                this::class.java.classLoader.getResourceAsStream("resources/nCommand/commands-Player.yml")!!.reader() })
            val adminCommandYamlFile = YamlFile.loadConfiguration(SupplierIO.Reader {
                this::class.java.classLoader.getResourceAsStream("resources/nCommand/commands-Admin.yml")!!.reader() })

            val playerCommandList: ArrayList<Commands.CommandDetail> =
                ArrayList(playerCommandYamlFile.getValues(false).entries
                    .map { Commands.CommandDetail(it) })

            val adminCommandList: ArrayList<Commands.CommandDetail> =
                ArrayList(adminCommandYamlFile.getValues(false).entries
                    .map { Commands.CommandDetail(it) })

            adminCommandList.addAll(playerCommandList)

            commandListBooks[Commands.CommandTargetUser.ADMIN] = createCommandListBook(adminCommandList)
            commandListBooks[Commands.CommandTargetUser.PLAYER] = createCommandListBook(playerCommandList)

            Commands.CommandAlias.entries.forEach {
                commandUsageBooks[it] = adminCommandList.find { commandDetail ->
                    commandDetail.commandName.equals(it.aliasName, true) }?.let { it1 ->
                    createCommandUsagesBook(
                        it1.commandUsageList, it)
                } ?: return@forEach
            }
        }

        /**
         * Create command list book
         *
         * @param commandList
         * @return
         */
        private fun createCommandListBook(commandList: ArrayList<Commands.CommandDetail>): ItemStack {
            /* ## Stage 1: Book initialization ## */
            val commandListBook = ItemStack(Material.WRITTEN_BOOK)
            val commandListBookMeta: BookMeta = commandListBook.itemMeta as BookMeta

            commandListBookMeta.author = Commands.CommandUIBookProperties.BOOK_AUTHOR
            commandListBookMeta.title = Commands.CommandUIBookProperties.COMMAND_LIST_BOOK_TITLE

            val pageComponents: ArrayList<BaseComponent> = ArrayList()

            var pageIndex = 1
            var maxPageCount = 1

            /* ## Stage 2: Book landing page initialization ## */
            /* Title properties */
            val landingPageTitle = TextComponent(
                " ${ChatColor.DARK_GRAY}${ChatColor.BOLD}${ChatColor.UNDERLINE}Neon Command List\n")

            /* Horizontal Line */
            val horizontalLine = TextComponent("${ChatColor.UNDERLINE}                             \n")

            /* Control */
            val control = TextComponent("        ${ChatColor.DARK_GRAY}${ChatColor.BOLD}${ChatColor.UNDERLINE}" +
                    "Control:\n\n")
            control.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${ChatColor.YELLOW}Controls used for navigation."))

            /* Button Hints */
            val commandButton = TextComponent("        ${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}Command\n\n")
            val previousButton = TextComponent("        ${ChatColor.BOLD}[${ChatColor.DARK_GREEN}${ChatColor.BOLD}↩${ChatColor.BLACK}${ChatColor.BOLD}]   ")
            val nextButton = TextComponent("${ChatColor.BOLD}[${ChatColor.DARK_GREEN}${ChatColor.BOLD}↪${ChatColor.BLACK}${ChatColor.BOLD}]\n\n")
            val closeButton = TextComponent("         ${ChatColor.BOLD}[${ChatColor.DARK_GREEN}${ChatColor.BOLD}Done${ChatColor.BLACK}${ChatColor.BOLD}]\n\n")

            /* Hover to show text */
            commandButton.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${ChatColor.YELLOW}Click the command to display its usages"))
            previousButton.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${ChatColor.YELLOW}Navigate to previous page if available"))
            nextButton.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${ChatColor.YELLOW}Navigate to next page if available"))
            closeButton.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${ChatColor.YELLOW}Close"))

            pageComponents.addAll(listOf(landingPageTitle, horizontalLine, control, commandButton, previousButton, nextButton, closeButton))
            commandListBookMeta.spigot().addPage(pageComponents.toArray(arrayOfNulls<BaseComponent>(pageComponents.size)))
            pageComponents.clear()

            /* Calculate how many pages are required to display commands */
            maxPageCount += (ceil(commandList.size.toDouble() / 6)).toInt()
            var lineCount = 0

            /* ## Stage 3.1: Content body initialization (Commands) ## */
            commandList.forEachIndexed { commandIndex, commandDetail ->
                val commandName = commandDetail.commandName

                /* Limit 6 commands per page */
                if (lineCount == 6) {
                    commandListBookMeta.spigot().addPage(pageComponents.toArray(arrayOfNulls<BaseComponent>(pageComponents.size)))
                    pageComponents.clear()

                    pageIndex++
                    lineCount = 1
                }

                val command = if (commandIndex == commandList.lastIndex || lineCount == 5) TextComponent(
                    "${commandIndex + 1}. ${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}${commandName}"
                ) else
                    TextComponent(
                        "${commandIndex + 1}. ${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}${commandName}\n\n"
                    )

                /* ## Stage 3.2: Initialize command detail (command name with prefix), and target command usage ## */
                command.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text("${ChatColor.GREEN}${ChatColor.BOLD}${commandDetail.commandWithPrefix}"))
                command.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "${CommandUINavigationCommand.SHOW_COMMAND_USAGES.command} $commandName")

                pageComponents.add(command)

                /* Add the last page */
                if (pageIndex == maxPageCount - 1 && commandDetail == commandList.last()) {
                    commandListBookMeta.spigot().addPage(pageComponents.toArray(arrayOfNulls<BaseComponent>(pageComponents.size)))
                    pageComponents.clear()
                    return@forEachIndexed
                }

                lineCount++
            }

            /* ## Stage 4: Add session ID to the item ## */
            commandListBookMeta.persistentDataContainer.set(NeonKeyGeneral.NCOMMAND_LIST_PROPERTY_ID.key, PersistentDataType.STRING, nCommandListSessionID)
            commandListBook.itemMeta = commandListBookMeta

            return commandListBook
        }

        /**
         * Create command usages book
         *
         * @param commands The targeted command.
         * @return Command usage book item.
         */
        private fun createCommandUsagesBook(commandUsages: ArrayList<Commands.CommandDetail.CommandUsage>, commands: Commands.CommandAlias): ItemStack {
            /* ## Stage 1: Book initialization ## */
            val commandUsageBook = ItemStack(Material.WRITTEN_BOOK)
            val commandUsageBookMeta: BookMeta = commandUsageBook.itemMeta as BookMeta

            commandUsageBookMeta.author = Commands.CommandUIBookProperties.BOOK_AUTHOR
            commandUsageBookMeta.title = Commands.CommandUIBookProperties.COMMAND_USAGE_BOOK_TITLE

            val pageComponents: ArrayList<BaseComponent> = ArrayList()

            var pageIndex = 1
            var maxPageCount = 1

            /* ## Stage 2: Book landing page initialization ## */
            /* Title properties */
            val landingPageTitle = TextComponent(
                "    ${ChatColor.DARK_GRAY}${ChatColor.BOLD}${ChatColor.UNDERLINE}Neon Command${ChatColor.RESET}\n         " +
                        "${ChatColor.DARK_GRAY}${ChatColor.BOLD}${ChatColor.UNDERLINE}Usages\n\n"
            )

            /* Target command properties */
            val targetCommand = TextComponent("${ChatColor.BOLD}> ${ChatColor.GOLD}${ChatColor.UNDERLINE}${ChatColor.BOLD}${commands.aliasName}\n")
            targetCommand.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${ChatColor.GREEN}${ChatColor.BOLD}/neon ${commands.aliasName}"))

            /* Horizontal Line */
            val horizontalLine = TextComponent("${ChatColor.UNDERLINE}                             \n")

            /* Control */
            val control = TextComponent("        ${ChatColor.DARK_GRAY}${ChatColor.BOLD}${ChatColor.UNDERLINE}" +
                    "Control:\n\n")
            control.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${ChatColor.YELLOW}Controls used for navigation."))

            /* Button Hints */
            val previousButton = TextComponent("        ${ChatColor.BOLD}[${ChatColor.DARK_GREEN}${ChatColor.BOLD}↩${ChatColor.BLACK}${ChatColor.BOLD}]   ")
            val nextButton = TextComponent("${ChatColor.BOLD}[${ChatColor.DARK_GREEN}${ChatColor.BOLD}↪${ChatColor.BLACK}${ChatColor.BOLD}]\n\n")
            val closeButton = TextComponent("         ${ChatColor.BOLD}[${ChatColor.DARK_GREEN}${ChatColor.BOLD}Done${ChatColor.BLACK}${ChatColor.BOLD}]\n\n")
            val switchUIButton = TextComponent("         ${ChatColor.BOLD}[${ChatColor.DARK_GREEN}${ChatColor.BOLD}Back${ChatColor.BLACK}${ChatColor.BOLD}]")

            /* Hover to show text */
            previousButton.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${ChatColor.YELLOW}Navigate to previous page if available"))
            nextButton.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${ChatColor.YELLOW}Navigate to next page if available"))
            closeButton.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${ChatColor.YELLOW}Close"))
            switchUIButton.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${ChatColor.YELLOW}Switch back to command list"))

            switchUIButton.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, CommandUINavigationCommand.BACK_TO_COMMAND_LIST.command)

            pageComponents.addAll(listOf(landingPageTitle, targetCommand, horizontalLine, control, previousButton, nextButton, closeButton, switchUIButton))
            commandUsageBookMeta.spigot().addPage(pageComponents.toArray(arrayOfNulls<BaseComponent>(pageComponents.size)))
            pageComponents.clear()

            /* Calculate how many pages are required to display command usage */
            maxPageCount += (ceil(commandUsages.size.toDouble() / 6)).toInt()
            var lineCount = 0

            /* ## Stage 3.1: Content body initialization (Command Usages) ## */
            commandUsages.forEachIndexed { usageIndex, usage ->
                /* Limit 6 command usages per page
                * Add [Back] button at the end of the page
                */
                if (lineCount == 6) {
                    val newLines = TextComponent(getNewLines(lineCount))

                    pageComponents.add(newLines)
                    pageComponents.add(switchUIButton)
                    commandUsageBookMeta.spigot().addPage(pageComponents.toArray(arrayOfNulls<BaseComponent>(pageComponents.size)))
                    pageComponents.clear()

                    pageIndex++
                    lineCount = 0
                }

                val showUsage = if (usageIndex == commandUsages.lastIndex || lineCount == 5) TextComponent(
                    "${usageIndex + 1}. ${ChatColor.BOLD} ▶${ChatColor.GOLD}${ChatColor.BOLD} Show Usage ${ChatColor.BLACK}◀"
                ) else
                    TextComponent(
                    "${usageIndex + 1}. ${ChatColor.BOLD} ▶${ChatColor.GOLD}${ChatColor.BOLD} Show Usage ${ChatColor.BLACK}◀\n\n"
                )

                /* ## Stage 3.2: Initialize command usage detail (syntax, description) ## */
                showUsage.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text("${ChatColor.GRAY}Syntax:\n"),
                    Text("${ChatColor.GREEN}${setColorField(usage.usageSyntax)}\n\n"),
                    Text("${ChatColor.GRAY}Description:\n"),
                    Text("${ChatColor.GREEN}${usage.usageDescription}"))

                pageComponents.add(showUsage)

                lineCount++

                /* Add [Back] button at the end of the page on the last page */
                if (pageIndex == maxPageCount - 1 && usage == commandUsages.last()) {
                    val newLines = TextComponent(getNewLines(lineCount))

                    pageComponents.addAll(listOf(newLines, switchUIButton))
                    commandUsageBookMeta.spigot().addPage(pageComponents.toArray(arrayOfNulls<BaseComponent>(pageComponents.size)))
                    pageComponents.clear()
                    return@forEachIndexed
                }
            }

            /* ## Stage 4: Add session ID to the item ## */
            commandUsageBookMeta.persistentDataContainer.set(NeonKeyGeneral.NCOMMAND_LIST_PROPERTY_ID.key, PersistentDataType.STRING, nCommandListSessionID)
            commandUsageBook.itemMeta = commandUsageBookMeta

            return commandUsageBook
        }

        /**
         * Get how many new lines are required before \[Back] button is place on.
         *
         * @param lineCount
         * @return
         */
        private fun getNewLines(lineCount: Int): String {
            if (lineCount < 0 || lineCount > 6) return ""

            val maxNewLineCount = 14
            val newLineCount = maxNewLineCount - (lineCount * 2)
            var newLines = ""

            for (i in 0 until newLineCount) {
                newLines += "\n"
            }

            return newLines
        }

        /**
         * Initialize command filter for internal command. (Unregister usable command)
         *
         */
        private fun initCommandFilter() {
            val loggerContext = LogManager.getContext(ServerGamePacketListenerImpl::class.java.classLoader, false) as LoggerContext
            val regexFilter = RegexFilter.createFilter(".*issued server command: (?:${CommandUINavigationCommand.SHOW_COMMAND_USAGES.command}" +
                    "|${CommandUINavigationCommand.BACK_TO_COMMAND_LIST.command}).*", arrayOf("CASE_INSENSITIVE"), false, Filter.Result.DENY, Filter.Result.ACCEPT)

            if (!loggerContext.rootLogger.filters.asSequence().toList().contains(regexFilter)) {
                loggerContext.addFilter(regexFilter)
            }

            if (!regexFilter.isStarted) regexFilter.start()
        }

        /**
         * Set color for value field of the command syntax.
         *
         * @param syntax Command syntax.
         * @return Colored value field.
         */
        private fun setColorField(syntax: String): String {
            /* Check if the syntax contains '<' and '>', which is the start and the end of the argument field */
            if (!(syntax.contains("<") && syntax.contains(">"))) return syntax

            val fields: MutableMap<Int, String> = TreeMap()

            syntax.split(" ").forEach {
                /* Filtered out which part of the syntax contain '<' and '>' */
                if (!(it.contains("<") && it.contains(">"))) return@forEach

                if (fields.containsValue(it)) return@forEach

                /* Store the field and its index */
                val indexPair = syntax.findAnyOf(listOf(it))

                fields[indexPair!!.first] = it
            }

            var newSyntax = syntax

            /* Replace the field with desired color */
            fields.values.forEach {
                newSyntax = newSyntax.replace(it, "${ChatColor.YELLOW}${it}${ChatColor.GREEN}")
            }

            return newSyntax
        }
    }

    /**
     * Get command list book for the target player.
     *
     * @param player The target player.
     * @return The command list book item
     */
    fun getCommandListBook(player: Player): ItemStack {
        if (player.isOp) return commandListBooks[Commands.CommandTargetUser.ADMIN]!!

        return commandListBooks[Commands.CommandTargetUser.PLAYER]!!
    }

    /**
     * Get command usage book for the target command
     *
     * @param commandAlias The target command
     * @return
     */
    private fun getCommandUsageBook(commandAlias: Commands.CommandAlias): ItemStack = commandUsageBooks[commandAlias]!!

    /**
     * Remove player's Command UI Session once they close the Command UI.
     *
     * @param player The target player
     */
    fun removeCommandUISession(player: Player) {
        hasCommandUISession(player)?.let {
            commandUISessions.remove(player.uniqueId)
        }
    }

    /**
     * Check if the player has the Command UI Session, if exists, return the session
     * else return null
     *
     * @param player The target player
     * @return Command UI Session or null
     */
    fun hasCommandUISession(player: Player): CommandUISession? = commandUISessions.entries.find { it.key == player.uniqueId }?.value

    /**
     * Display Command UI to the target player.
     *
     * @param player The player who perform the command /neon
     * @param bookItem The targeted Command UI book item
     * @param commandUIType The targeted Command UI type
     * @param commandAlias Command if the target Command UI type is CommandUITypes.COMMAND_USAGES
     */
    fun displayCommandUI(player: Player, bookItem: ItemStack, commandUIType: CommandUITypes, commandAlias: Commands.CommandAlias?) {
        /* Stage 1: Lectern inventory initialization */
        val lecternInventory = plugin.server.createInventory(player, InventoryType.LECTERN)
        lecternInventory.addItem(bookItem)

        player.openInventory(lecternInventory)

        /* Stage 2: Command UI session initialization */
        val commandUISession = addOrGetCommandUISession(player)
        val openedUIWindow = getOpenedUIWindow(player) ?: return
        val commandUI = commandUISession.addOrGetCommandUI(commandUIType, openedUIWindow)!!

        /*  Stage 3.1: Command UI session property updates */
        commandUISession.updateOrGetCurrentActiveUI(commandUIType)
        commandUI.updateOrGetCommandAlias(commandAlias)

        val lastSeenPage = commandUISession.updateOrGetLastSeenPage(false, commandUIType, commandAlias)

        if (commandUISession.updateOrGetDoSwitchUI(null)) {
            commandUISession.updateOrGetDoSwitchUI(false)
            commandUI.updateOrGetCommandUIWindow(openedUIWindow)
        }

        if (lastSeenPage == 0) return

        /* Stage 3.2: Command UI page updates */
        sendPageUpdate(commandUISession, ClickedButton.NONE, player)
        commandUI.updateOrGetCurrentPage()
    }

    /**
     * Perform navigation for Command UI.
     *
     * @param buttonState Clicked button state
     * @param nPlayer Minecraft player who clicked the button
     */
    fun navigateCommandUI(buttonState: Int, nPlayer: ServerPlayer) {
        val player: Player = nPlayer.javaClass.getMethod(NMSRemapped.Mapping.NMS_GET_BUKKIT_ENTITY.remapped).invoke(nPlayer) as Player

        if (hasCommandUISession(player) == null) return

        val commandUISession = addOrGetCommandUISession(player)

        when (buttonState) {
            1 -> {
                sendPageUpdate(commandUISession, ClickedButton.PREVIOUS_PAGE, player)
            }

            2 -> {
                sendPageUpdate(commandUISession, ClickedButton.NEXT_PAGE, player)
            }
        }
    }

    /**
     * Add or get command UI session.
     *
     * @param player The target player.
     * @return Newly created Command UI Session or existing Command UI Session
     */
    private fun addOrGetCommandUISession(player: Player): CommandUISession {
        var commandUISession = hasCommandUISession(player)

        commandUISession?.let { return it }
        commandUISession = CommandUISession(player)

        commandUISessions[player.uniqueId] = commandUISession

        return hasCommandUISession(player)!!
    }

    /**
     * Get opened UI window
     *
     * @param player
     * @return Opened UI Window.
     */
    private fun getOpenedUIWindow(player: Player): AbstractContainerMenu? {
        val nPlayer = NPacketProcessor.getNPlayer(player)

        val openedUI = nPlayer.javaClass.superclass.getField(NMSRemapped.Mapping.NMS_CONTAINER_BASE.remapped)[nPlayer] as AbstractContainerMenu
        val uiView = openedUI.bukkitView

        if (uiView.type != InventoryType.LECTERN) return null
        val bookItem = uiView.topInventory.getItem(0) ?: return null
        val bookMeta = bookItem.itemMeta ?: return null

        if (!bookMeta.persistentDataContainer.has(NeonKeyGeneral.NCOMMAND_LIST_PROPERTY_ID.key, PersistentDataType.STRING)) return null

        if (bookMeta.persistentDataContainer.get(NeonKeyGeneral.NCOMMAND_LIST_PROPERTY_ID.key, PersistentDataType.STRING) != nCommandListSessionID) return null

        return openedUI
    }

    /**
     * Perform Command UI switching
     *
     * @param e PlayerCommandPreprocessEvent
     */
    private fun switchCommandUI(e: PlayerCommandPreprocessEvent) {
        val player = e.player

        if (hasCommandUISession(player) == null) return

        when {
            e.message.contains(CommandUINavigationCommand.SHOW_COMMAND_USAGES.command, true) -> {
                val commandUISession = addOrGetCommandUISession(player)
                val commandAlias = getTargetCommandUsage(e.message)!!

                commandUISession.updateOrGetDoSwitchUI(true)
                commandUISession.updateOrGetStayOpen(true)
                commandUISession.updateOrGetLastUIType(CommandUITypes.COMMAND_LIST.typeName)

                displayCommandUI(player, getCommandUsageBook(commandAlias), CommandUITypes.COMMAND_USAGES, commandAlias)

                e.isCancelled = true
            }

            e.message.equals(CommandUINavigationCommand.BACK_TO_COMMAND_LIST.command, true) -> {
                val commandUISession = addOrGetCommandUISession(player)

                commandUISession.updateOrGetDoSwitchUI(true)
                commandUISession.updateOrGetStayOpen(true)
                commandUISession.updateOrGetLastUIType(CommandUITypes.COMMAND_USAGES.typeName)

                displayCommandUI(player, getCommandListBook(player), CommandUITypes.COMMAND_LIST, null)

                e.isCancelled = true
            }
        }
    }

    /**
     * Send page update to the Command UI.
     *
     * @param commandUISession Player's Command UI session
     * @param clickedButton Clicked button
     * @param player The target player who using the Command UI
     */
    private fun sendPageUpdate(commandUISession: CommandUISession, clickedButton: ClickedButton, player: Player) {
        /* Command UI session properties used for validation before sending the page update */
        val currentActiveUI = commandUISession.updateOrGetCurrentActiveUI(null)
        val commandUI = commandUISession.addOrGetCommandUI(currentActiveUI, null) ?: return
        val commandAlias = commandUI.updateOrGetCommandAlias(null)
        val lastSeenPage = commandUISession.updateOrGetLastSeenPage(false, currentActiveUI, commandAlias)

        var newPageNumber = 0

        /* Page calculation */
        when (clickedButton) {
            ClickedButton.PREVIOUS_PAGE -> {
                if (lastSeenPage <= 0) return

                newPageNumber = lastSeenPage - 1
            }

            ClickedButton.NEXT_PAGE -> {
                if (lastSeenPage >= 100) return

                newPageNumber = lastSeenPage + 1
            }

            ClickedButton.NONE -> {
                if (lastSeenPage < 0 || lastSeenPage > 100) return

                newPageNumber = lastSeenPage
            }
        }

        /* Page updates */
        commandUI.updateServerSideCurrentPage(newPageNumber)
        commandUISession.updateOrGetLastSeenPage(true, currentActiveUI, commandAlias)

        NPacketProcessor.sendGamePacket(player, ClientboundContainerSetDataPacket(commandUI.commandUIId, 0, newPageNumber))
        return
    }

    /**
     * Get target command usage when player switch to command usage UI.
     *
     * @param switchUICommand The command that used internally for switch Command UI
     * @return The targeted command
     */
    private fun getTargetCommandUsage(switchUICommand: String): Commands.CommandAlias? {
        /* Split and filter out the targeted command from the switch UI command */
        val filteredSwitchUICommand = switchUICommand.split(" ").run {
            if (this.size != 3) return null

            return@run this[2]
        }

        return Commands.CommandAlias.entries.find { it.aliasName.equals(filteredSwitchUICommand, true) }
    }

    private class EventProcessor: Listener {
        @EventHandler
        private fun onPlayerCommandPreprocess(e: PlayerCommandPreprocessEvent) {
            switchCommandUI(e)
        }

        @EventHandler
        private fun onPlayerQuit(e: PlayerQuitEvent) {
            removeCommandUISession(e.player)
        }

        @EventHandler
        private fun onInventoryClose(e: InventoryCloseEvent) {
            val player = e.player as Player
            val commandUISession = hasCommandUISession(player) ?: return
            val inventoryViewType = e.view.type

            if (inventoryViewType == InventoryType.CRAFTING
                || inventoryViewType == InventoryType.CREATIVE) return removeCommandUISession(player)

            val book = e.inventory.getItem(0) ?: return
            val bookMeta = book.itemMeta!! as BookMeta
            if (!bookMeta.persistentDataContainer.has(NeonKeyGeneral.NCOMMAND_LIST_PROPERTY_ID.key, PersistentDataType.STRING)) return
            if (bookMeta.persistentDataContainer.get(NeonKeyGeneral.NCOMMAND_LIST_PROPERTY_ID.key, PersistentDataType.STRING) != nCommandListSessionID) return

            bookMeta.title?.let {
                if (commandUISession.updateOrGetLastUIType(null) == "NONE") return@let

                if (it.endsWith(commandUISession.updateOrGetLastUIType(null))) return
            }

            if (commandUISession.updateOrGetStayOpen(null)) {
                commandUISession.updateOrGetStayOpen(false)
                return
            }

            removeCommandUISession(player)
        }
    }
}