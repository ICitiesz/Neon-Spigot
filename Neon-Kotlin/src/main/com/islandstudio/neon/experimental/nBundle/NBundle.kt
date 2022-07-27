package com.islandstudio.neon.experimental.nBundle

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nExperimental.NExperimental
import com.islandstudio.neon.stable.utils.NNamespaceKeys
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
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

object NBundle {
    private const val GENERATE_CHANCE: Double = 0.17
    private val isEnabled: () -> Boolean = {
        var tempBool = false

        NExperimental.Handler.getClientElement().forEach {
            val nExperimental = NExperimental(it)

            if (!nExperimental.experimentalName.equals("nBundle", true)) return@forEach

            if (!nExperimental.isEnabled) return@forEach

            tempBool = true
        }

        tempBool
    }

    /**
     * Initialize the nBundle.
     */
    fun run() {
        if (!isEnabled()) {
            NConstructor.plugin.server.worlds.forEach {
                it.entities.parallelStream()
                    .filter { entity -> entity is Villager }
                    .filter { entity -> (entity as Villager).profession == Villager.Profession.LEATHERWORKER }.forEach { entity ->
                        val villager = entity as Villager
                        val villagerRecipes: ArrayList<MerchantRecipe> = ArrayList(villager.recipes)

                        if (villagerRecipes.any { recipe -> recipe.result == ItemStack(Material.BUNDLE) }) {
                            villagerRecipes.remove(villagerRecipes.first { recipe -> recipe .result == ItemStack(Material.BUNDLE) })
                            villager.recipes = villagerRecipes
                        }
                }
            }

            return NConstructor.unRegisterEvent(EventController())
        }

        NConstructor.registerEvent(EventController())

        val shapedRecipe = ShapedRecipe(NNamespaceKeys.NEON_BUNDLE.key, ItemStack(Material.BUNDLE))

        shapedRecipe.shape("SRS", "R R", "RRR")
        shapedRecipe.setIngredient('S', Material.STRING)
        shapedRecipe.setIngredient('R', Material.RABBIT_HIDE)

        NConstructor.plugin.server.addRecipe(shapedRecipe)
    }

    /**
     * Generate bundle in the loot structure.
     *
     * @param lootTable The loot structure loot table.
     * @param loot The loot structure loot.
     *
     * @return The loot structure loot with bundle.
     */
    private fun generateLoot(lootTable: LootTable, loot: ArrayList<ItemStack>): List<ItemStack> {
        val lootTableKey: String = lootTable.key.toString()

        if (!lootTableKey.startsWith("minecraft:chests")) return loot

        if (!LootStructure.values().map { it.key }.contains(lootTableKey.split("/").last())) return loot

        if (Math.random() <= GENERATE_CHANCE) {
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

        /* Bundle sale properties */
        val maxBuy = 12
        val normalPrice = 5
        val priceMultiplier = 0.2f
        val villagerExperience = 5

        val villagerRecipes: ArrayList<MerchantRecipe> = ArrayList(villager.recipes)
        val bundleMerchantRecipe = MerchantRecipe(ItemStack(Material.BUNDLE), maxBuy)

        bundleMerchantRecipe.addIngredient(ItemStack(Material.EMERALD, normalPrice))
        bundleMerchantRecipe.setExperienceReward(true)
        bundleMerchantRecipe.villagerExperience = villagerExperience
        bundleMerchantRecipe.priceMultiplier = priceMultiplier

        if (villagerRecipes.any { it.result == ItemStack(Material.BUNDLE) }) {
            if (villager.villagerLevel >= 2) return

            villagerRecipes.remove(villagerRecipes.first { it.result == ItemStack(Material.BUNDLE) })

            villager.recipes = villagerRecipes
            return
        }

        if (villager.villagerLevel < 2) return

        villagerRecipes.add(bundleMerchantRecipe)

        villager.recipes = villagerRecipes
    }

    private class EventController: Listener {
        @EventHandler
        private fun onLootGenerate(e: LootGenerateEvent) {
            e.setLoot(generateLoot(e.lootTable, ArrayList(e.loot)))
        }

        @EventHandler
        private fun onPlayerInteractEntity(e: PlayerInteractEntityEvent) {
            val villager: Villager = if (e.rightClicked is Villager) e.rightClicked as Villager else return

            setTradingRecipe(villager)
        }
    }

    private enum class LootStructure(val key: String) {
        VILLAGE_TANNERY("village_tannery"),
        ABANDONED_MINESHAFT("abandoned_mineshaft"),
        DESERT_PYRAMID("desert_pyramid")
    }
}