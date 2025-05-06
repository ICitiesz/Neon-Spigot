package com.islandstudio.neon.shared.utils

import org.bukkit.ChatColor
import java.util.*

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

    /**
     * Slice text into multiple line based on the given length per line.
     *
     * @param text
     * @param sliceLength
     * @return Sliced text lines.
     */
    fun sliceText(text: String, sliceLength: Int): LinkedList<String> {
        return text.split(" ")
            .windowed(sliceLength, sliceLength, true)
            .map { it.joinToString(" ") }
            .toCollection(LinkedList())
    }
}