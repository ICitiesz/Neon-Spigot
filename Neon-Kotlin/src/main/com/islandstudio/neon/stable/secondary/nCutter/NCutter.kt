package com.islandstudio.neon.stable.secondary.nCutter

import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nServerFeatures.NServerFeatures
import com.islandstudio.neon.stable.primary.nServerFeatures.ServerFeature
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.StonecuttingRecipe
import org.bukkit.plugin.Plugin

object NCutter {
    private val plugin: Plugin = NConstructor.plugin

    /**
     * Initializes the nCutter.
     */
    fun run() {
        if (!NServerFeatures.getToggle(ServerFeature.FeatureNames.N_CUTTER)) return

        val woodenItems: MutableList<Material?> = WoodenItems.values().map { woodenItems -> woodenItems.item.map { it } }.flatten().toMutableList()
        val woodRecipes: MutableSet<StonecuttingRecipe> = HashSet()

        WoodPlanks.values().forEach { woodPlank: WoodPlanks? ->
            var result: ItemStack
            var namespacedKey: NamespacedKey
            var stoneCuttingRecipe: StonecuttingRecipe

            if (woodPlank == null) return@forEach

            woodenItems.forEach inner@{ item: Material? ->
                if (item == null) return@inner

                result = ItemStack(item)
                namespacedKey = NamespacedKey(plugin, "neon_${result.type.name.lowercase()}")

                if (woodPlank.type!!.name.split("_")[0].equals(item.name.split("_")[0], true)) {
                    if (item.name.endsWith("SLAB")) {
                        result = ItemStack(item, 2)
                    }

                    stoneCuttingRecipe = StonecuttingRecipe(
                        namespacedKey,
                        result,
                        woodPlank.type
                    )

                    plugin.server.addRecipe(stoneCuttingRecipe)
                }
            }

            val stick: Material = Material.getMaterial("STICK")!!
            val ladder: Material = Material.getMaterial("LADDER")!!
            val bowl: Material = Material.getMaterial("BOWL")!!

            /* Stick recipe */
            result = ItemStack(stick,2)
            namespacedKey = NamespacedKey(plugin, "${woodPlank.name.lowercase()}_stick")
            stoneCuttingRecipe = StonecuttingRecipe(namespacedKey, result, woodPlank.type!!)
            plugin.server.addRecipe(stoneCuttingRecipe)

            /* Ladder recipe */
            result = ItemStack(ladder)
            namespacedKey = NamespacedKey(plugin, "${woodPlank.name.lowercase()}_ladder")
            stoneCuttingRecipe = StonecuttingRecipe(namespacedKey, result, woodPlank.type)
            plugin.server.addRecipe(stoneCuttingRecipe)

            /* Bowl recipe */
            result = ItemStack(bowl, 2)
            namespacedKey = NamespacedKey(plugin, "${woodPlank.name.lowercase()}_bowl")
            stoneCuttingRecipe = StonecuttingRecipe(namespacedKey, result, woodPlank.type)
            plugin.server.addRecipe(stoneCuttingRecipe)
        }
    }
}
