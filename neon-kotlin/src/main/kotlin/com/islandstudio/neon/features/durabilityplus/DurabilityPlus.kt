package com.islandstudio.neon.features.durabilityplus

import com.islandstudio.neon.Neon
import com.islandstudio.neon.core.initialization.NeonPluginLoader
import com.islandstudio.neon.core.nmsmapping.NmsManager
import com.islandstudio.neon.core.nmsmapping.NmsMap
import com.islandstudio.neon.core.nmsmapping.NmsProcessor
import com.islandstudio.neon.core.nmsmapping.type.NmsField
import com.islandstudio.neon.experimental.gui.GuiConstructor
import com.islandstudio.neon.features.neonfeature.NeonFeatureManager
import com.islandstudio.neon.item.NeonItemMaterial
import com.islandstudio.neon.server.ServerGamePacketManager
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.config.property.NeonFeatureConfigProperty
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.utils.data.DataUtil
import net.minecraft.network.chat.Component
import net.minecraft.world.item.trading.MerchantOffer
import org.bukkit.*
import org.bukkit.entity.*
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerShearEntityEvent
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import java.util.*

@Single
class DurabilityPlus: NmsManager.INmsMapper {
    private val villagerProfessions = arrayOf(
        Villager.Profession.TOOLSMITH,
        Villager.Profession.WEAPONSMITH,
        Villager.Profession.FISHERMAN,
        Villager.Profession.FLETCHER
    )

    companion object: IRunner, IComponentInjector {
        private val neon by inject<Neon>()
        private val neonFeatureManager by inject<NeonFeatureManager>()
        private val durabilityPlus by inject<DurabilityPlus>()

        private val isEnabled = neonFeatureManager.getFeatureToggle(NeonFeatureConfigProperty.DurabilityPlusConfigProperty.IsEnabled)
        private val showItemDurability = neonFeatureManager.getFeatureOptionValue<Boolean>(NeonFeatureConfigProperty.DurabilityPlusConfigProperty.ShowItemDurability)
        private var restrictFortuneHarvest = false

        override fun run() {
            val durabilityPlusEvent = DurabilityPlusEvent()

            togglePlayerItemDamageProperty()
            toggleVillagerItemDamageProperty()

            if (isEnabled) {
                //restrictFortuneHarvest = true
                NeonPluginLoader.registerEventProcessor(durabilityPlusEvent)
            } else {
                NeonPluginLoader.unregisterEventProcessor(durabilityPlusEvent)
            }
        }

        fun togglePlayerItemDamageProperty(player: Player? = null) {
            /* Toggle for specific player */
            player?.let {
                it.inventory.contents.filterNotNull()
                    .filter { contentItem -> contentItem.itemMeta is Damageable }
                    .filter { damageableItem -> DamageableItemMatcher.matchesItem(damageableItem) }
                    .forEach { damageableItem ->
                        durabilityPlus.updateDurabilityState(damageableItem, 0)
                    }

                return
            }

            neon.server.onlinePlayers.forEach { onlinePlayer ->
                onlinePlayer.inventory.contents.filterNotNull()
                    .filter { contentItem -> contentItem.itemMeta is Damageable }
                    .filter { damageableItem ->  DamageableItemMatcher.matchesItem(damageableItem) }
                    .forEach { damageableItem ->
                        durabilityPlus.updateDurabilityState( damageableItem, 0)
                    }
            }
        }

        fun toggleVillagerItemDamageProperty() {
            if (isEnabled) return

            /* Remove and hide damage property display from supported villager */
            neon.server.worlds.forEach {
                it.entities.parallelStream()
                    .filter { entity -> entity is Villager || entity is WanderingTrader }
                    .filter { entity ->
                        when (entity) {
                            is Villager -> entity.profession in durabilityPlus.villagerProfessions

                            is WanderingTrader -> true

                            else -> false
                        }
                    }.forEach { entity ->
                        durabilityPlus.updateDurabilityStateOnTrading(entity as AbstractVillager)
                    }
            }
        }
    }

