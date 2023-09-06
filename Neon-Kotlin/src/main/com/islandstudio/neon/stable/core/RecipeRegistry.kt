package com.islandstudio.neon.stable.core

interface RecipeRegistry {
    /**
     * Filter recipes to meet certain condition.
     *
     * @return
     */
    fun filterRecipe(recipeHolder: String): HashMap<String, NRecipes> {
        val filteredRecipe: HashMap<String, NRecipes> = HashMap()

        NRecipes.values().filter { it.name.startsWith(recipeHolder, true) }.forEach { nRecipe ->
            if (nRecipe.result.bukkitMaterial == null) return@forEach

            if (nRecipe.ingredients.isEmpty()) return@forEach
            nRecipe.ingredients.removeIf { ingredient -> ingredient.bukkitMaterial == null }

            filteredRecipe[nRecipe.key.toString()] = nRecipe
        }

        return filteredRecipe
    }
    /**
     * Register recipe to the server.
     *
     */
    fun registerRecipe()
}