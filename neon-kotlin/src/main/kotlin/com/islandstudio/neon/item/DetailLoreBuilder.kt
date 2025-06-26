package com.islandstudio.neon.item

import org.bukkit.inventory.meta.ItemMeta
import java.util.*

abstract class DetailLoreBuilder {
    abstract fun setDetailLore(itemMeta: ItemMeta, isBroken: Boolean)

    protected fun buildDetailLore(itemMeta: ItemMeta, buildFunc: LinkedList<String>.() -> Unit) {
        val detailLore = LinkedList<String>()

        buildFunc(detailLore)

        itemMeta.lore = detailLore
    }

    fun removeDetailLore(itemMeta: ItemMeta) {
        itemMeta.lore = null
    }
}