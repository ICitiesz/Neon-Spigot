package com.islandstudio.neon.stable.features.nRank

import org.bukkit.ChatColor

enum class RankList(val tagPrefix: String) {
    OWNER(ChatColor.AQUA.toString() + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + "" + ChatColor.BOLD + "OWNER" + ChatColor.AQUA + "" + ChatColor.BOLD + "] "),
    VIP_PLUS(ChatColor.WHITE.toString() + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + "" + ChatColor.BOLD + "VIP" + ChatColor.GREEN + "+" + ChatColor.WHITE + "" + ChatColor.BOLD + "] "),
    VIP(ChatColor.WHITE.toString() + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + "" + ChatColor.BOLD + "VIP" + ChatColor.WHITE + "" + ChatColor.BOLD + "] "),
    MEMBER(ChatColor.WHITE.toString() + "" + ChatColor.BOLD + "[" + ChatColor.GRAY + "" + ChatColor.BOLD + "MEMBER" + ChatColor.WHITE + "" + ChatColor.BOLD + "] ")
}