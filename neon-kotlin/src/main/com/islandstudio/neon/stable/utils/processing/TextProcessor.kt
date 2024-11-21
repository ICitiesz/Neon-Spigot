package com.islandstudio.neon.stable.utils.processing

import org.bukkit.ChatColor
import java.util.*
import kotlin.math.pow

object TextProcessor {
    fun sliceText(inputText: String, length: Int): LinkedList<String> {

        return inputText.split(" ")
            .windowed(length, length, true)
            .map { it.joinToString(" ") }
            .toCollection(LinkedList())
    }

    fun processColorText(text: String): String {
        val COLOR_PATTERN = "(&[a-z0-9])".toRegex()

        return text.replace(COLOR_PATTERN) { matchResult ->
            val colorCode = matchResult.value.last()

            if (colorCode !in ChatColor.entries.map { chatColor -> chatColor.char }) {
                return@replace matchResult.value
            }

            ChatColor.getByChar(colorCode).toString()
        }
    }

    fun getDoubleModifier(value: Double, isCeilDouble: Boolean = false): Double {
        return getFloatingPointCount(value).run {
            with(1.0 / 10.0.pow(this.toDouble())) {
                if (isCeilDouble) {
                    return@with this * 10
                }

                this
            }
        }
    }

    fun roundOfDouble(value: Double, floatingPointCount: Int): Double {
        return String.format("%.${floatingPointCount}f", value).toDouble()
    }

    fun getFloatingPointCount(value: Double): Int {
        return value.toString().substringAfter(".").length
    }
}