    private fun updateDurabilityState(itemStack: ItemStack, durabilityConsumed: Int, player: Player? = null): DurabilityState {
        var isItemBroken = false

        /* Check if the item matches the supported tools/weapons */
        if (!DamageableItemMatcher.matchesItem(itemStack)) return DurabilityState(itemStack, false)

        /* Durability info */
        val damageableItemMeta = itemStack.itemMeta as Damageable
        val itemMaxDamageCount = itemStack.type.maxDurability.toInt() // max durability act as item maximum damage count before it break
        val currentItemDamageCount = damageableItemMeta.damage
        val durabilityDetailLore =  DurabilityDetailLore(isEnabled, showItemDurability)

        if (isEnabled) {
            /* Calculate the damage done */
            calculateDamageCount(currentItemDamageCount, durabilityConsumed).also { calculatedDamageCount ->
                if (!isItemBroken(calculatedDamageCount, itemMaxDamageCount)) {
                    durabilityDetailLore
                        .withDurabilityDetail(calculatedDamageCount, itemMaxDamageCount)
                        .setDetailLore(damageableItemMeta, false)
                    return@also
                }

                isItemBroken = true
                damageableItemMeta.damage = itemMaxDamageCount
                durabilityDetailLore
                    .withDurabilityDetail(damageableItemMeta.damage, itemMaxDamageCount)
                    .setDetailLore(damageableItemMeta, true)

                player?.let {
                    /* Check if it is required to play item break sound */
                    if (shouldPlayItemBreakSound(currentItemDamageCount, itemMaxDamageCount)) {
                        it.world.playSound(it.location, Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f)
                    }

                    sendItemBrokenWarning(it, itemStack)
                }
            }
        } else {
            durabilityDetailLore.removeDetailLore(damageableItemMeta)
        }

        itemStack.itemMeta = damageableItemMeta

        return DurabilityState(itemStack, isItemBroken)
    }

    fun updateDurabilityStateOnGive(gaveItem: net.minecraft.world.item.ItemStack) {
        if (!isEnabled) return

        val bukkitItemStack = NmsManager.toBukkitItemStack(gaveItem)

        updateDurabilityState(bukkitItemStack, 0)
    }

    private fun updateVillagerTradeResult(e: PlayerInteractEntityEvent) {
        with(e.rightClicked) {
            if (this !is AbstractVillager) return

            updateDurabilityStateOnTrading(this)
        }
    }

    private fun updateDurabilityStateOnTrading(villager: AbstractVillager) {
        /* Villager profession check */
        if (villager is Villager && villager.profession !in villagerProfessions) return

        villager.recipes
            .filter { merchantRecipe -> DamageableItemMatcher.matchesItem(merchantRecipe.result) }
            .forEach { merchantRecipe ->
                updateDurabilityState(merchantRecipe.result, 0).also { durabilityState ->
                    /* Get the nms trade recipe */
                    val nmsMerchantRecipe = DataUtil.asType<MerchantOffer>(
                        merchantRecipe.javaClass.getDeclaredField("handle").run {
                            this.isAccessible = true
                            this.get(merchantRecipe)
                        }
                    )

                    /* Replace the recipe result with updated durability detail */
                    nmsMerchantRecipe.javaClass.getDeclaredField(mapField(NmsField.MerchantRecipeResult)).apply {
                        this.isAccessible = true
                        this.set(nmsMerchantRecipe, NmsManager.toNmsItemStack(durabilityState.itemStack))
                    }
                }
            }
    }

    private fun updateDurabilityStateOnCrafting(e: PrepareItemCraftEvent) {
        val craftItem = e.view.getItem(0)?.let {
            if (it.type == Material.AIR) return

            it
        } ?: return

        updateDurabilityState(craftItem, 0)
    }

    private fun updateDurabilityStateOnPrepareCrafting(e: PrepareInventoryResultEvent) {
        e.result?.let {
            if (it.type == Material.AIR) return

            if (!DamageableItemMatcher.matchesItem(it)) return

            updateDurabilityState(it, 0)
        }
    }

    private fun updateDurabilityStateOnGenerateLoot(e: LootGenerateEvent) {
        if (!e.lootTable.key.toString().startsWith("minecraft:chests")) return

        e.setLoot(e.loot.fold(mutableListOf<ItemStack>()) { newChestLoot, itemStack ->
            durabilityPlus.updateDurabilityState(itemStack, 0)
            newChestLoot.add(itemStack)
            newChestLoot
        })
    }

