package com.islandstudio.neon.stable.primary.nCommand.nCommandList

import com.islandstudio.neon.stable.primary.nCommand.Commands
import com.islandstudio.neon.stable.utils.reflection.NMSRemapped
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.LecternMenu
import org.bukkit.entity.Player
import java.io.Serializable
import java.util.*
import java.util.concurrent.ConcurrentHashMap

data class CommandUISession(val player: Player) {
    /* Used to store every command usage UI last seen pages */
    private val cmdUsageLastSeenPages: ConcurrentHashMap<Commands.CommandAlias, Int> = ConcurrentHashMap(EnumMap(
        Commands.CommandAlias::class.java))

    /* Command UIs */
    private var commandListUI: CommandUI? = null
    private var commandUsageUI: CommandUI? = null

    /* Command list UI last seen page */
    private var cmdListUILastSeenPage = 0

    /* Session maintainers */
    private var currentActiveCommandUI: NCommandList.CommandUITypes = NCommandList.CommandUITypes.COMMAND_LIST
    private var doSwitchUI = false
    private var stayOpen = false
    private var lastUIType: String? = null

    /**
     * Add or get Command UI.
     *
     * @param commandUIType The targeted command UI type
     * @param commandUIWindow The targeted command UI window
     * @return
     */
    fun addOrGetCommandUI(commandUIType: NCommandList.CommandUITypes, commandUIWindow: AbstractContainerMenu?): CommandUI? {
        when (commandUIType) {
            NCommandList.CommandUITypes.COMMAND_LIST -> {
                commandListUI?.let { return it }

                commandListUI = commandUIWindow?.let { CommandUI(it) } ?: return null
                return commandListUI as CommandUI
            }

            NCommandList.CommandUITypes.COMMAND_USAGES -> {
                commandUsageUI?.let { return it }

                commandUsageUI = commandUIWindow?.let { CommandUI(it) } ?: return null
                return commandUsageUI as CommandUI
            }
        }
    }

    /**
     * Update or get doSwitchUI state.
     *
     * @param newDoSwitchUI
     * @return
     */
    fun updateOrGetDoSwitchUI(newDoSwitchUI: Boolean?): Boolean {
        if (newDoSwitchUI == null) return doSwitchUI

        doSwitchUI = newDoSwitchUI

        return doSwitchUI
    }

    /**
     * Update or get stayOpen state
     *
     * @param newStayOpen
     * @return
     */
    fun updateOrGetStayOpen(newStayOpen: Boolean?): Boolean {
        if (newStayOpen == null) return stayOpen

        stayOpen = newStayOpen

        return stayOpen
    }

    /**
     * Update or get last UI type.
     *
     * @param newLastUIType
     * @return
     */
    fun updateOrGetLastUIType(newLastUIType: String?): String {
        if (newLastUIType == null) return lastUIType ?: "NONE"

        lastUIType = newLastUIType

        return lastUIType ?: "NONE"
    }

    /**
     * Update or get current active UI
     *
     * @param newActiveCommandUI New active command UI type or null to get current active UI
     * @return Current active command UI type.
     */
    fun updateOrGetCurrentActiveUI(newActiveCommandUI: NCommandList.CommandUITypes?): NCommandList.CommandUITypes {
        if (newActiveCommandUI == null) return currentActiveCommandUI

        currentActiveCommandUI = newActiveCommandUI

        return currentActiveCommandUI
    }

    /**
     * Update or get last seen page
     *
     * @param doUpdate Do update last seen page
     * @param commandUITypes The target command UI type.
     * @param commandAlias The target command if getting command usage UI last seen page.
     * @return Last seen page of the targeted command UI
     */
    fun updateOrGetLastSeenPage(doUpdate: Boolean, commandUITypes: NCommandList.CommandUITypes, commandAlias: Commands.CommandAlias?): Int {
        when (commandUITypes) {
            NCommandList.CommandUITypes.COMMAND_LIST -> {
                if (!doUpdate) return cmdListUILastSeenPage

                cmdListUILastSeenPage = commandListUI?.updateOrGetCurrentPage() ?: 0

                return cmdListUILastSeenPage
            }

            NCommandList.CommandUITypes.COMMAND_USAGES -> {
                commandAlias?.let {
                    if (!doUpdate) return cmdUsageLastSeenPages[it] ?: 0

                    cmdUsageLastSeenPages[it] = commandUsageUI?.updateOrGetCurrentPage() ?: 0

                    return cmdUsageLastSeenPages[it]!!
                } ?: return 0
            }
        }
    }

    data class CommandUI(private var commandUIWindow: AbstractContainerMenu): Serializable {
        private var commandUIView: LecternMenu
        private var currentPage: Int
        private var commandAlias: Commands.CommandAlias? = null

        var commandUIId: Int

        init {
            commandUIView = getCommandUIView()
            commandUIId = commandUIWindow.containerId
            currentPage = commandUIView.page
        }

        private fun getCommandUIView(): LecternMenu {
            val commandUIViewField = commandUIWindow.javaClass.getDeclaredField("delegate")
            commandUIViewField.isAccessible = true
            return commandUIViewField[commandUIWindow] as LecternMenu
        }

        private fun updateCommandUI() {
            commandUIView = getCommandUIView()
            commandUIId = commandUIWindow.containerId
            updateOrGetCurrentPage()
        }

        fun updateOrGetCurrentPage(): Int {
            currentPage = commandUIView.page
            return currentPage
        }

        fun updateOrGetCommandUIWindow(newCommandUIWindow: AbstractContainerMenu?): AbstractContainerMenu {
            if (newCommandUIWindow == null) return commandUIWindow

            commandUIWindow = newCommandUIWindow
            updateCommandUI()

            return commandUIWindow
        }

        fun updateServerSideCurrentPage(newCurrentPage: Int) {
            commandUIView.javaClass.getMethod(NMSRemapped.Mapping.NMS_SET_CONTAINER_DATA.remapped, Int::class.java, Int::class.java)
                .invoke(commandUIView, 0, newCurrentPage)
        }

        fun updateOrGetCommandAlias(newCommandAlias: Commands.CommandAlias?): Commands.CommandAlias? {
            if (newCommandAlias == null) return commandAlias

            commandAlias = newCommandAlias

            return commandAlias
        }
    }
}
