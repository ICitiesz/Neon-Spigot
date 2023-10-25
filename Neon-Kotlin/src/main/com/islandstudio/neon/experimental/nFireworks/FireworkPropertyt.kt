package com.islandstudio.neon.experimental.nFireworks

import net.md_5.bungee.api.ChatColor
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.block.BlockFace

object FireworkProperty {
    data class FireworkEffects(var imageName: String = "") {
        var fireworkAmount: Byte = 1
        var fireworkColor: FireworkColors = FireworkColors.BLUE
        var fireworkExplosionType: FireworkEffect.Type = FireworkEffect.Type.STAR
        var fireworkPower: Byte = 10
        var fireworkWithFade: Boolean = true
        var fireworkWithFadeColor: FireworkColors = FireworkColors.AQUA
        var fireworkWithFlicker: Boolean = true
        var fireworkWithTrail: Boolean = true
        var fireworkPatternFacing: FireworkPatternFacing = FireworkPatternFacing.AUTO

        fun setDefault() {
            fireworkAmount = 1
            fireworkColor = FireworkColors.BLUE
            fireworkExplosionType = FireworkEffect.Type.STAR
            fireworkPower = 10
            fireworkWithFade = true
            fireworkWithFadeColor = FireworkColors.AQUA
            fireworkWithFlicker = true
            fireworkWithTrail = true
            fireworkPatternFacing = FireworkPatternFacing.AUTO
        }

        /**
         * Get explosion type colored name
         *
         */
        fun getExplosionTypeColoredName() = if (fireworkExplosionType.name == "BALL_LARGE") {
            "${ChatColor.GREEN}Large Ball"
        } else {
            "${ChatColor.GREEN}${fireworkExplosionType.name.lowercase().replaceFirstChar { it.uppercase() }}"
        }

        /**
         * Get toggle colored name
         *
         * @param toggleValue
         */
        fun getToggleColoredName(toggleValue: Boolean) = if (toggleValue) {
            "${ChatColor.GREEN}${toggleValue}"
        } else {
            "${ChatColor.RED}${toggleValue}"
        }

        fun getWithFadeColoredName() = "${getToggleColoredName(fireworkWithFade)} " +
                "${org.bukkit.ChatColor.GRAY}| ${fireworkWithFadeColor.coloredName}"
    }

    enum class FireworkColors(val coloredName: String) {
        WHITE("${ChatColor.of(java.awt.Color(Color.WHITE.asRGB()))}█ White") {
            override fun getBukkitColor(): Color = Color.WHITE },

        SILVER("${ChatColor.of(java.awt.Color(Color.SILVER.asRGB()))}█ Silver") {
            override fun getBukkitColor(): Color = Color.SILVER },

        GRAY("${ChatColor.of(java.awt.Color(Color.GRAY.asRGB()))}█ Gray") {
            override fun getBukkitColor(): Color = Color.GRAY },

        BLACK("${ChatColor.of(java.awt.Color(Color.BLACK.asRGB()))}█ ${org.bukkit.ChatColor.DARK_GRAY}Black") {
            override fun getBukkitColor(): Color = Color.BLACK },

        RED("${ChatColor.of(java.awt.Color(Color.RED.asRGB()))}█ Red") {
            override fun getBukkitColor(): Color = Color.RED },

        MAROON("${ChatColor.of(java.awt.Color(Color.MAROON.asRGB()))}█ Maroon") {
            override fun getBukkitColor(): Color = Color.MAROON },

        YELLOW("${ChatColor.of(java.awt.Color(Color.YELLOW.asRGB()))}█ Yellow") {
            override fun getBukkitColor(): Color = Color.YELLOW },

        OLIVE("${ChatColor.of(java.awt.Color(Color.OLIVE.asRGB()))}█ Olive") {
            override fun getBukkitColor(): Color = Color.OLIVE },

        LIME("${ChatColor.of(java.awt.Color(Color.LIME.asRGB()))}█ Lime") {
            override fun getBukkitColor(): Color = Color.LIME },

        GREEN("${ChatColor.of(java.awt.Color(Color.GREEN.asRGB()))}█ Green") {
            override fun getBukkitColor(): Color = Color.GREEN },

        AQUA("${ChatColor.of(java.awt.Color(Color.AQUA.asRGB()))}█ Aqua") {
            override fun getBukkitColor(): Color = Color.AQUA },

        TEAL("${ChatColor.of(java.awt.Color(Color.TEAL.asRGB()))}█ Teal") {
            override fun getBukkitColor(): Color = Color.TEAL },

        BLUE("${ChatColor.of(java.awt.Color(Color.BLUE.asRGB()))}█ Blue") {
            override fun getBukkitColor(): Color = Color.BLUE },

        NAVY("${ChatColor.of(java.awt.Color(Color.NAVY.asRGB()))}█ Navy") {
            override fun getBukkitColor(): Color = Color.NAVY },

        FUCHSIA("${ChatColor.of(java.awt.Color(Color.FUCHSIA.asRGB()))}█ Fuchsia") {
            override fun getBukkitColor(): Color = Color.FUCHSIA },

        PURPLE("${ChatColor.of(java.awt.Color(Color.PURPLE.asRGB()))}█ Purple") {
            override fun getBukkitColor(): Color = Color.PURPLE },

        ORANGE("${ChatColor.of(java.awt.Color(Color.ORANGE.asRGB()))}█ Orange") {
            override fun getBukkitColor(): Color = Color.ORANGE };

        abstract fun getBukkitColor(): Color
    }

    enum class FireworkPatternFacing {
        AUTO {
            override fun getFacing(): BlockFace? = null
        },
        SOUTH {
            override fun getFacing(): BlockFace = BlockFace.SOUTH
        },
        WEST {
            override fun getFacing(): BlockFace = BlockFace.WEST
        },
        NORTH {
            override fun getFacing(): BlockFace = BlockFace.NORTH
        },
        EAST {
            override fun getFacing(): BlockFace = BlockFace.EAST
        };

        abstract fun getFacing(): BlockFace?
    }
}
