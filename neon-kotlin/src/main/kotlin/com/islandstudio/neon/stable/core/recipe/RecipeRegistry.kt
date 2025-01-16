package com.islandstudio.neon.stable.core.recipe

interface RecipeRegistry {
    /**
     * Filter recipe to meet the specific condition.
     *
     * @param recipeHolder
     * @return
     */
    fun filterRecipe(recipeHolder: String): HashMap<String, NRecipes> {
        val filteredRecipes: HashMap<String, NRecipes> = HashMap()

        NRecipes.entries.filter { nRecipe -> nRecipe.name.startsWith(recipeHolder, true) }.forEach { nRecipe ->
            if (nRecipe.result.bukkitMaterial == null) return@forEach

            if (nRecipe.ingredients.isEmpty()) return@forEach
            nRecipe.ingredients.removeIf { ingredient -> ingredient.bukkitMaterial == null }

            filteredRecipes[nRecipe.key.toString()] = nRecipe
        }

        return filteredRecipes
    }

    /**
     * Register recipe to the server
     *
     */
    fun registerRecipe()
}