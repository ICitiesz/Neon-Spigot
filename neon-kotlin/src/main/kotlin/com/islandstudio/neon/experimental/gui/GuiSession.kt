package com.islandstudio.neon.experimental.gui

import com.islandstudio.neon.shared.core.exception.NeonException
import org.bukkit.entity.Player
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

data class GuiSession<T: GuiConstructor<*>>(
    val player: Player,
    val guiClass: KClass<T>
) {
    private val gui: T = guiClass.primaryConstructor?.call(player) ?: throw NeonException("No GUI attached to the player!")

    fun getGui(): T {
        return this.gui
    }

    fun matchesGui(guiName: String): Boolean {
        return getGui().getGuiName() == guiName
    }
}