    private fun calculateDamageCount(currentItemDamageCount: Int, durabilityConsumed: Int): Int {
        return currentItemDamageCount + durabilityConsumed
    }

    private fun isItemBroken(itemDamageCount: Int, itemMaxDurability: Int): Boolean {
        return itemDamageCount >= itemMaxDurability
    }

    private fun shouldPlayItemBreakSound(beforeItemDamageCount: Int, itemMaxDurability: Int): Boolean {
        return beforeItemDamageCount < itemMaxDurability
    }

    /**
     * Restrict any attack done by the player on broken items.
     *
     * @param e EntityDamageByEntityEvent
     */
    private fun restrictAttackOnBroken(e: EntityDamageByEntityEvent) {
        val attacker = (e.damager as? Player)?.let {
            if (it.gameMode == GameMode.CREATIVE) return

            it
        } ?: return

        val damageableItem = attacker.inventory.itemInMainHand.also {
            if (!DamageableItemMatcher.matchesItem(it)) return

            if (DamageableItemMatcher.matchesGeneralToolItems(it)) return

            if (DamageableItemMatcher.matchesBowWeaponItems(it)) return

            val damageableItemMeta = it.itemMeta as Damageable

            if (!isItemBroken(damageableItemMeta.damage, it.type.maxDurability.toInt())) {
                return
            }
        }

        e.isCancelled = true
        sendItemBrokenWarning(attacker, damageableItem)
    }

    /**
     * Restrict block breaking on broken items
     *
     * @param e BlockBreakEvent
     */
    private fun restrictBlockBreakingOnBroken(e: BlockBreakEvent) {
        val player = e.player.also {
            if (it.gameMode == GameMode.CREATIVE) return
        }
        val damageableItem = player.inventory.itemInMainHand.also {
            if (!DamageableItemMatcher.matchesItem(it)) return

            if (DamageableItemMatcher.matchesGeneralToolItems(it,
                    Material.FISHING_ROD,
                    Material.FLINT_AND_STEEL,
                    NeonItemMaterial.BRUSH)
                ) return

            if (DamageableItemMatcher.matchesBowWeaponItems(it)) return
        }
        val damageableItemMeta = damageableItem.itemMeta as Damageable
        val isInstantBreakBlock = with(e.block) {
            val breakSpeed = this.javaClass.getMethod("getBreakSpeed", Player::class.java).invoke(this, player) as Float

            breakSpeed >= 1.0f
        }

        when {
            isInstantBreakBlock && !DamageableItemMatcher.matchesGeneralToolItems(damageableItem, Material.SHEARS) -> return

            !isItemBroken(damageableItemMeta.damage, damageableItem.type.maxDurability.toInt()) -> return
        }

        e.isCancelled = true
        sendItemBrokenWarning(player, damageableItem)
    }

    /**
     * Restrict creeper ignition on broken Flint & Steel
     *
     * @param e PlayerInteractEntityEvent
     */
    private fun restrictCreeperIgnitionOnBroken(e: PlayerInteractEntityEvent) {
        val player = e.player.also {
            if (it.gameMode == GameMode.CREATIVE) return
        }

        if (e.rightClicked !is Creeper) return // Creeper aww man

        val flintAndSteelItem = when(e.hand) {
            EquipmentSlot.HAND -> player.inventory.itemInMainHand

            EquipmentSlot.OFF_HAND -> player.inventory.itemInOffHand

            else -> return
        }.also {
            if (!DamageableItemMatcher.matchesGeneralToolItems(it, Material.FLINT_AND_STEEL)) return

            val flintAndSteelItemMeta = it.itemMeta as Damageable

            if (!isItemBroken(flintAndSteelItemMeta.damage, it.type.maxDurability.toInt())) return
        }

        e.isCancelled = true
        sendItemBrokenWarning(player, flintAndSteelItem)
    }

