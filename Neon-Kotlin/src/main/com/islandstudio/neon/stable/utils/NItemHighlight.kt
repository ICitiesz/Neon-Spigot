package com.islandstudio.neon.stable.utils

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack

class NItemHighlight(key: NamespacedKey) : Enchantment(key) {
    override fun getName(): String {
        return ""
    }

    override fun getMaxLevel(): Int {
        return 0
    }

    override fun getStartLevel(): Int {
        return 0
    }

    override fun getItemTarget(): EnchantmentTarget {
        return EnchantmentTarget.ALL
    }

    override fun isTreasure(): Boolean {
        return false
    }

    override fun isCursed(): Boolean {
        return false
    }

    override fun conflictsWith(enchantment: Enchantment): Boolean {
        return false
    }

    override fun canEnchantItem(itemStack: ItemStack): Boolean {
        return false
    }

}