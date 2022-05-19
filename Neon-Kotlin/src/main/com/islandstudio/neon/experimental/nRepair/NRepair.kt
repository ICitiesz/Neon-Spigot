package com.islandstudio.neon.experimental.nRepair

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.primary.nExperimental.NExperimental
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin

object NRepair {
    /* It repairs the tools and weapons by using the materials that match with the tool
       and weapon within the inventory.
    */

    private val plugin: Plugin = getPlugin(Neon::class.java)

    fun run() {
        NExperimental.Handler.getClientElement().forEach {
            val nExperimental = NExperimental(it)

            if (!nExperimental.experimentalName.equals("nRepair", true)) return@forEach

            if (!nExperimental.isEnabled) return
        }

        /* 1) Get item to repair
        *  2) Get item durability
        */

        val shapelessRecipe = ShapelessRecipe(NamespacedKey.minecraft("iron_pickaxe_new"), ItemStack(Material.IRON_PICKAXE))
        shapelessRecipe.addIngredient(Material.IRON_INGOT)
        shapelessRecipe.addIngredient(Material.IRON_PICKAXE)

        plugin.server.addRecipe(shapelessRecipe)
    }

    fun test(e: InventoryClickEvent) {
        if (e.view.type == InventoryType.CRAFTING) {
            val craftingInventory: Inventory = e.inventory
            val itemStack: ItemStack? = craftingInventory.getItem(0)
            val itemMeta: ItemMeta? = itemStack?.itemMeta

            itemMeta!!.setDisplayName("test")
            itemStack.itemMeta = itemMeta

            //e.inventory.setItem(0, itemStack)

            e.view.setItem(0, itemStack)
        }
        println("Hey")
    }
}