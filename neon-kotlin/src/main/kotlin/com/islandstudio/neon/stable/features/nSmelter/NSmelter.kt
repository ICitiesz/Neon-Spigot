package com.islandstudio.neon.stable.features.nSmelter

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.recipe.NSmelterRecipe
import com.islandstudio.neon.stable.core.recipe.component.RecipeRegistry
import com.islandstudio.neon.stable.features.nServerFeatures.NServerFeaturesRemastered
import org.bukkit.inventory.BlastingRecipe
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin

class NSmelter: RecipeRegistry {
    private val plugin: Plugin = getPlugin(Neon::class.java)

    object Handler {
        /**
         * Initializes the nSmelter.
         */
        fun run() {
            val isEnabled = NServerFeaturesRemastered.serverFeatureSession.getActiveServerFeatureToggle("nSmelter") ?: false

            if (!isEnabled) return

            NSmelter().registerRecipe()
        }
    }

    override fun registerRecipe() {
        NSmelterRecipe.getAllRecipe().forEach { recipe ->
            val result = ItemStack(recipe.result.bukkitMaterial!!, recipe.resultAmount)
            val ingredients = MaterialChoice(recipe.ingredients.map { it.bukkitMaterial })
            val resultRecipeInServer = plugin.server.getRecipesFor(result)

            val furnaceRecipe = resultRecipeInServer.find { furnaceRecipeServer ->
                furnaceRecipeServer.toString().contains("FurnaceRecipe")
            }?.let { it as FurnaceRecipe }

            var expDrop = furnaceRecipe?.experience ?: 0f
            var smeltingTime = furnaceRecipe?.cookingTime?.div(2) ?: 0

            if (furnaceRecipe == null) {
                when (recipe) {
                    NSmelterRecipe.IronBlock, NSmelterRecipe.CopperBlock -> {
                        expDrop = (0.7f * 9)
                        smeltingTime = 140
                    }

                    NSmelterRecipe.GoldBlock -> {
                        expDrop = (1f * 9)
                        smeltingTime = 140
                    }

                    else -> return@forEach
                }
            }

            val blastingRecipe = BlastingRecipe(
                recipe.dataKey, result,
                ingredients, expDrop,
                smeltingTime
            )

            plugin.server.addRecipe(blastingRecipe)
        }
    }
}