package com.islandstudio.neon.stable.features.nCutter

import com.islandstudio.neon.Neon
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.stable.core.recipe.NCutterRecipe
import com.islandstudio.neon.stable.core.recipe.component.RecipeRegistry
import com.islandstudio.neon.stable.features.nServerFeatures.NServerFeaturesRemastered
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.StonecuttingRecipe
import org.koin.core.component.inject

class NCutter {
    companion object: IRunner, RecipeRegistry, IComponentInjector {
        private val neon by inject<Neon>()

        /**
         * Initializes the nCutter.
         */
        override fun run() {
            val isEnabled = NServerFeaturesRemastered.serverFeatureSession.getActiveServerFeatureToggle("nCutter") ?: false

            //if (!isEnabled) return

            registerRecipe()
        }

        override fun registerRecipe() {
            NCutterRecipe.getAllRecipe().forEach { recipe ->
                val stoneCuttingRecipe = StonecuttingRecipe(
                    recipe.dataKey,
                    ItemStack(recipe.result.bukkitMaterial!!, recipe.resultAmount),
                    RecipeChoice.MaterialChoice(recipe.ingredients.map { it.bukkitMaterial })
                )

                neon.server.addRecipe(stoneCuttingRecipe)
            }
        }

    }
}
