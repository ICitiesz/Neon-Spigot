package com.islandstudio.neon.stable.secondary.nCutter

import com.islandstudio.neon.stable.core.RecipeRegistry
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nServerFeatures.NServerFeatures
import com.islandstudio.neon.stable.primary.nServerFeatures.ServerFeature
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.StonecuttingRecipe
import org.bukkit.plugin.Plugin

class NCutter: RecipeRegistry {
    private val plugin: Plugin = NConstructor.plugin

    object Handler {
        /**
         * Initializes the nCutter.
         */
        fun run() {
            if (!NServerFeatures.getToggle(ServerFeature.FeatureNames.N_CUTTER)) return

            NCutter().registerRecipe()
        }
    }

    override fun registerRecipe() {
        val filteredRecipes = filterRecipe("NCUTTER")

        if (filteredRecipes.isEmpty()) return

        filteredRecipes.values.forEach { nRecipe ->
            lateinit var stoneCuttingRecipe: StonecuttingRecipe

            val namespaceKey = nRecipe.key
            val result = ItemStack(nRecipe.result.bukkitMaterial!!, nRecipe.resultAmount)
            val ingredients = RecipeChoice.MaterialChoice(nRecipe.ingredients.map { it.bukkitMaterial })

            stoneCuttingRecipe = StonecuttingRecipe(namespaceKey, result, ingredients)

            plugin.server.addRecipe(stoneCuttingRecipe)
        }
    }
}
