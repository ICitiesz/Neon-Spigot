package com.islandstudio.neon.stable.core.recipe.component

import com.islandstudio.neon.stable.core.application.datakey.AbstractDataKey
import com.islandstudio.neon.stable.core.recipe.NMaterial
import kotlin.reflect.KClass

abstract class AbstractRecipeHolder(keyName: String): AbstractDataKey(keyName) {
    abstract val result: NMaterial
    open val resultAmount: Int = 1
    abstract val ingredients: HashSet<NMaterial>

    protected fun setIngredients(vararg ingredients: NMaterial): HashSet<NMaterial> {
        return ingredients.toHashSet()
    }

    abstract class RecipeHolderHandler<T: AbstractRecipeHolder>(private val clazz: KClass<T>) {
        fun getAllRecipe(): ArrayList<T> {
            return clazz.sealedSubclasses
                .map { it.objectInstance as T }
                .filter { it.result.bukkitMaterial != null }
                .filter { it.ingredients.isNotEmpty() }
                .map {
                    it.ingredients.removeIf { ingredient -> ingredient.bukkitMaterial == null }
                    it
                }.toCollection(ArrayList())
        }
    }
}