package com.islandstudio.neon.stable.features.nSmelter

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.core.recipe.NRecipes
import com.islandstudio.neon.stable.core.recipe.RecipeRegistry
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
        val filteredRecipes = filterRecipe("NSMELTER")

        if (filteredRecipes.isNotEmpty()) return

        filteredRecipes.values.forEach { nRecipe ->
            lateinit var blastingRecipe: BlastingRecipe

            val furnaceRecipe: FurnaceRecipe?

            val namespacedKey = nRecipe.key
            val result = ItemStack(nRecipe.result.bukkitMaterial!!)
            val ingredients = MaterialChoice(nRecipe.ingredients.map { it.bukkitMaterial })
            val resultRecipeInServer = plugin.server.getRecipesFor(result)

            furnaceRecipe = resultRecipeInServer.find { recipe -> recipe.toString().contains("FurnaceRecipe") }?.let {
                it as FurnaceRecipe
            }

            var expDrop = furnaceRecipe?.experience ?: 0f
            var smeltingTime = furnaceRecipe?.cookingTime?.div(2) ?: 0

            if (furnaceRecipe == null) {
                when (nRecipe) {
                    NRecipes.NSMELTER_IRON_BLOCK, NRecipes.NSMELTER_COPPER_BLOCK -> {
                        expDrop = (0.7f * 9)
                        smeltingTime = 140
                    }

                    NRecipes.NSMELTER_GOLD_BLOCK -> {
                        expDrop = (1f * 9)
                        smeltingTime = 140
                    }

                    else -> { return@forEach }
                }
            }

            blastingRecipe = BlastingRecipe(namespacedKey, result, ingredients, expDrop, smeltingTime)

            plugin.server.addRecipe(blastingRecipe)
        }
    }
}