package com.islandstudio.neon.experimental.nDurable

import com.islandstudio.neon.stable.primary.nCommand.NCommand
import com.islandstudio.neon.stable.primary.nExperimental.NExperimental
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
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

    /**
     * Add "DAMAGED" tag to the tool/weapon if the durability == 0.
     *
     * @param e
     */
    fun itemDamage(e: PlayerItemDamageEvent) {
        if (!isEnabled()) return

        val item: Material = e.item.type
        val itemName: String = item.name

        when {
            itemName.contains("_PICKAXE") || itemName.contains("_AXE")
                    || itemName.contains("_SHOVEL") || itemName.contains("_SWORD")
                    || itemName.contains("_HOE") -> {
                val itemMeta: Damageable = (e.item.itemMeta as Damageable?)!!

                val finalDamage: Int = itemMeta.damage + e.damage

                if (finalDamage >= item.maxDurability) {
                    e.isCancelled = true

                    itemMeta.damage = item.maxDurability.toInt()

                    itemMeta.lore = (itemMeta.lore as? ArrayList<String>)?.let { setDamageTag(it) } ?: setDamageTag(ArrayList())

                    e.item.itemMeta = itemMeta
                }

            }
        }
    }

    /**
     * Cancel the tool/weapon's damage to the entity if it has the "DAMAGED" tag and the durability == 0.
     *
     * @param e EntityDamageByEntityEvent
     */
    fun setAttackDamage(e: EntityDamageByEntityEvent) {
        if (!isEnabled()) return

        if (e.damager !is Player) return

        val player = e.damager as Player
        val playerInventory: PlayerInventory = player.inventory
        val itemInHand: ItemStack = playerInventory.itemInMainHand
        val itemName: String = itemInHand.type.name

        when {
            itemName.contains("_PICKAXE") || itemName.contains("_AXE")
                    || itemName.contains("_SHOVEL") || itemName.contains("_SWORD")
                    || itemName.contains("_HOE") -> {
                val itemMeta: ItemMeta = itemInHand.itemMeta!!

                if ((itemMeta.lore?.contains("${ChatColor.RED}DAMAGED") != true && (itemMeta as Damageable).damage < itemInHand.type.maxDurability)) return

                e.damage = 0.0

                if (itemMeta.hasDisplayName()) {
                    player.sendMessage("${NCommand.getPluginName()} ${ChatColor.RED}Your ${ChatColor.YELLOW}${itemMeta.displayName} ${ChatColor.RED}is damaged!")
                    return
                }

                val itemNameTrimmed: String = if (itemName.contains("_")) {
                    var tempStr = ""

                    itemName.split("_").forEach { str ->
                        tempStr += str.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }.plus(" ")
                    }

                    tempStr.trimEnd()
                } else itemName.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                player.sendMessage("${NCommand.getPluginName()} ${ChatColor.RED}Your ${ChatColor.YELLOW}${itemNameTrimmed} ${ChatColor.RED}is damaged!")
            }
        }
    }

    /**
     * Cancel the block breaking if the tool/weapon has the "DAMAGED" tag and the durability == 0.
     *
     * @param e BlockBreakEvent
     */
    fun setBlockBreaking(e: BlockBreakEvent) {
        if (!isEnabled()) return

        val player = e.player
        val itemInHand: ItemStack = player.inventory.itemInMainHand
        val itemName: String = itemInHand.type.name

        when {
            itemName.contains("_PICKAXE") || itemName.contains("_AXE")
                    || itemName.contains("_SHOVEL") || itemName.contains("_SWORD")
                    || itemName.contains("_HOE") -> {
                val itemMeta: ItemMeta = itemInHand.itemMeta!!

                if ((itemMeta as Damageable).damage >= itemInHand.type.maxDurability) {
                    itemMeta.lore = (itemMeta.lore as? ArrayList<String>)?.let { setDamageTag(it) } ?: setDamageTag(ArrayList())
                    itemInHand.itemMeta = itemMeta
                }

                if ((itemMeta.lore?.contains("${ChatColor.RED}DAMAGED") != true && itemMeta.damage < itemInHand.type.maxDurability)) return

                if (isInstantBreak) return

                e.isCancelled = true

                if (itemMeta.hasDisplayName()) {
                    player.sendMessage("${NCommand.getPluginName()} ${ChatColor.RED}Your ${ChatColor.YELLOW}${itemMeta.displayName} ${ChatColor.RED}is damaged!")
                    return
                }

                val itemNameTrimmed: String = if (itemName.contains("_")) {
                    var tempStr = ""

                    itemName.split("_").forEach { str ->
                        tempStr += str.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }.plus(" ")
                    }

                    tempStr.trimEnd()
                } else itemName.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                player.sendMessage("${NCommand.getPluginName()} ${ChatColor.RED}Your ${ChatColor.YELLOW}${itemNameTrimmed} ${ChatColor.RED}is damaged!")
            }
        }
    }

    /**
     * Cancel the wood stripping if the axe has the "DAMAGED" tag and the durability == 0.
     *
     * @param e PlayerInteractEvent
     */
    fun setWoodStripping(e: PlayerInteractEvent) {
        if (!isEnabled()) return

        if (e.action != Action.RIGHT_CLICK_BLOCK) return

        val player: Player = e.player
        val itemInHand: ItemStack = player.inventory.itemInMainHand
        val itemName: String = itemInHand.type.name

        if (!itemName.contains("_AXE")) return

        e.clickedBlock?.let { block ->
            val clickedBlockName: String = block.type.name

            if (clickedBlockName.contains("STRIPPED_")) return

            if (clickedBlockName.contains("_LOG") || clickedBlockName.contains("_WOOD") || clickedBlockName.contains("_STEM")) {
                val itemMeta: ItemMeta = itemInHand.itemMeta!!

                if ((itemMeta as Damageable).damage >= itemInHand.type.maxDurability) {
                    itemMeta.lore = (itemMeta.lore as? ArrayList<String>)?.let { setDamageTag(it) } ?: setDamageTag(ArrayList())
                    itemInHand.itemMeta = itemMeta
                }

                if ((itemMeta.lore?.contains("${ChatColor.RED}DAMAGED") != true && itemMeta.damage < itemInHand.type.maxDurability)) return

                e.isCancelled = true

                if (itemMeta.hasDisplayName()) {
                    player.sendMessage("${NCommand.getPluginName()} ${ChatColor.RED}Your ${ChatColor.YELLOW}${itemMeta.displayName} ${ChatColor.RED}is damaged!")
                    return
                }

                val itemNameTrimmed: String = if (itemName.contains("_")) {
                    var tempStr = ""

                    itemName.split("_").forEach { str ->
                        tempStr += str.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }.plus(" ")
                    }

                    tempStr.trimEnd()
                } else itemName.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                player.sendMessage("${NCommand.getPluginName()} ${ChatColor.RED}Your ${ChatColor.YELLOW}${itemNameTrimmed} ${ChatColor.RED}is damaged!")
            }
        }
    }

    /**
     * Get instant break of the targeted block.
     *
     * @param e BlockDamageEvent
     */
    fun getInstantBreak(e: BlockDamageEvent) {
        if (!isEnabled()) return

        val player = e.player
        val itemInHand: ItemStack = player.inventory.itemInMainHand
        val itemName: String = itemInHand.type.name

        when {
            itemName.contains("_PICKAXE") || itemName.contains("_AXE")
                    || itemName.contains("_SHOVEL") || itemName.contains("_SWORD")
                    || itemName.contains("_HOE") -> {
                val itemMeta: ItemMeta = itemInHand.itemMeta!!

                if ((itemMeta.lore?.contains("${ChatColor.RED}DAMAGED") != true && (itemMeta as Damageable).damage < itemInHand.type.maxDurability)) return

                isInstantBreak = e.instaBreak
            }
        }
    }

    /**
     * Add "DAMAGED" tag to the item lore.
     *
     * @param lore Item lore from the item
     * @return Item lore with "DAMAGED" tag
     */
    private fun setDamageTag(lore: ArrayList<String>): List<String> {
        if (lore.contains("${ChatColor.RED}DAMAGED")) return lore

        lore.add("${ChatColor.RED}DAMAGED")
        return lore
    }

