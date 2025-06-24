package com.islandstudio.neon.features.durabilityplus

import com.islandstudio.neon.Neon
import com.islandstudio.neon.core.initialization.NeonPluginLoader
import com.islandstudio.neon.core.nmsmapping.NmsMap
import com.islandstudio.neon.core.nmsmapping.NmsProcessor
import com.islandstudio.neon.features.neonfeature.NeonFeatureManager
import com.islandstudio.neon.server.ServerGamePacketManager
import com.islandstudio.neon.shared.core.IRunner
import com.islandstudio.neon.shared.core.config.property.NeonFeatureConfigProperty
import com.islandstudio.neon.shared.core.di.IComponentInjector
import net.minecraft.network.chat.Component
import org.bukkit.*
import org.bukkit.entity.Creeper
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerShearEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.koin.core.annotation.Single
import org.koin.core.component.inject
import java.util.*

@Single
class DurabilityPlus: IComponentInjector {
    companion object: IRunner, IComponentInjector {
        private val neon by inject<Neon>()
        private val neonFeatureManager by inject<NeonFeatureManager>()
        private val durabilityPlus by inject<DurabilityPlus>()

        private val isEnabled = neonFeatureManager.getFeatureToggle(NeonFeatureConfigProperty.NDurableConfigProperty.IsEnabled)
        private val showItemDurability = neonFeatureManager.getFeatureOptionValue<Boolean>(NeonFeatureConfigProperty.NDurableConfigProperty.ShowItemDurability)
        private var restrictFortuneHarvest = false

        override fun run() {
            NeonPluginLoader.registerEventProcessor(DurabilityPlusEvent())

            togglePlayerItemDamageProperty()
//            toggleVillagerItemDamageProperty()
//
//            if (isEnabled) {
//                restrictFortuneHarvest = true
//                //NeonPluginLoader.registerEventProcessor(EventProcessor())
//            } else {
//                //NeonPluginLoader.unregisterEventProcessor(EventProcessor())
//            }
        }

        fun togglePlayerItemDamageProperty(player: Player? = null) {
            /* Toggle for specific player */
            player?.let {
                it.inventory.contents.filterNotNull()
                    .filter { contentItem -> contentItem.itemMeta is Damageable }
                    .filter { damageableItem -> DamageableItemMatcher.matchesItem(damageableItem) }
                    .forEach { damageableItem ->
                        durabilityPlus.updateDamageProperty(player, damageableItem, 0)
                    }

                return
            }

            neon.server.onlinePlayers.forEach { onlinePlayer ->
                onlinePlayer.inventory.contents.filterNotNull()
                    .filter { contentItem -> contentItem.itemMeta is Damageable }
                    .filter { damageableItem ->  DamageableItemMatcher.matchesItem(damageableItem) }
                    .forEach { damageableItem ->
                        durabilityPlus.updateDamageProperty(onlinePlayer, damageableItem, 0)
                    }
            }
        }

        fun toggleVillagerItemDamageProperty() {
            if (isEnabled) return

            /* Remove and hide damage property display from all tool smith villager & weapon smith villager */
            neon.server.worlds.forEach {
                it.entities.parallelStream()
                    .filter { entity -> entity is Villager }
                    .filter { entity -> (entity as Villager).profession == Villager.Profession.TOOLSMITH
                            || entity.profession == Villager.Profession.WEAPONSMITH }
                    .forEach { entity ->
                        //Handler.applyDamagePropertyOnTrading(entity as Villager)
                    }
            }
        }
    }

    fun updateDamageProperty(player: Player, itemStack: ItemStack, durabilityConsumed: Int): ItemStack {
        if (!DamageableItemMatcher.matchesItem(itemStack)) return itemStack

        val damageableItemMeta = itemStack.itemMeta as Damageable
        val itemMaxDamageCount = itemStack.type.maxDurability.toInt() // max durability act as item maximum damage count before it break
        val currentItemDamageCount = damageableItemMeta.damage

        calculateDamageCount(currentItemDamageCount, durabilityConsumed).also {
            if (!isItemBroken(it, itemMaxDamageCount)) {
                DurabilityDetailsLore(isEnabled, showItemDurability, it, itemMaxDamageCount)
                    .setDetailLore(damageableItemMeta, false)
                return@also
            }

            damageableItemMeta.damage = itemMaxDamageCount
            DurabilityDetailsLore(isEnabled, showItemDurability, damageableItemMeta.damage, itemMaxDamageCount)
                .setDetailLore(damageableItemMeta, true)

            player.world.playSound(player.location, Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f)
            sendItemBrokenWarning(player, itemStack)
        }

        itemStack.itemMeta = damageableItemMeta
        return itemStack
    }

    fun calculateDamageCount(currentItemDamageCount: Int, durabilityConsumed: Int): Int {
        return currentItemDamageCount + durabilityConsumed
    }

    fun isItemBroken(itemDamageCount: Int, itemMaxDurability: Int): Boolean {
        return itemDamageCount >= itemMaxDurability
    }

    /**
     * Restrict any attack done by the player with broken items.
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

    private fun restrictBlockBreakingOnBroken(e: BlockBreakEvent) {
        val player = e.player.also {
            if (it.gameMode == GameMode.CREATIVE) return
        }
        val damageableItem = player.inventory.itemInMainHand.also {
            if (!DamageableItemMatcher.matchesItem(it)) return

            if (DamageableItemMatcher.matchesGeneralToolItems(it, Material.FISHING_ROD, Material.FLINT_AND_STEEL)) return

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

    private fun restrictInteractiveUse(e: PlayerInteractEvent) {
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
            /* Axes interactive Use */
            DamageableItemMatcher.matchesAxeItems(usedItem) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (playerUseAction != Action.RIGHT_CLICK_BLOCK) return

