package com.islandstudio.neon.shared.utils

import org.bukkit.ChatColor

object TextUtil {
    fun toColorText(text: String): String {
        val colorCodePattern = "(&[a-z0-9])".toRegex()

        return text.replace(colorCodePattern) { matchResult ->
            val colorCode = matchResult.value.last() // Get the color code, e.g: &a -> a

            /* Check if the color code exist in ChatColor */
            if (colorCode !in ChatColor.entries.map { chatColor -> chatColor.char }) {
                return@replace matchResult.value
            }

            ChatColor.getByChar(colorCode).toString()
        }
    }
}