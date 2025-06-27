package com.islandstudio.neon.util

import com.islandstudio.neon.shared.core.exception.NeonException
import net.md_5.bungee.api.ChatColor

object NeonColor {
    enum class DefinedColor(val color: ChatColor) {
        CyanBlue(fromHex("#34baeb")),
        Orange(fromHex("#f57d1f")),
        LightGreen(fromHex("#9bec00")),
        Purple(fromHex("#892cdc")),
        Yellow(fromHex("#ffed00")),
        Red(ChatColor.RED),
        Green(ChatColor.GREEN),

        /* Decorator */
        Reset(ChatColor.RESET),
        Bold(ChatColor.BOLD);
    }

    fun fromHex(hexValue: String): ChatColor {
        return runCatching {
            ChatColor.of(hexValue)
        }.getOrElse {
            throw NeonException("Invalid hex color value!", it.cause)
        }
    }
}