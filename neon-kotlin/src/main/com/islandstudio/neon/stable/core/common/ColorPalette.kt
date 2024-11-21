package com.islandstudio.neon.stable.core.common

import net.md_5.bungee.api.ChatColor

enum class ColorPalette(val color: ChatColor) {
    CyanBlue(ChatColor.of("#34baeb")),
    Orange(ChatColor.of("#f57d1f")),
    LightGreen(ChatColor.of("#9bec00")),
    Purple(ChatColor.of("#892cdc")),
    Yellow(ChatColor.of("#ffed00")),
    Red(ChatColor.RED),
    Green(ChatColor.GREEN),

    /* Decorator */
    Reset(ChatColor.RESET),
    Bold(ChatColor.BOLD);
}
