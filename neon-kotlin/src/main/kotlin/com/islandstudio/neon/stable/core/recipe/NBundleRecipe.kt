package com.islandstudio.neon.stable.core.recipe

import com.islandstudio.neon.stable.core.recipe.component.AbstractRecipeHolder

sealed class NBundleRecipe(keyname: String): AbstractRecipeHolder(keyname) {
    companion object: RecipeHolderHandler<NBundleRecipe>(NBundleRecipe::class);

    data object Bundle: NBundleRecipe("nBundle.recipe.bundle.key") {
        override val result: NMaterial = NMaterial.BUNDLE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.STRING, NMaterial.RABBIT_HIDE
        )
    }
}