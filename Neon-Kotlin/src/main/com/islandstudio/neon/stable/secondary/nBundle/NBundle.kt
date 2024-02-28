package com.islandstudio.neon.stable.secondary.nBundle

import com.islandstudio.neon.stable.core.init.NConstructor
import com.islandstudio.neon.stable.core.recipe.NRecipes
import com.islandstudio.neon.stable.core.recipe.RecipeRegistry
import com.islandstudio.neon.stable.primary.nServerFeatures.NServerFeatures
import com.islandstudio.neon.stable.primary.nServerFeatures.ServerFeature
import com.islandstudio.neon.stable.utils.reflection.NReflector
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.loot.LootTable
import org.jetbrains.kotlin.utils.addToStdlib.ifFalse

object NBundle: RecipeRegistry {
    /* Default Bundle trade values */
    private var bundleGenerateChance = 0.17
    private var bundleMaxBuy = 12
    private var bundlePrice = 5
    private var bundlePriceMultiplier = 0.2f
    private var villagerExperience = 5

    private var isEnabled = false
    private val plugin = NConstructor.plugin

    private enum class LootStructure(val key: String) {
        VILLAGE_TANNERY("village_tannery"),
        ABANDONED_MINESHAFT("abandoned_mineshaft"),
        DESERT_PYRAMID("desert_pyramid")
    }

    object Handler {
        /**
         * Initialize the nBundle.
         */
        fun run() {
            isEnabled = NServerFeatures.getToggle(ServerFeature.FeatureNames.N_BUNDLE)

            if (!isEnabled) {
                removeBundleTradingRecipe()
                return NConstructor.unRegisterEventProcessor(EventProcessor())
            }

            val featureName = ServerFeature.FeatureNames.N_BUNDLE.featureName

            bundleGenerateChance = NServerFeatures.getOptionValue(featureName, "bundleGenerateChance" ) as Double
            bundleMaxBuy = NServerFeatures.getOptionValue(featureName, "bundleMaxBuy") as Int
            bundlePrice = NServerFeatures.getOptionValue(featureName, "bundlePrice") as Int
            bundlePriceMultiplier = (NServerFeatures.getOptionValue(featureName, "bundlePriceMultiplier") as Double).toFloat()
            villagerExperience = NServerFeatures.getOptionValue(featureName, "villagerExperience") as Int

            NConstructor.registerEventProcessor(EventProcessor())

            registerRecipe()
        }
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

        if (!LootStructure.entries.map { it.key }.contains(lootTableKey.split("/").last())) return loot

        if (Math.random() <= bundleGenerateChance) {
            loot.add(ItemStack(Material.BUNDLE, 1))
            return loot
        }

        return loot
    }

    override fun registerRecipe() {
        val filteredRecipe: HashMap<String, NRecipes> = filterRecipe("NBUNDLE")

        if (filteredRecipe.isEmpty()) return

        plugin.server.addRecipe(filteredRecipe.values.first().run {
            val bundleRecipe = ShapedRecipe(this.key, ItemStack(this.result.bukkitMaterial!!))

            bundleRecipe.shape("SRS", "R R", "RRR")
            bundleRecipe.setIngredient('S', this.ingredients.find { it.name.startsWith("S") }!!
                .bukkitMaterial!!)
            bundleRecipe.setIngredient('R', this.ingredients.find { it.name.startsWith("R") }!!
                .bukkitMaterial!!)
        })
    }

    /**
     * Discover bundle recipe once player acquired ingredient.
     *
     * @param player The target player.
     * @param gaveItem The given item.
     */
    fun discoverBundleRecipe(player: Player, gaveItem: net.minecraft.world.item.ItemStack? = null) {
        if (!isEnabled) return

        val bundleNamespaceKey = NRecipes.NBUNDLE_BUNDLE.key

        if (player.hasDiscoveredRecipe(bundleNamespaceKey)) return

        /* This section for player who using /give command or picked up the item */
        gaveItem?.let { itemStack ->
            (NReflector.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asCraftMirror",
                net.minecraft.world.item.ItemStack::class.java).invoke(null, itemStack) as ItemStack).also {
                if (!(it.type == Material.RABBIT_HIDE || it.type == Material.BUNDLE)) return

                plugin.server.scheduler.runTask(plugin, Runnable {
                    player.discoverRecipe(bundleNamespaceKey)
                })
                return
            }
        }

        player.inventory.contents.filterNotNull().any { itemStack ->
            itemStack.type == Material.RABBIT_HIDE || itemStack.type == Material.BUNDLE }.ifFalse { return }

        player.discoverRecipe(bundleNamespaceKey)
    }

    /**
     * Set trading recipe for the nBundle.
     *
     * @param villager The villager to set the recipe for.
     */
    private fun setBundleTradingRecipe(villager: Villager) {
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

    /**
     * Remove bundle trading recipe if nBundle feature has been disabled.
     *
     */
    private fun removeBundleTradingRecipe() {
        plugin.server.worlds.forEach {
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
    }

    private class EventProcessor: Listener {
        @EventHandler
        private fun onLootGenerate(e: LootGenerateEvent) {
            e.setLoot(generateBundle(e.lootTable, ArrayList(e.loot)))
        }

        @EventHandler
        private fun onPlayerInteractEntity(e: PlayerInteractEntityEvent) {
            val villager: Villager = if (e.rightClicked is Villager) e.rightClicked as Villager else return

            setBundleTradingRecipe(villager)
        }

        @EventHandler
        private fun onServerLoad(e: ServerLoadEvent) {
            if (!(e.type == ServerLoadEvent.LoadType.STARTUP || e.type == ServerLoadEvent.LoadType.RELOAD)) return

            plugin.server.onlinePlayers.parallelStream().forEach {
                discoverBundleRecipe(it)
            }
        }

        @EventHandler
        private fun onPlayerJoin(e: PlayerJoinEvent) {
            discoverBundleRecipe(e.player)
        }
    }
}