    /**
     * Restrict wool shearing on broken Shears
     *
     * @param e PlayerShearEntityEvent
     */
    private fun restrictWoolShearingOnBroken(e: PlayerShearEntityEvent) {
        val player = e.player.also {
            if (it.gameMode == GameMode.CREATIVE) return
        }
        val shears = e.item.also {
            if (!DamageableItemMatcher.matchesGeneralToolItems(it, Material.SHEARS)) return

            val shearsItemMeta = it.itemMeta as Damageable

            if (!isItemBroken(shearsItemMeta.damage, it.type.maxDurability.toInt())) return
        }

        e.isCancelled = true
        sendItemBrokenWarning(player, shears)
    }

    /**
     * Restrict bow shooting on broken bow
     *
     * @param e EntityShootBowEvent
     */
    private fun restrictBowShootingOnBroken(e: EntityShootBowEvent) {
        val player = e.entity.run {
            if (this !is Player) return

            if (this.gameMode == GameMode.CREATIVE) return

            this
        }
        val bow = e.bow?.let {
            if (!DamageableItemMatcher.matchesBowWeaponItems(it)) return

            val bowItemMeta = it.itemMeta as Damageable

            if (!isItemBroken(bowItemMeta.damage, it.type.maxDurability.toInt())) return

            it
        } ?: return

        e.isCancelled = true
        sendItemBrokenWarning(player, bow)
    }

    /**
     * Restrict interactive use on broken items
     *
     * @param e PlayerInteractEvent
     */
    private fun restrictInteractiveUseOnBroken(e: PlayerInteractEvent) {
        val player = e.player.also {
            if (it.gameMode == GameMode.CREATIVE) return
        }
        val usedItem = e.item?.let {
            if (!DamageableItemMatcher.matchesItem(it)) return

            it
        } ?: return
        val usedItemItemMeta = usedItem.itemMeta as Damageable
        val playerUseAction = e.action
        val playerUseOnBlock = e.clickedBlock

        when {
            /* Axes interactive uses */
            DamageableItemMatcher.matchesAxeItems(usedItem) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (playerUseAction != Action.RIGHT_CLICK_BLOCK) return

                playerUseOnBlock?.let { block ->
                    if (!DurabilityConsumerBlockMatcher.matchesAxeInteractiveBlocks(block.type)) return

                    e.setUseInteractedBlock(Event.Result.DENY)
                    sendItemBrokenWarning(player, usedItem)
                }
            }

            /* Brushing sand/gravel */
            DamageableItemMatcher.matchesGeneralToolItems(usedItem, NeonItemMaterial.BRUSH) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (playerUseAction != Action.RIGHT_CLICK_BLOCK) return

                playerUseOnBlock?.let { block ->
                    if (!DurabilityConsumerBlockMatcher.matchesBrushableBlocks(block.type)) return

                    e.isCancelled = true
                    sendItemBrokenWarning(player, usedItem)
                }
            }

            /* Block pathing */
            DamageableItemMatcher.matchesShovelItems(usedItem) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (playerUseAction != Action.RIGHT_CLICK_BLOCK) return

