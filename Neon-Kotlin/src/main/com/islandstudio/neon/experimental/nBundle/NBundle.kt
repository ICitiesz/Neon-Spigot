package com.islandstudio.neon.experimental.nBundle

import com.islandstudio.neon.experimental.nServerFeatures.NServerFeatures
import com.islandstudio.neon.experimental.nServerFeatures.ServerFeature
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.utils.NeonKey
import org.bukkit.Material
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.loot.LootTable

object NBundle {
    /* Default Bundle trade values */
    private var bundleGenerateChance = 0.17
    private var bundleMaxBuy = 12
    private var bundlePrice = 5
    private var bundlePriceMultiplier = 0.2f
    private var villagerExperience = 5

    private val isEnabled = NServerFeatures.getToggle(ServerFeature.FeatureNames.N_BUNDLE)

    private enum class LootStructure(val key: String) {
        VILLAGE_TANNERY("village_tannery"),
        ABANDONED_MINESHAFT("abandoned_mineshaft"),
        DESERT_PYRAMID("desert_pyramid")
    }

    /**
     * Initialize the nBundle.
     */
    fun run() {
        val server = NConstructor.plugin.server

        if (!isEnabled) {
            server.worlds.forEach {
                it.entities.parallelStream()
                    .filter { entity -> entity is Villager }
                    .filter { entity -> (entity as Villager).profession == Villager.Profession.LEATHERWORKER }.forEach FE@ { entity ->
                        val villager = entity as Villager
                        val villagerRecipes: ArrayList<MerchantRecipe> = ArrayList(villager.recipes)

                        if (!villagerRecipes.any { recipe -> recipe.result == ItemStack(Material.BUNDLE) }) return@FE

                        villagerRecipes.remove(villagerRecipes.first { recipe -> recipe .result == ItemStack(Material.BUNDLE) })
                        villager.recipes = villagerRecipes
                }
            }

            return NConstructor.unRegisterEvent(EventProcessor())
        }

        val featureName = ServerFeature.FeatureNames.N_BUNDLE.featureName

        bundleGenerateChance = NServerFeatures.getOptionValue(featureName, "bundleGenerateChance" ) as Double
        bundleMaxBuy = NServerFeatures.getOptionValue(featureName, "bundleMaxBuy") as Int
        bundlePrice = NServerFeatures.getOptionValue(featureName, "bundlePrice") as Int
        bundlePriceMultiplier = (NServerFeatures.getOptionValue(featureName, "bundlePriceMultiplier") as Double).toFloat()
        villagerExperience = NServerFeatures.getOptionValue(featureName, "villagerExperience") as Int

        NConstructor.registerEventProcessor(EventProcessor())

        val shapedRecipe = ShapedRecipe(NeonKey.NamespaceKeys.NEON_BUNDLE.key, ItemStack(Material.BUNDLE))

        shapedRecipe.shape("SRS", "R R", "RRR")
        shapedRecipe.setIngredient('S', Material.STRING)
        shapedRecipe.setIngredient('R', Material.RABBIT_HIDE)

        server.addRecipe(shapedRecipe)
    }

    /**
     * Generate bundle in the loot structure.
     *
     * @param lootTable The loot structure loot table.
     * @param loot The loot structure loot.
     *
     * @return The loot structure loot with bundle.
     */
    private fun generateBundle(lootTable: LootTable, loot: ArrayList<ItemStack>): List<ItemStack> {
        val lootTableKey: String = lootTable.key.toString()

        if (!lootTableKey.startsWith("minecraft:chests")) return loot

        if (!LootStructure.values().map { it.key }.contains(lootTableKey.split("/").last())) return loot

        if (Math.random() <= bundleGenerateChance) {
            loot.add(ItemStack(Material.BUNDLE, 1))
            return loot
        }

        return loot
    }

    /**
     * Set trading recipe for the nBundle.
     *
     * @param villager The villager to set the recipe for.
     */
    private fun setTradingRecipe(villager: Villager) {
        if (villager.profession != Villager.Profession.LEATHERWORKER) return

        val villagerTradeContent: ArrayList<MerchantRecipe> = ArrayList(villager.recipes)
        val villagerLevel = villager.villagerLevel
        val bundleMerchantRecipe = MerchantRecipe(ItemStack(Material.BUNDLE), bundleMaxBuy)

        bundleMerchantRecipe.addIngredient(ItemStack(Material.EMERALD, bundlePrice))
        bundleMerchantRecipe.villagerExperience = villagerExperience
        bundleMerchantRecipe.priceMultiplier = bundlePriceMultiplier

        if (villagerTradeContent.any { it.result == ItemStack(Material.BUNDLE) }) {
            if (villagerLevel >= 2) {
                villagerTradeContent.find { it.result == ItemStack(Material.BUNDLE) }.let {
                    villagerTradeContent[villagerTradeContent.indexOf(it)] = bundleMerchantRecipe
                }

                villager.recipes = villagerTradeContent
                return
            }

            villagerTradeContent.remove(villagerTradeContent.first { it.result == ItemStack(Material.BUNDLE) })

            villager.recipes = villagerTradeContent
            return
        }

        if (villagerLevel < 2) return

        villagerTradeContent.add(bundleMerchantRecipe)

        villager.recipes = villagerTradeContent
    }

    private class EventProcessor: Listener {
        @EventHandler
        private fun onLootGenerate(e: LootGenerateEvent) {
            e.setLoot(generateBundle(e.lootTable, ArrayList(e.loot)))
        }

        @EventHandler
        private fun onPlayerInteractEntity(e: PlayerInteractEntityEvent) {
            val villager: Villager = if (e.rightClicked is Villager) e.rightClicked as Villager else return

            setTradingRecipe(villager)
        }
    }
}