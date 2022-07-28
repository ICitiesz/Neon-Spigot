package com.islandstudio.neon.experimental.nDurable

import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nExperimental.NExperimental
import com.islandstudio.neon.stable.utils.NNamespaceKeys
import com.islandstudio.neon.stable.utils.NPacketProcessor
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.entity.Creeper
import org.bukkit.entity.Player
import org.bukkit.entity.Sheep
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.kotlin.utils.addToStdlib.applyIf
import java.util.*
import kotlin.collections.ArrayList

object NDurable {
    private var isInstantBreak: Boolean = false
    private val isEnabled: () -> Boolean = {
        var tempBool = false

        NExperimental.Handler.getClientElement().forEach {
            val nExperimental = NExperimental(it)

            if (!nExperimental.experimentalName.equals("nDurable", true)) return@forEach

            if (!nExperimental.isEnabled) return@forEach

            tempBool = true
        }

        tempBool
    }

    private val damagedTag = "${ChatColor.RED}DAMAGED"

    object Handler {
        fun run() {
            if (!isEnabled()) return NConstructor.unRegisterEvent(EventController())

            NConstructor.registerEvent(EventController())
        }
    }

    /**
     * Disable fortune harvest performed by nHarvest if the tool/weapon has been damaged.
     *
     * @param heldItem Tools/Weapon.
     * @param player The player who perform the harvest.
     * @return
     */
    fun disableFortuneHarvest(heldItem: ItemStack, player: Player): Boolean {
        if (!isEnabled()) return false

        if (!isItemMatch(heldItem)) return false

        val itemMetaDamageable: Damageable = heldItem.itemMeta?.let { it as Damageable } ?: return false

        if (!isItemDamaged(itemMetaDamageable.damage, heldItem.type.maxDurability.toInt())) return false

        if (!hasDamagedItemKey(itemMetaDamageable)) {
            player.server.pluginManager.callEvent(PlayerItemDamageEvent(player, heldItem, itemMetaDamageable.damage))
        }

        if (player.gameMode == GameMode.CREATIVE) return false

        return true
    }

    /**
     * Disable item being broken when the durability is 0 or less than 0.
     *
     * @param e PlayerItemDamageEvent
     */
    private fun disableItemBroken(e: PlayerItemDamageEvent) {
        val item = e.item

        if (!isItemMatch(item)) return

        val itemMetaDamageable: Damageable = item.itemMeta as Damageable
        val finalDamage: Int = itemMetaDamageable.damage + e.damage
        val maxDurability: Int = item.type.maxDurability.toInt()

        if (!isItemDamaged(finalDamage, maxDurability)) return

        e.isCancelled = true

        itemMetaDamageable.damage = maxDurability

        item.itemMeta = addDamageTag(itemMetaDamageable)
    }

    /**
     * Disable the block breaking if the tool/weapon has been damaged.
     *
     * @param e BlockBreakEvent
     */
    private fun disableBlockBreaking(e: BlockBreakEvent) {
        val player: Player = e.player
        val item: ItemStack = player.inventory.itemInMainHand

        if (!isItemMatch(item)) return

        val itemMetaDamageable: Damageable = item.itemMeta as Damageable

        if (!isItemDamaged(itemMetaDamageable.damage, item.type.maxDurability.toInt())) return

        if (!hasDamagedItemKey(itemMetaDamageable)) {
            player.server.pluginManager.callEvent(PlayerItemDamageEvent(player, item, itemMetaDamageable.damage))
        }

        if (isInstantBreak && !isItemMatch(item, NTools.SHEARS) || player.gameMode == GameMode.CREATIVE) {
            isInstantBreak = false
            return
        }

        e.isCancelled = true

        notifyDamage(player, item)
    }