                playerUseOnBlock?.let { block ->
                    if (!DurabilityConsumerBlockMatcher.matchesPathBlocks(block.type)) return

                    e.setUseItemInHand(Event.Result.DENY)
                    updateDurabilityState(usedItem, 0, player)
                    sendItemBrokenWarning(player, usedItem)
                }
            }

            /* Plowing farmland */
            DamageableItemMatcher.matchesHoeItems(usedItem) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (playerUseAction != Action.RIGHT_CLICK_BLOCK) return

                playerUseOnBlock?.let { block ->
                    if (!DurabilityConsumerBlockMatcher.matchesPathBlocks(block.type)) return

                    e.setUseItemInHand(Event.Result.DENY)
                    updateDurabilityState(usedItem, 0, player)
                    sendItemBrokenWarning(player, usedItem)
                }
            }

            /* Fishing */
            DamageableItemMatcher.matchesGeneralToolItems(usedItem, Material.FISHING_ROD) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (!(playerUseAction == Action.RIGHT_CLICK_AIR || playerUseAction == Action.RIGHT_CLICK_BLOCK)) return

                e.setUseItemInHand(Event.Result.DENY)
                updateDurabilityState(usedItem, 0, player)
                sendItemBrokenWarning(player, usedItem)
            }

            /* Pumpkin carving */
            DamageableItemMatcher.matchesGeneralToolItems(usedItem, Material.SHEARS) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (playerUseAction != Action.RIGHT_CLICK_BLOCK) return

                playerUseOnBlock?.let { block ->
                    if (!DurabilityConsumerBlockMatcher.matchesShearsInteractiveBlocks(block.type)) return

                    e.isCancelled = true
                    sendItemBrokenWarning(player, usedItem)
                }
            }

            /* TNT ignition # As TNT ignition is treat differently */
            DamageableItemMatcher.matchesGeneralToolItems(usedItem, Material.FLINT_AND_STEEL) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (playerUseAction != Action.RIGHT_CLICK_BLOCK) return

                playerUseOnBlock?.let { block ->
                    if (block.type != Material.TNT) return

                    e.isCancelled = true
                    sendItemBrokenWarning(player, usedItem)
                }
            }

            /* Drawing crossbow/bow */
            DamageableItemMatcher.matchesBowWeaponItems(usedItem) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (!(e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK)) return

                e.setUseItemInHand(Event.Result.DENY)
                updateDurabilityState(usedItem, 0, player)

                when(e.action) {
                    Action.RIGHT_CLICK_AIR -> {
                        sendItemBrokenWarning(player, usedItem)
                    }

                    Action.RIGHT_CLICK_BLOCK -> {
                        playerUseOnBlock?.let { block ->
                            val isBlockConditionValid = DurabilityConsumerBlockMatcher.matchesCreativeOnlyBlocks(block.type)
                                    || !block.type.isInteractable || e.useInteractedBlock() == Event.Result.DENY

                            if (player.isSneaking || isBlockConditionValid) {
                                sendItemBrokenWarning(player, usedItem)
                            }
                        }
                    }

                    else -> return
                }
            }
        }
    }

    /**
     * Restrict block ignition on broken Flint & Steel
     *
     * @param e BlockIgniteEvent
     */
    private fun restrictBlockIgnitionOnBroken(e: BlockIgniteEvent) {
        if (e.cause != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) return

        val player = e.player?.let {
            if (it.gameMode == GameMode.CREATIVE) return

            it
        } ?: return

        val flintAndSteel = when {
            player.inventory.itemInMainHand.type == Material.FLINT_AND_STEEL -> player.inventory.itemInMainHand

            player.inventory.itemInOffHand.type == Material.FLINT_AND_STEEL -> player.inventory.itemInOffHand

            else -> return
        }.also {
            if (!DamageableItemMatcher.matchesGeneralToolItems(it, Material.FLINT_AND_STEEL)) return

            val flintAndSteelItemMeta = it.itemMeta as Damageable

            if (!isItemBroken(flintAndSteelItemMeta.damage, it.type.maxDurability.toInt())) return
        }

        e.isCancelled = true
        sendItemBrokenWarning(player, flintAndSteel)
    }

    /**
     * Restrict fire on block on broken items
     *
     * @param e BlockPlaceEvent
     */
    private fun restrictPlaceFireOnBroken(e: BlockPlaceEvent) {
        val player = e.player.also {
            if (it.gameMode == GameMode.CREATIVE) return
        }

        if (!Tag.FIRE.isTagged(e.blockPlaced.type)) return

        val flintAndSteel = e.itemInHand.also {
            if (!DamageableItemMatcher.matchesGeneralToolItems(it, Material.FLINT_AND_STEEL)) return

            val flintAndSteelItemMeta = it.itemMeta as Damageable

            if (!isItemBroken(flintAndSteelItemMeta.damage, it.type.maxDurability.toInt())) return
        }

        e.isCancelled = true
        sendItemBrokenWarning(player, flintAndSteel)
    }

    /**
     * Send item broken warning
     *
     * @param player
     * @param damagedItem
     */
    private fun sendItemBrokenWarning(player: Player, damagedItem: ItemStack) {
        val warningMessage = "${ChatColor.GOLD}${getDamageableItemName(damagedItem)} " +
                "${ChatColor.RED}has been broken!"

        val setActionBarTextPacket = NmsProcessor()
            .getMcClass("network.protocol.game.${NmsMap.ClientPacketSetActionBarText.remapped}")!!
            .constructors
            .find { it.parameterTypes.contains(Component::class.java) }!!

        val actionTitlePacket = setActionBarTextPacket.newInstance(Component.Serializer.fromJson("{\"text\":\"${warningMessage}\"}"))

        ServerGamePacketManager.sendServerGamePacket(player,actionTitlePacket)
    }

    /**
     * Get damageable item name
     *
     * @param damageableItem
     * @return
     */
    private fun getDamageableItemName(damageableItem: ItemStack): String {
        val damageableItemMeta = damageableItem.itemMeta ?: return ""

        if (damageableItemMeta.hasDisplayName()) return "${ChatColor.ITALIC}${damageableItemMeta.displayName}"

        val damageableItemName = damageableItem.type.name

        if (damageableItemName.contains("_")) {
            var tempDamageableItemName = ""

            damageableItemName.split("_").forEach {
                tempDamageableItemName += it.lowercase().replaceFirstChar { splitName ->
                    if (splitName.isLowerCase()) {
                        splitName.titlecase(Locale.getDefault())
                    } else {
                        splitName.toString()
                    }
                }.plus(" ")
            }

            return tempDamageableItemName.trimEnd()
        }

        return damageableItemName.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())

            else it.toString()
        }
    }

    private class DurabilityPlusEvent: Listener, IComponentInjector {
        private val durabilityPlus by inject<DurabilityPlus>()

        @EventHandler
        private fun onPlayerItemDamage(e: PlayerItemDamageEvent) {
            val player = e.player
            val item = e.item

            durabilityPlus.updateDurabilityState(item, e.damage, player).also {
                if (it.isBroken) e.isCancelled = true
            }
        }

        @EventHandler
        private fun onBlockBreak(e: BlockBreakEvent) = durabilityPlus.restrictBlockBreakingOnBroken(e)

        @EventHandler
        private fun onEntityDamageByPlayer(e: EntityDamageByEntityEvent) = durabilityPlus.restrictAttackOnBroken(e)

        @EventHandler
        private fun onPlayerInteractEntity(e: PlayerInteractEntityEvent) {
            durabilityPlus.restrictCreeperIgnitionOnBroken(e)
            durabilityPlus.updateVillagerTradeResult(e)
        }

        @EventHandler
        private fun onBlockIgnite(e: BlockIgniteEvent) = durabilityPlus.restrictBlockIgnitionOnBroken(e)

        @EventHandler
        private fun onBlockPlace(e: BlockPlaceEvent) = durabilityPlus.restrictPlaceFireOnBroken(e)

        @EventHandler
        private fun onPlayerShearEntity(e: PlayerShearEntityEvent) = durabilityPlus.restrictWoolShearingOnBroken(e)

        @EventHandler
        private fun onBowShooting(e: EntityShootBowEvent) = durabilityPlus.restrictBowShootingOnBroken(e)

        @EventHandler
        private fun onPlayerInteract(e: PlayerInteractEvent) = durabilityPlus.restrictInteractiveUseOnBroken(e)

        @EventHandler
        private fun onRepairByAnvil(e: PrepareAnvilEvent) = durabilityPlus.updateDurabilityStateOnPrepareCrafting(e)

        @EventHandler
        private fun onCraftingItem(e: PrepareItemCraftEvent) = durabilityPlus.updateDurabilityStateOnCrafting(e)

        @EventHandler
        private fun onCraftingItemBySmithing(e: PrepareSmithingEvent) = durabilityPlus.updateDurabilityStateOnPrepareCrafting(e)

        @EventHandler
        private fun onChestLootGenerate(e: LootGenerateEvent) = durabilityPlus.updateDurabilityStateOnGenerateLoot(e)

        @EventHandler
        private fun onItemSpawn(e: ItemSpawnEvent) = durabilityPlus.updateDurabilityState(e.entity.itemStack, 0)

        @EventHandler
        private fun onItemPickup(e: EntityPickupItemEvent) {
            if (e.entityType != EntityType.PLAYER) return

            durabilityPlus.updateDurabilityState(e.item.itemStack, 0)
        }

        @EventHandler
        private fun onInventoryOpen(e: InventoryOpenEvent) {
            if (e.inventory.holder is GuiConstructor<*>) return

            e.inventory.contents.filterNotNull().forEach { durabilityPlus.updateDurabilityState(it, 0) }
        }
    }
}