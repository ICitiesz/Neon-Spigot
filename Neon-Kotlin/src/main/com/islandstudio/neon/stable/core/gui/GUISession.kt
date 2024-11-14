package com.islandstudio.neon.stable.core.gui

import com.islandstudio.neon.stable.core.gui.structure.GUIConstructor
import org.bukkit.entity.Player

data class GUISession(
    val guiHolder: Player,
    private val guiHandlerClass: Class<out GUIConstructor>
) {
    private var guiHandler: GUIConstructor = guiHandlerClass.constructors.find {
        it.parameterTypes.contains(this.javaClass)
    }?.newInstance(this) as GUIConstructor

    fun getGUIHandler(): GUIConstructor {
        return guiHandler
    }

    fun matchesGUI(guiTitle: String): Boolean {
        return guiTitle == getGUIHandler().getGUIName()
    }
}