//    fun setAnimation(player: Player, block: Block) {
//        val handle: Any = player.javaClass.getMethod("getHandle").invoke(player)
//        val mobEffectList: Any = NReflector.getNamespaceClass("world.effect.MobEffectList").getDeclaredMethod("a", Int::class.java).invoke(null, PotionEffectType.SLOW_DIGGING.id)
//        val mobEffect: Constructor<*> = NReflector.getNamespaceClass("world.effect.MobEffect").getConstructor(NReflector.getNamespaceClass("world.effect.MobEffectList"), Int::class.java, Int::class.java, Boolean::class.java, Boolean::class.java, Boolean::class.java)
//        val mobEffectInstance: Any = mobEffect.newInstance(mobEffectList, Int.MAX_VALUE, 5, false, false, false)
//
//        NReflector.getNamespaceClass("world.effect.MobEffect").getDeclaredMethod("a", Boolean::class.java).invoke(mobEffectInstance, true)
//
////        val mobEffectInst = MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1, false, false, false)
////        mobEffectInst.isNoCounter = true
//
//        val packet: Any = NReflector.getNamespaceClass("network.protocol.game.PacketPlayOutEntityEffect").getConstructor(Int::class.java, NReflector.getNamespaceClass("world.effect.MobEffect")).newInstance(Math.random().toInt(), mobEffectInstance)
//        NReflector.getNamespaceClass("world.entity.EntityLiving").getDeclaredMethod("b", NReflector.getNamespaceClass("world.effect.MobEffect")).invoke(handle, mobEffectInstance)
//
//    }

    /**
     * Repair the tool/weapon if it has the "DAMAGED" tag and the durability == 0. It also remove the "DAMAGED" tag from the item.
     *
     * @param e PrepareAnvilEvent
     */
    fun repairItem(e: PrepareAnvilEvent) {
        if (!isEnabled()) return

        val item: ItemStack = e.result!!
        val itemName = item.type.name

        when {
            itemName.contains("_PICKAXE") || itemName.contains("_AXE")
                    || itemName.contains("_SHOVEL") || itemName.contains("_SWORD")
                    || itemName.contains("_HOE") -> {
                val itemMeta = item.itemMeta
                val itemLore = itemMeta?.lore

                if ((itemMeta as Damageable).damage < (item.type.maxDurability - 1)) {
                    if (itemLore?.contains("${ChatColor.RED}DAMAGED") == true) {
                        itemLore.remove("${ChatColor.RED}DAMAGED")
                        itemMeta.lore = itemLore
                    }
                }

                item.itemMeta = itemMeta
                e.result = item
            }
        }

    }
}