    /**
     * Disable alternative use of the tool/weapon such as wood stripping, copper deoxidization, fishing
     * if the tool/weapon has been damaged.
     *
     * @param e PlayerInteractEvent
     */
    private fun disableAlternativeUse(e: PlayerInteractEvent) {
        val player = e.player
        val item: ItemStack = player.inventory.itemInMainHand

        if (!isItemMatch(item)) return

        val itemMetaDamageable: Damageable = item.itemMeta as Damageable

        if (!isItemDamaged(itemMetaDamageable.damage, item.type.maxDurability.toInt())) return

        if (!hasDamagedItemKey(itemMetaDamageable)) {
            player.server.pluginManager.callEvent(PlayerItemDamageEvent(player, item, itemMetaDamageable.damage))
        }

        when {
            /* Axe usage */
            isItemMatch(item, NTools.AXE) -> {
                if (e.action != Action.RIGHT_CLICK_BLOCK) return

                e.clickedBlock?.let {clickBlock ->
                    val clickedBlockName: String = clickBlock.type.name

                    NBlocks.values().forEach { nBlock ->
                        if (!clickedBlockName.contains(nBlock.blockName, true)) return@forEach

                        if (clickedBlockName.contains("stripped_", true)) return

                        if (player.gameMode == GameMode.CREATIVE) return

                        e.isCancelled = true

                        notifyDamage(player, item)
                    }
                }
            }

            /* Fishing */
            isItemMatch(item, NTools.FISHING_ROD) -> {
                if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
                    if (player.gameMode == GameMode.CREATIVE) return

                    e.isCancelled = true

                    notifyDamage(player, item)
                }
            }

            /* Ignition on block */
            isItemMatch(item, NTools.FLINT_AND_STEEL) -> {
                if (e.action != Action.RIGHT_CLICK_BLOCK) return

                if (player.gameMode == GameMode.CREATIVE) return

                e.isCancelled = true

                notifyDamage(player, item)
            }
        }
    }

    /**
     * Disable creeper ignition that cause by the Flint and Steel.
     *
     * @param e PlayerInteractEntityEvent
     */
    private fun disableCreeperIgnition(e: PlayerInteractEntityEvent) {
        val player = e.player

        if (e.rightClicked !is Creeper) return

        val item: ItemStack = player.inventory.itemInMainHand

        if (!isItemMatch(item, NTools.FLINT_AND_STEEL)) return

        val itemMetaDamageable: Damageable = item.itemMeta as Damageable

        if (!isItemDamaged(itemMetaDamageable.damage, item.type.maxDurability.toInt())) return

        if (!hasDamagedItemKey(itemMetaDamageable)) {
            player.server.pluginManager.callEvent(PlayerItemDamageEvent(player, item, itemMetaDamageable.damage))
        }

        e.isCancelled = true

        notifyDamage(player, item)
    }

    /**
     * Disable attack damage to the entity if the tool/weapon has been damaged.
     *
     * @param e EntityDamageByEntityEvent
     */
    private fun disableAttackDamage(e: EntityDamageByEntityEvent) {
        if (e.damager !is Player) return

        val player = e.damager as Player
        val item: ItemStack = player.inventory.itemInMainHand

        if (!isItemMatch(item)) return

        val itemMetaDamageable: Damageable = item.itemMeta as Damageable

        if (!isItemDamaged(itemMetaDamageable.damage, item.type.maxDurability.toInt())) return

        if (!hasDamagedItemKey(itemMetaDamageable)) {
            player.server.pluginManager.callEvent(PlayerItemDamageEvent(player, item, itemMetaDamageable.damage))
        }

        if (player.gameMode == GameMode.CREATIVE) return

        e.isCancelled = true

        notifyDamage(player, item)
    }

    /**
     * Disable wool shearing if the tool/weapon has been damaged.
     *
     * @param e PlayerShearEntityEvent
     */
    private fun disableWoolShearing(e: PlayerShearEntityEvent) {
        val player = e.player

        val item: ItemStack = player.inventory.itemInMainHand

        if (!isItemMatch(item)) return

        val itemMetaDamageable: Damageable = item.itemMeta as Damageable

        if (!isItemDamaged(itemMetaDamageable.damage, item.type.maxDurability.toInt())) return

        if (!hasDamagedItemKey(itemMetaDamageable)) {
            player.server.pluginManager.callEvent(PlayerItemDamageEvent(player, item, itemMetaDamageable.damage))
        }

        if (player.gameMode == GameMode.CREATIVE) return

        if (e.entity !is Sheep) return

        e.isCancelled = true

        notifyDamage(player, item)
    }

    /**
     * Disable bow shooting if it has been damaged.
     *
     * @param e EntityShootBowEvent
     */
    private fun disableBowShooting(e: EntityShootBowEvent) {
        val player = if (e.entity is Player) e.entity as Player else return

        val item: ItemStack = e.bow as ItemStack

        if (!isItemMatch(item)) return

        val itemMetaDamageable: Damageable = item.itemMeta as Damageable

        if (!isItemDamaged(itemMetaDamageable.damage, item.type.maxDurability.toInt())) return

        if (!hasDamagedItemKey(itemMetaDamageable)) {
            player.server.pluginManager.callEvent(PlayerItemDamageEvent(player, item, itemMetaDamageable.damage))
        }

        if (player.gameMode == GameMode.CREATIVE) return

        if (!item.type.name.equals(NWeapons.BOW.weaponName, true)) return

        e.projectile.remove()

        notifyDamage(player, item)
    }

    /**
     * Disable crossbow shooting if it has been damaged.
     *
     * @param e PlayerInteractEvent
     */
    private fun disableCrossBowShooting(e: PlayerInteractEvent) {
        if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
            if (!e.hasItem()) return

            val player = e.player
            val item = e.item!!

            if (!isItemMatch(item, nWeapons = NWeapons.CROSSBOW)) return

            val itemMetaDamageable: Damageable = item.itemMeta?.let { it as Damageable } ?: return

            if (!isItemDamaged(itemMetaDamageable.damage, item.type.maxDurability.toInt())) return

            if (!hasDamagedItemKey(itemMetaDamageable)) {
                player.server.pluginManager.callEvent(PlayerItemDamageEvent(player, item, itemMetaDamageable.damage))
            }

            e.setUseItemInHand(Event.Result.DENY)

            notifyDamage(player, item)
        }
    }

    /**
     * Add "neon_damaged_item" key and "DAMAGED" tag to the tool/weapon lore.
     *
     * @param itemMeta Item meta from the item
     * @return Item meta with "neon_damaged_item" key and "DAMAGED" tag.
     */
    private fun addDamageTag(itemMeta: ItemMeta): ItemMeta {
        if (hasDamagedItemKey(itemMeta)) return itemMeta

        itemMeta.persistentDataContainer.set(NNamespaceKeys.NEON_DAMAGED_ITEM.key, PersistentDataType.STRING, "true")

        itemMeta.lore?.let { lore ->
            lore.applyIf(!lore.contains(damagedTag)) {
                this.add(damagedTag)
                this
            }
        } ?: run {
            itemMeta.lore = ArrayList<String>().apply {
                this.add(damagedTag)
            }
        }

        return itemMeta
    }

    /**
     * Notify damage to the player through the action title bar.
     *
     * @param player The player who using the tool/weapon.
     * @param item The tool/weapon.
     */
    private fun notifyDamage(player: Player, item: ItemStack) {
        val itemMeta: ItemMeta = item.itemMeta!!

        val warning = if (itemMeta.hasDisplayName()) {
            "${ChatColor.YELLOW}${ChatColor.ITALIC}${itemMeta.displayName} ${ChatColor.RESET}${ChatColor.RED}has been damaged!"
        } else {
            val itemName = item.type.name

            val itemNameTrimmed: String = if (itemName.contains("_")) {
                var tempStr = ""

                itemName.split("_").forEach { str ->
                    tempStr += str.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }.plus(" ")
                }

                tempStr.trimEnd()
            } else itemName.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

            "${ChatColor.YELLOW}${itemNameTrimmed} ${ChatColor.RED}has been damaged!"
        }

        val actionTitlePacket = ClientboundSetActionBarTextPacket(
            Component.Serializer.fromJson("{\"text\":\"${warning}\"}")
        )

        NPacketProcessor.sendGamePacket(player, actionTitlePacket)
    }

    /**
     * Match the given item with nTools and nWeapons.
     *
     * @param item The given item.
     * @param nTools The specific item from nTools.
     * @param nWeapons The specific item from nWeapons.
     * @return bool True if the given item is match with nTools and nWeapons else false.
     */
    private fun isItemMatch(item: ItemStack, nTools: NTools? = null, nWeapons: NWeapons? = null): Boolean {
        val itemName = item.type.name

        when {
            nTools == null && nWeapons == null -> {
                NTools.values().forEach { nTool ->
                    if (!itemName.contains(nTool.toolName, true)) return@forEach

                    return true
                }

                NWeapons.values().forEach { nWeapon ->
                    if (!itemName.contains(nWeapon.weaponName, true)) return@forEach

                    if (itemName.contains(NWeapons.BOW.weaponName, true) || itemName.contains(NWeapons.CROSSBOW.weaponName, true)) {
                        if (itemName.length != nWeapon.weaponName.length) return@forEach
                    }

                    return true
                }
            }

            nTools != null -> {
                if (!itemName.contains(nTools.toolName, true)) return false

                return true
            }

            nWeapons != null -> {
                if (!itemName.contains(nWeapons.weaponName, true)) return false

                if (itemName.contains(NWeapons.BOW.weaponName, true) || itemName.contains(NWeapons.CROSSBOW.weaponName, true)) {
                    if (itemName.length != nWeapons.weaponName.length) return false
                }

                return true
            }
        }

        return false
    }

    /**
     * Check if the given item durability is less than the item max durability.
     *
     * @param itemCurrentDurability The current durability of the given item.
     * @param itemMaxDurability The max durability of the given item.
     * @return bool True if the given item durability is less than the item max durability else false.
     */
    private fun isItemDamaged(itemCurrentDurability: Int, itemMaxDurability: Int): Boolean {
        if (itemCurrentDurability < itemMaxDurability) return false

        return true
    }

    /**
     * Check if the given item has the "neon_damaged_item" key.
     *
     * @param itemMeta The given item meta.
     * @return bool True if the given item has the "neon_damaged_item" key else false.
     */
    private fun hasDamagedItemKey(itemMeta: ItemMeta): Boolean {
        val damagedItemKey: NamespacedKey = NNamespaceKeys.NEON_DAMAGED_ITEM.key

        if (!itemMeta.persistentDataContainer.has(damagedItemKey, PersistentDataType.STRING)
            && itemMeta.persistentDataContainer[damagedItemKey, PersistentDataType.STRING] != "true") return false

        return true
    }

    /**
     * Repair the tool/weapon if it has the "DAMAGED" tag and the durability == 0. It also remove the "DAMAGED" tag from the item.
     *
     * @param item The tool/weapon.
     * @param repairAmount The repair amount. (Only required if the tool/weapon fixed by using Mending enchantment)
     */
    private fun repairItem(item: ItemStack, repairAmount: Int = 0): ItemStack {
        val itemMetaDamageable: Damageable = item.itemMeta?.let { it as Damageable } ?: return item

        if (!isItemMatch(item)) return item

        /* Final repair only use in repairing item by Mending. */
        val finalRepair = itemMetaDamageable.damage - repairAmount

        /* Check if damaged for the repair result. */
        if (isItemDamaged(finalRepair, (item.type.maxDurability.toInt()))) return item

        item.itemMeta = removeDamageTag(itemMetaDamageable)

        return item
    }

    /**
     * Remove "neon_damaged_item" key and "DAMAGED" tag from the tool/weapon lore.
     *
     * @param itemMeta
     * @return Item meta without "neon_damaged_item" key and "DAMAGED" tag.
     */
    private fun removeDamageTag(itemMeta: ItemMeta): ItemMeta {
        if (!hasDamagedItemKey(itemMeta)) return itemMeta

        itemMeta.persistentDataContainer.remove(NNamespaceKeys.NEON_DAMAGED_ITEM.key)

        itemMeta.lore?.let {lore ->
            lore.applyIf(lore.contains(damagedTag)) {
                this.remove(damagedTag)
                this
            }
        }.also { itemMeta.lore = it }

        return itemMeta
    }

    private class EventController: Listener {
        @EventHandler
        private fun onItemDamage(e: PlayerItemDamageEvent) {
            disableItemBroken(e)
        }

        @EventHandler
        private fun onBlockBreak(e: BlockBreakEvent) {
            disableBlockBreaking(e)
        }

        @EventHandler
        private fun onPlayerInteract(e: PlayerInteractEvent) {
            disableAlternativeUse(e)
            disableCrossBowShooting(e)
        }

        @EventHandler
        private fun onPlayerInteractEntity(e: PlayerInteractEntityEvent) {
            disableCreeperIgnition(e)
        }

        @EventHandler
        private fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {
            disableAttackDamage(e)
        }

        @EventHandler
        private fun onWoolShear(e: PlayerShearEntityEvent) {
            disableWoolShearing(e)
        }

        @EventHandler
        private fun onBowShooting(e: EntityShootBowEvent) {
            disableBowShooting(e)
        }

        @EventHandler
        private fun onPrepareAnvil(e: PrepareAnvilEvent) {
            e.result = e.result?.let { result -> repairItem(result) }
        }

        @EventHandler
        private fun onItemMending(e: PlayerItemMendEvent) {
            e.item.itemMeta = repairItem(e.item, e.repairAmount).itemMeta
        }

        @EventHandler
        private fun onBlockDamage(e: BlockDamageEvent) {
            isInstantBreak = e.instaBreak
        }

        @EventHandler
        private fun onInventoryOpen(e: InventoryOpenEvent) {
//            val inventory: Inventory = e.inventory
//
//            //println(inventory.type)
//            //println(inventory is PlayerInventory)
//
//            inventory.contents.forEach {
//                if (it == null) return@forEach
//
//                val item: Material = it.type
//                val itemName: String = it.type.name
//
//                when {
//                    itemName.contains("_PICKAXE") || itemName.contains("_AXE")
//                            || itemName.contains("_SHOVEL") || itemName.contains("_SWORD")
//                            || itemName.contains("_HOE") -> {
//                        val itemMeta: Damageable = (it.itemMeta as Damageable?)!!
//
//                        itemMeta.damage = item.maxDurability - 1
//                        itemMeta.lore = listOf("${ChatColor.RED}BROKEN")
//
//                        it.itemMeta = itemMeta
//                    }
//                }
//
//                //(e.player as Player).updateInventory()
//            }
        }
    }
}