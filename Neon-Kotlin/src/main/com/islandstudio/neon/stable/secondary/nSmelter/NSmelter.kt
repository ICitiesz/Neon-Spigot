package com.islandstudio.neon.stable.secondary.nSmelter

import com.islandstudio.neon.Neon
import com.islandstudio.neon.experimental.nServerConfigurationNew.NServerConfigurationNew
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nServerConfiguration.NServerConfiguration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.inventory.BlastingRecipe
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin

object NSmelter {
    private val plugin: Plugin = getPlugin(Neon::class.java)

    /**
     * Initializes the nSmelter.
     */
    fun run() {
        if (!NServerConfigurationNew.getToggle("nCutter")) return

        getSmeltableItems().keys.forEach { key ->
            var furnaceRecipe: FurnaceRecipe? = null

            val inputMaterial: Material? = getSmeltableItems()[key]?.get(0)
            val resultMaterial: Material? = getSmeltableItems()[key]?.get(1)

            if (resultMaterial == null || inputMaterial == null) return@forEach


            val result = ItemStack(resultMaterial)
            var namespacedKey = NamespacedKey(plugin, "neon_${result.type.name.lowercase()}")

            val recipes: List<Recipe> = plugin.server.getRecipesFor(ItemStack(resultMaterial))

            recipes.forEach { recipe ->
                if (recipe.toString().contains("FurnaceRecipe")) {
                    furnaceRecipe = recipe as FurnaceRecipe
                }
            }

            if (key.startsWith("SAND") || key.startsWith("RED_SAND")) {
                namespacedKey = NamespacedKey(plugin, "${inputMaterial.name.lowercase()}_glass")
            }

            if (key.endsWith("CHARCOAL")) {
                namespacedKey = NamespacedKey(plugin, key.lowercase())
            }

            if (furnaceRecipe == null) return

            val exp: Float = furnaceRecipe!!.experience
            val cookingTime: Int = (furnaceRecipe!!.cookingTime / 2)

            val blastingRecipe = BlastingRecipe(
                namespacedKey,
                result,
                inputMaterial,
                exp,
                cookingTime
            )

            plugin.server.addRecipe(blastingRecipe)
        }
    }

    /**
     * Gets the smeltable items.
     *
     * @return The smeltable items. (Map<String, ArrayList<Material>>)
     */
    private fun getSmeltableItems(): MutableMap<String, ArrayList<Material?>> {
        val combinedSmeltableItems: MutableMap<String, ArrayList<Material?>> = HashMap()

        Smeltable.values().forEach {
            val smeltableItems: ArrayList<Material?> = ArrayList()

            if (it.input != null && it.result != null) {
                smeltableItems.add(it.input)
                smeltableItems.add(it.result)
                combinedSmeltableItems[it.name] = smeltableItems
            }
        }

        return combinedSmeltableItems
    }
}