package com.islandstudio.neon.experimental.nBundle

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.primary.nExperimental.NExperimental
import com.islandstudio.neon.stable.utils.NNamespaceKeys
import org.bukkit.Material
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

object NBundle {
    private val plugin: Plugin = JavaPlugin.getPlugin(Neon::class.java)
    private const val GENERATE_CHANCE: Double = 0.17
    private val isEnabled: () -> Boolean = {
        var tempBool = false

        NExperimental.Handler.getClientElement().forEach {
            val nExperimental = NExperimental(it)

            if (!nExperimental.experimentalName.equals("nBundle", true)) return@forEach

            if (!nExperimental.isEnabled) return@forEach

            tempBool = true
        }

        tempBool
    }

    /**
     * Initialize the nBundle.
     */
    fun run() {
        if (!isEnabled()) return

        val shapedRecipe = ShapedRecipe(NNamespaceKeys.NEON_BUNDLE.key, ItemStack(Material.BUNDLE))

        shapedRecipe.shape("SRS", "R R", "RRR")
        shapedRecipe.setIngredient('S', Material.STRING)
        shapedRecipe.setIngredient('R', Material.RABBIT_HIDE)

        plugin.server.addRecipe(shapedRecipe)
    }

    /**
     * Set bundle spawning in chests.
     *
     * @param e LootGenerateEvent
     */
    fun setSpawning(e: LootGenerateEvent) {
        if (!isEnabled()) return

        val lootTableList = arrayOf(
            "village_tannery",
            "abandoned_mineshaft",
            "desert_pyramid"
        )

        val lootTableKey: String = e.lootTable.key.toString()

        if (!lootTableKey.startsWith("minecraft:chests")) return

        if (lootTableKey.split("/").last() !in lootTableList) return

        val loot: ArrayList<ItemStack> = ArrayList(e.loot)

        if (Math.random() <= GENERATE_CHANCE) loot.add(ItemStack(Material.BUNDLE, 1))

        e.setLoot(loot)
    }

}