                playerUseOnBlock?.let { block ->
                    val blockItem = ItemStack(block.type)

                    if (!(DurabilityConsumerBlockMatcher.matchesNonStrippedWoodBlockItems(blockItem)
                            || DurabilityConsumerBlockMatcher.matchesExposedCopperBlockItems(blockItem)
                            || DurabilityConsumerBlockMatcher.matchesWeatheredCopperBlockItems(blockItem)
                            || DurabilityConsumerBlockMatcher.matchesOxidizedCopperBlockItems(blockItem)
                            || DurabilityConsumerBlockMatcher.matchesWaxedCopperBlockItems(blockItem))) {
                        return
                    }

                    e.setUseInteractedBlock(Event.Result.DENY)
                    sendItemBrokenWarning(player, usedItem)
                }
            }

            /* Block pathing */
            DamageableItemMatcher.matchesShovelItems(usedItem) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (playerUseAction != Action.RIGHT_CLICK_BLOCK) return

                playerUseOnBlock?.let { block ->
                    val blockItem = ItemStack(block.type)

                    if (!DurabilityConsumerBlockMatcher.matchesPathBlockITems(blockItem)) return

                    e.setUseItemInHand(Event.Result.DENY)
                    sendItemBrokenWarning(player, usedItem)
                }
            }

            /* Plowing farmland */
            DamageableItemMatcher.matchesHoeItems(usedItem) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (playerUseAction != Action.RIGHT_CLICK_BLOCK) return

                playerUseOnBlock?.let { block ->
                    val blockItem = ItemStack(block.type)

                    if (!DurabilityConsumerBlockMatcher.matchesPathBlockITems(blockItem)) return

                    e.setUseItemInHand(Event.Result.DENY)
                    sendItemBrokenWarning(player, usedItem)
                }
            }

            /* Fishing */
            DamageableItemMatcher.matchesGeneralToolItems(usedItem, Material.FISHING_ROD) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (!(playerUseAction == Action.RIGHT_CLICK_AIR || playerUseAction == Action.RIGHT_CLICK_BLOCK)) return

                e.setUseItemInHand(Event.Result.DENY)
                sendItemBrokenWarning(player, usedItem)
            }

            /* Pumpkin carving */
            DamageableItemMatcher.matchesGeneralToolItems(usedItem, Material.SHEARS) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (playerUseAction != Action.RIGHT_CLICK_BLOCK) return

                playerUseOnBlock?.let { block ->
                    if (block.type != Material.PUMPKIN) return

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

            /* Crossbow reload */
            DamageableItemMatcher.matchesBowWeaponItems(usedItem, Material.CROSSBOW) -> {
                if (!isItemBroken(usedItemItemMeta.damage, usedItem.type.maxDurability.toInt())) return

                if (!(e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK)) return

                e.setUseItemInHand(Event.Result.DENY)

                when(e.action) {
                    Action.RIGHT_CLICK_AIR -> {
                        sendItemBrokenWarning(player, usedItem)
                    }

                    Action.RIGHT_CLICK_BLOCK -> {
                        playerUseOnBlock?.let {
                            val isBlockConditionValid = DurabilityConsumerBlockMatcher.matchesCreativeOnlyBlockItems(ItemStack(it.type))
                                    || !it.type.isInteractable || e.useInteractedBlock() == Event.Result.DENY

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
            val itemMeta = item.itemMeta as Damageable
            val itemDamage = itemMeta.damage
            val itemMaxDurability = item.type.maxDurability.toInt()

            durabilityPlus.updateDamageProperty(player, item, e.damage)

            durabilityPlus.calculateDamageCount(itemDamage, e.damage).also {
                if (!durabilityPlus.isItemBroken(it, itemMaxDurability)) return@also

                e.isCancelled = true
            }
        }

        @EventHandler
        private fun onBlockBreak(e: BlockBreakEvent) = durabilityPlus.restrictBlockBreakingOnBroken(e)

        @EventHandler
        private fun onEntityDamageByPlayer(e: EntityDamageByEntityEvent) = durabilityPlus.restrictAttackOnBroken(e)

        @EventHandler
        private fun onPlayerInteractEntity(e: PlayerInteractEntityEvent) = durabilityPlus.restrictCreeperIgnitionOnBroken(e)

        @EventHandler
        private fun onBlockIgnite(e: BlockIgniteEvent) = durabilityPlus.restrictBlockIgnitionOnBroken(e)

        @EventHandler
        private fun onBlockPlace(e: BlockPlaceEvent) = durabilityPlus.restrictPlaceFireOnBroken(e)

        @EventHandler
        private fun onPlayerShearEntity(e: PlayerShearEntityEvent) = durabilityPlus.restrictWoolShearingOnBroken(e)

        @EventHandler
        private fun onBowShooting(e: EntityShootBowEvent) = durabilityPlus.restrictBowShootingOnBroken(e)

        @EventHandler
        private fun onPlayerInteract(e: PlayerInteractEvent) = durabilityPlus.restrictInteractiveUse(e)
    }
}