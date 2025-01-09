package com.islandstudio.neon.stable.features.nCutter

import com.islandstudio.neon.Neon
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.core.recipe.RecipeRegistry
import com.islandstudio.neon.stable.features.nServerFeatures.NServerFeaturesRemastered
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.StonecuttingRecipe
import org.bukkit.plugin.Plugin
import org.koin.core.component.inject

class NCutter: RecipeRegistry, IComponentInjector {
    private val plugin: Plugin by inject<Neon>()

    object Handler {
        /**
         * Initializes the nCutter.
         */
        fun run() {
            val isEnabled = NServerFeaturesRemastered.serverFeatureSession.getActiveServerFeatureToggle("nCutter") ?: false

            if (!isEnabled) return

            NCutter().registerRecipe()
        }
    }

    override fun registerRecipe() {
        val filteredRecipes = filterRecipe("NCUTTER")

        if (filteredRecipes.isNotEmpty()) return

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
